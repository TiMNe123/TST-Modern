package com.tstmodern.data.recipe;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

final class GalacticArmillaryRecipesContractTest {
    @Test
    void declaresCustomAndModOriginalBranches() throws Exception {
        String source = Files.readString(Path.of(
                "src/main/java/com/tstmodern/data/recipe/GalacticArmillaryRecipes.java"));
        assertTrue(source.contains("galactic_armillary/infinity_catalyst_nugget_custom"));
        assertTrue(source.contains("crafting/infinity_catalyst_custom"));
        assertTrue(source.contains("galactic_armillary/infinity_ingot_custom"));
        assertTrue(source.contains("crafting/diamond_lattice_custom"));
        assertTrue(source.contains("blast/crystal_matrix_ingot_custom"));

        assertTrue(source.contains("TSTConfig.GALACTIC_ARMILLARY_RECIPE_MODE.get() == TSTConfig.RecipeMode.MOD_ORIGINAL"));
        assertTrue(source.contains("Galactic Armillary machine recipes disabled in favor of original Avaritia progression"));
    }
}
