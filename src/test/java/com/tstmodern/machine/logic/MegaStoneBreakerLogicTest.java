package com.tstmodern.machine.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class MegaStoneBreakerLogicTest {
    @Test
    void computesTstBaseParallelAndSaturates() {
        assertEquals(4, MegaStoneBreakerLogic.baseParallel(0));
        assertEquals(16, MegaStoneBreakerLogic.baseParallel(2));
        assertEquals(Integer.MAX_VALUE, MegaStoneBreakerLogic.baseParallel(29));
        assertEquals(Integer.MAX_VALUE,
                MegaStoneBreakerLogic.addParallel(Integer.MAX_VALUE - 2, 16));
    }

    @Test
    void selectsNormalAndBoostedOutputBonuses() {
        assertEquals(4, MegaStoneBreakerLogic.outputBonus(false));
        assertEquals(1_024, MegaStoneBreakerLogic.outputBonus(true));
    }

    @Test
    void drainsOnFirstBoostedTickAndEveryTwentyActiveTicks() {
        assertTrue(MegaStoneBreakerLogic.shouldDrainBoost(0));
        assertFalse(MegaStoneBreakerLogic.shouldDrainBoost(1));
        assertFalse(MegaStoneBreakerLogic.shouldDrainBoost(19));
        assertTrue(MegaStoneBreakerLogic.shouldDrainBoost(20));
        assertTrue(MegaStoneBreakerLogic.shouldDrainBoost(40));
    }
}
