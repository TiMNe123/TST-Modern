package com.tstmodern.data.recipe;

import static com.gregtechceu.gtceu.api.GTValues.MAX;
import static com.gregtechceu.gtceu.api.GTValues.UEV;
import static com.gregtechceu.gtceu.api.GTValues.UHV;
import static com.gregtechceu.gtceu.api.GTValues.UIV;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class TSTCircuitTagsTest {

    @Test
    void fallsBackToUhvOnlyWhenAnEndgameCircuitPackIsAbsent() {
        assertEquals(UHV, TSTCircuitTags.resolveTier(UEV, false, false));
        assertEquals(UHV, TSTCircuitTags.resolveTier(MAX, false, false));

        assertEquals(UEV, TSTCircuitTags.resolveTier(UEV, true, false));
        assertEquals(UIV, TSTCircuitTags.resolveTier(UIV, true, false));
        assertEquals(UHV, TSTCircuitTags.resolveTier(MAX, true, false));

        assertEquals(UEV, TSTCircuitTags.resolveTier(UEV, false, true));
        assertEquals(MAX, TSTCircuitTags.resolveTier(MAX, false, true));
        assertEquals(UHV, TSTCircuitTags.resolveTier(UHV, true, true));
    }

    @Test
    void allExistingEndgameMachineRecipesUseTheSharedResolver() throws IOException {
        String massFabricator = Files.readString(Path.of(
                "src/main/java/com/tstmodern/data/recipe/MassFabricatorRecipes.java"));
        for (String tier : new String[] { "UEV", "UIV", "UXV", "OpV", "MAX" }) {
            assertTrue(massFabricator.contains("TSTCircuitTags.get(" + tier + ")"));
        }

        String disassembler = Files.readString(Path.of(
                "src/main/java/com/tstmodern/data/recipe/DisassemblerRecipes.java"));
        for (String tier : new String[] { "UEV", "UIV", "UXV", "OpV", "MAX" }) {
            assertTrue(disassembler.contains("TSTCircuitTags.get(" + tier + ")"));
        }
    }

    @Test
    void astralUsesNativeCircuitTagsBelowUev() throws IOException {
        String recipes = Files.readString(Path.of(
                "src/main/java/com/tstmodern/data/recipe/AstralComputingArrayRecipes.java"));
        assertTrue(recipes.contains("CustomTags.ZPM_CIRCUITS"));
        assertTrue(recipes.contains("CustomTags.UHV_CIRCUITS"));
        assertFalse(recipes.matches("(?s).*TSTCircuitTags\\.get\\((?:ZPM|UHV)\\).*"));
    }
}
