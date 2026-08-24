package com.tstmodern.recipe.disassembler;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

/** Pure batching and arithmetic rules for disassembling reverse recipes. */
public final class DisassemblerRecipePolicy {

    private static final int MIN_CASING_TIER = 1;
    private static final int MAX_CASING_TIER = 14;
    private static final long TICKS_PER_DURATION_UNIT = 100L;

    private DisassemblerRecipePolicy() {}

    public record AggregatePlan(
                                Map<Item, Integer> consumedItems,
                                Map<Item, Integer> returnedItems,
                                Map<Fluid, Integer> returnedFluids,
                                long processed,
                                int durationTicks) {
        public AggregatePlan {
            consumedItems = immutableIdentityCopy(consumedItems);
            returnedItems = immutableIdentityCopy(returnedItems);
            returnedFluids = immutableIdentityCopy(returnedFluids);
        }
    }

    public static boolean tierAllows(int casingTier, int recipeTier) {
        validateCasingTier(casingTier);
        if (recipeTier < 0) {
            return false;
        }
        return casingTier == MAX_CASING_TIER || recipeTier <= casingTier + 1;
    }

    public static Optional<AggregatePlan> aggregate(
                                                       Map<Item, Long> available,
                                                       int casingTier,
                                                       Function<Item, Optional<DisassemblerRecipeDescriptor>> lookup) {
        Objects.requireNonNull(available, "available");
        Objects.requireNonNull(lookup, "lookup");
        validateCasingTier(casingTier);

        Map<Item, Long> consumed = new IdentityHashMap<>();
        Map<Item, Long> returnedItems = new IdentityHashMap<>();
        Map<Fluid, Long> returnedFluids = new IdentityHashMap<>();
        long processed = 0;

        for (Map.Entry<Item, Long> entry : available.entrySet()) {
            Item sourceItem = entry.getKey();
            Long amount = entry.getValue();
            if (sourceItem == null || amount == null || amount <= 0) {
                continue;
            }

            Optional<DisassemblerRecipeDescriptor> descriptor = lookup.apply(sourceItem);
            if (descriptor == null || descriptor.isEmpty() || !tierAllows(casingTier, descriptor.get().recipeTier())) {
                continue;
            }

            DisassemblerRecipeDescriptor recipe = descriptor.get();
            long batches = amount / recipe.outputAmount();
            if (batches == 0) {
                continue;
            }

            add(consumed, sourceItem, saturatingMultiply(batches, recipe.outputAmount()));
            processed = saturatingAdd(processed, batches);
            for (DisassemblerRecipeDescriptor.ReturnedItem returned : recipe.returnedItems()) {
                add(returnedItems, returned.item(), saturatingMultiply(batches, returned.amount()));
            }
            for (DisassemblerRecipeDescriptor.ReturnedFluid returned : recipe.returnedFluids()) {
                add(returnedFluids, returned.fluid(), saturatingMultiply(batches, returned.amount()));
            }
        }

        if (consumed.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new AggregatePlan(
                clampAmounts(consumed),
                clampAmounts(returnedItems),
                clampAmounts(returnedFluids),
                processed,
                durationTicks(processed, casingTier)));
    }

    public static long saturatingAdd(long left, long right) {
        if (left > 0 && right > 0 && left > Long.MAX_VALUE - right) {
            return Long.MAX_VALUE;
        }
        return left + right;
    }

    public static long saturatingMultiply(long left, long right) {
        if (left > 0 && right > 0 && left > Long.MAX_VALUE / right) {
            return Long.MAX_VALUE;
        }
        if (left < 0 && right < 0 && left < Long.MAX_VALUE / right) {
            return Long.MAX_VALUE;
        }
        return left * right;
    }

    public static int durationTicks(long processed, int casingTier) {
        validateCasingTier(casingTier);
        long durationUnits = Math.max(1L, processed / (4L * casingTier));
        long ticks = saturatingMultiply(durationUnits, TICKS_PER_DURATION_UNIT);
        return ticks >= Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) ticks;
    }

    private static void validateCasingTier(int casingTier) {
        if (casingTier < MIN_CASING_TIER || casingTier > MAX_CASING_TIER) {
            throw new IllegalArgumentException("casingTier must be between 1 and 14: " + casingTier);
        }
    }

    private static <T> void add(Map<T, Long> amounts, T key, long amount) {
        amounts.merge(key, amount, DisassemblerRecipePolicy::saturatingAdd);
    }

    private static <T> Map<T, Integer> clampAmounts(Map<T, Long> amounts) {
        Map<T, Integer> clamped = new IdentityHashMap<>();
        for (Map.Entry<T, Long> entry : amounts.entrySet()) {
            clamped.put(entry.getKey(), entry.getValue() >= Integer.MAX_VALUE ? Integer.MAX_VALUE : entry.getValue().intValue());
        }
        return clamped;
    }

    private static <T, V> Map<T, V> immutableIdentityCopy(Map<T, V> source) {
        return Collections.unmodifiableMap(new IdentityHashMap<>(Objects.requireNonNull(source, "source")));
    }
}
