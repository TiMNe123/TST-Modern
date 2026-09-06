package com.tstmodern.machine.logic;

import java.util.List;
import java.util.function.DoubleSupplier;

/** Source-faithful rack math, kept independent from world/inventory plumbing. */
public final class AstralComputingLogic {
    public record Component(double computation, double heat, double coefficient, int maxHeat, boolean subZero) {}
    public record Stack(Component component, int count) {}
    public record TickResult(int computation, int heat) {}
    public record CoolingResult(int heat, int fluidUsed, double multiplier) {}

    private AstralComputingLogic() {}

    public static TickResult tick(
            List<Stack> stacks, int rackHeat, double overclock, double overvolt, DoubleSupplier random) {
        double computation = 0;
        double heat = 0;
        for (Stack stack : stacks) {
            Component component = stack.component();
            int count = stack.count();
            if (component.subZero() || rackHeat >= 0) {
                heat += (1 + component.coefficient() * rackHeat / 10_000.0) * count *
                        (component.heat() > 0
                                ? component.heat() * overclock * overclock * overvolt
                                : component.heat());
                if (overvolt * 10 > 7 + random.getAsDouble()) {
                    double reliability = Math.max(0, Math.min(
                            Math.min(overclock, overvolt * 2 - 0.25),
                            1 + random.getAsDouble() + (overvolt - 1) - (overclock - 1) / 2));
                    computation += component.computation() * reliability * count;
                }
            } else {
                computation += component.computation() * overclock * count;
            }
        }
        return new TickResult((int) Math.floor(computation), rackHeat + (int) Math.ceil(heat));
    }

    public static int passiveCool(List<Stack> stacks, int rackHeat) {
        int heat = rackHeat > 0 ? rackHeat - 1 : rackHeat < 0 ? rackHeat + 1 : 0;
        if (heat <= 0) return Math.max(-10_000, heat);
        double cooling = 0;
        for (Stack stack : stacks) {
            if (stack.component().heat() < 0) {
                cooling += stack.component().heat() * stack.count() * (heat / 10_000.0);
            }
        }
        return Math.max(-10_000, heat + (int) Math.max(-heat, Math.ceil(cooling)));
    }

    public static CoolingResult coolWithFluid(int rackHeat, int availableMb, double coefficient) {
        if (rackHeat <= 0 || availableMb <= 0 || coefficient <= 0) {
            return new CoolingResult(rackHeat, 0, 1);
        }
        double maxHeatCanCool = coefficient * availableMb;
        double multiplier = 1 + Math.log10(1 + maxHeatCanCool * rackHeat);
        int realHeatCanCool = (int) Math.min(
                maxHeatCanCool,
                rackHeat - rackHeat / (multiplier * multiplier));
        int fluidUsed = (int) Math.min(availableMb, realHeatCanCool / coefficient);
        return new CoolingResult(rackHeat - realHeatCanCool, fluidUsed, multiplier);
    }

    public static long requiredEUt(long baseVoltage, double overclock, double overvolt, double coolantMultiplier) {
        long multiplierSquared = (long) (coolantMultiplier * coolantMultiplier);
        long thingsActive = (long) (4 * coolantMultiplier * coolantMultiplier) + 1;
        long amperage = (1 + (thingsActive >> 2)) * multiplierSquared;
        return (long) (baseVoltage * overclock * overvolt) * amperage;
    }
}
