package com.tstmodern.registry.machine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

final class AstralComputingArrayStructureTest {
    @Test
    void preservesExactSourceDimensionsControllerAndSymbolCounts() {
        assertEquals(47, AstralComputingArrayStructure.PATTERN_AISLES.length);
        Map<Character, Integer> counts = new HashMap<>();
        for (String[] aisle : AstralComputingArrayStructure.PATTERN_AISLES) {
            assertEquals(35, aisle.length);
            for (String row : aisle) {
                assertEquals(47, row.length());
                for (char symbol : row.toCharArray()) counts.merge(symbol, 1, Integer::sum);
            }
        }
        assertEquals('~', AstralComputingArrayStructure.symbolAt(23, 34, 0));
        assertEquals(Map.ofEntries(
                Map.entry('~', 1), Map.entry('A', 48), Map.entry('B', 80), Map.entry('C', 436),
                Map.entry('D', 25), Map.entry('E', 295), Map.entry('F', 280), Map.entry('G', 1277),
                Map.entry('H', 168), Map.entry('I', 671), Map.entry('J', 516), Map.entry('K', 308),
                Map.entry('L', 64), Map.entry('M', 821), Map.entry('O', 4), Map.entry('P', 440),
                Map.entry('Q', 144), Map.entry(' ', 71737)), counts);
    }
}
