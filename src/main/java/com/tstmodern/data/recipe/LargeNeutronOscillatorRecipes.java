package com.tstmodern.data.recipe;

import static com.gregtechceu.gtceu.api.GTValues.EV;
import static com.gregtechceu.gtceu.api.GTValues.LuV;
import static com.gregtechceu.gtceu.api.GTValues.UEV;
import static com.gregtechceu.gtceu.api.GTValues.UIV;
import static com.gregtechceu.gtceu.api.GTValues.UV;
import static com.gregtechceu.gtceu.api.GTValues.VA;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.dust;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.frameGt;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.pipeLargeFluid;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.plate;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.plateDouble;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.rotor;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.wireFine;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.wireGtDouble;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.wireGtSingle;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ASSEMBLER_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ASSEMBLY_LINE_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.BLAST_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.FUSION_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.MIXER_RECIPES;

import java.util.function.Consumer;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKeys;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.tstmodern.TSTModern;
import com.tstmodern.registry.TSTBlocks;
import com.tstmodern.registry.TSTItems;
import com.tstmodern.registry.TSTMaterials;
import com.tstmodern.registry.TSTRecipeTypes;
import com.tstmodern.registry.machine.LargeNeutronOscillatorDefinition;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;

public final class LargeNeutronOscillatorRecipes {
    private LargeNeutronOscillatorRecipes() {}

    public static void register(Consumer<FinishedRecipe> provider) {
        registerCasings(provider);
        registerConstructionDependencies(provider);
        registerCompactFusionCoil(provider);
        registerControllerRecipe(provider);
        registerNeutronActivatorRecipes(provider);
    }

    private static void registerConstructionDependencies(Consumer<FinishedRecipe> provider) {
        MIXER_RECIPES.recipeBuilder(TSTModern.id("mixer/black_titanium_premix"))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Titanium, 55))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Lanthanum, 12))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Tungsten, 8))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Cobalt, 6))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Manganese, 4))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Phosphorus, 4))
                .outputItems(ChemicalHelper.get(dust, TSTMaterials.BLACK_TITANIUM_PREMIX, 1))
                .duration(890)
                .EUt(VA[LuV])
                .save(provider);

        BLAST_RECIPES.recipeBuilder(TSTModern.id("blast/black_titanium"))
                .inputItems(ChemicalHelper.get(dust, TSTMaterials.BLACK_TITANIUM_PREMIX, 1))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Palladium, 4))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Niobium, 2))
                .inputFluids(GTMaterials.Argon.getFluid(5000))
                .outputFluids(TSTMaterials.BLACK_TITANIUM.getFluid(14400))
                .blastFurnaceTemp(7776)
                .duration(1000)
                .EUt(VA[UV])
                .save(provider);

        MIXER_RECIPES.recipeBuilder(TSTModern.id("mixer/dalisenite"))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Titanium, 14))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Tungsten, 10))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.NiobiumTitanium, 9))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.RhodiumPlatedPalladium, 8))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.NaquadahAlloy, 7))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Erbium, 3))
                .outputItems(ChemicalHelper.get(dust, TSTMaterials.DALISENITE, 51))
                .duration(510)
                .EUt(VA[LuV])
                .save(provider);

        BLAST_RECIPES.recipeBuilder(TSTModern.id("blast/dalisenite"))
                .inputItems(ChemicalHelper.get(dust, TSTMaterials.DALISENITE, 51))
                .outputFluids(TSTMaterials.DALISENITE.getFluid(7344))
                .blastFurnaceTemp(8700)
                .duration(800)
                .EUt(491520)
                .save(provider);

        FUSION_RECIPES.recipeBuilder(TSTModern.id("fusion/metastable_oganesson"))
                .inputFluids(GTMaterials.Copper.getFluid(FluidStorageKeys.PLASMA, 576))
                .inputFluids(GTMaterials.Oganesson.getFluid(1000))
                .outputFluids(TSTMaterials.METASTABLE_OGANESSON.getFluid(576))
                .fusionStartEU(6_000_000_000L)
                .duration(100)
                .EUt(VA[UEV])
                .save(provider);

        ASSEMBLY_LINE_RECIPES.recipeBuilder(TSTModern.id("assembly_line/high_computation_station_t5"))
                .inputItems(CustomTags.UHV_CIRCUITS, 2)
                .inputItems(GTItems.QUBIT_CENTRAL_PROCESSING_UNIT.asStack(8))
                .inputItems(TSTCircuitTags.get(UEV), 1)
                .inputItems(ChemicalHelper.get(rotor, GTMaterials.TungstenCarbide, 2))
                .inputFluids(GTMaterials.Titanium.getFluid(1728))
                .inputFluids(GTMaterials.NaquadahAlloy.getFluid(1152))
                .inputFluids(GTMaterials.RhodiumPlatedPalladium.getFluid(576))
                .inputFluids(TSTMaterials.DALISENITE.getFluid(288))
                .outputItems(new ItemStack(TSTItems.HIGH_COMPUTATION_STATION_T5.get()))
                .duration(100)
                .EUt(1_966_080)
                .stationResearch(b -> b
                        .researchStack(GTItems.QUBIT_CENTRAL_PROCESSING_UNIT.asStack())
                        .researchId("high_computation_station_t5")
                        .dataStack(GTItems.TOOL_DATA_ORB.asStack())
                        .CWUt(64, 48_000)
                        .EUt(VA[UV]))
                .save(provider);

        ASSEMBLY_LINE_RECIPES.recipeBuilder(TSTModern.id("assembly_line/neutron_activator_component"))
                .inputItems(CustomTags.LuV_CIRCUITS, 2)
                .inputItems(GTItems.EMITTER_EV.asStack(2))
                .inputItems(GTItems.NEUTRON_REFLECTOR.asStack())
                .inputFluids(GTMaterials.StainlessSteel.getFluid(576))
                .inputFluids(GTMaterials.TungstenCarbide.getFluid(144))
                .outputItems(new ItemStack(TSTItems.NEUTRON_ACTIVATOR_COMPONENT.get()))
                .duration(100)
                .EUt(7680)
                .stationResearch(b -> b
                        .researchStack(GTItems.NEUTRON_REFLECTOR.asStack())
                        .researchId("neutron_activator_component")
                        .dataStack(GTItems.TOOL_DATA_ORB.asStack())
                        .CWUt(16, 12_000)
                        .EUt(VA[LuV]))
                .save(provider);
    }

    private static void registerCasings(Consumer<FinishedRecipe> provider) {
        // High Power Casing (sBlockCasingsTT:0) -> Assembler
        ASSEMBLER_RECIPES.recipeBuilder(TSTModern.id("assembler/high_power_casing"))
                .inputItems(ChemicalHelper.get(frameGt, GTMaterials.Iridium, 1))
                .inputItems(ChemicalHelper.get(plateDouble, GTMaterials.Iridium, 6))
                .inputItems(CustomTags.LuV_CIRCUITS, 1)
                .inputItems(ChemicalHelper.get(wireFine, GTMaterials.Cobalt, 16))
                .inputItems(ChemicalHelper.get(wireFine, GTMaterials.Copper, 16))
                .inputItems(ChemicalHelper.get(wireGtDouble, GTMaterials.NiobiumTitanium, 2))
                .inputFluids(GTMaterials.TungstenSteel.getFluid(576))
                .outputItems(new ItemStack(TSTBlocks.HIGH_POWER_CASING.get()))
                .duration(100)
                .EUt(VA[LuV])
                .save(provider);

        // Speeding Pipe Casing (Loaders.speedingPipe:0) -> Assembler (RecipeLoader2.java line 68)
        ASSEMBLER_RECIPES.recipeBuilder(TSTModern.id("assembler/speeding_pipe_casing"))
                .inputItems(ChemicalHelper.get(pipeLargeFluid, GTMaterials.StainlessSteel, 1))
                .inputItems(ChemicalHelper.get(frameGt, GTMaterials.BlueAlloy, 1))
                .inputItems(ChemicalHelper.get(wireGtSingle, GTMaterials.MercuryBariumCalciumCuprate, 32))
                .inputItems(ChemicalHelper.get(plate, GTMaterials.Beryllium, 32))
                .inputItems(CustomTags.IV_CIRCUITS, 1)
                .outputItems(new ItemStack(TSTBlocks.SPEEDING_PIPE_CASING.get()))
                .duration(300)
                .EUt(VA[EV])
                .save(provider);
    }

    private static void registerCompactFusionCoil(Consumer<FinishedRecipe> provider) {
        ASSEMBLY_LINE_RECIPES.recipeBuilder(TSTModern.id("compact_fusion_coil_t3"))
                .inputItems(GTBlocks.FUSION_CASING.asStack(3))
                .inputItems(new ItemStack(TSTItems.HIGH_COMPUTATION_STATION_T5.get()))
                .inputItems(GTItems.ENERGY_CLUSTER.asStack())
                .inputFluids(GTMaterials.NaquadahAlloy.getFluid(1152))
                .inputFluids(GTMaterials.RhodiumPlatedPalladium.getFluid(144))
                .outputItems(new ItemStack(TSTBlocks.COMPACT_FUSION_COIL_T3.get()))
                .duration(2000)
                .EUt(VA[UV])
                .stationResearch(b -> b
                        .researchStack(GTBlocks.FUSION_CASING_MK3.asStack())
                        .researchId("compact_fusion_coil_t3")
                        .dataStack(GTItems.TOOL_DATA_ORB.asStack())
                        .CWUt(32, 48_000)
                        .EUt(VA[UV]))
                .save(provider);
    }

    private static void registerControllerRecipe(Consumer<FinishedRecipe> provider) {
        // UIV Researchable Assembly Line controller recipe (precise assembler port)
        ASSEMBLY_LINE_RECIPES.recipeBuilder(TSTModern.id("large_neutron_oscillator"))
                .inputItems(new ItemStack(TSTItems.NEUTRON_ACTIVATOR_COMPONENT.get(), 64))
                .inputItems(new ItemStack(TSTItems.HIGH_COMPUTATION_STATION_T5.get(), 64))
                .inputItems(new ItemStack(TSTBlocks.COMPACT_FUSION_COIL_T3.get(), 8))
                .inputItems(TSTCircuitTags.get(UIV), 8)
                .inputFluids(TSTMaterials.BLACK_TITANIUM.getFluid(74016))
                .inputFluids(TSTMaterials.METASTABLE_OGANESSON.getFluid(74016))
                .inputFluids(TSTMaterials.DALISENITE.getFluid(74016))
                .outputItems(LargeNeutronOscillatorDefinition.LARGE_NEUTRON_OSCILLATOR.asStack())
                .duration(12000)
                .EUt(VA[UEV])
                .stationResearch(b -> b
                        .researchStack(new ItemStack(TSTBlocks.HIGH_POWER_CASING.get()))
                        .researchId("large_neutron_oscillator")
                        .dataStack(GTItems.TOOL_DATA_MODULE.asStack())
                        .CWUt(64, 144_000)
                        .EUt(VA[UEV]))
                .save(provider);
    }

    private static void registerNeutronActivatorRecipes(Consumer<FinishedRecipe> provider) {
        // 1. na_eu_thorium_fuel
        TSTRecipeTypes.NEUTRON_ACTIVATOR.recipeBuilder(TSTModern.id("na_eu_thorium_fuel"))
                .inputFluids(TSTMaterials.THORIUM_BASED_LIQUID_FUEL_EXCITED.getFluid(200))
                .outputFluids(TSTMaterials.THORIUM_BASED_LIQUID_FUEL_DEPLETED.getFluid(200))
                .duration(10000)
                .EUt(490000)
                .save(provider);

        // 2. na_eu_uranium_fuel
        TSTRecipeTypes.NEUTRON_ACTIVATOR.recipeBuilder(TSTModern.id("na_eu_uranium_fuel"))
                .notConsumable(ChemicalHelper.get(plate, GTMaterials.Tungsten, 1))
                .inputFluids(TSTMaterials.URANIUM_BASED_LIQUID_FUEL.getFluid(100))
                .outputFluids(TSTMaterials.URANIUM_BASED_LIQUID_FUEL_EXCITED.getFluid(100))
                .duration(80)
                .EUt(302500)
                .save(provider);

        // 3. na_eu_plutonium_fuel
        TSTRecipeTypes.NEUTRON_ACTIVATOR.recipeBuilder(TSTModern.id("na_eu_plutonium_fuel"))
                .notConsumable(ChemicalHelper.get(plate, GTMaterials.Tritanium, 1))
                .inputFluids(TSTMaterials.PLUTONIUM_BASED_LIQUID_FUEL.getFluid(100))
                .outputFluids(TSTMaterials.PLUTONIUM_BASED_LIQUID_FUEL_EXCITED.getFluid(100))
                .duration(80)
                .EUt(360000)
                .save(provider);

        // 4. na_eu_energised_tesseract_mkv
        TSTRecipeTypes.NEUTRON_ACTIVATOR.recipeBuilder(TSTModern.id("na_eu_energised_tesseract_mkv"))
                .inputItems(new ItemStack(TSTItems.TESSERACT.get()))
                .inputFluids(TSTMaterials.NAQUADAH_BASED_FUEL_MKV.getFluid(64))
                .outputItems(new ItemStack(TSTItems.ENERGISED_TESSERACT.get()))
                .outputFluids(TSTMaterials.NAQUADAH_BASED_FUEL_MKV_DEPLETED.getFluid(64))
                .duration(328000)
                .EUt(1210000)
                .save(provider);

        // 5. na_eu_energised_tesseract_mkvi
        TSTRecipeTypes.NEUTRON_ACTIVATOR.recipeBuilder(TSTModern.id("na_eu_energised_tesseract_mkvi"))
                .inputItems(new ItemStack(TSTItems.TESSERACT.get()))
                .inputFluids(TSTMaterials.NAQUADAH_BASED_FUEL_MKVI.getFluid(64))
                .outputItems(new ItemStack(TSTItems.ENERGISED_TESSERACT.get()))
                .outputFluids(TSTMaterials.NAQUADAH_BASED_FUEL_MKVI_DEPLETED.getFluid(64))
                .duration(492000)
                .EUt(1210000)
                .save(provider);

        // 6. na_eu_activate_naquadah
        TSTRecipeTypes.NEUTRON_ACTIVATOR.recipeBuilder(TSTModern.id("na_eu_activate_naquadah"))
                .inputItems(ChemicalHelper.get(dust, TSTMaterials.INERT_NAQUADAH, 96))
                .inputFluids(GTMaterials.Nickel.getFluid(FluidStorageKeys.PLASMA, 2304))
                .outputItems(ChemicalHelper.get(dust, GTMaterials.Nickel, 16))
                .outputFluids(GTMaterials.Naquadah.getFluid(9216))
                .duration(2000)
                .EUt(360000)
                .save(provider);

        // 7. na_eu_activate_enriched_naquadah
        TSTRecipeTypes.NEUTRON_ACTIVATOR.recipeBuilder(TSTModern.id("na_eu_activate_enriched_naquadah"))
                .inputItems(ChemicalHelper.get(dust, TSTMaterials.INERT_ENRICHED_NAQUADAH, 96))
                .inputFluids(GTMaterials.Titanium.getFluid(FluidStorageKeys.PLASMA, 2304))
                .outputItems(ChemicalHelper.get(dust, GTMaterials.Titanium, 16))
                .outputFluids(GTMaterials.NaquadahEnriched.getFluid(9216))
                .duration(2000)
                .EUt(810000)
                .save(provider);

        // 8. na_eu_activate_naquadria
        TSTRecipeTypes.NEUTRON_ACTIVATOR.recipeBuilder(TSTModern.id("na_eu_activate_naquadria"))
                .inputItems(ChemicalHelper.get(dust, TSTMaterials.INERT_NAQUADRIA, 96))
                .inputFluids(GTMaterials.Americium.getFluid(FluidStorageKeys.PLASMA, 2304))
                .outputItems(ChemicalHelper.get(dust, GTMaterials.Americium, 16))
                .outputFluids(GTMaterials.Naquadria.getFluid(9216))
                .duration(2000)
                .EUt(1210000)
                .save(provider);

        // 9. na_eu_naquadah_adamantium_separation
        TSTRecipeTypes.NEUTRON_ACTIVATOR.recipeBuilder(TSTModern.id("na_eu_naquadah_adamantium_separation"))
                .inputFluids(TSTMaterials.NAQUADAH_ADAMANTIUM_SOLUTION.getFluid(3000))
                .outputItems(ChemicalHelper.get(dust, TSTMaterials.ADAMANTINE, 4))
                .outputItems(ChemicalHelper.get(dust, TSTMaterials.NAQUADAH_EARTH, 2))
                .outputItems(ChemicalHelper.get(dust, TSTMaterials.CONCENTRATED_ENRICHED_NAQUADAH_SLUDGE, 1))
                .outputFluids(TSTMaterials.NAQUADAH_RICH_SOLUTION.getFluid(2000))
                .duration(100)
                .EUt(52900)
                .save(provider);

        // 10. na_eu_enriched_naquadah_sludge_treatment
        TSTRecipeTypes.NEUTRON_ACTIVATOR.recipeBuilder(TSTModern.id("na_eu_enriched_naquadah_sludge_treatment"))
                .inputItems(ChemicalHelper.get(dust, TSTMaterials.CONCENTRATED_ENRICHED_NAQUADAH_SLUDGE, 16))
                .outputItems(ChemicalHelper.get(dust, TSTMaterials.ENRICHED_NAQUADAH_SULPHATE, 165))
                .outputItems(ChemicalHelper.get(dust, TSTMaterials.SODIUM_SULFATE, 140))
                .outputItems(ChemicalHelper.get(dust, TSTMaterials.LOW_QUALITY_NAQUADRIA_SULPHATE, 2))
                .duration(120)
                .EUt(230400)
                .save(provider);

        // 11. na_eu_naquadria_sulphate_purification
        TSTRecipeTypes.NEUTRON_ACTIVATOR.recipeBuilder(TSTModern.id("na_eu_naquadria_sulphate_purification"))
                .inputFluids(TSTMaterials.NAQUADRIA_RICH_SOLUTION.getFluid(9000))
                .outputItems(ChemicalHelper.get(dust, TSTMaterials.NAQUADRIA_SULPHATE, 44))
                .outputItems(ChemicalHelper.get(dust, TSTMaterials.LOW_QUALITY_NAQUADRIA_SULPHATE, 6))
                .duration(100)
                .EUt(1210000)
                .save(provider);

        // 12. Metastable Oganesson -> Oganesson
        TSTRecipeTypes.NEUTRON_ACTIVATOR.recipeBuilder(TSTModern.id("na_eu_metastable_oganesson"))
                .inputItems(ChemicalHelper.get(dust, TSTMaterials.METASTABLE_OGANESSON, 1))
                .outputFluids(GTMaterials.Oganesson.getFluid(250))
                .duration(2000)
                .EUt(1210000)
                .save(provider);
    }
}
