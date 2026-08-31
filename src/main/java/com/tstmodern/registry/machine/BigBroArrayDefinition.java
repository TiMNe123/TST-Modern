package com.tstmodern.registry.machine;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;

import java.util.Arrays;
import java.util.List;
import java.util.function.ToIntFunction;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.MultiblockShapeInfo;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTMaterialBlocks;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.common.machine.multiblock.part.EnergyHatchPartMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.part.LaserHatchPartMachine;
import com.tstmodern.TSTModern;
import com.tstmodern.machine.BigBroArrayMachine;
import com.tstmodern.machine.logic.BigBroArrayTierRules;
import com.tstmodern.registry.TSTBlocks;

import com.lowdragmc.lowdraglib.utils.BlockInfo;
import com.tterrag.registrate.util.entry.BlockEntry;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** Definition and structural predicates for the Mega Array. */
public final class BigBroArrayDefinition {

    public static final MultiblockMachineDefinition MACHINE = TSTModern.REGISTRATE
            .multiblock("big_bro_array", BigBroArrayMachine::new)
            .langValue("Mega Array")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTRecipeTypes.DUMMY_RECIPES)
            .recipeModifiers(BigBroArrayMachine::recipeModifier)
            .appearanceBlock(GTBlocks.CASING_TUNGSTENSTEEL_ROBUST)
            .partAppearance(BigBroArrayDefinition::partAppearance)
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
                        .where('A', uniformTierPredicate(
                                BigBroArrayTierRules.GLASS_TIER_CONTEXT,
                                BigBroArrayTierRules::glassTier,
                                GTBlocks.CASING_TEMPERED_GLASS.get(),
                                GTBlocks.FUSION_GLASS.get()))
                        .where('B', uniformTierPredicate(
                                BigBroArrayTierRules.FRAME_TIER_CONTEXT,
                                BigBroArrayTierRules::frameTier,
                                frame(GTMaterials.Titanium),
                                frame(GTMaterials.TungstenSteel),
                                frame(GTMaterials.NaquadahAlloy),
                                frame(GTMaterials.Trinium),
                                frame(GTMaterials.Neutronium),
                                frame(GTMaterials.Tritanium)))
                        .where('C', uniformTierPredicate(
                                BigBroArrayTierRules.MACHINE_CASING_TIER_CONTEXT,
                                BigBroArrayTierRules::machineCasingTier,
                                GTBlocks.MACHINE_CASING_LV.get(),
                                GTBlocks.MACHINE_CASING_MV.get(),
                                GTBlocks.MACHINE_CASING_HV.get(),
                                GTBlocks.MACHINE_CASING_EV.get(),
                                GTBlocks.MACHINE_CASING_IV.get(),
                                GTBlocks.MACHINE_CASING_LuV.get(),
                                GTBlocks.MACHINE_CASING_ZPM.get(),
                                GTBlocks.MACHINE_CASING_UV.get(),
                                GTBlocks.MACHINE_CASING_UHV.get(),
                                GTBlocks.MACHINE_CASING_UEV.get(),
                                GTBlocks.MACHINE_CASING_UIV.get(),
                                GTBlocks.MACHINE_CASING_UXV.get(),
                                GTBlocks.MACHINE_CASING_OpV.get(),
                                GTBlocks.MACHINE_CASING_MAX.get()))
                        .where('D', blocks(GTBlocks.CASING_TUNGSTENSTEEL_ROBUST.get())
                                .or(Predicates.autoAbilities(true, true, false))
                                .or(Predicates.abilities(
                                                PartAbility.IMPORT_ITEMS,
                                                PartAbility.IMPORT_FLUIDS)
                                        .setMinGlobalLimited(1)
                                        .setPreviewCount(1))
                                .or(Predicates.abilities(
                                                PartAbility.EXPORT_ITEMS,
                                                PartAbility.EXPORT_FLUIDS)
                                        .setMinGlobalLimited(1)
                                        .setPreviewCount(1)))
                        .where('E', blocks(GTBlocks.CASING_STAINLESS_CLEAN.get()))
                        .where('F', blocks(GTBlocks.CASING_STAINLESS_CLEAN.get())
                                .or(Predicates.abilities(
                                                PartAbility.INPUT_ENERGY,
                                                PartAbility.SUBSTATION_INPUT_ENERGY,
                                                PartAbility.INPUT_LASER,
                                                PartAbility.OUTPUT_ENERGY,
                                                PartAbility.SUBSTATION_OUTPUT_ENERGY,
                                                PartAbility.OUTPUT_LASER)
                                        .setMinGlobalLimited(1)
                                        .setMaxGlobalLimited(4)
                                        .setPreviewCount(1)))
                        .where(' ', Predicates.any())
                        .build();
            })
            .shapeInfos(BigBroArrayDefinition::shapeInfos)
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

    private BigBroArrayDefinition() {}

    private static BlockState partAppearance(
                                             IMultiController ignoredController,
                                             IMultiPart part,
                                             Direction ignoredSide) {
        if (part instanceof EnergyHatchPartMachine || part instanceof LaserHatchPartMachine) {
            return GTBlocks.CASING_STAINLESS_CLEAN.get().defaultBlockState();
        }
        return GTBlocks.CASING_TUNGSTENSTEEL_ROBUST.get().defaultBlockState();
    }

    private static List<MultiblockShapeInfo> shapeInfos(MultiblockMachineDefinition definition) {
        return BigBroArrayStructure.previewLayouts().stream()
                .map(layout -> shapeInfo(definition, layout))
                .toList();
    }

    private static MultiblockShapeInfo shapeInfo(
                                                 MultiblockMachineDefinition definition,
                                                 String[][] layout) {
        MultiblockShapeInfo.ShapeInfoBuilder builder = MultiblockShapeInfo.builder();
        for (String[] aisle : layout) {
            builder.aisle(aisle);
        }
        return builder
                .where('~', definition, Direction.NORTH)
                .where('A', GTBlocks.FUSION_GLASS.get())
                .where('B', frame(GTMaterials.Tritanium))
                .where('C', GTBlocks.MACHINE_CASING_MAX.get())
                .where('D', GTBlocks.CASING_TUNGSTENSTEEL_ROBUST.get())
                .where('E', GTBlocks.CASING_STAINLESS_CLEAN.get())
                .where('F', GTBlocks.CASING_STAINLESS_CLEAN.get())
                .where('G', TSTBlocks.PARALLEL_CASING_MK1.get())
                .where('H', frame(GTMaterials.Tritanium))
                .where('I', GTBlocks.COIL_CUPRONICKEL.get())
                .where('J', GTBlocks.CASING_TUNGSTENSTEEL_GEARBOX.get())
                .where('K', GTBlocks.FUSION_GLASS.get())
                .where('L', GTBlocks.CASING_STAINLESS_CLEAN.get())
                .where('M', GTMachines.MAINTENANCE_HATCH, Direction.NORTH)
                .where('N', GTMachines.MUFFLER_HATCH[GTValues.IV], Direction.UP)
                .where('O', GTMachines.ITEM_IMPORT_BUS[GTValues.IV], Direction.NORTH)
                .where('P', GTMachines.ITEM_EXPORT_BUS[GTValues.IV], Direction.NORTH)
                .where('Q', GTMachines.FLUID_IMPORT_HATCH[GTValues.IV], Direction.NORTH)
                .where('R', GTMachines.FLUID_EXPORT_HATCH[GTValues.IV], Direction.NORTH)
                .where('S', GTMachines.ENERGY_INPUT_HATCH[GTValues.IV], Direction.NORTH)
                .where('T', GTMachines.ENERGY_OUTPUT_HATCH[GTValues.IV], Direction.NORTH)
                .build();
    }

    private static TraceabilityPredicate uniformTierPredicate(
                                                              String contextKey,
                                                              ToIntFunction<Block> tierLookup,
                                                              Block... candidates) {
        return Predicates.custom(
                state -> BigBroArrayTierRules.matchUniformTier(
                        state.getMatchContext(),
                        contextKey,
                        tierLookup.applyAsInt(state.getBlockState().getBlock())),
                () -> Arrays.stream(candidates).map(BlockInfo::fromBlock).toArray(BlockInfo[]::new));
    }

    private static Block frame(Material material) {
        BlockEntry<? extends Block> entry = GTMaterialBlocks.MATERIAL_BLOCKS.get(TagPrefix.frameGt, material);
        if (entry == null) {
            throw new IllegalStateException("Missing GTCEu frame block for " + material.getName());
        }
        return entry.get();
    }
}
