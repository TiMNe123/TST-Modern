package com.tstmodern.machine;

import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.tstmodern.machine.logic.MegaStoneBreakerLogic;

import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

/**
 * Behavioural port of TST_MegaStoneBreaker.
 *
 * <p>Normal mode multiplies item output by 4. If both dedicated input hatches have at least
 * 1,000 mB water and lava when the recipe starts, output is multiplied by 1,024 and the
 * machine consumes 1,000 mB of each fluid every 20 running ticks.</p>
 */
public final class MegaStoneBreakerMachine extends WorkableElectricMultiblockMachine {
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER =
            new ManagedFieldHolder(MegaStoneBreakerMachine.class,
                    WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    @DescSynced
    private boolean boosted;

    @Persisted
    private int activeBoostTicks;

    public MegaStoneBreakerMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    /**
     * TST base: maxParallel = 4 * 2^voltageTier. A single optional GTCEu
     * Parallel Control Hatch adds its configured parallel amount to that base.
     */
    public static ModifierFunction parallelAndOutputMultiplier(MetaMachine machine, GTRecipe recipe) {
        if (!(machine instanceof MegaStoneBreakerMachine breaker)) {
            return ModifierFunction.NULL;
        }

        int parallelLimit = MegaStoneBreakerLogic.addParallel(
                MegaStoneBreakerLogic.baseParallel(breaker.getTier()),
                breaker.getParallelHatch().map(h -> h.getCurrentParallel()).orElse(0));
        int outputBonus = MegaStoneBreakerLogic.outputBonus(breaker.hasBoostFluids());

        GTRecipe outputProbe = recipe.copy();
        outputProbe.outputs.clear();
        outputProbe.outputs.putAll(ContentModifier.multiplier(outputBonus).applyContents(recipe.outputs));

        int parallel = ParallelLogic.getParallelAmount(machine, outputProbe, parallelLimit);
        if (parallel <= 0) {
            return ModifierFunction.NULL;
        }

        return ModifierFunction.builder()
                .inputModifier(ContentModifier.multiplier(parallel))
                .outputModifier(ContentModifier.multiplier((double) parallel * outputBonus))
                .eutMultiplier(parallel)
                .parallels(parallel)
                .build();
    }

    @Override
    public boolean alwaysTryModifyRecipe() {
        return true;
    }

    @Override
    public boolean beforeWorking(GTRecipe recipe) {
        boosted = hasBoostFluids();
        activeBoostTicks = 0;
        return super.beforeWorking(recipe);
    }

    @Override
    public boolean onWorking() {
        boolean drainDue = boosted && MegaStoneBreakerLogic.shouldDrainBoost(activeBoostTicks);
        if (drainDue && !canConsumeBoostFluids()) {
            boosted = false;
            return false;
        }
        if (!super.onWorking()) {
            return false;
        }
        if (drainDue) {
            executeBoostFluidDrain();
        }
        if (boosted) {
            activeBoostTicks++;
        }
        return true;
    }

    @Override
    public void afterWorking() {
        super.afterWorking();
        boosted = false;
        activeBoostTicks = 0;
    }

    private boolean hasBoostFluids() {
        return fluidAmount(Fluids.WATER) >= 1_000L && fluidAmount(Fluids.LAVA) >= 1_000L;
    }

    private boolean canConsumeBoostFluids() {
        // Phase 1: Simulate both drains to verify availability without consuming anything.
        if (!drainAcrossInputs(Fluids.WATER, 1_000, IFluidHandler.FluidAction.SIMULATE)
                || !drainAcrossInputs(Fluids.LAVA, 1_000, IFluidHandler.FluidAction.SIMULATE)) {
            return false;
        }
        return true;
    }

    private void executeBoostFluidDrain() {
        // Phase 2: Both fluids confirmed available — execute the actual drain.
        drainAcrossInputs(Fluids.WATER, 1_000, IFluidHandler.FluidAction.EXECUTE);
        drainAcrossInputs(Fluids.LAVA, 1_000, IFluidHandler.FluidAction.EXECUTE);
    }

    private long fluidAmount(Fluid fluid) {
        long amount = 0L;
        for (var recipeHandler : getCapabilitiesFlat(IO.IN, FluidRecipeCapability.CAP)) {
            if (!(recipeHandler instanceof IFluidHandler fluidHandler)) {
                continue;
            }
            for (int tank = 0; tank < fluidHandler.getTanks(); tank++) {
                FluidStack stack = fluidHandler.getFluidInTank(tank);
                if (stack.getFluid() == fluid) {
                    amount += stack.getAmount();
                }
            }
        }
        return amount;
    }

    private boolean drainAcrossInputs(Fluid fluid, int amount, IFluidHandler.FluidAction action) {
        int remaining = amount;
        for (var recipeHandler : getCapabilitiesFlat(IO.IN, FluidRecipeCapability.CAP)) {
            if (!(recipeHandler instanceof IFluidHandler fluidHandler)) {
                continue;
            }
            FluidStack drained = fluidHandler.drain(new FluidStack(fluid, remaining), action);
            remaining -= drained.getAmount();
            if (remaining <= 0) {
                return true;
            }
        }
        return false;
    }
}
