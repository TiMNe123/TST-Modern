package com.tstmodern.data.recipe;

import static com.gregtechceu.gtceu.api.GTValues.EV;
import static com.gregtechceu.gtceu.api.GTValues.HV;
import static com.gregtechceu.gtceu.api.GTValues.IV;
import static com.gregtechceu.gtceu.api.GTValues.LV;
import static com.gregtechceu.gtceu.api.GTValues.LuV;
import static com.gregtechceu.gtceu.api.GTValues.MV;
import static com.gregtechceu.gtceu.api.GTValues.ULV;
import static com.gregtechceu.gtceu.api.GTValues.UEV;
import static com.gregtechceu.gtceu.api.GTValues.UHV;
import static com.gregtechceu.gtceu.api.GTValues.UIV;
import static com.gregtechceu.gtceu.api.GTValues.UV;
import static com.gregtechceu.gtceu.api.GTValues.UXV;
import static com.gregtechceu.gtceu.api.GTValues.VA;
import static com.gregtechceu.gtceu.api.GTValues.ZPM;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.dust;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.dustImpure;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.dustPure;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.dustTiny;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.foil;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.frameGt;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.gem;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.ingot;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.ingotHot;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.pipeHugeFluid;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.pipeNormalFluid;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.pipeTinyFluid;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.plate;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.plateDense;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.rod;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.screw;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.wireFine;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.wireGtDouble;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.wireGtQuadruple;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ASSEMBLER_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ASSEMBLY_LINE_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.AUTOCLAVE_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.BLAST_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.CENTRIFUGE_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.CHEMICAL_BATH_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.CHEMICAL_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.COMPRESSOR_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.DISTILLATION_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ELECTROLYZER_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.FORMING_PRESS_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.IMPLOSION_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.LASER_ENGRAVER_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.LARGE_CHEMICAL_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.MIXER_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.VACUUM_RECIPES;

import java.util.function.Consumer;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.MarkerMaterials;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.tstmodern.TSTModern;
import com.tstmodern.machine.logic.NaquadahFuelRefineryLogic;
import com.tstmodern.registry.TSTBlocks;
import com.tstmodern.registry.TSTItems;
import com.tstmodern.registry.TSTMaterials;
import com.tstmodern.registry.TSTRecipeTypes;
import com.tstmodern.registry.machine.MegaNaquadahReactorDefinition;
import com.tstmodern.registry.machine.NaquadahFuelRefineryDefinition;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;

public final class NaquadahFuelRefineryRecipes {
    private NaquadahFuelRefineryRecipes() {}

    public static void register(Consumer<FinishedRecipe> provider) {
        addNaquadahReworkRecipes(provider);
        addFeedstocks(provider);
        addSuperCoolant(provider);
        addCatalystAndDenseFuelComponents(provider);
        addStructureParts(provider);
        addFuelRecipes(provider);
        addController(provider);
    }

    private static void addSuperCoolant(Consumer<FinishedRecipe> provider) {
        LARGE_CHEMICAL_RECIPES.recipeBuilder(TSTModern.id("large_chemical/super_coolant"))
                .inputFluids(GTMaterials.PCBCoolant.getFluid(1_000))
                .inputFluids(GTMaterials.Helium.getFluid(1_000))
                .outputFluids(TSTMaterials.SUPER_COOLANT.getFluid(1_000))
                .duration(400)
                .EUt(VA[UV])
                .save(provider);
    }

    private static void addNaquadahReworkRecipes(Consumer<FinishedRecipe> provider) {
        CHEMICAL_BATH_RECIPES.recipeBuilder(TSTModern.id("chemical_bath/impure_naquadah_earth"))
                .inputItems(ChemicalHelper.get(dustImpure, GTMaterials.Naquadah))
                .inputFluids(GTMaterials.Water.getFluid(333))
                .outputItems(ChemicalHelper.get(dust, TSTMaterials.NAQUADAH_EARTH))
                .duration(20).EUt(VA[ULV]).save(provider);

        CHEMICAL_BATH_RECIPES.recipeBuilder(TSTModern.id("chemical_bath/pure_naquadah_earth"))
                .inputItems(ChemicalHelper.get(dustPure, GTMaterials.Naquadah))
                .inputFluids(GTMaterials.Water.getFluid(333))
                .outputItems(ChemicalHelper.get(dust, TSTMaterials.NAQUADAH_EARTH))
                .duration(20).EUt(VA[ULV]).save(provider);

        CHEMICAL_RECIPES.recipeBuilder(TSTModern.id("chemical/naquadah_emulsion"))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Quicklime, 32))
                .circuitMeta(4)
                .inputFluids(TSTMaterials.ACID_NAQUADAH_EMULSION.getFluid(4_000))
                .outputItems(ChemicalHelper.get(dust, GTMaterials.AntimonyTrioxide))
                .outputItems(ChemicalHelper.get(dust, TSTMaterials.FLUORITE, 16))
                .outputFluids(TSTMaterials.NAQUADAH_EMULSION.getFluid(4_000))
                .duration(240).EUt(VA[MV]).save(provider);

        CENTRIFUGE_RECIPES.recipeBuilder(TSTModern.id("centrifuge/naquadah_emulsion"))
                .circuitMeta(1)
                .inputFluids(TSTMaterials.NAQUADAH_EMULSION.getFluid(1_000))
                .chancedOutput(ChemicalHelper.get(dust, TSTMaterials.RADIOACTIVE_SLUDGE, 4), 8_000, 0)
                .chancedOutput(ChemicalHelper.get(dust, TSTMaterials.RADIOACTIVE_SLUDGE, 2), 7_500, 0)
                .chancedOutput(ChemicalHelper.get(dust, TSTMaterials.RADIOACTIVE_SLUDGE), 5_000, 0)
                .chancedOutput(ChemicalHelper.get(dust, TSTMaterials.RADIOACTIVE_SLUDGE), 2_000, 0)
                .chancedOutput(ChemicalHelper.get(dust, TSTMaterials.RADIOACTIVE_SLUDGE), 500, 0)
                .chancedOutput(ChemicalHelper.get(dust, TSTMaterials.RADIOACTIVE_SLUDGE), 100, 0)
                .outputFluids(TSTMaterials.NAQUADAH_SOLUTION.getFluid(500))
                .duration(800).EUt(VA[MV]).save(provider);

        DISTILLATION_RECIPES.recipeBuilder(TSTModern.id("distillation/naquadah_solution"))
                .inputFluids(TSTMaterials.NAQUADAH_SOLUTION.getFluid(1_000))
                .outputFluids(TSTMaterials.HEAVY_NAQUADAH_FUEL.getFluid(250))
                .outputFluids(TSTMaterials.LIGHT_NAQUADAH_FUEL.getFluid(500))
                .outputFluids(TSTMaterials.NAQUADAH_GAS.getFluid(3_000))
                .outputFluids(GTMaterials.Water.getFluid(500))
                .duration(50).EUt(VA[EV]).save(provider);

        CENTRIFUGE_RECIPES.recipeBuilder(TSTModern.id("centrifuge/radioactive_sludge"))
                .inputItems(ChemicalHelper.get(dust, TSTMaterials.RADIOACTIVE_SLUDGE, 4))
                .outputItems(ChemicalHelper.get(dust, GTMaterials.Calcium, 2))
                .chancedOutput(ChemicalHelper.get(dust, GTMaterials.Calcium), 9_500, 0)
                .chancedOutput(ChemicalHelper.get(dust, GTMaterials.Naquadah), 8_000, 0)
                .chancedOutput(ChemicalHelper.get(dust, GTMaterials.Uranium238), 2_500, 0)
                .chancedOutput(ChemicalHelper.get(dust, GTMaterials.Plutonium239), 2_000, 0)
                .chancedOutput(ChemicalHelper.get(dust, TSTMaterials.TIBERIUM), 2_000, 0)
                .outputFluids(GTMaterials.Radon.getFluid(20))
                .duration(900).EUt(VA[MV]).save(provider);

        BLAST_RECIPES.recipeBuilder(TSTModern.id("blast/low_quality_naquadah_emulsion"))
                .inputItems(ChemicalHelper.get(dust, TSTMaterials.NAQUADAH_EARTH, 2))
                .circuitMeta(1)
                .inputFluids(GTMaterials.FluoroantimonicAcid.getFluid(3_000))
                .outputItems(ChemicalHelper.get(dust, GTMaterials.TitaniumTrifluoride, 4))
                .outputFluids(TSTMaterials.LOW_QUALITY_NAQUADAH_EMULSION.getFluid(2_000))
                .blastFurnaceTemp(3_000).duration(100).EUt(VA[HV]).save(provider);

        BLAST_RECIPES.recipeBuilder(TSTModern.id("blast/titanium_trifluoride_reduction"))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.TitaniumTrifluoride, 4))
                .circuitMeta(1)
                .inputFluids(GTMaterials.Hydrogen.getFluid(3_000))
                .outputItems(ChemicalHelper.get(ingotHot, GTMaterials.Titanium))
                .outputFluids(GTMaterials.HydrofluoricAcid.getFluid(3_000))
                .blastFurnaceTemp(2_000).duration(120).EUt(VA[EV]).save(provider);

        CHEMICAL_RECIPES.recipeBuilder(TSTModern.id("chemical/two_ethyl_1_hexanol"))
                .notConsumable(ChemicalHelper.get(plate, GTMaterials.Copper))
                .inputFluids(GTMaterials.SeedOil.getFluid(3_000))
                .inputFluids(GTMaterials.Hydrogen.getFluid(8_000))
                .outputFluids(TSTMaterials.TWO_ETHYL_1_HEXANOL.getFluid(1_000))
                .duration(400).EUt(VA[HV]).save(provider);

        CHEMICAL_RECIPES.recipeBuilder(TSTModern.id("chemical/p507"))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Sodium, 2))
                .inputFluids(TSTMaterials.TWO_ETHYL_1_HEXANOL.getFluid(2_000))
                .inputFluids(GTMaterials.PhosphoricAcid.getFluid(1_000))
                .inputFluids(GTMaterials.Ethanol.getFluid(2_000))
                .outputFluids(TSTMaterials.P507.getFluid(1_000))
                .duration(1_200).EUt(VA[EV]).save(provider);

        CENTRIFUGE_RECIPES.recipeBuilder(TSTModern.id("centrifuge/low_quality_naquadah_emulsion"))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.SodiumHydroxide, 27))
                .circuitMeta(1)
                .inputFluids(TSTMaterials.LOW_QUALITY_NAQUADAH_EMULSION.getFluid(10_000))
                .chancedOutput(ChemicalHelper.get(dust, TSTMaterials.GALLIUM_HYDROXIDE, 112), 6_250, 0)
                .outputItems(ChemicalHelper.get(dust, GTMaterials.Antimony, 15))
                .outputFluids(TSTMaterials.LOW_QUALITY_NAQUADAH_SOLUTION.getFluid(9_000))
                .duration(1_000).EUt(VA[EV]).save(provider);

        CHEMICAL_RECIPES.recipeBuilder(TSTModern.id("chemical/naquadah_adamantium_solution"))
                .inputFluids(TSTMaterials.P507.getFluid(4_000))
                .inputFluids(TSTMaterials.LOW_QUALITY_NAQUADAH_SOLUTION.getFluid(36_000))
                .outputFluids(TSTMaterials.FLUORINE_RICH_WASTE_LIQUID.getFluid(10_000))
                .outputFluids(TSTMaterials.NAQUADAH_ADAMANTIUM_SOLUTION.getFluid(30_000))
                .duration(4_000).EUt(VA[EV]).save(provider);

        CHEMICAL_RECIPES.recipeBuilder(TSTModern.id("chemical/fluorine_rich_waste_treatment"))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Quicklime, 40))
                .inputFluids(TSTMaterials.FLUORINE_RICH_WASTE_LIQUID.getFluid(1_500))
                .outputItems(ChemicalHelper.get(dust, TSTMaterials.FLUORITE, 60))
                .outputFluids(TSTMaterials.WASTE_LIQUID.getFluid(1_000))
                .duration(1_000).EUt(VA[MV]).save(provider);

        DISTILLATION_RECIPES.recipeBuilder(TSTModern.id("distillation/waste_liquid"))
                .inputFluids(TSTMaterials.WASTE_LIQUID.getFluid(10_000))
                .outputItems(ChemicalHelper.get(dust, GTMaterials.Chromium, 3))
                .outputFluids(GTMaterials.SaltWater.getFluid(3_000))
                .outputFluids(GTMaterials.Phenol.getFluid(2_000))
                .outputFluids(GTMaterials.HydrochloricAcid.getFluid(5_000))
                .duration(300).EUt(VA[HV]).save(provider);

        AUTOCLAVE_RECIPES.recipeBuilder(TSTModern.id("autoclave/naquadah_rich_solution"))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.SodiumHydroxide, 27))
                .inputFluids(TSTMaterials.NAQUADAH_RICH_SOLUTION.getFluid(5_000))
                .outputItems(ChemicalHelper.get(dust, TSTMaterials.NAQUADAHINE, 30))
                .outputFluids(TSTMaterials.P507.getFluid(1_000))
                .duration(1_000).EUt(VA[MV]).save(provider);

        BLAST_RECIPES.recipeBuilder(TSTModern.id("blast/naquadahine_reduction"))
                .inputItems(ChemicalHelper.get(dust, TSTMaterials.NAQUADAHINE, 3))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Carbon))
                .outputItems(ChemicalHelper.get(ingotHot, GTMaterials.Naquadah))
                .outputFluids(GTMaterials.CarbonDioxide.getFluid(1_000))
                .blastFurnaceTemp(5_000).duration(40).EUt(VA[IV]).save(provider);

        ELECTROLYZER_RECIPES.recipeBuilder(TSTModern.id("electrolyzer/adamantine"))
                .inputItems(ChemicalHelper.get(dust, TSTMaterials.ADAMANTINE, 5))
                .outputItems(ChemicalHelper.get(dust, TSTMaterials.ADAMANTIUM, 2))
                .outputFluids(GTMaterials.Oxygen.getFluid(3_000))
                .duration(100).EUt(VA[LV]).save(provider);

        CHEMICAL_RECIPES.recipeBuilder(TSTModern.id("chemical/gallium_hydroxide_reduction"))
                .inputItems(ChemicalHelper.get(dust, TSTMaterials.GALLIUM_HYDROXIDE, 7))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Sodium, 3))
                .outputItems(ChemicalHelper.get(dust, GTMaterials.Gallium))
                .outputItems(ChemicalHelper.get(dust, GTMaterials.SodiumHydroxide, 9))
                .duration(40).EUt(VA[LV]).save(provider);

        ELECTROLYZER_RECIPES.recipeBuilder(TSTModern.id("electrolyzer/fluorite"))
                .inputItems(ChemicalHelper.get(dust, TSTMaterials.FLUORITE, 3))
                .outputItems(ChemicalHelper.get(dust, GTMaterials.Calcium))
                .outputFluids(GTMaterials.Fluorine.getFluid(2_000))
                .duration(60).EUt(VA[LV]).save(provider);

        BLAST_RECIPES.recipeBuilder(TSTModern.id("blast/enriched_naquadah_sulphate_reduction"))
                .inputItems(ChemicalHelper.get(dust, TSTMaterials.ENRICHED_NAQUADAH_SULPHATE, 11))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Zinc, 2))
                .outputItems(ChemicalHelper.get(ingotHot, GTMaterials.NaquadahEnriched))
                .outputItems(ChemicalHelper.get(dust, TSTMaterials.ZINC_SULFATE, 12))
                .blastFurnaceTemp(7_500).duration(100).EUt(VA[IV]).save(provider);

        CHEMICAL_RECIPES.recipeBuilder(TSTModern.id("chemical/zinc_sulfate_reduction"))
                .inputItems(ChemicalHelper.get(dust, TSTMaterials.ZINC_SULFATE, 6))
                .inputFluids(GTMaterials.Hydrogen.getFluid(2_000))
                .outputItems(ChemicalHelper.get(dust, GTMaterials.Zinc))
                .outputFluids(GTMaterials.SulfuricAcid.getFluid(1_000))
                .duration(30).EUt(VA[ULV]).save(provider);

        CHEMICAL_RECIPES.recipeBuilder(TSTModern.id("chemical/sodium_sulfate_reduction"))
                .inputItems(ChemicalHelper.get(dust, TSTMaterials.SODIUM_SULFATE, 7))
                .inputFluids(GTMaterials.Hydrogen.getFluid(2_000))
                .outputItems(ChemicalHelper.get(dust, GTMaterials.Sodium, 2))
                .outputFluids(GTMaterials.SulfuricAcid.getFluid(1_000))
                .duration(30).EUt(VA[LV]).save(provider);

        CHEMICAL_RECIPES.recipeBuilder(TSTModern.id("chemical/low_quality_naquadria_solution"))
                .inputItems(ChemicalHelper.get(dust, TSTMaterials.LOW_QUALITY_NAQUADRIA_SULPHATE, 3))
                .inputFluids(TSTMaterials.P507.getFluid(500))
                .inputFluids(GTMaterials.Water.getFluid(3_000))
                .outputFluids(TSTMaterials.LOW_QUALITY_NAQUADRIA_SOLUTION.getFluid(3_500))
                .duration(500).EUt(VA[EV]).save(provider);

        DISTILLATION_RECIPES.recipeBuilder(TSTModern.id("distillation/low_quality_naquadria_solution"))
                .inputFluids(TSTMaterials.LOW_QUALITY_NAQUADRIA_SOLUTION.getFluid(7_000))
                .outputItems(ChemicalHelper.get(dust, TSTMaterials.ENRICHED_NAQUADAH_EARTH, 2))
                .outputFluids(TSTMaterials.P507.getFluid(1_000))
                .outputFluids(TSTMaterials.NAQUADRIA_RICH_SOLUTION.getFluid(5_400))
                .outputFluids(GTMaterials.DilutedSulfuricAcid.getFluid(12_000))
                .duration(500).EUt(VA[IV]).save(provider);

        CHEMICAL_RECIPES.recipeBuilder(TSTModern.id("chemical/enriched_naquadah_rich_solution"))
                .inputItems(ChemicalHelper.get(dust, TSTMaterials.ENRICHED_NAQUADAH_EARTH, 4))
                .inputFluids(TSTMaterials.P507.getFluid(1_000))
                .inputFluids(GTMaterials.SulfuricAcid.getFluid(18_000))
                .outputItems(ChemicalHelper.get(dust, TSTMaterials.NAQUADAH_EARTH))
                .outputItems(ChemicalHelper.get(dust, TSTMaterials.TRINIUM_SULPHATE))
                .outputFluids(TSTMaterials.ENRICHED_NAQUADAH_RICH_SOLUTION.getFluid(4_000))
                .outputFluids(TSTMaterials.WASTE_LIQUID.getFluid(1_000))
                .duration(400).EUt(VA[EV]).save(provider);

        AUTOCLAVE_RECIPES.recipeBuilder(TSTModern.id("autoclave/enriched_naquadah_sludge"))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.SodiumHydroxide, 60))
                .inputFluids(TSTMaterials.ENRICHED_NAQUADAH_RICH_SOLUTION.getFluid(10_000))
                .outputItems(ChemicalHelper.get(dust, TSTMaterials.CONCENTRATED_ENRICHED_NAQUADAH_SLUDGE, 8))
                .outputFluids(TSTMaterials.P507.getFluid(2_500))
                .duration(1_000).EUt(VA[HV]).save(provider);

        CHEMICAL_RECIPES.recipeBuilder(TSTModern.id("chemical/trinium_sulphate_reduction"))
                .inputItems(ChemicalHelper.get(dust, TSTMaterials.TRINIUM_SULPHATE, 6))
                .inputFluids(GTMaterials.Hydrogen.getFluid(2_000))
                .outputItems(ChemicalHelper.get(dust, GTMaterials.Trinium))
                .outputFluids(GTMaterials.SulfuricAcid.getFluid(1_000))
                .duration(120).EUt(VA[HV]).save(provider);

        BLAST_RECIPES.recipeBuilder(TSTModern.id("blast/naquadria_sulphate_reduction"))
                .inputItems(ChemicalHelper.get(dust, TSTMaterials.NAQUADRIA_SULPHATE, 11))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Magnesium, 2))
                .outputItems(ChemicalHelper.get(ingotHot, GTMaterials.Naquadria))
                .outputItems(ChemicalHelper.get(dust, TSTMaterials.MAGNESIUM_SULPHATE, 12))
                .blastFurnaceTemp(9_100).duration(100).EUt(VA[ZPM]).save(provider);

        ELECTROLYZER_RECIPES.recipeBuilder(TSTModern.id("electrolyzer/magnesium_sulphate"))
                .inputItems(ChemicalHelper.get(dust, TSTMaterials.MAGNESIUM_SULPHATE, 6))
                .outputItems(ChemicalHelper.get(dust, GTMaterials.Magnesium))
                .outputItems(ChemicalHelper.get(dust, GTMaterials.Sulfur))
                .outputFluids(GTMaterials.Oxygen.getFluid(4_000))
                .duration(120).EUt(VA[MV]).save(provider);
    }

    private static void addFeedstocks(Consumer<FinishedRecipe> provider) {

        BLAST_RECIPES.recipeBuilder(TSTModern.id("blast/extremely_unstable_naquadah"))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Naquadria, 32))
                .inputFluids(GTMaterials.FluoroantimonicAcid.getFluid(4_000))
                .outputItems(ChemicalHelper.get(dust, TSTMaterials.EXTREMELY_UNSTABLE_NAQUADAH, 17))
                .outputFluids(TSTMaterials.ACID_NAQUADAH_EMULSION.getFluid(8_000))
                .blastFurnaceTemp(3_400).duration(3_600).EUt(VA[IV] / 2).save(provider);

        CHEMICAL_BATH_RECIPES.recipeBuilder(TSTModern.id("chemical_bath/tiberium"))
                .inputItems(ChemicalHelper.get(gem, GTMaterials.Diamond))
                .inputFluids(TSTMaterials.HEAVY_NAQUADAH_FUEL.getFluid(144))
                .outputItems(ChemicalHelper.get(gem, TSTMaterials.TIBERIUM))
                .duration(400).EUt(VA[EV]).save(provider);

        FORMING_PRESS_RECIPES.recipeBuilder(TSTModern.id("forming_press/orundum"))
                .inputItems(ChemicalHelper.get(plate, TSTMaterials.TIBERIUM))
                .inputItems(ChemicalHelper.get(plate, GTMaterials.Silicon, 8))
                .outputItems(ChemicalHelper.get(plate, TSTMaterials.ORUNDUM))
                .duration(400).EUt(VA[IV] / 2).save(provider);

        LASER_ENGRAVER_RECIPES.recipeBuilder(TSTModern.id("laser_engraver/astral_titanium"))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Titanium, 8))
                .notConsumable(new ItemStack(TSTItems.SPECIAL_LASER_LENS.get()))
                .outputItems(ChemicalHelper.get(dust, TSTMaterials.ASTRAL_TITANIUM))
                .duration(2_400).EUt(VA[UHV]).save(provider);

        MIXER_RECIPES.recipeBuilder(TSTModern.id("mixer/indalloy_140"))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Bismuth, 47))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Lead, 25))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Tin, 13))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Cadmium, 10))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Indium, 5))
                .circuitMeta(5)
                .outputFluids(TSTMaterials.INDALLOY_140.getFluid(14_400))
                .duration(800).EUt(VA[IV]).save(provider);

        ASSEMBLER_RECIPES.recipeBuilder(TSTModern.id("assembler/uhv_voltage_coil"))
                .inputItems(ChemicalHelper.get(rod, GTMaterials.SamariumMagnetic))
                .inputItems(ChemicalHelper.get(wireFine, GTMaterials.Tritanium, 16))
                .circuitMeta(1)
                .outputItems(new ItemStack(TSTItems.UHV_VOLTAGE_COIL.get()))
                .duration(200).EUt(VA[UHV]).save(provider);
    }

    private static void addCatalystAndDenseFuelComponents(Consumer<FinishedRecipe> provider) {
        MIXER_RECIPES.recipeBuilder(TSTModern.id("mixer/raw_atomic_separation_catalyst"))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Blaze, 32))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Duranium, 4))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Europium, 4))
                .circuitMeta(4)
                .inputFluids(GTMaterials.Naquadah.getFluid(288))
                .outputItems(new ItemStack(TSTItems.RAW_ATOMIC_SEPARATION_CATALYST.get(), 63))
                .duration(300).EUt(VA[HV]).save(provider);

        BLAST_RECIPES.recipeBuilder(TSTModern.id("blast/hot_atomic_separation_catalyst"))
                .inputItems(ChemicalHelper.get(plate, TSTMaterials.ORUNDUM, 2))
                .inputItems(new ItemStack(TSTItems.RAW_ATOMIC_SEPARATION_CATALYST.get(), 4))
                .inputFluids(GTMaterials.Plutonium239.getFluid(144))
                .outputItems(ChemicalHelper.get(ingotHot, TSTMaterials.ATOMIC_SEPARATION_CATALYST))
                .blastFurnaceTemp(5_000).duration(3_600).EUt(VA[HV]).save(provider);

        VACUUM_RECIPES.recipeBuilder(TSTModern.id("vacuum/hot_atomic_separation_catalyst"))
                .inputItems(ChemicalHelper.get(ingotHot, TSTMaterials.ATOMIC_SEPARATION_CATALYST))
                .outputItems(ChemicalHelper.get(ingot, TSTMaterials.ATOMIC_SEPARATION_CATALYST))
                .duration(200).EUt(VA[LuV]).save(provider);

        MIXER_RECIPES.recipeBuilder(TSTModern.id("mixer/graphite_uranium_mixture"))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Graphite, 3))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Uranium238))
                .outputItems(ChemicalHelper.get(dust, TSTMaterials.GRAPHITE_URANIUM_MIXTURE, 4))
                .duration(200).EUt(VA[HV]).save(provider);
        ASSEMBLER_RECIPES.recipeBuilder(TSTModern.id("assembler/wrapped_uranium_ingot"))
                .inputItems(ChemicalHelper.get(dust, TSTMaterials.GRAPHITE_URANIUM_MIXTURE, 4))
                .inputItems(ChemicalHelper.get(foil, GTMaterials.TungstenCarbide, 2)).circuitMeta(1)
                .outputItems(new ItemStack(TSTItems.WRAPPED_URANIUM_INGOT.get()))
                .duration(1_400).EUt(VA[HV]).save(provider);
        IMPLOSION_RECIPES.recipeBuilder(TSTModern.id("implosion/high_density_uranium_nugget"))
                .inputItems(new ItemStack(TSTItems.WRAPPED_URANIUM_INGOT.get(), 4))
                .outputItems(new ItemStack(TSTItems.HIGH_DENSITY_URANIUM_NUGGET.get()))
                .outputItems(ChemicalHelper.get(dustTiny, GTMaterials.TungstenCarbide, 8))
                .explosivesAmount(8).duration(20).EUt(VA[LV]).save(provider);
        COMPRESSOR_RECIPES.recipeBuilder(TSTModern.id("compressor/high_density_uranium"))
                .inputItems(new ItemStack(TSTItems.HIGH_DENSITY_URANIUM_NUGGET.get(), 9))
                .outputItems(new ItemStack(TSTItems.HIGH_DENSITY_URANIUM.get()))
                .duration(600).EUt(VA[HV]).save(provider);

        MIXER_RECIPES.recipeBuilder(TSTModern.id("mixer/plutonium_oxide_uranium_mixture"))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Plutonium239, 10))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Uranium238, 2))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Carbon, 8))
                .inputFluids(GTMaterials.Oxygen.getFluid(12_000))
                .outputItems(ChemicalHelper.get(dust, TSTMaterials.PLUTONIUM_OXIDE_URANIUM_MIXTURE, 32))
                .duration(300).EUt(VA[EV]).save(provider);
        ASSEMBLER_RECIPES.recipeBuilder(TSTModern.id("assembler/wrapped_plutonium_ingot"))
                .inputItems(ChemicalHelper.get(dust, TSTMaterials.PLUTONIUM_OXIDE_URANIUM_MIXTURE, 8))
                .inputItems(ChemicalHelper.get(foil, GTMaterials.HSSS, 4)).circuitMeta(1)
                .outputItems(new ItemStack(TSTItems.WRAPPED_PLUTONIUM_INGOT.get()))
                .duration(1_800).EUt(VA[EV]).save(provider);
        IMPLOSION_RECIPES.recipeBuilder(TSTModern.id("implosion/high_density_plutonium_nugget"))
                .inputItems(new ItemStack(TSTItems.WRAPPED_PLUTONIUM_INGOT.get(), 2))
                .outputItems(new ItemStack(TSTItems.HIGH_DENSITY_PLUTONIUM_NUGGET.get()))
                .outputItems(ChemicalHelper.get(dustTiny, GTMaterials.HSSS, 8))
                .explosivesAmount(16).duration(20).EUt(VA[LV]).save(provider);
        COMPRESSOR_RECIPES.recipeBuilder(TSTModern.id("compressor/high_density_plutonium"))
                .inputItems(new ItemStack(TSTItems.HIGH_DENSITY_PLUTONIUM_NUGGET.get(), 9))
                .outputItems(new ItemStack(TSTItems.HIGH_DENSITY_PLUTONIUM.get()))
                .duration(1_200).EUt(VA[MV]).save(provider);
    }

    private static void addStructureParts(Consumer<FinishedRecipe> provider) {
        ASSEMBLER_RECIPES.recipeBuilder(TSTModern.id("assembler/radiation_protection_plate"))
                .inputItems(ChemicalHelper.get(plateDense, GTMaterials.Iridium, 8))
                .inputItems(ChemicalHelper.get(plate, GTMaterials.NaquadahAlloy, 8)).circuitMeta(1)
                .inputFluids(GTMaterials.Lead.getFluid(1_152))
                .outputItems(new ItemStack(TSTItems.RADIATION_PROTECTION_PLATE.get()))
                .duration(400).EUt(VA[EV]).save(provider);

        CHEMICAL_RECIPES.recipeBuilder(TSTModern.id("chemical/radiation_proof_prismatic_naquadah_composite_sheet"))
                .inputItems(ChemicalHelper.get(foil, GTMaterials.TungstenCarbide, 16))
                .inputItems(ChemicalHelper.get(foil, GTMaterials.Lead, 16))
                .inputFluids(GTMaterials.Naquadria.getFluid(8_000))
                .outputItems(new ItemStack(TSTItems.RADIATION_PROOF_PRISMATIC_NAQUADAH_COMPOSITE_SHEET.get(), 16))
                .duration(320).EUt(VA[ZPM]).save(provider);

        ASSEMBLY_LINE_RECIPES.recipeBuilder(TSTModern.id("assembly_line/advanced_radiation_protection_plate"))
                .inputItems(new ItemStack(TSTItems.RADIATION_PROTECTION_PLATE.get(), 2))
                .inputItems(ChemicalHelper.get(plate, GTMaterials.Europium, 8))
                .inputItems(ChemicalHelper.get(plate, GTMaterials.Trinium, 4))
                .inputItems(ChemicalHelper.get(plate, GTMaterials.NaquadahAlloy, 4))
                .inputItems(ChemicalHelper.get(plate, GTMaterials.Osmiridium, 4))
                .inputItems(new ItemStack(TSTItems.RADIATION_PROOF_PRISMATIC_NAQUADAH_COMPOSITE_SHEET.get(), 4))
                .inputFluids(TSTMaterials.INDALLOY_140.getFluid(1_152))
                .outputItems(new ItemStack(TSTItems.ADVANCED_RADIATION_PROTECTION_PLATE.get()))
                .duration(1_000).EUt(VA[ZPM] / 2)
                .stationResearch(b -> station(b, new ItemStack(TSTItems.RADIATION_PROTECTION_PLATE.get()),
                        "advanced_radiation_protection_plate", 24_000, IV, 16)).save(provider);

        ASSEMBLER_RECIPES.recipeBuilder(TSTModern.id("assembler/europium_reinforced_radiation_proof_casing"))
                .inputItems(ChemicalHelper.get(frameGt, GTMaterials.TungstenSteel))
                .inputItems(ChemicalHelper.get(plateDense, GTMaterials.Lead, 6))
                .inputItems(new ItemStack(TSTItems.RADIATION_PROOF_PRISMATIC_NAQUADAH_COMPOSITE_SHEET.get(), 4))
                .inputItems(ChemicalHelper.get(foil, GTMaterials.Europium, 6))
                .inputItems(ChemicalHelper.get(screw, GTMaterials.Europium, 24))
                .inputFluids(GTMaterials.Lead.getFluid(864))
                .outputItems(new ItemStack(TSTBlocks.EUROPIUM_REINFORCED_RADIATION_PROOF_CASING.get()))
                .duration(200).EUt(VA[LuV]).save(provider);

        ASSEMBLER_RECIPES.recipeBuilder(TSTModern.id("assembler/field_restriction_glass"))
                .inputItems(ChemicalHelper.get(frameGt, GTMaterials.BorosilicateGlass))
                .inputItems(GTBlocks.CASING_TEMPERED_GLASS.asStack(6))
                .inputItems(ChemicalHelper.get(com.gregtechceu.gtceu.api.data.tag.TagPrefix.ring,
                        GTMaterials.NaquadahAlloy, 32))
                .inputItems(GTItems.FIELD_GENERATOR_HV.asStack(4)).circuitMeta(6)
                .inputFluids(GTMaterials.Naquadria.getFluid(288))
                .outputItems(new ItemStack(TSTBlocks.FIELD_RESTRICTION_GLASS.get()))
                .duration(300).EUt(VA[ZPM]).save(provider);

        ASSEMBLY_LINE_RECIPES.recipeBuilder(TSTModern.id("assembly_line/naquadah_fuel_refinery_casing"))
                .inputItems(ChemicalHelper.get(frameGt, GTMaterials.Thulium))
                .inputItems(new ItemStack(TSTItems.ADVANCED_RADIATION_PROTECTION_PLATE.get(), 6))
                .inputItems(GTItems.FIELD_GENERATOR_IV.asStack(2))
                .inputItems(ChemicalHelper.get(pipeTinyFluid, GTMaterials.Naquadah, 16))
                .inputItems(ChemicalHelper.get(wireFine, GTMaterials.NaquadahAlloy, 64))
                .inputItems(ChemicalHelper.get(wireFine, GTMaterials.Europium, 64))
                .inputItems(ChemicalHelper.get(plate, TSTMaterials.ORUNDUM, 4))
                .inputFluids(GTMaterials.TungstenSteel.getFluid(1_152))
                .inputFluids(TSTMaterials.INDALLOY_140.getFluid(2_304))
                .outputItems(new ItemStack(TSTBlocks.NAQUADAH_FUEL_REFINERY_CASING.get()))
                .duration(500).EUt(VA[ZPM] / 2)
                .stationResearch(b -> station(b, new ItemStack(TSTBlocks.FIELD_RESTRICTION_CASING.get()),
                        "naquadah_fuel_refinery_casing", 24_000, IV, 16)).save(provider);

        addCoilT2(provider);
        addCoilT3(provider);
        addCoilT4(provider);
    }

    private static void addCoilT2(Consumer<FinishedRecipe> provider) {
        ASSEMBLY_LINE_RECIPES.recipeBuilder(TSTModern.id("assembly_line/field_restriction_coil_t2"))
                .inputItems(ChemicalHelper.get(frameGt, GTMaterials.Neutronium))
                .inputItems(GTItems.FIELD_GENERATOR_UHV.asStack(2)).inputItems(GTItems.ELECTRIC_PUMP_UHV.asStack(8))
                .inputItems(ChemicalHelper.get(wireGtDouble,
                        GTMaterials.EnrichedNaquadahTriniumEuropiumDuranide, 64))
                .inputItems(ChemicalHelper.get(wireGtDouble,
                        GTMaterials.EnrichedNaquadahTriniumEuropiumDuranide, 64))
                .inputItems(ChemicalHelper.get(plateDense, GTMaterials.Neutronium, 8))
                .inputItems(ChemicalHelper.get(pipeNormalFluid, GTMaterials.Neutronium, 16))
                .inputItems(GTItems.ULTRA_HIGH_POWER_INTEGRATED_CIRCUIT_WAFER.asStack(48))
                .inputItems(TSTCircuitTags.get(UEV))
                .inputFluids(GTMaterials.Radon.getFluid(1_000)).inputFluids(GTMaterials.Neutronium.getFluid(9_216))
                .inputFluids(GTMaterials.Lubricant.getFluid(128_000))
                .outputItems(new ItemStack(TSTBlocks.FIELD_RESTRICTION_COIL_T2.get()))
                .duration(1_200).EUt(VA[UV])
                .stationResearch(b -> station(b, new ItemStack(TSTBlocks.FIELD_RESTRICTION_COIL_T1.get()),
                        "field_restriction_coil_t2", 60_000, UV, 64)).save(provider);
    }

    private static void addCoilT3(Consumer<FinishedRecipe> provider) {
        ASSEMBLY_LINE_RECIPES.recipeBuilder(TSTModern.id("assembly_line/field_restriction_coil_t3"))
                .inputItems(ChemicalHelper.get(frameGt, GTMaterials.Neutronium))
                .inputItems(GTItems.FIELD_GENERATOR_UEV.asStack(2)).inputItems(GTItems.ELECTRIC_PUMP_UEV.asStack(8))
                .inputItems(ChemicalHelper.get(wireGtQuadruple,
                        GTMaterials.RutheniumTriniumAmericiumNeutronate, 64))
                .inputItems(ChemicalHelper.get(wireGtQuadruple,
                        GTMaterials.RutheniumTriniumAmericiumNeutronate, 64))
                .inputItems(ChemicalHelper.get(plateDense, TSTMaterials.BLACK_TITANIUM, 8))
                .inputItems(ChemicalHelper.get(pipeNormalFluid, GTMaterials.Neutronium, 16))
                .inputItems(GTItems.ULTRA_HIGH_POWER_INTEGRATED_CIRCUIT_WAFER.asStack(64))
                .inputItems(TSTCircuitTags.get(UIV))
                .inputFluids(GTMaterials.Oganesson.getFluid(1_000)).inputFluids(GTMaterials.Neutronium.getFluid(9_216))
                .inputFluids(TSTMaterials.SUPER_COOLANT.getFluid(64_000))
                .outputItems(new ItemStack(TSTBlocks.FIELD_RESTRICTION_COIL_T3.get()))
                .duration(1_200).EUt(VA[UHV])
                .stationResearch(b -> station(b, new ItemStack(TSTBlocks.FIELD_RESTRICTION_COIL_T2.get()),
                        "field_restriction_coil_t3", 72_000, UHV, 64)).save(provider);
    }

    private static void addCoilT4(Consumer<FinishedRecipe> provider) {
        ASSEMBLY_LINE_RECIPES.recipeBuilder(TSTModern.id("assembly_line/field_restriction_coil_t4"))
                .inputItems(ChemicalHelper.get(frameGt, GTMaterials.Neutronium))
                .inputItems(GTItems.FIELD_GENERATOR_UIV.asStack(2)).inputItems(GTItems.ELECTRIC_PUMP_UIV.asStack(8))
                .inputItems(ChemicalHelper.get(wireGtQuadruple,
                        GTMaterials.RutheniumTriniumAmericiumNeutronate, 64))
                .inputItems(ChemicalHelper.get(wireGtQuadruple,
                        GTMaterials.RutheniumTriniumAmericiumNeutronate, 64))
                .inputItems(ChemicalHelper.get(plateDense, GTMaterials.Neutronium, 8))
                .inputItems(ChemicalHelper.get(pipeNormalFluid, GTMaterials.Neutronium, 16))
                .inputItems(GTItems.ULTRA_HIGH_POWER_INTEGRATED_CIRCUIT_WAFER.asStack(64))
                .inputItems(TSTCircuitTags.get(UXV))
                .inputFluids(TSTMaterials.METASTABLE_OGANESSON.getFluid(1_000))
                .inputFluids(TSTMaterials.BLACK_TITANIUM.getFluid(9_216))
                .inputFluids(TSTMaterials.SUPER_COOLANT.getFluid(64_000))
                .outputItems(new ItemStack(TSTBlocks.FIELD_RESTRICTION_COIL_T4.get()))
                .duration(1_200).EUt(VA[UEV])
                .stationResearch(b -> station(b, new ItemStack(TSTBlocks.FIELD_RESTRICTION_COIL_T3.get()),
                        "field_restriction_coil_t4", 84_000, UEV, 64)).save(provider);
    }

    private static void addFuelRecipes(Consumer<FinishedRecipe> provider) {
        TSTRecipeTypes.NAQUADAH_FUEL_REFINERY.recipeBuilder(TSTModern.id("naquadah_fuel_refinery/mkiii"))
                .inputItems(ChemicalHelper.get(dust, TSTMaterials.EXTREMELY_UNSTABLE_NAQUADAH, 4))
                .inputItems(ChemicalHelper.get(dust, TSTMaterials.TIBERIUM, 27))
                .inputItems(new ItemStack(TSTItems.HIGH_DENSITY_URANIUM.get(), 2))
                .inputItems(new ItemStack(TSTItems.HIGH_DENSITY_PLUTONIUM.get()))
                .inputFluids(TSTMaterials.HEAVY_NAQUADAH_FUEL.getFluid(800))
                .inputFluids(TSTMaterials.LIGHT_NAQUADAH_FUEL.getFluid(1_000))
                .outputFluids(TSTMaterials.NAQUADAH_BASED_FUEL_MKIII.getFluid(100))
                .addData(NaquadahFuelRefineryLogic.RECIPE_COIL_TIER, 1)
                .duration(100).EUt(1_100_000).save(provider);

        TSTRecipeTypes.NAQUADAH_FUEL_REFINERY.recipeBuilder(TSTModern.id("naquadah_fuel_refinery/mkiv"))
                .inputItems(ChemicalHelper.get(dust, TSTMaterials.EXTREMELY_UNSTABLE_NAQUADAH, 27))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.NetherStar, 64))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Neutronium, 64))
                .inputItems(ChemicalHelper.get(dust, TSTMaterials.ORUNDUM, 32))
                .inputFluids(TSTMaterials.NAQUADAH_BASED_FUEL_MKIII.getFluid(2_000))
                .inputFluids(GTMaterials.Praseodymium.getFluid(9_216))
                .outputFluids(TSTMaterials.NAQUADAH_BASED_FUEL_MKIV.getFluid(250))
                .addData(NaquadahFuelRefineryLogic.RECIPE_COIL_TIER, 2)
                .duration(160).EUt(46_000_000).save(provider);

        TSTRecipeTypes.NAQUADAH_FUEL_REFINERY.recipeBuilder(TSTModern.id("naquadah_fuel_refinery/mkv"))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Neutronium, 8))
                .inputItems(ChemicalHelper.get(dust, TSTMaterials.ATOMIC_SEPARATION_CATALYST, 32))
                .inputFluids(TSTMaterials.NAQUADAH_BASED_FUEL_MKIV.getFluid(2_000))
                .inputFluids(GTMaterials.Naquadria.getFluid(250))
                .outputFluids(TSTMaterials.NAQUADAH_BASED_FUEL_MKV.getFluid(500))
                .addData(NaquadahFuelRefineryLogic.RECIPE_COIL_TIER, 2)
                .duration(200).EUt(100_000_000).save(provider);

        TSTRecipeTypes.NAQUADAH_FUEL_REFINERY.recipeBuilder(TSTModern.id("naquadah_fuel_refinery/mkvi"))
                .inputItems(ChemicalHelper.get(dust, TSTMaterials.ASTRAL_TITANIUM, 64))
                .inputItems(ChemicalHelper.get(dust, GTMaterials.Tritanium, 32))
                .inputFluids(TSTMaterials.NAQUADAH_BASED_FUEL_MKV.getFluid(2_000))
                .inputFluids(TSTMaterials.METASTABLE_OGANESSON.getFluid(360))
                .outputFluids(TSTMaterials.NAQUADAH_BASED_FUEL_MKVI.getFluid(750))
                .addData(NaquadahFuelRefineryLogic.RECIPE_COIL_TIER, 3)
                .duration(240).EUt(320_000_000).save(provider);
    }

    private static void addController(Consumer<FinishedRecipe> provider) {
        ASSEMBLY_LINE_RECIPES.recipeBuilder(TSTModern.id("assembly_line/naquadah_fuel_refinery"))
                .inputItems(ChemicalHelper.get(frameGt, GTMaterials.Osmiridium, 8))
                .inputItems(new ItemStack(TSTItems.ADVANCED_RADIATION_PROTECTION_PLATE.get(), 64))
                .inputItems(GTItems.FIELD_GENERATOR_UV.asStack(8)).inputItems(GTItems.ELECTRIC_PUMP_UHV.asStack(2))
                .inputItems(TSTCircuitTags.get(UEV), 4)
                .inputItems(ChemicalHelper.get(pipeHugeFluid, GTMaterials.Neutronium, 8))
                .inputItems(GTItems.HIGH_POWER_INTEGRATED_CIRCUIT_WAFER.asStack(16))
                .inputItems(new ItemStack(TSTItems.UHV_VOLTAGE_COIL.get(), 64))
                .inputItems(GTItems.GLASS_LENSES.get(MarkerMaterials.Color.Yellow).asStack(16))
                .inputItems(ChemicalHelper.get(screw, GTMaterials.Thulium, 64))
                .inputFluids(GTMaterials.Naquadria.getFluid(9_216))
                .inputFluids(GTMaterials.Neutronium.getFluid(4_608))
                .inputFluids(TSTMaterials.EXTREMELY_UNSTABLE_NAQUADAH.getFluid(1_440))
                .inputFluids(TSTMaterials.INDALLOY_140.getFluid(14_400))
                .outputItems(NaquadahFuelRefineryDefinition.MACHINE)
                .duration(1_200).EUt(VA[UHV])
                .stationResearch(b -> b.researchStack(MegaNaquadahReactorDefinition.MACHINE.asStack())
                        .researchId("naquadah_fuel_refinery")
                        .dataStack(GTItems.TOOL_DATA_MODULE.asStack())
                        .CWUt(64, 110_592_000).EUt(VA[UIV]))
                .save(provider);
    }

    private static com.gregtechceu.gtceu.api.recipe.ResearchRecipeBuilder.StationRecipeBuilder station(
            com.gregtechceu.gtceu.api.recipe.ResearchRecipeBuilder.StationRecipeBuilder builder,
            ItemStack researchStack, String id, int totalCWU, int tier, int cwut) {
        return builder.researchStack(researchStack).researchId(id)
                .dataStack(GTItems.TOOL_DATA_ORB.asStack()).CWUt(cwut, totalCWU).EUt(VA[tier]);
    }
}
