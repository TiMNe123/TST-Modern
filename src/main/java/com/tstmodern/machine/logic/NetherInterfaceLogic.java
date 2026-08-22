package com.tstmodern.machine.logic;

import java.util.Objects;
import java.util.function.IntUnaryOperator;

/** Pure deterministic seams for Nether Interface package and fluid chance rolls. */
public final class NetherInterfaceLogic {

    private static final int PACKAGE_SELECTIONS_PER_CYCLE = 3;

    private NetherInterfaceLogic() {}

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
