package com.tstmodern.registry.machine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

class IncompactCyclotronStructureTest {

    @Test
    void preservesSourceDimensionsControllerAndSymbolTotals() {
        String[][] aisles = IncompactCyclotronStructure.PATTERN_AISLES;
        assertEquals(47, aisles.length);

        Map<Character, Integer> totals = new HashMap<>();
        for (String[] aisle : aisles) {
            assertEquals(7, aisle.length);
            for (String row : aisle) {
                assertEquals(47, row.length());
                row.chars().mapToObj(value -> (char) value)
                        .forEach(symbol -> totals.merge(symbol, 1, Integer::sum));
            }
        }

        assertEquals(1, totals.getOrDefault('~', 0));
        assertEquals(31, totals.getOrDefault('A', 0));
        assertEquals(128, totals.getOrDefault('B', 0));
        assertEquals(560, totals.getOrDefault('C', 0));
        assertEquals(1664, totals.getOrDefault('D', 0));
        assertEquals(64, totals.getOrDefault('E', 0));
        assertEquals(32, totals.getOrDefault('F', 0));
        assertEquals('~', IncompactCyclotronStructure.symbolAt(23, 3, 40));
    }
}
