package com.tstmodern.registry.machine;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;

import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.tstmodern.TSTModern;
import com.tstmodern.machine.OreProcessingFactoryMachine;
import com.tstmodern.registry.TSTBlocks;
import com.tstmodern.registry.TSTRecipeTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

public final class OreProcessingFactoryDefinition {
    public static final MultiblockMachineDefinition MACHINE = TSTModern.REGISTRATE
            .multiblock("ore_processing_factory", OreProcessingFactoryMachine::new)
            .langValue("Ore Processing Factory")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(TSTRecipeTypes.ORE_PROCESSING_FACTORY)
            .recipeModifier(OreProcessingFactoryMachine::maximumParallel)
            .appearanceBlock(TSTBlocks.ADVANCED_IRIDIUM_CASING)
            .partAppearance(OreProcessingFactoryDefinition::partAppearance)
            .pattern(definition -> {
                TraceabilityPredicate fluidInput = Predicates.abilities(PartAbility.IMPORT_FLUIDS)
                        .setMinGlobalLimited(1).setPreviewCount(1);
                TraceabilityPredicate energyInput = Predicates.abilities(
                        PartAbility.INPUT_ENERGY, PartAbility.SUBSTATION_INPUT_ENERGY, PartAbility.INPUT_LASER)
                        .setMinGlobalLimited(1).setPreviewCount(1);
                TraceabilityPredicate itemInput = Predicates.abilities(PartAbility.IMPORT_ITEMS)
                        .setMinGlobalLimited(1).setPreviewCount(1);
                TraceabilityPredicate itemOutput = Predicates.abilities(PartAbility.EXPORT_ITEMS)
                        .setMinGlobalLimited(1).setPreviewCount(1);
                FactoryBlockPattern pattern = FactoryBlockPattern.start(
                        RelativeDirection.RIGHT, RelativeDirection.DOWN, RelativeDirection.BACK);
                for (String[] aisle : OreProcessingFactoryStructure.PATTERN_AISLES) {
                    pattern.aisle(aisle);
                }
                return pattern
                        .where('~', Predicates.controller(blocks(definition.get())))
                        .where('A', blocks(GTBlocks.CASING_TEMPERED_GLASS.get())
                                .or(blocks(GTBlocks.FUSION_GLASS.get())))
                        .where('B', blocks(GTBlocks.CASING_TITANIUM_GEARBOX.get()))
                        .where('C', blocks(GTBlocks.COMPUTER_CASING.get()))
                        .where('D', blocks(GTBlocks.CASING_TUNGSTENSTEEL_PIPE.get()))
                        .where('E', blocks(GTBlocks.CASING_TUNGSTENSTEEL_ROBUST.get()))
                        .where('F', blocks(GTBlocks.CASING_STAINLESS_CLEAN.get()))
                        .where('G', blocks(TSTBlocks.ADVANCED_IRIDIUM_CASING.get()))
                        .where('H', blocks(TSTBlocks.HIGH_POWER_CASING.get()))
                        .where('I', blocks(TSTBlocks.ELECTROMAGNETIC_COMPUTER_COIL.get()))
                        .where('J', blocks(GTBlocks.CASING_TUNGSTENSTEEL_ROBUST.get()).or(fluidInput))
                        .where('K', blocks(TSTBlocks.HIGH_POWER_CASING.get()).or(energyInput))
                        .where('L', blocks(GTBlocks.CASING_TUNGSTENSTEEL_ROBUST.get())
                                .or(itemInput).or(itemOutput))
                        .where('M', Predicates.frames(GTMaterials.TungstenSteel))
                        .where(' ', Predicates.any())
                        .build();
            })
            .workableCasingModel(
                    TSTModern.id("block/casings/advanced_iridium_casing"),
                    TSTModern.id("block/multiblock/ore_processing_factory"))
            .tooltips(
                    Component.translatable("tstmodern.machine.ore_processing_factory.tooltip.0"),
                    Component.translatable("tstmodern.machine.ore_processing_factory.tooltip.1"),
                    Component.translatable("tstmodern.machine.ore_processing_factory.tooltip.2"),
                    Component.translatable("tstmodern.machine.ore_processing_factory.tooltip.3"),
                    Component.translatable("tstmodern.machine.ore_processing_factory.tooltip.4"),
                    Component.translatable("tstmodern.machine.ore_processing_factory.tooltip.5"),
                    Component.translatable("tstmodern.machine.ore_processing_factory.tooltip.6"),
                    Component.translatable("tstmodern.machine.ore_processing_factory.tooltip.7"))
            .register();

    private OreProcessingFactoryDefinition() {}

    public static BlockState partAppearance(IMultiController controller, IMultiPart part, Direction ignoredSide) {
        var machine = controller.self();
        BlockPos delta = part.self().getPos().subtract(machine.getPos());
        Direction right = RelativeDirection.RIGHT.getRelative(
                machine.getFrontFacing(), machine.getUpwardsFacing(), machine.isFlipped());
        Direction down = RelativeDirection.DOWN.getRelative(
                machine.getFrontFacing(), machine.getUpwardsFacing(), machine.isFlipped());
        Direction back = RelativeDirection.BACK.getRelative(
                machine.getFrontFacing(), machine.getUpwardsFacing(), machine.isFlipped());
        int patternRight = OreProcessingFactoryStructure.CONTROLLER_RIGHT + project(delta, right);
        int patternDown = OreProcessingFactoryStructure.CONTROLLER_DOWN + project(delta, down);
        int patternBack = OreProcessingFactoryStructure.CONTROLLER_BACK + project(delta, back);
        if (patternRight >= 0 && patternRight < OreProcessingFactoryStructure.WIDTH &&
                patternDown >= 0 && patternDown < OreProcessingFactoryStructure.HEIGHT &&
                patternBack >= 0 && patternBack < OreProcessingFactoryStructure.DEPTH) {
            return switch (OreProcessingFactoryStructure.symbolAt(patternRight, patternDown, patternBack)) {
                case 'K' -> TSTBlocks.HIGH_POWER_CASING.get().defaultBlockState();
                case 'J', 'L' -> GTBlocks.CASING_TUNGSTENSTEEL_ROBUST.get().defaultBlockState();
                default -> TSTBlocks.ADVANCED_IRIDIUM_CASING.get().defaultBlockState();
            };
        }
        return TSTBlocks.ADVANCED_IRIDIUM_CASING.get().defaultBlockState();
    }

    private static int project(BlockPos delta, Direction direction) {
        return delta.getX() * direction.getStepX() + delta.getY() * direction.getStepY() +
                delta.getZ() * direction.getStepZ();
    }
}
