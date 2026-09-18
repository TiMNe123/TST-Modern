package com.tstmodern.registry.machine;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.tstmodern.TSTModern;
import com.tstmodern.machine.DraconicCrucibleMachine;
import com.tstmodern.registry.TSTBlocks;
import com.tstmodern.registry.TSTRecipeTypes;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

public final class DraconicCrucibleDefinition {
    public static final MultiblockMachineDefinition MACHINE = TSTModern.REGISTRATE
            .multiblock("draconic_crucible", DraconicCrucibleMachine::new)
            .langValue("Draconic Crucible")
            .tier(GTValues.UHV)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(TSTRecipeTypes.DRACONIC_CRUCIBLE)
            .recipeModifier(DraconicCrucibleMachine::recipeModifier)
            .appearanceBlock(TSTBlocks.RADIANT_NAQUADAH_ALLOY_CASING)
            .partAppearance(DraconicCrucibleDefinition::partAppearance)
            .pattern(definition -> {
                TraceabilityPredicate flexibleParts = Predicates.abilities(
                                PartAbility.IMPORT_ITEMS,
                                PartAbility.EXPORT_ITEMS,
                                PartAbility.INPUT_ENERGY,
                                PartAbility.SUBSTATION_INPUT_ENERGY,
                                PartAbility.INPUT_LASER)
                        .setMaxGlobalLimited(10).setPreviewCount(3);

                FactoryBlockPattern pattern = FactoryBlockPattern.start(
                        RelativeDirection.RIGHT, RelativeDirection.DOWN, RelativeDirection.BACK);
                for (String[] aisle : DraconicCrucibleStructure.PATTERN_AISLES) pattern.aisle(aisle);
                return pattern
                        .where('~', Predicates.controller(blocks(definition.get())))
                        .where('D', blocks(TSTBlocks.EXTREME_DENSITY_CASING.get()))
                        .where('R', blocks(TSTBlocks.RADIANT_NAQUADAH_ALLOY_CASING.get())
                                .or(flexibleParts))
                        .where('M', blocks(TSTBlocks.MECHANICALLY_ENHANCED_OBSIDIAN.get()))
                        .where('S', blocks(TSTBlocks.STABALOY_FIREBOX_CASING.get()))
                        .where('T', blocks(TSTBlocks.FIELD_RESTRICTION_COIL_T1.get()))
                        .where('F', blocks(TSTBlocks.FIELD_RESTRICTION_CASING.get()))
                        .where('C', blocks(TSTBlocks.COMPACT_FUSION_COIL_T3.get()))
                        .where('B', blocks(TSTBlocks.PARTICLE_BEAM_GUIDANCE_PIPE_CASING.get()))
                        .where('G', blocks(TSTBlocks.FIELD_RESTRICTION_GLASS.get()))
                        .where('P', blocks(TSTBlocks.NEUTRONIUM_PIPE_CASING.get()))
                        .where('K', blocks(TSTBlocks.DRACONIC_CRUCIBLE_CORE.get()))
                        .where('#', Predicates.air())
                        .where(' ', Predicates.any())
                        .build();
            })
            .workableCasingModel(
                    TSTModern.id("block/casings/radiant_naquadah_alloy_casing"),
                    GTCEu.id("block/multiblock/fusion_reactor"))
            .tooltips(
                    Component.translatable("tstmodern.machine.draconic_crucible.tooltip.0"),
                    Component.translatable("tstmodern.machine.draconic_crucible.tooltip.1"),
                    Component.translatable("tstmodern.machine.draconic_crucible.tooltip.2"),
                    Component.translatable("tstmodern.machine.draconic_crucible.tooltip.3"),
                    Component.translatable("tstmodern.machine.draconic_crucible.tooltip.4"),
                    Component.translatable("tstmodern.machine.draconic_crucible.tooltip.5"),
                    Component.translatable("tstmodern.machine.draconic_crucible.tooltip.6"),
                    Component.translatable("tstmodern.machine.draconic_crucible.tooltip.7"))
            .register();

    private DraconicCrucibleDefinition() {}

    private static BlockState partAppearance(IMultiController ignoredController, IMultiPart ignoredPart,
                                             Direction ignoredSide) {
        return TSTBlocks.RADIANT_NAQUADAH_ALLOY_CASING.get().defaultBlockState();
    }
}
