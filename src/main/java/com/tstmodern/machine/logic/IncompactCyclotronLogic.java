package com.tstmodern.machine.logic;

public final class IncompactCyclotronLogic {
    private static final int SOURCE_PARALLEL = 256;

    private IncompactCyclotronLogic() {}

    public static int parallelLimit(int hatchParallel) {
        return (int) Math.min(Integer.MAX_VALUE, SOURCE_PARALLEL + Math.max(0L, hatchParallel));
    }

    public static double euMultiplier() {
        return 1.6;
    }

    public static double durationMultiplier() {
        return 0.5;
    }
}
