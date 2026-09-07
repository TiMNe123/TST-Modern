package com.tstmodern.data.recipe;

import static com.gregtechceu.gtceu.api.GTValues.EV;
import static com.gregtechceu.gtceu.api.GTValues.UEV;
import static com.gregtechceu.gtceu.api.GTValues.UIV;
import static com.gregtechceu.gtceu.api.GTValues.VA;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.toolHeadDrill;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.wireGtHex;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ASSEMBLY_LINE_RECIPES;

import java.util.function.Consumer;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.machines.GTMultiMachines;
import com.tstmodern.TSTModern;
import com.tstmodern.registry.TSTBlocks;
import com.tstmodern.registry.TSTItems;
import com.tstmodern.registry.TSTMaterials;
import com.tstmodern.registry.machine.StarcoreMinerDefinition;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;

public final class StarcoreMinerRecipes {
    private StarcoreMinerRecipes() {}

    public static void register(Consumer<FinishedRecipe> provider) {
        registerDimensionalBridgeCasing(provider);
        registerController(provider);
    }

    private static void registerDimensionalBridgeCasing(Consumer<FinishedRecipe> provider) {
        ASSEMBLY_LINE_RECIPES.recipeBuilder(TSTModern.id("assembly_line/dimensional_bridge_casing"))
                .inputItems(new ItemStack(TSTBlocks.COMPONENT_ASSEMBLY_LINE_CASING_UIV.get(), 8))
                .inputItems(TSTCircuitTags.get(UIV), 8)
                .inputItems(GTItems.GRAVI_STAR.asStack(16))
                .inputFluids(GTMaterials.Naquadria.getFluid(9216))
                .outputItems(new ItemStack(TSTBlocks.DIMENSIONAL_BRIDGE_CASING.get(), 8))
                .duration(1200)
                .EUt(VA[UIV])
                .stationResearch(b -> b
                        .researchStack(new ItemStack(TSTBlocks.COMPONENT_ASSEMBLY_LINE_CASING_UIV.get()))
                        .researchId("dimensional_bridge_casing")
                        .dataStack(GTItems.TOOL_DATA_ORB.asStack())
                        .CWUt(128, 2290280)
                        .EUt(VA[UIV]))
                .save(provider);
    }

    private static void registerController(Consumer<FinishedRecipe> provider) {
        ASSEMBLY_LINE_RECIPES.recipeBuilder(TSTModern.id("assembly_line/starcore_miner"))
                .inputItems(new ItemStack(TSTBlocks.SPACE_ELEVATOR_BASE_CASING.get(), 64))
                .inputItems(GTMultiMachines.BEDROCK_ORE_MINER[EV].asStack(64))
                .inputItems(GTItems.ROBOT_ARM_UEV.asStack(18))
                .inputItems(GTItems.GRAVI_STAR.asStack(18))
                .inputItems(new ItemStack(TSTItems.ENERGISED_TESSERACT.get(), 64))
                .inputItems(GTItems.ELECTRIC_MOTOR_UEV.asStack(64))
                .inputItems(GTItems.FIELD_GENERATOR_UEV.asStack(48))
                .inputItems(GTItems.SENSOR_UEV.asStack(64))
                .inputItems(TSTCircuitTags.get(UIV), 64)
                .inputItems(TSTCircuitTags.get(UEV), 64)
                .inputItems(new ItemStack(TSTBlocks.HIGH_POWER_CASING.get(), 64))
                .inputItems(ChemicalHelper.get(wireGtHex, GTMaterials.RutheniumTriniumAmericiumNeutronate, 64))
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(147456))
                .inputFluids(GTMaterials.Naquadria.getFluid(147456))
                .inputFluids(GTMaterials.UUMatter.getFluid(2048000))
                .inputFluids(TSTMaterials.METASTABLE_OGANESSON.getFluid(73728))
                .outputItems(StarcoreMinerDefinition.MACHINE.asStack())
                .duration(144000)
                .EUt(VA[UIV])
                .stationResearch(b -> b
                        .researchStack(ChemicalHelper.get(toolHeadDrill, GTMaterials.Neutronium))
                        .researchId("starcore_miner")
                        .dataStack(GTItems.TOOL_DATA_MODULE.asStack())
                        .CWUt(128, 2290280)
                        .EUt(VA[UIV]))
                .save(provider);
    }
}
