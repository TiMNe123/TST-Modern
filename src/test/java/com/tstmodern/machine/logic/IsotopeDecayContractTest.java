package com.tstmodern.machine.logic;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class IsotopeDecayContractTest {
    @Test
    void neptunium238DecaysIntoPlutonium238AfterSourceLifetime() throws IOException {
        String source = Files.readString(Path.of(
                "src/main/java/com/tstmodern/machine/logic/IsotopeDecayHandler.java"));
        assertTrue(source.contains("TSTConfig.NEPTUNIUM_238_DECAY_TICKS"));
        assertTrue(source.contains("TSTMaterials.NEPTUNIUM_238"));
        assertTrue(source.contains("TSTMaterials.PLUTONIUM_238"));

        String config = Files.readString(Path.of(
                "src/main/java/com/tstmodern/config/TSTConfig.java"));
        assertTrue(config.contains("defineInRange(\"neptunium238DecayTicks\", 50_000"));

        String entrypoint = Files.readString(Path.of(
                "src/main/java/com/tstmodern/TSTModern.java"));
        assertTrue(entrypoint.contains("IsotopeDecayHandler::onPlayerTick"));

        String client = Files.readString(Path.of(
                "src/main/java/com/tstmodern/client/TSTClient.java"));
        assertTrue(client.contains("addNeptuniumDecayTooltip"));
        assertTrue(client.contains("tstmodern.material.neptunium_238.decay"));
    }
}
