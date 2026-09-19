package com.tstmodern.registry.machine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

final class OreProcessingFactoryStructureTest {
    @Test
    void exactSourceShapeWasTransposedWithoutChangingSymbols() {
        assertEquals(15, OreProcessingFactoryStructure.PATTERN_AISLES.length);
        Map<Character, Integer> counts = new HashMap<>();
        for (String[] aisle : OreProcessingFactoryStructure.PATTERN_AISLES) {
            assertEquals(13, aisle.length);
            for (String row : aisle) {
                assertEquals(32, row.length());
                row.chars().forEach(c -> counts.merge((char) c, 1, Integer::sum));
            }
        }
        Map<Character, Integer> expected = Map.ofEntries(
                Map.entry(' ', 3375), Map.entry('A', 40), Map.entry('B', 512),
                Map.entry('C', 128), Map.entry('D', 300), Map.entry('E', 165),
                Map.entry('F', 104), Map.entry('G', 1144), Map.entry('H', 289),
                Map.entry('I', 1), Map.entry('J', 7), Map.entry('K', 8),
                Map.entry('L', 146), Map.entry('M', 20), Map.entry('~', 1));
        assertEquals(expected, counts);
        assertEquals('~', OreProcessingFactoryStructure.symbolAt(30, 11, 0));
    }
}
