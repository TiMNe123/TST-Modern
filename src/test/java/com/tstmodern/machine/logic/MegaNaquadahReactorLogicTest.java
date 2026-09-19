package com.tstmodern.machine.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.tstmodern.registry.machine.MegaNaquadahReactorStructure;

class MegaNaquadahReactorLogicTest {

    @Test
    void preservesSourceTimingDiscountParallelAndOutputRules() {
        assertEquals(5, MegaNaquadahReactorLogic.recipeSeconds(100));
        assertEquals(6, MegaNaquadahReactorLogic.recipeSeconds(101));
        assertEquals(0, MegaNaquadahReactorLogic.discountPercent(0));
        assertEquals(25, MegaNaquadahReactorLogic.discountPercent(
                MegaNaquadahReactorLogic.TICKS_TO_MAX_DISCOUNT / 2));
        assertEquals(50, MegaNaquadahReactorLogic.discountPercent(
                MegaNaquadahReactorLogic.TICKS_TO_MAX_DISCOUNT));
        assertEquals(500, MegaNaquadahReactorLogic.discountedPerSecond(1_000,
                MegaNaquadahReactorLogic.TICKS_TO_MAX_DISCOUNT));
        assertEquals(1_000, MegaNaquadahReactorLogic.parallelLimit(1_000, 5_000, 1));
        assertEquals(12_000_000, MegaNaquadahReactorLogic.totalFluidAmount(2_400, 1_000, 100));
        assertEquals(4_147_200_000L,
                MegaNaquadahReactorLogic.outputEuPerTick(12_960, 500, 64, 1_000));
    }

    @Test
    void rejectsFluidAmountsThatCannotFitAForgeFluidStack() {
        assertEquals(-1, MegaNaquadahReactorLogic.totalFluidAmount(2_400, 1_000_000, 100));
    }

    @Test
    void preservesTheExactTransposedSourceStructure() {
        int[] counts = new int[128];
        for (String[] aisle : MegaNaquadahReactorStructure.PATTERN_AISLES) {
            for (String row : aisle) for (int i = 0; i < row.length(); i++) counts[row.charAt(i)]++;
        }
        assertEquals(32, MegaNaquadahReactorStructure.PATTERN_AISLES.length);
        assertEquals(27, MegaNaquadahReactorStructure.PATTERN_AISLES[0].length);
        assertEquals(31, MegaNaquadahReactorStructure.PATTERN_AISLES[0][0].length());
        assertEquals(4_163, counts['D']);
        assertEquals(988, counts['F']);
        assertEquals(602, counts['A']);
        assertEquals(294, counts['B']);
        assertEquals(148, counts['E']);
        assertEquals(11, counts['C']);
        assertEquals(1, counts['~']);
    }
}
