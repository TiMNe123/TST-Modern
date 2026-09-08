package com.tstmodern.machine.logic;

import com.gregtechceu.gtceu.api.pattern.util.PatternMatchContext;

/** Pure rules for the refinery's uniform field-coil channel. */
public final class NaquadahFuelRefineryLogic {
    public static final String COIL_TIER_CONTEXT = "tstmodern_naquadah_fuel_refinery_coil_tier";
    public static final String RECIPE_COIL_TIER = "nfr_coil_tier";

    private NaquadahFuelRefineryLogic() {}

    public static boolean matchUniformCoil(PatternMatchContext context, int tier) {
        if (context == null || tier < 1 || tier > 4) return false;
        Integer first = context.get(COIL_TIER_CONTEXT);
        if (first == null) {
            context.set(COIL_TIER_CONTEXT, tier);
            return true;
        }
        return first == tier;
    }

    public static int parallelLimit(int coilTier) {
        return 4 * Math.max(0, coilTier);
    }

    public static long cappedOverclockVoltage(long machineVoltage, long recipeVoltage, int perfectOverclocks) {
        long cap = Math.max(1L, recipeVoltage);
        for (int i = 0; i < Math.max(0, perfectOverclocks); i++) {
            if (cap > Long.MAX_VALUE / 4L) return machineVoltage;
            cap *= 4L;
        }
        return Math.min(machineVoltage, cap);
    }
}
