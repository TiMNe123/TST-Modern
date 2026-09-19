package com.tstmodern.registry.machine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

final class DraconicCrucibleStructureTest {
    @Test
    void matchesApprovedDimensionsControllerAndCounts() {
        String[][] aisles = DraconicCrucibleStructure.PATTERN_AISLES;
        assertEquals(45, aisles.length);
        for (String[] aisle : aisles) {
            assertEquals(15, aisle.length);
            for (String row : aisle) assertEquals(61, row.length());
        }

        assertEquals(Map.ofEntries(
                Map.entry('~', 1), Map.entry('D', 2826), Map.entry('R', 2914),
                Map.entry('M', 1738), Map.entry('S', 1728), Map.entry('T', 8),
                Map.entry('F', 48), Map.entry('C', 55), Map.entry('K', 1),
                Map.entry('P', 26), Map.entry('B', 13), Map.entry('G', 30),
                Map.entry('#', 3677), Map.entry(' ', 28110)), counts(aisles));
        assertEquals('~', symbolAt(
                DraconicCrucibleStructure.CONTROLLER_X,
                DraconicCrucibleStructure.CONTROLLER_Y,
                DraconicCrucibleStructure.CONTROLLER_Z));
        assertEquals('K', symbolAt(
                DraconicCrucibleStructure.CORE_X,
                DraconicCrucibleStructure.CORE_Y,
                DraconicCrucibleStructure.CORE_Z));

        int previousWidth = Integer.MAX_VALUE;
        int previousDepth = Integer.MAX_VALUE;
        for (int y = 2; y <= 12; y++) {
            int radiusIndex = y - 2;
            int width = DraconicCrucibleStructure.VOLCANO_RADIUS_X[radiusIndex] * 2 + 1;
            int depth = DraconicCrucibleStructure.VOLCANO_RADIUS_Z[radiusIndex] * 2 + 1;
            assertTrue(width < previousWidth && depth < previousDepth, "volcano layer y=" + y);
            previousWidth = width;
            previousDepth = depth;

            int northZ = DraconicCrucibleStructure.VOLCANO_CENTER_Z
                    - DraconicCrucibleStructure.VOLCANO_RADIUS_Z[radiusIndex];
            assertTrue("MSPG".indexOf(symbolAt(DraconicCrucibleStructure.VOLCANO_CENTER_X, y, northZ)) >= 0);
            assertEquals(' ', symbolAt(
                    DraconicCrucibleStructure.VOLCANO_CENTER_X
                            + DraconicCrucibleStructure.VOLCANO_RADIUS_X[radiusIndex],
                    y,
                    northZ));
        }
    }

    private static char symbolAt(int x, int y, int z) {
        return DraconicCrucibleStructure.PATTERN_AISLES[z]
                [DraconicCrucibleStructure.HEIGHT - 1 - y].charAt(x);
    }

    private static Map<Character, Integer> counts(String[][] aisles) {
        Map<Character, Integer> result = new HashMap<>();
        for (String[] aisle : aisles) {
            for (String row : aisle) {
                for (char symbol : row.toCharArray()) result.merge(symbol, 1, Integer::sum);
            }
        }
        return result;
    }
}
