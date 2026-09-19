package com.tstmodern.registry.machine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

class LargeNeutronOscillatorStructureTest {

    @Test
    void preservesSourceDimensionsControllerAndSymbolTotals() {
        String[][] aisles = LargeNeutronOscillatorStructure.PATTERN_AISLES;
        assertEquals(13, aisles.length);

        Map<Character, Integer> totals = new HashMap<>();
        for (String[] aisle : aisles) {
            assertEquals(40, aisle.length);
            for (String row : aisle) {
                assertEquals(23, row.length());
                row.chars().mapToObj(value -> (char) value)
                        .forEach(symbol -> totals.merge(symbol, 1, Integer::sum));
            }
        }

        assertEquals(1, totals.getOrDefault('~', 0));
        assertEquals(8, totals.getOrDefault('A', 0));
        assertEquals(233, totals.getOrDefault('B', 0));
        assertEquals(346, totals.getOrDefault('C', 0));
        assertEquals(27, totals.getOrDefault('D', 0));
        assertEquals(592, totals.getOrDefault('E', 0));
        assertEquals(2405, totals.getOrDefault('F', 0));
        assertEquals(296, totals.getOrDefault('G', 0));
        assertEquals('~', LargeNeutronOscillatorStructure.symbolAt(3, 36, 1));
    }
}
