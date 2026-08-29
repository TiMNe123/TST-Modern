package com.tstmodern.data.recipe;

import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.plateDense;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ASSEMBLER_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ASSEMBLY_LINE_RECIPES;

import java.util.function.Consumer;

import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTItems;
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
        // BigBroArray Controller (IV Assembler, 24000 ticks, 6400 EU/t)
        ASSEMBLER_RECIPES.recipeBuilder("big_bro_array")
                .inputItems(GTItems.TOOL_DATA_ORB.asStack(16))
                .inputItems(GTItems.ROBOT_ARM_IV.asStack(8))
                .inputItems(GTItems.ELECTRIC_PISTON_IV.asStack(8))
                .inputItems(GTItems.ELECTRIC_MOTOR_IV.asStack(8))
                .inputItems(GTItems.ELECTRIC_PUMP_IV.asStack(8))
                .inputItems(GTItems.FIELD_GENERATOR_IV.asStack(4))
                .inputItems(GTItems.EMITTER_IV.asStack(4))
                .inputItems(GTItems.SENSOR_IV.asStack(4))
                .inputItems(com.gregtechceu.gtceu.api.data.tag.TagPrefix.wireGtHex, GTMaterials.SamariumIronArsenicOxide, 16)
                .inputFluids(GTMaterials.Titanium.getFluid(24576))
                .outputItems(BigBroArrayDefinition.MACHINE.asStack())
                .duration(24000)
                .EUt(6400)
                .save(provider);

        // Parallel Casing MK1 (IV Assembler, 3000 ticks, 6400 EU/t)
        ASSEMBLER_RECIPES.recipeBuilder("parallel_casing_mk1")
                .inputItems(GTBlocks.CASING_TUNGSTENSTEEL_ROBUST.asStack())
                .inputItems(GTItems.ROBOT_ARM_IV.asStack(2))
                .inputItems(GTItems.ELECTRIC_PISTON_IV.asStack(2))
                .inputItems(GTItems.ELECTRIC_MOTOR_IV.asStack(2))
                .inputItems(GTItems.ELECTRIC_PUMP_IV.asStack(2))
                .inputItems(GTItems.FIELD_GENERATOR_IV.asStack(1))
                .inputItems(GTItems.EMITTER_IV.asStack(1))
                .inputItems(GTItems.SENSOR_IV.asStack(1))
                .inputItems(CustomTags.IV_CIRCUITS, 4)
                .inputItems(com.gregtechceu.gtceu.api.data.tag.TagPrefix.wireGtHex, GTMaterials.SamariumIronArsenicOxide, 4)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(9216))
                .outputItems(new ItemStack(TSTBlocks.PARALLEL_CASING_MK1.get()))
                .duration(3000)
                .EUt(6400)
                .save(provider);

        // Parallel Casing MK2 (ZPM Assembly Line, 12000 ticks, 100000 EU/t)
        ASSEMBLY_LINE_RECIPES.recipeBuilder("parallel_casing_mk2")
                .inputItems(new ItemStack(TSTBlocks.PARALLEL_CASING_MK1.get()))
                .inputItems(GTItems.FIELD_GENERATOR_ZPM.asStack(4))
                .inputItems(GTItems.ENERGY_CLUSTER.asStack(4))
                .inputItems(GTItems.SENSOR_ZPM.asStack(4))
                .inputItems(GTItems.EMITTER_ZPM.asStack(4))
                .inputItems(GTItems.ELECTRIC_MOTOR_ZPM.asStack(4))
                .inputItems(GTItems.ROBOT_ARM_ZPM.asStack(4))
                .inputItems(GTItems.ELECTRIC_PISTON_ZPM.asStack(4))
                .inputItems(GTItems.ELECTRIC_PUMP_ZPM.asStack(4))
                .inputItems(CustomTags.ZPM_CIRCUITS, 4)
                .inputItems(plateDense, GTMaterials.Titanium, 8)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(5760))
                .inputFluids(GTMaterials.NaquadahAlloy.getFluid(5760))
                .outputItems(new ItemStack(TSTBlocks.PARALLEL_CASING_MK2.get()))
                .duration(12000)
                .EUt(100000)
                .scannerResearch(b -> b
                        .researchStack(new ItemStack(TSTBlocks.PARALLEL_CASING_MK1.get()))
                        .duration(288000)
                        .EUt(32))
                .save(provider);

        // Parallel Casing MK3 (UHV Assembly Line, 24000 ticks, 2000000 EU/t)
        ASSEMBLY_LINE_RECIPES.recipeBuilder("parallel_casing_mk3")
                .inputItems(new ItemStack(TSTBlocks.PARALLEL_CASING_MK2.get()))
                .inputItems(GTItems.FIELD_GENERATOR_UHV.asStack(4))
                .inputItems(GTItems.ENERGY_CLUSTER.asStack(4))
                .inputItems(GTItems.SENSOR_UHV.asStack(4))
                .inputItems(GTItems.EMITTER_UHV.asStack(4))
                .inputItems(GTItems.ELECTRIC_MOTOR_UHV.asStack(4))
                .inputItems(GTItems.ROBOT_ARM_UHV.asStack(4))
                .inputItems(GTItems.ELECTRIC_PISTON_UHV.asStack(4))
                .inputItems(GTItems.ELECTRIC_PUMP_UHV.asStack(4))
                .inputItems(CustomTags.UHV_CIRCUITS, 4)
                .inputItems(plateDense, GTMaterials.StainlessSteel, 8)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(5760))
                .inputFluids(GTMaterials.NaquadahAlloy.getFluid(5760))
                .inputFluids(GTMaterials.Europium.getFluid(5760))
                .outputItems(new ItemStack(TSTBlocks.PARALLEL_CASING_MK3.get()))
                .duration(24000)
                .EUt(2000000)
                .scannerResearch(b -> b
                        .researchStack(new ItemStack(TSTBlocks.PARALLEL_CASING_MK2.get()))
                        .duration(576000)
                        .EUt(32))
                .save(provider);

        // Parallel Casing MK4 (UIV Researchable Assembly Line, 24000 ticks, 8000000 EU/t)
        ASSEMBLY_LINE_RECIPES.recipeBuilder("parallel_casing_mk4")
                .inputItems(new ItemStack(TSTBlocks.PARALLEL_CASING_MK3.get(), 16))
                .inputItems(GTItems.FIELD_GENERATOR_UIV.asStack(8))
                .inputItems(GTItems.SENSOR_UIV.asStack(8))
                .inputItems(GTItems.EMITTER_UIV.asStack(8))
                .inputItems(GTItems.ELECTRIC_MOTOR_UIV.asStack(8))
                .inputItems(GTItems.ROBOT_ARM_UIV.asStack(8))
                .inputItems(GTItems.ELECTRIC_PISTON_UIV.asStack(8))
                .inputItems(GTItems.ELECTRIC_PUMP_UIV.asStack(8))
                .inputItems(CustomTags.UIV_CIRCUITS, 8)
                .inputItems(plateDense, GTMaterials.Tritanium, 16)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(11520))
                .inputFluids(GTMaterials.Neutronium.getFluid(11520))
                .outputItems(new ItemStack(TSTBlocks.PARALLEL_CASING_MK4.get()))
                .duration(24000)
                .EUt(8000000)
                .stationResearch(b -> b
                        .researchStack(new ItemStack(TSTBlocks.PARALLEL_CASING_MK3.get()))
                        .CWUt(64, 4800)
                        .EUt(8000000))
                .save(provider);

        // Parallel Casing MK5 (UXV Researchable Assembly Line, 24000 ticks, 32000000 EU/t)
        ASSEMBLY_LINE_RECIPES.recipeBuilder("parallel_casing_mk5")
                .inputItems(new ItemStack(TSTBlocks.PARALLEL_CASING_MK4.get(), 16))
                .inputItems(GTItems.FIELD_GENERATOR_UXV.asStack(16))
                .inputItems(GTItems.SENSOR_UXV.asStack(16))
                .inputItems(GTItems.EMITTER_UXV.asStack(16))
                .inputItems(GTItems.ELECTRIC_MOTOR_UXV.asStack(16))
                .inputItems(GTItems.ROBOT_ARM_UXV.asStack(16))
                .inputItems(GTItems.ELECTRIC_PISTON_UXV.asStack(16))
                .inputItems(GTItems.ELECTRIC_PUMP_UXV.asStack(16))
                .inputItems(CustomTags.UXV_CIRCUITS, 8)
                .inputItems(plateDense, GTMaterials.Tritanium, 16)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(23040))
                .inputFluids(GTMaterials.Tritanium.getFluid(23040))
                .outputItems(new ItemStack(TSTBlocks.PARALLEL_CASING_MK5.get()))
                .duration(24000)
                .EUt(32000000)
                .stationResearch(b -> b
                        .researchStack(new ItemStack(TSTBlocks.PARALLEL_CASING_MK4.get()))
                        .CWUt(128, 9600)
                        .EUt(32000000))
                .save(provider);
    }
}
