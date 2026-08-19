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
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.tstmodern.TSTModern;
import com.tstmodern.machine.GiantVacuumDryingFurnaceMachine;
import com.tstmodern.machine.MegaStoneBreakerMachine;
import com.tstmodern.machine.NetherInterfaceMachine;

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
                    TSTModern.id("block/multiblock/mega_stone_breaker"))
            .tooltips(
                    Component.translatable("tstmodern.machine.mega_stone_breaker.tooltip.0"),
                    Component.translatable("tstmodern.machine.mega_stone_breaker.tooltip.1"),
                    Component.translatable("tstmodern.machine.mega_stone_breaker.tooltip.2"),
                    Component.translatable("tstmodern.machine.mega_stone_breaker.tooltip.3"))
            .register();

    public static final MultiblockMachineDefinition GIANT_VACUUM_DRYING_FURNACE = TSTModern.REGISTRATE

            .multiblock("giant_vacuum_drying_furnace", GiantVacuumDryingFurnaceMachine::new)
            .langValue("Giant Vacuum Drying Furnace")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeTypes(TSTRecipeTypes.VACUUM_FURNACE, TSTRecipeTypes.CHEMICAL_DEHYDRATOR)
            .recipeModifiers(GiantVacuumDryingFurnaceMachine::recipeModifier,
                    GTRecipeModifiers.OC_NON_PERFECT)
            .appearanceBlock(GCYMBlocks.CASING_LARGE_SCALE_ASSEMBLING)
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
                            " SSSSSSS EEE H       H ",
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
                    .where('E', blocks(GCYMBlocks.CASING_LARGE_SCALE_ASSEMBLING.get())
                            .or(Predicates.autoAbilities(definition.getRecipeTypes()))
                            .or(Predicates.autoAbilities(true, false, false))
                            .or(Predicates.autoAbilities(false, false, true)))
                    .where('S', blocks(GTBlocks.CASING_STAINLESS_CLEAN.get()))
                    .where('A', blocks(GTBlocks.CASING_TEMPERED_GLASS.get()).or(blocks(GTBlocks.CASING_LAMINATED_GLASS.get())))
                    .where('G', blocks(GTBlocks.CASING_POLYTETRAFLUOROETHYLENE_PIPE.get()))
                    .where('V', blocks(GTBlocks.CASING_PTFE_INERT.get()))
                    .where('I', blocks(TSTBlocks.CASING_C.get()))
                    .where('F', blocks(GTBlocks.CASING_ALUMINIUM_FROSTPROOF.get()))
                    .where('H', blocks(GTBlocks.CASING_POLYTETRAFLUOROETHYLENE_PIPE.get()))
                    .where('J', blocks(TSTBlocks.VACUUM_CASING.get()))
                    .where('C', Predicates.heatingCoils())
                    .where(' ', Predicates.any())
                    .build())
            .workableCasingModel(
                    GTCEu.id("block/casings/gcym/large_scale_assembling_casing"),
                    TSTModern.id("block/multiblock/giant_vacuum_drying_furnace"))
            .tooltips(
                    Component.translatable("tstmodern.machine.giant_vacuum_drying_furnace.tooltip.0"),
                    Component.translatable("tstmodern.machine.giant_vacuum_drying_furnace.tooltip.1"),
                    Component.translatable("tstmodern.machine.giant_vacuum_drying_furnace.tooltip.2"),
                    Component.translatable("tstmodern.machine.giant_vacuum_drying_furnace.tooltip.3"))
            .register();

    public static final MultiblockMachineDefinition NETHER_INTERFACE = TSTModern.REGISTRATE
            .multiblock("nether_interface", NetherInterfaceMachine::new)
            .langValue("Nether Interface")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(TSTRecipeTypes.NETHER_INTERFACE)
            .recipeModifiers(NetherInterfaceMachine::recipeModifier,
                    GTRecipeModifiers.OC_NON_PERFECT)
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


    private TSTMachines() {}


    /**
     * Triggers static initialization of machine definitions when GTCEu opens its machine registry.
     * The method body is intentionally empty: loading this class registers all static
     * {@link MultiblockMachineDefinition} fields above.
     */
    public static void registerMachines(GTCEuAPI.RegisterEvent<ResourceLocation, MachineDefinition> event) {
        // Intentionally empty — class loading registers all static MultiblockMachineDefinition fields.
    }
}
