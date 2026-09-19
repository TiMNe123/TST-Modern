package com.tstmodern.data.recipe;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class StrangeDustProcessingContractTest {

    @Test
    void separatesTheApprovedGtppRadioactiveMineralMix() throws IOException {
        String recipes = Files.readString(Path.of(
                "src/main/java/com/tstmodern/data/recipe/GiantVacuumDryingFurnaceRecipes.java"));
        String materials = Files.readString(Path.of(
                "src/main/java/com/tstmodern/registry/TSTMaterials.java"));
        String recipeTypes = Files.readString(Path.of(
                "src/main/java/com/tstmodern/registry/TSTRecipeTypes.java"));

        assertTrue(recipes.contains("chemical_dehydrator/strange_dust_separation"));
        assertTrue(recipes.contains("new ItemStack(TSTItems.STRANGE_DUST.get(), 61)"));
        assertTrue(recipes.contains("GTMaterials.Radon.getFluid(2_000)"));
        assertTrue(recipes.contains("dust, GTMaterials.Radium, 1"));
        assertTrue(recipes.contains("dust, GTMaterials.Uranium235, 1"));
        assertTrue(recipes.contains("dust, GTMaterials.Uranium238, 10"));
        assertTrue(recipes.contains("dust, GTMaterials.Thorium, 29"));
        assertTrue(recipes.contains("dust, TSTMaterials.FLUORCAPHITE, 6"));
        assertTrue(recipes.contains("dust, TSTMaterials.SAMARSKITE_Y, 8"));
        assertTrue(recipes.contains("dust, TSTMaterials.TITANITE, 4"));

        assertTrue(materials.contains("public static Material FLUORCAPHITE;"));
        assertTrue(materials.contains("public static Material SAMARSKITE_Y;"));
        assertTrue(materials.contains("public static Material TITANITE;"));
        assertTrue(recipeTypes.contains(".setMaxIOSize(2, 7, 2, 2)"));
    }
}
