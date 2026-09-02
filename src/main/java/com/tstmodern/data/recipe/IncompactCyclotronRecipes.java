package com.tstmodern.data.recipe;

import static com.gregtechceu.gtceu.api.GTValues.LuV;
import static com.gregtechceu.gtceu.api.GTValues.EV;
import static com.gregtechceu.gtceu.api.GTValues.MV;
import static com.gregtechceu.gtceu.api.GTValues.UEV;
import static com.gregtechceu.gtceu.api.GTValues.UHV;
import static com.gregtechceu.gtceu.api.GTValues.UV;
import static com.gregtechceu.gtceu.api.GTValues.ZPM;
import static com.gregtechceu.gtceu.api.GTValues.VA;
import static com.gregtechceu.gtceu.api.GTValues.V;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.frameGt;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.dust;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.gear;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.rodLong;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.plateDense;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.screw;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.wireGtSingle;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ASSEMBLY_LINE_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.EXTRACTOR_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.PLASMA_GENERATOR_FUELS;

import java.util.function.Consumer;

import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKeys;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.tstmodern.registry.TSTBlocks;
import com.tstmodern.registry.TSTItems;
import com.tstmodern.registry.TSTMaterials;
import com.tstmodern.registry.TSTRecipeTypes;
import com.tstmodern.registry.machine.IncompactCyclotronDefinition;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;

public final class IncompactCyclotronRecipes {
    private IncompactCyclotronRecipes() {}

    public static void register(Consumer<FinishedRecipe> provider) {
        TSTRecipeTypes.CYCLOTRON_RECIPES.recipeBuilder("cyclotron_hydrogen_ions")
                .circuitMeta(24)
                .inputFluids(GTMaterials.Hydrogen.getFluid(1_000))
                .chancedOutput(new ItemStack(TSTItems.HYDROGEN_ION.get()), 500, 0)
                .chancedOutput(new ItemStack(TSTItems.HYDROGEN_ION.get()), 500, 0)
                .chancedOutput(new ItemStack(TSTItems.HYDROGEN_ION.get()), 500, 0)
                .chancedOutput(new ItemStack(TSTItems.HYDROGEN_ION.get()), 500, 0)
                .chancedOutput(new ItemStack(TSTItems.HYDROGEN_ION.get()), 500, 0)
                .chancedOutput(new ItemStack(TSTItems.HYDROGEN_ION.get()), 500, 0)
                .chancedOutput(new ItemStack(TSTItems.HYDROGEN_ION.get()), 500, 0)
                .chancedOutput(new ItemStack(TSTItems.HYDROGEN_ION.get()), 500, 0)
                .chancedOutput(new ItemStack(TSTItems.HYDROGEN_ION.get()), 500, 0)
                .duration(400)
                .EUt(VA[LuV])
                .save(provider);

        TSTRecipeTypes.CYCLOTRON_RECIPES.recipeBuilder("cyclotron_hydrogen_plasma")
                .inputItems(new ItemStack(TSTItems.HYDROGEN_ION.get()))
                .circuitMeta(21)
                .inputFluids(GTMaterials.Hydrogen.getFluid(1_000))
                .chancedOutput(new ItemStack(TSTItems.PROTON.get()), 1_250, 0)
                .chancedOutput(new ItemStack(TSTItems.NEUTRON.get()), 1_250, 0)
                .chancedOutput(new ItemStack(TSTItems.ELECTRON.get()), 1_250, 0)
                .chancedOutput(new ItemStack(TSTItems.UNKNOWN_PARTICLE.get()), 750, 0)
                .chancedOutput(new ItemStack(TSTItems.UNKNOWN_PARTICLE.get()), 750, 0)
                .chancedOutput(new ItemStack(TSTItems.UNKNOWN_PARTICLE.get()), 750, 0)
                .outputFluids(GTMaterials.Hydrogen.getFluid(FluidStorageKeys.PLASMA, 100))
                .duration(2_400)
                .EUt(VA[LuV])
                .save(provider);

        TSTRecipeTypes.CYCLOTRON_RECIPES.recipeBuilder("cyclotron_protons")
                .inputItems(new ItemStack(TSTItems.HYDROGEN_ION.get()))
                .circuitMeta(20)
                .inputFluids(GTMaterials.Hydrogen.getFluid(100))
                .chancedOutput(new ItemStack(TSTItems.PROTON.get()), 750, 0)
                .chancedOutput(new ItemStack(TSTItems.PROTON.get()), 750, 0)
                .chancedOutput(new ItemStack(TSTItems.PROTON.get()), 750, 0)
                .chancedOutput(new ItemStack(TSTItems.PROTON.get()), 750, 0)
                .chancedOutput(new ItemStack(TSTItems.PROTON.get()), 750, 0)
                .chancedOutput(new ItemStack(TSTItems.PROTON.get()), 750, 0)
                .chancedOutput(new ItemStack(TSTItems.PROTON.get()), 750, 0)
                .chancedOutput(new ItemStack(TSTItems.PROTON.get()), 750, 0)
                .chancedOutput(new ItemStack(TSTItems.PROTON.get()), 750, 0)
                .duration(400)
                .EUt(VA[LuV])
                .save(provider);

        TSTRecipeTypes.CYCLOTRON_RECIPES.recipeBuilder("cyclotron_particle_collider")
                .inputItems(new ItemStack(TSTItems.PROTON.get()))
                .inputItems(new ItemStack(TSTItems.ELECTRON.get()))
                .inputFluids(GTMaterials.Hydrogen.getFluid(1_000))
                .chancedOutput(new ItemStack(TSTItems.NEUTRON.get()), 1_000, 0)
                .chancedOutput(new ItemStack(TSTItems.UNKNOWN_PARTICLE.get()), 1_000, 0)
                .outputFluids(GTMaterials.Hydrogen.getFluid(FluidStorageKeys.PLASMA, 250))
                .duration(300)
                .EUt(VA[UV])
                .save(provider);

        TSTRecipeTypes.CYCLOTRON_RECIPES.recipeBuilder("cyclotron_unknown_to_protons")
                .inputItems(new ItemStack(TSTItems.UNKNOWN_PARTICLE.get()))
                .circuitMeta(22)
                .inputFluids(GTMaterials.Hydrogen.getFluid(100))
                .chancedOutput(new ItemStack(TSTItems.PROTON.get()), 375, 0)
                .chancedOutput(new ItemStack(TSTItems.PROTON.get()), 375, 0)
                .chancedOutput(new ItemStack(TSTItems.PROTON.get()), 375, 0)
                .chancedOutput(new ItemStack(TSTItems.PROTON.get()), 375, 0)
                .chancedOutput(new ItemStack(TSTItems.PROTON.get()), 375, 0)
                .chancedOutput(new ItemStack(TSTItems.PROTON.get()), 375, 0)
                .chancedOutput(new ItemStack(TSTItems.PROTON.get()), 375, 0)
                .chancedOutput(new ItemStack(TSTItems.PROTON.get()), 375, 0)
                .chancedOutput(new ItemStack(TSTItems.PROTON.get()), 375, 0)
                .duration(400)
                .EUt(VA[LuV])
                .save(provider);

        TSTRecipeTypes.CYCLOTRON_RECIPES.recipeBuilder("cyclotron_quantum_anomaly")
                .inputItems(new ItemStack(TSTItems.UNKNOWN_PARTICLE.get()))
                .circuitMeta(24)
                .inputFluids(GTMaterials.Duranium.getFluid(40))
                .chancedOutput(new ItemStack(TSTItems.SPECIAL_LASER_LENS.get()), 100, 0)
                .duration(500)
                .EUt(VA[UV])
                .save(provider);

        TSTRecipeTypes.CYCLOTRON_RECIPES.recipeBuilder("cyclotron_neptunium_238")
                .inputItems(dust, GTMaterials.Uranium238)
                .inputFluids(GTMaterials.Deuterium.getFluid(400))
                .chancedOutput(ChemicalHelper.get(dust, TSTMaterials.NEPTUNIUM_238), 500, 0)
                .duration(100)
                .EUt(VA[EV])
                .save(provider);

        TSTRecipeTypes.CYCLOTRON_RECIPES.recipeBuilder("cyclotron_strange_dust")
                .inputItems(dust, TSTMaterials.PLUTONIUM_238)
                .inputItems(new ItemStack(TSTItems.UNKNOWN_PARTICLE.get(), 8))
                .inputFluids(GTMaterials.EnderPearl.getFluid(1_000))
                .chancedOutput(new ItemStack(TSTItems.STRANGE_DUST.get()), 2_500, 0)
                .duration(18_000)
                .EUt(VA[ZPM])
                .save(provider);

        EXTRACTOR_RECIPES.recipeBuilder("ender_pearl_fluid")
                .inputItems(Items.ENDER_PEARL)
                .outputFluids(GTMaterials.EnderPearl.getFluid(250))
                .duration(100)
                .EUt(VA[MV])
                .save(provider);

        PLASMA_GENERATOR_FUELS.recipeBuilder("hydrogen_plasma_fuel")
                .inputFluids(GTMaterials.Hydrogen.getFluid(FluidStorageKeys.PLASMA, 1))
                .outputFluids(GTMaterials.Hydrogen.getFluid(1))
                .duration(32)
                .EUt(-V[EV])
                .save(provider);

        ASSEMBLY_LINE_RECIPES.recipeBuilder("quantum_frame")
                .inputItems(frameGt, GTMaterials.Neutronium)
                .inputItems(GTBlocks.MACHINE_CASING_UEV.asStack())
                .inputItems(GTItems.ENERGY_MODULE.asStack(4))
                .inputFluids(GTMaterials.Tritanium.getFluid(576))
                .outputItems(new ItemStack(TSTBlocks.QUANTUM_FRAME.get(), 4))
                .duration(600)
                .EUt(2_000_000)
                .stationResearch(b -> b
                        .researchStack(ChemicalHelper.get(frameGt, GTMaterials.Neutronium))
                        .researchId("quantum_frame")
                        .dataStack(GTItems.TOOL_DATA_ORB.asStack())
                        .CWUt(32, 144_000)
                        .EUt(VA[UHV]))
                .save(provider);

        ASSEMBLY_LINE_RECIPES.recipeBuilder("dense_cyclotron_outer_casing")
                .inputItems(GTBlocks.FUSION_CASING_MK2.asStack(4))
                .inputItems(new ItemStack(TSTBlocks.VACUUM_CASING.get(), 4))
                .inputItems(wireGtSingle, GTMaterials.RutheniumTriniumAmericiumNeutronate, 16)
                .inputItems(new ItemStack(TSTBlocks.ADVANCED_RADIATION_PROOF_CASING.get(), 6))
                .inputItems(rodLong, GTMaterials.Neutronium, 12)
                .inputItems(screw, GTMaterials.Tritanium, 24)
                .inputItems(GTItems.ELECTRIC_PISTON_UV.asStack(6))
                .inputFluids(GTMaterials.Titanium.getFluid(1_440))
                .inputFluids(GTMaterials.NaquadahAlloy.getFluid(576))
                .outputItems(new ItemStack(TSTBlocks.DENSE_CYCLOTRON_OUTER_CASING.get()))
                .duration(600)
                .EUt(2_000_000)
                .stationResearch(b -> b
                        .researchStack(GTBlocks.FUSION_CASING_MK2.asStack())
                        .researchId("dense_cyclotron_outer_casing")
                        .dataStack(GTItems.TOOL_DATA_ORB.asStack())
                        .CWUt(32, 144_000)
                        .EUt(VA[UHV]))
                .save(provider);

        ASSEMBLY_LINE_RECIPES.recipeBuilder("compact_cyclotron_coil")
                .inputItems(GTBlocks.FUSION_CASING_MK3.asStack(16))
                .inputItems(GTBlocks.FUSION_COIL.asStack(4))
                .inputItems(GTItems.ENERGY_CLUSTER.asStack())
                .inputItems(GTItems.ENERGY_MODULE.asStack(2))
                .inputItems(GTItems.VOLTAGE_COIL_UV.asStack(64))
                .inputItems(CustomTags.UHV_CIRCUITS, 2)
                .inputItems(GTItems.FIELD_GENERATOR_UHV.asStack())
                .inputFluids(GTMaterials.UUMatter.getFluid(64_000))
                .inputFluids(GTMaterials.Duranium.getFluid(16_000))
                .inputFluids(GTMaterials.RutheniumTriniumAmericiumNeutronate.getFluid(1_152))
                .inputFluids(GTMaterials.NaquadahAlloy.getFluid(288))
                .outputItems(new ItemStack(TSTBlocks.COMPACT_CYCLOTRON_COIL.get()))
                .duration(1_200)
                .EUt(2_000_000)
                .stationResearch(b -> b
                        .researchStack(GTBlocks.FUSION_COIL.asStack())
                        .researchId("compact_cyclotron_coil")
                        .dataStack(GTItems.TOOL_DATA_ORB.asStack())
                        .CWUt(32, 288_000)
                        .EUt(VA[UHV]))
                .save(provider);

        ASSEMBLY_LINE_RECIPES.recipeBuilder("incompact_cyclotron")
                .inputItems(GTMachines.HULL[com.gregtechceu.gtceu.api.GTValues.UEV].asStack(64))
                .inputItems(GTItems.EMITTER_UEV.asStack(64))
                .inputItems(GTBlocks.FUSION_COIL.asStack(8))
                .inputItems(GTItems.QUANTUM_EYE.asStack(4))
                .inputItems(GTItems.FIELD_GENERATOR_UHV.asStack(16))
                .inputItems(GTItems.ENERGY_CLUSTER.asStack(32))
                .inputItems(CustomTags.UHV_CIRCUITS, 16)
                .inputItems(plateDense, GTMaterials.NaquadahAlloy, 16)
                .inputItems(gear, GTMaterials.NaquadahAlloy, 16)
                .inputItems(screw, GTMaterials.NaquadahAlloy, 64)
                .inputFluids(GTMaterials.NaquadahAlloy.getFluid(36_864))
                .inputFluids(GTMaterials.Helium.getFluid(FluidStorageKeys.LIQUID, 1_000_000))
                .inputFluids(GTMaterials.Tritanium.getFluid(288))
                .outputItems(IncompactCyclotronDefinition.MACHINE.asStack())
                .duration(18_000)
                .EUt(8_000_000)
                .stationResearch(b -> b
                        .researchStack(GTBlocks.FUSION_COIL.asStack())
                        .researchId("incompact_cyclotron")
                        .dataStack(GTItems.TOOL_DATA_MODULE.asStack())
                        .CWUt(64, 144_000)
                        .EUt(VA[UEV]))
                .save(provider);
    }
}
