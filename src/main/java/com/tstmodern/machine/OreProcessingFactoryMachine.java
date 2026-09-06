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
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.machine.multiblock.part.EnergyHatchPartMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.part.LaserHatchPartMachine;
import com.tstmodern.machine.logic.OreProcessingFactoryLogic;

import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

/** Exact no-overclock processing semantics of the original Ore Processing Factory. */
public final class OreProcessingFactoryMachine extends WorkableElectricMultiblockMachine {
    private static final int LUBRICANT_COST = 3_200;
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER =
            new ManagedFieldHolder(OreProcessingFactoryMachine.class,
                    WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    private int activeTicks;

    public OreProcessingFactoryMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    public static ModifierFunction maximumParallel(MetaMachine machine, GTRecipe recipe) {
        if (!(machine instanceof OreProcessingFactoryMachine factory)) {
            return ModifierFunction.NULL;
        }
        var energy = factory.getEnergyContainer();
        long usablePower = OreProcessingFactoryLogic.usablePower(
                energy.getInputVoltage(), energy.getInputAmperage(), factory.hasSingleNormalEnergyHatch());
        int parallel = ParallelLogic.getParallelAmount(
                machine, recipe, OreProcessingFactoryLogic.parallelLimit(usablePower));
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

    private boolean hasSingleNormalEnergyHatch() {
        var energyHatches = getParts().stream()
                .filter(EnergyHatchPartMachine.class::isInstance)
                .map(part -> (EnergyHatchPartMachine) part)
                .toList();
        return energyHatches.size() == 1 &&
                getParts().stream().noneMatch(LaserHatchPartMachine.class::isInstance) &&
                !energyHatches.get(0).getDefinition().getId().getPath().startsWith("substation_input_hatch");
    }

    @Override
    public boolean alwaysTryModifyRecipe() {
        return true;
    }

    @Override
    public boolean onWorking() {
        boolean drainDue = OreProcessingFactoryLogic.lubricantDue(activeTicks);
        if (drainDue && !drainLubricant(IFluidHandler.FluidAction.SIMULATE)) {
            return false;
        }
        if (!super.onWorking()) {
            return false;
        }
        if (drainDue) {
            drainLubricant(IFluidHandler.FluidAction.EXECUTE);
            activeTicks = 0;
        } else {
            activeTicks++;
        }
        return true;
    }

    private boolean drainLubricant(IFluidHandler.FluidAction action) {
        Fluid lubricant = GTMaterials.Lubricant.getFluid();
        int remaining = LUBRICANT_COST;
        for (var handler : getCapabilitiesFlat(IO.IN, FluidRecipeCapability.CAP)) {
            if (!(handler instanceof IFluidHandler fluidHandler)) {
                continue;
            }
            FluidStack drained = fluidHandler.drain(new FluidStack(lubricant, remaining), action);
            remaining -= drained.getAmount();
            if (remaining <= 0) {
                return true;
            }
        }
        return false;
    }
}
