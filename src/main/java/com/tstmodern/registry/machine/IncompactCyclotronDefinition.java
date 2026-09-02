package com.tstmodern.registry.machine;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;

import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.tstmodern.TSTModern;
import com.tstmodern.machine.IncompactCyclotronMachine;
import com.tstmodern.registry.TSTBlocks;
import com.tstmodern.registry.TSTRecipeTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

public final class IncompactCyclotronDefinition {

        public static final MultiblockMachineDefinition MACHINE = TSTModern.REGISTRATE
                        .multiblock("incompact_cyclotron", IncompactCyclotronMachine::new)
                        .langValue("PULSAR - Incompact Cyclotron")
                        .rotationState(RotationState.NON_Y_AXIS)
                        .recipeType(TSTRecipeTypes.CYCLOTRON_RECIPES)
                        .recipeModifiers(IncompactCyclotronMachine::recipeModifier,
                                        GTRecipeModifiers.OC_NON_PERFECT)
                        .appearanceBlock(TSTBlocks.DENSE_CYCLOTRON_OUTER_CASING)
                        .partAppearance(IncompactCyclotronDefinition::partAppearance)
                        .pattern(definition -> {
                                FactoryBlockPattern pattern = FactoryBlockPattern.start(
                                                RelativeDirection.RIGHT,
                                                RelativeDirection.DOWN,
                                                RelativeDirection.BACK);
                                for (String[] aisle : IncompactCyclotronStructure.PATTERN_AISLES) {
                                        pattern.aisle(aisle);
                                }
                                return pattern
                                                .where('~', Predicates.controller(blocks(definition.get())))
                                                .where('A', blocks(GTBlocks.FUSION_GLASS.get()))
                                                .where('B', blocks(TSTBlocks.QUANTUM_FRAME.get()))
                                                .where('C', blocks(TSTBlocks.COMPACT_CYCLOTRON_COIL.get()))
                                                .where('D', blocks(TSTBlocks.DENSE_CYCLOTRON_OUTER_CASING.get()))
                                                .where('E', blocks(GTBlocks.FUSION_GLASS.get())
                                                                .or(Predicates.autoAbilities(
                                                                                definition.getRecipeTypes())))
                                                .where('F', blocks(TSTBlocks.DENSE_CYCLOTRON_OUTER_CASING.get())
                                                                .or(Predicates.abilities(
                                                                                PartAbility.INPUT_ENERGY,
                                                                                PartAbility.SUBSTATION_INPUT_ENERGY,
                                                                                PartAbility.INPUT_LASER)))
                                                .where(' ', Predicates.any())
                                                .build();
                        })
                        .workableCasingModel(
                                        TSTModern.id("block/casings/dense_cyclotron_outer_casing"),
                                        TSTModern.id("block/multiblock/incompact_cyclotron"))
                        .tooltips(
                                        Component.translatable("tstmodern.machine.incompact_cyclotron.tooltip.0"),
                                        Component.translatable("tstmodern.machine.incompact_cyclotron.tooltip.1"),
                                        Component.translatable("tstmodern.machine.incompact_cyclotron.tooltip.2"),
                                        Component.translatable("tstmodern.machine.incompact_cyclotron.tooltip.3"),
                                        Component.translatable("tstmodern.machine.incompact_cyclotron.tooltip.4"),
                                        Component.translatable("tstmodern.machine.incompact_cyclotron.tooltip.5"),
                                        Component.translatable("tstmodern.machine.incompact_cyclotron.tooltip.6"),
                                        Component.translatable("tstmodern.machine.incompact_cyclotron.tooltip.7"),
                                        Component.translatable("tstmodern.machine.incompact_cyclotron.tooltip.8"))
                        .register();

        private IncompactCyclotronDefinition() {
        }

        public static BlockState partAppearance(
                        IMultiController controller,
                        IMultiPart part,
                        Direction ignoredSide) {
                var machine = controller.self();
                BlockPos delta = part.self().getPos().subtract(machine.getPos());
                Direction right = RelativeDirection.RIGHT.getRelative(
                                machine.getFrontFacing(), machine.getUpwardsFacing(), machine.isFlipped());
                Direction down = RelativeDirection.DOWN.getRelative(
                                machine.getFrontFacing(), machine.getUpwardsFacing(), machine.isFlipped());
                Direction back = RelativeDirection.BACK.getRelative(
                                machine.getFrontFacing(), machine.getUpwardsFacing(), machine.isFlipped());
                int patternRight = IncompactCyclotronStructure.CONTROLLER_RIGHT + project(delta, right);
                int patternDown = IncompactCyclotronStructure.CONTROLLER_DOWN + project(delta, down);
                int patternBack = IncompactCyclotronStructure.CONTROLLER_BACK + project(delta, back);
                if (patternRight >= 0 && patternRight < IncompactCyclotronStructure.WIDTH &&
                                patternDown >= 0 && patternDown < IncompactCyclotronStructure.HEIGHT &&
                                patternBack >= 0 && patternBack < IncompactCyclotronStructure.DEPTH &&
                                IncompactCyclotronStructure.symbolAt(patternRight, patternDown, patternBack) == 'E') {
                        return GTBlocks.FUSION_GLASS.get().defaultBlockState();
                }
                return TSTBlocks.DENSE_CYCLOTRON_OUTER_CASING.get().defaultBlockState();
        }

        private static int project(BlockPos delta, Direction direction) {
                return delta.getX() * direction.getStepX() +
                                delta.getY() * direction.getStepY() +
                                delta.getZ() * direction.getStepZ();
        }
}
