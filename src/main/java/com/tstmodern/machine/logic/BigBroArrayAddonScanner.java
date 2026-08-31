package com.tstmodern.machine.logic;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.tstmodern.machine.logic.BigBroArrayAddonMatcher.AddonMatch;
import com.tstmodern.machine.logic.BigBroArrayAddonMatcher.BlockLookup;
import com.tstmodern.machine.logic.BigBroArrayTierRules.CoreTiers;
import com.tstmodern.registry.machine.BigBroArrayStructure.AddonPlacement;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

/** Staggered cache for the four independently optional Big Bro Array addon placements. */
public final class BigBroArrayAddonScanner {
    private static final int PLACEMENT_COUNT = 4;
    private static final BigBroArrayAddonState EMPTY_STATE =
            new BigBroArrayAddonState(0, 0, 0, 0, 0, 0);

    private final List<AddonPlacement> placements;
    private final Function<AddonPlacement, AddonMatch> matchPlacement;
    private final Supplier<CoreTiers> coreTiers;
    private final Consumer<BigBroArrayAddonState> onChanged;
    private final AddonMatch[] cachedMatches = new AddonMatch[PLACEMENT_COUNT];
    private int cursor;
    private BigBroArrayAddonState state = EMPTY_STATE;

    /**
     * Creates a world-backed scanner. The context suppliers are the minimal extension to the original scanner brief
     * required by {@link BigBroArrayAddonMatcher#match} and keep world access behind {@link BlockLookup}.
     */
    public BigBroArrayAddonScanner(
                                  List<AddonPlacement> placements,
                                  BigBroArrayAddonMatcher matcher,
                                  BlockLookup lookup,
                                  Supplier<BlockPos> controllerPos,
                                  Supplier<Direction> front,
                                  Supplier<Direction> up,
                                  BooleanSupplier flipped,
                                  Supplier<CoreTiers> coreTiers,
                                  Consumer<BigBroArrayAddonState> onChanged) {
        this(
                placements,
                matcherOperation(matcher, lookup, controllerPos, front, up, flipped),
                coreTiers,
                onChanged);
    }

    BigBroArrayAddonScanner(
                           List<AddonPlacement> placements,
                           Function<AddonPlacement, AddonMatch> matchPlacement,
                           Supplier<CoreTiers> coreTiers,
                           Consumer<BigBroArrayAddonState> onChanged) {
        this.placements = indexedPlacements(placements);
        this.matchPlacement = Objects.requireNonNull(matchPlacement, "matchPlacement");
        this.coreTiers = Objects.requireNonNull(coreTiers, "coreTiers");
        this.onChanged = Objects.requireNonNull(onChanged, "onChanged");
    }

    /** Scans all four placements and publishes at most one aggregate change. */
    public void scanAllNow() {
        CoreTiers core = coreTiers.get();
        if (core == null) {
            clear();
            return;
        }

        for (AddonPlacement placement : placements) {
            cachedMatches[placement.index()] = matchPlacement.apply(placement);
        }
        cursor = 0;
        publish(BigBroArrayAddonState.aggregate(core, Arrays.asList(cachedMatches)));
    }

    /** Scans one placement in the fixed order 0, 1, 2, 3. */
    public void scanNext() {
        CoreTiers core = coreTiers.get();
        if (core == null) {
            clear();
            return;
        }

        AddonPlacement placement = placements.get(cursor);
        cachedMatches[placement.index()] = matchPlacement.apply(placement);
        cursor = (cursor + 1) % PLACEMENT_COUNT;
        publish(BigBroArrayAddonState.aggregate(core, Arrays.asList(cachedMatches)));
    }

    /** Clears only addon-derived cache and aggregate state. */
    public void clear() {
        Arrays.fill(cachedMatches, null);
        cursor = 0;
        publish(EMPTY_STATE);
    }

    public BigBroArrayAddonState state() {
        return state;
    }

    private void publish(BigBroArrayAddonState nextState) {
        if (!state.equals(nextState)) {
            state = nextState;
            onChanged.accept(nextState);
        }
    }

    private static List<AddonPlacement> indexedPlacements(List<AddonPlacement> placements) {
        Objects.requireNonNull(placements, "placements");
        if (placements.size() != PLACEMENT_COUNT) {
            throw new IllegalArgumentException("Exactly four addon placements are required");
        }

        AddonPlacement[] indexed = new AddonPlacement[PLACEMENT_COUNT];
        for (AddonPlacement placement : placements) {
            Objects.requireNonNull(placement, "placement");
            int index = placement.index();
            if (index < 0 || index >= PLACEMENT_COUNT) {
                throw new IllegalArgumentException("Addon placement index must be between 0 and 3: " + index);
            }
            if (indexed[index] != null) {
                throw new IllegalArgumentException("Duplicate addon placement index: " + index);
            }
            indexed[index] = placement;
        }
        return List.of(indexed);
    }

    private static Function<AddonPlacement, AddonMatch> matcherOperation(
                                                                        BigBroArrayAddonMatcher matcher,
                                                                        BlockLookup lookup,
                                                                        Supplier<BlockPos> controllerPos,
                                                                        Supplier<Direction> front,
                                                                        Supplier<Direction> up,
                                                                        BooleanSupplier flipped) {
        Objects.requireNonNull(matcher, "matcher");
        Objects.requireNonNull(lookup, "lookup");
        Objects.requireNonNull(controllerPos, "controllerPos");
        Objects.requireNonNull(front, "front");
        Objects.requireNonNull(up, "up");
        Objects.requireNonNull(flipped, "flipped");
        return placement -> matcher.match(
                placement,
                lookup,
                controllerPos.get(),
                front.get(),
                up.get(),
                flipped.getAsBoolean());
    }
}
