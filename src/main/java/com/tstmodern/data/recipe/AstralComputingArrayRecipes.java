package com.tstmodern.data.recipe;

import static com.gregtechceu.gtceu.api.GTValues.EV;
import static com.gregtechceu.gtceu.api.GTValues.LuV;
import static com.gregtechceu.gtceu.api.GTValues.UEV;
import static com.gregtechceu.gtceu.api.GTValues.UHV;
import static com.gregtechceu.gtceu.api.GTValues.UIV;
import static com.gregtechceu.gtceu.api.GTValues.UV;
import static com.gregtechceu.gtceu.api.GTValues.VA;
import static com.gregtechceu.gtceu.api.GTValues.ZPM;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.bolt;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.foil;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.frameGt;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.pipeNormalFluid;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.plate;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.plateDense;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.plateDouble;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.ring;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.rodLong;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.rotor;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.screw;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.wireFine;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.wireGtDouble;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.wireGtSingle;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ASSEMBLER_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ASSEMBLY_LINE_RECIPES;

import java.util.function.Consumer;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKeys;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.machines.GTResearchMachines;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.tstmodern.TSTModern;
import com.tstmodern.registry.TSTBlocks;
import com.tstmodern.registry.TSTItems;
import com.tstmodern.registry.TSTMaterials;
import com.tstmodern.registry.machine.AstralComputingArrayDefinition;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;

public final class AstralComputingArrayRecipes {
    private AstralComputingArrayRecipes() {}

    public static void register(Consumer<FinishedRecipe> provider) {
        registerCasings(provider);
        registerRack(provider);
        registerController(provider);
    }

    private static void registerCasings(Consumer<FinishedRecipe> provider) {
        ASSEMBLY_LINE_RECIPES.recipeBuilder(TSTModern.id("assembly_line/field_restriction_coil_t1"))
                .inputItems(ChemicalHelper.get(frameGt, GTMaterials.Osmium, 1))
                .inputItems(GTItems.FIELD_GENERATOR_UV.asStack(2))
                .inputItems(GTItems.ELECTRIC_PUMP_UV.asStack(8))
                .inputItems(ChemicalHelper.get(wireGtSingle, GTMaterials.UraniumRhodiumDinaquadide, 64))
                .inputItems(ChemicalHelper.get(wireGtSingle, GTMaterials.UraniumRhodiumDinaquadide, 64))
                .inputItems(ChemicalHelper.get(plateDense, GTMaterials.Americium, 8))
                .inputItems(ChemicalHelper.get(pipeNormalFluid, GTMaterials.Neutronium, 16))
                .inputItems(GTItems.POWER_INTEGRATED_CIRCUIT_WAFER.asStack(32))
                .inputItems(CustomTags.UHV_CIRCUITS, 1)
                .inputFluids(GTMaterials.Krypton.getFluid(1000))
                .inputFluids(GTMaterials.NaquadahAlloy.getFluid(9216))
                .inputFluids(GTMaterials.Lubricant.getFluid(128000))
                .outputItems(new ItemStack(TSTBlocks.FIELD_RESTRICTION_COIL_T1.get()))
                .duration(1200).EUt(VA[ZPM])
                .stationResearch(b -> station(b, GTItems.FIELD_GENERATOR_UV.asStack(),
                        "field_restriction_coil_t1", 48_000, UV, 64))
                .save(provider);

        ASSEMBLY_LINE_RECIPES.recipeBuilder(TSTModern.id("assembly_line/compact_fusion_coil_t0"))
                .inputItems(GTBlocks.FUSION_COIL.asStack(3))
                .inputItems(new ItemStack(TSTItems.HIGH_COMPUTATION_STATION_T5.get()))
                .inputItems(ChemicalHelper.get(plate, GTMaterials.Aluminium, 2))
                .inputFluids(GTMaterials.NaquadahAlloy.getFluid(1152))
                .inputFluids(GTMaterials.RhodiumPlatedPalladium.getFluid(288))
                .outputItems(new ItemStack(TSTBlocks.COMPACT_FUSION_COIL_T0.get()))
                .duration(1200).EUt(VA[LuV])
                .stationResearch(b -> station(b, new ItemStack(TSTItems.HIGH_COMPUTATION_STATION_T5.get()),
                        "compact_fusion_coil_t0", 48_000, UV, 32))
                .save(provider);

        ASSEMBLY_LINE_RECIPES.recipeBuilder(TSTModern.id("assembly_line/space_elevator_base_casing"))
                .inputItems(ChemicalHelper.get(frameGt, GTMaterials.Neutronium, 8))
                .inputItems(ChemicalHelper.get(screw, GTMaterials.Palladium, 32))
                .inputItems(ChemicalHelper.get(plate, GTMaterials.Osmiridium, 64))
                .inputItems(CustomTags.UHV_CIRCUITS, 4)
                .inputItems(GTItems.ELECTRIC_PISTON_UV.asStack(2))
                .inputItems(ChemicalHelper.get(ring, GTMaterials.Neutronium, 8))
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(5760))
                .inputFluids(GTMaterials.UUMatter.getFluid(2000))
                .inputFluids(GTMaterials.Iridium.getFluid(1152))
                .outputItems(new ItemStack(TSTBlocks.SPACE_ELEVATOR_BASE_CASING.get(), 8))
                .duration(1200).EUt(VA[UV])
                .stationResearch(b -> station(b, GTBlocks.MACHINE_CASING_UV.asStack(),
                        "space_elevator_base_casing", 72_000, UV, 64))
                .save(provider);

        ASSEMBLY_LINE_RECIPES.recipeBuilder(TSTModern.id("assembly_line/space_elevator_support_structure"))
                .inputItems(ChemicalHelper.get(frameGt, GTMaterials.Neutronium, 8))
                .inputItems(ChemicalHelper.get(bolt, GTMaterials.Naquadria, 16))
                .inputItems(ChemicalHelper.get(rodLong, GTMaterials.Neutronium, 8))
                .inputItems(ChemicalHelper.get(plateDouble, GTMaterials.Osmiridium, 8))
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(5760))
                .inputFluids(GTMaterials.UUMatter.getFluid(1000))
                .inputFluids(GTMaterials.Iridium.getFluid(1440))
                .outputItems(new ItemStack(TSTBlocks.SPACE_ELEVATOR_SUPPORT_STRUCTURE.get(), 8))
                .duration(1200).EUt(VA[UV])
                .stationResearch(b -> station(b, new ItemStack(TSTBlocks.SPACE_ELEVATOR_BASE_CASING.get()),
                        "space_elevator_support_structure", 72_000, UV, 64))
                .save(provider);

        ASSEMBLY_LINE_RECIPES.recipeBuilder(TSTModern.id("assembly_line/space_elevator_internal_structure"))
                .inputItems(new ItemStack(TSTBlocks.HIGH_POWER_CASING.get(), 8))
                .inputItems(ChemicalHelper.get(bolt, GTMaterials.Palladium, 16))
                .inputItems(ChemicalHelper.get(plateDouble, GTMaterials.Neutronium, 8))
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(5760))
                .inputFluids(GTMaterials.UUMatter.getFluid(8000))
                .inputFluids(GTMaterials.Concrete.getFluid(1440))
                .outputItems(new ItemStack(TSTBlocks.SPACE_ELEVATOR_INTERNAL_STRUCTURE.get(), 8))
                .duration(1200).EUt(VA[UV])
                .stationResearch(b -> station(b, new ItemStack(TSTBlocks.SPACE_ELEVATOR_SUPPORT_STRUCTURE.get()),
                        "space_elevator_internal_structure", 72_000, UV, 64))
                .save(provider);

        ASSEMBLER_RECIPES.recipeBuilder(TSTModern.id("assembler/computer_casing"))
                .inputItems(new ItemStack(TSTBlocks.HIGH_POWER_CASING.get()))
                .inputItems(ChemicalHelper.get(plate, GTMaterials.StainlessSteel, 8))
                .inputItems(CustomTags.ZPM_CIRCUITS, 1)
                .inputItems(ChemicalHelper.get(wireGtDouble, GTMaterials.NiobiumTitanium, 2))
                .inputFluids(GTMaterials.Aluminium.getFluid(1296))
                .outputItems(new ItemStack(TSTBlocks.COMPUTER_CASING.get()))
                .duration(200).EUt(VA[ZPM]).save(provider);

        ASSEMBLER_RECIPES.recipeBuilder(TSTModern.id("assembler/computer_heat_vent"))
                .inputItems(ChemicalHelper.get(frameGt, GTMaterials.StainlessSteel, 1))
                .inputItems(GTItems.ELECTRIC_MOTOR_IV.asStack(2))
                .inputItems(ChemicalHelper.get(rotor, GTMaterials.StainlessSteel, 2))
                .inputItems(ChemicalHelper.get(pipeNormalFluid, GTMaterials.StainlessSteel, 16))
                .inputItems(ChemicalHelper.get(plate, GTMaterials.Copper, 16))
                .inputItems(ChemicalHelper.get(wireGtSingle, GTMaterials.NiobiumTitanium, 1))
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(1296))
                .outputItems(new ItemStack(TSTBlocks.COMPUTER_HEAT_VENT.get()))
                .duration(100).EUt(VA[EV]).save(provider);

        ASSEMBLER_RECIPES.recipeBuilder(TSTModern.id("assembler/advanced_computer_casing"))
                .inputItems(new ItemStack(TSTBlocks.COMPUTER_CASING.get()))
                .inputItems(CustomTags.ZPM_CIRCUITS, 1)
                .inputItems(ChemicalHelper.get(wireFine, GTMaterials.Cobalt, 64))
                .inputItems(ChemicalHelper.get(wireFine, GTMaterials.Electrum, 64))
                .inputItems(ChemicalHelper.get(wireGtDouble, GTMaterials.UraniumRhodiumDinaquadide, 4))
                .inputFluids(GTMaterials.Iridium.getFluid(1296))
                .outputItems(new ItemStack(TSTBlocks.ADVANCED_COMPUTER_CASING.get()))
                .duration(200).EUt(VA[ZPM]).save(provider);

        ASSEMBLY_LINE_RECIPES.recipeBuilder(TSTModern.id("assembly_line/electromagnetic_computer_coil"))
                .inputItems(new ItemStack(TSTBlocks.ADVANCED_COMPUTER_CASING.get()))
                .inputItems(GTBlocks.FUSION_COIL.asStack(2))
                .inputItems(GTBlocks.COIL_NAQUADAH.asStack(2))
                .inputItems(ChemicalHelper.get(wireFine, GTMaterials.Europium, 64))
                .inputItems(ChemicalHelper.get(foil, GTMaterials.Europium, 64))
                .inputFluids(GTMaterials.Glass.getFluid(2304))
                .inputFluids(GTMaterials.SiliconeRubber.getFluid(1872))
                .inputFluids(GTMaterials.PCBCoolant.getFluid(2000))
                .inputFluids(GTMaterials.Trinium.getFluid(1296))
                .outputItems(new ItemStack(TSTBlocks.ELECTROMAGNETIC_COMPUTER_COIL.get(), 4))
                .duration(800).EUt(200_000)
                .stationResearch(b -> station(b, new ItemStack(TSTBlocks.ADVANCED_COMPUTER_CASING.get()),
                        "electromagnetic_computer_coil", 60_000, UV, 64))
                .save(provider);

        ASSEMBLY_LINE_RECIPES.recipeBuilder(TSTModern.id("assembly_line/containment_casing"))
                .inputItems(GTItems.FIELD_GENERATOR_IV.asStack(32))
                .inputItems(GTItems.ELECTRIC_MOTOR_EV.asStack(64))
                .inputItems(GTItems.ENERGY_CLUSTER.asStack(32))
                .inputItems(ChemicalHelper.get(plate, GTMaterials.Naquadria, 64))
                .inputItems(CustomTags.IV_CIRCUITS, 64)
                .inputItems(CustomTags.LuV_CIRCUITS, 32)
                .inputItems(CustomTags.ZPM_CIRCUITS, 16)
                .inputFluids(GTMaterials.NaquadahAlloy.getFluid(9216))
                .inputFluids(GTMaterials.SolderingAlloy.getFluid(9216))
                .outputItems(new ItemStack(TSTBlocks.CONTAINMENT_CASING.get(), 32))
                .duration(24000).EUt(VA[LuV])
                .stationResearch(b -> station(b, GTItems.FIELD_GENERATOR_IV.asStack(),
                        "containment_casing", 96_000, ZPM, 64))
                .save(provider);

        ASSEMBLER_RECIPES.recipeBuilder(TSTModern.id("assembler/radiation_protection_steel_frame"))
                .inputItems(ChemicalHelper.get(rodLong, GTMaterials.Naquadria, 8))
                .inputItems(ChemicalHelper.get(frameGt, GTMaterials.HSSE, 4))
                .circuitMeta(24)
                .outputItems(new ItemStack(TSTBlocks.RADIATION_PROTECTION_STEEL_FRAME.get()))
                .duration(320).EUt(VA[EV]).save(provider);

        ASSEMBLY_LINE_RECIPES.recipeBuilder(TSTModern.id("assembly_line/astral_pylon_casing"))
                .inputItems(GTBlocks.FUSION_GLASS.asStack(4))
                .inputItems(GTItems.QUANTUM_STAR.asStack(4))
                .inputItems(GTItems.EMITTER_UEV.asStack(4))
                .inputItems(GTItems.SENSOR_UEV.asStack(4))
                .inputItems(TSTCircuitTags.get(UIV), 4)
                .inputFluids(GTMaterials.Neutronium.getFluid(2304))
                .inputFluids(GTMaterials.NaquadahAlloy.getFluid(2304))
                .inputFluids(TSTMaterials.SUPER_COOLANT.getFluid(16000))
                .outputItems(new ItemStack(TSTBlocks.ASTRAL_PYLON_CASING.get(), 4))
                .duration(1200).EUt(VA[UEV])
                .stationResearch(b -> station(b, GTItems.QUANTUM_STAR.asStack(),
                        "astral_pylon_casing", 120_000, UEV, 128))
                .save(provider);
    }

    private static void registerRack(Consumer<FinishedRecipe> provider) {
        ASSEMBLER_RECIPES.recipeBuilder(TSTModern.id("assembler/astral_computation_rack"))
                .inputItems(GTResearchMachines.HPCA_ADVANCED_COMPUTATION_COMPONENT.asStack(64))
                .inputItems(GTItems.FIELD_GENERATOR_UEV.asStack(64))
                .inputItems(new ItemStack(TSTBlocks.COMPUTER_CASING.get(), 64))
                .inputItems(new ItemStack(TSTItems.HIGH_COMPUTATION_STATION_T5.get(), 64))
                .outputItems(AstralComputingArrayDefinition.ASTRAL_COMPUTATION_RACK.asStack())
                .duration(10000).EUt(VA[UEV] * 2L).save(provider);
    }

    private static void registerController(Consumer<FinishedRecipe> provider) {
        var recipe = ASSEMBLY_LINE_RECIPES.recipeBuilder(TSTModern.id("assembly_line/astral_computing_array"));
        for (int i = 0; i < 4; i++) {
            recipe.inputItems(GTResearchMachines.HIGH_PERFORMANCE_COMPUTING_ARRAY.asStack(64));
        }
        recipe.inputItems(new ItemStack(TSTItems.HIGH_COMPUTATION_STATION_T5.get(), 64))
                .inputItems(new ItemStack(TSTItems.HIGH_COMPUTATION_STATION_T5.get(), 64))
                .inputItems(TSTCircuitTags.get(UIV), 64)
                .inputItems(TSTCircuitTags.get(UIV), 64)
                .inputItems(GTItems.SENSOR_UEV.asStack(64))
                .inputItems(GTItems.FIELD_GENERATOR_UEV.asStack(64))
                .inputItems(GTItems.ENERGY_CLUSTER.asStack(64))
                .inputItems(GTItems.QUANTUM_STAR.asStack(64))
                .inputItems(new ItemStack(TSTBlocks.COMPUTER_CASING.get(), 64))
                .inputItems(new ItemStack(TSTBlocks.COMPUTER_CASING.get(), 64))
                .inputItems(new ItemStack(TSTBlocks.COMPUTER_HEAT_VENT.get(), 64))
                .inputItems(new ItemStack(TSTBlocks.COMPUTER_HEAT_VENT.get(), 64))
                .inputFluids(GTMaterials.Tin.getFluid(FluidStorageKeys.PLASMA, 14400))
                .inputFluids(TSTMaterials.SUPER_COOLANT.getFluid(4_000_000))
                .inputFluids(GTMaterials.Neutronium.getFluid(114_514))
                .outputItems(AstralComputingArrayDefinition.MACHINE.asStack())
                .duration(20000).EUt(VA[UEV] * 3L)
                .stationResearch(b -> station(b,
                        GTResearchMachines.HIGH_PERFORMANCE_COMPUTING_ARRAY.asStack(),
                        "astral_computing_array", 2_290_280, UEV, 128))
                .save(provider);
    }

    private static com.gregtechceu.gtceu.api.recipe.ResearchRecipeBuilder.StationRecipeBuilder station(
            com.gregtechceu.gtceu.api.recipe.ResearchRecipeBuilder.StationRecipeBuilder builder,
            ItemStack researchStack, String id, int totalCWU, int tier, int cwut) {
        return builder.researchStack(researchStack)
                .researchId(id)
                .dataStack(tier >= UEV ? GTItems.TOOL_DATA_MODULE.asStack() : GTItems.TOOL_DATA_ORB.asStack())
                .CWUt(cwut, totalCWU)
                .EUt(VA[tier]);
    }
}
