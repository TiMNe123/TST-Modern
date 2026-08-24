package com.tstmodern.data.recipe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.TreeScanner;
import com.sun.source.util.Trees;

final class PortRecipeCatalogContractTest {

    private static final List<String> EXPECTED_MEGA_DIRECT_RECIPES = List.of(
            "obsidian",
            "netherrack",
            "basalt",
            "cobbled_deepslate");

    private static final List<String> EXPECTED_GIANT_DEHYDRATOR_IDS = List.of(
            "chemical_dehydrator/calcium_chloride",
            "chemical_dehydrator/magnesium_chloride",
            "chemical_dehydrator/lithium_chloride",
            "chemical_dehydrator/bauxite_dehydration",
            "chemical_dehydrator/rare_earth",
            "chemical_dehydrator/clay_drying",
            "chemical_dehydrator/sponge_drying");

    private static final List<String> EXPECTED_GIANT_VACUUM_IDS = List.of(
            "vacuum_furnace/annealed_copper",
            "vacuum_furnace/silicon_annealing",
            "vacuum_furnace/titanium_sintering",
            "vacuum_furnace/tungsten_sintering",
            "vacuum_furnace/naquadah",
            "vacuum_furnace/naquadah_alloy",
            "vacuum_furnace/trinium",
            "vacuum_furnace/hssg",
            "vacuum_furnace/hsse",
            "vacuum_furnace/hsss");

    private static final List<String> EXPECTED_GIANT_ALL_RECIPE_IDS = List.of(
            "chemical_dehydrator/calcium_chloride",
            "chemical_dehydrator/magnesium_chloride",
            "chemical_dehydrator/lithium_chloride",
            "chemical_dehydrator/bauxite_dehydration",
            "chemical_dehydrator/rare_earth",
            "chemical_dehydrator/clay_drying",
            "chemical_dehydrator/sponge_drying",
            "vacuum_furnace/annealed_copper",
            "vacuum_furnace/silicon_annealing",
            "vacuum_furnace/titanium_sintering",
            "vacuum_furnace/tungsten_sintering",
            "vacuum_furnace/naquadah",
            "vacuum_furnace/naquadah_alloy",
            "vacuum_furnace/trinium",
            "vacuum_furnace/hssg",
            "vacuum_furnace/hsse",
            "vacuum_furnace/hsss");

    private static final List<String> EXPECTED_HYPER_PAIR_NAMES = List.of(
            "helium_plasma",
            "nitrogen_plasma",
            "oxygen_plasma",
            "argon_plasma",
            "iron_plasma",
            "nickel_plasma",
            "lava_cooling");

    private static final List<String> EXPECTED_HYPER_DISTILLED_IDS = List.of(
            "rapid_heat_exchange/helium_plasma_distilled_water",
            "rapid_heat_exchange/nitrogen_plasma_distilled_water",
            "rapid_heat_exchange/oxygen_plasma_distilled_water",
            "rapid_heat_exchange/argon_plasma_distilled_water",
            "rapid_heat_exchange/iron_plasma_distilled_water",
            "rapid_heat_exchange/nickel_plasma_distilled_water",
            "rapid_heat_exchange/lava_cooling_distilled_water");

    private static final String EXPECTED_HYPER_PAIR_HELPER_BODY = """
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
    void megaStoneBreakerRetainsTenBasicAndFourDirectProcessingRecipes() throws IOException {
        ParsedSource source = parse(Path.of("src/main/java/com/tstmodern/data/recipe/MegaStoneBreakerRecipes.java"));
        MethodTree recipes = methodNamed(source, "MegaStoneBreakerRecipes", "addMegaStoneBreakerRecipes");

        List<MethodInvocationTree> basicCalls = invocationsNamed(recipes.getBody(), "basic");
        List<String> directRecipes = topLevelExpressionStatements(recipes.getBody()).stream()
                .filter(statement -> containsInvocationNamed(statement, "recipe"))
                .map(statement -> singleInvocationNamed(statement, "recipe"))
                .map(invocation -> stringLiteralArgument(invocation, 0))
                .toList();

        assertEquals(10, basicCalls.size(), "basic helper recipe count");
        assertEquals(EXPECTED_MEGA_DIRECT_RECIPES, directRecipes);
        assertEquals(14, basicCalls.size() + directRecipes.size(), "total Mega Stone Breaker processing recipes");
    }

    @Test
    void giantVacuumDryingFurnaceRetainsApprovedRecipeIdCounts() throws IOException {
        ParsedSource source = parse(Path.of(
                "src/main/java/com/tstmodern/data/recipe/GiantVacuumDryingFurnaceRecipes.java"));
        MethodTree recipes = methodNamed(source, "GiantVacuumDryingFurnaceRecipes",
                "addGiantVacuumDryingFurnaceRecipes");
        List<String> recipeIds = recipeBuilderIds(recipes.getBody());
        List<String> dehydratorIds = recipeIds.stream()
                .filter(id -> id.startsWith("chemical_dehydrator/"))
                .toList();
        List<String> vacuumIds = recipeIds.stream()
                .filter(id -> id.startsWith("vacuum_furnace/"))
                .toList();

        assertEquals(EXPECTED_GIANT_ALL_RECIPE_IDS, recipeIds);
        assertEquals(EXPECTED_GIANT_DEHYDRATOR_IDS, dehydratorIds);
        assertEquals(EXPECTED_GIANT_VACUUM_IDS, vacuumIds);
    }

    @Test
    void netherInterfaceRetainsSingleDistilledWaterCoreRecipeWithoutLegacyFluids() throws IOException {
        ParsedSource source = parse(Path.of("src/main/java/com/tstmodern/data/recipe/NetherInterfaceRecipes.java"));
        MethodTree register = methodNamed(source, "NetherInterfaceRecipes", "register");
        List<String> recipeIds = recipeBuilderIds(register.getBody());
        List<ExpressionStatementTree> harvestingStatements = topLevelExpressionStatements(register.getBody()).stream()
                .filter(statement -> containsRecipeBuilderId(statement, "nether_interface/dimensional_harvesting"))
                .toList();

        assertEquals(1L, recipeIds.stream().filter("nether_interface/dimensional_harvesting"::equals).count());
        assertEquals(1, harvestingStatements.size(), "dimensional harvesting statement cardinality");
        assertFalse(containsSimpleName(harvestingStatements.get(0), "LiquidNetherAir"));
        assertFalse(containsSimpleName(harvestingStatements.get(0), "LAVA"));
    }

    @Test
    void hyperThermalConvectorRetainsSevenPairsAndStableDistilledIds() throws IOException {
        ParsedSource source = parse(Path.of(
                "src/main/java/com/tstmodern/data/recipe/HyperThermalConvectorRecipes.java"));
        MethodTree recipes = methodNamed(source, "HyperThermalConvectorRecipes",
                "addHyperThermalConvectorRecipes");
        MethodTree helper = methodNamed(source, "HyperThermalConvectorRecipes", "addRapidHeatExchangePair");

        List<String> pairNames = invocationsNamed(recipes.getBody(), "addRapidHeatExchangePair").stream()
                .map(invocation -> stringLiteralArgument(invocation, 1))
                .toList();
        List<String> distilledIds = pairNames.stream()
                .map(name -> "rapid_heat_exchange/" + name + "_distilled_water")
                .toList();

        assertEquals(EXPECTED_HYPER_PAIR_NAMES, pairNames);
        assertEquals(7, pairNames.size(), "rapid heat-exchange pair call-site count");
        assertEquals(EXPECTED_HYPER_DISTILLED_IDS, distilledIds);
        assertEquals(normalizeWhitespace(EXPECTED_HYPER_PAIR_HELPER_BODY),
                normalizeWhitespace(source.blockContents(helper.getBody())));
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

    private static List<ExpressionStatementTree> topLevelExpressionStatements(BlockTree block) {
        return block.getStatements().stream()
                .filter(ExpressionStatementTree.class::isInstance)
                .map(ExpressionStatementTree.class::cast)
                .toList();
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

    private static MethodInvocationTree singleInvocationNamed(Tree tree, String name) {
        List<MethodInvocationTree> matches = invocationsNamed(tree, name);
        assertEquals(1, matches.size(), "invocation cardinality for " + name);
        return matches.get(0);
    }

    private static boolean containsInvocationNamed(Tree tree, String name) {
        return !invocationsNamed(tree, name).isEmpty();
    }

    private static List<String> recipeBuilderIds(Tree tree) {
        return invocationsNamed(tree, "recipeBuilder").stream()
                .map(PortRecipeCatalogContractTest::recipeBuilderId)
                .filter(id -> id != null)
                .toList();
    }

    private static boolean containsRecipeBuilderId(Tree tree, String recipeId) {
        return recipeBuilderIds(tree).stream().anyMatch(recipeId::equals);
    }

    private static String recipeBuilderId(MethodInvocationTree invocation) {
        if (invocation.getArguments().size() != 1) {
            return null;
        }
        Tree argument = invocation.getArguments().get(0);
        if (!(argument instanceof MethodInvocationTree idCall) || !methodName(idCall).equals("id")) {
            return null;
        }
        return stringLiteralArgument(idCall, 0);
    }

    private static String stringLiteralArgument(MethodInvocationTree invocation, int index) {
        assertTrue(index < invocation.getArguments().size(), "missing argument " + index);
        Tree argument = invocation.getArguments().get(index);
        assertTrue(argument instanceof LiteralTree, "argument " + index + " must be a string literal");
        Object value = ((LiteralTree) argument).getValue();
        assertTrue(value instanceof String, "argument " + index + " must be a string literal");
        return (String) value;
    }

    private static boolean containsSimpleName(Tree tree, String name) {
        boolean[] found = { false };
        new TreeScanner<Void, Void>() {

            @Override
            public Void visitIdentifier(IdentifierTree identifier, Void unused) {
                if (identifier.getName().contentEquals(name)) {
                    found[0] = true;
                }
                return super.visitIdentifier(identifier, unused);
            }

            @Override
            public Void visitMemberSelect(MemberSelectTree memberSelect, Void unused) {
                if (memberSelect.getIdentifier().contentEquals(name)) {
                    found[0] = true;
                }
                return super.visitMemberSelect(memberSelect, unused);
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

    private static String normalizeWhitespace(String source) {
        return source.replaceAll("\\s+", " ").trim();
    }
}
