package com.tstmodern.registry.machine;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.tstmodern.TSTModern;
import com.tstmodern.machine.BigBroArrayMachine;
import com.tstmodern.registry.TSTBlocks;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;

/**
 * Definition and structural predicates for the Mega Array.
 */
public final class BigBroArrayDefinition {
        public static final MultiblockMachineDefinition MACHINE = TSTModern.REGISTRATE
                        .multiblock("big_bro_array", BigBroArrayMachine::new)
                        .langValue("Mega Array")
                        .rotationState(RotationState.NON_Y_AXIS)
                        .recipeType(GTRecipeTypes.DUMMY_RECIPES)
                        .recipeModifiers(BigBroArrayMachine::recipeModifier, GTRecipeModifiers.OC_NON_PERFECT)
                        .appearanceBlock(GTBlocks.CASING_TUNGSTENSTEEL_ROBUST)
                        .pattern(definition -> {
                                FactoryBlockPattern pattern = FactoryBlockPattern.start(
                                                RelativeDirection.RIGHT,
                                                RelativeDirection.DOWN,
                                                RelativeDirection.BACK);
                                for (String[] aisle : BigBroArrayStructure.CORE_AISLES) {
                                        pattern.aisle(aisle);
                                }
                                return pattern
                                                .where('~', Predicates.controller(blocks(definition.get())))
                                                .where('A', blocks(GTBlocks.CASING_TEMPERED_GLASS.get())
                                                                .or(blocks(GTBlocks.FUSION_GLASS.get())))
                                                .where('B', Predicates.frames(GTMaterials.TungstenSteel))
                                                .where('C', blocks(TSTBlocks.PARALLEL_CASINGS.stream().map(r -> r.get())
                                                                .toArray(Block[]::new)))
                                                .where('D', blocks(GTBlocks.CASING_TUNGSTENSTEEL_ROBUST.get())
                                                                .or(Predicates.autoAbilities(true, true, true))
                                                                .or(Predicates.abilities(PartAbility.INPUT_ENERGY, PartAbility.SUBSTATION_INPUT_ENERGY, PartAbility.INPUT_LASER)
                                                                                .setMinGlobalLimited(1)
                                                                                .setMaxGlobalLimited(2)
                                                                                .setPreviewCount(1))
                                                                .or(Predicates.abilities(PartAbility.IMPORT_ITEMS)
                                                                                .setPreviewCount(1))
                                                                .or(Predicates.abilities(PartAbility.EXPORT_ITEMS)
                                                                                .setPreviewCount(1))
                                                                .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS)
                                                                                .setPreviewCount(1))
                                                                .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS)
                                                                                .setPreviewCount(1)))
                                                .where('E', blocks(GTBlocks.CASING_STAINLESS_CLEAN.get()))
                                                .where('F', blocks(GTBlocks.CASING_STAINLESS_CLEAN.get()))
                                                .where('H', Predicates.heatingCoils())
                                                .where(' ', Predicates.any())
                                                .build();
                        })
                        .workableCasingModel(
                                        GTCEu.id("block/casings/solid/machine_casing_robust_tungstensteel"),
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

        private BigBroArrayDefinition() {
        }
}
