package com.tstmodern.machine;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.tstmodern.machine.logic.IncompactCyclotronLogic;

public final class IncompactCyclotronMachine extends WorkableElectricMultiblockMachine {

    public IncompactCyclotronMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    public static ModifierFunction recipeModifier(MetaMachine machine, GTRecipe recipe) {
        if (!(machine instanceof IncompactCyclotronMachine cyclotron)) {
            return ModifierFunction.NULL;
        }

        int limit = IncompactCyclotronLogic.parallelLimit(
                cyclotron.getParallelHatch().map(hatch -> hatch.getCurrentParallel()).orElse(0));
        int parallel = ParallelLogic.getParallelAmount(machine, recipe, limit);
        if (parallel <= 0) {
            return ModifierFunction.NULL;
        }

        return ModifierFunction.builder()
                .modifyAllContents(ContentModifier.multiplier(parallel))
                .eutMultiplier(parallel * IncompactCyclotronLogic.euMultiplier())
                .durationMultiplier(IncompactCyclotronLogic.durationMultiplier())
                .parallels(parallel)
                .build();
    }
}
