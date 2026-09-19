package com.tstmodern.data.recipe;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class DedicatedServerDataContractTest {

    @Test
    void allRecipeMaterialFormsExist() throws IOException {
        String materials = normalized("src/main/java/com/tstmodern/registry/TSTMaterials.java");
        String megaTree = normalized("src/main/java/com/tstmodern/data/recipe/MegaTreeFarmRecipes.java");

        assertTrue(materials.contains("GTMaterials.Erbium.hasProperty(PropertyKey.DUST)"));
        assertTrue(materials.contains("new DustProperty()"));
        assertTrue(materials.contains("GTMaterials.TungstenCarbide.addFlags( MaterialFlags.GENERATE_ROTOR );"));
        assertTrue(megaTree.contains("inputItems(pipeNormalFluid, GTMaterials.Polybenzimidazole, 4)"));
        assertFalse(megaTree.contains("inputItems(pipeNormalFluid, NaquadahAlloy, 4)"));
    }

    @Test
    void assemblerSupportsTheApprovedTenInputCasingRecipes() throws IOException {
        String recipes = normalized("src/main/java/com/tstmodern/data/recipe/DisassemblerRecipes.java");
        assertTrue(recipes.contains("ASSEMBLER_RECIPES.setMaxIOSize(10, 1, 1, 0);"));
    }

    @Test
    void removedCasingsHaveNoLootTables() {
        for (String casing : new String[] {
                "purple_lamp_casing",
                "extreme_heat_resistant_casing",
                "superconducting_magnetic_casing"
        }) {
            assertFalse(Files.exists(Path.of(
                    "src/main/resources/data/tstmodern/loot_tables/blocks/" + casing + ".json")));
        }
    }

    @Test
    void recipeAlternativesAndResearchInputsDoNotConflict() throws IOException {
        String hyper = normalized("src/main/java/com/tstmodern/data/recipe/HyperThermalConvectorRecipes.java");
        String cyclotron = normalized("src/main/java/com/tstmodern/data/recipe/IncompactCyclotronRecipes.java");

        assertTrue(hyper.contains("centrifuge/decompress_supercritical_steam\")) .circuitMeta(1)"));
        assertTrue(hyper.contains("centrifuge/supercritical_to_superheated\")) .circuitMeta(2)"));
        assertTrue(cyclotron.contains("researchStack(new ItemStack(TSTBlocks.VACUUM_CASING.get()))"));
    }

    private static String normalized(String path) throws IOException {
        return Files.readString(Path.of(path)).replaceAll("\\s+", " ");
    }
}
