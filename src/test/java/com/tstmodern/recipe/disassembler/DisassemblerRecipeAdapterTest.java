package com.tstmodern.recipe.disassembler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import net.minecraft.SharedConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;

final class DisassemblerRecipeAdapterTest {

    @BeforeAll
    static void initMinecraft() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void adaptAcceptsValidSingleOutputRecipe() {
        DisassemblerRecipeAdapter.DecodedRecipe recipe = new DisassemblerRecipeAdapter.DecodedRecipe(
                new ResourceLocation("gtceu", "test_recipe"),
                2,
                List.of(new DisassemblerRecipeAdapter.DecodedItemContent(List.of(Items.GOLD_INGOT), 4, 100)),
                List.of(new DisassemblerRecipeAdapter.DecodedFluidContent(List.of(Fluids.LAVA), 100, 100)),
                List.of(new DisassemblerRecipeAdapter.DecodedItemContent(List.of(Items.GOLDEN_APPLE), 1, 100)),
                0);

        Optional<DisassemblerRecipeDescriptor> descriptor = DisassemblerRecipeAdapter.adapt(
                recipe, 0, item -> false);

        assertTrue(descriptor.isPresent());
        DisassemblerRecipeDescriptor desc = descriptor.get();
        assertEquals(new ResourceLocation("gtceu", "test_recipe"), desc.sourceId());
        assertEquals(0, desc.sourcePriority());
        assertEquals(2, desc.recipeTier());
        assertEquals(Items.GOLDEN_APPLE, desc.outputItem());
        assertEquals(1, desc.outputAmount());
        assertEquals(1, desc.returnedItems().size());
        assertEquals(Items.GOLD_INGOT, desc.returnedItems().get(0).item());
        assertEquals(4, desc.returnedItems().get(0).amount());
        assertEquals(1, desc.returnedFluids().size());
        assertEquals(Fluids.LAVA, desc.returnedFluids().get(0).fluid());
        assertEquals(100, desc.returnedFluids().get(0).amount());
    }

    @Test
    void adaptRejectsMultipleItemOutputsOrFluidOutputs() {
        // Multiple item outputs
        DisassemblerRecipeAdapter.DecodedRecipe multiItemOutput = new DisassemblerRecipeAdapter.DecodedRecipe(
                new ResourceLocation("gtceu", "multi_output"),
                1,
                List.of(new DisassemblerRecipeAdapter.DecodedItemContent(List.of(Items.IRON_INGOT), 1, 100)),
                List.of(),
                List.of(
                        new DisassemblerRecipeAdapter.DecodedItemContent(List.of(Items.IRON_NUGGET), 9, 100),
                        new DisassemblerRecipeAdapter.DecodedItemContent(List.of(Items.DIRT), 1, 100)),
                0);
        assertFalse(DisassemblerRecipeAdapter.adapt(multiItemOutput, 0, item -> false).isPresent());

        // Has fluid output
        DisassemblerRecipeAdapter.DecodedRecipe fluidOutput = new DisassemblerRecipeAdapter.DecodedRecipe(
                new ResourceLocation("gtceu", "fluid_output"),
                1,
                List.of(new DisassemblerRecipeAdapter.DecodedItemContent(List.of(Items.IRON_INGOT), 1, 100)),
                List.of(),
                List.of(new DisassemblerRecipeAdapter.DecodedItemContent(List.of(Items.IRON_NUGGET), 9, 100)),
                1);
        assertFalse(DisassemblerRecipeAdapter.adapt(fluidOutput, 0, item -> false).isPresent());
    }

    @Test
    void adaptRejectsBlacklistedOutputItem() {
        DisassemblerRecipeAdapter.DecodedRecipe recipe = new DisassemblerRecipeAdapter.DecodedRecipe(
                new ResourceLocation("gtceu", "blacklisted"),
                1,
                List.of(new DisassemblerRecipeAdapter.DecodedItemContent(List.of(Items.IRON_INGOT), 1, 100)),
                List.of(),
                List.of(new DisassemblerRecipeAdapter.DecodedItemContent(List.of(Items.BARRIER), 1, 100)),
                0);

        Optional<DisassemblerRecipeDescriptor> descriptor = DisassemblerRecipeAdapter.adapt(
                recipe, 0, item -> item == Items.BARRIER);

        assertFalse(descriptor.isPresent());
    }

    @Test
    void adaptOmitsChanceZeroInputs() {
        DisassemblerRecipeAdapter.DecodedRecipe recipe = new DisassemblerRecipeAdapter.DecodedRecipe(
                new ResourceLocation("gtceu", "chance_zero"),
                1,
                List.of(
                        new DisassemblerRecipeAdapter.DecodedItemContent(List.of(Items.IRON_INGOT), 1, 100),
                        new DisassemblerRecipeAdapter.DecodedItemContent(List.of(Items.DIRT), 1, 0)),
                List.of(
                        new DisassemblerRecipeAdapter.DecodedFluidContent(List.of(Fluids.WATER), 100, 100),
                        new DisassemblerRecipeAdapter.DecodedFluidContent(List.of(Fluids.LAVA), 100, 0)),
                List.of(new DisassemblerRecipeAdapter.DecodedItemContent(List.of(Items.IRON_BLOCK), 1, 100)),
                0);

        Optional<DisassemblerRecipeDescriptor> descriptor = DisassemblerRecipeAdapter.adapt(
                recipe, 0, item -> false);

        assertTrue(descriptor.isPresent());
        assertEquals(1, descriptor.get().returnedItems().size());
        assertEquals(Items.IRON_INGOT, descriptor.get().returnedItems().get(0).item());
        assertEquals(1, descriptor.get().returnedFluids().size());
        assertEquals(Fluids.WATER, descriptor.get().returnedFluids().get(0).fluid());
    }
}
