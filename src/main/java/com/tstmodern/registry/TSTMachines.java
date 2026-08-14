package com.tstmodern.registry;

import static com.gregtechceu.gtceu.api.pattern.Predicates.abilities;
import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.common.data.GCYMBlocks;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.tstmodern.TSTModern;
import com.tstmodern.machine.MegaStoneBreakerMachine;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/** Machine definitions. This is the copyable base for later TST multiblock ports. */
public final class TSTMachines {
    public static final MultiblockMachineDefinition MEGA_STONE_BREAKER = TSTModern.REGISTRATE
            .multiblock("mega_stone_breaker", MegaStoneBreakerMachine::new)
            .langValue("Silicon Rock Synthesizer")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(TSTRecipeTypes.MEGA_STONE_BREAKER)
            .recipeModifiers(MegaStoneBreakerMachine::parallelAndOutputMultiplier,
                    GTRecipeModifiers.OC_NON_PERFECT)
            .appearanceBlock(GCYMBlocks.CASING_LARGE_SCALE_ASSEMBLING)
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
                    .where('A', blocks(GTBlocks.CASING_TITANIUM_STABLE.get()))
                    .where('B', blocks(GTBlocks.CASING_TUNGSTENSTEEL_PIPE.get()))
                    .where('C', blocks(TSTBlocks.CASING_C.get()))
                    .where('D', blocks(GTBlocks.MACHINE_CASING_ZPM.get()))
                    .where('E', blocks(TSTBlocks.COSMIC_NEUTRONIUM_FRAME.get()))
                    // Ability-part bases render with the machine's appearance casing while formed.
                    // Keep maintenance on F so its texture matches Large Scale Assembling Casing.
                    .where('F', blocks(GCYMBlocks.CASING_LARGE_SCALE_ASSEMBLING.get())
                            .or(Predicates.autoAbilities(true, false, false)))
                    .where('G', blocks(GCYMBlocks.CASING_STRESS_PROOF.get()))
                    .where('H', blocks(GTBlocks.FUSION_CASING.get()))
                    .where('I', blocks(GCYMBlocks.CASING_CORROSION_PROOF.get()))
                    .where('J', blocks(GCYMBlocks.CASING_LARGE_SCALE_ASSEMBLING.get())
                            // Derive item I/O and energy abilities from this machine's recipe type.
                            .or(Predicates.autoAbilities(definition.getRecipeTypes()))
                            // Parallel is a machine feature rather than recipe-map I/O.
                            .or(Predicates.autoAbilities(false, false, true)))
                    .where('L', abilities(PartAbility.IMPORT_FLUIDS).setPreviewCount(1))
                    .where('W', abilities(PartAbility.IMPORT_FLUIDS).setPreviewCount(1))
                    .where(' ', Predicates.any())
                    .build())
            .workableCasingModel(
                    GTCEu.id("block/casings/gcym/large_scale_assembling_casing"),
                    GTCEu.id("block/machines/rock_crusher"))
            .tooltips(
                    Component.translatable("tstmodern.machine.mega_stone_breaker.tooltip.0"),
                    Component.translatable("tstmodern.machine.mega_stone_breaker.tooltip.1"),
                    Component.translatable("tstmodern.machine.mega_stone_breaker.tooltip.2"),
                    Component.translatable("tstmodern.machine.mega_stone_breaker.tooltip.3"))
            .register();

    private TSTMachines() {}

    public static void registerMachines(GTCEuAPI.RegisterEvent<ResourceLocation, MachineDefinition> event) {
        // Static initialization registers the machine while GTCEu's machine registry is open.
    }
}
