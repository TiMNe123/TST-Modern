package com.tstmodern.machine.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.gregtechceu.gtceu.api.pattern.util.PatternMatchContext;

class NaquadahFuelRefineryLogicTest {

    @Test
    void preservesUniformCoilsAndSourceParallelScaling() {
        PatternMatchContext context = new PatternMatchContext();
        assertTrue(NaquadahFuelRefineryLogic.matchUniformCoil(context, 3));
        assertTrue(NaquadahFuelRefineryLogic.matchUniformCoil(context, 3));
        assertFalse(NaquadahFuelRefineryLogic.matchUniformCoil(context, 2));
        assertFalse(NaquadahFuelRefineryLogic.matchUniformCoil(context, 0));
        assertEquals(0, NaquadahFuelRefineryLogic.parallelLimit(0));
        assertEquals(4, NaquadahFuelRefineryLogic.parallelLimit(1));
        assertEquals(16, NaquadahFuelRefineryLogic.parallelLimit(4));
    }

    @Test
    void capsPerfectOverclocksToCoilTierDifference() {
        assertEquals(1_100_000L,
                NaquadahFuelRefineryLogic.cappedOverclockVoltage(320_000_000L, 1_100_000L, 0));
        assertEquals(4_400_000L,
                NaquadahFuelRefineryLogic.cappedOverclockVoltage(320_000_000L, 1_100_000L, 1));
        assertEquals(17_600_000L,
                NaquadahFuelRefineryLogic.cappedOverclockVoltage(320_000_000L, 1_100_000L, 2));
    }
}
