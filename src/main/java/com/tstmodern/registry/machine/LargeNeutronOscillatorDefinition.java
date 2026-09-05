package com.tstmodern.registry.machine;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;

import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.tstmodern.TSTModern;
import com.tstmodern.registry.TSTBlocks;
import com.tstmodern.registry.TSTRecipeTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

public final class LargeNeutronOscillatorDefinition {

    public static final MultiblockMachineDefinition LARGE_NEUTRON_OSCILLATOR = TSTModern.REGISTRATE
            .multiblock("large_neutron_oscillator", WorkableElectricMultiblockMachine::new)
            .langValue("Large Neutron Oscillator")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(TSTRecipeTypes.NEUTRON_ACTIVATOR)
            .recipeModifiers(GTRecipeModifiers.PARALLEL_HATCH,
                    GTRecipeModifiers.ELECTRIC_OVERCLOCK.apply(com.gregtechceu.gtceu.api.recipe.OverclockingLogic.NON_PERFECT_OVERCLOCK))
            .appearanceBlock(GTBlocks.CASING_STAINLESS_CLEAN)
            .partAppearance(LargeNeutronOscillatorDefinition::partAppearance)
            .pattern(definition -> {
                TraceabilityPredicate parallelHatch = Predicates.abilities(PartAbility.PARALLEL_HATCH)
                        .setExactLimit(1)
                        .setPreviewCount(1);
                TraceabilityPredicate energyInputs = Predicates.abilities(
                        PartAbility.INPUT_ENERGY,
                        PartAbility.SUBSTATION_INPUT_ENERGY,
                        PartAbility.INPUT_LASER)
                        .setMinGlobalLimited(1)
                        .setMaxGlobalLimited(2)
                        .setPreviewCount(1);
                TraceabilityPredicate recipeIo = Predicates.abilities(PartAbility.IMPORT_ITEMS)
                        .setMinGlobalLimited(1)
                        .setPreviewCount(1)
                        .or(Predicates.abilities(PartAbility.EXPORT_ITEMS).setMinGlobalLimited(1).setPreviewCount(1))
                        .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setMinGlobalLimited(1).setPreviewCount(1))
                        .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS).setMinGlobalLimited(1).setPreviewCount(1));
                FactoryBlockPattern pattern = FactoryBlockPattern.start(
                        RelativeDirection.RIGHT,
                        RelativeDirection.DOWN,
                        RelativeDirection.BACK);
                for (String[] aisle : LargeNeutronOscillatorStructure.PATTERN_AISLES) {
                    pattern.aisle(aisle);
                }
                return pattern
                        .where('~', Predicates.controller(blocks(definition.get())))
                        // A: Modular Hatch / Parallel Hatch or Stainless Clean Casing (max 1 parallel hatch)
                        .where('A', blocks(GTBlocks.CASING_STAINLESS_CLEAN.get())
                                .or(parallelHatch))
                        // B: Energy Hatch or Advanced Iridium Casing (1-2 energy inputs globally)
                        .where('B', blocks(TSTBlocks.ADVANCED_IRIDIUM_CASING.get())
                                .or(energyInputs))
                        // C: Input/Output Bus & Hatch or Radiant Naquadah Alloy Casing (at least 1 each)
                        .where('C', blocks(TSTBlocks.RADIANT_NAQUADAH_ALLOY_CASING.get())
                                .or(recipeIo))
                        // D: High Power Casing (sBlockCasingsTT:0)
                        .where('D', blocks(TSTBlocks.HIGH_POWER_CASING.get()))
                        // E: Speeding Pipe Casing (Loaders.speedingPipe:0)
                        .where('E', blocks(TSTBlocks.SPEEDING_PIPE_CASING.get()))
                        // F: Glasses
                        .where('F', blocks(GTBlocks.CASING_TEMPERED_GLASS.get())
                                .or(blocks(GTBlocks.FUSION_GLASS.get())))
                        // G: Naquadah Alloy Frame
                        .where('G', Predicates.frames(GTMaterials.NaquadahAlloy))
                        .where(' ', Predicates.any())
                        .build();
            })
            .workableCasingModel(
                    new net.minecraft.resources.ResourceLocation("gtceu", "block/casings/solid/machine_casing_clean_stainless_steel"),
                    TSTModern.id("block/multiblock/large_neutron_oscillator"))
            .tooltips(
                    Component.translatable("tstmodern.machine.large_neutron_oscillator.tooltip.0"),
                    Component.translatable("tstmodern.machine.large_neutron_oscillator.tooltip.1"),
                    Component.translatable("tstmodern.machine.large_neutron_oscillator.tooltip.2"),
                    Component.translatable("tstmodern.machine.large_neutron_oscillator.tooltip.3"),
                    Component.translatable("tstmodern.machine.large_neutron_oscillator.tooltip.4"),
                    Component.translatable("tstmodern.machine.large_neutron_oscillator.tooltip.5"),
                    Component.translatable("tstmodern.machine.large_neutron_oscillator.tooltip.6"),
                    Component.translatable("tstmodern.machine.large_neutron_oscillator.tooltip.7"),
                    Component.translatable("tstmodern.machine.large_neutron_oscillator.tooltip.8"),
                    Component.translatable("tstmodern.machine.large_neutron_oscillator.tooltip.9"),
                    Component.translatable("tstmodern.machine.large_neutron_oscillator.tooltip.10"))
            .register();

    private LargeNeutronOscillatorDefinition() {}

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
        int patternRight = LargeNeutronOscillatorStructure.CONTROLLER_RIGHT + project(delta, right);
        int patternDown = LargeNeutronOscillatorStructure.CONTROLLER_DOWN + project(delta, down);
        int patternBack = LargeNeutronOscillatorStructure.CONTROLLER_BACK + project(delta, back);
        if (patternRight >= 0 && patternRight < LargeNeutronOscillatorStructure.WIDTH &&
                patternDown >= 0 && patternDown < LargeNeutronOscillatorStructure.HEIGHT &&
                patternBack >= 0 && patternBack < LargeNeutronOscillatorStructure.DEPTH) {
            char symbol = LargeNeutronOscillatorStructure.symbolAt(patternRight, patternDown, patternBack);
            if (symbol == 'B') {
                return TSTBlocks.ADVANCED_IRIDIUM_CASING.get().defaultBlockState();
            }
            if (symbol == 'C') {
                return TSTBlocks.RADIANT_NAQUADAH_ALLOY_CASING.get().defaultBlockState();
            }
        }
        return GTBlocks.CASING_STAINLESS_CLEAN.get().defaultBlockState();
    }

    private static int project(BlockPos delta, Direction direction) {
        return delta.getX() * direction.getStepX() +
                delta.getY() * direction.getStepY() +
                delta.getZ() * direction.getStepZ();
    }
}
