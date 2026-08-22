package com.tstmodern.machine;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.CoilWorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.tstmodern.machine.logic.GiantVacuumDryingFurnaceLogic;

/**
 * Behavioural port of TST_GiantVacuumDryingFurnace.
 *
 * <p>Base parallel = one segment * source coil tier * 32.
 * Duration multiplier = (0.8 ^ machine tier) / (source coil tier * 0.5).</p>
 */
public final class GiantVacuumDryingFurnaceMachine extends CoilWorkableElectricMultiblockMachine {

    public GiantVacuumDryingFurnaceMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    public static ModifierFunction recipeModifier(MetaMachine machine, GTRecipe recipe) {
        if (!(machine instanceof GiantVacuumDryingFurnaceMachine furnace)) {
            return ModifierFunction.NULL;
        }

        int coilTier = GiantVacuumDryingFurnaceLogic.sourceCoilTier(
                furnace.getCoilType().getTier());
        int parallelLimit = GiantVacuumDryingFurnaceLogic.parallelLimit(
                coilTier,
                furnace.getParallelHatch().map(h -> h.getCurrentParallel()).orElse(0));
        int parallel = ParallelLogic.getParallelAmount(machine, recipe, parallelLimit);
        if (parallel <= 0) {
            return ModifierFunction.NULL;
        }

        double durationMultiplier = GiantVacuumDryingFurnaceLogic.durationMultiplier(
                furnace.getTier(), coilTier);

        return ModifierFunction.builder()
                .inputModifier(ContentModifier.multiplier(parallel))
                .outputModifier(ContentModifier.multiplier(parallel))
                .eutMultiplier(parallel)
                .durationMultiplier(durationMultiplier)
                .parallels(parallel)
                .build();
    }
}
