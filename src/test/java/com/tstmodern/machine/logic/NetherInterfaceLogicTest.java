package com.tstmodern.machine.logic;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

class NetherInterfaceLogicTest {

    private static final long IV_EUT = 7_680L;

    private record WeightedPackage(String name, int rawWeight) {}

    private record FluidEntry(String name, int rawChance, int rawMaxChance) {}

    @Test
    void reservesTwoIvAmpsBeforeParallelWork() {
        assertEquals(0, NetherInterfaceLogic.powerParallel(2 * IV_EUT, IV_EUT));
        assertEquals(1, NetherInterfaceLogic.powerParallel(3 * IV_EUT, IV_EUT));
        assertEquals(64, NetherInterfaceLogic.powerParallel(66 * IV_EUT, IV_EUT));
    }

    @Test
    void saturatesInputPowerAndParallelLimit() {
        assertEquals(Long.MAX_VALUE, NetherInterfaceLogic.saturatedMultiply(Long.MAX_VALUE, 2));
        assertEquals(0, NetherInterfaceLogic.saturatedMultiply(-1, 2));
        assertEquals(Integer.MAX_VALUE,
                NetherInterfaceLogic.parallelLimit(64, Integer.MAX_VALUE));
        assertEquals(64, NetherInterfaceLogic.parallelLimit(64, -1));
    }

    @Test
    void activeModifierSeparatesResourceAndPowerParallel() throws IOException {
        String source = Files.readString(Path.of(
                "src/main/java/com/tstmodern/machine/NetherInterfaceMachine.java"));

        assertTrue(source.contains("ParallelLogic.getParallelAmountWithoutEU(machine, recipe, limit)"));
        assertTrue(source.contains("netherInterface.getEnergyContainer().getInputVoltage()"));
        assertTrue(source.contains("netherInterface.getEnergyContainer().getInputAmperage()"));
        assertTrue(source.contains("NetherInterfaceLogic.powerParallel(availableEUt, VA[IV])"));
        assertTrue(source.contains("int parallel = Math.min(resourceParallel, powerParallel)"));
        assertTrue(source.contains(".inputModifier(ContentModifier.multiplier(parallel))"));
        assertTrue(source.contains(".outputModifier(ContentModifier.multiplier(parallel))"));
        assertTrue(source.contains(".eutMultiplier((double) (parallel + 2))"));
        assertTrue(source.contains(".parallels(parallel)"));
    }

    @Test
    void dimensionalHarvestingUsesNetherOutputsAndCustomChanceLogics() throws IOException {
        String source = Files.readString(Path.of(
                "src/main/java/com/tstmodern/data/recipe/NetherInterfaceRecipes.java"));

        assertTrue(source.contains(".inputFluids(DistilledWater.getFluid(16_000))"));
        assertTrue(source.contains(".outputFluids(TSTMaterials.POOR_NETHER_WASTE.getFluid(16_000))"));
        assertTrue(source.contains(".chancedOutput(new ItemStack(Items.ANCIENT_DEBRIS), 100, 0)"));
        assertTrue(source.contains(".chancedOutput(new ItemStack(Blocks.NETHERRACK, 16), 4_900, 0)"));
        assertTrue(source.contains(".chancedOutput(new ItemStack(Items.NETHERITE_SCRAP, 4), 3_000, 0)"));
        assertTrue(source.contains(".chancedOutput(new ItemStack(Items.NETHERITE_INGOT), 1_000, 0)"));
        assertTrue(source.contains(".chancedOutput(new ItemStack(Items.NETHER_STAR), 1_000, 0)"));
        assertTrue(source.contains(".chancedOutput(TSTMaterials.HELLISH_METAL.getFluid(288), 3_000, 0)"));
        assertTrue(source.contains(".chancedItemOutputLogic(TSTChanceLogics.THREE_WEIGHTED_SCALED)"));
        assertTrue(source.contains(".chancedFluidOutputLogic(TSTChanceLogics.SINGLE_ROLL_SCALED)"));
        assertFalse(source.contains("LiquidNetherAir"));
        assertFalse(source.contains("Fluids.LAVA, 16_000"));
    }

    @Test
    void definitionLeavesExactPowerEquationUnmodified() throws IOException {
        String source = Files.readString(Path.of(
                "src/main/java/com/tstmodern/registry/machine/NetherInterfaceDefinition.java"));

        assertTrue(source.contains(".recipeModifiers(NetherInterfaceMachine::recipeModifier)"));
        assertFalse(source.contains("GTRecipeModifiers.OC_NON_PERFECT"));
    }

    @Test
    void mapsAllWeightedPackageBoundaries() {
        int[] weights = { 1, 49, 30, 10, 10 };
        assertEquals(0, NetherInterfaceLogic.selectWeightedIndex(0, weights));
        assertEquals(1, NetherInterfaceLogic.selectWeightedIndex(1, weights));
        assertEquals(1, NetherInterfaceLogic.selectWeightedIndex(49, weights));
        assertEquals(2, NetherInterfaceLogic.selectWeightedIndex(50, weights));
        assertEquals(2, NetherInterfaceLogic.selectWeightedIndex(79, weights));
        assertEquals(3, NetherInterfaceLogic.selectWeightedIndex(80, weights));
        assertEquals(4, NetherInterfaceLogic.selectWeightedIndex(99, weights));
    }

    @Test
    void treatsThirtyPercentAsOneExactCycleRoll() {
        assertTrue(NetherInterfaceLogic.passesChance(2_999, 3_000));
        assertFalse(NetherInterfaceLogic.passesChance(3_000, 3_000));
    }

    @Test
    void performsExactlyThreeSelectionsAndScalesOnePackage() {
        int[] scriptedRolls = { 0, 50, 99 };
        AtomicInteger calls = new AtomicInteger();
        int[] selected = NetherInterfaceLogic.selectThreeWeighted(
                bound -> scriptedRolls[calls.getAndIncrement()],
                new int[] { 1, 49, 30, 10, 10 });
        assertArrayEquals(new int[] { 0, 2, 4 }, selected);
        assertEquals(3, calls.get());
        assertEquals(64, NetherInterfaceLogic.scaledAmount(4, 16));
    }

    @Test
    void performsOnlyOneFluidChanceRollPerCycle() {
        AtomicInteger calls = new AtomicInteger();
        assertTrue(NetherInterfaceLogic.rollOnce(bound -> {
            calls.incrementAndGet();
            return 2_999;
        }, 3_000, 10_000));
        assertEquals(1, calls.get());
    }

    @Test
    void weightedDecisionReturnsThreeScaledOutputsWithReplacementAndRawWeights() {
        List<WeightedPackage> entries = List.of(
                new WeightedPackage("debris", 1),
                new WeightedPackage("mud", 49),
                new WeightedPackage("scrap", 30),
                new WeightedPackage("nanoparticles", 10),
                new WeightedPackage("kami", 10));
        int[] scriptedRolls = { 1, 1, 50 };
        AtomicInteger randomCalls = new AtomicInteger();
        List<Integer> extractedWeights = new ArrayList<>();
        List<String> scaledEntries = new ArrayList<>();

        List<String> outputs = NetherInterfaceLogic.selectAndScaleThreeWeighted(
                entries,
                entry -> {
                    extractedWeights.add(entry.rawWeight());
                    return entry.rawWeight();
                },
                bound -> {
                    assertEquals(100, bound);
                    return scriptedRolls[randomCalls.getAndIncrement()];
                },
                (entry, scale) -> {
                    assertEquals(16, scale);
                    String scaled = entry.name() + "@" + scale;
                    scaledEntries.add(scaled);
                    return scaled;
                },
                16);

        assertEquals(List.of(1, 49, 30, 10, 10), extractedWeights);
        assertEquals(3, randomCalls.get());
        assertEquals(List.of("mud@16", "mud@16", "scrap@16"), outputs);
        assertEquals(outputs, scaledEntries);
    }

    @Test
    void fluidDecisionRollsRawChanceOnceAndScalesOnlySuccess() {
        FluidEntry entry = new FluidEntry("hellish_metal", 3_000, 10_000);
        AtomicInteger chanceReads = new AtomicInteger();
        AtomicInteger maximumReads = new AtomicInteger();
        AtomicInteger successRolls = new AtomicInteger();
        AtomicInteger failureRolls = new AtomicInteger();
        List<Integer> scales = new ArrayList<>();

        List<String> success = NetherInterfaceLogic.rollAndScaleOnce(
                entry,
                fluid -> {
                    chanceReads.incrementAndGet();
                    return fluid.rawChance();
                },
                fluid -> {
                    maximumReads.incrementAndGet();
                    return fluid.rawMaxChance();
                },
                bound -> {
                    assertEquals(10_000, bound);
                    successRolls.incrementAndGet();
                    return 2_999;
                },
                (fluid, scale) -> {
                    scales.add(scale);
                    return fluid.name() + "@" + scale;
                },
                16);

        List<String> failure = NetherInterfaceLogic.rollAndScaleOnce(
                entry,
                fluid -> {
                    chanceReads.incrementAndGet();
                    return fluid.rawChance();
                },
                fluid -> {
                    maximumReads.incrementAndGet();
                    return fluid.rawMaxChance();
                },
                bound -> {
                    assertEquals(10_000, bound);
                    failureRolls.incrementAndGet();
                    return 3_000;
                },
                (fluid, scale) -> {
                    scales.add(scale);
                    return fluid.name() + "@" + scale;
                },
                32);

        assertEquals(List.of("hellish_metal@16"), success);
        assertTrue(failure.isEmpty());
        assertEquals(2, chanceReads.get());
        assertEquals(2, maximumReads.get());
        assertEquals(1, successRolls.get());
        assertEquals(1, failureRolls.get());
        assertEquals(List.of(16), scales);
    }
}
