package com.tstmodern.machine;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;

/**
 * Behavioural port of TST_NetherInterface.
 *
 * <p>Base parallel = 64. A single optional GTCEu Parallel Control Hatch adds
 * its configured parallel amount. Energy cost: 2A IV base maintenance + 1A IV per parallel.</p>
 */
public final class NetherInterfaceMachine extends WorkableElectricMultiblockMachine {

    public NetherInterfaceMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    /**
     * TST base: maxParallel = 64. A single optional GTCEu Parallel Control Hatch adds
     * its configured parallel amount. EUt = 7680 * (2 + parallel).
     */
    public static ModifierFunction recipeModifier(MetaMachine machine, GTRecipe recipe) {
        if (!(machine instanceof NetherInterfaceMachine netherInterface)) {
            return ModifierFunction.NULL;
        }

        long baseParallel = 64L;
        long hatchParallel = netherInterface.getParallelHatch()
                .map(part -> (long) part.getCurrentParallel())
                .orElse(0L);
        int parallelLimit = (int) Math.min(Integer.MAX_VALUE, baseParallel + hatchParallel);
        int parallel = ParallelLogic.getParallelAmount(machine, recipe, parallelLimit);
        if (parallel <= 0) {
            return ModifierFunction.NULL;
        }

        // Base recipe is 1 parallel at 7680 EU/t.
        // Machine consumes 2A IV (15360) base + 1A IV (7680) per parallel -> total = 7680 * (2 + parallel).
        double eutMultiplier = (double) (2 + parallel);

        return ModifierFunction.builder()
                .inputModifier(ContentModifier.multiplier(parallel))
                .outputModifier(ContentModifier.multiplier(parallel))
                .eutMultiplier(eutMultiplier)
                .parallels(parallel)
                .build();
    }
}
