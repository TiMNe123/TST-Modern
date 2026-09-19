package com.tstmodern.data.recipe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

final class DraconicCrucibleRecipesContractTest {
    @Test
    void preservesApprovedBatchChanceTimingAndResearch() throws Exception {
        String source = Files.readString(Path.of(
                "src/main/java/com/tstmodern/data/recipe/DraconicCrucibleRecipes.java"));
        assertTrue(source.contains(".inputItems(PREFERRED_ORE, 64)"));
        assertTrue(source.contains("PREFERRED_DUST, 64"));
        assertTrue(source.contains("PREFERRED_DUST, 16, 8_450"));
        assertTrue(source.contains("PREFERRED_ORE, 2, 1_000"));
        assertTrue(source.contains("PREFERRED_HEART, 1, 500"));
        assertTrue(source.contains("PREFERRED_AWAKENED_NUGGET, 1, 50"));
        assertTrue(source.contains(".chancedItemOutputLogic(ChanceLogic.XOR)"));
        assertTrue(source.contains(".duration(400)"));
        assertTrue(source.contains(".duration(1_000)"));
        assertEquals(2, Pattern.compile("stationResearch").matcher(source).results().count());
        assertTrue(source.contains("TOOL_DATA_ORB"));
        assertTrue(source.contains("TOOL_DATA_MODULE"));
    }

    @Test
    void declaresCustomAndModOriginalBranches() throws Exception {
        String source = Files.readString(Path.of(
                "src/main/java/com/tstmodern/data/recipe/DraconicCrucibleRecipes.java"));
        assertTrue(source.contains("draconic_crucible/awakened_draconium_custom"));
        assertTrue(source.contains("crafting/draconium_core_custom"));
        assertTrue(source.contains("TSTConfig.DRACONIC_CRUCIBLE_RECIPE_MODE.get() == TSTConfig.RecipeMode.MOD_ORIGINAL"));
        assertTrue(source.contains("Draconic Crucible machine recipes disabled in favor of original Draconic Evolution progression"));
    }
}
