package com.tstmodern.recipe.disassembler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import net.minecraft.SharedConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

final class DisassemblerRecipeSourcesTest {

    @BeforeAll
    static void initMinecraft() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void lowerPrioritySourceWinsForTheSameOutput() {
        DisassemblerRecipeSources sources = new DisassemblerRecipeSources();
        sources.registerSpecial("assembler", 40,
                () -> List.of(descriptor("assembler/diamond", 40, Items.DIAMOND, Items.COAL)));
        sources.registerSpecial("special", 0,
                () -> List.of(descriptor("special/diamond", 0, Items.DIAMOND, Items.EMERALD)));
        DisassemblerRecipeIndex index = new DisassemblerRecipeIndex(sources, new DisassemblerRecipeAdapter());

        DisassemblerRecipeDescriptor winner = index.find(Items.DIAMOND).orElseThrow();

        assertEquals(new ResourceLocation("tstmodern", "special/diamond"), winner.sourceId());
        assertEquals(Items.EMERALD, winner.returnedItems().get(0).item());
    }

    @Test
    void lateRegistrationAdvancesGenerationAndInvalidatesRuntimeSnapshot() {
        DisassemblerRecipeSources sources = new DisassemblerRecipeSources();
        DisassemblerRecipeIndex index = new DisassemblerRecipeIndex(sources, new DisassemblerRecipeAdapter());
        long emptyGeneration = sources.generation();

        assertTrue(index.find(Items.DIAMOND).isEmpty());
        sources.registerSpecial("late_miracle_top", 20,
                () -> List.of(descriptor("miracle_top/diamond", 20, Items.DIAMOND, Items.GOLD_INGOT)));

        assertTrue(sources.generation() > emptyGeneration);
        assertEquals(Items.GOLD_INGOT,
                index.find(Items.DIAMOND).orElseThrow().returnedItems().get(0).item());
    }

    @Test
    void photonSourceOnlyAcceptsDedicatedTstmodernRecipeIds() {
        assertTrue(DisassemblerRecipeSources.isPhotonControllerRecipe(
                new ResourceLocation("tstmodern", "photon_controller/wafer")));
        assertFalse(DisassemblerRecipeSources.isPhotonControllerRecipe(
                new ResourceLocation("tstmodern", "laser_engraver/wafer")));
        assertFalse(DisassemblerRecipeSources.isPhotonControllerRecipe(
                new ResourceLocation("gtceu", "photon_controller/wafer")));
    }

    @Test
    void fusionCoilSpecialReturnsTheTstComponents() {
        DisassemblerRecipeDescriptor descriptor = DisassemblerSpecialRecipes.createFusionCoilDescriptor(
                Items.DIAMOND_BLOCK,
                Items.COPPER_BLOCK,
                Items.IRON_INGOT,
                Items.ENDER_EYE,
                Items.REPEATER);

        assertEquals(Items.DIAMOND_BLOCK, descriptor.outputItem());
        assertEquals(1, descriptor.outputAmount());
        assertEquals(3, descriptor.recipeTier());
        assertEquals(List.of(
                new DisassemblerRecipeDescriptor.ReturnedItem(Items.COPPER_BLOCK, 1),
                new DisassemblerRecipeDescriptor.ReturnedItem(Items.IRON_INGOT, 2),
                new DisassemblerRecipeDescriptor.ReturnedItem(Items.ENDER_EYE, 2),
                new DisassemblerRecipeDescriptor.ReturnedItem(Items.REPEATER, 4)), descriptor.returnedItems());
    }

    private static DisassemblerRecipeDescriptor descriptor(String path, int priority, Item output, Item returned) {
        return new DisassemblerRecipeDescriptor(
                new ResourceLocation("tstmodern", path),
                priority,
                1,
                output,
                1,
                List.of(new DisassemblerRecipeDescriptor.ReturnedItem(returned, 1)),
                List.of());
    }
}
