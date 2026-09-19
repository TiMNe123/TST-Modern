package com.tstmodern.machine.logic;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

import org.junit.jupiter.api.Test;

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreeScanner;
import com.sun.source.util.Trees;

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
        ParsedSource source = parse(Path.of(
                "src/main/java/com/tstmodern/machine/NetherInterfaceMachine.java"));
        MethodTree modifier = methodNamed(source, "NetherInterfaceMachine", "recipeModifier");
        String body = source.blockContents(modifier.getBody());

        assertEquals(normalizeWhitespace(EXPECTED_MODIFIER_BODY), normalizeWhitespace(body));
        assertEquals(2, occurrences(body, "return ModifierFunction.NULL;"));
        assertEquals(1, occurrences(body, "return ModifierFunction.builder()"));
        assertFalse(body.contains("(double) (parallel + 2)"));
    }

    @Test
    void dimensionalHarvestingMatchesExactBuilderAndOutputCardinality() throws IOException {
        ParsedSource source = parse(Path.of(
                "src/main/java/com/tstmodern/data/recipe/NetherInterfaceRecipes.java"));
        MethodTree register = methodNamed(source, "NetherInterfaceRecipes", "register");
        String builder = source.sourceOf(dimensionalHarvestingStatement(register));

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
        ParsedSource source = parse(Path.of(
                "src/main/java/com/tstmodern/registry/machine/NetherInterfaceDefinition.java"));
        VariableTree machine = fieldNamed(source, "NetherInterfaceDefinition", "MACHINE");
        List<MethodInvocationTree> chain = invocationChain(machine.getInitializer());
        int recipeType = invocationIndex(chain, "recipeType");
        int recipeModifiers = invocationIndex(chain, "recipeModifiers");
        String modifiers = source.invocationLink(chain.get(recipeType)) + "\n" +
                source.invocationLink(chain.get(recipeModifiers));

        assertEquals(normalizeWhitespace(EXPECTED_DEFINITION_MODIFIERS), normalizeWhitespace(modifiers));
        assertEquals(recipeType + 1, recipeModifiers);
        assertEquals(1, invocationCount(machine.getInitializer(), "recipeModifiers"));
        assertFalse(containsIdentifier(machine.getInitializer(), "GTRecipeModifiers"));
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

    private record ParsedSource(String source, CompilationUnitTree unit, SourcePositions positions) {

        String sourceOf(Tree tree) {
            long start = positions.getStartPosition(unit, tree);
            long end = positions.getEndPosition(unit, tree);
            assertTrue(start >= 0 && end >= start, "missing source positions for " + tree.getKind());
            return source.substring((int) start, (int) end);
        }

        String blockContents(BlockTree block) {
            String blockSource = sourceOf(block);
            assertTrue(blockSource.startsWith("{") && blockSource.endsWith("}"));
            return blockSource.substring(1, blockSource.length() - 1);
        }

        String invocationLink(MethodInvocationTree invocation) {
            assertTrue(invocation.getMethodSelect() instanceof MemberSelectTree);
            MemberSelectTree select = (MemberSelectTree) invocation.getMethodSelect();
            long start = positions.getEndPosition(unit, select.getExpression());
            long end = positions.getEndPosition(unit, invocation);
            assertTrue(start >= 0 && end >= start, "missing invocation-link positions");
            return source.substring((int) start, (int) end);
        }
    }

    private static ParsedSource parse(Path path) throws IOException {
        String source = Files.readString(path);
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        assertTrue(compiler != null, "tests require a JDK compiler");
        JavaFileObject sourceFile = new SimpleJavaFileObject(URI.create("string:///" +
                path.getFileName()), JavaFileObject.Kind.SOURCE) {

            @Override
            public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                return source;
            }
        };
        JavacTask task = (JavacTask) compiler.getTask(
                null, null, null, List.of("-proc:none"), null, List.of(sourceFile));
        CompilationUnitTree unit = task.parse().iterator().next();
        return new ParsedSource(source, unit, Trees.instance(task).getSourcePositions());
    }

    private static ClassTree classNamed(ParsedSource source, String className) {
        List<ClassTree> matches = source.unit().getTypeDecls().stream()
                .filter(ClassTree.class::isInstance)
                .map(ClassTree.class::cast)
                .filter(type -> type.getSimpleName().contentEquals(className))
                .toList();
        assertEquals(1, matches.size(), "class declaration cardinality for " + className);
        return matches.get(0);
    }

    private static MethodTree methodNamed(ParsedSource source, String className, String methodName) {
        List<MethodTree> matches = classNamed(source, className).getMembers().stream()
                .filter(MethodTree.class::isInstance)
                .map(MethodTree.class::cast)
                .filter(method -> method.getName().contentEquals(methodName))
                .toList();
        assertEquals(1, matches.size(), "method declaration cardinality for " + methodName);
        return matches.get(0);
    }

    private static VariableTree fieldNamed(ParsedSource source, String className, String fieldName) {
        List<VariableTree> matches = classNamed(source, className).getMembers().stream()
                .filter(VariableTree.class::isInstance)
                .map(VariableTree.class::cast)
                .filter(field -> field.getName().contentEquals(fieldName))
                .toList();
        assertEquals(1, matches.size(), "field declaration cardinality for " + fieldName);
        return matches.get(0);
    }

    private static ExpressionStatementTree dimensionalHarvestingStatement(MethodTree register) {
        List<ExpressionStatementTree> matches = new ArrayList<>();
        new TreeScanner<Void, Void>() {

            @Override
            public Void visitExpressionStatement(ExpressionStatementTree statement, Void unused) {
                if (statement.getExpression() instanceof MethodInvocationTree invocation &&
                        methodName(invocation).equals("save") &&
                        containsRecipeBuilderId(invocation, "nether_interface/dimensional_harvesting")) {
                    matches.add(statement);
                }
                return super.visitExpressionStatement(statement, unused);
            }
        }.scan(register.getBody(), null);
        assertEquals(1, matches.size(), "dimensional harvesting statement cardinality");
        return matches.get(0);
    }

    private static boolean containsRecipeBuilderId(Tree tree, String recipeId) {
        boolean[] found = { false };
        new TreeScanner<Void, Void>() {

            @Override
            public Void visitMethodInvocation(MethodInvocationTree invocation, Void unused) {
                if (methodName(invocation).equals("recipeBuilder") &&
                        containsStringLiteral(invocation, recipeId)) {
                    found[0] = true;
                }
                return super.visitMethodInvocation(invocation, unused);
            }
        }.scan(tree, null);
        return found[0];
    }

    private static boolean containsStringLiteral(Tree tree, String value) {
        boolean[] found = { false };
        new TreeScanner<Void, Void>() {

            @Override
            public Void visitLiteral(LiteralTree literal, Void unused) {
                if (value.equals(literal.getValue())) {
                    found[0] = true;
                }
                return super.visitLiteral(literal, unused);
            }
        }.scan(tree, null);
        return found[0];
    }

    private static List<MethodInvocationTree> invocationChain(ExpressionTree initializer) {
        List<MethodInvocationTree> reversed = new ArrayList<>();
        ExpressionTree current = initializer;
        while (current instanceof MethodInvocationTree invocation &&
                invocation.getMethodSelect() instanceof MemberSelectTree select) {
            reversed.add(invocation);
            current = select.getExpression();
        }
        Collections.reverse(reversed);
        return reversed;
    }

    private static int invocationIndex(List<MethodInvocationTree> chain, String name) {
        List<Integer> matches = new ArrayList<>();
        for (int index = 0; index < chain.size(); index++) {
            if (methodName(chain.get(index)).equals(name)) {
                matches.add(index);
            }
        }
        assertEquals(1, matches.size(), "invocation cardinality for " + name);
        return matches.get(0);
    }

    private static int invocationCount(Tree tree, String name) {
        AtomicInteger count = new AtomicInteger();
        new TreeScanner<Void, Void>() {

            @Override
            public Void visitMethodInvocation(MethodInvocationTree invocation, Void unused) {
                if (methodName(invocation).equals(name)) {
                    count.incrementAndGet();
                }
                return super.visitMethodInvocation(invocation, unused);
            }
        }.scan(tree, null);
        return count.get();
    }

    private static boolean containsIdentifier(Tree tree, String name) {
        boolean[] found = { false };
        new TreeScanner<Void, Void>() {

            @Override
            public Void visitIdentifier(IdentifierTree identifier, Void unused) {
                if (identifier.getName().contentEquals(name)) {
                    found[0] = true;
                }
                return super.visitIdentifier(identifier, unused);
            }
        }.scan(tree, null);
        return found[0];
    }

    private static String methodName(MethodInvocationTree invocation) {
        if (invocation.getMethodSelect() instanceof MemberSelectTree select) {
            return select.getIdentifier().toString();
        }
        if (invocation.getMethodSelect() instanceof IdentifierTree identifier) {
            return identifier.getName().toString();
        }
        throw new AssertionError("unsupported method select: " + invocation.getMethodSelect());
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
