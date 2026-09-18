package com.tstmodern.integration.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.advanced.IRecipeManagerPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public final class TSTEquivalentRecipePlugin implements IRecipeManagerPlugin {

    private static final Map<ResourceLocation, ResourceLocation> EQUIVALENTS = new HashMap<>();

    static {
        register("gtceu:infinity_ingot", "avaritia:infinity_ingot");
        register("gtceu:awakened_draconium_ingot", "draconicevolution:awakened_draconium_ingot");
        register("tstmodern:draconium_core", "draconicevolution:draconium_core");
        register("tstmodern:dragon_heart", "draconicevolution:dragon_heart");
        register("tstmodern:crystal_matrix_ingot", "avaritia:crystal_matrix_ingot");
        register("tstmodern:diamond_lattice", "avaritia:diamond_lattice");
        register("tstmodern:infinity_catalyst", "avaritia:infinity_catalyst");
    }

    private static void register(String a, String b) {
        ResourceLocation ra = new ResourceLocation(a);
        ResourceLocation rb = new ResourceLocation(b);
        EQUIVALENTS.put(ra, rb);
        EQUIVALENTS.put(rb, ra);
    }

    private final ThreadLocal<Boolean> inLookup = ThreadLocal.withInitial(() -> false);
    private IJeiRuntime runtime;

    public void setRuntime(IJeiRuntime runtime) {
        this.runtime = runtime;
    }

    private Optional<ItemStack> resolveEquivalent(IFocus<?> focus) {
        if (inLookup.get() || runtime == null) return Optional.empty();

        Object value = focus.getTypedValue().getIngredient();
        if (!(value instanceof ItemStack stack) || stack.isEmpty()) return Optional.empty();

        ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (id == null) return Optional.empty();

        ResourceLocation mappedId = EQUIVALENTS.get(id);
        if (mappedId == null) return Optional.empty();

        Item mappedItem = ForgeRegistries.ITEMS.getValue(mappedId);
        if (mappedItem == null) return Optional.empty();

        return Optional.of(new ItemStack(mappedItem));
    }

    @Override
    public <V> List<RecipeType<?>> getRecipeTypes(IFocus<V> focus) {
        if (inLookup.get() || runtime == null) return Collections.emptyList();

        Optional<ItemStack> equivalent = resolveEquivalent(focus);
        if (equivalent.isEmpty()) return Collections.emptyList();

        inLookup.set(true);
        try {
            IFocus<ItemStack> mappedFocus = runtime.getJeiHelpers().getFocusFactory()
                    .createFocus(focus.getRole(), VanillaTypes.ITEM_STACK, equivalent.get());
            return runtime.getRecipeManager().createRecipeCategoryLookup()
                    .limitFocus(List.of(mappedFocus))
                    .get()
                    .<RecipeType<?>>map(IRecipeCategory::getRecipeType)
                    .toList();
        } finally {
            inLookup.set(false);
        }
    }

    @Override
    public <T, V> List<T> getRecipes(IRecipeCategory<T> recipeCategory, IFocus<V> focus) {
        if (inLookup.get() || runtime == null) return Collections.emptyList();

        Optional<ItemStack> equivalent = resolveEquivalent(focus);
        if (equivalent.isEmpty()) return Collections.emptyList();

        inLookup.set(true);
        try {
            IFocus<ItemStack> mappedFocus = runtime.getJeiHelpers().getFocusFactory()
                    .createFocus(focus.getRole(), VanillaTypes.ITEM_STACK, equivalent.get());
            return runtime.getRecipeManager().createRecipeLookup(recipeCategory.getRecipeType())
                    .limitFocus(List.of(mappedFocus))
                    .get()
                    .toList();
        } finally {
            inLookup.set(false);
        }
    }

    @Override
    public <T> List<T> getRecipes(IRecipeCategory<T> recipeCategory) {
        return Collections.emptyList();
    }
}
