package com.tstmodern.registry.machine;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.SimpleTieredMachine;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.gregtechceu.gtceu.common.data.machines.GTMachineUtils;
import com.tstmodern.TSTModern;
import com.tstmodern.registry.TSTRecipeTypes;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.common.data.machines.GTMachineUtils.workableTiered;
import static com.gregtechceu.gtceu.utils.FormattingUtil.toEnglishName;

/**
 * Definition for tiered Mass Fabricator singleblock machines (UHV to MAX tiers).
 */
public final class MassFabricatorDefinition {

    public static final MachineDefinition[] MACHINES = GTMachineUtils.registerTieredMachines(
            TSTModern.REGISTRATE,
            "mass_fabricator",
            (holder, tier) -> new SimpleTieredMachine(holder, tier, GTMachineUtils.defaultTankSizeFunction),
            (tier, builder) -> builder
                    .recipeModifier(GTRecipeModifiers.OC_NON_PERFECT)
                    .langValue("%s %s %s".formatted(VLVH[tier], toEnglishName("mass_fabricator"), VLVT[tier]))
                    .editableUI(SimpleTieredMachine.EDITABLE_UI_CREATOR.apply(GTCEu.id("mass_fabricator"), TSTRecipeTypes.MASS_FABRICATOR))
                    .rotationState(RotationState.NON_Y_AXIS)
                    .recipeType(TSTRecipeTypes.MASS_FABRICATOR)
                    .workableTieredHullModel(GTCEu.id("block/machines/mass_fabricator"))
                    .tooltips(workableTiered(tier, GTValues.V[tier], GTValues.V[tier] * 64, TSTRecipeTypes.MASS_FABRICATOR,
                            GTMachineUtils.defaultTankSizeFunction.applyAsInt(tier), true))
                    .register(),
            GTValues.tiersBetween(GTValues.UHV, GTCEuAPI.isHighTier() ? GTValues.MAX : GTValues.OpV)
    );

    private MassFabricatorDefinition() {}
}
