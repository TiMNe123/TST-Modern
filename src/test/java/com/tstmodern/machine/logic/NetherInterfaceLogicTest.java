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

    private static final String EXPECTED_MODIFIER_BODY = """
            if (!(machine instanceof NetherInterfaceMachine netherInterface)) {
                return ModifierFunction.NULL;
            }

            int limit = NetherInterfaceLogic.parallelLimit(64,
                    netherInterface.getParallelHatch().map(h -> h.getCurrentParallel()).orElse(0));
            int resourceParallel = ParallelLogic.getParallelAmountWithoutEU(machine, recipe, limit);
            long availableEUt = NetherInterfaceLogic.saturatedMultiply(
                    netherInterface.getEnergyContainer().getInputVoltage(),
                    netherInterface.getEnergyContainer().getInputAmperage());
            int powerParallel = NetherInterfaceLogic.powerParallel(availableEUt, VA[IV]);
            int parallel = Math.min(resourceParallel, powerParallel);
            if (parallel <= 0) {
                return ModifierFunction.NULL;
            }

            return ModifierFunction.builder()
                    .inputModifier(ContentModifier.multiplier(parallel))
                    .outputModifier(ContentModifier.multiplier(parallel))
                    .eutMultiplier(NetherInterfaceLogic.eutMultiplier(parallel))
                    .parallels(parallel)
                    .build();
            """;

    private static final String EXPECTED_DIMENSIONAL_HARVESTING_BUILDER = """
            TSTRecipeTypes.NETHER_INTERFACE.recipeBuilder(TSTModern.id("nether_interface/dimensional_harvesting"))
                    .inputFluids(DistilledWater.getFluid(16_000))
                    .outputFluids(TSTMaterials.POOR_NETHER_WASTE.getFluid(16_000))
                    .chancedOutput(new ItemStack(Items.ANCIENT_DEBRIS), 100, 0)
                    .chancedOutput(new ItemStack(Blocks.NETHERRACK, 16), 4_900, 0)
                    .chancedOutput(new ItemStack(Items.NETHERITE_SCRAP, 4), 3_000, 0)
                    .chancedOutput(new ItemStack(Items.NETHERITE_INGOT), 1_000, 0)
                    .chancedOutput(new ItemStack(Items.NETHER_STAR), 1_000, 0)
                    .chancedOutput(TSTMaterials.HELLISH_METAL.getFluid(288), 3_000, 0)
                    .chancedItemOutputLogic(TSTChanceLogics.THREE_WEIGHTED_SCALED)
                    .chancedFluidOutputLogic(TSTChanceLogics.SINGLE_ROLL_SCALED)
                    .EUt(VA[IV])
                    .duration(1200)
                    .save(provider);
            """;

    private static final String EXPECTED_DEFINITION_MODIFIERS = """
            .recipeType(TSTRecipeTypes.NETHER_INTERFACE)
            .recipeModifiers(NetherInterfaceMachine::recipeModifier)
            """;

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
    void widensParallelBeforeAddingReservedAmps() {
        assertEquals(2_147_483_649.0, NetherInterfaceLogic.eutMultiplier(Integer.MAX_VALUE));
    }

    @Test
    void activeModifierMatchesExactPowerAndParallelContract() throws IOException {
        String source = Files.readString(Path.of(
                "src/main/java/com/tstmodern/machine/NetherInterfaceMachine.java"));
        String body = methodBody(source,
                "public static ModifierFunction recipeModifier(MetaMachine machine, GTRecipe recipe)");

        assertEquals(normalizeWhitespace(EXPECTED_MODIFIER_BODY), normalizeWhitespace(body));
        assertEquals(2, occurrences(body, "return ModifierFunction.NULL;"));
        assertEquals(1, occurrences(body, "return ModifierFunction.builder()"));
        assertFalse(body.contains("(double) (parallel + 2)"));
    }

    @Test
    void dimensionalHarvestingMatchesExactBuilderAndOutputCardinality() throws IOException {
        String source = Files.readString(Path.of(
                "src/main/java/com/tstmodern/data/recipe/NetherInterfaceRecipes.java"));
        String builder = statement(
                source,
                "TSTRecipeTypes.NETHER_INTERFACE.recipeBuilder(TSTModern.id(\"nether_interface/dimensional_harvesting\"))",
                ".save(provider);");

        assertEquals(normalizeWhitespace(EXPECTED_DIMENSIONAL_HARVESTING_BUILDER),
                normalizeWhitespace(builder));
        assertEquals(1, occurrences(builder, ".inputFluids("));
        assertEquals(1, occurrences(builder, ".outputFluids("));
        assertEquals(6, occurrences(builder, ".chancedOutput("));
        assertEquals(1, occurrences(builder, ".chancedItemOutputLogic("));
        assertEquals(1, occurrences(builder, ".chancedFluidOutputLogic("));
        assertEquals(0, occurrences(builder, ".outputItems("));
        assertEquals(0, occurrences(builder, "LiquidNetherAir"));
        assertEquals(0, occurrences(builder, "Fluids.LAVA"));
    }

    @Test
    void definitionUsesOnlyTheNetherModifier() throws IOException {
        String source = Files.readString(Path.of(
                "src/main/java/com/tstmodern/registry/machine/NetherInterfaceDefinition.java"));
        String modifiers = section(
                source,
                ".recipeType(TSTRecipeTypes.NETHER_INTERFACE)",
                ".appearanceBlock(GTBlocks.CASING_STEEL_SOLID)");

        assertEquals(normalizeWhitespace(EXPECTED_DEFINITION_MODIFIERS), normalizeWhitespace(modifiers));
        assertEquals(1, occurrences(source, ".recipeModifiers("));
        assertEquals(0, occurrences(source, "GTRecipeModifiers"));
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

    private static String methodBody(String source, String methodMarker) {
        int method = source.indexOf(methodMarker);
        assertTrue(method >= 0, "missing method marker: " + methodMarker);
        int openingBrace = source.indexOf('{', method);
        assertTrue(openingBrace >= 0, "missing opening brace for: " + methodMarker);

        int depth = 1;
        for (int cursor = openingBrace + 1; cursor < source.length(); cursor++) {
            char current = source.charAt(cursor);
            if (current == '{') {
                depth++;
            } else if (current == '}' && --depth == 0) {
                return source.substring(openingBrace + 1, cursor);
            }
        }
        throw new AssertionError("missing closing brace for: " + methodMarker);
    }

    private static String statement(String source, String startMarker, String endMarker) {
        int start = source.indexOf(startMarker);
        assertTrue(start >= 0, "missing statement start: " + startMarker);
        int end = source.indexOf(endMarker, start);
        assertTrue(end >= start, "missing statement end: " + endMarker);
        return source.substring(start, end + endMarker.length());
    }

    private static String section(String source, String startMarker, String endMarker) {
        int start = source.indexOf(startMarker);
        assertTrue(start >= 0, "missing section start: " + startMarker);
        int end = source.indexOf(endMarker, start);
        assertTrue(end > start, "missing section end: " + endMarker);
        return source.substring(start, end);
    }

    private static int occurrences(String source, String needle) {
        int count = 0;
        int cursor = 0;
        while ((cursor = source.indexOf(needle, cursor)) >= 0) {
            count++;
            cursor += needle.length();
        }
        return count;
    }

    private static String normalizeWhitespace(String source) {
        return source.replaceAll("\\s+", " ").trim();
    }
}
