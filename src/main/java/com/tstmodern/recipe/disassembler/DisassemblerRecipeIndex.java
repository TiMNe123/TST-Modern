package com.tstmodern.recipe.disassembler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeHandler;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;
import com.tstmodern.machine.DisassemblerMachine;
import com.tstmodern.registry.TSTRecipeTypes;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

/**
 * A reload-aware, immutable-generation index of recipes that the Disassembler can reverse.
 */
public final class DisassemblerRecipeIndex implements GTRecipeType.ICustomRecipeLogic {
    private static final ResourceLocation RUNTIME_RECIPE_ID = new ResourceLocation("tstmodern", "runtime/disassembler");
    private static final int MAX_RUNTIME_ITEM_OUTPUTS = 16;
    private static final Comparator<DisassemblerRecipeDescriptor> WINNER_ORDER = Comparator
            .comparingInt(DisassemblerRecipeDescriptor::sourcePriority)
            .thenComparing(descriptor -> descriptor.sourceId().toString());

    public static final DisassemblerRecipeIndex INSTANCE = new DisassemblerRecipeIndex(List.of(
            new DisassemblerRecipeSource("assembly_line", 0, () -> GTRecipeTypes.ASSEMBLY_LINE_RECIPES),
            new DisassemblerRecipeSource("assembler", 1, () -> GTRecipeTypes.ASSEMBLER_RECIPES)),
            new DisassemblerRecipeAdapter());

    private final List<DisassemblerRecipeSource> sources;
    private final DisassemblerRecipeAdapter adapter;
    private final DescriptorEnumerator enumerator;
    private Snapshot snapshot = Snapshot.empty();

    DisassemblerRecipeIndex(List<DisassemblerRecipeSource> sources, DisassemblerRecipeAdapter adapter) {
        this(sources, adapter, DisassemblerRecipeIndex::enumerateSources);
    }

    DisassemblerRecipeIndex(List<DisassemblerRecipeSource> sources, DisassemblerRecipeAdapter adapter,
                            DescriptorEnumerator enumerator) {
        this.sources = List.copyOf(Objects.requireNonNull(sources, "sources"));
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

    @Override
    public GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder) {
        if (!(holder instanceof DisassemblerMachine machine)) {
            return null;
        }
        Optional<RuntimeRecipePlan> plan = buildRuntimePlan(readInputItems(holder), machine.getCasingTier(), this::find);
        return plan.map(this::buildRuntimeRecipe).orElse(null);
    }

    @Override
    public void buildRepresentativeRecipes() {
        invalidate();
        List<DisassemblerRecipeDescriptor> descriptors;
        synchronized (this) {
            descriptors = List.copyOf(currentSnapshot().descriptors().values());
        }
        for (DisassemblerRecipeDescriptor descriptor : descriptors) {
            buildRuntimePlan(Map.of(descriptor.outputItem(), (long) descriptor.outputAmount()), 14,
                    ignored -> Optional.of(descriptor))
                    .map(plan -> buildRecipe(plan, representativeId(descriptor)))
                    .ifPresent(recipe -> TSTRecipeTypes.DISASSEMBLER.addToMainCategory(recipe));
        }
    }

    static Map<Item, DisassemblerRecipeDescriptor> selectWinners(
            Collection<DisassemblerRecipeDescriptor> descriptors) {
        Map<Item, DisassemblerRecipeDescriptor> winners = new IdentityHashMap<>();
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
        for (C category : categories) {
            Iterable<R> inCategory = recipes.apply(category);
            if (inCategory != null) {
                for (R recipe : inCategory) {
                    result.add(recipe);
                }
            }
        }
        return List.copyOf(result);
    }

    static Optional<RuntimeRecipePlan> buildRuntimePlan(Map<Item, Long> available, int casingTier,
                                                         Function<Item, Optional<DisassemblerRecipeDescriptor>> lookup) {
        Optional<DisassemblerRecipePolicy.AggregatePlan> aggregate = DisassemblerRecipePolicy.aggregate(
                available, casingTier, lookup);
        if (aggregate.isEmpty()) {
            return Optional.empty();
        }
        DisassemblerRecipePolicy.AggregatePlan plan = aggregate.get();
        return Optional.of(new RuntimeRecipePlan(plan.consumedItems(), splitItemOutputs(plan.returnedItems()),
                fluidOutputs(plan.returnedFluids()), plan.durationTicks()));
    }

    private synchronized Snapshot currentSnapshot() {
        if (snapshot.enumerated()) {
            return snapshot;
        }
        snapshot = new Snapshot(selectWinners(enumerator.enumerate(sources, adapter)), Map.of(), true);
        return snapshot;
    }

    private static Collection<DisassemblerRecipeDescriptor> enumerateSources(List<DisassemblerRecipeSource> sources,
                                                                                DisassemblerRecipeAdapter adapter) {
        List<DisassemblerRecipeDescriptor> descriptors = new ArrayList<>();
        for (DisassemblerRecipeSource source : sources) {
            GTRecipeType type = source.recipeType().get();
            if (type == null) {
                continue;
            }
            for (GTRecipe recipe : visitAll(type.getCategories(), type::getRecipesInCategory)) {
                adapter.adapt(recipe, source.priority()).ifPresent(descriptors::add);
            }
        }
        return descriptors;
    }

    private static Map<Item, Long> readInputItems(IRecipeCapabilityHolder holder) {
        Map<Item, Long> items = new IdentityHashMap<>();
        for (IRecipeHandler<?> handler : holder.getCapabilitiesFlat(IO.IN, ItemRecipeCapability.CAP)) {
            for (Object content : handler.getContents()) {
                if (content instanceof ItemStack stack && !stack.isEmpty()) {
                    items.merge(stack.getItem(), (long) stack.getCount(), DisassemblerRecipePolicy::saturatingAdd);
                }
            }
        }
        return items;
    }

    private GTRecipe buildRuntimeRecipe(RuntimeRecipePlan plan) {
        return buildRecipe(plan, RUNTIME_RECIPE_ID);
    }

    private GTRecipe buildRecipe(RuntimeRecipePlan plan, ResourceLocation id) {
        GTRecipeBuilder builder = new GTRecipeBuilder(id, TSTRecipeTypes.DISASSEMBLER)
                .duration(plan.durationTicks());
        for (Map.Entry<Item, Integer> input : plan.consumedItems().entrySet()) {
            builder.inputItems(input.getKey(), input.getValue());
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

    private static ResourceLocation representativeId(DisassemblerRecipeDescriptor descriptor) {
        ResourceLocation sourceId = descriptor.sourceId();
        String path = "representative/disassembler/" + sourceId.getNamespace() + "_"
                + sourceId.getPath().replace('/', '_');
        return new ResourceLocation("tstmodern", path);
    }

    private static List<ItemStack> splitItemOutputs(Map<Item, Integer> outputAmounts) {
        List<ItemStack> stacks = new ArrayList<>();
        for (Map.Entry<Item, Integer> output : outputAmounts.entrySet()) {
            int remaining = output.getValue();
            int maxStackSize = Math.max(1, output.getKey().getMaxStackSize());
            while (remaining > 0 && stacks.size() < MAX_RUNTIME_ITEM_OUTPUTS) {
                int count = Math.min(remaining, maxStackSize);
                stacks.add(new ItemStack(output.getKey(), count));
                remaining -= count;
            }
            if (stacks.size() == MAX_RUNTIME_ITEM_OUTPUTS) {
                break;
            }
        }
        return List.copyOf(stacks);
    }

    private static List<FluidStack> fluidOutputs(Map<Fluid, Integer> outputAmounts) {
        List<FluidStack> fluids = new ArrayList<>(outputAmounts.size());
        for (Map.Entry<Fluid, Integer> output : outputAmounts.entrySet()) {
            if (output.getValue() > 0) {
                fluids.add(new FluidStack(output.getKey(), output.getValue()));
            }
        }
        return List.copyOf(fluids);
    }

    @FunctionalInterface
    interface DescriptorEnumerator {
        Collection<DisassemblerRecipeDescriptor> enumerate(List<DisassemblerRecipeSource> sources,
                                                             DisassemblerRecipeAdapter adapter);
    }

    record RuntimeRecipePlan(Map<Item, Integer> consumedItems, List<ItemStack> returnedItems,
                             List<FluidStack> returnedFluids, int durationTicks) {
        RuntimeRecipePlan {
            consumedItems = Collections.unmodifiableMap(new IdentityHashMap<>(consumedItems));
            returnedItems = returnedItems.stream().map(ItemStack::copy).toList();
            returnedFluids = returnedFluids.stream().map(FluidStack::copy).toList();
        }
    }

    private record Snapshot(Map<Item, DisassemblerRecipeDescriptor> descriptors,
                            Map<Item, Optional<DisassemblerRecipeDescriptor>> lookups, boolean enumerated) {
        private Snapshot {
            descriptors = Collections.unmodifiableMap(new IdentityHashMap<>(descriptors));
            lookups = Collections.unmodifiableMap(new IdentityHashMap<>(lookups));
        }

        static Snapshot empty() {
            return new Snapshot(Map.of(), Map.of(), false);
        }

        Snapshot withLookups(Map<Item, Optional<DisassemblerRecipeDescriptor>> replacements) {
            return new Snapshot(descriptors, replacements, enumerated);
        }
    }
}
