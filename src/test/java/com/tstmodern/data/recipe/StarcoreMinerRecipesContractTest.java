package com.tstmodern.data.recipe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

final class StarcoreMinerRecipesContractTest {
    private static final Path SOURCE = Path.of(
            "src/main/java/com/tstmodern/data/recipe/StarcoreMinerRecipes.java");

    private static final List<String> EXPECTED_IDS = List.of(
            "assembly_line/dimensional_bridge_casing",
            "assembly_line/starcore_miner");

    @Test
    void registersOnlyApprovedRecipes() throws IOException {
        String source = Files.readString(SOURCE);
        Matcher matcher = Pattern.compile("recipeBuilder\\(TSTModern\\.id\\(\"([^\"]+)\"\\)\\)").matcher(source);
        List<String> actual = new ArrayList<>();
        while (matcher.find()) {
            actual.add(matcher.group(1));
        }

        assertEquals(EXPECTED_IDS, actual);
        assertTrue(source.contains("COMPONENT_ASSEMBLY_LINE_CASING_UIV.get(), 8"));
        assertTrue(source.contains("DIMENSIONAL_BRIDGE_CASING.get(), 8"));
        assertTrue(source.contains("SPACE_ELEVATOR_BASE_CASING.get(), 64"));
        assertTrue(source.contains("BEDROCK_ORE_MINER[EV].asStack(64)"));
        assertTrue(source.contains(".duration(1200)"));
        assertTrue(source.contains(".duration(144000)"));
        assertTrue(source.contains(".EUt(VA[UIV])"));
        assertTrue(source.contains("TSTMaterials.METASTABLE_OGANESSON.getFluid(73728)"));
        assertEquals(2, source.split("\\.stationResearch\\(", -1).length - 1);
        assertTrue(source.contains(".researchId(\"dimensional_bridge_casing\")"));
        assertEquals(2, source.split("GTItems\\.TOOL_DATA_MODULE\\.asStack\\(\\)", -1).length - 1);
        assertTrue(source.contains(".researchId(\"starcore_miner\")"));
        assertTrue(source.contains("GTItems.TOOL_DATA_MODULE.asStack()"));
    }
}
