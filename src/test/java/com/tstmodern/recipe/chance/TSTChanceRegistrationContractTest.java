package com.tstmodern.recipe.chance;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

final class TSTChanceRegistrationContractTest {

    @Test
    void listensForTheGtceuChanceLogicRegistrationEvent() throws IOException {
        String source = Files.readString(Path.of("src/main/java/com/tstmodern/TSTModern.java"));

        assertTrue(source.contains(
                "modBus.addGenericListener(ChanceLogic.class, TSTChanceLogics::registerChanceLogics);"));
    }

    @Test
    void declaresBothNamespacedChanceLogicIds() throws IOException {
        String source = Files.readString(
                Path.of("src/main/java/com/tstmodern/recipe/chance/TSTChanceLogics.java"));

        assertTrue(source.contains("tstmodern:three_weighted_scaled"));
        assertTrue(source.contains("tstmodern:single_roll_scaled"));
    }
}
