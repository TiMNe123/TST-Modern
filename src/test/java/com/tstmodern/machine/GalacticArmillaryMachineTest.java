package com.tstmodern.machine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class GalacticArmillaryMachineTest {
    @Test
    void coreChargeSaturatesAtFixedCapacity() {
        assertEquals(1, GalacticArmillaryMachine.advanceCharge(0, 1));
        assertEquals(GalacticArmillaryMachine.CORE_CAPACITY,
                GalacticArmillaryMachine.advanceCharge(GalacticArmillaryMachine.CORE_CAPACITY - 1, 2));
        assertEquals(GalacticArmillaryMachine.CORE_CAPACITY,
                GalacticArmillaryMachine.advanceCharge(GalacticArmillaryMachine.CORE_CAPACITY, 1));
        assertEquals(0, GalacticArmillaryMachine.advanceCharge(0, -1));
    }

    @Test
    void interruptionGraceSurvivesGtceuRecipeRetryCadence() {
        assertFalse(GalacticArmillaryMachine.eligibilityExpired(140, 100));
        assertTrue(GalacticArmillaryMachine.eligibilityExpired(141, 100));
        assertTrue(GalacticArmillaryMachine.eligibilityExpired(0, Long.MIN_VALUE));
    }

    @Test
    void configurableStartupSecondsAreConvertedAndSaturated() {
        assertEquals(2_400, GalacticArmillaryMachine.startupTicksForSeconds(120));
        assertEquals(400, GalacticArmillaryMachine.startupTicksForSeconds(1));
        assertEquals(120, GalacticArmillaryMachine.advanceStartupTicks(119, 120));
        assertEquals(120, GalacticArmillaryMachine.advanceStartupTicks(120, 120));
        assertFalse(GalacticArmillaryMachine.startupComplete(
                GalacticArmillaryMachine.CORE_CAPACITY - 1, 120, 120));
        assertFalse(GalacticArmillaryMachine.startupComplete(
                GalacticArmillaryMachine.CORE_CAPACITY, 119, 120));
        assertTrue(GalacticArmillaryMachine.startupComplete(
                GalacticArmillaryMachine.CORE_CAPACITY, 120, 120));
    }

    @Test
    void progressPercentageIsClamped() {
        assertEquals(0, GalacticArmillaryMachine.progressPercent(-1, 100));
        assertEquals(50, GalacticArmillaryMachine.progressPercent(50, 100));
        assertEquals(100, GalacticArmillaryMachine.progressPercent(200, 100));
        assertEquals(0.5f, GalacticArmillaryMachine.activationProgress(
                GalacticArmillaryMachine.CORE_CAPACITY, 50, 100));
        assertEquals(1.0f, GalacticArmillaryMachine.activationProgress(
                GalacticArmillaryMachine.CORE_CAPACITY, 100, 100));
    }
}
