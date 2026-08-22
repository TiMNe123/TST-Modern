package com.tstmodern.machine.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.ToIntFunction;

/** Pure deterministic seams for Nether Interface package and fluid chance rolls. */
public final class NetherInterfaceLogic {

    private static final int PACKAGE_SELECTIONS_PER_CYCLE = 3;

    private NetherInterfaceLogic() {}

    public static int powerParallel(long availableEUt, long ivEUt) {
        if (ivEUt <= 0 || availableEUt < 3L * ivEUt) return 0;
        return (int) Math.min(Integer.MAX_VALUE, availableEUt / ivEUt - 2L);
    }

    public static long saturatedMultiply(long left, long right) {
        if (left <= 0 || right <= 0) {
            return 0;
        }
        if (left > Long.MAX_VALUE / right) {
            return Long.MAX_VALUE;
        }
        return left * right;
    }

    public static int parallelLimit(int baseParallel, int hatchParallel) {
        long limit = Math.max(0L, baseParallel) + Math.max(0L, hatchParallel);
        return (int) Math.min(Integer.MAX_VALUE, limit);
    }

    public static double eutMultiplier(int parallel) {
        return (double) parallel + 2.0;
    }

    public static int selectWeightedIndex(int roll, int[] weights) {
        int total = totalWeight(weights);
        if (roll < 0 || roll >= total) {
            throw new IllegalArgumentException("roll must be between 0 (inclusive) and total weight (exclusive)");
        }

        long cumulative = 0;
        for (int index = 0; index < weights.length; index++) {
            cumulative += weights[index];
            if (roll < cumulative) {
                return index;
            }
        }
        throw new IllegalStateException("validated weighted roll did not map to a package");
    }

    public static int[] selectThreeWeighted(IntUnaryOperator boundedRoll, int[] weights) {
        Objects.requireNonNull(boundedRoll, "boundedRoll");
        int total = totalWeight(weights);
        int[] selected = new int[PACKAGE_SELECTIONS_PER_CYCLE];
        for (int selection = 0; selection < selected.length; selection++) {
            selected[selection] = selectWeightedIndex(boundedRoll.applyAsInt(total), weights);
        }
        return selected;
    }

    public static <T, R> List<R> selectAndScaleThreeWeighted(
                                                              List<? extends T> entries,
                                                              ToIntFunction<? super T> rawWeightExtractor,
                                                              IntUnaryOperator boundedRoll,
                                                              BiFunction<? super T, Integer, ? extends R> scaler,
                                                              int times) {
        Objects.requireNonNull(entries, "entries");
        Objects.requireNonNull(rawWeightExtractor, "rawWeightExtractor");
        Objects.requireNonNull(boundedRoll, "boundedRoll");
        Objects.requireNonNull(scaler, "scaler");
        if (entries.isEmpty()) {
            throw new IllegalArgumentException("entries must not be empty");
        }

        int[] rawWeights = entries.stream().mapToInt(rawWeightExtractor).toArray();
        int[] selectedIndexes = selectThreeWeighted(boundedRoll, rawWeights);
        List<R> outputs = new ArrayList<>(PACKAGE_SELECTIONS_PER_CYCLE);
        for (int selectedIndex : selectedIndexes) {
            outputs.add(scaler.apply(entries.get(selectedIndex), times));
        }
        return List.copyOf(outputs);
    }

    public static boolean rollOnce(IntUnaryOperator boundedRoll, int chance, int maxChance) {
        Objects.requireNonNull(boundedRoll, "boundedRoll");
        if (maxChance <= 0) {
            throw new IllegalArgumentException("maxChance must be positive");
        }
        if (chance < 0 || chance > maxChance) {
            throw new IllegalArgumentException("chance must be between 0 and maxChance");
        }

        int roll = boundedRoll.applyAsInt(maxChance);
        if (roll < 0 || roll >= maxChance) {
            throw new IllegalArgumentException("bounded roll must be between 0 (inclusive) and maxChance (exclusive)");
        }
        return passesChance(roll, chance);
    }

    public static <T, R> List<R> rollAndScaleOnce(
                                                   T entry,
                                                   ToIntFunction<? super T> rawChanceExtractor,
                                                   ToIntFunction<? super T> rawMaxChanceExtractor,
                                                   IntUnaryOperator boundedRoll,
                                                   BiFunction<? super T, Integer, ? extends R> scaler,
                                                   int times) {
        Objects.requireNonNull(entry, "entry");
        Objects.requireNonNull(rawChanceExtractor, "rawChanceExtractor");
        Objects.requireNonNull(rawMaxChanceExtractor, "rawMaxChanceExtractor");
        Objects.requireNonNull(boundedRoll, "boundedRoll");
        Objects.requireNonNull(scaler, "scaler");

        int rawChance = rawChanceExtractor.applyAsInt(entry);
        int rawMaxChance = rawMaxChanceExtractor.applyAsInt(entry);
        if (!rollOnce(boundedRoll, rawChance, rawMaxChance)) {
            return List.of();
        }
        R scaled = scaler.apply(entry, times);
        return List.of(scaled);
    }

    public static boolean passesChance(int roll, int chance) {
        return roll < chance;
    }

    public static int scaledAmount(int amount, int multiplier) {
        long scaled = (long) amount * multiplier;
        if (scaled > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        if (scaled < Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        return (int) scaled;
    }

    private static int totalWeight(int[] weights) {
        Objects.requireNonNull(weights, "weights");
        if (weights.length == 0) {
            throw new IllegalArgumentException("weights must not be empty");
        }

        long total = 0;
        for (int weight : weights) {
            if (weight < 0) {
                throw new IllegalArgumentException("weights must not be negative");
            }
            total += weight;
        }
        if (total <= 0 || total > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("total weight must be positive and fit in an int");
        }
        return (int) total;
    }
}
