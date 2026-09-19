package com.tstmodern.registry.machine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class IncompactCyclotronDefinitionContractTest {

    @Test
    void usesExactAxesRolesAndApprovedMachineBehavior() throws IOException {
        String definition = Files.readString(Path.of(
                "src/main/java/com/tstmodern/registry/machine/IncompactCyclotronDefinition.java"));
        assertTrue(definition.contains("RelativeDirection.RIGHT"));
        assertTrue(definition.contains("RelativeDirection.DOWN"));
        assertTrue(definition.contains("RelativeDirection.BACK"));
        assertTrue(definition.contains("IncompactCyclotronStructure.PATTERN_AISLES"));
        assertTrue(definition.contains(".where('E'"));
        assertTrue(definition.contains(".where('F'"));
        assertTrue(definition.contains("PartAbility.INPUT_ENERGY"));
        assertTrue(definition.contains("PartAbility.SUBSTATION_INPUT_ENERGY"));
        assertFalse(definition.contains("MAINTENANCE"));
        assertFalse(definition.contains("MUFFLER"));
        assertTrue(definition.contains("GTRecipeModifiers.OC_NON_PERFECT"));

        String registry = Files.readString(Path.of(
                "src/main/java/com/tstmodern/registry/machine/TSTMachineRegistry.java"));
        assertEquals(1, occurrences(registry, "IncompactCyclotronDefinition.MACHINE"));
    }

    @Test
    void limitsSharedEnergyInputsAcrossEAndFToTwo() throws IOException {
        String definition = Files.readString(Path.of(
                "src/main/java/com/tstmodern/registry/machine/IncompactCyclotronDefinition.java"));
        String compact = definition.replaceAll("\\s+", "");

        assertTrue(compact.contains("setMinGlobalLimited(1).setMaxGlobalLimited(2)"));
        assertEquals(2, occurrences(compact, ".or(energyInputs)"));
        assertTrue(compact.contains("autoAbilities(definition.getRecipeTypes(),false,false,true,true,true,true)"));
    }

    private static int occurrences(String text, String needle) {
        return (text.length() - text.replace(needle, "").length()) / needle.length();
    }
}
