package com.tstmodern.machine;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.OverclockingLogic;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.tstmodern.machine.logic.NaquadahFuelRefineryLogic;

import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

public final class NaquadahFuelRefineryMachine extends WorkableElectricMultiblockMachine {
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            NaquadahFuelRefineryMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    @DescSynced
    private int coilTier;

    public NaquadahFuelRefineryMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        Integer matchedTier = getMultiblockState().getMatchContext().get(NaquadahFuelRefineryLogic.COIL_TIER_CONTEXT);
        coilTier = matchedTier == null ? 0 : matchedTier;
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        coilTier = 0;
    }

    public int getCoilTier() {
        return coilTier;
    }

    public static ModifierFunction recipeModifier(MetaMachine machine, GTRecipe recipe) {
        if (!(machine instanceof NaquadahFuelRefineryMachine refinery) || recipe == null) {
            return ModifierFunction.NULL;
        }
        int requiredTier = recipe.data.getInt(NaquadahFuelRefineryLogic.RECIPE_COIL_TIER);
        if (requiredTier < 1 || requiredTier > refinery.coilTier) {
            return ModifierFunction.NULL;
        }

        int parallel = ParallelLogic.getParallelAmount(
                machine, recipe, NaquadahFuelRefineryLogic.parallelLimit(refinery.coilTier));
        if (parallel <= 0) return ModifierFunction.NULL;

        long recipeVoltage = Math.abs(RecipeHelper.getRealEUt(recipe).voltage());
        long ocVoltage = NaquadahFuelRefineryLogic.cappedOverclockVoltage(
                refinery.getOverclockVoltage(), recipeVoltage, refinery.coilTier - requiredTier);
        ModifierFunction overclock = OverclockingLogic.PERFECT_OVERCLOCK.getModifier(
                machine, recipe, ocVoltage, false);
        ModifierFunction parallelism = ModifierFunction.builder()
                .inputModifier(ContentModifier.multiplier(parallel))
                .outputModifier(ContentModifier.multiplier(parallel))
                .eutMultiplier(parallel)
                .parallels(parallel)
                .build();
        return overclock.andThen(parallelism);
    }
}
