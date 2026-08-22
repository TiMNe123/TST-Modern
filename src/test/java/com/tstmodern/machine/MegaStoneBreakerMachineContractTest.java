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

    @Test
    void simulatesBothBoostDrainsBeforeSuccessfulWorkAndExecutesAfterward() throws IOException {
        String source = Files.readString(Path.of("src/main/java/com/tstmodern/machine/MegaStoneBreakerMachine.java"));
        String simulation = source.substring(
                source.indexOf("private boolean canConsumeBoostFluids()"),
                source.indexOf("private void executeBoostFluidDrain()"));
        String working = source.substring(
                source.indexOf("public boolean onWorking()"),
                source.indexOf("public void afterWorking()"));

        int waterSimulation = simulation.indexOf(
                "boolean waterAvailable = drainAcrossInputs(Fluids.WATER, 1_000, IFluidHandler.FluidAction.SIMULATE);");
        int lavaSimulation = simulation.indexOf(
                "boolean lavaAvailable = drainAcrossInputs(Fluids.LAVA, 1_000, IFluidHandler.FluidAction.SIMULATE);");
        int simulationResult = simulation.indexOf("return waterAvailable && lavaAvailable;");
        int simulationCheck = working.indexOf("if (drainDue && !canConsumeBoostFluids()) {");
        int failedSimulationReturn = working.indexOf("boosted = false;\n            return false;");
        int successfulWork = working.indexOf("if (!super.onWorking()) {");
        int execution = working.indexOf("executeBoostFluidDrain();");
        int counterIncrement = working.indexOf("activeBoostTicks++;");

        assertTrue(waterSimulation >= 0, "water must be simulated into a separate result");
        assertTrue(lavaSimulation > waterSimulation, "lava must be simulated even if water is unavailable");
        assertTrue(simulationResult > lavaSimulation, "simulation results must be combined after both probes");
        assertTrue(simulationCheck >= 0, "boost drain simulation must be checked before work");
        assertTrue(failedSimulationReturn > simulationCheck,
                "a failed simulation must return before work or drain execution");
        assertTrue(successfulWork > failedSimulationReturn, "successful GTCEu work follows simulation");
        assertTrue(execution > successfulWork, "boost drains execute only after successful work");
        assertTrue(counterIncrement > successfulWork, "the active tick counter advances only after successful work");
    }
}
