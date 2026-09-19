package com.tstmodern.machine.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class IncompactCyclotronLogicTest {

    @Test
    void addsParallelHatchToTheSourceParallelLimitWithoutOverflow() {
        assertEquals(256, IncompactCyclotronLogic.parallelLimit(0));
        assertEquals(272, IncompactCyclotronLogic.parallelLimit(16));
        assertEquals(Integer.MAX_VALUE, IncompactCyclotronLogic.parallelLimit(Integer.MAX_VALUE));
    }

    @Test
    void preservesTstPowerAndSpeedModifiers() {
        assertEquals(1.6, IncompactCyclotronLogic.euMultiplier(), 0.000_001);
        assertEquals(0.5, IncompactCyclotronLogic.durationMultiplier(), 0.000_001);
    }
}
