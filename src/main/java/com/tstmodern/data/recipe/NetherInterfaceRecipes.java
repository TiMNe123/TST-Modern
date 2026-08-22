package com.tstmodern.data.recipe;

import static com.gregtechceu.gtceu.api.GTValues.EV;
import static com.gregtechceu.gtceu.api.GTValues.IV;
import static com.gregtechceu.gtceu.api.GTValues.LuV;
import static com.gregtechceu.gtceu.api.GTValues.VA;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.dust;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.frameGt;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.ingot;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.plate;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.plateDense;
import static com.gregtechceu.gtceu.common.data.GTItems.FIELD_GENERATOR_LuV;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Blaze;
import static com.gregtechceu.gtceu.common.data.GTMaterials.DarkAsh;
import static com.gregtechceu.gtceu.common.data.GTMaterials.DistilledWater;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Netherite;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Netherrack;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Obsidian;
import static com.gregtechceu.gtceu.common.data.GTMaterials.TungstenSteel;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ASSEMBLER_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.BLAST_RECIPES;

import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.tstmodern.TSTModern;
import com.tstmodern.recipe.chance.TSTChanceLogics;
import com.tstmodern.registry.TSTBlocks;
import com.tstmodern.registry.TSTMaterials;
import com.tstmodern.registry.TSTRecipeTypes;
import com.tstmodern.registry.machine.NetherInterfaceDefinition;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Consumer;

public final class NetherInterfaceRecipes {
    private NetherInterfaceRecipes() {}

    public static void register(Consumer<FinishedRecipe> provider) {
        TSTRecipeTypes.NETHER_INTERFACE.recipeBuilder(TSTModern.id("nether_interface/dimensional_harvesting"))
                .inputFluids(DistilledWater.getFluid(16_000))
                .outputFluids(TSTMaterials.POOR_NETHER_WASTE.getFluid(16_000))
                .chancedOutput(new ItemStack(Items.ANCIENT_DEBRIS), 100, 0)
                .chancedOutput(new ItemStack(Blocks.NETHERRACK, 16), 4_900, 0)
                .chancedOutput(new ItemStack(Items.NETHERITE_SCRAP, 4), 3_000, 0)
                .chancedOutput(new ItemStack(Items.NETHERITE_INGOT), 1_000, 0)
                .chancedOutput(new ItemStack(Items.NETHER_STAR), 1_000, 0)
                .chancedOutput(TSTMaterials.HELLISH_METAL.getFluid(288), 3_000, 0)
                .chancedItemOutputLogic(TSTChanceLogics.THREE_WEIGHTED_SCALED)
                .chancedFluidOutputLogic(TSTChanceLogics.SINGLE_ROLL_SCALED)
                .EUt(VA[IV])
                .duration(1200)
                .save(provider);

        addHellishMetalBootstrappingRecipe(provider);

        ASSEMBLER_RECIPES.recipeBuilder(TSTModern.id("assembler/nether_interface"))
                .inputItems(frameGt, Obsidian, 16)
                .inputItems(FIELD_GENERATOR_LuV, 4)
                .inputItems(CustomTags.ZPM_CIRCUITS, 16)
                .inputItems(plateDense, Netherite, 16)
                .inputFluids(TSTMaterials.HELLISH_METAL.getFluid(9_216))
                .outputItems(NetherInterfaceDefinition.MACHINE)
                .duration(20 * 360)
                .EUt(VA[LuV])
                .save(provider);

        ASSEMBLER_RECIPES.recipeBuilder(TSTModern.id("assembler/mechanically_enhanced_obsidian"))
                .inputItems(Blocks.OBSIDIAN.asItem(), 1)
                .inputItems(plate, Netherite, 1)
                .inputItems(frameGt, TungstenSteel, 1)
                .inputFluids(TSTMaterials.HELLISH_METAL.getFluid(144))
                .outputItems(TSTBlocks.MECHANICALLY_ENHANCED_OBSIDIAN)
                .duration(200)
                .EUt(VA[LuV])
                .save(provider);
    }

    private static void addHellishMetalBootstrappingRecipe(Consumer<FinishedRecipe> provider) {
        // === Bootstrapping Hellish Metal before building Nether Interface ===
        // Blast Furnace: Synthesize Hellish Metal from Nether essence & Lava
        BLAST_RECIPES.recipeBuilder(TSTModern.id("blast/hellish_metal_synthesis"))
                .inputItems(dust, Netherrack, 4)
                .inputItems(new ItemStack(Items.NETHERITE_SCRAP, 1))
                .inputItems(dust, Blaze, 1)
                .inputFluids(new FluidStack(Fluids.LAVA, 1000))
                .outputItems(ingot, TSTMaterials.HELLISH_METAL, 1)
                .outputItems(dust, DarkAsh, 1)
                .blastFurnaceTemp(2800)
                .duration(400)
                .EUt(VA[EV])
                .save(provider);
    }
}
