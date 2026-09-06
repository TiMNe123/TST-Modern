package com.tstmodern.machine.logic;

/** Pure calculations used by the Ore Processing Factory. */
public final class OreProcessingFactoryLogic {
    public static final int RECIPE_EUT = 30;
    public static final int LUBRICANT_INTERVAL = 256;

    private OreProcessingFactoryLogic() {}

    public static long usablePower(long voltage, long amperage, boolean singleNormalEnergyHatch) {
        long power = Math.max(0L, voltage) * Math.max(0L, amperage);
        return singleNormalEnergyHatch ? power * 15 / 16 : power * 31 / 32;
    }

    public static int parallelLimit(long usablePower) {
        return (int) Math.min(Integer.MAX_VALUE, usablePower / RECIPE_EUT);
    }

    public static boolean lubricantDue(int activeTicks) {
        return activeTicks >= LUBRICANT_INTERVAL - 1;
    }
}
