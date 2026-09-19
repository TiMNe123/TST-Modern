package com.tstmodern.registry.machine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

final class StarcoreMinerStructureTest {

    @Test
    void preservesExactSourcePiecesAndSymbolCounts() {
        assertShape(StarcoreMinerStructure.MAIN_AISLES, 31, 26, 21);
        assertShape(StarcoreMinerStructure.MIDDLE_AISLES, 9, 1, 9);
        assertShape(StarcoreMinerStructure.END_AISLES, 1, 1, 1);

        assertEquals(Map.ofEntries(
                Map.entry('~', 1), Map.entry('A', 62), Map.entry('B', 80), Map.entry('C', 16),
                Map.entry('D', 64), Map.entry('E', 624), Map.entry('F', 272), Map.entry('G', 346),
                Map.entry('H', 188), Map.entry('I', 63), Map.entry('J', 664), Map.entry('K', 88),
                Map.entry('L', 23), Map.entry(' ', 14_435)), counts(StarcoreMinerStructure.MAIN_AISLES));
        assertEquals(Map.of('F', 48, 'K', 4, 'I', 1, ' ', 28),
                counts(StarcoreMinerStructure.MIDDLE_AISLES));
        assertEquals(Map.of('Z', 1), counts(StarcoreMinerStructure.END_AISLES));
        assertEquals('~', StarcoreMinerStructure.symbolAt(10, 22, 1));
    }

    private static void assertShape(String[][] shape, int depth, int height, int width) {
        assertEquals(depth, shape.length);
        for (String[] aisle : shape) {
            assertEquals(height, aisle.length);
            for (String row : aisle) assertEquals(width, row.length());
        }
    }

    private static Map<Character, Integer> counts(String[][] shape) {
        Map<Character, Integer> counts = new HashMap<>();
        for (String[] aisle : shape) {
            for (String row : aisle) {
                for (char symbol : row.toCharArray()) counts.merge(symbol, 1, Integer::sum);
            }
        }
        return counts;
    }
}
