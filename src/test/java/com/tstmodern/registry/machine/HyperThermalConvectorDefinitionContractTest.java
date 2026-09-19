package com.tstmodern.registry.machine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

import org.junit.jupiter.api.Test;

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
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

final class HyperThermalConvectorDefinitionContractTest {

    private static final String EXPECTED_G_PREDICATE = """
            .where('G', blocks(TSTBlocks.ADVANCED_IRIDIUM_CASING.get())
                    .or(Predicates.autoAbilities(true, false, false))
                    .or(Predicates.autoAbilities(false, false, true)))
            """;

    private static final String EXPECTED_N_PREDICATE = """
            .where('N', blocks(TSTBlocks.IRIDIUM_REINFORCED_NEUTRONIUM_CASING.get()))
            """;

    private static final String EXPECTED_R_PREDICATE = """
            .where('R', Predicates.abilities(PartAbility.IMPORT_FLUIDS).setPreviewCount(1))
            """;

    private static final String EXPECTED_S_PREDICATE = """
            .where('S', Predicates.abilities(PartAbility.EXPORT_FLUIDS).setPreviewCount(1))
            """;

    private static final String EXPECTED_T_PREDICATE = """
            .where('T', Predicates.abilities(PartAbility.EXPORT_FLUIDS).setPreviewCount(1))
            """;

    private static final String EXPECTED_U_PREDICATE = """
            .where('U', Predicates.abilities(PartAbility.IMPORT_FLUIDS).setPreviewCount(1))
            """;

    private static final List<String> EXPECTED_PAIR_CALLS = List.of(
            """
                    addRapidHeatExchangePair(provider, "helium_plasma",
                            Helium.getFluid(FluidStorageKeys.PLASMA, 1000), 2000,
                            Helium.getFluid(FluidStorageKeys.GAS, 1000),
                            TSTMaterials.DENSE_SUPERCRITICAL_STEAM.getFluid(320_000))
                    """,
            """
                    addRapidHeatExchangePair(provider, "nitrogen_plasma",
                            Nitrogen.getFluid(FluidStorageKeys.PLASMA, 1000), 2000,
                            Nitrogen.getFluid(1000),
                            TSTMaterials.DENSE_SUPERCRITICAL_STEAM.getFluid(320_000))
                    """,
            """
                    addRapidHeatExchangePair(provider, "oxygen_plasma",
                            Oxygen.getFluid(FluidStorageKeys.PLASMA, 1000), 2000,
                            Oxygen.getFluid(FluidStorageKeys.GAS, 1000),
                            TSTMaterials.DENSE_SUPERCRITICAL_STEAM.getFluid(320_000))
                    """,
            """
                    addRapidHeatExchangePair(provider, "argon_plasma",
                            Argon.getFluid(FluidStorageKeys.PLASMA, 1000), 2000,
                            Argon.getFluid(1000),
                            TSTMaterials.DENSE_SUPERCRITICAL_STEAM.getFluid(320_000))
                    """,
            """
                    addRapidHeatExchangePair(provider, "iron_plasma",
                            Iron.getFluid(FluidStorageKeys.PLASMA, 1000), 2000,
                            Iron.getFluid(1000),
                            TSTMaterials.DENSE_SUPERCRITICAL_STEAM.getFluid(320_000))
                    """,
            """
                    addRapidHeatExchangePair(provider, "nickel_plasma",
                            Nickel.getFluid(FluidStorageKeys.PLASMA, 1000), 2000,
                            Nickel.getFluid(1000),
                            TSTMaterials.DENSE_SUPERCRITICAL_STEAM.getFluid(320_000))
                    """,
            """
                    addRapidHeatExchangePair(provider, "lava_cooling",
                            new FluidStack(Fluids.LAVA, 10_000), 10_000,
                            TSTMaterials.DENSE_SUPERHEATED_STEAM.getFluid(1_600_000),
                            DistilledWater.getFluid(2000))
                    """);

    private static final String EXPECTED_PAIR_HELPER_BODY = """
            for (boolean distilled : new boolean[] { false, true }) {
                String suffix = distilled ? "_distilled_water" : "";
                FluidStack coolingFluid = distilled ?
                        DistilledWater.getFluid(coolingAmount) :
                        new FluidStack(Fluids.WATER, coolingAmount);
                TSTRecipeTypes.RAPID_HEAT_EXCHANGE
                        .recipeBuilder(TSTModern.id("rapid_heat_exchange/" + recipeName + suffix))
                        .inputFluids(hotInput.copy())
                        .inputFluids(coolingFluid)
                        .outputFluids(firstOutput.copy())
                        .outputFluids(secondOutput.copy())
                        .duration(20)
                        .EUt(VA[UV])
                        .save(provider);
            }
            """;

    @Test
    void bindsDedicatedAbilitiesOnlyToTheirApprovedPatternCharacters() throws IOException {
        ParsedSource source = parse(Path.of(
                "src/main/java/com/tstmodern/registry/machine/HyperThermalConvectorDefinition.java"));
        VariableTree machine = fieldNamed(source, "HyperThermalConvectorDefinition", "MACHINE");

        assertEquals(normalizeWhitespace(EXPECTED_G_PREDICATE),
                normalizeWhitespace(source.invocationLink(whereFor(source, machine, 'G'))));
        assertEquals(normalizeWhitespace(EXPECTED_N_PREDICATE),
                normalizeWhitespace(source.invocationLink(whereFor(source, machine, 'N'))));
        assertEquals(normalizeWhitespace(EXPECTED_R_PREDICATE),
                normalizeWhitespace(source.invocationLink(whereFor(source, machine, 'R'))));
        assertEquals(normalizeWhitespace(EXPECTED_S_PREDICATE),
                normalizeWhitespace(source.invocationLink(whereFor(source, machine, 'S'))));
        assertEquals(normalizeWhitespace(EXPECTED_T_PREDICATE),
                normalizeWhitespace(source.invocationLink(whereFor(source, machine, 'T'))));
        assertEquals(normalizeWhitespace(EXPECTED_U_PREDICATE),
                normalizeWhitespace(source.invocationLink(whereFor(source, machine, 'U'))));

        assertEquals(0, invocationsNamed(machine.getInitializer(), "autoAbilities").stream()
                .filter(source::hasDefinitionRecipeTypesArgument)
                .count(), "generic recipe I/O must not appear anywhere in the machine definition");
    }

    @Test
    void registersExactlyTheSevenApprovedWaterAndDistilledWaterPairs() throws IOException {
        ParsedSource source = parse(Path.of(
                "src/main/java/com/tstmodern/data/recipe/HyperThermalConvectorRecipes.java"));
        MethodTree recipes = methodNamed(source, "HyperThermalConvectorRecipes",
                "addHyperThermalConvectorRecipes");
        List<String> actualCalls = invocationsNamed(recipes.getBody(), "addRapidHeatExchangePair").stream()
                .map(source::sourceOf)
                .map(HyperThermalConvectorDefinitionContractTest::normalizeWhitespace)
                .toList();

        assertEquals(7, actualCalls.size(), "rapid heat-exchange pair call-site cardinality");
        assertEquals(EXPECTED_PAIR_CALLS.stream().map(
                HyperThermalConvectorDefinitionContractTest::normalizeWhitespace).toList(), actualCalls);
    }

    @Test
    void pairHelperAddsOnlyTheApprovedSuffixAndSharesRecipeParameters() throws IOException {
        ParsedSource source = parse(Path.of(
                "src/main/java/com/tstmodern/data/recipe/HyperThermalConvectorRecipes.java"));
        MethodTree helper = methodNamed(source, "HyperThermalConvectorRecipes",
                "addRapidHeatExchangePair");
        String body = source.blockContents(helper.getBody());

        assertEquals(normalizeWhitespace(EXPECTED_PAIR_HELPER_BODY), normalizeWhitespace(body));
        assertEquals(1, literalCount(helper.getBody(), "_distilled_water"));
        assertEquals(1, invocationsNamed(helper.getBody(), "recipeBuilder").size());
        assertEquals(2, invocationsNamed(helper.getBody(), "inputFluids").size());
        assertEquals(2, invocationsNamed(helper.getBody(), "outputFluids").size());
        assertEquals(1, invocationsNamed(helper.getBody(), "duration").size());
        assertEquals(1, invocationsNamed(helper.getBody(), "EUt").size());
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

        boolean hasDefinitionRecipeTypesArgument(MethodInvocationTree invocation) {
            return invocation.getArguments().size() == 1 &&
                    normalizeWhitespace(sourceOf(invocation.getArguments().get(0)))
                            .equals("definition.getRecipeTypes()");
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

    private static MethodInvocationTree whereFor(ParsedSource source, VariableTree machine, char symbol) {
        List<MethodInvocationTree> matches = invocationsNamed(machine.getInitializer(), "where").stream()
                .filter(invocation -> invocation.getArguments().size() == 2)
                .filter(invocation -> invocation.getArguments().get(0) instanceof LiteralTree)
                .filter(invocation -> Character.valueOf(symbol).equals(
                        ((LiteralTree) invocation.getArguments().get(0)).getValue()))
                .toList();
        assertEquals(1, matches.size(), "where predicate cardinality for " + symbol);
        return matches.get(0);
    }

    private static List<MethodInvocationTree> invocationsNamed(Tree tree, String name) {
        List<MethodInvocationTree> matches = new ArrayList<>();
        new TreeScanner<Void, Void>() {

            @Override
            public Void visitMethodInvocation(MethodInvocationTree invocation, Void unused) {
                if (methodName(invocation).equals(name)) {
                    matches.add(invocation);
                }
                return super.visitMethodInvocation(invocation, unused);
            }
        }.scan(tree, null);
        return matches;
    }

    private static int literalCount(Tree tree, String value) {
        int[] count = { 0 };
        new TreeScanner<Void, Void>() {

            @Override
            public Void visitLiteral(LiteralTree literal, Void unused) {
                if (value.equals(literal.getValue())) {
                    count[0]++;
                }
                return super.visitLiteral(literal, unused);
            }
        }.scan(tree, null);
        return count[0];
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

    private static String normalizeWhitespace(String source) {
        return source.replaceAll("\\s+", " ").trim();
    }
}
