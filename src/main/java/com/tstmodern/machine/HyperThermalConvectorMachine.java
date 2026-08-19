package com.tstmodern.machine;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;

/**
 * Behavioural port of TST_HyperThermalConvector.
 * High-throughput heat exchange and rapid cooling multiblock.
 */
public final class HyperThermalConvectorMachine extends WorkableElectricMultiblockMachine {

    public HyperThermalConvectorMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    public static ModifierFunction recipeModifier(MetaMachine machine, GTRecipe recipe) {
        if (!(machine instanceof HyperThermalConvectorMachine convector)) {
            return ModifierFunction.NULL;
        }

        int parallelLimit = convector.getParallelHatch()
                .map(part -> part.getCurrentParallel())
                .orElse(128);
        int parallel = ParallelLogic.getParallelAmount(machine, recipe, parallelLimit);
        if (parallel <= 0) {
            return ModifierFunction.NULL;
        }

        return ModifierFunction.builder()
                .inputModifier(ContentModifier.multiplier(parallel))
                .outputModifier(ContentModifier.multiplier(parallel))
                .eutMultiplier(parallel)
                .parallels(parallel)
                .build();
    }
}
