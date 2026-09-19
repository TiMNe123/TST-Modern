package com.tstmodern.machine.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class OreProcessingFactoryLogicTest {
    @Test
    void preservesPowerLossAndLubricantCadence() {
        assertEquals(960, OreProcessingFactoryLogic.usablePower(1_024, 1, true));
        assertEquals(992, OreProcessingFactoryLogic.usablePower(1_024, 1, false));
        assertEquals(32, OreProcessingFactoryLogic.parallelLimit(960));
        assertFalse(OreProcessingFactoryLogic.lubricantDue(254));
        assertTrue(OreProcessingFactoryLogic.lubricantDue(255));
    }
}
