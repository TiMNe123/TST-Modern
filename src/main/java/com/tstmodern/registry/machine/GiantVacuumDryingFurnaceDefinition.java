package com.tstmodern.registry.machine;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;

import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.tstmodern.TSTModern;
import com.tstmodern.machine.GiantVacuumDryingFurnaceMachine;
import com.tstmodern.registry.TSTBlocks;
import com.tstmodern.registry.TSTRecipeTypes;
import net.minecraft.network.chat.Component;

public final class GiantVacuumDryingFurnaceDefinition {
    public static final MultiblockMachineDefinition MACHINE = TSTModern.REGISTRATE
            .multiblock("giant_vacuum_drying_furnace", GiantVacuumDryingFurnaceMachine::new)
            .langValue("Giant Vacuum Drying Furnace")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeTypes(TSTRecipeTypes.VACUUM_FURNACE, TSTRecipeTypes.CHEMICAL_DEHYDRATOR)
            .recipeModifiers(GiantVacuumDryingFurnaceMachine::recipeModifier,
                    GTRecipeModifiers.OC_NON_PERFECT)
            .appearanceBlock(TSTBlocks.NEUTRONIUM_MINING_CASING)
            .pattern(definition -> FactoryBlockPattern.start(
                    RelativeDirection.RIGHT,
                    RelativeDirection.DOWN,
                    RelativeDirection.BACK)
                    // Aisle 0: DT front protrusion (symmetric with aisle 10)
                    .aisle(
                            "                       ",
                            "                       ",
                            "                       ",
                            "                       ",
                            "                       ",
                            "                       ",
                            "                       ",
                            "                       ",
                            "                       ",
                            "                       ",
                            "                       ",
                            "                       ",
                            "                       ",
                            "              HHHHHHH  ",
                            "             VVVVVVVVV ")
                    // Aisle 1: Controller + DT body + VP/MF start
                    .aisle(
                            "                       ",
                            "                       ",
                            "                       ",
                            "               I   I   ",
                            "               I   I   ",
                            "               I   I   ",
                            "               I   I   ",
                            "               I   I   ",
                            "         EEE   I   I   ",
                            "         EEE   I   I   ",
                            "         EEE IIIIIIIII ",
                            "         E~E H       H ",
                            "         EEE H       H ",
                            "         EEE H       H ",
                            "VVVVVVVVVEEEVVVVVVVVVVV")
                    // Aisle 2
                    .aisle(
                            "                       ",
                            "               I   I   ",
                            "               I   I   ",
                            "              JJJAJJJ  ",
                            "                       ",
                            "               C   C   ",
                            "              CCC CCC  ",
                            " SSSSSSS      CCC CCC  ",
                            " SAAAAAS EEE   C   C   ",
                            " SAAAAAS EEE           ",
                            " SAAAAAS EEE IJJJAJJJI ",
                            " SAAAAAS EEE           ",
                            " SAAAAAS EEE  FFFFFFF  ",
                            " SSSSSSS EEEH         H",
                            "VVVVVVVVVEEEVVVVVVVVVVV")
                    // Aisle 3
                    .aisle(
                            "                VVV    ",
                            "              IV   VI  ",
                            "              I     I  ",
                            "             IJJJAJJJI ",
                            "             I C   C I ",
                            "             IC C C CI ",
                            "             IC C C CI ",
                            " SSSSSSS     IC C C CI ",
                            " S     S EEE IC C C CI ",
                            " S     S EEE I C   C I ",
                            " S     S EEE IJJJAJJJI ",
                            " S     S EEE  FFFFFFF  ",
                            " S     S EEE FGGGGGGGF ",
                            " SSSSSSS EEEH FFFFFFF H",
                            "VVVVVVVVVEEEVVVVVVVVVVV")
                    // Aisle 4
                    .aisle(
                            "               VGGGV   ",
                            "                 V     ",
                            "                       ",
                            "              JJJAJJJ  ",
                            "                       ",
                            "               C   C   ",
                            "              CCC CCC  ",
                            " SSSSSSS      CCC CCC  ",
                            " S     S EEE   C   C   ",
                            " S     S EEE           ",
                            " S     S EEE IJJJAJJJI ",
                            " S     S EEE     V     ",
                            " S     S EEE  FFFFFFF  ",
                            " SSSSSSS EEEH         H",
                            "VVVVVVVVVEEEVVVVVVVVVVV")
                    // Aisle 5 (center axis)
                    .aisle(
                            "               VGGGV   ",
                            "                VGV    ",
                            "                 G     ",
                            "             AAAAGAAAA ",
                            "                 G     ",
                            "                 G     ",
                            "                 G     ",
                            " SSSSSSS         G     ",
                            " S     S EEE     G     ",
                            " S     S EEE     G     ",
                            " S     S EEE AAAAGAAAA ",
                            " S     S EEE    VGV    ",
                            " S     S EEE           ",
                            " SSSSSSS EEEH         H",
                            "VVVVVVVVVEEEVVVVVVVVVVV")
                    // Aisle 6
                    .aisle(
                            "               VGGGV   ",
                            "                 V     ",
                            "                       ",
                            "              JJJAJJJ  ",
                            "                       ",
                            "               C   C   ",
                            "              CCC CCC  ",
                            " SSSSSSS      CCC CCC  ",
                            " S     S EEE   C   C   ",
                            " S     S EEE           ",
                            " S     S EEE IJJJAJJJI ",
                            " S     S EEE     V     ",
                            " S     S EEE  FFFFFFF  ",
                            " SSSSSSS EEEH         H",
                            "VVVVVVVVVEEEVVVVVVVVVVV")
                    // Aisle 7
                    .aisle(
                            "                VVV    ",
                            "              IV   VI  ",
                            "              I     I  ",
                            "             IJJJAJJJI ",
                            "             I C   C I ",
                            "             IC C C CI ",
                            "             IC C C CI ",
                            " SSSSSSS     IC C C CI ",
                            " SAAAAAS EEE IC C C CI ",
                            " SAAAAAS EEE I C   C I ",
                            " SAAAAAS EEE IJJJAJJJI ",
                            " SAAAAAS EEE  FFFFFFF  ",
                            " SAAAAAS EEE FGGGGGGGF ",
                            " SSSSSSS EEEH FFFFFFF H",
                            "VVVVVVVVVEEEVVVVVVVVVVV")
                    // Aisle 8
                    .aisle(
                            "                       ",
                            "               I   I   ",
                            "               I   I   ",
                            "              JJJAJJJ  ",
                            "                       ",
                            "               C   C   ",
                            "              CCC CCC  ",
                            " SSSSSSS      CCC CCC  ",
                            " SAAAAAS EEE   C   C   ",
                            " SAAAAAS EEE           ",
                            " SAAAAAS EEE IJJJAJJJI ",
                            " SAAAAAS EEE           ",
                            " SAAAAAS EEE  FFFFFFF  ",
                            " SSSSSSS EEEH         H",
                            "VVVVVVVVVEEEVVVVVVVVVVV")
                    // Aisle 9
                    .aisle(
                            "                       ",
                            "                       ",
                            "                       ",
                            "               I   I   ",
                            "               I   I   ",
                            "               I   I   ",
                            "               I   I   ",
                            "               I   I   ",
                            "         EEE   I   I   ",
                            "         EEE   I   I   ",
                            "         EEE IIIIIIIII ",
                            "         EEE H       H ",
                            "         EEE H       H ",
                            "         EEE H       H ",
                            "VVVVVVVVVEEEVVVVVVVVVVV")
                    // Aisle 10: DT back protrusion (symmetric with aisle 0)
                    .aisle(
                            "                       ",
                            "                       ",
                            "                       ",
                            "                       ",
                            "                       ",
                            "                       ",
                            "                       ",
                            "                       ",
                            "                       ",
                            "                       ",
                            "                       ",
                            "                       ",
                            "                       ",
                            "              HHHHHHH  ",
                            "             VVVVVVVVV ")
                    .where('~', Predicates.controller(blocks(definition.get())))
                    .where('E', blocks(TSTBlocks.NEUTRONIUM_MINING_CASING.get())
                            .or(Predicates.autoAbilities(definition.getRecipeTypes()))
                            .or(Predicates.autoAbilities(true, false, false))
                            .or(Predicates.autoAbilities(false, false, true)))
                    .where('S', blocks(GTBlocks.CASING_STAINLESS_CLEAN.get()))
                    .where('A', blocks(GTBlocks.CASING_TEMPERED_GLASS.get())
                            .or(blocks(GTBlocks.FUSION_GLASS.get())))
                    .where('G', blocks(GTBlocks.CASING_POLYTETRAFLUOROETHYLENE_PIPE.get()))
                    .where('V', blocks(GTBlocks.CASING_PTFE_INERT.get()))
                    .where('I', blocks(TSTBlocks.ADVANCED_IRIDIUM_CASING.get()))
                    .where('F', blocks(GTBlocks.CASING_ALUMINIUM_FROSTPROOF.get()))
                    .where('H', blocks(GTBlocks.CASING_POLYTETRAFLUOROETHYLENE_PIPE.get()))
                    .where('J', blocks(TSTBlocks.VACUUM_CASING.get()))
                    .where('C', Predicates.heatingCoils())
                    .where(' ', Predicates.air())
                    .build())
            .workableCasingModel(
                    TSTModern.id("block/casings/neutronium_mining_casing"),
                    TSTModern.id("block/multiblock/giant_vacuum_drying_furnace"))
            .tooltips(
                    Component.translatable("tstmodern.machine.giant_vacuum_drying_furnace.tooltip.0"),
                    Component.translatable("tstmodern.machine.giant_vacuum_drying_furnace.tooltip.1"),
                    Component.translatable("tstmodern.machine.giant_vacuum_drying_furnace.tooltip.2"),
                    Component.translatable("tstmodern.machine.giant_vacuum_drying_furnace.tooltip.3"))
            .register();

    private GiantVacuumDryingFurnaceDefinition() {}
}
