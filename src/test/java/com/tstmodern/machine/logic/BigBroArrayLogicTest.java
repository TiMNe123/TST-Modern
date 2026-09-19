package com.tstmodern.machine.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class BigBroArrayLogicTest {

    @Test
    void casingMultiplierMatchesSourceFormula() {
        assertEquals(0, BigBroArrayLogic.casingMultiplier(0));
        assertEquals(1, BigBroArrayLogic.casingMultiplier(1));
        assertEquals(2, BigBroArrayLogic.casingMultiplier(2));
        assertEquals(3, BigBroArrayLogic.casingMultiplier(3));
        assertEquals(10, BigBroArrayLogic.casingMultiplier(4));
        assertEquals(11, BigBroArrayLogic.casingMultiplier(5));
    }

    @Test
    void noAddonAlwaysCaps64() {
        for (int tier = 0; tier <= 5; tier++) {
            assertEquals(64L, BigBroArrayLogic.calculateMaxParallelism(0, tier),
                    "addonCount=0, tier=" + tier);
        }
    }

    @ParameterizedTest(name = "addonCount={0}, tier={1} → maxParallel={2}")
    @CsvSource({
            // MK1 (tier 1), 1 addon: (64 << 2) * 2 = 512
            "1, 1, 512",
            // MK2 (tier 2), 2 addons: (64 << 4) * 3 = 3072
            "2, 2, 3072",
            // MK3 (tier 3), 4 addons: (64 << 6) * 5 = 20480
            "4, 3, 20480",
            // MK4 (tier 4): shift = 8 + 6 = 14, base = 64 << 14 = 1,048,576. 4 addons: * 5 = 5,242,880
            "4, 4, 5242880",
            // MK1, 4 addons: (64 << 2) * 5 = 1280
            "4, 1, 1280",
            // MK2, 1 addon: (64 << 4) * 2 = 2048
            "1, 2, 2048",
    })
    void calculateMaxParallelismMatchesSourceFormulaParameterized(int addonCount, int tier, long expected) {
        assertEquals(expected, BigBroArrayLogic.calculateMaxParallelism(addonCount, tier));
    }

    @ParameterizedTest(name = "MK5 addon={0} → expected={1}")
    @CsvSource({
            "1, 1759218603622",
            "2, 2638827905433",
            "3, 3518437207244",
            "4, 4398046509055"
    })
    void mk5ParallelMatchesExactFormula(int addonCount, long expected) {
        long result = BigBroArrayLogic.calculateMaxParallelism(addonCount, 5);
        assertEquals(expected, result);
        assertTrue(result > 0, "MK5 parallel must be positive, got " + result);
    }

    @Test
    void mk5MonotonicallyIncreasesWithAddons() {
        long prev = BigBroArrayLogic.calculateMaxParallelism(1, 5);
        for (int addons = 2; addons <= 4; addons++) {
            long current = BigBroArrayLogic.calculateMaxParallelism(addons, 5);
            assertTrue(current > prev,
                    "MK5 addon " + addons + " (" + current + ") must exceed addon " + (addons - 1) + " (" + prev + ")");
            prev = current;
        }
    }

    @Test
    void parallelismNeverDecreasesWithMoreAddons() {
        for (int tier = 0; tier <= 5; tier++) {
            long prev = BigBroArrayLogic.calculateMaxParallelism(0, tier);
            for (int addons = 1; addons <= 4; addons++) {
                long current = BigBroArrayLogic.calculateMaxParallelism(addons, tier);
                assertTrue(current >= prev,
                        "tier=" + tier + " addon " + addons + " (" + current + ") < addon " + (addons - 1) + " (" + prev + ")");
                prev = current;
            }
        }
    }

    @Test
    void calculateParallelismCapsAndShiftsCorrectly() {
        // No addons: hard capped at 64
        assertEquals(16L, BigBroArrayLogic.calculateParallelism(16, 3, 0));
        assertEquals(64L, BigBroArrayLogic.calculateParallelism(100, 3, 0));

        // MK1, 1 addon: machineCount << 1 capped by max (512)
        assertEquals(32L, BigBroArrayLogic.calculateParallelism(16, 1, 1));
        assertEquals(512L, BigBroArrayLogic.calculateParallelism(500, 1, 1));
    }

    @Test
    void calculateParallelismNeverExceedsMaxParallelism() {
        for (int tier = 0; tier <= 5; tier++) {
            for (int addons = 0; addons <= 4; addons++) {
                long max = BigBroArrayLogic.calculateMaxParallelism(addons, tier);
                long actual = BigBroArrayLogic.calculateParallelism(Integer.MAX_VALUE, tier, addons);
                assertTrue(actual <= max,
                        "tier=" + tier + " addons=" + addons + ": actual " + actual + " > max " + max);
                assertTrue(actual >= 0,
                        "tier=" + tier + " addons=" + addons + ": actual " + actual + " must be non-negative");
            }
        }
    }

    @Test
    void calculateParallelismReturnsZeroForNonPositiveMachineCount() {
        assertEquals(0L, BigBroArrayLogic.calculateParallelism(0, 3, 2));
        assertEquals(0L, BigBroArrayLogic.calculateParallelism(-1, 3, 2));
    }

    @Test
    void extremeInputSaturatesNotWraps() {
        long result = BigBroArrayLogic.calculateMaxParallelism(4, 5);
        assertTrue(result > 0, "Extreme input must saturate positively, got " + result);

        long parallelism = BigBroArrayLogic.calculateParallelism(Integer.MAX_VALUE, 5, 4);
        assertTrue(parallelism > 0, "Extreme parallelism must be positive, got " + parallelism);
    }

    @Test
    void energyDiscountAndDurationMultipliersMatchMath() {
        assertEquals(1.0, BigBroArrayLogic.calculateEnergyDiscount(0), 1e-6);
        assertEquals(0.9, BigBroArrayLogic.calculateEnergyDiscount(1), 1e-6);
        assertEquals(0.81, BigBroArrayLogic.calculateEnergyDiscount(2), 1e-6);

        assertEquals(1.0, BigBroArrayLogic.calculateDurationMultiplier(0), 1e-6);
        assertEquals(0.66, BigBroArrayLogic.calculateDurationMultiplier(1), 1e-6);
        assertEquals(0.4356, BigBroArrayLogic.calculateDurationMultiplier(2), 1e-6);
    }
}
