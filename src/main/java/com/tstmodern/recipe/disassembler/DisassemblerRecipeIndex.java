package com.tstmodern.recipe.disassembler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeHandler;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;
import com.tstmodern.machine.DisassemblerMachine;
import com.tstmodern.registry.TSTRecipeTypes;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.packs.resources.PreparableReloadListener.PreparationBarrier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

/**
 * A reload-aware, immutable-generation index of recipes that the Disassembler can reverse.
 */
public final class DisassemblerRecipeIndex implements GTRecipeType.ICustomRecipeLogic {
    private static final ResourceLocation RUNTIME_RECIPE_ID = new ResourceLocation("tstmodern", "runtime/disassembler");
    private static final int MAX_RUNTIME_ITEM_OUTPUTS = 16;
    private static final int MAX_RUNTIME_ITEM_INPUTS = 16;
    private static final int MAX_RUNTIME_FLUID_OUTPUTS = 4;
    private static final Comparator<DisassemblerRecipeDescriptor> WINNER_ORDER = Comparator
            .comparingInt(DisassemblerRecipeDescriptor::sourcePriority)
            .thenComparing(descriptor -> descriptor.sourceId().toString());

    public static final DisassemblerRecipeIndex INSTANCE = new DisassemblerRecipeIndex(
            DisassemblerRecipeSources.INSTANCE, new DisassemblerRecipeAdapter());

    private final DisassemblerRecipeSources sources;
    private final DisassemblerRecipeAdapter adapter;
    private final DescriptorEnumerator enumerator;
    private Set<GTRecipe> ownedRepresentativeInstances = Collections.newSetFromMap(new IdentityHashMap<>());
    private Snapshot snapshot = Snapshot.empty();

    DisassemblerRecipeIndex(DisassemblerRecipeSources sources, DisassemblerRecipeAdapter adapter) {
        this(sources, adapter, DisassemblerRecipeIndex::enumerateSources);
    }

    DisassemblerRecipeIndex(DisassemblerRecipeSources sources, DisassemblerRecipeAdapter adapter,
                            DescriptorEnumerator enumerator) {
        this.sources = Objects.requireNonNull(sources, "sources");
        this.adapter = Objects.requireNonNull(adapter, "adapter");
        this.enumerator = Objects.requireNonNull(enumerator, "enumerator");
    }

    public synchronized Optional<DisassemblerRecipeDescriptor> find(Item item) {
        if (item == null) {
            return Optional.empty();
        }
        Snapshot current = currentSnapshot();
        if (current.lookups().containsKey(item)) {
            return current.lookups().get(item);
        }

        Optional<DisassemblerRecipeDescriptor> result = Optional.ofNullable(current.descriptors().get(item));
        Map<Item, Optional<DisassemblerRecipeDescriptor>> lookupCopy = new IdentityHashMap<>(current.lookups());
        lookupCopy.put(item, result);
        snapshot = current.withLookups(lookupCopy);
        return result;
    }

    public synchronized void invalidate() {
        snapshot = Snapshot.empty();
    }

    /** Narrow reload seam so Forge lifecycle code does not need access to recipe registries. */
    public void invalidateFromReload() {
        invalidate();
    }

    /** Invalidates only after resource preparation has crossed the reload barrier on the game executor. */
    public CompletableFuture<Void> invalidateAfterReload(PreparationBarrier barrier, Executor gameExecutor) {
        return barrier.wait(Boolean.TRUE).thenRunAsync(this::invalidate, gameExecutor);
    }

    @Override
    public GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder) {
        if (!(holder instanceof DisassemblerMachine machine)) {
            return null;
        }
        int casingTier = machine.getCasingTier();
        if (casingTier < 1 || casingTier > 14) {
            return null;
        }
        Map<Item, Long> available = readInputItems(holder);
        synchronized (this) {
            Optional<RuntimeRecipePlan> plan = buildRuntimePlanForSnapshot(currentSnapshot(), available, casingTier);
            return plan.map(this::buildRuntimeRecipe).orElse(null);
        }
    }

    @Override
    public void buildRepresentativeRecipes() {
        synchronized (this) {
            invalidate();
            List<RepresentativePlan> representatives = buildRepresentativePlansForCurrentGeneration();
            GTRecipeType type = TSTRecipeTypes.DISASSEMBLER;
            Set<GTRecipe> mainCategory = type.getCategoryMap().get(type.getCategory());
            List<GTRecipe> replacementRecipes = new ArrayList<>();
            for (RepresentativePlan representative : representatives) {
                replacementRecipes.add(buildRecipe(representative.plan(), representative.id()));
            }
            if (mainCategory != null) {
                ownedRepresentativeInstances = replaceOwnedInstances(mainCategory, ownedRepresentativeInstances,
                        replacementRecipes, recipe -> recipe.id);
            } else {
                replacementRecipes.forEach(type::addToMainCategory);
                Set<GTRecipe> inserted = type.getCategoryMap().get(type.getCategory());
                ownedRepresentativeInstances = identitySet();
                if (inserted != null) {
                    for (GTRecipe replacement : replacementRecipes) if (containsIdentity(inserted, replacement)) {
                        ownedRepresentativeInstances.add(replacement);
                    }
                }
            }
        }
    }

    static Map<Item, DisassemblerRecipeDescriptor> selectWinners(
            Collection<DisassemblerRecipeDescriptor> descriptors) {
        Map<Item, DisassemblerRecipeDescriptor> winners = new IdentityHashMap<>();
        if (descriptors == null) return Map.of();
        for (DisassemblerRecipeDescriptor descriptor : descriptors) {
            if (descriptor == null) {
                continue;
            }
            winners.merge(descriptor.outputItem(), descriptor, (left, right) ->
                    WINNER_ORDER.compare(left, right) <= 0 ? left : right);
        }
        return Collections.unmodifiableMap(new IdentityHashMap<>(winners));
    }

    static <C, R> List<R> visitAll(Iterable<C> categories, Function<C, ? extends Iterable<R>> recipes) {
        List<R> result = new ArrayList<>();
        if (categories == null || recipes == null) {
            return List.of();
        }
        for (C category : categories) {
            if (category == null) continue;
            Iterable<R> inCategory = recipes.apply(category);
            if (inCategory != null) {
                for (R recipe : inCategory) {
                    if (recipe != null) result.add(recipe);
                }
            }
        }
        return List.copyOf(result);
    }

    static Optional<RuntimeRecipePlan> buildRuntimePlan(Map<Item, Long> available, int casingTier,
                                                         Function<Item, Optional<DisassemblerRecipeDescriptor>> lookup) {
        if (casingTier < 1 || casingTier > 14) {
            return Optional.empty();
        }
        Optional<DisassemblerRecipePolicy.AggregatePlan> aggregate = DisassemblerRecipePolicy.aggregate(
                available, casingTier, lookup);
        if (aggregate.isEmpty()) {
            return Optional.empty();
        }
        Map<Item, Long> acceptedInputs = new IdentityHashMap<>();
        sortedItems(aggregate.get().consumedItems()).stream().limit(MAX_RUNTIME_ITEM_INPUTS)
                .forEach(entry -> acceptedInputs.put(entry.getKey(), entry.getValue().longValue()));
        Optional<DisassemblerRecipePolicy.AggregatePlan> accepted = DisassemblerRecipePolicy.aggregate(
                acceptedInputs, casingTier, lookup);
        if (accepted.isEmpty()) return Optional.empty();
        DisassemblerRecipePolicy.AggregatePlan plan = accepted.get();
        Optional<List<ItemStack>> returnedItems = splitItemOutputs(plan.returnedItems());
        Optional<List<FluidStack>> returnedFluids = fluidOutputs(plan.returnedFluids());
        if (returnedItems.isEmpty() || returnedFluids.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new RuntimeRecipePlan(itemStacks(plan.consumedItems()), returnedItems.get(),
                returnedFluids.get(), plan.durationTicks()));
    }

    synchronized Optional<RuntimeRecipePlan> buildRuntimePlanForCurrentGeneration(Map<Item, Long> available, int casingTier) {
        return buildRuntimePlanForSnapshot(currentSnapshot(), available, casingTier);
    }

    synchronized List<RepresentativePlan> buildRepresentativePlansForCurrentGeneration() {
        Snapshot current = currentSnapshot();
        return sortedDescriptors(current.descriptors().values()).stream()
                .map(descriptor -> buildRuntimePlanForSnapshot(current,
                        Map.of(descriptor.outputItem(), (long) descriptor.outputAmount()), 14)
                        .map(plan -> new RepresentativePlan(representativeId(descriptor.outputItem()), plan)))
                .flatMap(Optional::stream)
                .toList();
    }

    private static Optional<RuntimeRecipePlan> buildRuntimePlanForSnapshot(Snapshot snapshot, Map<Item, Long> available,
                                                                             int casingTier) {
        return buildRuntimePlan(available, casingTier,
                item -> Optional.ofNullable(snapshot.descriptors().get(item)));
    }

    private synchronized Snapshot currentSnapshot() {
        DisassemblerRecipeSources.Snapshot sourceSnapshot = sources.snapshot();
        if (snapshot.enumerated() && snapshot.sourceGeneration() == sourceSnapshot.generation()) {
            return snapshot;
        }
        Collection<DisassemblerRecipeDescriptor> descriptors = enumerator.enumerate(sourceSnapshot.sources(), adapter);
        snapshot = new Snapshot(selectWinners(descriptors == null ? List.of() : descriptors), Map.of(), true,
                sourceSnapshot.generation());
        return snapshot;
    }

    private static Collection<DisassemblerRecipeDescriptor> enumerateSources(List<DisassemblerRecipeSource> sources,
                                                                                DisassemblerRecipeAdapter adapter) {
        List<DisassemblerRecipeDescriptor> descriptors = new ArrayList<>();
        for (DisassemblerRecipeSource source : sources) {
            if (source == null) continue;
            descriptors.addAll(source.enumerate(adapter));
        }
        return descriptors;
    }

    private static Map<Item, Long> readInputItems(IRecipeCapabilityHolder holder) {
        if (holder == null) return Map.of();
        List<Iterable<?>> contents = new ArrayList<>();
        List<IRecipeHandler<?>> handlers = holder.getCapabilitiesFlat(IO.IN, ItemRecipeCapability.CAP);
        if (handlers != null) for (IRecipeHandler<?> handler : handlers) {
            if (handler != null && handler.getContents() != null) contents.add(handler.getContents());
        }
        return collectInputItems(contents);
    }

    static Map<Item, Long> collectInputItems(Iterable<? extends Iterable<?>> handlers) {
        Map<Item, Long> items = new IdentityHashMap<>();
        if (handlers == null) return items;
        for (Iterable<?> contents : handlers) {
            if (contents == null) continue;
            for (Object content : contents) if (content instanceof ItemStack stack && !stack.isEmpty()) {
                items.merge(stack.getItem(), (long) stack.getCount(), DisassemblerRecipePolicy::saturatingAdd);
            }
        }
        return items;
    }

    static <T> Set<T> replaceOwnedInstances(Collection<T> category, Set<T> ownedInstances,
                                            Collection<T> replacements, Function<T, ResourceLocation> id) {
        Set<T> owned = identitySet();
        if (ownedInstances != null) owned.addAll(ownedInstances);
        if (category == null || id == null) return identitySet();
        category.removeIf(owned::contains);
        Set<ResourceLocation> occupiedIds = new HashSet<>();
        for (T entry : category) if (entry != null) occupiedIds.add(id.apply(entry));
        Set<T> nextOwned = identitySet();
        if (replacements != null) for (T replacement : replacements) {
            if (replacement == null) continue;
            ResourceLocation replacementId = id.apply(replacement);
            if (occupiedIds.contains(replacementId)) continue;
            if (category.add(replacement)) {
                nextOwned.add(replacement);
                occupiedIds.add(replacementId);
            }
        }
        return nextOwned;
    }

    private static <T> Set<T> identitySet() {
        return Collections.newSetFromMap(new IdentityHashMap<>());
    }

    private static <T> boolean containsIdentity(Collection<T> entries, T target) {
        for (T entry : entries) if (entry == target) return true;
        return false;
    }

    private GTRecipe buildRuntimeRecipe(RuntimeRecipePlan plan) {
        return buildRecipe(plan, RUNTIME_RECIPE_ID);
    }

    private GTRecipe buildRecipe(RuntimeRecipePlan plan, ResourceLocation id) {
        GTRecipeBuilder builder = new GTRecipeBuilder(id, TSTRecipeTypes.DISASSEMBLER)
                .duration(plan.durationTicks());
        for (ItemStack input : plan.consumedItems()) {
            builder.inputItems(input);
        }
        for (ItemStack output : plan.returnedItems()) {
            builder.outputItems(output);
        }
        for (FluidStack output : plan.returnedFluids()) {
            builder.outputFluids(output);
        }
        GTRecipe recipe = builder.buildRawRecipe();
        recipe.id = id;
        return recipe;
    }

    @SuppressWarnings("deprecation")
    private static ResourceLocation representativeId(Item outputItem) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(outputItem);
        return new ResourceLocation("tstmodern", "representative/disassembler/" + itemId.getNamespace() + "/" + itemId.getPath());
    }

    @SuppressWarnings("deprecation")
    private static Optional<List<ItemStack>> splitItemOutputs(Map<Item, Integer> outputAmounts) {
        List<ItemStack> stacks = new ArrayList<>();
        for (Map.Entry<Item, Integer> output : sortedItems(outputAmounts)) {
            int remaining = output.getValue();
            int maxStackSize = Math.max(1, output.getKey().getMaxStackSize());
            while (remaining > 0) {
                if (stacks.size() >= MAX_RUNTIME_ITEM_OUTPUTS) {
                    return Optional.empty();
                }
                int count = Math.min(remaining, maxStackSize);
                stacks.add(new ItemStack(output.getKey(), count));
                remaining -= count;
            }
        }
        return Optional.of(List.copyOf(stacks));
    }

    private static Optional<List<FluidStack>> fluidOutputs(Map<Fluid, Integer> outputAmounts) {
        List<FluidStack> fluids = new ArrayList<>(outputAmounts.size());
        for (Map.Entry<Fluid, Integer> output : sortedFluids(outputAmounts)) {
            if (output.getKey() != Fluids.EMPTY && output.getValue() > 0) {
                if (fluids.size() >= MAX_RUNTIME_FLUID_OUTPUTS) {
                    return Optional.empty();
                }
                fluids.add(new FluidStack(output.getKey(), output.getValue()));
            }
        }
        return Optional.of(List.copyOf(fluids));
    }

    @FunctionalInterface
    interface DescriptorEnumerator {
        Collection<DisassemblerRecipeDescriptor> enumerate(List<DisassemblerRecipeSource> sources,
                                                             DisassemblerRecipeAdapter adapter);
    }

    @SuppressWarnings("deprecation")
    private static List<Map.Entry<Item, Integer>> sortedItems(Map<Item, Integer> amounts) {
        return amounts.entrySet().stream()
                .filter(entry -> entry.getKey() != null && entry.getValue() != null && entry.getValue() > 0)
                .sorted(Comparator.comparing(entry -> BuiltInRegistries.ITEM.getKey(entry.getKey()).toString()))
                .toList();
    }

    private static List<ItemStack> itemStacks(Map<Item, Integer> amounts) {
        return sortedItems(amounts).stream().map(entry -> new ItemStack(entry.getKey(), entry.getValue())).toList();
    }

    @SuppressWarnings("deprecation")
    private static List<Map.Entry<Fluid, Integer>> sortedFluids(Map<Fluid, Integer> amounts) {
        return amounts.entrySet().stream()
                .filter(entry -> entry.getKey() != null && entry.getValue() != null && entry.getValue() > 0)
                .sorted(Comparator.comparing(entry -> BuiltInRegistries.FLUID.getKey(entry.getKey()).toString()))
                .toList();
    }

    @SuppressWarnings("deprecation")
    private static List<DisassemblerRecipeDescriptor> sortedDescriptors(Collection<DisassemblerRecipeDescriptor> descriptors) {
        return descriptors.stream().sorted(Comparator.comparing(descriptor ->
                BuiltInRegistries.ITEM.getKey(descriptor.outputItem()).toString())).toList();
    }

    record RuntimeRecipePlan(List<ItemStack> consumedItems, List<ItemStack> returnedItems,
                             List<FluidStack> returnedFluids, int durationTicks) {
        RuntimeRecipePlan {
            returnedItems = returnedItems.stream().map(ItemStack::copy).toList();
            consumedItems = consumedItems.stream().map(ItemStack::copy).toList();
            returnedFluids = returnedFluids.stream().map(FluidStack::copy).toList();
        }
    }

    record RepresentativePlan(ResourceLocation id, RuntimeRecipePlan plan) {}

    private record Snapshot(Map<Item, DisassemblerRecipeDescriptor> descriptors,
                            Map<Item, Optional<DisassemblerRecipeDescriptor>> lookups, boolean enumerated,
                            long sourceGeneration) {
        private Snapshot {
            descriptors = Collections.unmodifiableMap(new IdentityHashMap<>(descriptors));
            lookups = Collections.unmodifiableMap(new IdentityHashMap<>(lookups));
        }

        static Snapshot empty() {
            return new Snapshot(Map.of(), Map.of(), false, -1L);
        }

        Snapshot withLookups(Map<Item, Optional<DisassemblerRecipeDescriptor>> replacements) {
            return new Snapshot(descriptors, replacements, enumerated, sourceGeneration);
        }
    }
}
