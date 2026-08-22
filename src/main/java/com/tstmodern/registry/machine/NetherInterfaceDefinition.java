package com.tstmodern.registry.machine;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.tstmodern.TSTModern;
import com.tstmodern.machine.NetherInterfaceMachine;
import com.tstmodern.registry.TSTBlocks;
import com.tstmodern.registry.TSTRecipeTypes;
import net.minecraft.network.chat.Component;

public final class NetherInterfaceDefinition {
    public static final MultiblockMachineDefinition MACHINE = TSTModern.REGISTRATE
            .multiblock("nether_interface", NetherInterfaceMachine::new)
            .langValue("Nether Interface")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(TSTRecipeTypes.NETHER_INTERFACE)
            .recipeModifiers(NetherInterfaceMachine::recipeModifier)
            .appearanceBlock(GTBlocks.CASING_STEEL_SOLID)
            .pattern(definition -> FactoryBlockPattern.start(
                    RelativeDirection.RIGHT,
                    RelativeDirection.DOWN,
                    RelativeDirection.BACK)
                    // === Aisle 0 (Z=0) ===
                    .aisle(
                            "               ",
                            "               ",
                            "               ",
                            "               ",
                            "               ",
                            "               ",
                            "               ",
                            "               ",
                            "               ",
                            "               ",
                            "               ",
                            "               ",
                            "               ",
                            "      AAA      ",
                            "      A~A      ",
                            "      AAA      ")
                    // === Aisle 1 (Z=1) ===
                    .aisle(
                            " AAAAAAAAAAAAA ",
                            " A           A ",
                            " BCCCCCCCCCCCB ",
                            " BC         CB ",
                            " BC         CB ",
                            " BC         CB ",
                            " BC         CB ",
                            " BC         CB ",
                            " BC         CB ",
                            " BC         CB ",
                            " BC         CB ",
                            " BC         CB ",
                            " BC         CB ",
                            " BCCCCCCCCCCCB ",
                            "AAAAAAAAAAAAAAA",
                            "AAAAAAAAAAAAAAA")
                    // === Aisle 2 (Z=2) ===
                    .aisle(
                            " AAAAAAAAAAAAA ",
                            " CCCCCCCCCCCCC ",
                            " CDDDDDDDDDDDC ",
                            " CD         DC ",
                            " CD         DC ",
                            " CD         DC ",
                            " CD         DC ",
                            " CD         DC ",
                            " CD         DC ",
                            " CD         DC ",
                            " CD         DC ",
                            " CD         DC ",
                            " CD         DC ",
                            " CDDDDDDDDDDDC ",
                            "ACCCCCCCCCCCCCA",
                            "AAAAAAAAAAAAAAA")
                    // === Aisle 3 (Z=3) ===
                    .aisle(
                            " AAAAAAAAAAAAA ",
                            " A           A ",
                            " BCCCCCCCCCCCB ",
                            " BC         CB ",
                            " BC         CB ",
                            " BC         CB ",
                            " BC         CB ",
                            " BC         CB ",
                            " BC         CB ",
                            " BC         CB ",
                            " BC         CB ",
                            " BC         CB ",
                            " BC         CB ",
                            " BCCCCCCCCCCCB ",
                            "AAAAAAAAAAAAAAA",
                            "AAAAAAAAAAAAAAA")
                    .where('~', Predicates.controller(blocks(definition.get())))
                    .where('A', blocks(GTBlocks.CASING_STEEL_SOLID.get())
                            .or(Predicates.autoAbilities(definition.getRecipeTypes()))
                            .or(Predicates.autoAbilities(true, false, false))
                            .or(Predicates.autoAbilities(false, false, true)))
                    .where('B', Predicates.frames(GTMaterials.Obsidian))
                    .where('C', blocks(TSTBlocks.MECHANICALLY_ENHANCED_OBSIDIAN.get()))
                    .where('D', blocks(net.minecraft.world.level.block.Blocks.OBSIDIAN))
                    .where(' ', Predicates.air())
                    .build())
            .workableCasingModel(
                    GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    TSTModern.id("block/multiblock/nether_interface"))
            .tooltips(
                    Component.translatable("tstmodern.machine.nether_interface.tooltip.0"),
                    Component.translatable("tstmodern.machine.nether_interface.tooltip.1"),
                    Component.translatable("tstmodern.machine.nether_interface.tooltip.2"),
                    Component.translatable("tstmodern.machine.nether_interface.tooltip.3"),
                    Component.translatable("tstmodern.machine.nether_interface.tooltip.4"))
            .register();

    private NetherInterfaceDefinition() {}
}
