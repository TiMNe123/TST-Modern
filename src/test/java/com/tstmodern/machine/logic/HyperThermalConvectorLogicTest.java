package com.tstmodern.machine.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

import org.junit.jupiter.api.Test;

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.SourcePositions;
import com.sun.source.util.Trees;

class HyperThermalConvectorLogicTest {

    private static final String EXPECTED_MODIFIER_BODY = """
            if (!(machine instanceof HyperThermalConvectorMachine convector)) {
                return ModifierFunction.NULL;
            }

            boolean heatExchange = recipe.recipeType == TSTRecipeTypes.RAPID_HEAT_EXCHANGE;
            int limit = HyperThermalConvectorLogic.parallelLimit(
                    HyperThermalConvectorLogic.baseParallel(heatExchange),
                    convector.getParallelHatch().map(h -> h.getCurrentParallel()).orElse(0));
            int parallel = ParallelLogic.getParallelAmount(machine, recipe, limit);
            if (parallel <= 0) {
                return ModifierFunction.NULL;
            }

            return ModifierFunction.builder()
                    .inputModifier(ContentModifier.multiplier(parallel))
                    .outputModifier(ContentModifier.multiplier(parallel))
                    .eutMultiplier(parallel)
                    .parallels(parallel)
                    .build();
            """;

    @Test
    void assignsApprovedBaseParallelPerMode() {
        assertEquals(128, HyperThermalConvectorLogic.baseParallel(true));
        assertEquals(16, HyperThermalConvectorLogic.baseParallel(false));
    }

    @Test
    void addsRatherThanReplacesParallelHatch() {
        assertEquals(144, HyperThermalConvectorLogic.parallelLimit(128, 16));
        assertEquals(32, HyperThermalConvectorLogic.parallelLimit(16, 16));
        assertEquals(Integer.MAX_VALUE,
                HyperThermalConvectorLogic.parallelLimit(Integer.MAX_VALUE, 64));
    }

    @Test
    void activeModifierSelectsModeByRecipeTypeIdentityAndScalesExactlyOnce() throws IOException {
        ParsedSource source = parse(Path.of(
                "src/main/java/com/tstmodern/machine/HyperThermalConvectorMachine.java"));
        MethodTree modifier = methodNamed(source, "HyperThermalConvectorMachine", "recipeModifier");
        String body = source.blockContents(modifier.getBody());

        assertEquals(normalizeWhitespace(EXPECTED_MODIFIER_BODY), normalizeWhitespace(body));
        assertEquals(1, occurrences(body, "recipe.recipeType == TSTRecipeTypes.RAPID_HEAT_EXCHANGE"));
        assertEquals(1, occurrences(body, "ParallelLogic.getParallelAmount(machine, recipe, limit)"));
        assertEquals(1, occurrences(body, ".inputModifier(ContentModifier.multiplier(parallel))"));
        assertEquals(1, occurrences(body, ".outputModifier(ContentModifier.multiplier(parallel))"));
        assertEquals(1, occurrences(body, ".eutMultiplier(parallel)"));
        assertEquals(1, occurrences(body, ".parallels(parallel)"));
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
