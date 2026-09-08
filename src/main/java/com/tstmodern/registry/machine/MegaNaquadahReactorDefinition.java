package com.tstmodern.registry.machine;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;

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
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.config.ConfigHolder;
import com.tstmodern.TSTModern;
import com.tstmodern.machine.MegaNaquadahReactorMachine;
import com.tstmodern.registry.TSTBlocks;
import com.tstmodern.registry.TSTRecipeTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

public final class MegaNaquadahReactorDefinition {
    public static final MultiblockMachineDefinition MACHINE = TSTModern.REGISTRATE
            .multiblock("mega_naquadah_reactor", MegaNaquadahReactorMachine::new)
            .langValue("Mega Naquadah Reactor")
            .tier(GTValues.UIV)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(TSTRecipeTypes.MEGA_NAQUADAH_REACTOR_FUELS)
            .generator(true)
            .noRecipeModifier()
            .appearanceBlock(TSTBlocks.MINING_BLACK_PLUTONIUM_CASING)
            .partAppearance(MegaNaquadahReactorDefinition::partAppearance)
            .pattern(definition -> {
                TraceabilityPredicate energyOutputs = Predicates.abilities(
                        PartAbility.OUTPUT_ENERGY,
                        PartAbility.SUBSTATION_OUTPUT_ENERGY,
                        PartAbility.OUTPUT_LASER).setMinGlobalLimited(1).setPreviewCount(1);
                TraceabilityPredicate fluidInputs = Predicates.abilities(PartAbility.IMPORT_FLUIDS)
                        .setMinGlobalLimited(1).setPreviewCount(1);
                TraceabilityPredicate fluidOutputs = Predicates.abilities(PartAbility.EXPORT_FLUIDS)
                        .setMinGlobalLimited(1).setPreviewCount(1);
                TraceabilityPredicate casingOrAbilities = blocks(TSTBlocks.MINING_BLACK_PLUTONIUM_CASING.get())
                        .or(energyOutputs).or(fluidInputs).or(fluidOutputs);
                if (ConfigHolder.INSTANCE.machines.enableMaintenance) {
                    casingOrAbilities = casingOrAbilities.or(Predicates.abilities(PartAbility.MAINTENANCE)
                            .setExactLimit(1).setPreviewCount(1));
                }

                FactoryBlockPattern pattern = FactoryBlockPattern.start(
                        RelativeDirection.RIGHT, RelativeDirection.DOWN, RelativeDirection.BACK);
                for (String[] aisle : MegaNaquadahReactorStructure.PATTERN_AISLES) pattern.aisle(aisle);
                return pattern
                        .where('~', Predicates.controller(blocks(definition.get())))
                        .where('A', blocks(GTBlocks.CASING_TEMPERED_GLASS.get())
                                .or(blocks(GTBlocks.FUSION_GLASS.get())))
                        .where('B', blocks(TSTBlocks.FIELD_RESTRICTION_CASING.get()))
                        .where('C', blocks(TSTBlocks.DIMENSIONAL_BRIDGE_CASING.get()))
                        .where('D', casingOrAbilities)
                        .where('E', blocks(TSTBlocks.PARTICLE_BEAM_GUIDANCE_PIPE_CASING.get()))
                        .where('F', blocks(TSTBlocks.IRIDIUM_REINFORCED_NEUTRONIUM_CASING.get()))
                        .where(' ', Predicates.any())
                        .build();
            })
            .sidedWorkableCasingModel(
                    TSTModern.id("block/casings/mining_black_plutonium_casing"),
                    TSTModern.id("block/multiblock/mega_naquadah_reactor"))
            .tooltips(
                    Component.translatable("tstmodern.machine.mega_naquadah_reactor.tooltip.0"),
                    Component.translatable("tstmodern.machine.mega_naquadah_reactor.tooltip.1"),
                    Component.translatable("tstmodern.machine.mega_naquadah_reactor.tooltip.2"),
                    Component.translatable("tstmodern.machine.mega_naquadah_reactor.tooltip.3"),
                    Component.translatable("tstmodern.machine.mega_naquadah_reactor.tooltip.4"),
                    Component.translatable("tstmodern.machine.mega_naquadah_reactor.tooltip.5"),
                    Component.translatable("tstmodern.machine.mega_naquadah_reactor.tooltip.6"),
                    Component.translatable("tstmodern.machine.mega_naquadah_reactor.tooltip.7"))
            .register();

    private MegaNaquadahReactorDefinition() {}

    public static BlockState partAppearance(IMultiController controller, IMultiPart part, Direction ignoredSide) {
        var machine = controller.self();
        BlockPos delta = part.self().getPos().subtract(machine.getPos());
        Direction right = RelativeDirection.RIGHT.getRelative(
                machine.getFrontFacing(), machine.getUpwardsFacing(), machine.isFlipped());
        Direction down = RelativeDirection.DOWN.getRelative(
                machine.getFrontFacing(), machine.getUpwardsFacing(), machine.isFlipped());
        Direction back = RelativeDirection.BACK.getRelative(
                machine.getFrontFacing(), machine.getUpwardsFacing(), machine.isFlipped());
        int patternRight = MegaNaquadahReactorStructure.CONTROLLER_RIGHT + project(delta, right);
        int patternDown = MegaNaquadahReactorStructure.CONTROLLER_DOWN + project(delta, down);
        int patternBack = MegaNaquadahReactorStructure.CONTROLLER_BACK + project(delta, back);
        if (patternRight < 0 || patternRight >= MegaNaquadahReactorStructure.WIDTH || patternDown < 0 ||
                patternDown >= MegaNaquadahReactorStructure.HEIGHT || patternBack < 0 ||
                patternBack >= MegaNaquadahReactorStructure.DEPTH) {
            return TSTBlocks.MINING_BLACK_PLUTONIUM_CASING.get().defaultBlockState();
        }
        return switch (MegaNaquadahReactorStructure.symbolAt(patternRight, patternDown, patternBack)) {
            case 'A' -> GTBlocks.CASING_TEMPERED_GLASS.get().defaultBlockState();
            case 'B' -> TSTBlocks.FIELD_RESTRICTION_CASING.get().defaultBlockState();
            case 'C' -> TSTBlocks.DIMENSIONAL_BRIDGE_CASING.get().defaultBlockState();
            case 'E' -> TSTBlocks.PARTICLE_BEAM_GUIDANCE_PIPE_CASING.get().defaultBlockState();
            case 'F' -> TSTBlocks.IRIDIUM_REINFORCED_NEUTRONIUM_CASING.get().defaultBlockState();
            default -> TSTBlocks.MINING_BLACK_PLUTONIUM_CASING.get().defaultBlockState();
        };
    }

    private static int project(BlockPos delta, Direction direction) {
        return delta.getX() * direction.getStepX() + delta.getY() * direction.getStepY() +
                delta.getZ() * direction.getStepZ();
    }
}
