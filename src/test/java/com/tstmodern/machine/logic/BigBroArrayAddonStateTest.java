package com.tstmodern.machine.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import com.gregtechceu.gtceu.api.GTValues;
import com.tstmodern.machine.logic.BigBroArrayAddonMatcher.AddonMatch;
import com.tstmodern.machine.logic.BigBroArrayAddonMatcher.Failure;
import com.tstmodern.machine.logic.BigBroArrayTierRules.CoreTiers;

import org.junit.jupiter.api.Test;

class BigBroArrayAddonStateTest {
    private static final CoreTiers CORE = new CoreTiers(6, GTValues.MAX, GTValues.UV);

    @Test
    void noAddonKeepsCoreTiersAndPublishesZeroAddonOnlyTiers() {
        BigBroArrayAddonState state = BigBroArrayAddonState.aggregate(CORE, List.of());

        assertEquals(0, state.addonCount());
        assertEquals(6, state.frameTier());
        assertEquals(GTValues.MAX, state.glassTier());
        assertEquals(0, state.parallelTier());
        assertEquals(0, state.coilTier());
        assertEquals(0, state.validMask());
    }

    @Test
    void fourValidAddonsAggregateCoreAndAddonMinimaWithAnIndexedMask() {
        List<AddonMatch> fourValid = List.of(
                valid(0, 6, GTValues.MAX, 5, 4),
                valid(1, 5, GTValues.IV, 3, 2),
                valid(2, 4, GTValues.MAX, 4, 5),
                valid(3, 3, GTValues.MAX, 1, 3));

        BigBroArrayAddonState state = BigBroArrayAddonState.aggregate(CORE, fourValid);

        assertEquals(4, state.addonCount());
        assertEquals(3, state.frameTier());
        assertEquals(GTValues.IV, state.glassTier());
        assertEquals(1, state.parallelTier());
        assertEquals(2, state.coilTier());
        assertEquals(0b1111, state.validMask());
    }

    @Test
    void invalidPlacementsDoNotContributeTiersCountOrMaskBits() {
        List<AddonMatch> matches = List.of(
                valid(0, 5, GTValues.MAX, 4, 3),
                invalid(1, Failure.UNLOADED),
                valid(2, 4, GTValues.IV, 2, 1),
                invalid(3, Failure.MIXED_TIER));

        BigBroArrayAddonState state = BigBroArrayAddonState.aggregate(CORE, matches);

        assertEquals(2, state.addonCount());
        assertEquals(4, state.frameTier());
        assertEquals(GTValues.IV, state.glassTier());
        assertEquals(2, state.parallelTier());
        assertEquals(1, state.coilTier());
        assertEquals(0b0101, state.validMask());
    }

    @Test
    void everySuppliedMatchMustUseAPlacementIndexFromZeroThroughThree() {
        for (AddonMatch malformed : List.of(
                valid(-1, 5, GTValues.MAX, 4, 3),
                invalid(4, Failure.UNLOADED),
                invalid(32, Failure.MISSING_BLOCK))) {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> BigBroArrayAddonState.aggregate(CORE, List.of(malformed)));
        }
    }

    @Test
    void duplicatePlacementIndicesAreRejectedBeforeInvalidMatchesAreFiltered() {
        List<AddonMatch> duplicates = List.of(
                valid(2, 5, GTValues.MAX, 4, 3),
                invalid(2, Failure.UNLOADED));

        assertThrows(
                IllegalArgumentException.class,
                () -> BigBroArrayAddonState.aggregate(CORE, duplicates));
    }

    private static AddonMatch valid(
                                    int index,
                                    int frameTier,
                                    int glassTier,
                                    int parallelTier,
                                    int coilTier) {
        return new AddonMatch(index, true, frameTier, glassTier, parallelTier, coilTier, Failure.NONE);
    }

    private static AddonMatch invalid(int index, Failure failure) {
        return new AddonMatch(index, false, 0, 0, 0, 0, failure);
    }
}
