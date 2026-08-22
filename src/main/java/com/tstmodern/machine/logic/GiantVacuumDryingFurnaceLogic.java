package com.tstmodern.machine.logic;

/** Formula helpers for the Giant Vacuum Drying Furnace's fixed one-segment structure. */
public final class GiantVacuumDryingFurnaceLogic {

    private GiantVacuumDryingFurnaceLogic() {}

    public static int sourceCoilTier(int gtceuTier) {
        long sourceTier = Math.max(1L, (long) gtceuTier + 1L);
        return (int) Math.min(Integer.MAX_VALUE, sourceTier);
    }

    public static int parallelLimit(int sourceCoilTier, int hatchParallel) {
        long base = 32L * Math.max(1, sourceCoilTier);
        return (int) Math.min(Integer.MAX_VALUE, base + Math.max(0L, hatchParallel));
    }

    public static double durationMultiplier(int machineTier, int sourceCoilTier) {
        return Math.pow(0.8, Math.max(0, machineTier)) /
                (Math.max(1, sourceCoilTier) * 0.5);
    }
}
