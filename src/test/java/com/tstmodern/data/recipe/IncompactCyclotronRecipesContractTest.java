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

class IncompactCyclotronRecipesContractTest {
    private static final Path SOURCE = Path.of(
            "src/main/java/com/tstmodern/data/recipe/IncompactCyclotronRecipes.java");
    private static final List<String> IDS = List.of(
            "cyclotron_hydrogen_ions",
            "cyclotron_hydrogen_plasma",
            "cyclotron_protons",
            "cyclotron_particle_collider",
            "cyclotron_unknown_to_protons",
            "cyclotron_quantum_anomaly",
            "cyclotron_neptunium_238",
            "cyclotron_strange_dust",
            "ender_pearl_fluid",
            "hydrogen_plasma_fuel",
            "quantum_frame",
            "dense_cyclotron_outer_casing",
            "compact_cyclotron_coil",
            "incompact_cyclotron");

    @Test
    void registersOnlyTheApprovedClosedRecipeSet() throws IOException {
        String source = Files.readString(SOURCE);
        Matcher matcher = Pattern.compile("recipeBuilder\\(\"([^\"]+)\"\\)").matcher(source);
        var actual = new java.util.ArrayList<String>();
        while (matcher.find()) {
            actual.add(matcher.group(1));
        }
        assertEquals(IDS, actual);
        assertTrue(source.contains(".chancedOutput(new ItemStack(TSTItems.HYDROGEN_ION.get()), 500, 0)"));
        assertTrue(source.contains(".chancedOutput(new ItemStack(TSTItems.NEUTRON.get()), 1_000, 0)"));
        assertTrue(source.contains(".duration(300)"));
        assertTrue(source.contains(".EUt(VA[UV])"));
        assertTrue(source.contains("FluidStorageKeys.PLASMA, 250"));
        assertTrue(source.contains("TSTMaterials.NEPTUNIUM_238"));
        assertTrue(source.contains("TSTMaterials.PLUTONIUM_238"));
        assertTrue(source.contains("GTMaterials.EnderPearl.getFluid(1_000)"));
        assertTrue(source.contains("GTMaterials.EnderPearl.getFluid(250)"));
        assertTrue(source.contains("PLASMA_GENERATOR_FUELS"));
        assertTrue(source.contains("GTMaterials.Hydrogen.getFluid(FluidStorageKeys.PLASMA, 1)"));
        assertTrue(source.contains(".outputFluids(GTMaterials.Hydrogen.getFluid(1))"));
        assertTrue(source.contains(".EUt(-V[EV])"));
        assertTrue(source.contains("TSTItems.SPECIAL_LASER_LENS"));
        assertTrue(source.contains("TSTItems.STRANGE_DUST"));
        assertTrue(source.contains(".duration(18_000)"));
        assertTrue(source.contains(".EUt(8_000_000)"));
        assertTrue(!source.contains(".scannerResearch("));
        assertEquals(4, source.split(Pattern.quote(".stationResearch("), -1).length - 1);
        assertEquals(3, source.split(Pattern.quote("GTItems.TOOL_DATA_ORB.asStack()"), -1).length - 1);
        assertEquals(1, source.split(Pattern.quote("GTItems.TOOL_DATA_MODULE.asStack()"), -1).length - 1);
        assertEquals(3, source.split(Pattern.quote(".CWUt(32,"), -1).length - 1);
        assertEquals(1, source.split(Pattern.quote(".CWUt(64,"), -1).length - 1);
        assertEquals(3, source.split(Pattern.quote(".EUt(VA[UHV])"), -1).length - 1);
        assertEquals(1, source.split(Pattern.quote(".EUt(VA[UEV])"), -1).length - 1);
        for (String id : List.of(
                "quantum_frame",
                "dense_cyclotron_outer_casing",
                "compact_cyclotron_coil",
                "incompact_cyclotron")) {
            assertEquals(1, source.split(Pattern.quote(".researchId(\"" + id + "\")"), -1).length - 1);
        }
        assertTrue(source.contains(".inputItems(TSTCircuitTags.get(UEV), 16)"));
        assertEquals(1, source.split("CustomTags.UHV_CIRCUITS", -1).length - 1);
        assertTrue(source.contains(
                ".outputItems(new ItemStack(TSTBlocks.DENSE_CYCLOTRON_OUTER_CASING.get()))"));
        assertTrue(source.contains(
                ".outputItems(new ItemStack(TSTBlocks.COMPACT_CYCLOTRON_COIL.get()))"));

        String materials = Files.readString(Path.of(
                "src/main/java/com/tstmodern/registry/TSTMaterials.java"));
        assertTrue(materials.contains("GTMaterials.Hydrogen.getProperty(PropertyKey.FLUID)"));
        assertTrue(materials.contains("enqueueRegistration(FluidStorageKeys.PLASMA"));
    }
}
