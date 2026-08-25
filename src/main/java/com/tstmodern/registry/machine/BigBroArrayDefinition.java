package com.tstmodern.registry.machine;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;

import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.tstmodern.TSTModern;
import com.tstmodern.machine.BigBroArrayMachine;
import com.tstmodern.registry.TSTBlocks;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;

/**
 * Definition and structural predicates for the MegaArray (BigBroArray).
 */
public final class BigBroArrayDefinition {
    public static final MultiblockMachineDefinition MACHINE = TSTModern.REGISTRATE
            .multiblock("big_bro_array", BigBroArrayMachine::new)
            .langValue("MegaArray (BigBroArray)")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTRecipeTypes.DUMMY_RECIPES)
            .appearanceBlock(GTBlocks.CASING_TUNGSTENSTEEL_ROBUST)
            .pattern(definition -> {
                FactoryBlockPattern pattern = FactoryBlockPattern.start(
                        RelativeDirection.RIGHT,
                        RelativeDirection.UP,
                        RelativeDirection.BACK);
                for (String[] aisle : BigBroArrayStructure.CORE_AISLES) {
                    pattern.aisle(aisle);
                }
                return pattern
                        .where('~', Predicates.controller(blocks(definition.get())))
                        .where('A', blocks(GTBlocks.CASING_TEMPERED_GLASS.get())
                                .or(blocks(GTBlocks.FUSION_GLASS.get())))
                        .where('B', Predicates.frames(GTMaterials.TungstenSteel)
                                .or(Predicates.frames(GTMaterials.HSSE))
                                .or(Predicates.frames(GTMaterials.HSSS))
                                .or(Predicates.frames(GTMaterials.Tritanium))
                                .or(Predicates.frames(GTMaterials.NaquadahAlloy))
                                .or(Predicates.frames(GTMaterials.Neutronium)))
                        .where('C', blocks(GTBlocks.MACHINE_CASING_LV.get())
                                .or(blocks(GTBlocks.MACHINE_CASING_MV.get()))
                                .or(blocks(GTBlocks.MACHINE_CASING_HV.get()))
                                .or(blocks(GTBlocks.MACHINE_CASING_EV.get()))
                                .or(blocks(GTBlocks.MACHINE_CASING_IV.get()))
                                .or(blocks(GTBlocks.MACHINE_CASING_LuV.get()))
                                .or(blocks(GTBlocks.MACHINE_CASING_ZPM.get()))
                                .or(blocks(GTBlocks.MACHINE_CASING_UV.get()))
                                .or(blocks(GTBlocks.MACHINE_CASING_UHV.get()))
                                .or(blocks(GTBlocks.MACHINE_CASING_UEV.get()))
                                .or(blocks(GTBlocks.MACHINE_CASING_UIV.get()))
                                .or(blocks(GTBlocks.MACHINE_CASING_UXV.get()))
                                .or(blocks(GTBlocks.MACHINE_CASING_OpV.get()))
                                .or(blocks(GTBlocks.MACHINE_CASING_MAX.get()))
                                .or(blocks(TSTBlocks.PARALLEL_CASINGS.stream().map(r -> r.get()).toArray(Block[]::new))))
                        .where('D', blocks(GTBlocks.CASING_TUNGSTENSTEEL_ROBUST.get())
                                .or(Predicates.autoAbilities(true, false, false))
                                .or(Predicates.autoAbilities(false, false, true))
                                .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMinGlobalLimited(1))
                                .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1)))
                        .where('E', blocks(GTBlocks.CASING_STAINLESS_CLEAN.get()))
                        .where('F', blocks(GTBlocks.CASING_STAINLESS_CLEAN.get())
                                .or(Predicates.abilities(PartAbility.OUTPUT_ENERGY))
                                .or(Predicates.abilities(PartAbility.MUFFLER)))
                        .where(' ', Predicates.any())
                        .build();
            })
            .workableCasingModel(
                    GTBlocks.CASING_TUNGSTENSTEEL_ROBUST.getId(),
                    TSTModern.id("block/multiblock/big_bro_array"))
            .tooltips(
                    Component.translatable("tstmodern.machine.big_bro_array.tooltip.0"),
                    Component.translatable("tstmodern.machine.big_bro_array.tooltip.1"),
                    Component.translatable("tstmodern.machine.big_bro_array.tooltip.2"),
                    Component.translatable("tstmodern.machine.big_bro_array.tooltip.3"),
                    Component.translatable("tstmodern.machine.big_bro_array.tooltip.4"),
                    Component.translatable("tstmodern.machine.big_bro_array.tooltip.5"),
                    Component.translatable("tstmodern.machine.big_bro_array.tooltip.6"))
            .register();

    private BigBroArrayDefinition() {}
}
