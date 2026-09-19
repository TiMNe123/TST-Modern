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
import java.util.Map;

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
            "chemical_dehydrator/sponge_drying",
            "chemical_dehydrator/strange_dust_separation",
            "chemical_dehydrator/fluorcaphite_decomposition");

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
            "chemical_dehydrator/strange_dust_separation",
            "chemical_dehydrator/fluorcaphite_decomposition",
            "electrolyzer/samarskite_y_decomposition",
            "electrolyzer/titanite_decomposition",
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

    private static final Map<String, List<String>> EXPECTED_STRUCTURE_CASING_OUTPUTS = Map.of(
            "MegaStoneBreakerRecipes.java", List.of(
                    "BLACK_PLUTONIUM_ITEM_PIPE_CASING",
                    "STABLE_RED_STEEL_CASING",
                    "STABLE_TANTALLOY_61_CASING",
                    "STABALOY_FIREBOX_CASING"),
            "GiantVacuumDryingFurnaceRecipes.java", List.of("NEUTRONIUM_MINING_CASING"),
            "HyperThermalConvectorRecipes.java", List.of(
                    "HS188A_BLOCK",
                    "QUANTUM_ALLOY_BLOCK",
                    "EXTREME_DENSITY_CASING",
                    "OSMIRIDIUM_MINING_CASING",
                    "TANK_CASING_TIER_10",
                    "DYSON_SWARM_FLOOR"),
            "MegaTreeFarmRecipes.java", List.of("VENT_T2_CASING"));

    private static final Map<String, List<String>> EXPECTED_STATION_RESEARCH_CASINGS = Map.of(
            "BigBroArrayRecipes.java", List.of(
                    "PARALLEL_CASING_MK2",
                    "PARALLEL_CASING_MK3",
                    "PARALLEL_CASING_MK4",
                    "PARALLEL_CASING_MK5"),
            "MegaTreeFarmRecipes.java", List.of(
                    "ASEPTIC_GREENHOUSE_CASING",
                    "INTEGRAL_FRAMEWORK_UV_CASING",
                    "COMPOSITE_FARM_CASING",
                    "AIR_CRYSTAL_CASING",
                    "WATER_CRYSTAL_CASING",
                    "EARTH_CRYSTAL_CASING"),
            "HyperThermalConvectorRecipes.java", List.of(
                    "HS188A_BLOCK",
                    "QUANTUM_ALLOY_BLOCK",
                    "EXTREME_DENSITY_CASING",
                    "TANK_CASING_TIER_10",
                    "DYSON_SWARM_FLOOR",
                    "IRIDIUM_REINFORCED_NEUTRONIUM_CASING",
                    "BOROPHENE_NANOWIRE_CASING",
                    "NEUTRONIUM_PIPE_CASING"));

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

    @Test
    void disassemblerRetainsSeventeenRegisteredRecipeSpecs() {
        List<com.tstmodern.data.recipe.DisassemblerRecipes.RecipeSpec> specs = com.tstmodern.data.recipe.DisassemblerRecipes.recipeSpecs();
        assertEquals(17, specs.size(), "Disassembler recipe spec cardinality");

        long assemblerCasingCount = specs.stream()
                .filter(s -> s.type() == com.tstmodern.data.recipe.DisassemblerRecipes.RecipeType.ASSEMBLER && s.id().contains("component_assembly_line_casing"))
                .count();
        long assemblyLineCasingCount = specs.stream()
                .filter(s -> s.type() == com.tstmodern.data.recipe.DisassemblerRecipes.RecipeType.ASSEMBLY_LINE && s.id().contains("component_assembly_line_casing"))
                .count();
        long molecularCount = specs.stream()
                .filter(s -> s.id().equals("assembler/molecular_casing"))
                .count();
        long hollowCount = specs.stream()
                .filter(s -> s.id().equals("assembly_line/hollow_casing"))
                .count();
        long disassemblerCount = specs.stream()
                .filter(s -> s.id().equals("assembly_line/disassembler"))
                .count();

        assertEquals(5, assemblerCasingCount, "LV through IV assembler casing count");
        assertEquals(9, assemblyLineCasingCount, "LuV through MAX assembly line casing count");
        assertEquals(1, molecularCount, "Molecular casing recipe count");
        assertEquals(1, hollowCount, "Hollow casing recipe count");
        assertEquals(1, disassemblerCount, "Disassembler controller recipe count");

        var disassemblerSpec = specs.stream()
                .filter(s -> s.id().equals("assembly_line/disassembler"))
                .findFirst()
                .orElseThrow();
        assertEquals(com.tstmodern.data.recipe.DisassemblerRecipes.ResearchKind.STATION,
                disassemblerSpec.research().kind(), "Disassembler controller requires Research Station");
        assertEquals(256, disassemblerSpec.research().cwuPerTick(), "Disassembler controller requires 256 CWU/t");
        assertEquals("tool/data_orb", disassemblerSpec.research().dataStack().key(), "Disassembler controller requires Data Orb");
    }

    @Test
    void hollowCasingUsesStationResearchWithItsModernPredecessor() {
        var hollow = DisassemblerRecipes.recipeSpecs().stream()
                .filter(spec -> spec.id().equals("assembly_line/hollow_casing"))
                .findFirst()
                .orElseThrow();

        assertEquals(DisassemblerRecipes.ResearchKind.STATION, hollow.research().kind());
        assertEquals("block/molecular_casing", hollow.research().target().key());
        assertEquals(1_000, hollow.research().durationTicks());
        assertEquals(32, hollow.research().cwuPerTick());
        assertEquals("tool/data_orb", hollow.research().dataStack().key());
    }

    @Test
    void componentAssemblyLineCasingsSkipResearchThroughIvUseScannerAtLuvAndStationFromZpm() {
        var casingSpecs = DisassemblerRecipes.recipeSpecs().stream()
                .filter(spec -> spec.id().contains("component_assembly_line_casing"))
                .toList();

        assertEquals(List.of(
                        DisassemblerRecipes.ResearchKind.NONE,
                        DisassemblerRecipes.ResearchKind.NONE,
                        DisassemblerRecipes.ResearchKind.NONE,
                        DisassemblerRecipes.ResearchKind.NONE,
                        DisassemblerRecipes.ResearchKind.NONE,
                        DisassemblerRecipes.ResearchKind.SCANNER,
                        DisassemblerRecipes.ResearchKind.STATION,
                        DisassemblerRecipes.ResearchKind.STATION,
                        DisassemblerRecipes.ResearchKind.STATION,
                        DisassemblerRecipes.ResearchKind.STATION,
                        DisassemblerRecipes.ResearchKind.STATION,
                        DisassemblerRecipes.ResearchKind.STATION,
                        DisassemblerRecipes.ResearchKind.STATION,
                        DisassemblerRecipes.ResearchKind.STATION),
                casingSpecs.stream().map(spec -> spec.research().kind()).toList());
        casingSpecs.stream().skip(6).forEach(spec ->
                assertEquals("tool/data_orb", spec.research().dataStack().key(),
                        spec.id() + " requires the green Data Orb"));
    }

    @Test
    void approvedCasingDependenciesAreRegisteredWithoutSpeculativeContainmentCasing() throws IOException {
        String materials = Files.readString(Path.of("src/main/java/com/tstmodern/registry/TSTMaterials.java"));
        String blocks = Files.readString(Path.of("src/main/java/com/tstmodern/registry/TSTBlocks.java"));
        String megaStone = Files.readString(Path.of("src/main/java/com/tstmodern/data/recipe/MegaStoneBreakerRecipes.java"));
        String megaStoneDefinition = Files.readString(Path.of(
                "src/main/java/com/tstmodern/registry/machine/MegaStoneBreakerDefinition.java"));
        String hyperDefinition = Files.readString(Path.of(
                "src/main/java/com/tstmodern/registry/machine/HyperThermalConvectorDefinition.java"));

        assertFalse(blocks.contains("CONTAINMENT_FIELD_CASING"));
        assertTrue(megaStone.contains("outputItems(TSTBlocks.PRESSURE_RESISTANT_WALL)"));
        assertTrue(megaStoneDefinition.contains("blocks(TSTBlocks.PRESSURE_RESISTANT_WALL.get())"));
        assertTrue(hyperDefinition.contains("blocks(TSTBlocks.PRESSURE_RESISTANT_WALL.get())"));
        assertTrue(materials.contains("GTMaterials.Tritanium.addFlags("));
        assertTrue(materials.contains("MaterialFlags.GENERATE_DENSE"));
        assertTrue(materials.contains("GTMaterials.Iridium.addFlags(MaterialFlags.GENERATE_ROTOR)"));
    }

    @Test
    void collidingStationRecipesUseDistinctResearchPredecessors() throws IOException {
        String treeRecipes = Files.readString(Path.of(
                "src/main/java/com/tstmodern/data/recipe/MegaTreeFarmRecipes.java"));
        String hyperRecipes = Files.readString(Path.of(
                "src/main/java/com/tstmodern/data/recipe/HyperThermalConvectorRecipes.java"));

        assertTrue(treeRecipes.contains(".researchStack(TSTBlocks.AIR_CRYSTAL_CASING.get().asItem().getDefaultInstance())"));
        assertTrue(treeRecipes.contains(".researchStack(TSTBlocks.WATER_CRYSTAL_CASING.get().asItem().getDefaultInstance())"));
        assertTrue(hyperRecipes.contains(".researchStack(TSTBlocks.IRIDIUM_REINFORCED_NEUTRONIUM_CASING.get().asItem().getDefaultInstance())"));
        assertTrue(hyperRecipes.contains(".researchStack(TSTBlocks.HS188A_BLOCK.get().asItem().getDefaultInstance())"));
    }

    @Test
    void everyRemainingCustomStructureCasingHasAProductionRecipe() throws IOException {
        for (var entry : EXPECTED_STRUCTURE_CASING_OUTPUTS.entrySet()) {
            ParsedSource source = parse(Path.of("src/main/java/com/tstmodern/data/recipe", entry.getKey()));
            List<String> outputs = tstBlockOutputs(source.unit());
            entry.getValue().forEach(expected -> assertTrue(outputs.contains(expected),
                    entry.getKey() + " must produce TSTBlocks." + expected));
        }
    }

    @Test
    void zpmAndLaterCasingRecipesUseStationResearchAndGreenDataOrb() throws IOException {
        for (var entry : EXPECTED_STATION_RESEARCH_CASINGS.entrySet()) {
            ParsedSource source = parse(Path.of("src/main/java/com/tstmodern/data/recipe", entry.getKey()));
            for (String casing : entry.getValue()) {
                List<ExpressionStatementTree> recipes = topLevelExpressionStatements(source.unit()).stream()
                        .filter(statement -> tstBlockOutputs(statement).contains(casing))
                        .toList();
                assertEquals(1, recipes.size(), entry.getKey() + " recipe cardinality for " + casing);
                assertTrue(containsInvocationNamed(recipes.get(0), "stationResearch"),
                        casing + " must use Research Station");
                assertTrue(containsInvocationNamed(recipes.get(0), "researchId"),
                        casing + " must have a unique research id");
                assertTrue(containsMemberSelect(recipes.get(0), "GTItems", "TOOL_DATA_ORB"),
                        casing + " must use the green Data Orb");
            }
        }
    }

    @Test
    void hyperThermalControllerUsesStationResearchAndPurpleDataModule() throws IOException {
        ParsedSource source = parse(Path.of(
                "src/main/java/com/tstmodern/data/recipe/HyperThermalConvectorRecipes.java"));
        List<ExpressionStatementTree> recipes = topLevelExpressionStatements(source.unit()).stream()
                .filter(statement -> containsMemberSelect(
                        statement, "HyperThermalConvectorDefinition", "MACHINE"))
                .toList();

        assertEquals(1, recipes.size(), "Hyper Thermal controller recipe cardinality");
        ExpressionStatementTree recipe = recipes.get(0);
        assertTrue(containsInvocationNamed(recipe, "stationResearch"));
        assertEquals("hyper_thermal_convector",
                stringLiteralArgument(singleInvocationNamed(recipe, "researchId"), 0));
        assertTrue(containsMemberSelect(recipe, "GTItems", "TOOL_DATA_MODULE"));
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

    private static List<ExpressionStatementTree> topLevelExpressionStatements(Tree tree) {
        List<ExpressionStatementTree> statements = new ArrayList<>();
        new TreeScanner<Void, Void>() {

            @Override
            public Void visitExpressionStatement(ExpressionStatementTree statement, Void unused) {
                statements.add(statement);
                return null;
            }
        }.scan(tree, null);
        return statements;
    }

    private static List<String> tstBlockOutputs(Tree tree) {
        List<String> outputs = new ArrayList<>();
        invocationsNamed(tree, "outputItems").forEach(invocation -> invocation.getArguments().forEach(argument ->
                new TreeScanner<Void, Void>() {

                    @Override
                    public Void visitMemberSelect(MemberSelectTree select, Void unused) {
                        if (select.getExpression() instanceof IdentifierTree owner &&
                                owner.getName().contentEquals("TSTBlocks")) {
                            outputs.add(select.getIdentifier().toString());
                        }
                        return super.visitMemberSelect(select, unused);
                    }
                }
                        .scan(argument, null)));
        return outputs;
    }

    private static boolean containsMemberSelect(Tree tree, String ownerName, String memberName) {
        boolean[] found = { false };
        new TreeScanner<Void, Void>() {

            @Override
            public Void visitMemberSelect(MemberSelectTree select, Void unused) {
                if (select.getExpression() instanceof IdentifierTree owner &&
                        owner.getName().contentEquals(ownerName) &&
                        select.getIdentifier().contentEquals(memberName)) {
                    found[0] = true;
                }
                return super.visitMemberSelect(select, unused);
            }
        }.scan(tree, null);
        return found[0];
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
