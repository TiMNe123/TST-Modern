package com.tstmodern.data.recipe;

import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKeys;
import com.tstmodern.TSTModern;
import com.tstmodern.machine.MegaTreeFarmMachine;
import com.tstmodern.registry.TSTBlocks;
import com.tstmodern.registry.TSTRecipeTypes;
import com.tstmodern.registry.machine.MegaTreeFarmDefinition;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import static com.gregtechceu.gtceu.common.data.GTMaterials.StainlessSteel;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Lead;
import static com.gregtechceu.gtceu.common.data.GTMaterials.NetherQuartz;
import static com.gregtechceu.gtceu.api.GTValues.UHV;
import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.GTValues.EV;
import static com.gregtechceu.gtceu.api.GTValues.IV;
import static com.gregtechceu.gtceu.api.GTValues.LuV;
import static com.gregtechceu.gtceu.api.GTValues.VA;
import static com.gregtechceu.gtceu.api.GTValues.ZPM;
import static com.gregtechceu.gtceu.api.GTValues.UV;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.dust;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.frameGt;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.pipeLargeFluid;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.plate;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.plateDense;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.foil;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.screw;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.gear;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.wireGtSingle;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.wireGtQuadruple;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.pipeTinyFluid;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.pipeNormalFluid;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.pipeHugeFluid;
import static com.gregtechceu.gtceu.common.data.GTItems.ELECTRIC_PISTON_UV;
import static com.gregtechceu.gtceu.common.data.GTItems.ELECTRIC_PUMP_UV;
import static com.gregtechceu.gtceu.common.data.GTBlocks.CASING_TUNGSTENSTEEL_ROBUST;
import static com.gregtechceu.gtceu.common.data.GTItems.CONVEYOR_MODULE_ZPM;
import static com.gregtechceu.gtceu.common.data.GTItems.ELECTRIC_PUMP_ZPM;
import static com.gregtechceu.gtceu.common.data.GTItems.FIELD_GENERATOR_ZPM;
import static com.gregtechceu.gtceu.common.data.GTItems.ROBOT_ARM_ZPM;
import static com.gregtechceu.gtceu.common.data.GTMachines.HULL;
import static com.gregtechceu.gtceu.common.data.GTMaterials.LiquidNetherAir;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Naquadah;
import static com.gregtechceu.gtceu.common.data.GTMaterials.NaquadahAlloy;
import static com.gregtechceu.gtceu.common.data.GTMaterials.SolderingAlloy;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Titanium;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ASSEMBLER_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ASSEMBLY_LINE_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTMaterials.DistilledWater;
import com.gregtechceu.gtceu.data.recipe.CustomTags;

/** Exact processing and construction recipes owned by the Mega Tree Farm. */
public final class MegaTreeFarmRecipes {
        private MegaTreeFarmRecipes() {
        }

        /**
         * Registers the Mega Tree Farm controller and dedicated casing construction
         * recipes, all tree-growth recipes, and the aquatic-zone recipe.
         */
        public static void register(Consumer<FinishedRecipe> provider) {
        addMegaTreeFarmControllerRecipe(provider);
        addMegaTreeFarmCasingRecipes(provider);
        addMegaTreeFarmRecipes(provider);
        }

    private static void addMegaTreeFarmControllerRecipe(Consumer<FinishedRecipe> provider) {
        ASSEMBLY_LINE_RECIPES.recipeBuilder(TSTModern.id("assembly_line/mega_tree_farm"))
                .inputItems(HULL[UHV], 2)
                .inputItems(CustomTags.UHV_CIRCUITS, 16)
                .inputItems(ELECTRIC_PUMP_ZPM, 16)
                .inputItems(ROBOT_ARM_ZPM, 16)
                .inputItems(CONVEYOR_MODULE_ZPM, 16)
                .inputItems(FIELD_GENERATOR_ZPM, 8)
                .inputItems(plateDense, NaquadahAlloy, 16)
                .inputItems(TSTBlocks.STERILE_CASING.get().asItem(), 16)
                .inputItems(TSTBlocks.CULTIVATION_SOIL_CASING.get().asItem(), 16)
                .inputFluids(SolderingAlloy.getFluid(9_216))
                .inputFluids(DistilledWater.getFluid(64_000))
                .outputItems(MegaTreeFarmDefinition.MACHINE)
                .stationResearch(b -> b
                        .researchStack(HULL[UHV].asStack())
                        .CWUt(64, 128_000)
                        .EUt(VA[UHV]))
                .duration(20 * 60)
                .EUt(VA[UHV])
                .save(provider);
    }

    private static void addMegaTreeFarmCasingRecipes(Consumer<FinishedRecipe> provider) {
        // B: Reinforced Stone Brick Casing (EV, 240t)
        ASSEMBLER_RECIPES.recipeBuilder(TSTModern.id("assembler/reinforced_stone_brick_casing"))
                .inputItems(frameGt, GTMaterials.Palladium, 1)
                .inputItems(Items.STONE_BRICKS, 1)
                .inputFluids(GTMaterials.Concrete.getFluid(9216))
                .outputItems(TSTBlocks.REINFORCED_STONE_BRICK_CASING, 1)
                .duration(240)
                .EUt(VA[EV])
                .save(provider);

        // H/Q/R: Sterile Casing (IV, 200t)
        ASSEMBLER_RECIPES.recipeBuilder(TSTModern.id("assembler/sterile_casing"))
                .inputItems(frameGt, GTMaterials.SterlingSilver, 1)
                .inputItems(pipeTinyFluid, GTMaterials.Steel, 1)
                .inputItems(GTBlocks.COIL_CUPRONICKEL.asStack(1))
                .inputItems(GTItems.PLANT_BALL.asStack(4))
                .inputItems(Items.OAK_PLANKS, 8)
                .inputFluids(DistilledWater.getFluid(2000))
                .outputItems(TSTBlocks.STERILE_CASING, 1)
                .duration(200)
                .EUt(VA[IV])
                .save(provider);

        // h/q/r: Aseptic Greenhouse Casing (Assembly Line, UV)
        ASSEMBLY_LINE_RECIPES.recipeBuilder(TSTModern.id("assembly_line/aseptic_greenhouse_casing"))
                .inputItems(TSTBlocks.STERILE_CASING.get().asItem(), 1)
                .inputItems(GTBlocks.COIL_NAQUADAH.asStack(1))
                .inputItems(frameGt, GTMaterials.SterlingSilver, 1)
                .inputItems(pipeLargeFluid, StainlessSteel, 4)
                .inputItems(plateDense, NaquadahAlloy, 6)
                .inputItems(CustomTags.UV_CIRCUITS, 2)
                .inputFluids(DistilledWater.getFluid(8000))
                .inputFluids(GTMaterials.Helium.getFluid(FluidStorageKeys.LIQUID, 64000))
                .outputItems(TSTBlocks.ASEPTIC_GREENHOUSE_CASING, 1)
                .duration(600)
                .EUt(VA[UV])
                .save(provider);

        // E: Advanced Radiation Proof Casing (LuV, 200t)
        ASSEMBLER_RECIPES.recipeBuilder(TSTModern.id("assembler/advanced_radiation_proof_casing"))
                .inputItems(CASING_TUNGSTENSTEEL_ROBUST.asStack())
                .inputItems(plate, NaquadahAlloy, 4)
                .inputItems(foil, GTMaterials.Europium, 4)
                .inputFluids(Lead.getFluid(864))
                .outputItems(TSTBlocks.ADVANCED_RADIATION_PROOF_CASING, 1)
                .duration(200)
                .EUt(VA[LuV])
                .save(provider);

        // I: Integral Framework UV Casing (UV, 200t)
        ASSEMBLER_RECIPES.recipeBuilder(TSTModern.id("assembler/integral_framework_uv_casing"))
                .inputItems(HULL[UV].asStack())
                .inputItems(plate, GTMaterials.HSSS, 4)
                .inputItems(gear, GTMaterials.Titanium, 2)
                .inputItems(wireGtQuadruple, GTMaterials.Naquadah, 4)
                .inputItems(CustomTags.UV_CIRCUITS, 2)
                .inputFluids(NaquadahAlloy.getFluid(576))
                .outputItems(TSTBlocks.INTEGRAL_FRAMEWORK_UV_CASING, 1)
                .duration(200)
                .EUt(VA[UV])
                .save(provider);

        // C: Composite Farm Casing (Assembly Line, UV)
        ASSEMBLY_LINE_RECIPES.recipeBuilder(TSTModern.id("assembly_line/composite_farm_casing"))
                .inputItems(TSTBlocks.REINFORCED_STONE_BRICK_CASING.get().asItem(), 1)
                .inputItems(pipeHugeFluid, GTMaterials.Polybenzimidazole, 4)
                .inputItems(pipeNormalFluid, NaquadahAlloy, 4)
                .inputItems(TSTBlocks.VENT_T2_CASING.get().asItem(), 1)
                .inputItems(plateDense, GTMaterials.Gold, 4)
                .inputItems(plateDense, GTMaterials.Bronze, 4)
                .inputItems(plateDense, GTMaterials.Lapis, 4)
                .inputItems(plateDense, GTMaterials.Tin, 4)
                .inputItems(gear, NaquadahAlloy, 4)
                .inputItems(ELECTRIC_PISTON_UV, 2)
                .inputItems(ELECTRIC_PUMP_UV, 2)
                .inputItems(CustomTags.ZPM_CIRCUITS, 4)
                .inputItems(CustomTags.UHV_CIRCUITS, 2)
                .inputItems(wireGtSingle, NaquadahAlloy, 18)
                .inputFluids(GTMaterials.Helium.getFluid(FluidStorageKeys.LIQUID, 16000))
                .inputFluids(SolderingAlloy.getFluid(864))
                .inputFluids(NaquadahAlloy.getFluid(144))
                .outputItems(TSTBlocks.COMPOSITE_FARM_CASING, 1)
                .duration(600)
                .EUt(VA[UV])
                .save(provider);

        // F: Radiant Naquadah Alloy Casing (LuV, 200t)
        ASSEMBLER_RECIPES.recipeBuilder(TSTModern.id("assembler/radiant_naquadah_alloy_casing"))
                .inputItems(CASING_TUNGSTENSTEEL_ROBUST.asStack())
                .inputItems(plate, NaquadahAlloy, 4)
                .inputItems(screw, GTMaterials.Europium, 4)
                .inputItems(Items.GLOWSTONE_DUST, 4)
                .inputFluids(SolderingAlloy.getFluid(576))
                .outputItems(TSTBlocks.RADIANT_NAQUADAH_ALLOY_CASING, 4)
                .duration(200)
                .EUt(VA[LuV])
                .save(provider);

        // J: Arcane Translucent Casing
        ASSEMBLER_RECIPES.recipeBuilder(TSTModern.id("assembler/arcane_translucent_casing"))
                .inputItems(GTBlocks.CASING_TEMPERED_GLASS.asStack())
                .inputItems(plate, NaquadahAlloy, 4)
                .inputFluids(SolderingAlloy.getFluid(288))
                .outputItems(TSTBlocks.ARCANE_TRANSLUCENT_CASING, 2)
                .duration(200)
                .EUt(VA[LuV])
                .save(provider);

        // K: Air Crystal Casing
        ASSEMBLER_RECIPES.recipeBuilder(TSTModern.id("assembler/air_crystal_casing"))
                .inputItems(TSTBlocks.RADIANT_NAQUADAH_ALLOY_CASING.get().asItem())
                .inputItems(dust, NetherQuartz, 4)
                .inputFluids(LiquidNetherAir.getFluid(1000))
                .outputItems(TSTBlocks.AIR_CRYSTAL_CASING, 2)
                .duration(200)
                .EUt(VA[ZPM])
                .save(provider);

        // L: Water Crystal Casing
        ASSEMBLER_RECIPES.recipeBuilder(TSTModern.id("assembler/water_crystal_casing"))
                .inputItems(TSTBlocks.RADIANT_NAQUADAH_ALLOY_CASING.get().asItem())
                .inputItems(dust, NetherQuartz, 4)
                .inputFluids(DistilledWater.getFluid(1000))
                .outputItems(TSTBlocks.WATER_CRYSTAL_CASING, 2)
                .duration(200)
                .EUt(VA[ZPM])
                .save(provider);

        // M: Earth Crystal Casing
        ASSEMBLER_RECIPES.recipeBuilder(TSTModern.id("assembler/earth_crystal_casing"))
                .inputItems(TSTBlocks.RADIANT_NAQUADAH_ALLOY_CASING.get().asItem())
                .inputItems(dust, NetherQuartz, 4)
                .inputItems(GTItems.FERTILIZER.asStack(4))
                .inputFluids(SolderingAlloy.getFluid(288))
                .outputItems(TSTBlocks.EARTH_CRYSTAL_CASING, 2)
                .duration(200)
                .EUt(VA[ZPM])
                .save(provider);

        // N: Cultivation Soil Casing
        ASSEMBLER_RECIPES.recipeBuilder(TSTModern.id("assembler/cultivation_soil_casing"))
                .inputItems(Items.DIRT, 4)
                .inputItems(GTItems.FERTILIZER.asStack(4))
                .inputItems(plate, StainlessSteel, 4)
                .inputFluids(DistilledWater.getFluid(1000))
                .outputItems(TSTBlocks.CULTIVATION_SOIL_CASING, 2)
                .duration(200)
                .EUt(VA[EV])
                .save(provider);
    }

    private static void addMegaTreeFarmRecipes(Consumer<FinishedRecipe> provider) {
        // Saplings select the tree and are not consumed. These are one-run TST
        // base yields; the machine applies the source tier multiplier at runtime.
        TSTRecipeTypes.TREE_GROWTH_SIMULATOR.recipeBuilder(TSTModern.id("tree_growth_simulator/oak"))
                .notConsumable(Items.OAK_SAPLING)
                .inputFluids(new FluidStack(Fluids.WATER, 1000))
                .outputItems(Items.OAK_LOG, MegaTreeFarmMachine.baseOutputAmount(MegaTreeFarmMachine.OutputMode.LOG))
                .outputItems(Items.OAK_SAPLING, MegaTreeFarmMachine.baseOutputAmount(MegaTreeFarmMachine.OutputMode.SAPLING))
                .outputItems(Items.OAK_LEAVES, MegaTreeFarmMachine.baseOutputAmount(MegaTreeFarmMachine.OutputMode.LEAVES))
                .outputItems(Items.APPLE, MegaTreeFarmMachine.baseOutputAmount(MegaTreeFarmMachine.OutputMode.FRUIT))
                .duration(100)
                .EUt(8)
                .save(provider);

        TSTRecipeTypes.TREE_GROWTH_SIMULATOR.recipeBuilder(TSTModern.id("tree_growth_simulator/birch"))
                .notConsumable(Items.BIRCH_SAPLING)
                .inputFluids(new FluidStack(Fluids.WATER, 1000))
                .outputItems(Items.BIRCH_LOG, MegaTreeFarmMachine.baseOutputAmount(MegaTreeFarmMachine.OutputMode.LOG))
                .outputItems(Items.BIRCH_SAPLING, MegaTreeFarmMachine.baseOutputAmount(MegaTreeFarmMachine.OutputMode.SAPLING))
                .outputItems(Items.BIRCH_LEAVES, MegaTreeFarmMachine.baseOutputAmount(MegaTreeFarmMachine.OutputMode.LEAVES))
                .duration(100)
                .EUt(8)
                .save(provider);

        TSTRecipeTypes.TREE_GROWTH_SIMULATOR.recipeBuilder(TSTModern.id("tree_growth_simulator/spruce"))
                .notConsumable(Items.SPRUCE_SAPLING)
                .inputFluids(new FluidStack(Fluids.WATER, 1000))
                .outputItems(Items.SPRUCE_LOG, MegaTreeFarmMachine.baseOutputAmount(MegaTreeFarmMachine.OutputMode.LOG))
                .outputItems(Items.SPRUCE_SAPLING, MegaTreeFarmMachine.baseOutputAmount(MegaTreeFarmMachine.OutputMode.SAPLING))
                .outputItems(Items.SPRUCE_LEAVES, MegaTreeFarmMachine.baseOutputAmount(MegaTreeFarmMachine.OutputMode.LEAVES))
                .duration(100)
                .EUt(8)
                .save(provider);

        TSTRecipeTypes.TREE_GROWTH_SIMULATOR.recipeBuilder(TSTModern.id("tree_growth_simulator/jungle"))
                .notConsumable(Items.JUNGLE_SAPLING)
                .inputFluids(new FluidStack(Fluids.WATER, 1000))
                .outputItems(Items.JUNGLE_LOG, MegaTreeFarmMachine.baseOutputAmount(MegaTreeFarmMachine.OutputMode.LOG))
                .outputItems(Items.JUNGLE_SAPLING, MegaTreeFarmMachine.baseOutputAmount(MegaTreeFarmMachine.OutputMode.SAPLING))
                .outputItems(Items.JUNGLE_LEAVES, MegaTreeFarmMachine.baseOutputAmount(MegaTreeFarmMachine.OutputMode.LEAVES))
                .outputItems(Items.COCOA_BEANS, MegaTreeFarmMachine.baseOutputAmount(MegaTreeFarmMachine.OutputMode.FRUIT))
                .duration(100)
                .EUt(8)
                .save(provider);

        TSTRecipeTypes.TREE_GROWTH_SIMULATOR.recipeBuilder(TSTModern.id("tree_growth_simulator/acacia"))
                .notConsumable(Items.ACACIA_SAPLING)
                .inputFluids(new FluidStack(Fluids.WATER, 1000))
                .outputItems(Items.ACACIA_LOG, MegaTreeFarmMachine.baseOutputAmount(MegaTreeFarmMachine.OutputMode.LOG))
                .outputItems(Items.ACACIA_SAPLING, MegaTreeFarmMachine.baseOutputAmount(MegaTreeFarmMachine.OutputMode.SAPLING))
                .outputItems(Items.ACACIA_LEAVES, MegaTreeFarmMachine.baseOutputAmount(MegaTreeFarmMachine.OutputMode.LEAVES))
                .duration(100)
                .EUt(8)
                .save(provider);

        TSTRecipeTypes.TREE_GROWTH_SIMULATOR.recipeBuilder(TSTModern.id("tree_growth_simulator/dark_oak"))
                .notConsumable(Items.DARK_OAK_SAPLING)
                .inputFluids(new FluidStack(Fluids.WATER, 1000))
                .outputItems(Items.DARK_OAK_LOG, MegaTreeFarmMachine.baseOutputAmount(MegaTreeFarmMachine.OutputMode.LOG))
                .outputItems(Items.DARK_OAK_SAPLING, MegaTreeFarmMachine.baseOutputAmount(MegaTreeFarmMachine.OutputMode.SAPLING))
                .outputItems(Items.DARK_OAK_LEAVES, MegaTreeFarmMachine.baseOutputAmount(MegaTreeFarmMachine.OutputMode.LEAVES))
                .outputItems(Items.APPLE, MegaTreeFarmMachine.baseOutputAmount(MegaTreeFarmMachine.OutputMode.FRUIT))
                .duration(100)
                .EUt(8)
                .save(provider);

        TSTRecipeTypes.TREE_GROWTH_SIMULATOR.recipeBuilder(TSTModern.id("tree_growth_simulator/mangrove"))
                .notConsumable(Items.MANGROVE_PROPAGULE)
                .inputFluids(new FluidStack(Fluids.WATER, 1000))
                .outputItems(Items.MANGROVE_LOG, MegaTreeFarmMachine.baseOutputAmount(MegaTreeFarmMachine.OutputMode.LOG))
                .outputItems(Items.MANGROVE_PROPAGULE, MegaTreeFarmMachine.baseOutputAmount(MegaTreeFarmMachine.OutputMode.SAPLING))
                .outputItems(Items.MANGROVE_LEAVES, MegaTreeFarmMachine.baseOutputAmount(MegaTreeFarmMachine.OutputMode.LEAVES))
                .duration(100)
                .EUt(8)
                .save(provider);

        TSTRecipeTypes.TREE_GROWTH_SIMULATOR.recipeBuilder(TSTModern.id("tree_growth_simulator/cherry"))
                .notConsumable(Items.CHERRY_SAPLING)
                .inputFluids(new FluidStack(Fluids.WATER, 1000))
                .outputItems(Items.CHERRY_LOG, MegaTreeFarmMachine.baseOutputAmount(MegaTreeFarmMachine.OutputMode.LOG))
                .outputItems(Items.CHERRY_SAPLING, MegaTreeFarmMachine.baseOutputAmount(MegaTreeFarmMachine.OutputMode.SAPLING))
                .outputItems(Items.CHERRY_LEAVES, MegaTreeFarmMachine.baseOutputAmount(MegaTreeFarmMachine.OutputMode.LEAVES))
                .duration(100)
                .EUt(8)
                .save(provider);

        TSTRecipeTypes.TREE_GROWTH_SIMULATOR.recipeBuilder(TSTModern.id("tree_growth_simulator/rubber"))
                .notConsumable(GTBlocks.RUBBER_SAPLING.get().asItem())
                .inputFluids(new FluidStack(Fluids.WATER, 1000))
                .outputItems(GTBlocks.RUBBER_LOG.get().asItem(), MegaTreeFarmMachine.baseOutputAmount(MegaTreeFarmMachine.OutputMode.LOG))
                .outputItems(GTBlocks.RUBBER_SAPLING.get().asItem(), MegaTreeFarmMachine.baseOutputAmount(MegaTreeFarmMachine.OutputMode.SAPLING))
                .outputItems(GTBlocks.RUBBER_LEAVES.get().asItem(), MegaTreeFarmMachine.baseOutputAmount(MegaTreeFarmMachine.OutputMode.LEAVES))
                .outputItems(GTItems.STICKY_RESIN.asStack(MegaTreeFarmMachine.baseOutputAmount(MegaTreeFarmMachine.OutputMode.FRUIT)))
                .duration(100)
                .EUt(8)
                .save(provider);

        // One aquatic recipe run is rolled once per actual hatch parallel.
        TSTRecipeTypes.AQUATIC_ZONE_SIMULATOR.recipeBuilder(TSTModern.id("aquatic_zone_simulator/marine_resources"))
                .inputFluids(new FluidStack(Fluids.WATER, 10_000))
                .chancedOutput(new ItemStack(Items.COD, 16), 1_392, 0)
                .chancedOutput(new ItemStack(Items.SALMON, 16), 1_392, 0)
                .chancedOutput(new ItemStack(Items.PUFFERFISH, 8), 696, 0)
                .chancedOutput(new ItemStack(Items.TROPICAL_FISH, 8), 696, 0)
                .chancedOutput(new ItemStack(Items.INK_SAC, 16), 1_392, 0)
                .chancedOutput(new ItemStack(Items.PRISMARINE_SHARD, 8), 696, 0)
                .chancedOutput(new ItemStack(Items.PRISMARINE_CRYSTALS, 8), 696, 0)
                .chancedOutput(new ItemStack(Items.KELP, 32), 2_784, 0)
                .chancedOutput(new ItemStack(Items.NAUTILUS_SHELL, 2), 174, 0)
                .duration(100)
                .EUt(8)
                .save(provider);
    }

}
