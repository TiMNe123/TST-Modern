package com.tstmodern.data.recipe;

import static com.gregtechceu.gtceu.api.GTValues.EV;
import static com.gregtechceu.gtceu.api.GTValues.HV;
import static com.gregtechceu.gtceu.api.GTValues.IV;
import static com.gregtechceu.gtceu.api.GTValues.LV;
import static com.gregtechceu.gtceu.api.GTValues.LuV;
import static com.gregtechceu.gtceu.api.GTValues.MV;
import static com.gregtechceu.gtceu.api.GTValues.UHV;
import static com.gregtechceu.gtceu.api.GTValues.UIV;
import static com.gregtechceu.gtceu.api.GTValues.UV;
import static com.gregtechceu.gtceu.api.GTValues.VA;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.dust;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.dustTiny;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.frameGt;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.ingot;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.nugget;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.pipeHugeFluid;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.plate;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.plateDense;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.foil;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.screw;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.wireGtOctal;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ASSEMBLER_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ASSEMBLY_LINE_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.BLAST_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.CENTRIFUGE_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.CHEMICAL_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.COMPRESSOR_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.FUSION_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.IMPLOSION_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.LARGE_CHEMICAL_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.MIXER_RECIPES;

import java.util.function.Consumer;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKeys;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.tstmodern.TSTModern;
import com.tstmodern.registry.TSTBlocks;
import com.tstmodern.registry.TSTItems;
import com.tstmodern.registry.TSTMaterials;
import com.tstmodern.registry.machine.MegaNaquadahReactorDefinition;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;

public final class MegaNaquadahReactorRecipes {
    private MegaNaquadahReactorRecipes() {}

    public static void register(Consumer<FinishedRecipe> provider) {
        addFuelPreparation(provider);
        addDepletedFuelRecovery(provider);
        addReactorCore(provider);
        addCasings(provider);
        addController(provider);
    }

    private static void addFuelPreparation(Consumer<FinishedRecipe> provider) {
        CHEMICAL_RECIPES.recipeBuilder(TSTModern.id("chemical/thorium_nitrate"))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Thorium))
                .inputFluids(GTMaterials.HydrofluoricAcid.getFluid(100))
                .inputFluids(GTMaterials.NitricAcid.getFluid(8_000))
                .outputFluids(TSTMaterials.THORIUM_NITRATE.getFluid(1_000))
                .outputFluids(GTMaterials.NitrogenDioxide.getFluid(4_000))
                .duration(40).EUt(VA[MV]).save(provider);

        CHEMICAL_RECIPES.recipeBuilder(TSTModern.id("chemical/thorium_hydroxide"))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.SodiumHydroxide, 12)).circuitMeta(1)
                .inputFluids(TSTMaterials.THORIUM_NITRATE.getFluid(1_000))
                .outputItems(ChemicalHelper.get(dust, TSTMaterials.THORIUM_HYDROXIDE, 9))
                .outputItems(ChemicalHelper.get(dust, TSTMaterials.SODIUM_NITRATE, 20))
                .duration(200).EUt(VA[MV]).save(provider);

        CHEMICAL_RECIPES.recipeBuilder(TSTModern.id("chemical/sodium_nitrate_recovery"))
                .inputItems(ChemicalHelper.get(dust, TSTMaterials.SODIUM_NITRATE, 10)).circuitMeta(1)
                .inputFluids(GTMaterials.SulfuricAcid.getFluid(1_000))
                .outputItems(ChemicalHelper.get(dust, TSTMaterials.SODIUM_SULFATE, 7))
                .outputFluids(GTMaterials.NitricAcid.getFluid(2_000))
                .duration(200).EUt(VA[HV]).save(provider);

        CHEMICAL_RECIPES.recipeBuilder(TSTModern.id("chemical/thorium_tetrafluoride"))
                .inputItems(ChemicalHelper.get(dust, TSTMaterials.THORIUM_HYDROXIDE, 9)).circuitMeta(1)
                .inputFluids(GTMaterials.HydrofluoricAcid.getFluid(4_000))
                .outputFluids(TSTMaterials.THORIUM_TETRAFLUORIDE.getFluid(1_000))
                .duration(400).EUt(VA[LV]).save(provider);

        CENTRIFUGE_RECIPES.recipeBuilder(TSTModern.id("centrifuge/thorium_232_tetrafluoride"))
                .inputFluids(TSTMaterials.THORIUM_TETRAFLUORIDE.getFluid(4_000)).circuitMeta(1)
                .outputItems(ChemicalHelper.get(dust, GTMaterials.Thorium))
                .outputFluids(TSTMaterials.THORIUM_232_TETRAFLUORIDE.getFluid(3_000))
                .duration(400).EUt(VA[HV]).save(provider);

        CHEMICAL_RECIPES.recipeBuilder(TSTModern.id("chemical/zinc_chloride"))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Zinc)).circuitMeta(1)
                .inputFluids(GTMaterials.Chlorine.getFluid(2_000))
                .outputItems(ChemicalHelper.get(dust, TSTMaterials.ZINC_CHLORIDE, 3))
                .duration(100).EUt(VA[LV]).save(provider);

        BLAST_RECIPES.recipeBuilder(TSTModern.id("blast/zinc_thorium_alloy"))
                .inputItems(ChemicalHelper.get(dust, TSTMaterials.ZINC_CHLORIDE, 3))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Calcium, 3))
                .inputFluids(TSTMaterials.THORIUM_232_TETRAFLUORIDE.getFluid(1_000))
                .outputItems(ChemicalHelper.get(ingot, TSTMaterials.ZINC_THORIUM_ALLOY))
                .outputItems(ChemicalHelper.get(dust, TSTMaterials.FLUORITE, 6))
                .outputFluids(GTMaterials.CalciumChloride.getFluid(3_000))
                .blastFurnaceTemp(3_000).duration(300).EUt(VA[MV]).save(provider);

        BLAST_RECIPES.recipeBuilder(TSTModern.id("blast/thorium_232"))
                .inputItems(ChemicalHelper.get(ingot, TSTMaterials.ZINC_THORIUM_ALLOY)).circuitMeta(11)
                .outputItems(ChemicalHelper.get(dust, TSTMaterials.THORIUM_232))
                .outputFluids(GTMaterials.Zinc.getFluid(144))
                .blastFurnaceTemp(1_900).duration(150).EUt(VA[HV]).save(provider);

        MIXER_RECIPES.recipeBuilder(TSTModern.id("mixer/uranium_carbide_thorium_mixture"))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Thorium, 11))
                .inputItems(ChemicalHelper.get(dust, TSTMaterials.THORIUM_232))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Uranium235))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Carbon, 3))
                .outputItems(ChemicalHelper.get(dust, TSTMaterials.URANIUM_CARBIDE_THORIUM_MIXTURE, 16))
                .duration(200).EUt(VA[HV]).save(provider);

        ASSEMBLER_RECIPES.recipeBuilder(TSTModern.id("assembler/wrapped_thorium"))
                .inputItems(ChemicalHelper.get(dust, TSTMaterials.URANIUM_CARBIDE_THORIUM_MIXTURE, 64))
                .inputItems(ChemicalHelper.get(foil, GTMaterials.TungstenSteel, 4)).circuitMeta(1)
                .outputItems(ChemicalHelper.get(ingot, TSTMaterials.WRAPPED_THORIUM))
                .duration(300).EUt(VA[HV]).save(provider);

        IMPLOSION_RECIPES.recipeBuilder(TSTModern.id("implosion/high_density_thorium_nugget"))
                .inputItems(ChemicalHelper.get(ingot, TSTMaterials.WRAPPED_THORIUM))
                .outputItems(ChemicalHelper.get(nugget, TSTMaterials.HIGH_DENSITY_THORIUM))
                .outputItems(ChemicalHelper.get(dustTiny, GTMaterials.TungstenSteel, 8))
                .explosivesAmount(4).duration(20).EUt(VA[LV]).save(provider);

        COMPRESSOR_RECIPES.recipeBuilder(TSTModern.id("compressor/high_density_thorium"))
                .inputItems(ChemicalHelper.get(nugget, TSTMaterials.HIGH_DENSITY_THORIUM, 9))
                .outputItems(ChemicalHelper.get(ingot, TSTMaterials.HIGH_DENSITY_THORIUM))
                .duration(200).EUt(VA[MV]).save(provider);

        MIXER_RECIPES.recipeBuilder(TSTModern.id("mixer/thorium_based_liquid_fuel"))
                .inputItems(ChemicalHelper.get(ingot, TSTMaterials.HIGH_DENSITY_THORIUM))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Lithium, 4))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Duranium, 2)).circuitMeta(2)
                .inputFluids(GTMaterials.Mercury.getFluid(1_000))
                .outputFluids(TSTMaterials.THORIUM_BASED_LIQUID_FUEL.getFluid(4_000))
                .duration(3_000).EUt(VA[HV] / 2).save(provider);

        MIXER_RECIPES.recipeBuilder(TSTModern.id("mixer/thorium_based_liquid_fuel_excited"))
                .inputFluids(TSTMaterials.THORIUM_BASED_LIQUID_FUEL.getFluid(1_000))
                .inputFluids(GTMaterials.Helium.getFluid(FluidStorageKeys.PLASMA, 250))
                .outputFluids(TSTMaterials.THORIUM_BASED_LIQUID_FUEL_EXCITED.getFluid(1_000))
                .duration(120).EUt(VA[IV] / 2).save(provider);

        MIXER_RECIPES.recipeBuilder(TSTModern.id("mixer/uranium_based_liquid_fuel"))
                .inputItems(new ItemStack(TSTItems.HIGH_DENSITY_URANIUM.get()))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Potassium, 8))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Naquadria, 4)).circuitMeta(1)
                .inputFluids(GTMaterials.Radon.getFluid(1_000))
                .outputFluids(TSTMaterials.URANIUM_BASED_LIQUID_FUEL.getFluid(1_000))
                .duration(200).EUt(VA[LuV] / 2).save(provider);

        FUSION_RECIPES.recipeBuilder(TSTModern.id("fusion/uranium_based_liquid_fuel_excited"))
                .inputFluids(TSTMaterials.URANIUM_BASED_LIQUID_FUEL.getFluid(10))
                .inputFluids(GTMaterials.Hydrogen.getFluid(100))
                .outputFluids(TSTMaterials.URANIUM_BASED_LIQUID_FUEL_EXCITED.getFluid(10))
                .fusionStartEU(200_000_000L).duration(40).EUt(VA[IV]).save(provider);

        MIXER_RECIPES.recipeBuilder(TSTModern.id("mixer/plutonium_based_liquid_fuel"))
                .inputItems(new ItemStack(TSTItems.HIGH_DENSITY_PLUTONIUM.get()))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Neutronium, 8))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Caesium, 16))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Naquadah, 2)).circuitMeta(1)
                .outputFluids(TSTMaterials.PLUTONIUM_BASED_LIQUID_FUEL.getFluid(1_000))
                .duration(360).EUt(VA[LuV]).save(provider);

        FUSION_RECIPES.recipeBuilder(TSTModern.id("fusion/plutonium_based_liquid_fuel_excited"))
                .inputFluids(GTMaterials.Lutetium.getFluid(16))
                .inputFluids(TSTMaterials.PLUTONIUM_BASED_LIQUID_FUEL.getFluid(20))
                .outputFluids(TSTMaterials.PLUTONIUM_BASED_LIQUID_FUEL_EXCITED.getFluid(20))
                .fusionStartEU(220_000_000L).duration(20).EUt(VA[LuV] / 2).save(provider);

        FUSION_RECIPES.recipeBuilder(TSTModern.id("fusion/naquadah_based_fuel_mki"))
                .inputFluids(TSTMaterials.LIGHT_NAQUADAH_FUEL.getFluid(780))
                .inputFluids(TSTMaterials.HEAVY_NAQUADAH_FUEL.getFluid(360))
                .outputFluids(TSTMaterials.NAQUADAH_BASED_FUEL_MKI.getFluid(100))
                .fusionStartEU(320_000_000L).duration(500).EUt(VA[LuV]).save(provider);

        LARGE_CHEMICAL_RECIPES.recipeBuilder(TSTModern.id("large_chemical/naquadah_based_fuel_mkii"))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.NetherStar, 4))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Europium, 32)).circuitMeta(1)
                .inputFluids(TSTMaterials.NAQUADAH_BASED_FUEL_MKI.getFluid(100))
                .inputFluids(TSTMaterials.NAQUADAH_GAS.getFluid(1_500))
                .outputFluids(TSTMaterials.NAQUADAH_BASED_FUEL_MKII.getFluid(100))
                .duration(500).EUt(VA[UHV] / 2).save(provider);
    }

    private static void addDepletedFuelRecovery(Consumer<FinishedRecipe> provider) {
        CENTRIFUGE_RECIPES.recipeBuilder(TSTModern.id("centrifuge/depleted_naquadah_fuel_mkvi"))
                .inputFluids(TSTMaterials.NAQUADAH_BASED_FUEL_MKVI_DEPLETED.getFluid(1_000)).circuitMeta(1)
                .chancedOutput(ChemicalHelper.get(dust, GTMaterials.Naquadria), 1_000, 0)
                .chancedOutput(ChemicalHelper.get(dust, GTMaterials.Naquadria), 500, 0)
                .chancedOutput(ChemicalHelper.get(dust, GTMaterials.Naquadria), 400, 0)
                .chancedOutput(ChemicalHelper.get(dust, GTMaterials.Naquadria), 50, 0)
                .chancedOutput(ChemicalHelper.get(dust, GTMaterials.Naquadria), 20, 0)
                .chancedOutput(ChemicalHelper.get(dust, GTMaterials.Naquadria), 5, 0)
                .outputFluids(TSTMaterials.METASTABLE_OGANESSON.getFluid(122))
                .duration(250).EUt(VA[LuV]).save(provider);

        CENTRIFUGE_RECIPES.recipeBuilder(TSTModern.id("centrifuge/depleted_naquadah_fuel_mkv"))
                .inputFluids(TSTMaterials.NAQUADAH_BASED_FUEL_MKV_DEPLETED.getFluid(1_000)).circuitMeta(1)
                .chancedOutput(ChemicalHelper.get(dust, GTMaterials.Naquadah), 1_000, 0)
                .chancedOutput(ChemicalHelper.get(dust, GTMaterials.Naquadah), 500, 0)
                .chancedOutput(ChemicalHelper.get(dust, GTMaterials.Naquadah), 400, 0)
                .chancedOutput(ChemicalHelper.get(dust, GTMaterials.Naquadah), 50, 0)
                .chancedOutput(ChemicalHelper.get(dust, GTMaterials.Naquadah), 20, 0)
                .chancedOutput(ChemicalHelper.get(dust, GTMaterials.Naquadah), 5, 0)
                .outputFluids(GTMaterials.Oganesson.getFluid(182))
                .duration(250).EUt(VA[LuV]).save(provider);

        CENTRIFUGE_RECIPES.recipeBuilder(TSTModern.id("centrifuge/depleted_naquadah_fuel_mkiv"))
                .inputFluids(TSTMaterials.NAQUADAH_BASED_FUEL_MKIV_DEPLETED.getFluid(1_000)).circuitMeta(1)
                .chancedOutput(ChemicalHelper.get(dust, GTMaterials.Naquadah, 64), 9_900, 0)
                .chancedOutput(ChemicalHelper.get(dust, GTMaterials.Naquadah, 64), 9_500, 0)
                .chancedOutput(ChemicalHelper.get(dust, GTMaterials.Naquadah, 64), 9_000, 0)
                .chancedOutput(ChemicalHelper.get(dust, GTMaterials.Naquadah, 64), 8_000, 0)
                .chancedOutput(ChemicalHelper.get(dust, TSTMaterials.SUNNARIUM, 32), 5_000, 0)
                .chancedOutput(ChemicalHelper.get(dust, TSTMaterials.SUNNARIUM, 32), 3_000, 0)
                .outputFluids(GTMaterials.Oganesson.getFluid(864))
                .duration(2_500).EUt(VA[LuV]).save(provider);

        CENTRIFUGE_RECIPES.recipeBuilder(TSTModern.id("centrifuge/depleted_naquadah_fuel_mkiii"))
                .inputFluids(TSTMaterials.NAQUADAH_BASED_FUEL_MKIII_DEPLETED.getFluid(1_000)).circuitMeta(1)
                .chancedOutput(ChemicalHelper.get(dust, GTMaterials.Naquadah, 64), 9_500, 0)
                .chancedOutput(ChemicalHelper.get(dust, GTMaterials.Naquadah, 64), 9_000, 0)
                .chancedOutput(ChemicalHelper.get(dust, GTMaterials.Naquadah, 64), 8_000, 0)
                .chancedOutput(ChemicalHelper.get(dust, TSTMaterials.BEDROCKIUM, 32), 7_000, 0)
                .chancedOutput(ChemicalHelper.get(dust, TSTMaterials.BEDROCKIUM, 32), 5_000, 0)
                .chancedOutput(ChemicalHelper.get(dust, TSTMaterials.BEDROCKIUM, 32), 4_000, 0)
                .outputFluids(GTMaterials.Oganesson.getFluid(720))
                .duration(2_000).EUt(VA[LuV]).save(provider);

        CENTRIFUGE_RECIPES.recipeBuilder(TSTModern.id("centrifuge/depleted_naquadah_fuel_mkii"))
                .inputFluids(TSTMaterials.NAQUADAH_BASED_FUEL_MKII_DEPLETED.getFluid(1_000)).circuitMeta(1)
                .chancedOutput(ChemicalHelper.get(dust, GTMaterials.Naquadah, 64), 9_000, 0)
                .chancedOutput(ChemicalHelper.get(dust, GTMaterials.Naquadah, 64), 8_500, 0)
                .chancedOutput(ChemicalHelper.get(dust, GTMaterials.Naquadah, 32), 5_000, 0)
                .chancedOutput(ChemicalHelper.get(dust, GTMaterials.Americium, 32), 4_000, 0)
                .chancedOutput(ChemicalHelper.get(dust, GTMaterials.Californium, 32), 2_000, 0)
                .outputFluids(GTMaterials.Oganesson.getFluid(144))
                .duration(8_000).EUt(VA[EV]).save(provider);

        CENTRIFUGE_RECIPES.recipeBuilder(TSTModern.id("centrifuge/depleted_uranium_liquid_fuel"))
                .inputFluids(TSTMaterials.URANIUM_BASED_LIQUID_FUEL_DEPLETED.getFluid(1_000)).circuitMeta(1)
                .chancedOutput(ChemicalHelper.get(dust, GTMaterials.Lead, 16), 6_000, 0)
                .chancedOutput(ChemicalHelper.get(dust, GTMaterials.Bismuth), 1_000, 0)
                .chancedOutput(ChemicalHelper.get(dust, GTMaterials.Barium, 6), 5_000, 0)
                .outputFluids(GTMaterials.Xenon.getFluid(10))
                .duration(1_000).EUt(VA[EV] / 2).save(provider);

        CENTRIFUGE_RECIPES.recipeBuilder(TSTModern.id("centrifuge/depleted_thorium_liquid_fuel"))
                .inputFluids(TSTMaterials.THORIUM_BASED_LIQUID_FUEL_DEPLETED.getFluid(1_000)).circuitMeta(1)
                .chancedOutput(ChemicalHelper.get(dust, TSTMaterials.THORIUM_232, 64), 10_000, 0)
                .chancedOutput(ChemicalHelper.get(dust, TSTMaterials.THORIUM_232, 16), 8_000, 0)
                .chancedOutput(ChemicalHelper.get(dust, GTMaterials.Praseodymium, 64), 10_000, 0)
                .chancedOutput(ChemicalHelper.get(dust, GTMaterials.Praseodymium, 32), 8_000, 0)
                .chancedOutput(ChemicalHelper.get(dust, GTMaterials.Boron, 2), 3_000, 0)
                .chancedOutput(ChemicalHelper.get(dust, GTMaterials.Indium, 4), 5_000, 0)
                .duration(1_500).EUt(VA[EV] / 2).save(provider);

        CENTRIFUGE_RECIPES.recipeBuilder(TSTModern.id("centrifuge/depleted_plutonium_liquid_fuel"))
                .inputFluids(TSTMaterials.PLUTONIUM_BASED_LIQUID_FUEL_DEPLETED.getFluid(1_000)).circuitMeta(1)
                .chancedOutput(ChemicalHelper.get(dust, GTMaterials.Tritanium, 9), 5_000, 0)
                .chancedOutput(ChemicalHelper.get(dust, GTMaterials.Cerium, 4), 8_000, 0)
                .chancedOutput(ChemicalHelper.get(dust, GTMaterials.Gold, 2), 7_500, 0)
                .outputFluids(GTMaterials.Krypton.getFluid(144))
                .duration(2_500).EUt(VA[IV]).save(provider);

        CENTRIFUGE_RECIPES.recipeBuilder(TSTModern.id("centrifuge/depleted_naquadah_fuel_mki"))
                .inputFluids(TSTMaterials.NAQUADAH_BASED_FUEL_MKI_DEPLETED.getFluid(1_000)).circuitMeta(1)
                .chancedOutput(ChemicalHelper.get(dust, GTMaterials.Naquadah, 64), 9_000, 0)
                .chancedOutput(ChemicalHelper.get(dust, GTMaterials.Naquadah, 48), 8_500, 0)
                .chancedOutput(ChemicalHelper.get(dust, GTMaterials.Naquadah, 32), 5_000, 0)
                .chancedOutput(ChemicalHelper.get(dust, GTMaterials.Neodymium, 32), 4_000, 0)
                .chancedOutput(ChemicalHelper.get(dust, GTMaterials.Europium, 32), 2_000, 0)
                .outputFluids(GTMaterials.Xenon.getFluid(144))
                .duration(6_000).EUt(VA[EV]).save(provider);
    }

    private static void addReactorCore(Consumer<FinishedRecipe> provider) {
        ASSEMBLY_LINE_RECIPES.recipeBuilder(TSTModern.id("assembly_line/naquadah_reactor_core"))
                .inputItems(ChemicalHelper.get(frameGt, GTMaterials.Tritanium, 8))
                .inputItems(ChemicalHelper.get(plateDense, GTMaterials.NaquadahAlloy, 16))
                .inputItems(GTItems.FIELD_GENERATOR_ZPM.asStack(2))
                .inputItems(GTItems.ELECTRIC_PUMP_ZPM.asStack(8))
                .inputItems(CustomTags.UV_CIRCUITS, 4)
                .inputItems(ChemicalHelper.get(wireGtOctal, GTMaterials.Europium, 8))
                .inputItems(ChemicalHelper.get(pipeHugeFluid, GTMaterials.Naquadah, 4))
                .inputItems(ChemicalHelper.get(plate, GTMaterials.Naquadria, 8))
                .inputItems(ChemicalHelper.get(screw, GTMaterials.Osmium, 16))
                .inputFluids(GTMaterials.Trinium.getFluid(576))
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(4_608))
                .inputFluids(GTMaterials.Lubricant.getFluid(8_000))
                .outputItems(new ItemStack(TSTItems.NAQUADAH_REACTOR_CORE.get()))
                .duration(1_200)
                .EUt(VA[UV])
                .stationResearch(b -> b
                        .researchStack(GTItems.FIELD_GENERATOR_ZPM.asStack())
                        .researchId("naquadah_reactor_core")
                        .dataStack(GTItems.TOOL_DATA_ORB.asStack())
                        .CWUt(32, 48_000)
                        .EUt(VA[UV]))
                .save(provider);
    }

    private static void addCasings(Consumer<FinishedRecipe> provider) {
        ASSEMBLY_LINE_RECIPES.recipeBuilder(TSTModern.id("assembly_line/field_restriction_casing"))
                .inputItems(ChemicalHelper.get(frameGt, GTMaterials.Europium))
                .inputItems(ChemicalHelper.get(plateDense, GTMaterials.NaquadahAlloy, 2))
                .inputItems(ChemicalHelper.get(plate, GTMaterials.Tritanium, 6))
                .inputItems(GTItems.FIELD_GENERATOR_MV.asStack())
                .outputItems(new ItemStack(TSTBlocks.FIELD_RESTRICTION_CASING.get()))
                .duration(400).EUt(VA[UV])
                .stationResearch(b -> b
                        .researchStack(GTItems.FIELD_GENERATOR_MV.asStack())
                        .researchId("field_restriction_casing")
                        .dataStack(GTItems.TOOL_DATA_ORB.asStack())
                        .CWUt(32, 48_000).EUt(VA[UV]))
                .save(provider);

        ASSEMBLY_LINE_RECIPES.recipeBuilder(TSTModern.id("assembly_line/mining_black_plutonium_casing"))
                .inputItems(ChemicalHelper.get(frameGt, TSTMaterials.BLACK_TITANIUM))
                .inputItems(ChemicalHelper.get(plate, TSTMaterials.BLACK_TITANIUM, 6))
                .inputItems(CustomTags.UV_CIRCUITS)
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(288))
                .outputItems(new ItemStack(TSTBlocks.MINING_BLACK_PLUTONIUM_CASING.get()))
                .duration(50).EUt(VA[UV])
                .stationResearch(b -> b
                        .researchStack(ChemicalHelper.get(plate, TSTMaterials.BLACK_TITANIUM))
                        .researchId("mining_black_plutonium_casing")
                        .dataStack(GTItems.TOOL_DATA_ORB.asStack())
                        .CWUt(32, 48_000).EUt(VA[UV]))
                .save(provider);

        ASSEMBLY_LINE_RECIPES.recipeBuilder(TSTModern.id("assembly_line/particle_beam_guidance_pipe_casing"))
                .inputItems(ChemicalHelper.get(frameGt, GTMaterials.NaquadahAlloy))
                .inputItems(ChemicalHelper.get(pipeHugeFluid, GTMaterials.Naquadah, 2))
                .inputItems(GTItems.EMITTER_ZPM.asStack(2))
                .inputItems(ChemicalHelper.get(plate, GTMaterials.Europium, 4))
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(576))
                .outputItems(new ItemStack(TSTBlocks.PARTICLE_BEAM_GUIDANCE_PIPE_CASING.get()))
                .duration(400).EUt(VA[UV])
                .stationResearch(b -> b
                        .researchStack(GTItems.EMITTER_ZPM.asStack())
                        .researchId("particle_beam_guidance_pipe_casing")
                        .dataStack(GTItems.TOOL_DATA_ORB.asStack())
                        .CWUt(32, 48_000).EUt(VA[UV]))
                .save(provider);
    }

    private static void addController(Consumer<FinishedRecipe> provider) {
        var builder = ASSEMBLY_LINE_RECIPES.recipeBuilder(TSTModern.id("assembly_line/mega_naquadah_reactor"));
        for (int i = 0; i < 4; i++) builder.inputItems(new ItemStack(TSTItems.NAQUADAH_REACTOR_CORE.get(), 64));
        for (int i = 0; i < 4; i++) builder.inputItems(TSTCircuitTags.get(UIV), 16);
        builder.inputItems(new ItemStack(TSTItems.SPECIAL_LASER_LENS.get(), 16))
                .inputItems(ChemicalHelper.get(plateDense, GTMaterials.Duranium))
                .inputItems(ChemicalHelper.get(plateDense, GTMaterials.Duranium))
                .inputItems(new ItemStack(TSTItems.SPECIAL_LASER_LENS.get(), 16));
        for (int i = 0; i < 4; i++) {
            builder.inputItems(new ItemStack(TSTBlocks.MINING_BLACK_PLUTONIUM_CASING.get(), 64));
        }
        builder.inputFluids(TSTMaterials.METASTABLE_OGANESSON.getFluid(256_000))
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(32_000))
                .inputFluids(GTMaterials.Naquadria.getFluid(1_000))
                .outputItems(MegaNaquadahReactorDefinition.MACHINE)
                .duration(8_000)
                .EUt(VA[UIV])
                .stationResearch(b -> b
                        .researchStack(new ItemStack(TSTItems.NAQUADAH_REACTOR_CORE.get()))
                        .researchId("mega_naquadah_reactor")
                        .dataStack(GTItems.TOOL_DATA_MODULE.asStack())
                        .CWUt(64, 110_592_000)
                        .EUt(VA[UIV]))
                .save(provider);
    }
}
