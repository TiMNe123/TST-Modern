package com.tstmodern.data.recipe;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class AstralComputingArrayRecipesContractTest {

    @Test
    void compactFusionCoilUsesItsUniqueResearchInput() throws Exception {
        String source = Files.readString(Path.of(
                "src/main/java/com/tstmodern/data/recipe/AstralComputingArrayRecipes.java"));
        assertTrue(source.contains("station(b, new ItemStack(TSTItems.HIGH_COMPUTATION_STATION_T5.get())"));
        assertTrue(source.contains("\"compact_fusion_coil_t0\", 48_000, UV, 32"));
    }
}
