package com.tstmodern.machine.logic;

/** Pure mode and parallel-limit rules for the Hyper Thermal Convector. */
public final class HyperThermalConvectorLogic {

    private HyperThermalConvectorLogic() {}

    public static int baseParallel(boolean heatExchange) {
        return heatExchange ? 128 : 16;
    }

    public static int parallelLimit(int baseParallel, int hatchParallel) {
        long limit = Math.max(0L, baseParallel) + Math.max(0L, hatchParallel);
        return (int) Math.min(Integer.MAX_VALUE, limit);
    }
}
