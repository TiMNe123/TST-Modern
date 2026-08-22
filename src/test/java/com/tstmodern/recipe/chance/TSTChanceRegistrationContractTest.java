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
    void weightedProductionUsesRawWeightsThreeSelectionsAndScaledCopies() throws IOException {
        String source = Files.readString(CHANCE_LOGICS_SOURCE);
        String weightedClass = section(
                source,
                "private static final class ThreeWeightedScaledChanceLogic",
                "private static final class SingleRollScaledChanceLogic");
        String rollBody = methodBody(weightedClass, "public @Unmodifiable List<@NotNull Content> roll(");

        assertTrue(rollBody.contains(
                "int[] weights = chancedEntries.stream().mapToInt(entry -> entry.chance).toArray();"),
                "weighted production must use each entry's raw chance as its weight");
        assertEquals(1, occurrences(rollBody, "NetherInterfaceLogic.selectThreeWeighted("),
                "weighted production must delegate once to the exactly-three selection seam");
        assertTrue(rollBody.contains(
                "int[] selectedIndexes = NetherInterfaceLogic.selectThreeWeighted(GTValues.RNG::nextInt, weights);"),
                "weighted production must use the indexes returned by the exactly-three selection seam");
        assertEquals(1, occurrences(rollBody,
                "selected.copyChanced(cap, ContentModifier.multiplier(times))"),
                "every selected package must be scaled through copyChanced by times");
        assertFalse(rollBody.contains("selected.copy(cap)"),
                "selected packages must not bypass chanced scaling with an unmodified copy");
        assertIgnoresBoostTierAndCache(rollBody);
    }

    @Test
    void fluidProductionUsesRawChanceOneRollAndScaledCopy() throws IOException {
        String source = Files.readString(CHANCE_LOGICS_SOURCE);
        String fluidClass = source.substring(
                source.indexOf("private static final class SingleRollScaledChanceLogic"));
        String rollBody = methodBody(fluidClass, "public @Unmodifiable List<@NotNull Content> roll(");

        assertEquals(1, occurrences(rollBody, "NetherInterfaceLogic.rollOnce("),
                "fluid production must delegate once to the exactly-one roll seam");
        assertTrue(rollBody.contains(
                "if (!NetherInterfaceLogic.rollOnce(GTValues.RNG::nextInt, entry.chance, entry.maxChance))"),
                "fluid production must roll the entry's raw chance against its raw maximum");
        assertEquals(1, occurrences(rollBody,
                "entry.copyChanced(cap, ContentModifier.multiplier(times))"),
                "successful fluid content must be scaled through copyChanced by times");
        assertFalse(rollBody.contains("entry.copy(cap)"),
                "successful fluid content must not bypass chanced scaling with an unmodified copy");
        assertIgnoresBoostTierAndCache(rollBody);
    }

    @Test
    void constructorSelfRegistrationIsNotDuplicatedThroughTheEvent() throws IOException {
        String source = Files.readString(CHANCE_LOGICS_SOURCE);
        String registrationBody = methodBody(source, "public static void registerChanceLogics(");

        assertFalse(registrationBody.contains("event.register("),
                "ChanceLogic constructors self-register; event.register would register each logic twice");
    }

    private static void assertIgnoresBoostTierAndCache(String rollBody) {
        assertFalse(rollBody.contains("chanceBoostFunction"),
                "source probabilities must not use GTCEu tier chance boost");
        assertFalse(rollBody.contains("recipeTier"),
                "source probabilities must not use recipe tier");
        assertFalse(rollBody.contains("chanceTier"),
                "source probabilities must not use chance tier");
        assertFalse(rollBody.contains("cache"),
                "source probabilities must not use GTCEu chance cache");
        assertFalse(rollBody.contains("getBoostedChance("),
                "source probabilities must remain raw rather than boosted");
        assertFalse(rollBody.contains("tierChanceBoost"),
                "entry tier chance boost must not alter source probabilities");
        assertFalse(rollBody.contains("getCachedChance("),
                "source probabilities must not read GTCEu chance cache helpers");
        assertFalse(rollBody.contains("updateCachedChance("),
                "source probabilities must not update GTCEu chance cache helpers");
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
}
