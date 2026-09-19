package com.tstmodern.registry.machine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

final class GalacticArmillaryStructureTest {
    @Test
    void matchesApprovedPrototypeContract() {
        String[][] aisles = GalacticArmillaryStructure.PATTERN_AISLES;
        assertEquals(41, aisles.length);
        for (String[] aisle : aisles) {
            assertEquals(39, aisle.length);
            for (String row : aisle) assertEquals(41, row.length());
        }

        assertEquals(Map.of(
                '~', 1,
                'D', 5358,
                'H', 291,
                'E', 16,
                'I', 112,
                'O', 112,
                'A', 72,
                ' ', 59597), counts(aisles));
        assertEquals('~', GalacticArmillaryStructure.symbolAt(20, 3, 20));
        for (int[] socket : GalacticArmillaryStructure.ENERGY_SOCKET_COORDINATES) {
            assertEquals('E', GalacticArmillaryStructure.symbolAt(socket[0], socket[1], socket[2]));
        }
        for (int x = 19; x <= 21; x++) {
            for (int z = 19; z <= 21; z++) assertEquals('H', GalacticArmillaryStructure.symbolAt(x, 2, z));
        }
        assertEquals('D', GalacticArmillaryStructure.symbolAt(20, 1, 20));
        assertEquals(' ', GalacticArmillaryStructure.symbolAt(17, 3, 17));
        assertEquals(' ', GalacticArmillaryStructure.symbolAt(23, 8, 23));
        assertEquals(' ', GalacticArmillaryStructure.symbolAt(23, 10, 23));
        assertEquals(' ', GalacticArmillaryStructure.symbolAt(20, 18, 20));
        assertEquals('A', GalacticArmillaryStructure.symbolAt(20, 18, 29));
        assertEquals('A', GalacticArmillaryStructure.symbolAt(20, 18, 11));
        assertEquals('I', GalacticArmillaryStructure.symbolAt(28, 25, 20));
        assertEquals('I', GalacticArmillaryStructure.symbolAt(12, 11, 20));
        assertEquals('O', GalacticArmillaryStructure.symbolAt(28, 11, 20));
        assertEquals('O', GalacticArmillaryStructure.symbolAt(12, 25, 20));
        assertEquals(' ', GalacticArmillaryStructure.symbolAt(29, 29, 20));
        assertEquals(' ', GalacticArmillaryStructure.symbolAt(29, 38, 20));
        assertEquals('D', GalacticArmillaryStructure.symbolAt(29, 37, 20));
        assertEquals('H', GalacticArmillaryStructure.symbolAt(26, 37, 20));
        assertEquals('H', GalacticArmillaryStructure.symbolAt(20, 37, 28));
        assertEquals('D', GalacticArmillaryStructure.symbolAt(26, 37, 19));
        assertEquals('D', GalacticArmillaryStructure.symbolAt(23, 37, 20));
        assertEquals('A', GalacticArmillaryStructure.symbolAt(16, 37, 20));
        assertEquals('A', GalacticArmillaryStructure.symbolAt(24, 37, 20));
        assertEquals(' ', GalacticArmillaryStructure.symbolAt(20, 37, 20));
        assertEquals(' ', GalacticArmillaryStructure.symbolAt(20, 38, 20));
        for (int y = 33; y <= 35; y++) {
            assertEquals('D', GalacticArmillaryStructure.symbolAt(23, y, 20));
        }
        for (int y = 30; y <= 32; y++) {
            assertEquals('D', GalacticArmillaryStructure.symbolAt(22, y, 20));
        }
        assertEquals('H', GalacticArmillaryStructure.symbolAt(21, 29, 20));
        for (int y = 27; y <= 28; y++) {
            assertEquals('H', GalacticArmillaryStructure.symbolAt(21, y, 21));
            assertEquals(' ', GalacticArmillaryStructure.symbolAt(21, y, 20));
        }
        for (int y = 27; y <= 35; y++) {
            assertEquals(' ', GalacticArmillaryStructure.symbolAt(20, y, 20));
        }
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
