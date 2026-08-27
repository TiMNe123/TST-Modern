package com.tstmodern.machine.logic;

import java.util.List;
import java.util.Objects;

import com.tstmodern.machine.logic.BigBroArrayAddonMatcher.AddonMatch;
import com.tstmodern.machine.logic.BigBroArrayTierRules.CoreTiers;

/** Aggregate tiers exposed by the Big Bro Array core and its independently valid addons. */
public record BigBroArrayAddonState(
                                    int addonCount,
                                    int frameTier,
                                    int glassTier,
                                    int parallelTier,
                                    int coilTier,
                                    int validMask) {
    public static BigBroArrayAddonState aggregate(CoreTiers core, List<AddonMatch> matches) {
        Objects.requireNonNull(core, "core");
        Objects.requireNonNull(matches, "matches");

        int addonCount = 0;
        int frameTier = core.frameTier();
        int glassTier = core.glassTier();
        int parallelTier = 0;
        int coilTier = 0;
        int validMask = 0;

        for (AddonMatch match : matches) {
            if (match == null || !match.valid()) continue;

            frameTier = Math.min(frameTier, match.frameTier());
            glassTier = Math.min(glassTier, match.glassTier());
            if (addonCount == 0) {
                parallelTier = match.parallelTier();
                coilTier = match.coilTier();
            } else {
                parallelTier = Math.min(parallelTier, match.parallelTier());
                coilTier = Math.min(coilTier, match.coilTier());
            }
            addonCount++;
            validMask |= 1 << match.index();
        }

        return new BigBroArrayAddonState(
                addonCount, frameTier, glassTier, parallelTier, coilTier, validMask);
    }
}
