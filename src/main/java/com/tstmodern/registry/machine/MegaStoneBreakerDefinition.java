package com.tstmodern.registry.machine;

import static com.gregtechceu.gtceu.api.pattern.Predicates.abilities;
import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;

import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.common.data.GCYMBlocks;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.tstmodern.TSTModern;
import com.tstmodern.machine.MegaStoneBreakerMachine;
import com.tstmodern.registry.TSTBlocks;
import com.tstmodern.registry.TSTRecipeTypes;
import net.minecraft.network.chat.Component;

public final class MegaStoneBreakerDefinition {
    public static final MultiblockMachineDefinition MACHINE = TSTModern.REGISTRATE
            .multiblock("mega_stone_breaker", MegaStoneBreakerMachine::new)
            .langValue("Silicon Rock Synthesizer")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(TSTRecipeTypes.MEGA_STONE_BREAKER)
            .recipeModifiers(MegaStoneBreakerMachine::parallelAndOutputMultiplier,
                    GTRecipeModifiers.OC_NON_PERFECT)
            .appearanceBlock(TSTBlocks.STABLE_RED_STEEL_CASING)
            // StructureLib/TST axes increase L->R, U->D and F->B. GTCEu's default
            // pattern directions are the opposite, so declare all three explicitly.
            .pattern(definition -> FactoryBlockPattern.start(
                    RelativeDirection.RIGHT,
                    RelativeDirection.DOWN,
                    RelativeDirection.BACK)
                    .aisle(
                            "                     ",
                            "  FFFFF       FFFFF  ",
                            "  GGGGG       GGGGG  ",
                            "  GHHHG       GHHHG  ",
                            "  GHHHG       GHHHG  ",
                            "  GHHHG       GHHHG  ",
                            "  GGGGG       GGGGG  ",
                            "  FFLFF       FFWFF  ")
                    .aisle(
                            "  GGGGG       GGGGG  ",
                            " FGEEEGF     FGEEEGF ",
                            " GEEEEEG     GEEEEEG ",
                            " GIIIIIG     GIIIIIG ",
                            " GIIIIIG     GIIIIIG ",
                            " GIIIIIG     GIIIIIG ",
                            " GEEGEEG     GEEGEEG ",
                            " FGGGGGF     FGGGGGF ")
                    .aisle(
                            " GGDCDGG     GGDCDGG ",
                            "FGBBBBBGFFFFFGBBBBBGF",
                            "GEBBBBBEGGGGGEBBBBBEG",
                            "GIBBBBBIGFGFGIBBBBBIG",
                            "GIBBBBBIGFFFGIBBBBBIG",
                            "GIBBBBBIGFGFGIBBBBBIG",
                            "GEEEGEEEGGGGGEEEGEEEG",
                            "FGGGGGGGJJ~JJGGGGGGGF")
                    .aisle(
                            " GDDCDDGGGGGGGDDCDDG ",
                            "FEBIIIBEGEEEGEBIIIBEF",
                            "GEBIIIBEGIIIGEBIIIBEG",
                            "HIBIIIBIIIIIIIBIIIBIH",
                            "HIBIIIBIIIIIIIBIIIBIH",
                            "HIBIIIBIIIIIIIBIIIBIH",
                            "GEEIIIEEGIIIGEEIIIEEG",
                            "FGGGGGGGGGGGGGGGGGGGF")
                    .aisle(
                            " GCCCCCCCCCCCCCCCCCG ",
                            "FEBIBIBEGEEEGEBIBIBEF",
                            "GEBIBIBEGGGGGEBIBIBEG",
                            "HIBIBIBIAAAAAIBIBIBIH",
                            "HIBIBIBIAAAAAIBIBIBIH",
                            "HIBIBIBIAAAAAIBIBIBIH",
                            "GGGIBIGGGGGGGGGIBIGGG",
                            "FGGGGGGGGGGGGGGGGGGGF")
                    .aisle(
                            " GDDCDDGGGGGGGDDCDDG ",
                            "FEBIIIBEGEEEGEBIIIBEF",
                            "GEBIIIBEGIIIGEBIIIBEG",
                            "HIBIIIBIIIIIIIBIIIBIH",
                            "HIBIIIBIIIIIIIBIIIBIH",
                            "HIBIIIBIIIIIIIBIIIBIH",
                            "GEEIIIEEGIIIGEEIIIEEG",
                            "FGGGGGGGGGGGGGGGGGGGF")
                    .aisle(
                            " GGDCDGG     GGDCDGG ",
                            "FGBBBBBGFFFFFGBBBBBGF",
                            "GEBBBBBEGGGGGEBBBBBEG",
                            "GIBBBBBIGGGGGIBBBBBIG",
                            "GIBBBBBIGGGGGIBBBBBIG",
                            "GIBBBBBIGGGGGIBBBBBIG",
                            "GEEEGEEEGGGGGEEEGEEEG",
                            "FGGGGGGGFFFFFGGGGGGGF")
                    .aisle(
                            "  GGGGG       GGGGG  ",
                            " FGEEEGF     FGEEEGF ",
                            " GEEEEEG     GEEEEEG ",
                            " GIIIIIG     GIIIIIG ",
                            " GIIIIIG     GIIIIIG ",
                            " GIIIIIG     GIIIIIG ",
                            " GEEGEEG     GEEGEEG ",
                            " FGGGGGF     FGGGGGF ")
                    .aisle(
                            "                     ",
                            "  FFFFF       FFFFF  ",
                            "  GGGGG       GGGGG  ",
                            "  GHHHG       GHHHG  ",
                            "  GHHHG       GHHHG  ",
                            "  GHHHG       GHHHG  ",
                            "  GGGGG       GGGGG  ",
                            "  FFFFF       FFFFF  ")
                    .where('~', Predicates.controller(blocks(definition.get())))
                    .where('A', blocks(TSTBlocks.BLACK_PLUTONIUM_ITEM_PIPE_CASING.get()))
                    .where('B', blocks(GTBlocks.CASING_TUNGSTENSTEEL_PIPE.get()))
                    .where('C', blocks(TSTBlocks.ADVANCED_IRIDIUM_CASING.get()))
                    .where('D', blocks(GTBlocks.FILTER_CASING.get()))
                    .where('E', Predicates.frames(GTMaterials.Neutronium))
                    // Ability-part bases render with the machine's appearance casing while formed.
                    // Keep maintenance on the source Stable Red Steel casing.
                    .where('F', blocks(TSTBlocks.STABLE_RED_STEEL_CASING.get())
                            .or(Predicates.autoAbilities(true, false, false)))
                    .where('G', blocks(TSTBlocks.STABLE_TANTALLOY_61_CASING.get()))
                    .where('H', blocks(TSTBlocks.STABALOY_FIREBOX_CASING.get()))
                    .where('I', blocks(GCYMBlocks.CASING_STRESS_PROOF.get()))
                    .where('J', blocks(TSTBlocks.STABLE_RED_STEEL_CASING.get())
                            // Derive item I/O and energy abilities from this machine's recipe type.
                            .or(Predicates.autoAbilities(definition.getRecipeTypes()))
                            // Parallel is a machine feature rather than recipe-map I/O.
                            .or(Predicates.autoAbilities(false, false, true)))
                    .where('L', abilities(PartAbility.IMPORT_FLUIDS).setPreviewCount(1))
                    .where('W', abilities(PartAbility.IMPORT_FLUIDS).setPreviewCount(1))
                    .where(' ', Predicates.air())
                    .build())
            .workableCasingModel(
                    TSTModern.id("block/casings/stable_red_steel_casing"),
                    TSTModern.id("block/multiblock/mega_stone_breaker"))
            .tooltips(
                    Component.translatable("tstmodern.machine.mega_stone_breaker.tooltip.0"),
                    Component.translatable("tstmodern.machine.mega_stone_breaker.tooltip.1"),
                    Component.translatable("tstmodern.machine.mega_stone_breaker.tooltip.2"),
                    Component.translatable("tstmodern.machine.mega_stone_breaker.tooltip.3"))
            .register();

    private MegaStoneBreakerDefinition() {}
}
