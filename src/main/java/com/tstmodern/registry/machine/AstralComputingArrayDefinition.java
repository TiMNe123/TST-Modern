package com.tstmodern.registry.machine;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.tstmodern.TSTModern;
import com.tstmodern.machine.AstralComputationRackMachine;
import com.tstmodern.machine.AstralComputingArrayMachine;
import com.tstmodern.registry.TSTBlocks;

import net.minecraft.network.chat.Component;

public final class AstralComputingArrayDefinition {
    public static final PartAbility ASTRAL_RACK = new PartAbility("astral_computation_rack");

    public static final MachineDefinition ASTRAL_COMPUTATION_RACK = TSTModern.REGISTRATE
            .machine("astral_computation_rack", AstralComputationRackMachine::new)
            .langValue("Astral Computation Rack")
            .tier(GTValues.UEV)
            .rotationState(RotationState.ALL)
            .abilities(ASTRAL_RACK)
            .tooltips(Component.translatable("tstmodern.machine.astral_computation_rack.tooltip"))
            .overlayTieredHullModel("data_access_hatch")
            .register();

    public static final MultiblockMachineDefinition MACHINE = TSTModern.REGISTRATE
            .multiblock("astral_computing_array", AstralComputingArrayMachine::new)
            .langValue("Astral Computing Array")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTRecipeTypes.DUMMY_RECIPES)
            .appearanceBlock(TSTBlocks.SPACE_ELEVATOR_INTERNAL_STRUCTURE)
            .partAppearance((controller, part, side) ->
                    TSTBlocks.SPACE_ELEVATOR_INTERNAL_STRUCTURE.get().defaultBlockState())
            .pattern(definition -> {
                FactoryBlockPattern pattern = FactoryBlockPattern.start(
                        RelativeDirection.RIGHT, RelativeDirection.DOWN, RelativeDirection.BACK);
                for (String[] aisle : AstralComputingArrayStructure.PATTERN_AISLES) pattern.aisle(aisle);
                var rack = Predicates.abilities(ASTRAL_RACK).setExactLimit(1).setPreviewCount(1);
                var transmitter = Predicates.abilities(PartAbility.COMPUTATION_DATA_TRANSMISSION)
                        .setExactLimit(1).setPreviewCount(1);
                var fluid = Predicates.abilities(PartAbility.IMPORT_FLUIDS)
                        .setMinGlobalLimited(1).setPreviewCount(1);
                var energy = Predicates.abilities(PartAbility.INPUT_ENERGY,
                        PartAbility.SUBSTATION_INPUT_ENERGY, PartAbility.INPUT_LASER)
                        .setMinGlobalLimited(1).setPreviewCount(1);
                return pattern
                        .where('~', Predicates.controller(blocks(definition.get())))
                        .where('A', blocks(TSTBlocks.FIELD_RESTRICTION_COIL_T1.get()))
                        .where('B', blocks(TSTBlocks.COMPACT_FUSION_COIL_T0.get()))
                        .where('C', blocks(TSTBlocks.SPACE_ELEVATOR_BASE_CASING.get()))
                        .where('D', blocks(TSTBlocks.SPACE_ELEVATOR_SUPPORT_STRUCTURE.get()))
                        .where('E', blocks(TSTBlocks.SPACE_ELEVATOR_INTERNAL_STRUCTURE.get())
                                .or(rack).or(transmitter).or(fluid).or(energy))
                        .where('F', blocks(TSTBlocks.HIGH_POWER_CASING.get()))
                        .where('G', blocks(TSTBlocks.COMPUTER_CASING.get()))
                        .where('H', blocks(TSTBlocks.COMPUTER_HEAT_VENT.get()))
                        .where('I', blocks(TSTBlocks.ADVANCED_COMPUTER_CASING.get()))
                        .where('J', blocks(TSTBlocks.ELECTROMAGNETIC_COMPUTER_COIL.get()))
                        .where('K', blocks(TSTBlocks.CONTAINMENT_CASING.get()))
                        .where('L', blocks(TSTBlocks.RADIATION_PROTECTION_STEEL_FRAME.get()))
                        .where('M', blocks(GTBlocks.FUSION_GLASS.get()))
                        .where('O', blocks(TSTBlocks.ASTRAL_PYLON_CASING.get()))
                        .where('P', blocks(TSTBlocks.COMPUTER_HEAT_VENT.get()))
                        .where('Q', blocks(TSTBlocks.ADVANCED_COMPUTER_CASING.get()))
                        .where(' ', Predicates.any())
                        .build();
            })
            .workableCasingModel(
                    TSTModern.id("block/casings/space_elevator_internal_structure"),
                    TSTModern.id("block/multiblock/astral_computing_array"))
            .tooltips(
                    Component.translatable("tstmodern.machine.astral_computing_array.tooltip.0"),
                    Component.translatable("tstmodern.machine.astral_computing_array.tooltip.1"),
                    Component.translatable("tstmodern.machine.astral_computing_array.tooltip.2"),
                    Component.translatable("tstmodern.machine.astral_computing_array.tooltip.3"),
                    Component.translatable("tstmodern.machine.astral_computing_array.tooltip.4"),
                    Component.translatable("tstmodern.machine.astral_computing_array.tooltip.5"),
                    Component.translatable("tstmodern.machine.astral_computing_array.tooltip.6"))
            .register();

    private AstralComputingArrayDefinition() {}
}
