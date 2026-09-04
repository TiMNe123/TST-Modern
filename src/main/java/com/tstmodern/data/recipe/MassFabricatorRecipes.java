package com.tstmodern.data.recipe;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.cableGtSingle;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.dust;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ASSEMBLER_RECIPES;

import java.util.function.Consumer;

import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.tstmodern.registry.TSTRecipeTypes;
import com.tstmodern.registry.machine.MassFabricatorDefinition;

import net.minecraft.data.recipes.FinishedRecipe;

/**
 * Recipes for the Mass Fabricator and its UU-Matter synthesis operations.
 */
public final class MassFabricatorRecipes {

    private MassFabricatorRecipes() {}

    public static void register(Consumer<FinishedRecipe> provider) {
        // --- 1. UU-Matter Production Recipes ---

        // Default: 1000 mB Neutronium -> 1 mB UU-Matter (10s at UHV)
        TSTRecipeTypes.MASS_FABRICATOR.recipeBuilder("uu_matter_from_neutronium")
                .inputFluids(GTMaterials.Neutronium.getFluid(1000))
                .outputFluids(GTMaterials.UUMatter.getFluid(1))
                .duration(200)
                .EUt(VA[UHV])
                .save(provider);

        // Amplified with Activated Carbon Dust: 1000 mB Neutronium + 16 Activated Carbon Dust -> 16 mB UU-Matter (8s at UHV)
        TSTRecipeTypes.MASS_FABRICATOR.recipeBuilder("uu_matter_from_neutronium_amplified")
                .inputItems(dust, GTMaterials.ActivatedCarbon, 16)
                .inputFluids(GTMaterials.Neutronium.getFluid(1000))
                .outputFluids(GTMaterials.UUMatter.getFluid(16))
                .duration(160)
                .EUt(VA[UHV])
                .save(provider);

        // --- 2. Machine Crafting Recipes (All Tiers: UHV -> MAX) ---

        // UHV Mass Fabricator
        if (MassFabricatorDefinition.MACHINES[UHV] != null) {
            ASSEMBLER_RECIPES.recipeBuilder("uhv_mass_fabricator")
                    .inputItems(GTMachines.HULL[UHV].asStack())
                    .inputItems(CustomTags.UHV_CIRCUITS, 4)
                    .inputItems(GTItems.FIELD_GENERATOR_UHV.asStack(2))
                    .inputItems(GTItems.ROBOT_ARM_UHV.asStack(2))
                    .inputItems(GTItems.ELECTRIC_PUMP_UHV.asStack(2))
                    .inputItems(cableGtSingle, GTMaterials.Europium, 4)
                    .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 8))
                    .outputItems(MassFabricatorDefinition.MACHINES[UHV].asStack())
                    .duration(600).EUt(VA[UHV])
                    .save(provider);
        }

        // UEV Mass Fabricator
        if (MassFabricatorDefinition.MACHINES[UEV] != null) {
            ASSEMBLER_RECIPES.recipeBuilder("uev_mass_fabricator")
                    .inputItems(GTMachines.HULL[UEV].asStack())
                    .inputItems(TSTCircuitTags.get(UEV), 8)
                    .inputItems(GTItems.FIELD_GENERATOR_UEV.asStack(2))
                    .inputItems(GTItems.ROBOT_ARM_UEV.asStack(2))
                    .inputItems(GTItems.ELECTRIC_PUMP_UEV.asStack(2))
                    .inputItems(cableGtSingle, GTMaterials.Europium, 8)
                    .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 16))
                    .outputItems(MassFabricatorDefinition.MACHINES[UEV].asStack())
                    .duration(800).EUt(VA[UEV])
                    .save(provider);
        }

        // UIV Mass Fabricator
        if (MassFabricatorDefinition.MACHINES[UIV] != null) {
            ASSEMBLER_RECIPES.recipeBuilder("uiv_mass_fabricator")
                    .inputItems(GTMachines.HULL[UIV].asStack())
                    .inputItems(TSTCircuitTags.get(UIV), 16)
                    .inputItems(GTItems.FIELD_GENERATOR_UIV.asStack(2))
                    .inputItems(GTItems.ROBOT_ARM_UIV.asStack(2))
                    .inputItems(GTItems.ELECTRIC_PUMP_UIV.asStack(2))
                    .inputItems(cableGtSingle, GTMaterials.Europium, 16)
                    .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 24))
                    .outputItems(MassFabricatorDefinition.MACHINES[UIV].asStack())
                    .duration(1000).EUt(VA[UIV])
                    .save(provider);
        }

        // UXV Mass Fabricator
        if (MassFabricatorDefinition.MACHINES[UXV] != null) {
            ASSEMBLER_RECIPES.recipeBuilder("uxv_mass_fabricator")
                    .inputItems(GTMachines.HULL[UXV].asStack())
                    .inputItems(TSTCircuitTags.get(UXV), 32)
                    .inputItems(GTItems.FIELD_GENERATOR_UXV.asStack(2))
                    .inputItems(GTItems.ROBOT_ARM_UXV.asStack(2))
                    .inputItems(GTItems.ELECTRIC_PUMP_UXV.asStack(2))
                    .inputItems(cableGtSingle, GTMaterials.Europium, 32)
                    .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 32))
                    .outputItems(MassFabricatorDefinition.MACHINES[UXV].asStack())
                    .duration(1200).EUt(VA[UXV])
                    .save(provider);
        }

        // OpV Mass Fabricator
        if (MassFabricatorDefinition.MACHINES[OpV] != null) {
            ASSEMBLER_RECIPES.recipeBuilder("opv_mass_fabricator")
                    .inputItems(GTMachines.HULL[OpV].asStack())
                    .inputItems(TSTCircuitTags.get(OpV), 64)
                    .inputItems(GTItems.FIELD_GENERATOR_OpV.asStack(2))
                    .inputItems(GTItems.ROBOT_ARM_OpV.asStack(2))
                    .inputItems(GTItems.ELECTRIC_PUMP_OpV.asStack(2))
                    .inputItems(cableGtSingle, GTMaterials.Europium, 48)
                    .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 40))
                    .outputItems(MassFabricatorDefinition.MACHINES[OpV].asStack())
                    .duration(1400).EUt(VA[OpV])
                    .save(provider);
        }

        // MAX Mass Fabricator
        if (MassFabricatorDefinition.MACHINES[MAX] != null) {
            ASSEMBLER_RECIPES.recipeBuilder("max_mass_fabricator")
                    .inputItems(GTMachines.HULL[MAX].asStack())
                    .inputItems(TSTCircuitTags.get(MAX), 64)
                    .inputItems(GTItems.FIELD_GENERATOR_OpV.asStack(4))
                    .inputItems(GTItems.ROBOT_ARM_OpV.asStack(4))
                    .inputItems(GTItems.ELECTRIC_PUMP_OpV.asStack(4))
                    .inputItems(cableGtSingle, GTMaterials.Europium, 64)
                    .inputFluids(GTMaterials.SolderingAlloy.getFluid(L * 48))
                    .outputItems(MassFabricatorDefinition.MACHINES[MAX].asStack())
                    .duration(1600).EUt(VA[MAX])
                    .save(provider);
        }
    }
}
