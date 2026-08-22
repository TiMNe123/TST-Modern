package com.tstmodern.machine.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class GiantVacuumDryingFurnaceLogicTest {

    @Test
    void convertsZeroBasedGtceuCoilTierToTstTier() {
        assertEquals(1, GiantVacuumDryingFurnaceLogic.sourceCoilTier(0));
        assertEquals(4, GiantVacuumDryingFurnaceLogic.sourceCoilTier(3));
    }

    @Test
    void saturatesSourceCoilTierAtIntegerMaximum() {
        assertEquals(Integer.MAX_VALUE,
                GiantVacuumDryingFurnaceLogic.sourceCoilTier(Integer.MAX_VALUE));
    }

    @Test
    void computesParallelAndDurationFromMachineTierOnly() {
        assertEquals(32, GiantVacuumDryingFurnaceLogic.parallelLimit(1, 0));
        assertEquals(144, GiantVacuumDryingFurnaceLogic.parallelLimit(4, 16));
        assertEquals(2.0, GiantVacuumDryingFurnaceLogic.durationMultiplier(0, 1), 1.0e-9);
        assertEquals(Math.pow(0.8, 4) / 1.5,
                GiantVacuumDryingFurnaceLogic.durationMultiplier(4, 3), 1.0e-9);
    }
}
