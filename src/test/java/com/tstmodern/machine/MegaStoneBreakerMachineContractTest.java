package com.tstmodern.machine;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

final class MegaStoneBreakerMachineContractTest {
    @Test
    void probesOutputCapacityUsingTheFinalOutputBonus() throws IOException {
        String source = Files.readString(Path.of("src/main/java/com/tstmodern/machine/MegaStoneBreakerMachine.java"));

        int copiedRecipe = source.indexOf("GTRecipe outputProbe = recipe.copy();");
        int outputsReplaced = source.indexOf(
                "outputProbe.outputs.putAll(ContentModifier.multiplier(outputBonus).applyContents(recipe.outputs));");
        int parallelCalculation = source.indexOf(
                "ParallelLogic.getParallelAmount(machine, outputProbe, parallelLimit)");

        assertTrue(copiedRecipe >= 0, "output capacity must use a copied recipe");
        assertTrue(outputsReplaced > copiedRecipe, "the copied recipe must contain final bonus outputs");
        assertTrue(parallelCalculation > outputsReplaced,
                "parallel selection must probe the final bonus outputs");
    }
}
