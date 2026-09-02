package com.tstmodern.recipe.disassembler;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.data.recipe.CustomTags;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

/** TST special reverse recipes that override generic GTCEu recipe-map adaptation. */
final class DisassemblerSpecialRecipes {
    private static final ResourceLocation FUSION_COIL_ID =
            new ResourceLocation("tstmodern", "special/disassembler/fusion_coil");

    private DisassemblerSpecialRecipes() {}

    static Collection<DisassemblerRecipeDescriptor> all() {
        Item circuit = firstItem(CustomTags.LuV_CIRCUITS);
        if (circuit == null) {
            return List.of();
        }
        return List.of(createFusionCoilDescriptor(
                GTBlocks.FUSION_COIL.get().asItem(),
                GTBlocks.SUPERCONDUCTING_COIL.get().asItem(),
                GTItems.NEUTRON_REFLECTOR.get(),
                GTItems.FIELD_GENERATOR_MV.get(),
                circuit));
    }

    static DisassemblerRecipeDescriptor createFusionCoilDescriptor(Item fusionCoil, Item superconductingCoil,
                                                                    Item neutronReflector, Item mvFieldGenerator,
                                                                    Item luvCircuit) {
        return new DisassemblerRecipeDescriptor(
                FUSION_COIL_ID,
                DisassemblerRecipeSources.PRIORITY_SPECIAL,
                GTValues.HV,
                fusionCoil,
                1,
                List.of(
                        new DisassemblerRecipeDescriptor.ReturnedItem(superconductingCoil, 1),
                        new DisassemblerRecipeDescriptor.ReturnedItem(neutronReflector, 2),
                        new DisassemblerRecipeDescriptor.ReturnedItem(mvFieldGenerator, 2),
                        new DisassemblerRecipeDescriptor.ReturnedItem(luvCircuit, 4)),
                List.of());
    }

    @SuppressWarnings("deprecation")
    private static Item firstItem(net.minecraft.tags.TagKey<Item> tag) {
        return Arrays.stream(Ingredient.of(tag).getItems())
                .filter(stack -> stack != null && !stack.isEmpty())
                .map(ItemStack::getItem)
                .min(Comparator.comparing(item -> BuiltInRegistries.ITEM.getKey(item).toString()))
                .orElse(null);
    }
}
