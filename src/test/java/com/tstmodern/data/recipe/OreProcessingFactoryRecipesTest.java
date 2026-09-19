package com.tstmodern.data.recipe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

final class OreProcessingFactoryRecipesTest {
    @Test
    void normalizesMixedCaseOrePrefixForRecipeIds() {
        String path = OreProcessingFactoryRecipes.recipePath("redSand", "gtceu:yellow_limonite");

        assertEquals("ore_processing_factory/redsand_gtceu_yellow_limonite", path);
        assertTrue(ResourceLocation.isValidResourceLocation("tstmodern:" + path));
    }

    @Test
    void mergesDuplicateRecipeOutputsIntoOneStack() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        List<ItemStack> outputs = new ArrayList<>();

        OreProcessingFactoryRecipes.addOutput(outputs, new ItemStack(Items.REDSTONE, 4));
        OreProcessingFactoryRecipes.addOutput(outputs, new ItemStack(Items.REDSTONE, 3));

        assertEquals(1, outputs.size());
        assertEquals(7, outputs.get(0).getCount());
    }

    @Test
    void mergesOnlyOutputsWithMatchingNbt() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        List<ItemStack> outputs = new ArrayList<>();
        ItemStack first = new ItemStack(Items.REDSTONE, 2);
        ItemStack matching = new ItemStack(Items.REDSTONE, 3);
        ItemStack distinct = new ItemStack(Items.REDSTONE, 5);
        CompoundTag sharedTag = new CompoundTag();
        sharedTag.putString("grade", "pure");
        first.setTag(sharedTag.copy());
        matching.setTag(sharedTag.copy());
        distinct.getOrCreateTag().putString("grade", "impure");

        OreProcessingFactoryRecipes.addOutput(outputs, first);
        OreProcessingFactoryRecipes.addOutput(outputs, matching);
        OreProcessingFactoryRecipes.addOutput(outputs, distinct);

        assertEquals(2, outputs.size());
        assertEquals(5, outputs.get(0).getCount());
        assertEquals("pure", outputs.get(0).getTag().getString("grade"));
        assertEquals(5, outputs.get(1).getCount());
        assertEquals("impure", outputs.get(1).getTag().getString("grade"));
    }
}
