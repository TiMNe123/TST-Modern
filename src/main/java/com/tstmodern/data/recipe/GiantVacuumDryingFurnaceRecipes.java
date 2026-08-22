package com.tstmodern.data.recipe;

import static com.gregtechceu.gtceu.api.GTValues.EV;
import static com.gregtechceu.gtceu.api.GTValues.HV;
import static com.gregtechceu.gtceu.api.GTValues.IV;
import static com.gregtechceu.gtceu.api.GTValues.LV;
import static com.gregtechceu.gtceu.api.GTValues.LuV;
import static com.gregtechceu.gtceu.api.GTValues.MV;
import static com.gregtechceu.gtceu.api.GTValues.VA;
import static com.gregtechceu.gtceu.api.GTValues.ZPM;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.dust;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.ingot;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.pipeLargeFluid;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.plate;
import static com.gregtechceu.gtceu.common.data.GTBlocks.CASING_STAINLESS_CLEAN;
import static com.gregtechceu.gtceu.common.data.GTItems.CONVEYOR_MODULE_ZPM;
import static com.gregtechceu.gtceu.common.data.GTItems.ELECTRIC_PUMP_HV;
import static com.gregtechceu.gtceu.common.data.GTItems.ROBOT_ARM_ZPM;
import static com.gregtechceu.gtceu.common.data.GTMachines.ELECTRIC_FURNACE;
import static com.gregtechceu.gtceu.common.data.GTMachines.HULL;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Aluminium;
import static com.gregtechceu.gtceu.common.data.GTMaterials.AnnealedCopper;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Bauxite;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Calcium;
import static com.gregtechceu.gtceu.common.data.GTMaterials.CalciumChloride;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Cerium;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Chlorine;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Copper;
import static com.gregtechceu.gtceu.common.data.GTMaterials.HSSG;
import static com.gregtechceu.gtceu.common.data.GTMaterials.HSSE;
import static com.gregtechceu.gtceu.common.data.GTMaterials.HSSS;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Invar;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Iridium;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Lithium;
import static com.gregtechceu.gtceu.common.data.GTMaterials.LithiumChloride;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Magnesium;
import static com.gregtechceu.gtceu.common.data.GTMaterials.MagnesiumChloride;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Naquadah;
import static com.gregtechceu.gtceu.common.data.GTMaterials.NaquadahAlloy;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Neodymium;
import static com.gregtechceu.gtceu.common.data.GTMaterials.RareEarth;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Rutile;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Samarium;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Silicon;
import static com.gregtechceu.gtceu.common.data.GTMaterials.SolderingAlloy;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Titanium;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Trinium;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Tungsten;
import static com.gregtechceu.gtceu.common.data.GTMaterials.UraniumRhodiumDinaquadide;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Yttrium;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ASSEMBLER_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ASSEMBLY_LINE_RECIPES;

import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.tstmodern.TSTModern;
import com.tstmodern.registry.TSTBlocks;
import com.tstmodern.registry.TSTRecipeTypes;
import com.tstmodern.registry.machine.GiantVacuumDryingFurnaceDefinition;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Consumer;

public final class GiantVacuumDryingFurnaceRecipes {
    private GiantVacuumDryingFurnaceRecipes() {}

    public static void register(Consumer<FinishedRecipe> provider) {
        addGiantVacuumDryingFurnaceRecipes(provider);
        addVacuumCasingRecipe(provider);
        addGiantVacuumDryingFurnaceControllerRecipe(provider);
    }

    private static void addGiantVacuumDryingFurnaceRecipes(Consumer<FinishedRecipe> provider) {
        // === Chemical Dehydrator Recipes ===
        TSTRecipeTypes.CHEMICAL_DEHYDRATOR.recipeBuilder(TSTModern.id("chemical_dehydrator/calcium_chloride"))
                .inputItems(dust, CalciumChloride, 1)
                .outputItems(dust, Calcium, 1)
                .outputFluids(Chlorine.getFluid(2000))
                .EUt(VA[MV])
                .duration(200)
                .save(provider);

        TSTRecipeTypes.CHEMICAL_DEHYDRATOR.recipeBuilder(TSTModern.id("chemical_dehydrator/magnesium_chloride"))
                .inputItems(dust, MagnesiumChloride, 1)
                .outputItems(dust, Magnesium, 1)
                .outputFluids(Chlorine.getFluid(2000))
                .EUt(VA[MV])
                .duration(200)
                .save(provider);

        TSTRecipeTypes.CHEMICAL_DEHYDRATOR.recipeBuilder(TSTModern.id("chemical_dehydrator/lithium_chloride"))
                .inputItems(dust, LithiumChloride, 1)
                .outputItems(dust, Lithium, 1)
                .outputFluids(Chlorine.getFluid(1000))
                .EUt(VA[HV])
                .duration(200)
                .save(provider);

        TSTRecipeTypes.CHEMICAL_DEHYDRATOR
                .recipeBuilder(TSTModern.id("chemical_dehydrator/bauxite_dehydration"))
                .inputItems(dust, Bauxite, 4)
                .outputItems(dust, Aluminium, 2)
                .chancedOutput(dust, Rutile, 1, 2500, 0)
                .outputFluids(new FluidStack(Fluids.WATER, 2000))
                .EUt(VA[HV])
                .duration(240)
                .save(provider);

        TSTRecipeTypes.CHEMICAL_DEHYDRATOR.recipeBuilder(TSTModern.id("chemical_dehydrator/rare_earth"))
                .inputItems(dust, RareEarth, 1)
                .chancedOutput(dust, Neodymium, 1, 4000, 0)
                .chancedOutput(dust, Yttrium, 1, 3000, 0)
                .chancedOutput(dust, Samarium, 1, 2000, 0)
                .chancedOutput(dust, Cerium, 1, 1000, 0)
                .outputFluids(new FluidStack(Fluids.WATER, 1000))
                .EUt(VA[IV])
                .duration(300)
                .save(provider);

        TSTRecipeTypes.CHEMICAL_DEHYDRATOR.recipeBuilder(TSTModern.id("chemical_dehydrator/clay_drying"))
                .inputItems(Blocks.CLAY.asItem(), 1)
                .outputItems(Blocks.TERRACOTTA.asItem(), 1)
                .outputFluids(new FluidStack(Fluids.WATER, 1000))
                .EUt(VA[LV])
                .duration(100)
                .save(provider);

        TSTRecipeTypes.CHEMICAL_DEHYDRATOR.recipeBuilder(TSTModern.id("chemical_dehydrator/sponge_drying"))
                .inputItems(Blocks.WET_SPONGE.asItem(), 1)
                .outputItems(Blocks.SPONGE.asItem(), 1)
                .outputFluids(new FluidStack(Fluids.WATER, 1000))
                .EUt(VA[LV])
                .duration(40)
                .save(provider);

        // === Vacuum Furnace Recipes ===
        TSTRecipeTypes.VACUUM_FURNACE.recipeBuilder(TSTModern.id("vacuum_furnace/annealed_copper"))
                .inputItems(ingot, Copper, 1)
                .outputItems(ingot, AnnealedCopper, 1)
                .EUt(VA[MV])
                .duration(100)
                .save(provider);

        TSTRecipeTypes.VACUUM_FURNACE.recipeBuilder(TSTModern.id("vacuum_furnace/silicon_annealing"))
                .inputItems(dust, Silicon, 1)
                .outputItems(ingot, Silicon, 1)
                .EUt(VA[HV])
                .duration(160)
                .save(provider);

        TSTRecipeTypes.VACUUM_FURNACE.recipeBuilder(TSTModern.id("vacuum_furnace/titanium_sintering"))
                .inputItems(dust, Titanium, 1)
                .outputItems(ingot, Titanium, 1)
                .EUt(VA[HV])
                .duration(240)
                .save(provider);

        TSTRecipeTypes.VACUUM_FURNACE.recipeBuilder(TSTModern.id("vacuum_furnace/tungsten_sintering"))
                .inputItems(dust, Tungsten, 1)
                .outputItems(ingot, Tungsten, 1)
                .EUt(VA[EV])
                .duration(300)
                .save(provider);

        TSTRecipeTypes.VACUUM_FURNACE.recipeBuilder(TSTModern.id("vacuum_furnace/naquadah"))
                .inputItems(dust, Naquadah, 1)
                .outputItems(ingot, Naquadah, 1)
                .EUt(VA[IV])
                .duration(400)
                .save(provider);

        TSTRecipeTypes.VACUUM_FURNACE.recipeBuilder(TSTModern.id("vacuum_furnace/naquadah_alloy"))
                .inputItems(dust, NaquadahAlloy, 1)
                .outputItems(ingot, NaquadahAlloy, 1)
                .EUt(VA[LuV])
                .duration(500)
                .save(provider);

        TSTRecipeTypes.VACUUM_FURNACE.recipeBuilder(TSTModern.id("vacuum_furnace/trinium"))
                .inputItems(dust, Trinium, 1)
                .outputItems(ingot, Trinium, 1)
                .EUt(VA[LuV])
                .duration(450)
                .save(provider);

        TSTRecipeTypes.VACUUM_FURNACE.recipeBuilder(TSTModern.id("vacuum_furnace/hssg"))
                .inputItems(dust, HSSG, 1)
                .outputItems(ingot, HSSG, 1)
                .EUt(VA[IV])
                .duration(360)
                .save(provider);

        TSTRecipeTypes.VACUUM_FURNACE.recipeBuilder(TSTModern.id("vacuum_furnace/hsse"))
                .inputItems(dust, HSSE, 1)
                .outputItems(ingot, HSSE, 1)
                .EUt(VA[IV])
                .duration(400)
                .save(provider);

        TSTRecipeTypes.VACUUM_FURNACE.recipeBuilder(TSTModern.id("vacuum_furnace/hsss"))
                .inputItems(dust, HSSS, 1)
                .outputItems(ingot, HSSS, 1)
                .EUt(VA[LuV])
                .duration(440)
                .save(provider);
    }

    private static void addVacuumCasingRecipe(Consumer<FinishedRecipe> provider) {
        ASSEMBLER_RECIPES.recipeBuilder(TSTModern.id("assembler/vacuum_casing"))
                .inputItems(CASING_STAINLESS_CLEAN.asStack())
                .inputItems(plate, Invar, 6)
                .inputItems(ELECTRIC_PUMP_HV, 2)
                .inputFluids(SolderingAlloy.getFluid(288))
                .outputItems(TSTBlocks.VACUUM_CASING, 2)
                .duration(200)
                .EUt(VA[HV])
                .save(provider);
    }

    private static void addGiantVacuumDryingFurnaceControllerRecipe(Consumer<FinishedRecipe> provider) {
        ASSEMBLY_LINE_RECIPES.recipeBuilder(TSTModern.id("assembly_line/giant_vacuum_drying_furnace"))
                .inputItems(HULL[ZPM], 2)
                .inputItems(ELECTRIC_FURNACE[ZPM], 4)
                .inputItems(ROBOT_ARM_ZPM, 16)
                .inputItems(CONVEYOR_MODULE_ZPM, 16)
                .inputItems(CustomTags.ZPM_CIRCUITS, 32)
                .inputItems(pipeLargeFluid, Iridium, 16)
                .inputItems(plate, NaquadahAlloy, 16)
                .inputItems(plate, UraniumRhodiumDinaquadide, 16)
                .inputItems(TSTBlocks.VACUUM_CASING.get().asItem(), 4)
                .inputFluids(SolderingAlloy.getFluid(9_216))
                .inputFluids(Iridium.getFluid(4_608))
                .outputItems(GiantVacuumDryingFurnaceDefinition.MACHINE)
                .scannerResearch(b -> b
                        .researchStack(ELECTRIC_FURNACE[ZPM].asStack())
                        .duration(20 * 60)
                        .EUt(VA[LuV]))
                .duration(20 * 60)
                .EUt(VA[ZPM])
                .save(provider);
    }
}
