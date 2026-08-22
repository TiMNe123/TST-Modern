package com.tstmodern.machine;

import static com.gregtechceu.gtceu.api.GTValues.IV;
import static com.gregtechceu.gtceu.api.GTValues.VA;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.tstmodern.machine.logic.NetherInterfaceLogic;

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

        int limit = NetherInterfaceLogic.parallelLimit(64,
                netherInterface.getParallelHatch().map(h -> h.getCurrentParallel()).orElse(0));
        int resourceParallel = ParallelLogic.getParallelAmountWithoutEU(machine, recipe, limit);
        long availableEUt = NetherInterfaceLogic.saturatedMultiply(
                netherInterface.getEnergyContainer().getInputVoltage(),
                netherInterface.getEnergyContainer().getInputAmperage());
        int powerParallel = NetherInterfaceLogic.powerParallel(availableEUt, VA[IV]);
        int parallel = Math.min(resourceParallel, powerParallel);
        if (parallel <= 0) {
            return ModifierFunction.NULL;
        }

        return ModifierFunction.builder()
                .inputModifier(ContentModifier.multiplier(parallel))
                .outputModifier(ContentModifier.multiplier(parallel))
                .eutMultiplier((double) (parallel + 2))
                .parallels(parallel)
                .build();
    }
}
