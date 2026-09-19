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

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
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

class BigBroArrayRecipesContractTest {

    private static final List<String> EXPECTED_RECIPE_IDS = List.of(
            "big_bro_array",
            "parallel_casing_mk1",
            "parallel_casing_mk2",
            "parallel_casing_mk3",
            "parallel_casing_mk4",
            "parallel_casing_mk5");

    @Test
    void allSixRecipesRegisteredWithCorrectParameters() throws IOException {
        ParsedSource source = parse(Path.of("src/main/java/com/tstmodern/data/recipe/BigBroArrayRecipes.java"));
        MethodTree register = methodNamed(source, "BigBroArrayRecipes", "register");
        List<String> recipeIds = recipeBuilderIds(register.getBody());

        assertEquals(EXPECTED_RECIPE_IDS, recipeIds, "Must register exactly the 6 BigBroArray recipes");

        String sourceText = source.sourceOf(register.getBody());
        // Check exact progression properties
        assertTrue(sourceText.contains("recipeBuilder(\"big_bro_array\")"), "big_bro_array recipe builder");
        assertTrue(sourceText.contains(".duration(24000)"), "big_bro_array / MK3/4/5 duration");
        assertTrue(sourceText.contains(".EUt(6400)"), "controller / MK1 EUt");
        assertTrue(sourceText.contains("recipeBuilder(\"parallel_casing_mk1\")"), "MK1 builder");
        assertTrue(sourceText.contains("recipeBuilder(\"parallel_casing_mk2\")"), "MK2 builder");
        assertTrue(sourceText.contains(".EUt(100000)"), "MK2 EUt");
        assertTrue(sourceText.contains("recipeBuilder(\"parallel_casing_mk3\")"), "MK3 builder");
        assertTrue(sourceText.contains(".EUt(2000000)"), "MK3 EUt");
        assertTrue(sourceText.contains("recipeBuilder(\"parallel_casing_mk4\")"), "MK4 builder");
        assertTrue(sourceText.contains(".EUt(8000000)"), "MK4 EUt");
        assertTrue(sourceText.contains("recipeBuilder(\"parallel_casing_mk5\")"), "MK5 builder");
        assertTrue(sourceText.contains(".EUt(32000000)"), "MK5 EUt");

        assertTrue(sourceText.contains("ROBOT_ARM_IV.asStack(32)"), "controller source arm count");
        assertTrue(sourceText.contains("EMITTER_IV.asStack(32)"), "controller source emitter count");
        assertTrue(sourceText.contains("FIELD_GENERATOR_IV.asStack(32)"), "controller source field generator count");
        assertTrue(sourceText.contains("PARALLEL_CASING_MK1.get(), 4"), "MK2 predecessor count");
        assertTrue(sourceText.contains("PARALLEL_CASING_MK2.get(), 4"), "MK3 predecessor count");
        assertTrue(sourceText.contains("CASING_TITANIUM_STABLE"), "MK2 stable titanium base");
        assertTrue(sourceText.contains("CASING_STAINLESS_CLEAN"), "MK3 clean stainless base");
        assertTrue(sourceText.contains("FUSION_CASING_MK3"), "MK4 fusion/endgame component");
        assertTrue(sourceText.contains("MACHINE_CASING_MAX"), "MK5 MAX-stage casing");
        assertTrue(sourceText.contains("TSTCircuitTags.get(UIV), 8"), "MK4 UIV circuit compatibility");
        assertTrue(sourceText.contains("TSTCircuitTags.get(UXV), 8"), "MK5 UXV circuit compatibility");
        assertTrue(sourceText.contains("TSTCircuitTags.get(MAX), 4"), "MK5 MAX circuit compatibility");
        assertFalse(sourceText.contains("catch (Throwable"), "registration failures must propagate");
    }

    private static List<String> recipeBuilderIds(Tree tree) {
        List<String> ids = new ArrayList<>();
        new TreeScanner<Void, Void>() {
            @Override
            public Void visitMethodInvocation(MethodInvocationTree invocation, Void unused) {
                if (methodName(invocation).equals("recipeBuilder")
                        && !invocation.getArguments().isEmpty()
                        && invocation.getArguments().get(0) instanceof LiteralTree literal
                        && literal.getValue() instanceof String id) {
                    ids.add(id);
                }
                return super.visitMethodInvocation(invocation, unused);
            }
        }.scan(tree, null);
        return ids;
    }

    private static MethodTree methodNamed(ParsedSource source, String className, String methodName) {
        for (Tree typeDecl : source.unit().getTypeDecls()) {
            if (typeDecl instanceof ClassTree classTree && classTree.getSimpleName().contentEquals(className)) {
                for (Tree member : classTree.getMembers()) {
                    if (member instanceof MethodTree method && method.getName().contentEquals(methodName)) {
                        return method;
                    }
                }
            }
        }
        throw new AssertionError("Method " + methodName + " not found in " + className);
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

    private static ParsedSource parse(Path path) throws IOException {
        String code = Files.readString(path);
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException("JDK compiler is required for contract AST tests");
        }
        JavaFileObject fileObject = new SimpleJavaFileObject(
                URI.create("string:///" + path.getFileName()),
                JavaFileObject.Kind.SOURCE) {
            @Override
            public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                return code;
            }
        };
        JavacTask task = (JavacTask) compiler.getTask(null, null, null, List.of(), null, List.of(fileObject));
        CompilationUnitTree unit = task.parse().iterator().next();
        Trees trees = Trees.instance(task);
        return new ParsedSource(code, unit, trees.getSourcePositions());
    }

    private record ParsedSource(String text, CompilationUnitTree unit, SourcePositions positions) {
        String sourceOf(Tree tree) {
            int start = (int) positions.getStartPosition(unit, tree);
            int end = (int) positions.getEndPosition(unit, tree);
            return text.substring(start, end);
        }
    }
}
