package com.tstmodern.machine.logic;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

class NetherInterfaceLogicTest {

    @Test
    void mapsAllWeightedPackageBoundaries() {
        int[] weights = { 1, 49, 30, 10, 10 };
        assertEquals(0, NetherInterfaceLogic.selectWeightedIndex(0, weights));
        assertEquals(1, NetherInterfaceLogic.selectWeightedIndex(1, weights));
        assertEquals(1, NetherInterfaceLogic.selectWeightedIndex(49, weights));
        assertEquals(2, NetherInterfaceLogic.selectWeightedIndex(50, weights));
        assertEquals(2, NetherInterfaceLogic.selectWeightedIndex(79, weights));
        assertEquals(3, NetherInterfaceLogic.selectWeightedIndex(80, weights));
        assertEquals(4, NetherInterfaceLogic.selectWeightedIndex(99, weights));
    }

    @Test
    void treatsThirtyPercentAsOneExactCycleRoll() {
        assertTrue(NetherInterfaceLogic.passesChance(2_999, 3_000));
        assertFalse(NetherInterfaceLogic.passesChance(3_000, 3_000));
    }

    @Test
    void performsExactlyThreeSelectionsAndScalesOnePackage() {
        int[] scriptedRolls = { 0, 50, 99 };
        AtomicInteger calls = new AtomicInteger();
        int[] selected = NetherInterfaceLogic.selectThreeWeighted(
                bound -> scriptedRolls[calls.getAndIncrement()],
                new int[] { 1, 49, 30, 10, 10 });
        assertArrayEquals(new int[] { 0, 2, 4 }, selected);
        assertEquals(3, calls.get());
        assertEquals(64, NetherInterfaceLogic.scaledAmount(4, 16));
    }

    @Test
    void performsOnlyOneFluidChanceRollPerCycle() {
        AtomicInteger calls = new AtomicInteger();
        assertTrue(NetherInterfaceLogic.rollOnce(bound -> {
            calls.incrementAndGet();
            return 2_999;
        }, 3_000, 10_000));
        assertEquals(1, calls.get());
    }
}
