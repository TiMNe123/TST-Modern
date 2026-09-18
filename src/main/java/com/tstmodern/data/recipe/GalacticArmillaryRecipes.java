package com.tstmodern.data.recipe;

import static com.gregtechceu.gtceu.api.GTValues.UEV;
import static com.gregtechceu.gtceu.api.GTValues.ZPM;
import static com.gregtechceu.gtceu.api.GTValues.VA;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.block;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.ingot;

import java.util.function.Consumer;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.tstmodern.TSTModern;
import com.tstmodern.registry.TSTItems;
import com.tstmodern.registry.TSTMaterials;
import com.tstmodern.registry.TSTRecipeTypes;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/** Approved Infinity progression for the Galactic Armillary. */
public final class GalacticArmillaryRecipes {
    private static final String AVARITIA = "avaritia";

    private GalacticArmillaryRecipes() {}

    public static void register(Consumer<FinishedRecipe> provider) {
        registerDiamondLattice(provider);
        registerCrystalMatrix(provider);
        registerInfinityCatalyst(provider);
        registerInfinityIngot(provider);
    }

    private static void registerDiamondLattice(Consumer<FinishedRecipe> provider) {
        ItemStack output = preferred("diamond_lattice", TSTItems.DIAMOND_LATTICE, 1);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, output.getItem())
                .pattern("DED")
                .pattern("ENE")
                .pattern("DED")
                .define('D', Blocks.DIAMOND_BLOCK)
                .define('E', Blocks.EMERALD_BLOCK)
                .define('N', Items.NETHER_STAR)
                .unlockedBy("has_nether_star", net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance
                        .hasItems(Items.NETHER_STAR))
                .save(provider, TSTModern.id("crafting/diamond_lattice"));
    }

    private static void registerCrystalMatrix(Consumer<FinishedRecipe> provider) {
        GTRecipeTypes.BLAST_RECIPES.recipeBuilder(TSTModern.id("blast/crystal_matrix_ingot"))
                .inputItems(preferred("diamond_lattice", TSTItems.DIAMOND_LATTICE, 64))
                .inputFluids(GTMaterials.UUMatter.getFluid(16_000))
                .outputItems(preferred("crystal_matrix_ingot", TSTItems.CRYSTAL_MATRIX_INGOT, 16))
                .blastFurnaceTemp(9_001)
                .duration(400)
                .EUt(VA[ZPM])
                .save(provider);
    }

    private static void registerInfinityCatalyst(Consumer<FinishedRecipe> provider) {
        TSTRecipeTypes.GALACTIC_ARMILLARY.recipeBuilder(
                        TSTModern.id("galactic_armillary/infinity_catalyst_nugget"))
                .circuitMeta(1)
                .outputItems(new ItemStack(TSTItems.INFINITY_CATALYST_NUGGET.get()))
                .duration(100)
                .EUt(VA[UEV])
                .save(provider);

        ItemStack output = preferred("infinity_catalyst", TSTItems.INFINITY_CATALYST, 1);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, output.getItem())
                .pattern("NNN")
                .pattern("NNN")
                .pattern("NNN")
                .define('N', TSTItems.INFINITY_CATALYST_NUGGET.get())
                .unlockedBy("has_infinity_catalyst_nugget",
                        net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance
                                .hasItems(TSTItems.INFINITY_CATALYST_NUGGET.get()))
                .save(provider, TSTModern.id("crafting/infinity_catalyst"));
    }

    private static void registerInfinityIngot(Consumer<FinishedRecipe> provider) {
        TSTRecipeTypes.GALACTIC_ARMILLARY.recipeBuilder(TSTModern.id("galactic_armillary/infinity_ingot"))
                .inputItems(preferred("infinity_catalyst", TSTItems.INFINITY_CATALYST, 4))
                .inputItems(ChemicalHelper.get(block, GTMaterials.Neutronium, 16))
                .inputItems(preferred("crystal_matrix_ingot", TSTItems.CRYSTAL_MATRIX_INGOT, 32))
                .outputItems(preferredInfinityIngot())
                .duration(200)
                .EUt(VA[UEV])
                .save(provider);
    }

    @SuppressWarnings("deprecation")
    private static ItemStack preferred(String path, RegistryObject<Item> fallback, int amount) {
        if (ModList.get().isLoaded(AVARITIA)) {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(AVARITIA, path));
            if (item != null && item != Items.AIR) return new ItemStack(item, amount);
        }
        return new ItemStack(fallback.get(), amount);
    }

    @SuppressWarnings("deprecation")
    private static ItemStack preferredInfinityIngot() {
        if (ModList.get().isLoaded(AVARITIA)) {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(AVARITIA, "infinity_ingot"));
            if (item != null && item != Items.AIR) return new ItemStack(item);
        }
        return ChemicalHelper.get(ingot, TSTMaterials.INFINITY);
    }
}
