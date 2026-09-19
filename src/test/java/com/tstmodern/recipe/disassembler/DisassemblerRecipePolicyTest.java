package com.tstmodern.recipe.disassembler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import net.minecraft.SharedConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;

final class DisassemblerRecipePolicyTest {

    @BeforeAll
    static void initMinecraft() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void tierAllowsCorrectlyEvaluatesTierGates() {
        // casingTier 1 allows recipe tier 0, 1, 2
        assertTrue(DisassemblerRecipePolicy.tierAllows(1, 0));
        assertTrue(DisassemblerRecipePolicy.tierAllows(1, 1));
        assertTrue(DisassemblerRecipePolicy.tierAllows(1, 2));
        assertFalse(DisassemblerRecipePolicy.tierAllows(1, 3));

        // casingTier 13 allows recipe tier <= 14
        assertTrue(DisassemblerRecipePolicy.tierAllows(13, 14));
        assertFalse(DisassemblerRecipePolicy.tierAllows(13, 15));

        // casingTier 14 (MAX) allows all non-negative tiers
        assertTrue(DisassemblerRecipePolicy.tierAllows(14, 0));
        assertTrue(DisassemblerRecipePolicy.tierAllows(14, 99));

        // Negative recipe tier is always rejected
        assertFalse(DisassemblerRecipePolicy.tierAllows(1, -1));
        assertFalse(DisassemblerRecipePolicy.tierAllows(14, -1));

        // Invalid casing tier throws
        assertThrows(IllegalArgumentException.class, () -> DisassemblerRecipePolicy.tierAllows(0, 1));
        assertThrows(IllegalArgumentException.class, () -> DisassemblerRecipePolicy.tierAllows(15, 1));
    }

    @Test
    void saturatingAddAndMultiplyHandleOverflows() {
        assertEquals(10L, DisassemblerRecipePolicy.saturatingAdd(4L, 6L));
        assertEquals(Long.MAX_VALUE, DisassemblerRecipePolicy.saturatingAdd(Long.MAX_VALUE - 5L, 10L));

        assertEquals(24L, DisassemblerRecipePolicy.saturatingMultiply(4L, 6L));
        assertEquals(Long.MAX_VALUE, DisassemblerRecipePolicy.saturatingMultiply(Long.MAX_VALUE / 2L + 1L, 2L));
    }

    @Test
    void durationTicksCalculatesFormulaCorrectly() {
        assertEquals(100, DisassemblerRecipePolicy.durationTicks(1, 1));
        assertEquals(200, DisassemblerRecipePolicy.durationTicks(8, 1));
        assertEquals(100, DisassemblerRecipePolicy.durationTicks(8, 2));
        assertEquals(400, DisassemblerRecipePolicy.durationTicks(32, 2));
    }

    @Test
    void aggregateProcessesBatchesAndReturnsOutputs() {
        DisassemblerRecipeDescriptor descriptor = new DisassemblerRecipeDescriptor(
                new ResourceLocation("tstmodern", "test_source"),
                1,
                1,
                Items.IRON_INGOT,
                2,
                List.of(new DisassemblerRecipeDescriptor.ReturnedItem(Items.IRON_NUGGET, 18)),
                List.of(new DisassemblerRecipeDescriptor.ReturnedFluid(Fluids.WATER, 50)));

        Map<Item, Long> available = Map.of(Items.IRON_INGOT, 5L);
        Optional<DisassemblerRecipePolicy.AggregatePlan> plan = DisassemblerRecipePolicy.aggregate(
                available, 1, item -> item == Items.IRON_INGOT ? Optional.of(descriptor) : Optional.empty());

        assertTrue(plan.isPresent());
        DisassemblerRecipePolicy.AggregatePlan aggregate = plan.get();

        // 5 Input Items with batch size 2 -> 2 batches (4 consumed)
        assertEquals(4, aggregate.consumedItems().get(Items.IRON_INGOT));
        assertEquals(36, aggregate.returnedItems().get(Items.IRON_NUGGET));
        assertEquals(100, aggregate.returnedFluids().get(Fluids.WATER));
        assertEquals(2L, aggregate.processed());
        assertEquals(100, aggregate.durationTicks());
    }

    @Test
    void aggregateReturnsEmptyWhenNoMatchingOrEligibleInputs() {
        DisassemblerRecipeDescriptor highTierDescriptor = new DisassemblerRecipeDescriptor(
                new ResourceLocation("tstmodern", "high_tier_source"),
                1,
                5,
                Items.DIAMOND,
                1,
                List.of(new DisassemblerRecipeDescriptor.ReturnedItem(Items.COAL, 8)),
                List.of());

        // Casing tier 1 only allows recipe tier <= 2; recipe tier 5 is rejected
        Map<Item, Long> available = Map.of(Items.DIAMOND, 10L);
        Optional<DisassemblerRecipePolicy.AggregatePlan> plan = DisassemblerRecipePolicy.aggregate(
                available, 1, item -> Optional.of(highTierDescriptor));

        assertFalse(plan.isPresent());
    }

    @Test
    void runtimePlanRejectsBatchWhenCompleteItemOutputCannotFit() {
        DisassemblerRecipeDescriptor descriptor = new DisassemblerRecipeDescriptor(
                new ResourceLocation("tstmodern", "oversized_return"),
                1,
                1,
                Items.DIAMOND,
                1,
                List.of(new DisassemblerRecipeDescriptor.ReturnedItem(Items.IRON_INGOT, 16 * 64 + 1)),
                List.of());

        Optional<DisassemblerRecipeIndex.RuntimeRecipePlan> plan = DisassemblerRecipeIndex.buildRuntimePlan(
                Map.of(Items.DIAMOND, 1L),
                1,
                item -> item == Items.DIAMOND ? Optional.of(descriptor) : Optional.empty());

        assertTrue(plan.isEmpty(), "The machine must not consume input when even one returned item would be truncated");
    }

    @Test
    void runtimePlanKeepsEveryItemAtExactOutputCapacity() {
        DisassemblerRecipeDescriptor descriptor = new DisassemblerRecipeDescriptor(
                new ResourceLocation("tstmodern", "exact_return_capacity"),
                1,
                1,
                Items.DIAMOND,
                1,
                List.of(new DisassemblerRecipeDescriptor.ReturnedItem(Items.IRON_INGOT, 16 * 64)),
                List.of());

        Optional<DisassemblerRecipeIndex.RuntimeRecipePlan> plan = DisassemblerRecipeIndex.buildRuntimePlan(
                Map.of(Items.DIAMOND, 1L),
                1,
                item -> item == Items.DIAMOND ? Optional.of(descriptor) : Optional.empty());

        assertTrue(plan.isPresent());
        assertEquals(16, plan.get().returnedItems().size());
        assertEquals(16 * 64, plan.get().returnedItems().stream().mapToInt(stack -> stack.getCount()).sum());
    }
}
