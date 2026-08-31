package com.tstmodern.machine.logic;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.OverclockingLogic;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;

/**
 * High-fidelity recipe modifiers for processor and generator operating modes of the Big Bro Array.
 *
 * <ul>
 *   <li>Processor: embedded-tier OC first, then parallel + coil discount + speed.</li>
 *   <li>Generator: parallel only — no OC, no speed bonus, no coil discount.</li>
 * </ul>
 */
public final class BigBroArrayRecipeModifiers {

    private BigBroArrayRecipeModifiers() {}

    public static ModifierFunction recipeModifier(
            MetaMachine machine,
            GTRecipe recipe,
            int machineCount,
            int embeddedTier,
            BigBroArrayMode mode,
            int parallelCasingTier,
            int addonCount,
            int coilTier) {

        if (machineCount <= 0 || recipe == null) {
            return ModifierFunction.NULL;
        }

        if (mode == BigBroArrayMode.GENERATOR) {
            return generatorModifier(machine, recipe, machineCount, parallelCasingTier, addonCount);
        }

        return processorModifier(machine, recipe, machineCount, embeddedTier, parallelCasingTier, addonCount, coilTier);
    }

    public static ModifierFunction processorModifier(
            MetaMachine machine,
            GTRecipe recipe,
            int machineCount,
            int embeddedTier,
            int parallelCasingTier,
            int addonCount,
            int coilTier) {

        // Guard: embedded tier must be within valid range for GTValues.V[]
        if (embeddedTier < GTValues.ULV || embeddedTier > GTValues.MAX) {
            return ModifierFunction.NULL;
        }

        long maxAllowedVoltage = GTValues.V[embeddedTier];
        long baseVoltage = RecipeHelper.getRealEUt(recipe).voltage();
        if (baseVoltage > maxAllowedVoltage) {
            return ModifierFunction.NULL;
        }

        // Step 1: Apply embedded-tier overclock FIRST (OC on base recipe, capped by embedded machine tier)
        ModifierFunction embeddedOc = OverclockingLogic.NON_PERFECT_OVERCLOCK
                .getModifier(machine, recipe, maxAllowedVoltage);

        // Step 2: Compute parallel from structure
        long calculatedParallel = BigBroArrayLogic.calculateParallelism(machineCount, parallelCasingTier, addonCount);
        int maxParallel = (int) Math.min(calculatedParallel, Integer.MAX_VALUE);
        if (maxParallel <= 0) {
            return embeddedOc; // OC only, no parallel
        }

        int actualParallel = ParallelLogic.getParallelAmount(machine, recipe, maxParallel);
        if (actualParallel <= 0) {
            return ModifierFunction.NULL;
        }

        // Step 3: Build array effects (parallel + coil discount + speed)
        double durationMultiplier = BigBroArrayLogic.calculateDurationMultiplier(parallelCasingTier);
        double energyDiscount = BigBroArrayLogic.calculateEnergyDiscount(coilTier);

        ModifierFunction arrayEffects = ModifierFunction.builder()
                .inputModifier(ContentModifier.multiplier(actualParallel))
                .outputModifier(ContentModifier.multiplier(actualParallel))
                .eutMultiplier(actualParallel * energyDiscount)
                .durationMultiplier(durationMultiplier)
                .parallels(actualParallel)
                .build();

        // Compose: OC first, then array effects
        return embeddedOc.andThen(arrayEffects);
    }

    public static ModifierFunction generatorModifier(
            MetaMachine machine,
            GTRecipe recipe,
            int machineCount,
            int parallelCasingTier,
            int addonCount) {

        long calculatedParallel = BigBroArrayLogic.calculateParallelism(machineCount, parallelCasingTier, addonCount);
        int maxParallel = (int) Math.min(calculatedParallel, Integer.MAX_VALUE);
        if (maxParallel <= 0) {
            return ModifierFunction.NULL;
        }

        int actualParallel = ParallelLogic.getParallelAmount(machine, recipe, maxParallel);
        if (actualParallel <= 0) {
            return ModifierFunction.NULL;
        }

        // Generator: parallel only. No OC, no speed bonus, no coil discount.
        return ModifierFunction.builder()
                .inputModifier(ContentModifier.multiplier(actualParallel))
                .outputModifier(ContentModifier.multiplier(actualParallel))
                .eutMultiplier(actualParallel)
                .durationMultiplier(1.0)
                .parallels(actualParallel)
                .build();
    }
}
