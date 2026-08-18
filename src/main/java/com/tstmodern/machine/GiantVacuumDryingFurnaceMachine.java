package com.tstmodern.machine;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.CoilWorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;

/**
 * Behavioural port of TST_GiantVacuumDryingFurnace.
 *
 * <p>Base parallel = piece * coilTier * 32.
 * Speed bonus: duration multiplied by (0.8 ^ voltageTierDelta) / (1 + 0.5 * (coilTier - 1)).</p>
 */
public final class GiantVacuumDryingFurnaceMachine extends CoilWorkableElectricMultiblockMachine {

    public GiantVacuumDryingFurnaceMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    public static ModifierFunction recipeModifier(MetaMachine machine, GTRecipe recipe) {
        if (!(machine instanceof GiantVacuumDryingFurnaceMachine furnace)) {
            return ModifierFunction.NULL;
        }

        int coilTier = Math.max(1, furnace.getCoilType().getTier());
        int piece = 1; // Standard tower segment
        long baseParallel = (long) piece * coilTier * 32L;
        long hatchParallel = furnace.getParallelHatch()
                .map(part -> (long) part.getCurrentParallel())
                .orElse(0L);
        int parallelLimit = (int) Math.min(Integer.MAX_VALUE, baseParallel + hatchParallel);
        int parallel = ParallelLogic.getParallelAmount(machine, recipe, parallelLimit);
        if (parallel <= 0) {
            return ModifierFunction.NULL;
        }

        int machineTier = furnace.getTier();
        int recipeTier = RecipeHelper.getRecipeEUtTier(recipe);
        int tierDelta = Math.max(0, machineTier - recipeTier);
        double voltageSpeedReduction = Math.pow(0.8, tierDelta);
        double coilSpeedBoost = 1.0 + (coilTier - 1) * 0.5;
        double durationMultiplier = voltageSpeedReduction / coilSpeedBoost;

        return ModifierFunction.builder()
                .inputModifier(ContentModifier.multiplier(parallel))
                .outputModifier(ContentModifier.multiplier(parallel))
                .eutMultiplier(parallel)
                .durationMultiplier(durationMultiplier)
                .parallels(parallel)
                .build();
    }
}
