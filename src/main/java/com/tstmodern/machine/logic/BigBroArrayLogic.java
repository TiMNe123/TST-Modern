package com.tstmodern.machine.logic;

/**
 * Pure calculation logic for BigBroArray parallelism, energy reduction, and speed multiplier.
 * All formulas are ported 1:1 from TST 1.7.10 {@code TST_BigBroArray}:
 * <ul>
 *   <li>{@code casingMultiplier = parallelismTier > 3 ? (parallelismTier + 6) : parallelismTier}</li>
 *   <li>{@code actualParallelism = min(stackSize << casingMultiplier, maxParallelism)}</li>
 *   <li>{@code maxParallelism = 64 << ((tier * 2) + (tier > 3 ? 6 : 0)) * (1 + addonCount)} for tier &lt; 5,
 *       else {@code (Integer.MAX_VALUE << casingMultiplier) / 5 * (1 + addonCount)}</li>
 *   <li>No addon: fixed cap of 64 parallel.</li>
 * </ul>
 */
public final class BigBroArrayLogic {

    private BigBroArrayLogic() {}

    /**
     * TST 1.7.10: {@code casingMultiplier = tier > 3 ? tier + 6 : tier}.
     */
    public static int casingMultiplier(int parallelCasingTier) {
        return parallelCasingTier > 3 ? parallelCasingTier + 6 : parallelCasingTier;
    }

    /**
     * TST 1.7.10 maximum parallelism by addon count and parallel casing tier.
     */
    public static long calculateMaxParallelism(int addonCount, int parallelCasingTier) {
        if (addonCount <= 0) {
            return 64L;
        }
        int multiplier = casingMultiplier(parallelCasingTier);
        if (parallelCasingTier >= 5) {
            long infinity = Integer.MAX_VALUE << multiplier;
            return (infinity / 5) * (1L + addonCount);
        }
        long base = 64L << ((long) parallelCasingTier * 2 + (parallelCasingTier > 3 ? 6 : 0));
        return saturatingMultiply(base, 1L + addonCount);
    }

    /**
     * TST 1.7.10: {@code actual = min(stackSize << casingMultiplier, maxParallelism)}.
     * Returns the effective parallelism for an embedded machine stack.
     */
    public static long calculateParallelism(int machineCount, int parallelCasingTier, int addonCount) {
        if (machineCount <= 0 || addonCount <= 0 && machineCount <= 0) {
            return 0;
        }
        if (addonCount <= 0) {
            // No addon structure: hard cap of 64 regardless of casings
            return Math.min(64L, machineCount);
        }
        long shifted = shiftLeftSaturating(machineCount, casingMultiplier(parallelCasingTier));
        long max = calculateMaxParallelism(addonCount, parallelCasingTier);
        return Math.min(shifted, max);
    }

    /**
     * TST 1.7.10: {@code processingLogic.setEuModifier((float) Math.pow(0.9, coilTier))}.
     * Each coil tier multiplies EU consumption by 0.9.
     */
    public static double calculateEnergyDiscount(int coilTier) {
        if (coilTier <= 0) {
            return 1.0;
        }
        return Math.pow(0.9, coilTier);
    }

    /**
     * TST 1.7.10: {@code processingLogic.setSpeedBonus((float) Math.pow(0.66, parallelismTier))}.
     * Duration is multiplied by 0.66 per tier; the displayed "speed boost" is its reciprocal (~1.515).
     */
    public static double calculateDurationMultiplier(int parallelCasingTier) {
        if (parallelCasingTier <= 0) {
            return 1.0;
        }
        return Math.pow(0.66, parallelCasingTier);
    }

    /**
     * Display-only speed boost factor shown in the original UI ({@code Math.pow(1.5, tier)}).
     */
    public static double calculateSpeedBoostDisplay(int parallelCasingTier) {
        if (parallelCasingTier <= 0) {
            return 1.0;
        }
        return Math.pow(1.5, parallelCasingTier);
    }

    /**
     * TST 1.7.10: {@code getPollutionPerTick -> min(actualParallelism, 10000)} pollution per tick.
     */
    public static int calculatePollution(long actualParallel) {
        if (actualParallel <= 0) {
            return 0;
        }
        return (int) Math.min(actualParallel, 10_000L);
    }

    private static long saturatingMultiply(long a, long b) {
        try {
            return Math.multiplyExact(a, b);
        } catch (ArithmeticException overflow) {
            return Long.MAX_VALUE;
        }
    }

    private static long shiftLeftSaturating(long value, int bits) {
        if (bits >= Long.SIZE - 1 || value > (Long.MAX_VALUE >> bits)) {
            return Long.MAX_VALUE;
        }
        return value << bits;
    }
}
