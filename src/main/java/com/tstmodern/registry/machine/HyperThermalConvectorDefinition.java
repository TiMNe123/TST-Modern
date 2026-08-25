package com.tstmodern.registry.machine;

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
import com.tstmodern.machine.HyperThermalConvectorMachine;
import com.tstmodern.registry.TSTBlocks;
import com.tstmodern.registry.TSTRecipeTypes;
import net.minecraft.network.chat.Component;

public final class HyperThermalConvectorDefinition {
    public static final MultiblockMachineDefinition MACHINE = TSTModern.REGISTRATE
            .multiblock("hyper_thermal_convector", HyperThermalConvectorMachine::new)
            .langValue("Hyper Thermal Convector")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeTypes(TSTRecipeTypes.RAPID_HEAT_EXCHANGE, TSTRecipeTypes.RAPID_COOLING)
            .recipeModifiers(HyperThermalConvectorMachine::recipeModifier,
                    GTRecipeModifiers.OC_NON_PERFECT)
            .appearanceBlock(TSTBlocks.ADVANCED_IRIDIUM_CASING)
            .pattern(definition -> FactoryBlockPattern.start(
                    RelativeDirection.RIGHT,
                    RelativeDirection.DOWN,
                    RelativeDirection.BACK)
                    // === Aisle 0 (Z=0) ===
                    .aisle(
                            "                     ",
                            "                     ",
                            "                     ",
                            "                     ",
                            "                     ",
                            "                     ",
                            "                     ",
                            "                     ",
                            "                     ",
                            "                     ",
                            "                     ",
                            "                     ",
                            "       MMMMMMM       ",
                            "       MMMMMMM       ")
                    // === Aisle 1 (Z=1) ===
                    .aisle(
                            "                     ",
                            "                     ",
                            "                     ",
                            "                     ",
                            "                     ",
                            "                     ",
                            "                     ",
                            "                     ",
                            "                     ",
                            "                     ",
                            "                     ",
                            "                     ",
                            "      MMMMMMMMM      ",
                            "      MMMMMMMMM      ")
                    // === Aisle 2 (Z=2) ===
                    .aisle(
                            "                     ",
                            "       HGGGGGH       ",
                            "       HGGGGGH       ",
                            "       HGGGGGH       ",
                            "      HGGGGGGGH      ",
                            "      HGGGGGGGH      ",
                            "      HGGG~GGGH      ",
                            "      HGGGGGGGH      ",
                            "      HGGGGGGGH      ",
                            "       HGGGGGH       ",
                            "       HGGGGGH       ",
                            "       HGGGGGH       ",
                            "    MMMMMMMMMMMMM    ",
                            "    MMMMMMMMMMMMM    ")
                    // === Aisle 3 (Z=3) ===
                    .aisle(
                            "       HHGGGHH       ",
                            "      HEEEEEEEH      ",
                            "      HDDDDDDDH      ",
                            "      HDDDDDDDH      ",
                            "     HKDDDDDDDKH     ",
                            "     HKDDDDDDDKH     ",
                            "     HKDDDDDDDKH     ",
                            "     HKDDDDDDDKH     ",
                            "     HKDDDDDDDKH     ",
                            "      HDDDDDDDH      ",
                            "      HDDDDDDDH      ",
                            "      HEEEEEEEH      ",
                            " MMMMMMHHGGGHHMMMMMM ",
                            " MMMMMMMMMMMMMMMMMMM ")
                    // === Aisle 4 (Z=4) ===
                    .aisle(
                            "      HHHHHHHHH      ",
                            "     EECCCCCCCEE     ",
                            "     GECCCCCCCEG     ",
                            "     GECCCCCCCEG     ",
                            "     GECCCCCCCEG     ",
                            "     EECCCCCCCEE     ",
                            "     DDCCCCCCCDD     ",
                            "     EECCCCCCCEE     ",
                            "     GECCCCCCCEG     ",
                            "     GECCCCCCCEG     ",
                            "     GECCCCCCCEG     ",
                            "     EECCCCCCCEE     ",
                            "MMMMMMHHHHHHHHHMMMMMM",
                            "MMMMMMMMMMMMMMMMMMMMM")
                    // === Aisle 5 (Z=5) ===
                    .aisle(
                            "     JGHHHHHHHGJ     ",
                            "  EEEGCCCCCCCCCGEEE  ",
                            "  GGGCCLLQQQLLCCGGG  ",
                            "  GRGCCLLNNNLLCCGTG  ",
                            "  GGGCCLLNNNLLCCGGG  ",
                            "  EEECCLLNNNLLCCEEE  ",
                            "  DDDCCLLNNNLLCCDDD  ",
                            "  EEECCLLNNNLLCCEEE  ",
                            "  GGGCCLLNNNLLCCGGG  ",
                            "  GSGCCLLNNNLLCCGUG  ",
                            "  GGGCCLLQQQLLCCGGG  ",
                            "  EEEGCCCCCCCCCGEEE  ",
                            "MMMMMGGHHHHHHHGGMMMMM",
                            "MMMMMMMMMMMMMMMMMMMMM")
                    // === Aisle 6 (Z=6) ===
                    .aisle(
                            "    JGGGGGGGGGGGJ    ",
                            " EGGGCCCCCCCCCCCGGGE ",
                            " GPPPPPPLNNNLPPPPPPG ",
                            " GPFPPPPBOOOAPPPPFPG ",
                            " GPPPPPPBOOOAPPPPPPG ",
                            " EQQQPPPBOOOAPPPQQQE ",
                            " DCCCPPPBOOOAPPPCCCD ",
                            " EQQQPPPBOOOAPPPQQQE ",
                            " GPPPPPPBOOOAPPPPPPG ",
                            " GPFPPPPBOOOAPPPPFPG ",
                            " GPPPPPPLNNNLPPPPPPG ",
                            " EGGGCCCCCCCCCCCGGGE ",
                            "MMMMGGGGGGGGGGGGGMMMM",
                            "MMMMMMMMMMMMMMMMMMMMM")
                    // === Aisle 7 (Z=7) ===
                    .aisle(
                            "    JGIIIIIIIIIGJ    ",
                            " EGGGCCCCCCCCCCCGGGE ",
                            " GPPPPPPLNNNLPPPPPPG ",
                            " GPFFFFPBOOOAPFFFFPG ",
                            " GPPPPFPBOOOAPFPPPPG ",
                            " EQQQPFPBOOOAPFPQQQE ",
                            " DCCCPFPBOOOAPFPCCCD ",
                            " EQQQPFPBOOOAPFPQQQE ",
                            " GPPPPFPBOOOAPFPPPPG ",
                            " GPFFFFPBOOOAPFFFFPG ",
                            " GPPPPPPLNNNLPPPPPPG ",
                            " EGGGCCCCCCCCCCCGGGE ",
                            "MMMMGGGGGGGGGGGGGMMMM",
                            "MMMMMMMMMMMMMMMMMMMMM")
                    // === Aisle 8 (Z=8) ===
                    .aisle(
                            "    JGGGGGGGGGGGJ    ",
                            " EGGGCCCCCCCCCCCGGGE ",
                            " GPPPPPPLNNNLPPPPPPG ",
                            " GPPPPPPBOOOAPPPPPPG ",
                            " GPPPPPPBOOOAPPPPPPG ",
                            " EQQQPPPBOOOAPPPQQQE ",
                            " DCCCPPPBOOOAPPPCCCD ",
                            " EQQQPPPBOOOAPPPQQQE ",
                            " GPPPPPPBOOOAPPPPPPG ",
                            " GPPPPPPBOOOAPPPPPPG ",
                            " GPPPPPPLNNNLPPPPPPG ",
                            " EGGGCCCCCCCCCCCGGGE ",
                            "MMMMGGGGGGGGGGGGGMMMM",
                            "MMMMMMMMMMMMMMMMMMMMM")
                    // === Aisle 9 (Z=9) ===
                    .aisle(
                            "     JGHHHHHHHGJ     ",
                            "  EEEGCCCCCCCCCGEEE  ",
                            "  GGGCCLLQQQLLCCGGG  ",
                            "  GGGCCLLNNNLLCCGGG  ",
                            "  GGGCCLLNNNLLCCGGG  ",
                            "  EEECCLLNNNLLCCEEE  ",
                            "  DDDCCLLNNNLLCCDDD  ",
                            "  EEECCLLNNNLLCCEEE  ",
                            "  GGGCCLLNNNLLCCGGG  ",
                            "  GGGCCLLNNNLLCCGGG  ",
                            "  GGGCCLLQQQLLCCGGG  ",
                            "  EEEGCCCCCCCCCGEEE  ",
                            "MMMMMGGHHHHHHHGGMMMMM",
                            "MMMMMMMMMMMMMMMMMMMMM")
                    // === Aisle 10 (Z=10) ===
                    .aisle(
                            "      HHHHHHHHH      ",
                            "     EECCCCCCCEE     ",
                            "     GECCCCCCCEG     ",
                            "     GECCCCCCCEG     ",
                            "     GECCCCCCCEG     ",
                            "     EECCCCCCCEE     ",
                            "     DDCCCCCCCDD     ",
                            "     EECCCCCCCEE     ",
                            "     GECCCCCCCEG     ",
                            "     GECCCCCCCEG     ",
                            "     GECCCCCCCEG     ",
                            "     EECCCCCCCEE     ",
                            "MMMMMMHHHHHHHHHMMMMMM",
                            "MMMMMMMMMMMMMMMMMMMMM")
                    // === Aisle 11 (Z=11) ===
                    .aisle(
                            "       HHHHHHH       ",
                            "      HEEEEEEEH      ",
                            "      HDDDDDDDH      ",
                            "      HDDDDDDDH      ",
                            "     HKDDDDDDDKH     ",
                            "     HKDDDDDDDKH     ",
                            "     HKDDDDDDDEH     ",
                            "     HKDDDDDDDKH     ",
                            "     HKDDDDDDDKH     ",
                            "      HDDDDDDDH      ",
                            "      HDDDDDDDH      ",
                            "      HEEEEEEEH      ",
                            " MMMMMMHHHHHHHMMMMMM ",
                            " MMMMMMMMMMMMMMMMMMM ")
                    // === Aisle 12 (Z=12) ===
                    .aisle(
                            "                     ",
                            "       HGGGGGH       ",
                            "       HGGGGGH       ",
                            "       HGGGGGH       ",
                            "      HGGGGGGGH      ",
                            "      HGGGGGGGH      ",
                            "      HGGGGGGGH      ",
                            "      HGGGGGGGH      ",
                            "      HGGGGGGGH      ",
                            "       HGGGGGH       ",
                            "       HGGGGGH       ",
                            "       HGGGGGH       ",
                            "    MMMMMMMMMMMMM    ",
                            "    MMMMMMMMMMMMM    ")
                    // === Aisle 13 (Z=13) ===
                    .aisle(
                            "                     ",
                            "                     ",
                            "                     ",
                            "                     ",
                            "                     ",
                            "                     ",
                            "                     ",
                            "                     ",
                            "                     ",
                            "                     ",
                            "                     ",
                            "                     ",
                            "      MMMMMMMMM      ",
                            "      MMMMMMMMM      ")
                    // === Aisle 14 (Z=14) ===
                    .aisle(
                            "                     ",
                            "                     ",
                            "                     ",
                            "                     ",
                            "                     ",
                            "                     ",
                            "                     ",
                            "                     ",
                            "                     ",
                            "                     ",
                            "                     ",
                            "                     ",
                            "       MMMMMMM       ",
                            "       MMMMMMM       ")
                    .where('~', Predicates.controller(blocks(definition.get())))
                    .where('A', blocks(TSTBlocks.HS188A_BLOCK.get()))
                    .where('B', blocks(TSTBlocks.QUANTUM_ALLOY_BLOCK.get()))
                    .where('C', blocks(TSTBlocks.EXTREME_DENSITY_CASING.get()))
                    .where('D', blocks(GTBlocks.CASING_ALUMINIUM_FROSTPROOF.get()))
                    .where('E', blocks(TSTBlocks.OSMIRIDIUM_MINING_CASING.get()))
                    .where('F', blocks(TSTBlocks.TANK_CASING_TIER_10.get()))
                    .where('G', blocks(TSTBlocks.ADVANCED_IRIDIUM_CASING.get())
                            .or(Predicates.autoAbilities(true, false, false))
                            .or(Predicates.autoAbilities(false, false, true)))
                    .where('H', blocks(TSTBlocks.RADIANT_NAQUADAH_ALLOY_CASING.get()))
                    .where('I', blocks(GTBlocks.FILTER_CASING.get()))
                    .where('J', Predicates.frames(GTMaterials.Iridium))
                    .where('K', Predicates.frames(GTMaterials.Neutronium))
                    .where('L', blocks(GCYMBlocks.CASING_STRESS_PROOF.get()))
                    .where('M', blocks(TSTBlocks.DYSON_SWARM_FLOOR.get()))
                    .where('N', blocks(TSTBlocks.IRIDIUM_REINFORCED_NEUTRONIUM_CASING.get()))
                    .where('O', blocks(TSTBlocks.BOROPHENE_NANOWIRE_CASING.get()))
                    .where('P', blocks(TSTBlocks.NEUTRONIUM_PIPE_CASING.get()))
                    .where('Q', blocks(GTBlocks.FUSION_CASING.get()))
                    .where('R', Predicates.abilities(PartAbility.IMPORT_FLUIDS).setPreviewCount(1))
                    .where('S', Predicates.abilities(PartAbility.EXPORT_FLUIDS).setPreviewCount(1))
                    .where('T', Predicates.abilities(PartAbility.EXPORT_FLUIDS).setPreviewCount(1))
                    .where('U', Predicates.abilities(PartAbility.IMPORT_FLUIDS).setPreviewCount(1))
                    .where(' ', Predicates.air())
                    .build())
            .workableCasingModel(
                    TSTModern.id("block/casings/advanced_iridium_casing"),
                    TSTModern.id("block/multiblock/hyper_thermal_convector"))
            .tooltips(
                    Component.translatable("tstmodern.machine.hyper_thermal_convector.tooltip.0"),
                    Component.translatable("tstmodern.machine.hyper_thermal_convector.tooltip.1"),
                    Component.translatable("tstmodern.machine.hyper_thermal_convector.tooltip.2"),
                    Component.translatable("tstmodern.machine.hyper_thermal_convector.tooltip.3"),
                    Component.translatable("tstmodern.machine.hyper_thermal_convector.tooltip.4"),
                    Component.translatable("tstmodern.machine.hyper_thermal_convector.tooltip.5"))
            .register();

    private HyperThermalConvectorDefinition() {}
}
