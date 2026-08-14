package com.tstmodern.data;

import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;
import com.tstmodern.TSTModern;
import com.tstmodern.registry.TSTBlocks;
import com.tstmodern.registry.TSTMachines;
import com.tstmodern.registry.TSTRecipeTypes;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.gregtechceu.gtceu.api.GTValues.IV;
import static com.gregtechceu.gtceu.api.GTValues.LuV;
import static com.gregtechceu.gtceu.api.GTValues.VA;
import static com.gregtechceu.gtceu.api.GTValues.ZPM;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.frameGt;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.pipeLargeFluid;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.plate;
import static com.gregtechceu.gtceu.common.data.GTItems.ELECTRIC_PUMP_ZPM;
import static com.gregtechceu.gtceu.common.data.GTItems.ROBOT_ARM_ZPM;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Iridium;
import static com.gregtechceu.gtceu.common.data.GTMaterials.NaquadahAlloy;
import static com.gregtechceu.gtceu.common.data.GTMaterials.SolderingAlloy;
import static com.gregtechceu.gtceu.common.data.GTMaterials.UraniumRhodiumDinaquadide;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ASSEMBLER_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ASSEMBLY_LINE_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.COMPRESSOR_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTBlocks.CASING_TUNGSTENSTEEL_ROBUST;
import static com.gregtechceu.gtceu.common.data.GTMachines.HULL;
import static com.gregtechceu.gtceu.common.data.GTMachines.ROCK_CRUSHER;

/** Exact processing recipes from TST's MegaStoneBreakerRecipePool (1.7.10). */
public final class TSTRecipes {
    private TSTRecipes() {}

    public static void addMegaStoneBreakerRecipes(Consumer<FinishedRecipe> provider) {
        basic(provider, "cobblestone", 1, Blocks.COBBLESTONE, 6, 20);
        basic(provider, "compressed_cobblestone_1", 2, TSTBlocks.COMPRESSED_COBBLESTONE_1, 24, 40);
        basic(provider, "compressed_cobblestone_2", 3, TSTBlocks.COMPRESSED_COBBLESTONE_2, 96, 60);
        basic(provider, "compressed_cobblestone_3", 4, TSTBlocks.COMPRESSED_COBBLESTONE_3, 384, 80);
        basic(provider, "compressed_cobblestone_4", 5, TSTBlocks.COMPRESSED_COBBLESTONE_4, 1_536, 100);
        basic(provider, "compressed_cobblestone_5", 6, TSTBlocks.COMPRESSED_COBBLESTONE_5, 6_144, 120);
        basic(provider, "compressed_cobblestone_6", 7, TSTBlocks.COMPRESSED_COBBLESTONE_6, 12_288, 140);
        basic(provider, "compressed_cobblestone_7", 8, TSTBlocks.COMPRESSED_COBBLESTONE_7, 24_576, 160);
        basic(provider, "compressed_cobblestone_8", 9, TSTBlocks.COMPRESSED_COBBLESTONE_8, 98_304, 180);
        basic(provider, "stone", 20, Blocks.STONE, 6, 20);

        recipe("obsidian").circuitMeta(24)
                .inputItems(net.minecraft.world.item.Items.REDSTONE)
                .outputItems(Blocks.OBSIDIAN.asItem())
                .EUt(6).duration(20).save(provider);

        recipe("netherrack").circuitMeta(21)
                .inputItems(net.minecraft.world.item.Items.GLOWSTONE_DUST)
                .outputItems(Blocks.NETHERRACK.asItem())
                .EUt(6).duration(20).save(provider);

        // TST used zero-sized Et Futurum stacks here; in GTCEu these are non-consumed catalysts.
        recipe("basalt").circuitMeta(22)
                .notConsumable(Blocks.BLUE_ICE.asItem())
                .notConsumable(Blocks.SOUL_SAND.asItem())
                .outputItems(Blocks.BASALT.asItem())
                .EUt(6).duration(20).save(provider);

        recipe("cobbled_deepslate").circuitMeta(23)
                .notConsumable(Blocks.MAGMA_BLOCK.asItem())
                .notConsumable(Blocks.SOUL_SAND.asItem())
                .outputItems(Blocks.COBBLED_DEEPSLATE.asItem())
                .EUt(6).duration(20).save(provider);
    }

    /**
     * Construction chain for the TST-only structure blocks and controller.
     *
     * <p>The processing behaviour remains faithful to TST, while construction is
     * rebalanced for the beginning of ZPM in a modern 1.20.1 modpack.</p>
     */
    public static void addConstructionRecipes(Consumer<FinishedRecipe> provider) {
        addCompressedCobblestoneRecipes(provider);

        // GT5U Advanced Iridium Casing (sBlockCasings8:7) has no native GTCEu block.
        // Iridium metal is established at IV, so keep this reusable casing
        // available from the same progression stage.
        ASSEMBLER_RECIPES.recipeBuilder(TSTModern.id("assembler/advanced_iridium_casing"))
                .inputItems(CASING_TUNGSTENSTEEL_ROBUST.asStack())
                .inputItems(plate, Iridium, 6)
                .inputFluids(SolderingAlloy.getFluid(144))
                .outputItems(TSTBlocks.CASING_C)
                .duration(400)
                .EUt(VA[IV])
                .save(provider);

        // Cosmic Neutronium is absent from GTCEu. Keep the dedicated TST frame block,
        // but synthesize it from native ZPM materials so the 129-block structure does
        // not depend on UV/UHV progression.
        ASSEMBLER_RECIPES.recipeBuilder(TSTModern.id("assembler/cosmic_neutronium_frame"))
                .inputItems(frameGt, NaquadahAlloy)
                .inputItems(plate, NaquadahAlloy, 6)
                .inputFluids(SolderingAlloy.getFluid(576))
                .outputItems(TSTBlocks.COSMIC_NEUTRONIUM_FRAME, 2)
                .duration(400)
                .EUt(VA[ZPM])
                .save(provider);

        addControllerRecipe(provider);
    }

    private static void addCompressedCobblestoneRecipes(Consumer<FinishedRecipe> provider) {
        compressor("compressed_cobblestone_1", Blocks.COBBLESTONE,
                TSTBlocks.COMPRESSED_COBBLESTONE_1, provider);
        compressor("compressed_cobblestone_2", TSTBlocks.COMPRESSED_COBBLESTONE_1.get(),
                TSTBlocks.COMPRESSED_COBBLESTONE_2, provider);
        compressor("compressed_cobblestone_3", TSTBlocks.COMPRESSED_COBBLESTONE_2.get(),
                TSTBlocks.COMPRESSED_COBBLESTONE_3, provider);
        compressor("compressed_cobblestone_4", TSTBlocks.COMPRESSED_COBBLESTONE_3.get(),
                TSTBlocks.COMPRESSED_COBBLESTONE_4, provider);
        compressor("compressed_cobblestone_5", TSTBlocks.COMPRESSED_COBBLESTONE_4.get(),
                TSTBlocks.COMPRESSED_COBBLESTONE_5, provider);
        compressor("compressed_cobblestone_6", TSTBlocks.COMPRESSED_COBBLESTONE_5.get(),
                TSTBlocks.COMPRESSED_COBBLESTONE_6, provider);
        compressor("compressed_cobblestone_7", TSTBlocks.COMPRESSED_COBBLESTONE_6.get(),
                TSTBlocks.COMPRESSED_COBBLESTONE_7, provider);
        compressor("compressed_cobblestone_8", TSTBlocks.COMPRESSED_COBBLESTONE_7.get(),
                TSTBlocks.COMPRESSED_COBBLESTONE_8, provider);
    }

    private static void compressor(String name, ItemLike input, Supplier<? extends ItemLike> output,
                                   Consumer<FinishedRecipe> provider) {
        COMPRESSOR_RECIPES.recipeBuilder(TSTModern.id("compressor/" + name))
                .inputItems(input.asItem(), 9)
                .outputItems(output)
                .duration(300)
                .EUt(30)
                .save(provider);
    }

    private static void addControllerRecipe(Consumer<FinishedRecipe> provider) {
        ASSEMBLY_LINE_RECIPES.recipeBuilder(TSTModern.id("assembly_line/mega_stone_breaker"))
                .inputItems(HULL[ZPM], 2)
                .inputItems(ROCK_CRUSHER[ZPM], 4)
                .inputItems(ROBOT_ARM_ZPM, 16)
                .inputItems(ELECTRIC_PUMP_ZPM, 8)
                // Ultimet has no generated fluid-pipe item in GTCEu 7.4.0. Large
                // Iridium Fluid Pipe is the nearest real, progression-safe input.
                .inputItems(pipeLargeFluid, Iridium, 16)
                .inputItems(plate, NaquadahAlloy, 16)
                .inputItems(plate, UraniumRhodiumDinaquadide, 16)
                .inputItems(TSTBlocks.COMPRESSED_COBBLESTONE_4.get().asItem(), 4)
                .inputFluids(SolderingAlloy.getFluid(9_216))
                .inputFluids(new FluidStack(Fluids.LAVA, 64_000))
                .inputFluids(new FluidStack(Fluids.WATER, 64_000))
                .outputItems(TSTMachines.MEGA_STONE_BREAKER)
                .scannerResearch(b -> b
                        .researchStack(ROCK_CRUSHER[ZPM].asStack())
                        .duration(20 * 60)
                        .EUt(VA[LuV]))
                .duration(20 * 60)
                .EUt(VA[ZPM])
                .save(provider);
    }

    private static void basic(Consumer<FinishedRecipe> provider, String name, int circuit,
                              ItemLike output, long eut, int duration) {
        recipe(name).circuitMeta(circuit).outputItems(output.asItem()).EUt(eut).duration(duration).save(provider);
    }

    private static void basic(Consumer<FinishedRecipe> provider, String name, int circuit,
                              Supplier<? extends ItemLike> output, long eut, int duration) {
        recipe(name).circuitMeta(circuit).outputItems(output).EUt(eut).duration(duration).save(provider);
    }

    private static GTRecipeBuilder recipe(String name) {
        return TSTRecipeTypes.MEGA_STONE_BREAKER.recipeBuilder(TSTModern.id("mega_stone_breaker/" + name));
    }
}
