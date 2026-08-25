package com.tstmodern.data.recipe;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.frameGt;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.plateDense;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.wireGtDouble;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ASSEMBLER_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ASSEMBLY_LINE_RECIPES;

import java.util.function.Consumer;

import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.tstmodern.registry.TSTBlocks;
import com.tstmodern.registry.machine.BigBroArrayDefinition;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;

/**
 * Assembly and Assembler recipes for the BigBroArray controller and Parallelism Casings MK1-MK5.
 */
public final class BigBroArrayRecipes {

    private BigBroArrayRecipes() {}

    public static void register(Consumer<FinishedRecipe> provider) {
        // Parallel Casing MK1 (HV-tier components)
        ASSEMBLER_RECIPES.recipeBuilder("parallel_casing_mk1")
                .inputItems(GTBlocks.CASING_STAINLESS_CLEAN.asStack())
                .inputItems(CustomTags.HV_CIRCUITS, 2)
                .inputItems(GTItems.ROBOT_ARM_HV.asStack(2))
                .inputItems(wireGtDouble, GTMaterials.Electrum, 4)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 2))
                .outputItems(new ItemStack(TSTBlocks.PARALLEL_CASING_MK1.get()))
                .duration(200).EUt(VA[HV])
                .save(provider);

        // Parallel Casing MK2 (EV-tier components)
        ASSEMBLER_RECIPES.recipeBuilder("parallel_casing_mk2")
                .inputItems(new ItemStack(TSTBlocks.PARALLEL_CASING_MK1.get()))
                .inputItems(CustomTags.EV_CIRCUITS, 2)
                .inputItems(GTItems.ROBOT_ARM_EV.asStack(2))
                .inputItems(wireGtDouble, GTMaterials.Aluminium, 4)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 4))
                .outputItems(new ItemStack(TSTBlocks.PARALLEL_CASING_MK2.get()))
                .duration(300).EUt(VA[EV])
                .save(provider);

        // Parallel Casing MK3 (IV-tier components)
        ASSEMBLER_RECIPES.recipeBuilder("parallel_casing_mk3")
                .inputItems(new ItemStack(TSTBlocks.PARALLEL_CASING_MK2.get()))
                .inputItems(CustomTags.IV_CIRCUITS, 2)
                .inputItems(GTItems.ROBOT_ARM_IV.asStack(2))
                .inputItems(wireGtDouble, GTMaterials.Platinum, 4)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 6))
                .outputItems(new ItemStack(TSTBlocks.PARALLEL_CASING_MK3.get()))
                .duration(400).EUt(VA[IV])
                .save(provider);

        // Parallel Casing MK4 (LuV-tier components)
        ASSEMBLER_RECIPES.recipeBuilder("parallel_casing_mk4")
                .inputItems(new ItemStack(TSTBlocks.PARALLEL_CASING_MK3.get()))
                .inputItems(CustomTags.LuV_CIRCUITS, 2)
                .inputItems(GTItems.ROBOT_ARM_LuV.asStack(2))
                .inputItems(wireGtDouble, GTMaterials.NiobiumTitanium, 4)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 8))
                .outputItems(new ItemStack(TSTBlocks.PARALLEL_CASING_MK4.get()))
                .duration(500).EUt(VA[LuV])
                .save(provider);

        // Parallel Casing MK5 (ZPM-tier components)
        ASSEMBLER_RECIPES.recipeBuilder("parallel_casing_mk5")
                .inputItems(new ItemStack(TSTBlocks.PARALLEL_CASING_MK4.get()))
                .inputItems(CustomTags.ZPM_CIRCUITS, 2)
                .inputItems(GTItems.ROBOT_ARM_ZPM.asStack(2))
                .inputItems(wireGtDouble, GTMaterials.VanadiumGallium, 4)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 10))
                .outputItems(new ItemStack(TSTBlocks.PARALLEL_CASING_MK5.get()))
                .duration(600).EUt(VA[ZPM])
                .save(provider);

        // BigBroArray Controller Assembly Line Recipe (UV-tier)
        ASSEMBLY_LINE_RECIPES.recipeBuilder("big_bro_array")
                .inputItems(GTBlocks.CASING_TUNGSTENSTEEL_ROBUST.asStack(16))
                .inputItems(CustomTags.UV_CIRCUITS, 4)
                .inputItems(GTItems.ROBOT_ARM_UV.asStack(4))
                .inputItems(GTItems.CONVEYOR_MODULE_UV.asStack(4))
                .inputItems(GTItems.ELECTRIC_PUMP_UV.asStack(4))
                .inputItems(GTItems.FIELD_GENERATOR_UV.asStack(2))
                .inputItems(GTItems.EMITTER_UV.asStack(4))
                .inputItems(GTItems.SENSOR_UV.asStack(4))
                .inputItems(frameGt, GTMaterials.Tritanium, 4)
                .inputItems(plateDense, GTMaterials.TungstenSteel, 8)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 32))
                .inputFluids(GTMaterials.Lubricant.getFluid(4000))
                .outputItems(BigBroArrayDefinition.MACHINE.asStack())
                .duration(1200)
                .EUt(VA[UV])
                .stationResearch(b -> b
                        .researchStack(GTMachines.HULL[UV].asStack())
                        .CWUt(64, 4800)
                        .EUt(VA[UV]))
                .save(provider);
    }
}
