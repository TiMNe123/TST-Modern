package com.tstmodern.data.recipe;

import static com.gregtechceu.gtceu.api.GTValues.UEV;
import static com.gregtechceu.gtceu.api.GTValues.ZPM;
import static com.gregtechceu.gtceu.api.GTValues.VA;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.block;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.ingot;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.tstmodern.TSTModern;
import com.tstmodern.config.TSTConfig;
import com.tstmodern.registry.TSTItems;
import com.tstmodern.registry.TSTMaterials;
import com.tstmodern.registry.TSTRecipeTypes;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.fml.ModList;

/** Approved Infinity progression for the Galactic Armillary. */
public final class GalacticArmillaryRecipes {
    private static final Logger LOGGER = LoggerFactory.getLogger(GalacticArmillaryRecipes.class);
    private static final String AVARITIA = "avaritia";

    private GalacticArmillaryRecipes() {}

    public static void register(Consumer<FinishedRecipe> provider) {
        boolean useAvaritiaOriginal = ModList.get().isLoaded(AVARITIA)
                && TSTConfig.GALACTIC_ARMILLARY_RECIPE_MODE.get() == TSTConfig.RecipeMode.MOD_ORIGINAL;

        if (useAvaritiaOriginal) {
            LOGGER.info("Galactic Armillary machine recipes disabled in favor of original Avaritia progression");
            return;
        }

        registerCustomProgression(provider);
    }

    private static void registerCustomProgression(Consumer<FinishedRecipe> provider) {
        registerDiamondLatticeCustom(provider);
        registerCrystalMatrixCustom(provider);
        registerInfinityCatalystCustom(provider);
        registerInfinityIngotCustom(provider);
    }

    private static void registerDiamondLatticeCustom(Consumer<FinishedRecipe> provider) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TSTItems.DIAMOND_LATTICE.get())
                .pattern("DED")
                .pattern("ENE")
                .pattern("DED")
                .define('D', Blocks.DIAMOND_BLOCK)
                .define('E', Blocks.EMERALD_BLOCK)
                .define('N', Items.NETHER_STAR)
                .unlockedBy("has_nether_star", net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance
                        .hasItems(Items.NETHER_STAR))
                .save(provider, TSTModern.id("crafting/diamond_lattice_custom"));
        LOGGER.info("Registered Galactic Armillary recipe [CUSTOM]: crafting/diamond_lattice_custom");
    }

    private static void registerCrystalMatrixCustom(Consumer<FinishedRecipe> provider) {
        GTRecipeTypes.BLAST_RECIPES.recipeBuilder(TSTModern.id("blast/crystal_matrix_ingot_custom"))
                .inputItems(new ItemStack(TSTItems.DIAMOND_LATTICE.get(), 64))
                .inputFluids(GTMaterials.UUMatter.getFluid(16_000))
                .outputItems(new ItemStack(TSTItems.CRYSTAL_MATRIX_INGOT.get(), 16))
                .blastFurnaceTemp(9_001)
                .duration(400)
                .EUt(VA[ZPM])
                .save(provider);
        LOGGER.info("Registered Galactic Armillary recipe [CUSTOM]: blast/crystal_matrix_ingot_custom");
    }

    private static void registerInfinityCatalystCustom(Consumer<FinishedRecipe> provider) {
        TSTRecipeTypes.GALACTIC_ARMILLARY.recipeBuilder(
                        TSTModern.id("galactic_armillary/infinity_catalyst_nugget_custom"))
                .circuitMeta(1)
                .outputItems(new ItemStack(TSTItems.INFINITY_CATALYST_NUGGET.get()))
                .duration(100)
                .EUt(VA[UEV])
                .save(provider);
        LOGGER.info("Registered Galactic Armillary recipe [CUSTOM]: galactic_armillary/infinity_catalyst_nugget_custom");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TSTItems.INFINITY_CATALYST.get())
                .pattern("NNN")
                .pattern("NNN")
                .pattern("NNN")
                .define('N', TSTItems.INFINITY_CATALYST_NUGGET.get())
                .unlockedBy("has_infinity_catalyst_nugget",
                        net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance
                                .hasItems(TSTItems.INFINITY_CATALYST_NUGGET.get()))
                .save(provider, TSTModern.id("crafting/infinity_catalyst_custom"));
        LOGGER.info("Registered Galactic Armillary recipe [CUSTOM]: crafting/infinity_catalyst_custom");
    }

    private static void registerInfinityIngotCustom(Consumer<FinishedRecipe> provider) {
        TSTRecipeTypes.GALACTIC_ARMILLARY.recipeBuilder(TSTModern.id("galactic_armillary/infinity_ingot_custom"))
                .inputItems(new ItemStack(TSTItems.INFINITY_CATALYST.get(), 4))
                .inputItems(ChemicalHelper.get(block, GTMaterials.Neutronium, 16))
                .inputItems(new ItemStack(TSTItems.CRYSTAL_MATRIX_INGOT.get(), 32))
                .outputItems(ChemicalHelper.get(ingot, TSTMaterials.INFINITY))
                .duration(200)
                .EUt(VA[UEV])
                .save(provider);
        LOGGER.info("Registered Galactic Armillary recipe [CUSTOM]: galactic_armillary/infinity_ingot_custom");
    }
}
