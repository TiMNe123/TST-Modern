package com.tstmodern.data.recipe;

import static com.gregtechceu.gtceu.api.GTValues.UHV;
import static com.gregtechceu.gtceu.api.GTValues.VA;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.plate;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ASSEMBLY_LINE_RECIPES;

import java.util.function.Consumer;

import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.recipe.chance.logic.ChanceLogic;
import com.gregtechceu.gtceu.api.recipe.ingredient.SizedIngredient;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.tstmodern.TSTModern;
import com.tstmodern.registry.TSTBlocks;
import com.tstmodern.registry.TSTItems;
import com.tstmodern.registry.TSTRecipeTypes;
import com.tstmodern.registry.machine.DraconicCrucibleDefinition;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public final class DraconicCrucibleRecipes {
    private static final TagKey<Item> DRACONIUM_INGOTS =
            ItemTags.create(new ResourceLocation("forge", "ingots/draconium"));
    private static final TagKey<Item> PREFERRED_ORE = preferred("draconium_ore");
    private static final TagKey<Item> PREFERRED_DUST = preferred("draconium_dust");
    private static final TagKey<Item> PREFERRED_INGOT = preferred("draconium_ingot");
    private static final TagKey<Item> PREFERRED_CORE = preferred("draconium_core");
    private static final TagKey<Item> PREFERRED_HEART = preferred("dragon_heart");
    private static final TagKey<Item> PREFERRED_AWAKENED_NUGGET = preferred("awakened_draconium_nugget");
    private static final TagKey<Item> PREFERRED_AWAKENED_INGOT = preferred("awakened_draconium_ingot");

    private DraconicCrucibleRecipes() {}

    public static void register(Consumer<FinishedRecipe> provider) {
        registerProcessing(provider);
        registerFallbackCore(provider);
        registerCore(provider);
        registerController(provider);
    }

    private static void registerProcessing(Consumer<FinishedRecipe> provider) {
        var ore = TSTRecipeTypes.DRACONIC_CRUCIBLE
                .recipeBuilder(TSTModern.id("draconic_crucible/draconium_ore"))
                .inputItems(PREFERRED_ORE, 64)
                .output(ItemRecipeCapability.CAP, SizedIngredient.create(PREFERRED_DUST, 64));
        chanceOutput(ore, PREFERRED_DUST, 16, 8_450);
        chanceOutput(ore, PREFERRED_ORE, 2, 1_000);
        chanceOutput(ore, PREFERRED_HEART, 1, 500);
        chanceOutput(ore, PREFERRED_AWAKENED_NUGGET, 1, 50);
        ore.chancedItemOutputLogic(ChanceLogic.XOR)
                .duration(400)
                .EUt(VA[UHV])
                .save(provider);

        TSTRecipeTypes.DRACONIC_CRUCIBLE
                .recipeBuilder(TSTModern.id("draconic_crucible/awakened_draconium"))
                .inputItems(PREFERRED_INGOT, 4)
                .inputItems(PREFERRED_CORE, 6)
                .inputItems(PREFERRED_HEART)
                .output(ItemRecipeCapability.CAP, SizedIngredient.create(PREFERRED_AWAKENED_INGOT, 4))
                .duration(1_000)
                .EUt(VA[UHV])
                .save(provider);
    }

    private static void chanceOutput(com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder builder,
                                     TagKey<Item> tag, int amount, int chance) {
        builder.chance(chance)
                .output(ItemRecipeCapability.CAP, SizedIngredient.create(tag, amount));
    }

    private static void registerFallbackCore(Consumer<FinishedRecipe> provider) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TSTItems.DRACONIUM_CORE.get())
                .pattern("DGD")
                .pattern("GXG")
                .pattern("DGD")
                .define('D', DRACONIUM_INGOTS)
                .define('G', Items.GOLD_INGOT)
                .define('X', Items.DIAMOND)
                .unlockedBy("has_diamond", net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance
                        .hasItems(Items.DIAMOND))
                .save(provider, TSTModern.id("crafting/draconium_core"));
    }

    private static void registerCore(Consumer<FinishedRecipe> provider) {
        ASSEMBLY_LINE_RECIPES.recipeBuilder(TSTModern.id("assembly_line/draconic_crucible_core"))
                .inputItems(new ItemStack(Blocks.DRAGON_EGG))
                .inputItems(new ItemStack(TSTBlocks.FIELD_RESTRICTION_CASING.get(), 8))
                .inputItems(new ItemStack(TSTBlocks.COMPACT_FUSION_COIL_T3.get(), 8))
                .inputItems(GTItems.FIELD_GENERATOR_UHV.asStack(4))
                .inputItems(ChemicalHelper.get(plate, GTMaterials.Neutronium, 16))
                .outputItems(new ItemStack(TSTBlocks.DRACONIC_CRUCIBLE_CORE.get()))
                .duration(1_000)
                .EUt(VA[UHV])
                .stationResearch(b -> b
                        .researchStack(new ItemStack(Blocks.DRAGON_EGG))
                        .researchId("draconic_crucible_core")
                        .dataStack(GTItems.TOOL_DATA_ORB.asStack())
                        .CWUt(64, 1_200_000)
                        .EUt(VA[UHV]))
                .save(provider);
    }

    private static void registerController(Consumer<FinishedRecipe> provider) {
        ASSEMBLY_LINE_RECIPES.recipeBuilder(TSTModern.id("assembly_line/draconic_crucible"))
                .inputItems(new ItemStack(TSTBlocks.FIELD_RESTRICTION_CASING.get(), 16))
                .inputItems(new ItemStack(TSTBlocks.COMPACT_FUSION_COIL_T3.get(), 8))
                .inputItems(new ItemStack(TSTBlocks.PARTICLE_BEAM_GUIDANCE_PIPE_CASING.get(), 8))
                .inputItems(GTItems.FIELD_GENERATOR_UHV.asStack(4))
                .inputItems(GTItems.EMITTER_UHV.asStack(4))
                .inputItems(CustomTags.UHV_CIRCUITS, 4)
                .outputItems(DraconicCrucibleDefinition.MACHINE)
                .duration(1_200)
                .EUt(VA[UHV])
                .stationResearch(b -> b
                        .researchStack(new ItemStack(TSTBlocks.DRACONIC_CRUCIBLE_CORE.get()))
                        .researchId("draconic_crucible")
                        .dataStack(GTItems.TOOL_DATA_MODULE.asStack())
                        .CWUt(64, 1_200_000)
                        .EUt(VA[UHV]))
                .save(provider);
    }

    private static TagKey<Item> preferred(String path) {
        return ItemTags.create(TSTModern.id("preferred/" + path));
    }
}
