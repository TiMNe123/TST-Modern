package com.tstmodern.registry.machine;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;

import java.util.Set;

import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.tstmodern.TSTModern;
import com.tstmodern.machine.StarcoreMinerMachine;
import com.tstmodern.registry.TSTBlocks;
import com.tstmodern.registry.TSTRecipeTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

public final class StarcoreMinerDefinition {
    public static boolean isAllowedAbility(PartAbility ability) {
        return StarcoreMinerMachine.isAllowedAbility(ability);
    }

    public static final MultiblockMachineDefinition MACHINE = TSTModern.REGISTRATE
            .multiblock("starcore_miner", StarcoreMinerMachine::new)
            .langValue("Starcore Miner")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(TSTRecipeTypes.STARCORE_MINING)
            .recipeModifier((machine, recipe) -> ModifierFunction.IDENTITY)
            .appearanceBlock(TSTBlocks.SPACE_ELEVATOR_BASE_CASING)
            .partAppearance(StarcoreMinerDefinition::partAppearance)
            .pattern(definition -> {
                TraceabilityPredicate exportItems = Predicates.abilities(PartAbility.EXPORT_ITEMS)
                        .setMinGlobalLimited(1).setPreviewCount(1);
                TraceabilityPredicate energyInput = Predicates.abilities(
                        PartAbility.INPUT_ENERGY, PartAbility.SUBSTATION_INPUT_ENERGY, PartAbility.INPUT_LASER)
                        .setMinGlobalLimited(1).setPreviewCount(1);
                TraceabilityPredicate otherAbilities = Predicates.abilities(
                        PartAbility.IMPORT_ITEMS, PartAbility.IMPORT_FLUIDS);

                FactoryBlockPattern pattern = FactoryBlockPattern.start(
                        RelativeDirection.RIGHT, RelativeDirection.DOWN, RelativeDirection.BACK);
                for (String[] aisle : StarcoreMinerStructure.MAIN_AISLES) {
                    pattern.aisle(aisle);
                }
                return pattern
                        .where('~', Predicates.controller(blocks(definition.get())))
                        .where('A', blocks(GTBlocks.CASING_TEMPERED_GLASS.get())
                                .or(blocks(GTBlocks.FUSION_GLASS.get())))
                        .where('B', blocks(GTBlocks.CASING_INVAR_HEATPROOF.get()))
                        .where('C', blocks(TSTBlocks.DIMENSIONAL_BRIDGE_CASING.get()))
                        .where('D', blocks(GTBlocks.CASING_TUNGSTENSTEEL_PIPE.get()))
                        .where('E', blocks(TSTBlocks.ADVANCED_IRIDIUM_CASING.get()))
                        .where('F', blocks(TSTBlocks.RADIANT_NAQUADAH_ALLOY_CASING.get()))
                        .where('G', blocks(TSTBlocks.SPACE_ELEVATOR_BASE_CASING.get()))
                        .where('H', blocks(TSTBlocks.SPACE_ELEVATOR_SUPPORT_STRUCTURE.get()))
                        .where('I', blocks(TSTBlocks.HOLLOW_CASING.get()))
                        .where('J', blocks(TSTBlocks.DYSON_SWARM_FLOOR.get()))
                        .where('K', blocks(GTBlocks.FUSION_GLASS.get())
                                .or(blocks(TSTBlocks.RADIANT_NAQUADAH_ALLOY_CASING.get()))
                                .or(Predicates.frames(GTMaterials.NaquadahAlloy)))
                        .where('L', blocks(TSTBlocks.SPACE_ELEVATOR_BASE_CASING.get())
                                .or(exportItems)
                                .or(energyInput)
                                .or(otherAbilities))
                        .where(' ', Predicates.any())
                        .build();
            })
            .workableCasingModel(
                    TSTModern.id("block/casings/space_elevator_base_casing"),
                    TSTModern.id("block/multiblock/starcore_miner"))
            .tooltips(
                    Component.translatable("tstmodern.machine.starcore_miner.tooltip.0"),
                    Component.translatable("tstmodern.machine.starcore_miner.tooltip.1"),
                    Component.translatable("tstmodern.machine.starcore_miner.tooltip.2"),
                    Component.translatable("tstmodern.machine.starcore_miner.tooltip.3"),
                    Component.translatable("tstmodern.machine.starcore_miner.tooltip.4"),
                    Component.translatable("tstmodern.machine.starcore_miner.tooltip.5"),
                    Component.translatable("tstmodern.machine.starcore_miner.tooltip.6"),
                    Component.translatable("tstmodern.machine.starcore_miner.tooltip.7"),
                    Component.translatable("tstmodern.machine.starcore_miner.tooltip.8"))
            .register();

    private StarcoreMinerDefinition() {}

    public static BlockState partAppearance(IMultiController controller, IMultiPart part, Direction ignoredSide) {
        return TSTBlocks.SPACE_ELEVATOR_BASE_CASING.get().defaultBlockState();
    }
}
