package com.tstmodern.machine.logic;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.gregtechceu.gtceu.utils.GTHashMaps;

import net.minecraft.world.item.ItemStack;

import it.unimi.dsi.fastutil.objects.Object2IntMap;

/** Pure Starcore Miner output calculations. */
public final class StarcoreMinerLogic {
    public static final int BASE_STACK_SIZE = 131_072;
    public static final int OUTPUT_STACKS = 24;
    public static final int DURATION = 128;
    public static final int EUT = 2_013_265_920;

    public record WeightedEntry<T>(T value, int weight) {}

    public static int boostedStackSize(int fabricators) {
        return fabricators <= 0 ? BASE_STACK_SIZE :
                BASE_STACK_SIZE * 2 * (int) Math.ceil(Math.pow(fabricators, 1.5));
    }

    public static <T> List<WeightedEntry<T>> aggregateWeights(List<WeightedEntry<T>> entries) {
        Map<T, Integer> totals = new LinkedHashMap<>();
        for (WeightedEntry<T> entry : entries) {
            if (entry.weight > 0) totals.merge(entry.value, entry.weight, StarcoreMinerLogic::saturatingAdd);
        }
        List<WeightedEntry<T>> result = new ArrayList<>(totals.size());
        totals.forEach((value, weight) -> result.add(new WeightedEntry<>(value, weight)));
        return List.copyOf(result);
    }

    public static List<WeightedEntry<ItemStack>> aggregateItemStackWeights(
            List<WeightedEntry<ItemStack>> entries) {
        Object2IntMap<ItemStack> totals = GTHashMaps.createItemStackMap(true);
        for (WeightedEntry<ItemStack> entry : entries) {
            if (!entry.value.isEmpty() && entry.weight > 0) {
                totals.put(entry.value.copyWithCount(1), saturatingAdd(totals.getInt(entry.value), entry.weight));
            }
        }
        List<WeightedEntry<ItemStack>> result = new ArrayList<>(totals.size());
        totals.object2IntEntrySet().forEach(entry ->
                result.add(new WeightedEntry<>(entry.getKey(), entry.getIntValue())));
        return List.copyOf(result);
    }

    public static int totalWeight(List<? extends WeightedEntry<?>> entries) {
        int total = 0;
        for (WeightedEntry<?> entry : entries) {
            total = saturatingAdd(total, Math.max(0, entry.weight));
        }
        return total;
    }

    public static int positiveWeightProduct(int first, int second) {
        return (int) Math.min(Integer.MAX_VALUE,
                (long) Math.max(1, first) * Math.max(1, second));
    }

    public static <T> T select(List<WeightedEntry<T>> entries, int roll) {
        int total = totalWeight(entries);
        if (roll < 0 || roll >= total) throw new IllegalArgumentException("roll outside total weight");
        int cumulative = 0;
        for (WeightedEntry<T> entry : entries) {
            cumulative = saturatingAdd(cumulative, entry.weight);
            if (roll < cumulative) return entry.value;
        }
        throw new IllegalStateException("unreachable weighted selection");
    }

    private static int saturatingAdd(int first, int second) {
        return (int) Math.min(Integer.MAX_VALUE, (long) first + second);
    }

    private StarcoreMinerLogic() {}
}
