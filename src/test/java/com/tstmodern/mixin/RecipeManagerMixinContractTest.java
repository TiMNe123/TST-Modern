package com.tstmodern.mixin;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

final class RecipeManagerMixinContractTest {
    @Test
    void declaresRequiredAvaritiaAndDraconicRecipeFilters() throws Exception {
        String source = Files.readString(Path.of(
                "src/main/java/com/tstmodern/mixin/RecipeManagerMixin.java"));

        assertTrue(source.contains("avaritia\", \"infinity_ingot\""));
        assertTrue(source.contains("avaritia\", \"infinity_catalyst\""));
        assertTrue(source.contains("avaritia\", \"infinity_catalyst_eternal\""));
        assertTrue(source.contains("avaritia\", \"crystal_matrix_ingot\""));
        assertTrue(source.contains("avaritia\", \"diamond_lattice\""));

        assertTrue(source.contains("draconicevolution\", \"awakened_draconium_block\""));

        assertTrue(source.contains("TSTConfig.GALACTIC_ARMILLARY_RECIPE_MODE.get() == TSTConfig.RecipeMode.CUSTOM"));
        assertTrue(source.contains("TSTConfig.DRACONIC_CRUCIBLE_RECIPE_MODE.get() == TSTConfig.RecipeMode.CUSTOM"));

        String mixinConfig = Files.readString(Path.of("src/main/resources/tstmodern.mixins.json"));
        assertTrue(mixinConfig.contains("\"RecipeManagerMixin\""));
    }
}
