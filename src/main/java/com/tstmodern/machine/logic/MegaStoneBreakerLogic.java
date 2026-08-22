package com.tstmodern.machine.logic;

public final class MegaStoneBreakerLogic {
    private MegaStoneBreakerLogic() {}

    public static int baseParallel(int machineTier) {
        if (machineTier >= 29) return Integer.MAX_VALUE;
        return (int) Math.min(Integer.MAX_VALUE, 4L << Math.max(0, machineTier));
    }

    public static int addParallel(int base, int hatch) {
        return (int) Math.min(Integer.MAX_VALUE,
                Math.max(0L, (long) base) + Math.max(0L, (long) hatch));
    }

    public static int outputBonus(boolean boosted) {
        return boosted ? 1_024 : 4;
    }

    public static boolean shouldDrainBoost(int activeBoostTicks) {
        return activeBoostTicks >= 0 && activeBoostTicks % 20 == 0;
    }
}
