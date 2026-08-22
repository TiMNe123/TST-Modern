package com.tstmodern.machine;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.tstmodern.machine.logic.HyperThermalConvectorLogic;
import com.tstmodern.registry.TSTRecipeTypes;

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

        boolean heatExchange = recipe.recipeType == TSTRecipeTypes.RAPID_HEAT_EXCHANGE;
        int limit = HyperThermalConvectorLogic.parallelLimit(
                HyperThermalConvectorLogic.baseParallel(heatExchange),
                convector.getParallelHatch().map(h -> h.getCurrentParallel()).orElse(0));
        int parallel = ParallelLogic.getParallelAmount(machine, recipe, limit);
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
