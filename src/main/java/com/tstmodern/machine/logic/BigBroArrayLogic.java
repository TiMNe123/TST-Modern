package com.tstmodern.machine.logic;

import java.math.BigInteger;

/**
 * Pure calculation logic for BigBroArray parallelism, energy reduction, and speed multiplier.
 */
public final class BigBroArrayLogic {

    private BigBroArrayLogic() {}

    /**
     * Calculates the effective parallelism based on embedded machine count, parallel casing tier, and addon presence.
     * Base parallelism is 64 without addon.
     * With addon structure, each machine provides 2^(casingTier) parallelism.
     */
    public static long calculateParallelism(int machineCount, int parallelCasingTier, boolean hasAddon) {
        if (machineCount <= 0) {
            return 0;
        }
        if (!hasAddon) {
            return Math.min(64L, (long) machineCount * 64L);
        }
        // Tier 0 -> 1x, Tier 1 -> 2x, Tier 2 -> 4x, Tier 3 -> 8x, Tier 4 -> 16x, Tier 5 -> 32x per machine
        // Or using exponential scaling from TST 1.7.10: 2^(tier) per machine
        long multiplier = 1L << Math.min(30, parallelCasingTier);
        try {
            return Math.multiplyExact((long) machineCount, multiplier);
        } catch (ArithmeticException overflow) {
            return Long.MAX_VALUE;
        }
    }

    /**
     * Calculates energy consumption discount from heating coils.
     * Each coil tier reduces energy consumption by 10% multiplicatively (0.9^coilTier).
     */
    public static double calculateEnergyDiscount(int coilTier) {
        if (coilTier <= 0) {
            return 1.0;
        }
        return Math.pow(0.9, coilTier);
    }

    /**
     * Calculates speed multiplier from parallel casings.
     * Each parallel casing tier increases processing speed by 50% multiplicatively (1.5^casingTier).
     */
    public static double calculateSpeedMultiplier(int parallelCasingTier) {
        if (parallelCasingTier <= 0) {
            return 1.0;
        }
        return Math.pow(1.5, parallelCasingTier);
    }

    /**
     * Calculates pollution per second (20 points per parallel, capped at 200,000/s).
     */
    public static int calculatePollution(long actualParallel) {
        if (actualParallel <= 0) {
            return 0;
        }
        long raw = actualParallel * 20L;
        return (int) Math.min(200_000L, raw);
    }
}
