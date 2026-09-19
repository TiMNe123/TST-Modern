package com.tstmodern.registry.machine;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class LargeNeutronOscillatorDefinitionContractTest {

    private static final Path SOURCE = Path.of(
            "src/main/java/com/tstmodern/registry/machine/LargeNeutronOscillatorDefinition.java");

    @Test
    void abilitiesDeclareApprovedGlobalLimitsAndJeiPreviewCounts() throws Exception {
        String source = Files.readString(SOURCE).replace("\r\n", "\n");

        assertTrue(source.contains("PartAbility.PARALLEL_HATCH)\n" +
                "                        .setExactLimit(1)\n" +
                "                        .setPreviewCount(1)"));
        assertTrue(source.contains("PartAbility.INPUT_LASER)\n" +
                "                        .setMinGlobalLimited(1)\n" +
                "                        .setMaxGlobalLimited(2)\n" +
                "                        .setPreviewCount(1)"));
        assertTrue(source.contains("PartAbility.EXPORT_FLUIDS).setMinGlobalLimited(1).setPreviewCount(1)"));
    }
}
