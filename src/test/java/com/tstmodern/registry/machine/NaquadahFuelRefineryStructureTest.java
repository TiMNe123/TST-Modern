package com.tstmodern.registry.machine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

class NaquadahFuelRefineryStructureTest {

    @Test
    void preservesExactSourceDimensionsControllerAndSymbolCounts() {
        assertEquals(NaquadahFuelRefineryStructure.DEPTH,
                NaquadahFuelRefineryStructure.PATTERN_AISLES.length);
        Map<Character, Integer> counts = new HashMap<>();
        for (String[] aisle : NaquadahFuelRefineryStructure.PATTERN_AISLES) {
            assertEquals(NaquadahFuelRefineryStructure.HEIGHT, aisle.length);
            for (String row : aisle) {
                assertEquals(NaquadahFuelRefineryStructure.WIDTH, row.length());
                for (char symbol : row.toCharArray()) counts.merge(symbol, 1, Integer::sum);
            }
        }
        assertEquals('~', NaquadahFuelRefineryStructure.symbolAt(
                NaquadahFuelRefineryStructure.CONTROLLER_RIGHT,
                NaquadahFuelRefineryStructure.CONTROLLER_DOWN,
                NaquadahFuelRefineryStructure.CONTROLLER_BACK));
        assertEquals(Map.of(
                '~', 1, 'A', 489, 'B', 72, 'C', 192,
                'E', 124, 'F', 64, ' ', 2_703), counts);
    }
}
