package com.tstmodern.registry.machine;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;

import java.util.Arrays;

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
import com.gregtechceu.gtceu.config.ConfigHolder;
import com.tstmodern.TSTModern;
import com.tstmodern.machine.NaquadahFuelRefineryMachine;
import com.tstmodern.machine.logic.NaquadahFuelRefineryLogic;
import com.tstmodern.registry.TSTBlocks;
import com.tstmodern.registry.TSTRecipeTypes;

import com.lowdragmc.lowdraglib.utils.BlockInfo;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class NaquadahFuelRefineryDefinition {
    public static final MultiblockMachineDefinition MACHINE = TSTModern.REGISTRATE
            .multiblock("naquadah_fuel_refinery", NaquadahFuelRefineryMachine::new)
            .langValue("Naquadah Fuel Refinery")
            .tier(GTValues.UHV)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(TSTRecipeTypes.NAQUADAH_FUEL_REFINERY)
            .recipeModifiers(NaquadahFuelRefineryMachine::recipeModifier)
            .appearanceBlock(TSTBlocks.NAQUADAH_FUEL_REFINERY_CASING)
            .partAppearance(NaquadahFuelRefineryDefinition::partAppearance)
            .pattern(definition -> {
                TraceabilityPredicate casing = blocks(TSTBlocks.NAQUADAH_FUEL_REFINERY_CASING.get())
                        .setMinGlobalLimited(470);
                TraceabilityPredicate itemInput = Predicates.abilities(PartAbility.IMPORT_ITEMS)
                        .setMinGlobalLimited(1).setPreviewCount(1);
                TraceabilityPredicate fluidInput = Predicates.abilities(PartAbility.IMPORT_FLUIDS)
                        .setMinGlobalLimited(1).setPreviewCount(1);
                TraceabilityPredicate fluidOutput = Predicates.abilities(PartAbility.EXPORT_FLUIDS)
                        .setMinGlobalLimited(1).setPreviewCount(1);
                TraceabilityPredicate energyInput = Predicates.abilities(
                                PartAbility.INPUT_ENERGY,
                                PartAbility.SUBSTATION_INPUT_ENERGY,
                                PartAbility.INPUT_LASER)
                        .setMinGlobalLimited(1).setMaxGlobalLimited(2).setPreviewCount(1);
                TraceabilityPredicate casingOrAbilities = casing.or(itemInput).or(fluidInput).or(fluidOutput)
                        .or(energyInput);
                if (ConfigHolder.INSTANCE.machines.enableMaintenance) {
                    casingOrAbilities = casingOrAbilities.or(Predicates.abilities(PartAbility.MAINTENANCE)
                            .setExactLimit(1).setPreviewCount(1));
                }

                FactoryBlockPattern pattern = FactoryBlockPattern.start(
                        RelativeDirection.RIGHT, RelativeDirection.DOWN, RelativeDirection.BACK);
                for (String[] aisle : NaquadahFuelRefineryStructure.PATTERN_AISLES) pattern.aisle(aisle);
                return pattern
                        .where('~', Predicates.controller(blocks(definition.get())))
                        .where('A', casingOrAbilities)
                        .where('B', uniformCoilPredicate())
                        .where('C', blocks(TSTBlocks.FIELD_RESTRICTION_GLASS.get()))
                        .where('E', blocks(TSTBlocks.EUROPIUM_REINFORCED_RADIATION_PROOF_CASING.get()))
                        .where('F', blocks(TSTBlocks.RADIATION_PROTECTION_STEEL_FRAME.get()))
                        .where(' ', Predicates.air())
                        .build();
            })
            .workableCasingModel(
                    TSTModern.id("block/casings/naquadah_fuel_refinery_casing"),
                    GTCEu.id("block/multiblock/assembly_line"))
            .tooltips(
                    Component.translatable("tstmodern.machine.naquadah_fuel_refinery.tooltip.0"),
                    Component.translatable("tstmodern.machine.naquadah_fuel_refinery.tooltip.1"),
                    Component.translatable("tstmodern.machine.naquadah_fuel_refinery.tooltip.2"),
                    Component.translatable("tstmodern.machine.naquadah_fuel_refinery.tooltip.3"),
                    Component.translatable("tstmodern.machine.naquadah_fuel_refinery.tooltip.4"),
                    Component.translatable("tstmodern.machine.naquadah_fuel_refinery.tooltip.5"),
                    Component.translatable("tstmodern.machine.naquadah_fuel_refinery.tooltip.6"),
                    Component.translatable("tstmodern.machine.naquadah_fuel_refinery.tooltip.7"))
            .register();

    private NaquadahFuelRefineryDefinition() {}

    private static TraceabilityPredicate uniformCoilPredicate() {
        Block[] coils = {
                TSTBlocks.FIELD_RESTRICTION_COIL_T1.get(),
                TSTBlocks.FIELD_RESTRICTION_COIL_T2.get(),
                TSTBlocks.FIELD_RESTRICTION_COIL_T3.get(),
                TSTBlocks.FIELD_RESTRICTION_COIL_T4.get()
        };
        return Predicates.custom(
                state -> NaquadahFuelRefineryLogic.matchUniformCoil(
                        state.getMatchContext(), coilTier(state.getBlockState().getBlock())),
                () -> Arrays.stream(coils).map(BlockInfo::fromBlock).toArray(BlockInfo[]::new));
    }

    private static int coilTier(Block block) {
        if (block == TSTBlocks.FIELD_RESTRICTION_COIL_T1.get()) return 1;
        if (block == TSTBlocks.FIELD_RESTRICTION_COIL_T2.get()) return 2;
        if (block == TSTBlocks.FIELD_RESTRICTION_COIL_T3.get()) return 3;
        if (block == TSTBlocks.FIELD_RESTRICTION_COIL_T4.get()) return 4;
        return 0;
    }

    private static BlockState partAppearance(IMultiController ignoredController, IMultiPart ignoredPart,
                                             Direction ignoredSide) {
        return TSTBlocks.NAQUADAH_FUEL_REFINERY_CASING.get().defaultBlockState();
    }
}
