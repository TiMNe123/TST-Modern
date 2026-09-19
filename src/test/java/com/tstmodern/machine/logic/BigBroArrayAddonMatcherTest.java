package com.tstmodern.machine.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.tstmodern.machine.logic.BigBroArrayAddonMatcher.AddonMatch;
import com.tstmodern.machine.logic.BigBroArrayAddonMatcher.BlockLookup;
import com.tstmodern.machine.logic.BigBroArrayAddonMatcher.BlockRules;
import com.tstmodern.machine.logic.BigBroArrayAddonMatcher.Failure;
import com.tstmodern.registry.machine.BigBroArrayStructure;
import com.tstmodern.registry.machine.BigBroArrayStructure.AddonPlacement;
import com.tstmodern.registry.machine.BigBroArrayStructure.RelativeCell;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class BigBroArrayAddonMatcherTest {
    private static final BlockPos CONTROLLER = new BlockPos(100, 64, 100);
    private static final Direction FRONT = Direction.NORTH;
    private static final Direction UP = Direction.UP;

    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    private final Block PARALLEL_3 = Blocks.COPPER_BLOCK;
    private final Block PARALLEL_4 = Blocks.GOLD_BLOCK;
    private final Block FRAME_2 = Blocks.IRON_BLOCK;
    private final Block FRAME_3 = Blocks.DIAMOND_BLOCK;
    private final Block COIL_2 = Blocks.REDSTONE_BLOCK;
    private final Block COIL_4 = Blocks.EMERALD_BLOCK;
    private final Block GLASS_4 = Blocks.GLASS;
    private final Block GLASS_5 = Blocks.TINTED_GLASS;
    private final Block GEARBOX = Blocks.DEEPSLATE;
    private final Block CLEAN_CASING = Blocks.SMOOTH_STONE;

    private final BlockRules TEST_RULES = new BlockRules() {
        @Override
        public int frameTier(BlockState state) {
            return state.is(FRAME_2) ? 2 : state.is(FRAME_3) ? 3 : -1;
        }

        @Override
        public int glassTier(BlockState state) {
            return state.is(GLASS_4) ? 4 : state.is(GLASS_5) ? 5 : -1;
        }

        @Override
        public int parallelTier(BlockState state) {
            return state.is(PARALLEL_3) ? 3 : state.is(PARALLEL_4) ? 4 : -1;
        }

        @Override
        public int coilTier(BlockState state) {
            return state.is(COIL_2) ? 2 : state.is(COIL_4) ? 4 : -1;
        }

        @Override
        public boolean isGearbox(BlockState state) {
            return state.is(GEARBOX);
        }

        @Override
        public boolean isCleanCasing(BlockState state) {
            return state.is(CLEAN_CASING);
        }
    };

    private final BigBroArrayAddonMatcher matcher = new BigBroArrayAddonMatcher(TEST_RULES);

    @Test
    void allFourRotationsUseTheExactGtceuRelativeDirectionTransform() {
        List<BlockPos> expectedFirstQueries = List.of(
                new BlockPos(98, 58, 101),
                new BlockPos(98, 64, 107),
                new BlockPos(98, 70, 101),
                new BlockPos(98, 64, 95));
        List<BlockPos> actualFirstQueries = new ArrayList<>();

        for (AddonPlacement placement : BigBroArrayStructure.ADDON_PLACEMENTS) {
            RecordingUnloadedLookup lookup = new RecordingUnloadedLookup();
            AddonMatch match = matcher.match(placement, lookup, CONTROLLER, FRONT, UP, false);

            assertEquals(Failure.UNLOADED, match.failure());
            actualFirstQueries.add(lookup.firstQuery());
            assertEquals(0, lookup.stateReads());
        }
        assertEquals(expectedFirstQueries, actualFirstQueries);
    }

    @Test
    void unloadedLoadedMissingAndLoadedAirCellsAreDistinct() {
        TestWorld unloadedWorld = validWorld();
        BlockPos unloadedCell = worldPos(placement(1), firstCell(placement(1), 'K'));
        unloadedWorld.loaded().remove(unloadedCell);
        assertFailureOnly(matchAll(unloadedWorld), 1, Failure.UNLOADED);
        assertEquals(0, unloadedWorld.readCount(unloadedCell));

        TestWorld missingWorld = validWorld();
        BlockPos missingCell = worldPos(placement(3), firstCell(placement(3), 'K'));
        missingWorld.states().remove(missingCell);
        assertFailureOnly(matchAll(missingWorld), 3, Failure.MISSING_BLOCK);
        assertEquals(1, missingWorld.readCount(missingCell));

        TestWorld airWorld = validWorld();
        BlockPos airCell = worldPos(placement(2), firstCell(placement(2), 'K'));
        airWorld.states().put(airCell, Blocks.AIR.defaultBlockState());
        assertFailureOnly(matchAll(airWorld), 2, Failure.MISSING_BLOCK);
        assertEquals(1, airWorld.readCount(airCell));
    }

    @Test
    void emptyOrIncompletePlacementCannotMatchWithNegativeTiers() {
        for (List<RelativeCell> cells : List.of(
                List.<RelativeCell>of(),
                List.of(new RelativeCell(0, 0, 0, 'G')))) {
            AddonPlacement incomplete = new AddonPlacement(0, 0, 0, 0, 0, cells);
            TestWorld world = new TestWorld();
            if (!cells.isEmpty()) {
                BlockPos pos = worldPos(incomplete, cells.get(0));
                world.loaded().add(pos);
                world.states().put(pos, PARALLEL_3.defaultBlockState());
            }

            AddonMatch match = matcher.match(incomplete, world, CONTROLLER, FRONT, UP, false);

            assertFalse(match.valid());
            assertEquals(Failure.MISSING_BLOCK, match.failure());
            assertEquals(0, match.frameTier());
            assertEquals(0, match.glassTier());
            assertEquals(0, match.parallelTier());
            assertEquals(0, match.coilTier());
        }
    }

    @Test
    void wrongGearboxOrCleanCasingInvalidatesOnlyThatPlacement() {
        for (char symbol : new char[] { 'J', 'L' }) {
            TestWorld world = validWorld();
            AddonPlacement badPlacement = placement(2);
            world.states().put(worldPos(badPlacement, firstCell(badPlacement, symbol)), Blocks.DIRT.defaultBlockState());
            assertFailureOnly(matchAll(world), 2, Failure.WRONG_BLOCK);
        }
    }

    @Test
    void mixedTierInEachTieredChannelInvalidatesOnlyThatPlacement() {
        Map<Character, BlockState> alternates = Map.of(
                'G', PARALLEL_4.defaultBlockState(),
                'H', FRAME_3.defaultBlockState(),
                'I', COIL_4.defaultBlockState(),
                'K', GLASS_5.defaultBlockState());

        for (Map.Entry<Character, BlockState> alternate : alternates.entrySet()) {
            TestWorld world = validWorld();
            AddonPlacement badPlacement = placement(0);
            world.states().put(
                    worldPos(badPlacement, secondCell(badPlacement, alternate.getKey())),
                    alternate.getValue());
            assertFailureOnly(matchAll(world), 0, Failure.MIXED_TIER);
        }
    }

    @Test
    void fourValidPlacementsIgnoreEverythingOutsideTheirNineHundredOccupiedCells() {
        TestWorld world = validWorld();
        BlockPos unrelated = CONTROLLER.offset(200, 200, 200);
        world.loaded().add(unrelated);
        world.states().put(unrelated, Blocks.DIRT.defaultBlockState());

        List<AddonMatch> matches = matchAll(world);

        assertEquals(4, matches.stream().filter(AddonMatch::valid).count());
        for (AddonMatch match : matches) {
            assertEquals(Failure.NONE, match.failure());
            assertEquals(2, match.frameTier());
            assertEquals(4, match.glassTier());
            assertEquals(3, match.parallelTier());
            assertEquals(2, match.coilTier());
        }
        assertFalse(world.wasRead(unrelated));
    }

    private List<AddonMatch> matchAll(BlockLookup lookup) {
        List<AddonMatch> matches = new ArrayList<>(4);
        for (AddonPlacement placement : BigBroArrayStructure.ADDON_PLACEMENTS) {
            matches.add(matcher.match(placement, lookup, CONTROLLER, FRONT, UP, false));
        }
        return matches;
    }

    private static void assertFailureOnly(List<AddonMatch> matches, int invalidIndex, Failure failure) {
        for (AddonMatch match : matches) {
            if (match.index() == invalidIndex) {
                assertFalse(match.valid());
                assertEquals(failure, match.failure());
            } else {
                assertTrue(match.valid(), "placement " + match.index() + " should remain valid");
                assertEquals(Failure.NONE, match.failure());
            }
        }
    }

    private TestWorld validWorld() {
        TestWorld world = new TestWorld();
        for (AddonPlacement placement : BigBroArrayStructure.ADDON_PLACEMENTS) {
            assertEquals(900, placement.cells().size());
            for (RelativeCell cell : placement.cells()) {
                BlockPos pos = worldPos(placement, cell);
                world.loaded().add(pos);
                world.states().put(pos, stateFor(cell.symbol()));
            }
        }
        return world;
    }

    private BlockState stateFor(char symbol) {
        return switch (symbol) {
            case 'G' -> PARALLEL_3.defaultBlockState();
            case 'H' -> FRAME_2.defaultBlockState();
            case 'I' -> COIL_2.defaultBlockState();
            case 'J' -> GEARBOX.defaultBlockState();
            case 'K' -> GLASS_4.defaultBlockState();
            case 'L' -> CLEAN_CASING.defaultBlockState();
            default -> throw new AssertionError("unexpected addon symbol " + symbol);
        };
    }

    private static BlockPos worldPos(AddonPlacement placement, RelativeCell sourceCell) {
        RelativeCell cell = new RelativeCell(
                sourceCell.right() - placement.offsetX(),
                sourceCell.down() - placement.offsetY(),
                sourceCell.back() - placement.offsetZ(),
                sourceCell.symbol());
        return RelativeDirection.offsetPos(
                CONTROLLER, FRONT, UP, false,
                -cell.down(), -cell.right(), -cell.back());
    }

    private static RelativeCell firstCell(AddonPlacement placement, char symbol) {
        return placement.cells().stream().filter(cell -> cell.symbol() == symbol).findFirst().orElseThrow();
    }

    private static RelativeCell secondCell(AddonPlacement placement, char symbol) {
        return placement.cells().stream().filter(cell -> cell.symbol() == symbol).skip(1).findFirst().orElseThrow();
    }

    private static AddonPlacement placement(int index) {
        return BigBroArrayStructure.ADDON_PLACEMENTS.get(index);
    }

    private static final class RecordingUnloadedLookup implements BlockLookup {
        private BlockPos firstQuery;
        private int stateReads;

        @Override
        public boolean isLoaded(BlockPos pos) {
            if (firstQuery == null) firstQuery = pos;
            return false;
        }

        @Override
        public BlockState getLoadedState(BlockPos pos) {
            stateReads++;
            return null;
        }

        BlockPos firstQuery() {
            return firstQuery;
        }

        int stateReads() {
            return stateReads;
        }
    }

    private static final class TestWorld implements BlockLookup {
        private final Set<BlockPos> loaded = new HashSet<>();
        private final Map<BlockPos, BlockState> states = new HashMap<>();
        private final Map<BlockPos, Integer> reads = new HashMap<>();

        @Override
        public boolean isLoaded(BlockPos pos) {
            return loaded.contains(pos);
        }

        @Override
        public BlockState getLoadedState(BlockPos pos) {
            reads.merge(pos, 1, Integer::sum);
            return states.get(pos);
        }

        Set<BlockPos> loaded() {
            return loaded;
        }

        Map<BlockPos, BlockState> states() {
            return states;
        }

        int readCount(BlockPos pos) {
            return reads.getOrDefault(pos, 0);
        }

        boolean wasRead(BlockPos pos) {
            return reads.containsKey(pos);
        }
    }
}
