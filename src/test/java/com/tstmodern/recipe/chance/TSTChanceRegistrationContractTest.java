package com.tstmodern.recipe.chance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

final class TSTChanceRegistrationContractTest {

    private static final Path CHANCE_LOGICS_SOURCE =
            Path.of("src/main/java/com/tstmodern/recipe/chance/TSTChanceLogics.java");

    @Test
    void listensForTheGtceuChanceLogicRegistrationEvent() throws IOException {
        String source = Files.readString(Path.of("src/main/java/com/tstmodern/TSTModern.java"));

        assertTrue(source.contains(
                "modBus.addGenericListener(ChanceLogic.class, TSTChanceLogics::registerChanceLogics);"));
    }

    @Test
    void declaresBothNamespacedChanceLogicIds() throws IOException {
        String source = Files.readString(CHANCE_LOGICS_SOURCE);

        assertTrue(source.contains("tstmodern:three_weighted_scaled"));
        assertTrue(source.contains("tstmodern:single_roll_scaled"));
    }

    @Test
    void productionRollBodiesMatchTheOnlyAllowedControlFlowAndArguments() throws IOException {
        String source = Files.readString(CHANCE_LOGICS_SOURCE);
        String weightedClass = section(
                source,
                "private static final class ThreeWeightedScaledChanceLogic",
                "private static final class SingleRollScaledChanceLogic");
        String weightedRollBody = methodBody(weightedClass, "public @Unmodifiable List<@NotNull Content> roll(");
        String fluidClass = source.substring(
                source.indexOf("private static final class SingleRollScaledChanceLogic"));
        String fluidRollBody = methodBody(fluidClass, "public @Unmodifiable List<@NotNull Content> roll(");

        String expectedWeightedBody = """
                if (chancedEntries.isEmpty()) {
                    return Collections.emptyList();
                }
                return NetherInterfaceLogic.selectAndScaleThreeWeighted(
                        chancedEntries,
                        entry -> entry.chance,
                        GTValues.RNG::nextInt,
                        (selected, scale) -> selected.copyChanced(cap, ContentModifier.multiplier(scale)),
                        times);
                """;
        String expectedFluidBody = """
                if (chancedEntries.isEmpty()) {
                    return Collections.emptyList();
                }
                Content entry = chancedEntries.get(0);
                return NetherInterfaceLogic.rollAndScaleOnce(
                        entry,
                        selected -> selected.chance,
                        selected -> selected.maxChance,
                        GTValues.RNG::nextInt,
                        (selected, scale) -> selected.copyChanced(cap, ContentModifier.multiplier(scale)),
                        times);
                """;

        assertEquals(normalizeWhitespace(expectedWeightedBody), normalizeWhitespace(weightedRollBody),
                "weighted production must have only the empty guard and exact raw/scaled seam return");
        assertEquals(normalizeWhitespace(expectedFluidBody), normalizeWhitespace(fluidRollBody),
                "fluid production must have only the empty guard, first entry, and exact raw/scaled seam return");
    }

    @Test
    void constructorSelfRegistrationIsNotDuplicatedThroughTheEvent() throws IOException {
        String source = Files.readString(CHANCE_LOGICS_SOURCE);
        String registrationBody = methodBody(source, "public static void registerChanceLogics(");

        assertFalse(registrationBody.matches("(?s).*\\.\\s*register\\s*\\(.*"),
                "ChanceLogic constructors self-register; any receiver.register call would register twice");
        assertEquals(1, occurrences(registrationBody,
                "THREE_WEIGHTED_SCALED = new ThreeWeightedScaledChanceLogic();"),
                "the weighted logic must be constructed and assigned exactly once");
        assertEquals(1, occurrences(registrationBody,
                "SINGLE_ROLL_SCALED = new SingleRollScaledChanceLogic();"),
                "the fluid logic must be constructed and assigned exactly once");
    }

    private static String section(String source, String startMarker, String endMarker) {
        int start = source.indexOf(startMarker);
        int end = source.indexOf(endMarker, start);
        assertTrue(start >= 0, "missing source marker: " + startMarker);
        assertTrue(end > start, "missing source marker: " + endMarker);
        return source.substring(start, end);
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
