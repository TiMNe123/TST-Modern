package com.tstmodern.machine.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

final class StarcoreMinerLogicTest {

    @BeforeAll
    static void initMinecraft() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void preservesBaseAndAstralFabricatorBoost() {
        assertEquals(131_072, StarcoreMinerLogic.boostedStackSize(0));
        assertEquals(262_144, StarcoreMinerLogic.boostedStackSize(1));
        assertEquals(786_432, StarcoreMinerLogic.boostedStackSize(2));
        assertEquals(2_097_152, StarcoreMinerLogic.boostedStackSize(4));
        assertEquals(134_217_728, StarcoreMinerLogic.boostedStackSize(64));
    }

    @Test
    void aggregatesDuplicateWeightsAndSelectsEveryBoundary() {
        List<StarcoreMinerLogic.WeightedEntry<String>> entries = StarcoreMinerLogic.aggregateWeights(List.of(
                new StarcoreMinerLogic.WeightedEntry<>("copper", 6),
                new StarcoreMinerLogic.WeightedEntry<>("iron", 4),
                new StarcoreMinerLogic.WeightedEntry<>("copper", 2)));

        assertEquals(List.of(
                new StarcoreMinerLogic.WeightedEntry<>("copper", 8),
                new StarcoreMinerLogic.WeightedEntry<>("iron", 4)), entries);
        assertEquals("copper", StarcoreMinerLogic.select(entries, 0));
        assertEquals("copper", StarcoreMinerLogic.select(entries, 7));
        assertEquals("iron", StarcoreMinerLogic.select(entries, 8));
        assertEquals("iron", StarcoreMinerLogic.select(entries, 11));
        assertThrows(IllegalArgumentException.class, () -> StarcoreMinerLogic.select(entries, 12));
    }

    @Test
    void aggregatesEquivalentItemStacksIgnoringCount() {
        List<StarcoreMinerLogic.WeightedEntry<ItemStack>> entries =
                StarcoreMinerLogic.aggregateItemStackWeights(List.of(
                        new StarcoreMinerLogic.WeightedEntry<>(new ItemStack(Items.DIAMOND, 1), 6),
                        new StarcoreMinerLogic.WeightedEntry<>(new ItemStack(Items.DIAMOND, 64), 7)));

        assertEquals(1, entries.size());
        assertEquals(13, entries.get(0).weight());
        assertEquals(1, entries.get(0).value().getCount());
    }

    @Test
    void saturatesOversizedWeights() {
        assertEquals(Integer.MAX_VALUE, StarcoreMinerLogic.positiveWeightProduct(Integer.MAX_VALUE, 2));
        assertEquals(Integer.MAX_VALUE, StarcoreMinerLogic.totalWeight(List.of(
                new StarcoreMinerLogic.WeightedEntry<>("a", Integer.MAX_VALUE),
                new StarcoreMinerLogic.WeightedEntry<>("b", 1))));
    }
}
