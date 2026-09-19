package com.tstmodern.data.recipe;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class NaquadahFuelRefineryDependencyContractTest {
    private static final Path SOURCE = Path.of(
            "src/main/java/com/tstmodern/data/recipe/NaquadahFuelRefineryRecipes.java");

    @Test
    void superCoolantHasTheApprovedLargeChemicalRecipe() throws IOException {
        String source = Files.readString(SOURCE);
        int start = source.indexOf("large_chemical/super_coolant");
        assertTrue(start >= 0, "Missing Super Coolant recipe");
        int end = source.indexOf(".save(provider);", start);
        assertTrue(end >= 0, "Super Coolant recipe has no save call");
        String recipe = source.substring(start, end);
        assertTrue(recipe.contains("GTMaterials.PCBCoolant.getFluid(1_000)"));
        assertTrue(recipe.contains("GTMaterials.Helium.getFluid(1_000)"));
        assertTrue(recipe.contains("TSTMaterials.SUPER_COOLANT.getFluid(1_000)"));
        assertTrue(recipe.contains(".duration(400)"));
        assertTrue(recipe.contains(".EUt(VA[UV])"));
    }
}
