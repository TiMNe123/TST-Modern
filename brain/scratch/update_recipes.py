with open('src/main/java/com/tstmodern/data/TSTRecipes.java', 'r', encoding='utf-8') as f:
    c = f.read()

# Fix imports
imports = """import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.foil;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.screw;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.gear;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.wireGtSingle;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.wireGtQuadruple;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.pipeTinyFluid;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.pipeNormalFluid;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.pipeHugeFluid;
import static com.gregtechceu.gtceu.common.data.GTItems.ELECTRIC_PISTON_UV;
import static com.gregtechceu.gtceu.common.data.GTItems.ELECTRIC_PUMP_UV;
"""

# Remove wrong wireGt01 imports if any
c = c.replace('import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.wireGt01;\n', '')
c = c.replace('import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.wireGt04;\n', '')
c = c.replace('import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.foil;\n', '')
c = c.replace('import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.screw;\n', '')
c = c.replace('import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.gear;\n', '')
c = c.replace('import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.pipeTinyFluid;\n', '')
c = c.replace('import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.pipeNormalFluid;\n', '')
c = c.replace('import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.pipeHugeFluid;\n', '')
c = c.replace('import static com.gregtechceu.gtceu.common.data.GTItems.ELECTRIC_PISTON_UV;\n', '')
c = c.replace('import static com.gregtechceu.gtceu.common.data.GTItems.ELECTRIC_PUMP_UV;\n', '')

c = c.replace('import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.plateDense;\n', 'import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.plateDense;\n' + imports)

start_mark = '    private static void addMegaTreeFarmCasingRecipes(Consumer<FinishedRecipe> provider) {'
end_mark = '    public static void addMegaTreeFarmRecipes(Consumer<FinishedRecipe> provider) {'

start_idx = c.find(start_mark)
end_idx = c.find(end_mark)

if start_idx != -1 and end_idx != -1:
    new_recipes = """    private static void addMegaTreeFarmCasingRecipes(Consumer<FinishedRecipe> provider) {
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

"""
    c = c[:start_idx] + new_recipes + c[end_idx:]

with open('src/main/java/com/tstmodern/data/TSTRecipes.java', 'w', encoding='utf-8') as f:
    f.write(c)

print('Updated TSTRecipes.java successfully!')
