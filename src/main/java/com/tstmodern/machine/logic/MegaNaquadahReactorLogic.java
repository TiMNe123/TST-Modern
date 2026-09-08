package com.tstmodern.machine.logic;

public final class MegaNaquadahReactorLogic {
    public static final int TICKS_PER_SECOND = 20;
    public static final int LIQUID_AIR_PER_SECOND = 2_400;
    public static final long TICKS_TO_MAX_DISCOUNT = 24L * 60 * 60 * TICKS_PER_SECOND;
    public static final long IDLE_DECAY_PER_TICK = 20L;
    public static final int MAX_DISCOUNT_PERCENT = 50;

    private MegaNaquadahReactorLogic() {}

    public static int recipeSeconds(int durationTicks) {
        return durationTicks <= 0 ? 0 : (durationTicks + TICKS_PER_SECOND - 1) / TICKS_PER_SECOND;
    }

    public static int discountPercent(long runTimeTicks) {
        if (runTimeTicks <= 0) return 0;
        if (runTimeTicks >= TICKS_TO_MAX_DISCOUNT) return MAX_DISCOUNT_PERCENT;
        return (int) (runTimeTicks * MAX_DISCOUNT_PERCENT / TICKS_TO_MAX_DISCOUNT);
    }

    public static int discountedPerSecond(int amount, long runTimeTicks) {
        int discount = discountPercent(runTimeTicks);
        return Math.max(1, amount * (100 - discount) / 100);
    }

    public static int parallelLimit(int configuredLimit, long availableFuel, int fuelPerRecipe) {
        if (configuredLimit <= 0 || availableFuel <= 0 || fuelPerRecipe <= 0) return 0;
        return (int) Math.min(configuredLimit, Math.min(availableFuel / fuelPerRecipe,
                Integer.MAX_VALUE / (long) fuelPerRecipe));
    }

    public static int totalFluidAmount(int perSecond, int parallel, int durationTicks) {
        long total = (long) perSecond * parallel * recipeSeconds(durationTicks);
        return total <= 0 || total > Integer.MAX_VALUE ? -1 : (int) total;
    }

    public static long outputEuPerTick(long base, int coolantEfficiency, int excitedMultiplier, int parallel) {
        if (base <= 0 || coolantEfficiency <= 0 || excitedMultiplier <= 0 || parallel <= 0) return 0;
        long adjusted = base * coolantEfficiency * excitedMultiplier / 100L;
        return adjusted > Long.MAX_VALUE / parallel ? Long.MAX_VALUE : adjusted * parallel;
    }
}
