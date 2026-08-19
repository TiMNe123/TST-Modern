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

        int tier = breaker.getTier();
        int baseParallel = tier >= 29 ? Integer.MAX_VALUE : (int) Math.min(Integer.MAX_VALUE, 4L << tier);
        int hatchParallel = breaker.getParallelHatch()
                .map(hatch -> hatch.getCurrentParallel())
                .orElse(0);
        int parallelLimit = baseParallel == Integer.MAX_VALUE ? Integer.MAX_VALUE :
                (int) Math.min(Integer.MAX_VALUE, (long) baseParallel + hatchParallel);
        int parallel = ParallelLogic.getParallelAmount(machine, recipe, parallelLimit);
        if (parallel <= 0) {
            return ModifierFunction.NULL;
        }

        int outputBonus = breaker.hasBoostFluids() ? 1_024 : 4;
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
        return super.beforeWorking(recipe);
    }

    @Override
    public boolean onWorking() {
        if (!super.onWorking()) {
            return false;
        }
        if (boosted && getOffsetTimer() % 20L == 0L && !consumeBoostFluids()) {
            boosted = false;
            return false;
        }
        return true;
    }

    @Override
    public void afterWorking() {
        super.afterWorking();
        boosted = false;
    }

    private boolean hasBoostFluids() {
        return fluidAmount(Fluids.WATER) >= 1_000L && fluidAmount(Fluids.LAVA) >= 1_000L;
    }

    private boolean consumeBoostFluids() {
        // Phase 1: Simulate both drains to verify availability without consuming anything.
        if (!drainAcrossInputs(Fluids.WATER, 1_000, IFluidHandler.FluidAction.SIMULATE)
                || !drainAcrossInputs(Fluids.LAVA, 1_000, IFluidHandler.FluidAction.SIMULATE)) {
            return false;
        }
        // Phase 2: Both fluids confirmed available — execute the actual drain.
        drainAcrossInputs(Fluids.WATER, 1_000, IFluidHandler.FluidAction.EXECUTE);
        drainAcrossInputs(Fluids.LAVA, 1_000, IFluidHandler.FluidAction.EXECUTE);
        return true;
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
