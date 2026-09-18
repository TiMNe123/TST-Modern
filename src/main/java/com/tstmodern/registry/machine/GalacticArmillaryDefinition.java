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
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.tstmodern.TSTModern;
import com.tstmodern.machine.GalacticArmillaryMachine;
import com.tstmodern.registry.TSTBlocks;
import com.tstmodern.registry.TSTRecipeTypes;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public final class GalacticArmillaryDefinition {
    public static final MultiblockMachineDefinition MACHINE = TSTModern.REGISTRATE
            .multiblock("galactic_armillary", GalacticArmillaryMachine::new)
            .langValue("Galactic Armillary")
            .tier(GTValues.UEV)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(TSTRecipeTypes.GALACTIC_ARMILLARY)
            .recipeModifier((machine, recipe) -> ModifierFunction.IDENTITY)
            .appearanceBlock(TSTBlocks.EXTREME_DENSITY_CASING)
            .partAppearance(GalacticArmillaryDefinition::partAppearance)
            .pattern(definition -> {
                TraceabilityPredicate itemInput = Predicates.abilities(PartAbility.IMPORT_ITEMS)
                        .setMinGlobalLimited(1).setMaxGlobalLimited(1).setPreviewCount(1);
                TraceabilityPredicate itemOutput = Predicates.abilities(PartAbility.EXPORT_ITEMS)
                        .setMinGlobalLimited(1).setMaxGlobalLimited(1).setPreviewCount(1);
                TraceabilityPredicate energyInput = Predicates.ability(PartAbility.INPUT_ENERGY,
                                GTValues.UEV, GTValues.UIV, GTValues.UXV, GTValues.OpV, GTValues.MAX)
                        .setMinGlobalLimited(1).setMaxGlobalLimited(16).setPreviewCount(1);
                FactoryBlockPattern pattern = FactoryBlockPattern.start(
                        RelativeDirection.RIGHT, RelativeDirection.DOWN, RelativeDirection.BACK);
                for (String[] aisle : GalacticArmillaryStructure.PATTERN_AISLES) pattern.aisle(aisle);
                return pattern
                        .where('~', Predicates.controller(blocks(definition.get())))
                        .where('D', blocks(TSTBlocks.EXTREME_DENSITY_CASING.get()))
                        .where('H', blocks(TSTBlocks.HIGH_POWER_CASING.get())
                                .or(itemInput)
                                .or(itemOutput))
                        .where('E', blocks(TSTBlocks.ASTRAL_PYLON_CASING.get()).or(energyInput))
                        .where('I', blocks(TSTBlocks.HIGH_POWER_CASING.get()))
                        .where('O', blocks(TSTBlocks.HIGH_POWER_CASING.get()))
                        .where('A', blocks(TSTBlocks.ASTRAL_PYLON_CASING.get()))
                        .where(' ', Predicates.any())
                        .build();
            })
            .workableCasingModel(
                    GTCEu.id("block/casings/gcym/high_temperature_smelting_casing"),
                    GTCEu.id("block/multiblock/fusion_reactor"))
            .tooltips(
                    Component.translatable("tstmodern.machine.galactic_armillary.tooltip.0"),
                    Component.translatable("tstmodern.machine.galactic_armillary.tooltip.1"),
                    Component.translatable("tstmodern.machine.galactic_armillary.tooltip.2"),
                    Component.translatable("tstmodern.machine.galactic_armillary.tooltip.3"),
                    Component.translatable("tstmodern.machine.galactic_armillary.tooltip.4"),
                    Component.translatable("tstmodern.machine.galactic_armillary.tooltip.5"))
            .register();

    private GalacticArmillaryDefinition() {}

    public static BlockState partAppearance(IMultiController controller, IMultiPart part, Direction ignoredSide) {
        var machine = controller.self();
        BlockPos delta = part.self().getPos().subtract(machine.getPos());
        Direction right = RelativeDirection.RIGHT.getRelative(
                machine.getFrontFacing(), machine.getUpwardsFacing(), machine.isFlipped());
        Direction down = RelativeDirection.DOWN.getRelative(
                machine.getFrontFacing(), machine.getUpwardsFacing(), machine.isFlipped());
        Direction back = RelativeDirection.BACK.getRelative(
                machine.getFrontFacing(), machine.getUpwardsFacing(), machine.isFlipped());
        int x = GalacticArmillaryStructure.CONTROLLER_X + project(delta, right);
        int y = GalacticArmillaryStructure.CONTROLLER_Y - project(delta, down);
        int z = GalacticArmillaryStructure.CONTROLLER_Z + project(delta, back);
        if (x >= 0 && x < GalacticArmillaryStructure.WIDTH && y >= 0 && y < GalacticArmillaryStructure.HEIGHT &&
                z >= 0 && z < GalacticArmillaryStructure.DEPTH &&
                GalacticArmillaryStructure.symbolAt(x, y, z) == 'E') {
            return TSTBlocks.ASTRAL_PYLON_CASING.get().defaultBlockState();
        }
        return TSTBlocks.HIGH_POWER_CASING.get().defaultBlockState();
    }

    private static int project(BlockPos delta, Direction direction) {
        return delta.getX() * direction.getStepX() + delta.getY() * direction.getStepY() +
                delta.getZ() * direction.getStepZ();
    }
}
