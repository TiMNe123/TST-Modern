package com.tstmodern.data.recipe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

class LargeNeutronOscillatorRecipesContractTest {
    private static final Path SOURCE = Path.of(
            "src/main/java/com/tstmodern/data/recipe/LargeNeutronOscillatorRecipes.java");
    private static final List<String> IDS = List.of(
            "mixer/black_titanium_premix",
            "blast/black_titanium",
            "mixer/dalisenite",
            "blast/dalisenite",
            "fusion/potassium_lithium_to_titanium_plasma",
            "fusion/iron_lithium_to_copper_plasma",
            "fusion/metastable_oganesson",
            "assembly_line/high_computation_station_t5",
            "assembly_line/neutron_activator_component",
            "assembler/high_power_casing",
            "assembler/speeding_pipe_casing",
            "compact_fusion_coil_t3",
            "large_neutron_oscillator",
            "na_eu_thorium_fuel",
            "na_eu_uranium_fuel",
            "na_eu_plutonium_fuel",
            "na_eu_energised_tesseract_mkv",
            "na_eu_energised_tesseract_mkvi",
            "na_eu_activate_naquadah",
            "na_eu_activate_enriched_naquadah",
            "na_eu_activate_naquadria",
            "na_eu_naquadah_adamantium_separation",
            "na_eu_enriched_naquadah_sludge_treatment",
            "na_eu_naquadria_sulphate_purification",
            "na_eu_metastable_oganesson");

    @Test
    void registersOnlyTheApprovedClosedRecipeSet() throws IOException {
        String source = Files.readString(SOURCE);
        Matcher matcher = Pattern.compile("recipeBuilder\\((?:TSTModern\\.id\\(\"([^\"]+)\"\\)|\"([^\"]+)\")\\)").matcher(source);
        var actual = new java.util.ArrayList<String>();
        while (matcher.find()) {
            actual.add(matcher.group(1) != null ? matcher.group(1) : matcher.group(2));
        }
        assertEquals(IDS, actual);
    }

    @Test
    void everyRecipeUsesTheTstmodernNamespace() throws IOException {
        assertTrue(!Files.readString(SOURCE).contains(".recipeBuilder(\""),
                "String recipeBuilder overload silently registers recipes under gtceu");
    }

    @Test
    void casingRecipesPreserveAuditedInputs() throws IOException {
        String source = Files.readString(SOURCE);
        int highPowerStart = source.indexOf("assembler/high_power_casing");
        int highPowerEnd = source.indexOf(".save(provider);", highPowerStart);
        String highPower = source.substring(highPowerStart, highPowerEnd);
        assertContainsAll(highPower,
                "frameGt, GTMaterials.Iridium, 1",
                "plateDouble, GTMaterials.Iridium, 6",
                "CustomTags.LuV_CIRCUITS, 1",
                "wireFine, GTMaterials.Cobalt, 16",
                "wireFine, GTMaterials.Copper, 16",
                "wireGtDouble, GTMaterials.NiobiumTitanium, 2",
                "GTMaterials.TungstenSteel.getFluid(576)",
                ".duration(100)",
                ".EUt(VA[LuV])");

        int speedingStart = source.indexOf("assembler/speeding_pipe_casing");
        int speedingEnd = source.indexOf(".save(provider);", speedingStart);
        String speeding = source.substring(speedingStart, speedingEnd);
        assertContainsAll(speeding,
                "pipeLargeFluid, GTMaterials.StainlessSteel, 1",
                "frameGt, GTMaterials.BlueAlloy, 1",
                "wireGtSingle, GTMaterials.MercuryBariumCalciumCuprate, 32",
                "plate, GTMaterials.Beryllium, 32",
                "CustomTags.IV_CIRCUITS, 1",
                ".duration(300)",
                ".EUt(VA[EV])");
    }

    @Test
    void constructionDependenciesHaveSurvivalRecipes() throws IOException {
        assertContainsAll(recipeBlock("mixer/black_titanium_premix"),
                "GTMaterials.Titanium, 55", "GTMaterials.Lanthanum, 12",
                "GTMaterials.Tungsten, 8", "GTMaterials.Cobalt, 6",
                "GTMaterials.Manganese, 4", "GTMaterials.Phosphorus, 4",
                "TSTMaterials.BLACK_TITANIUM_PREMIX");
        assertContainsAll(recipeBlock("blast/black_titanium"),
                "TSTMaterials.BLACK_TITANIUM_PREMIX", "GTMaterials.Palladium, 4",
                "GTMaterials.Niobium, 2", "GTMaterials.Argon.getFluid(5000)",
                "TSTMaterials.BLACK_TITANIUM.getFluid(14400)", ".blastFurnaceTemp(7776)");
        assertContainsAll(recipeBlock("mixer/dalisenite"),
                "GTMaterials.Titanium, 14", "GTMaterials.Tungsten, 10",
                "GTMaterials.NiobiumTitanium, 9", "GTMaterials.RhodiumPlatedPalladium, 8",
                "GTMaterials.NaquadahAlloy, 7", "GTMaterials.Erbium, 3",
                "TSTMaterials.DALISENITE, 51");
        assertContainsAll(recipeBlock("blast/dalisenite"),
                "TSTMaterials.DALISENITE, 51", "TSTMaterials.DALISENITE.getFluid(7344)",
                ".blastFurnaceTemp(8700)", ".duration(800)", ".EUt(491520)");
        assertContainsAll(recipeBlock("fusion/metastable_oganesson"),
                "GTMaterials.Copper.getFluid(FluidStorageKeys.PLASMA, 576)",
                "GTMaterials.Oganesson.getFluid(1000)",
                "TSTMaterials.METASTABLE_OGANESSON.getFluid(576)",
                ".fusionStartEU(6_000_000_000L)", ".duration(100)", ".EUt(VA[UEV])");

        assertContainsAll(recipeBlock("fusion/potassium_lithium_to_titanium_plasma"),
                "GTMaterials.Potassium.getFluid(16)",
                "GTMaterials.Lithium.getFluid(16)",
                "GTMaterials.Titanium.getFluid(FluidStorageKeys.PLASMA, 16)",
                ".fusionStartEU(640_000_000L)", ".duration(10)", ".EUt(VA[UV])");
        assertContainsAll(recipeBlock("fusion/iron_lithium_to_copper_plasma"),
                "GTMaterials.Iron.getFluid(16)",
                "GTMaterials.Lithium.getFluid(16)",
                "GTMaterials.Copper.getFluid(FluidStorageKeys.PLASMA, 16)",
                ".fusionStartEU(640_000_000L)", ".duration(10)", ".EUt(VA[UV])");

        assertContainsAll(recipeBlock("assembly_line/high_computation_station_t5"),
                "CustomTags.UHV_CIRCUITS, 2", "GTItems.QUBIT_CENTRAL_PROCESSING_UNIT.asStack(8)",
                "TSTCircuitTags.get(UEV), 1", "rotor, GTMaterials.TungstenCarbide, 2",
                "GTMaterials.Titanium.getFluid(1728)", "GTMaterials.NaquadahAlloy.getFluid(1152)",
                "GTMaterials.RhodiumPlatedPalladium.getFluid(576)", "TSTMaterials.DALISENITE.getFluid(288)",
                "TSTItems.HIGH_COMPUTATION_STATION_T5");
        assertContainsAll(recipeBlock("assembly_line/neutron_activator_component"),
                "CustomTags.LuV_CIRCUITS, 2", "GTItems.EMITTER_EV.asStack(2)",
                "GTItems.NEUTRON_REFLECTOR.asStack()", "GTMaterials.StainlessSteel.getFluid(576)",
                "GTMaterials.TungstenCarbide.getFluid(144)", "TSTItems.NEUTRON_ACTIVATOR_COMPONENT",
                ".duration(100)", ".EUt(7680)");
    }

    @Test
    void compactFusionCoilT3KeepsTheSourceIdentityWithApprovedNativeInputs() throws IOException {
        String recipe = recipeBlock("compact_fusion_coil_t3");
        assertContainsAll(recipe,
                "GTBlocks.FUSION_CASING.asStack(3)",
                "new ItemStack(TSTItems.HIGH_COMPUTATION_STATION_T5.get())",
                "GTItems.ENERGY_CLUSTER.asStack()",
                "GTMaterials.NaquadahAlloy.getFluid(1152)",
                "GTMaterials.RhodiumPlatedPalladium.getFluid(144)",
                "new ItemStack(TSTBlocks.COMPACT_FUSION_COIL_T3.get())",
                ".researchId(\"compact_fusion_coil_t3\")",
                ".dataStack(GTItems.TOOL_DATA_ORB.asStack())",
                ".duration(2000)",
                ".EUt(VA[UV])");
    }

    @Test
    void controllerRecipeUsesApprovedInputsAndResearch() throws IOException {
        String recipe = recipeBlock("large_neutron_oscillator");
        assertContainsAll(recipe,
                "new ItemStack(TSTItems.NEUTRON_ACTIVATOR_COMPONENT.get(), 64)",
                "new ItemStack(TSTItems.HIGH_COMPUTATION_STATION_T5.get(), 64)",
                "new ItemStack(TSTBlocks.COMPACT_FUSION_COIL_T3.get(), 8)",
                "TSTCircuitTags.get(UIV), 8",
                "TSTMaterials.BLACK_TITANIUM.getFluid(74016)",
                "TSTMaterials.METASTABLE_OGANESSON.getFluid(74016)",
                "TSTMaterials.DALISENITE.getFluid(74016)",
                ".researchId(\"large_neutron_oscillator\")",
                ".dataStack(GTItems.TOOL_DATA_MODULE.asStack())",
                ".duration(12000)",
                ".EUt(VA[UEV])");
    }

    @Test
    void fuelAndTesseractRecipesUseApprovedMaterials() throws IOException {
        assertContainsAll(recipeBlock("na_eu_thorium_fuel"),
                "TSTMaterials.THORIUM_BASED_LIQUID_FUEL_EXCITED.getFluid(200)",
                "TSTMaterials.THORIUM_BASED_LIQUID_FUEL_DEPLETED.getFluid(200)");
        assertContainsAll(recipeBlock("na_eu_uranium_fuel"),
                ".notConsumable(ChemicalHelper.get(plate, GTMaterials.Tungsten, 1))",
                "TSTMaterials.URANIUM_BASED_LIQUID_FUEL.getFluid(100)",
                "TSTMaterials.URANIUM_BASED_LIQUID_FUEL_EXCITED.getFluid(100)");
        assertContainsAll(recipeBlock("na_eu_plutonium_fuel"),
                ".notConsumable(ChemicalHelper.get(plate, GTMaterials.Tritanium, 1))",
                "TSTMaterials.PLUTONIUM_BASED_LIQUID_FUEL.getFluid(100)",
                "TSTMaterials.PLUTONIUM_BASED_LIQUID_FUEL_EXCITED.getFluid(100)");
        assertContainsAll(recipeBlock("na_eu_energised_tesseract_mkv"),
                "new ItemStack(TSTItems.TESSERACT.get())", "new ItemStack(TSTItems.ENERGISED_TESSERACT.get())",
                "TSTMaterials.NAQUADAH_BASED_FUEL_MKV.getFluid(64)",
                "TSTMaterials.NAQUADAH_BASED_FUEL_MKV_DEPLETED.getFluid(64)");
        assertContainsAll(recipeBlock("na_eu_energised_tesseract_mkvi"),
                "new ItemStack(TSTItems.TESSERACT.get())", "new ItemStack(TSTItems.ENERGISED_TESSERACT.get())",
                "TSTMaterials.NAQUADAH_BASED_FUEL_MKVI.getFluid(64)",
                "TSTMaterials.NAQUADAH_BASED_FUEL_MKVI_DEPLETED.getFluid(64)");
    }

    @Test
    void naquadahChainUsesApprovedIntermediateMaterials() throws IOException {
        assertContainsAll(recipeBlock("na_eu_activate_naquadah"),
                "TSTMaterials.INERT_NAQUADAH, 96", "GTMaterials.Naquadah.getFluid(9216)");
        assertContainsAll(recipeBlock("na_eu_activate_enriched_naquadah"),
                "TSTMaterials.INERT_ENRICHED_NAQUADAH, 96", "GTMaterials.NaquadahEnriched.getFluid(9216)");
        assertContainsAll(recipeBlock("na_eu_activate_naquadria"),
                "TSTMaterials.INERT_NAQUADRIA, 96", "GTMaterials.Naquadria.getFluid(9216)");
        assertContainsAll(recipeBlock("na_eu_naquadah_adamantium_separation"),
                "TSTMaterials.NAQUADAH_ADAMANTIUM_SOLUTION.getFluid(3000)",
                "TSTMaterials.ADAMANTINE, 4", "TSTMaterials.NAQUADAH_EARTH, 2",
                "TSTMaterials.CONCENTRATED_ENRICHED_NAQUADAH_SLUDGE, 1",
                "TSTMaterials.NAQUADAH_RICH_SOLUTION.getFluid(2000)");
        assertContainsAll(recipeBlock("na_eu_enriched_naquadah_sludge_treatment"),
                "TSTMaterials.CONCENTRATED_ENRICHED_NAQUADAH_SLUDGE, 16",
                "TSTMaterials.ENRICHED_NAQUADAH_SULPHATE, 165",
                "TSTMaterials.SODIUM_SULFATE, 140",
                "TSTMaterials.LOW_QUALITY_NAQUADRIA_SULPHATE, 2");
        assertContainsAll(recipeBlock("na_eu_naquadria_sulphate_purification"),
                "TSTMaterials.NAQUADRIA_RICH_SOLUTION.getFluid(9000)",
                "TSTMaterials.NAQUADRIA_SULPHATE, 44",
                "TSTMaterials.LOW_QUALITY_NAQUADRIA_SULPHATE, 6");
        assertContainsAll(recipeBlock("na_eu_metastable_oganesson"),
                "TSTMaterials.METASTABLE_OGANESSON, 1",
                "GTMaterials.Oganesson.getFluid(250)");
    }

    @Test
    void everyActivatorRecipePreservesApprovedDurationAndEu() throws IOException {
        assertTiming("na_eu_thorium_fuel", 10000, 490000);
        assertTiming("na_eu_uranium_fuel", 80, 302500);
        assertTiming("na_eu_plutonium_fuel", 80, 360000);
        assertTiming("na_eu_energised_tesseract_mkv", 328000, 1210000);
        assertTiming("na_eu_energised_tesseract_mkvi", 492000, 1210000);
        assertTiming("na_eu_activate_naquadah", 2000, 360000);
        assertTiming("na_eu_activate_enriched_naquadah", 2000, 810000);
        assertTiming("na_eu_activate_naquadria", 2000, 1210000);
        assertTiming("na_eu_naquadah_adamantium_separation", 100, 52900);
        assertTiming("na_eu_enriched_naquadah_sludge_treatment", 120, 230400);
        assertTiming("na_eu_naquadria_sulphate_purification", 100, 1210000);
        assertTiming("na_eu_metastable_oganesson", 2000, 1210000);
    }

    private static void assertTiming(String id, int duration, int eut) throws IOException {
        assertContainsAll(recipeBlock(id), ".duration(" + duration + ")", ".EUt(" + eut + ")");
    }

    private static String recipeBlock(String id) throws IOException {
        String source = Files.readString(SOURCE);
        String marker = "recipeBuilder(TSTModern.id(\"" + id + "\"))";
        int start = source.indexOf(marker);
        assertTrue(start >= 0, () -> "Missing recipe " + id);
        int end = source.indexOf(".save(provider);", start);
        assertTrue(end >= 0, () -> "Recipe has no save call: " + id);
        return source.substring(start, end);
    }

    private static void assertContainsAll(String source, String... expected) {
        for (String value : expected) {
            assertTrue(source.contains(value), () -> "Missing approved recipe token: " + value);
        }
    }
}
