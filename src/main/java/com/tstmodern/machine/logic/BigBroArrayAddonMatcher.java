package com.tstmodern.machine.logic;

import java.util.Objects;

import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.tstmodern.registry.machine.BigBroArrayStructure.AddonPlacement;
import com.tstmodern.registry.machine.BigBroArrayStructure.RelativeCell;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

/** Matches one optional Big Bro Array addon without affecting the validity of any other placement. */
public final class BigBroArrayAddonMatcher {
    private static final BlockRules GTCEU_RULES = new BlockRules() {
        @Override
        public int frameTier(BlockState state) {
            return BigBroArrayTierRules.frameTier(state.getBlock());
        }

        @Override
        public int glassTier(BlockState state) {
            return BigBroArrayTierRules.glassTier(state.getBlock());
        }

        @Override
        public int parallelTier(BlockState state) {
            return BigBroArrayTierRules.parallelCasingTier(state.getBlock());
        }

        @Override
        public int coilTier(BlockState state) {
            return BigBroArrayTierRules.coilTier(state);
        }

        @Override
        public boolean isGearbox(BlockState state) {
            return state.is(GTBlocks.CASING_TUNGSTENSTEEL_GEARBOX.get());
        }

        @Override
        public boolean isCleanCasing(BlockState state) {
            return state.is(GTBlocks.CASING_STAINLESS_CLEAN.get());
        }
    };

    private final BlockRules rules;

    public BigBroArrayAddonMatcher() {
        this(GTCEU_RULES);
    }

    BigBroArrayAddonMatcher(BlockRules rules) {
        this.rules = Objects.requireNonNull(rules, "rules");
    }

    public AddonMatch match(
                            AddonPlacement placement,
                            BlockLookup lookup,
                            BlockPos controllerPos,
                            Direction front,
                            Direction up,
                            boolean flipped) {
        Objects.requireNonNull(placement, "placement");
        Objects.requireNonNull(lookup, "lookup");
        Objects.requireNonNull(controllerPos, "controllerPos");
        Objects.requireNonNull(front, "front");
        Objects.requireNonNull(up, "up");

        int frameTier = -1;
        int glassTier = -1;
        int parallelTier = -1;
        int coilTier = -1;

        for (RelativeCell sourceCell : placement.cells()) {
            RelativeCell cell = controllerRelativeCell(placement, sourceCell);
            BlockPos world = RelativeDirection.offsetPos(
                    controllerPos, front, up, flipped,
                    -cell.down(), -cell.right(), -cell.back());
            if (!lookup.isLoaded(world)) {
                return invalid(placement.index(), Failure.UNLOADED);
            }

            BlockState state = lookup.getLoadedState(world);
            if (state == null || state.isAir()) {
                return invalid(placement.index(), Failure.MISSING_BLOCK);
            }

            switch (cell.symbol()) {
                case 'G' -> {
                    int tier = rules.parallelTier(state);
                    if (tier < 0) return invalid(placement.index(), Failure.WRONG_BLOCK);
                    if (parallelTier >= 0 && parallelTier != tier) {
                        return invalid(placement.index(), Failure.MIXED_TIER);
                    }
                    parallelTier = tier;
                }
                case 'H' -> {
                    int tier = rules.frameTier(state);
                    if (tier < 0) return invalid(placement.index(), Failure.WRONG_BLOCK);
                    if (frameTier >= 0 && frameTier != tier) {
                        return invalid(placement.index(), Failure.MIXED_TIER);
                    }
                    frameTier = tier;
                }
                case 'I' -> {
                    int tier = rules.coilTier(state);
                    if (tier < 0) return invalid(placement.index(), Failure.WRONG_BLOCK);
                    if (coilTier >= 0 && coilTier != tier) {
                        return invalid(placement.index(), Failure.MIXED_TIER);
                    }
                    coilTier = tier;
                }
                case 'J' -> {
                    if (!rules.isGearbox(state)) return invalid(placement.index(), Failure.WRONG_BLOCK);
                }
                case 'K' -> {
                    int tier = rules.glassTier(state);
                    if (tier < 0) return invalid(placement.index(), Failure.WRONG_BLOCK);
                    if (glassTier >= 0 && glassTier != tier) {
                        return invalid(placement.index(), Failure.MIXED_TIER);
                    }
                    glassTier = tier;
                }
                case 'L' -> {
                    if (!rules.isCleanCasing(state)) return invalid(placement.index(), Failure.WRONG_BLOCK);
                }
                default -> {
                    return invalid(placement.index(), Failure.WRONG_BLOCK);
                }
            }
        }

        return new AddonMatch(
                placement.index(), true, frameTier, glassTier, parallelTier, coilTier, Failure.NONE);
    }

    private static RelativeCell controllerRelativeCell(AddonPlacement placement, RelativeCell cell) {
        return new RelativeCell(
                cell.right() - placement.offsetX(),
                cell.down() - placement.offsetY(),
                cell.back() - placement.offsetZ(),
                cell.symbol());
    }

    private static AddonMatch invalid(int index, Failure failure) {
        return new AddonMatch(index, false, 0, 0, 0, 0, failure);
    }

    /** World access that proves chunk availability before returning a state from that chunk. */
    public interface BlockLookup {
        boolean isLoaded(BlockPos pos);

        /** Returns the state at a position already proven loaded, or {@code null} when the cell is absent. */
        BlockState getLoadedState(BlockPos pos);
    }

    interface BlockRules {
        int frameTier(BlockState state);

        int glassTier(BlockState state);

        int parallelTier(BlockState state);

        int coilTier(BlockState state);

        boolean isGearbox(BlockState state);

        boolean isCleanCasing(BlockState state);
    }

    public enum Failure {
        NONE,
        UNLOADED,
        MISSING_BLOCK,
        WRONG_BLOCK,
        MIXED_TIER
    }

    public record AddonMatch(
                             int index,
                             boolean valid,
                             int frameTier,
                             int glassTier,
                             int parallelTier,
                             int coilTier,
                             Failure failure) {}
}
