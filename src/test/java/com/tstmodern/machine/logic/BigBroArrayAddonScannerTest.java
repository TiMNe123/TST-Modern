package com.tstmodern.machine.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.gregtechceu.gtceu.api.GTValues;
import com.tstmodern.machine.logic.BigBroArrayAddonMatcher.AddonMatch;
import com.tstmodern.machine.logic.BigBroArrayAddonMatcher.Failure;
import com.tstmodern.machine.logic.BigBroArrayTierRules.CoreTiers;
import com.tstmodern.registry.machine.BigBroArrayStructure.AddonPlacement;

import org.junit.jupiter.api.Test;

class BigBroArrayAddonScannerTest {
    private static final CoreTiers CORE = new CoreTiers(6, GTValues.MAX, GTValues.UV);
    private static final BigBroArrayAddonState EMPTY = new BigBroArrayAddonState(0, 0, 0, 0, 0, 0);
    private static final List<AddonPlacement> PLACEMENTS = List.of(
            placement(0), placement(1), placement(2), placement(3));

    @Test
    void scanAllNowChecksPlacementsZeroThroughThreeAndPublishesOneAggregate() {
        Fixture fixture = new Fixture();

        fixture.scanner.scanAllNow();

        assertEquals(List.of(0, 1, 2, 3), fixture.scanned);
        assertEquals(1, fixture.notifications.size());
        assertEquals(new BigBroArrayAddonState(0, 6, GTValues.MAX, 0, 0, 0),
                fixture.scanner.state());
    }

    @Test
    void fourScanNextCallsCheckEachPlacementExactlyOnceInIndexOrder() {
        Fixture fixture = new Fixture();

        for (int scan = 0; scan < 4; scan++) {
            fixture.scanner.scanNext();
        }

        assertEquals(List.of(0, 1, 2, 3), fixture.scanned);
    }

    @Test
    void unchangedFullAndStaggeredScansDoNotNotify() {
        Fixture fixture = new Fixture();
        fixture.scanner.scanAllNow();
        fixture.notifications.clear();

        fixture.scanner.scanAllNow();
        for (int scan = 0; scan < 4; scan++) {
            fixture.scanner.scanNext();
        }

        assertEquals(List.of(), fixture.notifications);
    }

    @Test
    void buildUpgradeAndRemovalEachPublishExactlyOneAggregateChange() {
        Fixture fixture = new Fixture();
        fixture.scanner.scanAllNow();
        fixture.notifications.clear();

        fixture.matches[1] = valid(1, 5, GTValues.IV, 1, 2);
        fixture.scanner.scanAllNow();
        assertEquals(1, fixture.notifications.size());
        assertEquals(1, fixture.scanner.state().addonCount());

        fixture.notifications.clear();
        fixture.matches[1] = valid(1, 5, GTValues.IV, 4, 3);
        fixture.scanner.scanAllNow();
        assertEquals(1, fixture.notifications.size());
        assertEquals(4, fixture.scanner.state().parallelTier());
        assertEquals(3, fixture.scanner.state().coilTier());

        fixture.notifications.clear();
        fixture.matches[1] = invalid(1);
        fixture.scanner.scanAllNow();
        assertEquals(1, fixture.notifications.size());
        assertEquals(0, fixture.scanner.state().addonCount());
    }

    @Test
    void invalidCoreClearsEveryCachedAddonWithoutTouchingTheWorld() {
        Fixture fixture = new Fixture();
        for (int index = 0; index < fixture.matches.length; index++) {
            fixture.matches[index] = valid(index, 5, GTValues.IV, 2, 1);
        }
        fixture.scanner.scanAllNow();
        assertEquals(4, fixture.scanner.state().addonCount());
        fixture.scanned.clear();
        fixture.notifications.clear();

        fixture.core.set(null);
        fixture.scanner.scanNext();

        assertEquals(List.of(), fixture.scanned);
        assertEquals(EMPTY, fixture.scanner.state());
        assertEquals(List.of(EMPTY), fixture.notifications);
    }

    private static AddonPlacement placement(int index) {
        return new AddonPlacement(index, index, 0, 0, 0, List.of());
    }

    private static AddonMatch valid(
                                    int index,
                                    int frameTier,
                                    int glassTier,
                                    int parallelTier,
                                    int coilTier) {
        return new AddonMatch(index, true, frameTier, glassTier, parallelTier, coilTier, Failure.NONE);
    }

    private static AddonMatch invalid(int index) {
        return new AddonMatch(index, false, 0, 0, 0, 0, Failure.MISSING_BLOCK);
    }

    private static final class Fixture {
        private final AtomicReference<CoreTiers> core = new AtomicReference<>(CORE);
        private final AddonMatch[] matches = {
                invalid(0), invalid(1), invalid(2), invalid(3)
        };
        private final List<Integer> scanned = new ArrayList<>();
        private final List<BigBroArrayAddonState> notifications = new ArrayList<>();
        private final BigBroArrayAddonScanner scanner = new BigBroArrayAddonScanner(
                PLACEMENTS,
                placement -> {
                    scanned.add(placement.index());
                    return matches[placement.index()];
                },
                core::get,
                notifications::add);
    }
}
