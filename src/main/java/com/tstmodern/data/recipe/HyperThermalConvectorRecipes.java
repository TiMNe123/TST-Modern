package com.tstmodern.data.recipe;

import static com.gregtechceu.gtceu.api.GTValues.EV;
import static com.gregtechceu.gtceu.api.GTValues.HV;
import static com.gregtechceu.gtceu.api.GTValues.IV;
import static com.gregtechceu.gtceu.api.GTValues.LV;
import static com.gregtechceu.gtceu.api.GTValues.MV;
import static com.gregtechceu.gtceu.api.GTValues.UV;
import static com.gregtechceu.gtceu.api.GTValues.VA;
import static com.gregtechceu.gtceu.api.GTValues.ZPM;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.dust;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.frameGt;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.pipeLargeFluid;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.plate;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.plateDense;
import static com.gregtechceu.gtceu.common.data.GTBlocks.CASING_INVAR_HEATPROOF;
import static com.gregtechceu.gtceu.common.data.GTBlocks.CASING_POLYTETRAFLUOROETHYLENE_PIPE;
import static com.gregtechceu.gtceu.common.data.GTBlocks.CASING_TITANIUM_STABLE;
import static com.gregtechceu.gtceu.common.data.GTItems.ELECTRIC_PUMP_ZPM;
import static com.gregtechceu.gtceu.common.data.GTItems.ELECTRIC_PUMP_UV;
import static com.gregtechceu.gtceu.common.data.GTItems.FIELD_GENERATOR_ZPM;
import static com.gregtechceu.gtceu.common.data.GTMachines.HULL;
import static com.gregtechceu.gtceu.common.data.GTMachines.QUANTUM_TANK;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Americium;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Argon;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Carbon;
import static com.gregtechceu.gtceu.common.data.GTMaterials.DistilledWater;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Darmstadtium;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Helium;
import static com.gregtechceu.gtceu.common.data.GTMaterials.HeavyFuel;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Iridium;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Iron;
import static com.gregtechceu.gtceu.common.data.GTMaterials.LightFuel;
import static com.gregtechceu.gtceu.common.data.GTMaterials.NaquadahAlloy;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Naphtha;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Neutronium;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Nickel;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Nitrogen;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Oxygen;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Osmiridium;
import static com.gregtechceu.gtceu.common.data.GTMaterials.RhodiumPlatedPalladium;
import static com.gregtechceu.gtceu.common.data.GTMaterials.RutheniumTriniumAmericiumNeutronate;
import static com.gregtechceu.gtceu.common.data.GTMaterials.SeverelySteamCrackedHeavyFuel;
import static com.gregtechceu.gtceu.common.data.GTMaterials.SeverelySteamCrackedLightFuel;
import static com.gregtechceu.gtceu.common.data.GTMaterials.SeverelySteamCrackedNaphtha;
import static com.gregtechceu.gtceu.common.data.GTMaterials.SolderingAlloy;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Steam;
import static com.gregtechceu.gtceu.common.data.GTMaterials.TungstenSteel;
import static com.gregtechceu.gtceu.common.data.GTMaterials.HSSS;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ASSEMBLER_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ASSEMBLY_LINE_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.CENTRIFUGE_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.CRACKING_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.STEAM_TURBINE_FUELS;

import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKeys;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.tstmodern.TSTModern;
import com.tstmodern.registry.TSTBlocks;
import com.tstmodern.registry.TSTMaterials;
import com.tstmodern.registry.TSTRecipeTypes;
import com.tstmodern.registry.machine.HyperThermalConvectorDefinition;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Consumer;

public final class HyperThermalConvectorRecipes {
    private HyperThermalConvectorRecipes() {}

    public static void register(Consumer<FinishedRecipe> provider) {
        addHyperThermalConvectorRecipes(provider);
        addDenseSteamUsageRecipes(provider);
        addHyperThermalConvectorControllerRecipe(provider);
        addHyperThermalConvectorCasingRecipes(provider);
    }

    private static void addHyperThermalConvectorControllerRecipe(Consumer<FinishedRecipe> provider) {
        ASSEMBLY_LINE_RECIPES.recipeBuilder(TSTModern.id("assembly_line/hyper_thermal_convector"))
                .inputItems(HULL[UV], 2)
                .inputItems(ELECTRIC_PUMP_ZPM, 16)
                .inputItems(FIELD_GENERATOR_ZPM, 8)
                .inputItems(CustomTags.UV_CIRCUITS, 16)
                .inputItems(pipeLargeFluid, Iridium, 16)
                .inputItems(plate, Neutronium, 16)
                .inputItems(plate, NaquadahAlloy, 16)
                .inputItems(TSTBlocks.IRIDIUM_REINFORCED_NEUTRONIUM_CASING.get().asItem(), 4)
                .inputFluids(SolderingAlloy.getFluid(9_216))
                .inputFluids(Iridium.getFluid(4_608))
                .inputFluids(new FluidStack(Fluids.WATER, 64_000))
                .outputItems(HyperThermalConvectorDefinition.MACHINE)
                .stationResearch(b -> b
                        .researchStack(HULL[UV].asStack())
                        .researchId("hyper_thermal_convector")
                        .dataStack(GTItems.TOOL_DATA_MODULE.asStack())
                        .CWUt(64, 128_000)
                        .EUt(VA[UV]))
                .duration(20 * 60)
                .EUt(VA[UV])
                .save(provider);
    }

    private static void addHyperThermalConvectorCasingRecipes(Consumer<FinishedRecipe> provider) {
        ASSEMBLY_LINE_RECIPES.recipeBuilder(TSTModern.id("assembly_line/iridium_reinforced_neutronium_casing"))
                .inputItems(CASING_TITANIUM_STABLE.asStack())
                .inputItems(plate, Iridium, 6)
                .inputItems(plate, Neutronium, 2)
                .inputFluids(SolderingAlloy.getFluid(288))
                .outputItems(TSTBlocks.IRIDIUM_REINFORCED_NEUTRONIUM_CASING, 2)
                .stationResearch(b -> b
                        .researchStack(CASING_TITANIUM_STABLE.asStack())
                        .researchId("iridium_reinforced_neutronium_casing")
                        .dataStack(GTItems.TOOL_DATA_ORB.asStack())
                        .CWUt(32, 72_000)
                        .EUt(VA[ZPM]))
                .duration(200)
                .EUt(VA[UV])
                .save(provider);

        ASSEMBLY_LINE_RECIPES.recipeBuilder(TSTModern.id("assembly_line/borophene_nanowire_casing"))
                .inputItems(CASING_INVAR_HEATPROOF.asStack())
                .inputItems(plate, TungstenSteel, 4)
                .inputItems(dust, Carbon, 4)
                .inputFluids(SolderingAlloy.getFluid(288))
                .outputItems(TSTBlocks.BOROPHENE_NANOWIRE_CASING, 2)
                .stationResearch(b -> b
                        .researchStack(CASING_INVAR_HEATPROOF.asStack())
                        .researchId("borophene_nanowire_casing")
                        .dataStack(GTItems.TOOL_DATA_ORB.asStack())
                        .CWUt(32, 72_000)
                        .EUt(VA[ZPM]))
                .duration(200)
                .EUt(VA[UV])
                .save(provider);

        ASSEMBLY_LINE_RECIPES.recipeBuilder(TSTModern.id("assembly_line/neutronium_pipe_casing"))
                .inputItems(CASING_POLYTETRAFLUOROETHYLENE_PIPE.asStack())
                .inputItems(plate, Neutronium, 4)
                .inputFluids(SolderingAlloy.getFluid(288))
                .outputItems(TSTBlocks.NEUTRONIUM_PIPE_CASING, 2)
                .stationResearch(b -> b
                        .researchStack(CASING_POLYTETRAFLUOROETHYLENE_PIPE.asStack())
                        .researchId("neutronium_pipe_casing")
                        .dataStack(GTItems.TOOL_DATA_ORB.asStack())
                        .CWUt(32, 72_000)
                        .EUt(VA[ZPM]))
                .duration(200)
                .EUt(VA[UV])
                .save(provider);

        // HS188-A is a large GT++ alloy family. HSSS and Rhodium-Plated
        // Palladium preserve its high-temperature superalloy role in GTCEu.
        ASSEMBLY_LINE_RECIPES.recipeBuilder(TSTModern.id("assembly_line/hs188a_block"))
                .inputItems(CASING_TITANIUM_STABLE.asStack())
                .inputItems(plate, HSSS, 8)
                .inputItems(plate, RhodiumPlatedPalladium, 4)
                .inputItems(plate, TungstenSteel, 4)
                .inputFluids(SolderingAlloy.getFluid(576))
                .outputItems(TSTBlocks.HS188A_BLOCK)
                .stationResearch(b -> b
                        .researchStack(TSTBlocks.IRIDIUM_REINFORCED_NEUTRONIUM_CASING.get().asItem().getDefaultInstance())
                        .researchId("hs188a_block")
                        .dataStack(GTItems.TOOL_DATA_ORB.asStack())
                        .CWUt(32, 144_000)
                        .EUt(VA[ZPM]))
                .duration(600)
                .EUt(VA[UV])
                .save(provider);

        ASSEMBLY_LINE_RECIPES.recipeBuilder(TSTModern.id("assembly_line/quantum_alloy_block"))
                .inputItems(GTBlocks.FUSION_CASING_MK2.asStack())
                .inputItems(plateDense, RutheniumTriniumAmericiumNeutronate, 2)
                .inputItems(plate, Americium, 8)
                .inputItems(FIELD_GENERATOR_ZPM, 4)
                .inputFluids(NaquadahAlloy.getFluid(2_304))
                .outputItems(TSTBlocks.QUANTUM_ALLOY_BLOCK)
                .stationResearch(b -> b
                        .researchStack(TSTBlocks.HS188A_BLOCK.get().asItem().getDefaultInstance())
                        .researchId("quantum_alloy_block")
                        .dataStack(GTItems.TOOL_DATA_ORB.asStack())
                        .CWUt(48, 144_000)
                        .EUt(VA[UV]))
                .duration(800)
                .EUt(VA[UV])
                .save(provider);

        ASSEMBLY_LINE_RECIPES.recipeBuilder(TSTModern.id("assembly_line/extreme_density_casing"))
                .inputItems(GTBlocks.FUSION_CASING_MK2.asStack())
                .inputItems(plateDense, Darmstadtium, 4)
                .inputItems(plate, Neutronium, 8)
                .inputItems(FIELD_GENERATOR_ZPM, 4)
                .inputFluids(NaquadahAlloy.getFluid(4_608))
                .outputItems(TSTBlocks.EXTREME_DENSITY_CASING)
                .stationResearch(b -> b
                        .researchStack(GTBlocks.FUSION_CASING_MK2.asStack())
                        .researchId("extreme_density_casing")
                        .dataStack(GTItems.TOOL_DATA_ORB.asStack())
                        .CWUt(48, 144_000)
                        .EUt(VA[UV]))
                .duration(800)
                .EUt(VA[UV])
                .save(provider);

        // Exact GT5U mining-casing material recipe.
        ASSEMBLER_RECIPES.recipeBuilder(TSTModern.id("assembler/osmiridium_mining_casing"))
                .inputItems(plate, Osmiridium, 6)
                .inputItems(frameGt, Osmiridium)
                .outputItems(TSTBlocks.OSMIRIDIUM_MINING_CASING)
                .duration(50)
                .EUt(VA[LV] / 2)
                .save(provider);

        ASSEMBLY_LINE_RECIPES.recipeBuilder(TSTModern.id("assembly_line/tank_casing_tier_10"))
                .inputItems(QUANTUM_TANK[IV])
                .inputItems(plate, Neutronium, 8)
                .inputItems(ELECTRIC_PUMP_UV, 4)
                .inputItems(FIELD_GENERATOR_ZPM, 2)
                .inputFluids(SolderingAlloy.getFluid(1_152))
                .outputItems(TSTBlocks.TANK_CASING_TIER_10)
                .stationResearch(b -> b
                        .researchStack(QUANTUM_TANK[IV].asStack())
                        .researchId("tank_casing_tier_10")
                        .dataStack(GTItems.TOOL_DATA_ORB.asStack())
                        .CWUt(48, 144_000)
                        .EUt(VA[UV]))
                .duration(800)
                .EUt(VA[UV])
                .save(provider);

        ASSEMBLY_LINE_RECIPES.recipeBuilder(TSTModern.id("assembly_line/dyson_swarm_floor"))
                .inputItems(Items.STONE_BRICKS, 16)
                .inputItems(frameGt, NaquadahAlloy)
                .inputItems(plate, Neutronium, 4)
                .inputItems(plate, HSSS, 4)
                .inputFluids(com.gregtechceu.gtceu.common.data.GTMaterials.Concrete.getFluid(9_216))
                .outputItems(TSTBlocks.DYSON_SWARM_FLOOR, 4)
                .stationResearch(b -> b
                        .researchStack(Items.STONE_BRICKS.getDefaultInstance())
                        .researchId("dyson_swarm_floor")
                        .dataStack(GTItems.TOOL_DATA_ORB.asStack())
                        .CWUt(32, 72_000)
                        .EUt(VA[ZPM]))
                .duration(600)
                .EUt(VA[UV])
                .save(provider);
    }

    private static void addHyperThermalConvectorRecipes(Consumer<FinishedRecipe> provider) {
        // === Rapid Heat Exchange Recipes (UV Tier) ===
        // 1. Plasma Thermal Exchanges (Plasma + Water -> Cooled Gas/Molten Metal +
        // Dense Supercritical Steam)
        addRapidHeatExchangePair(provider, "helium_plasma",
                Helium.getFluid(FluidStorageKeys.PLASMA, 1000), 2000,
                Helium.getFluid(FluidStorageKeys.GAS, 1000),
                TSTMaterials.DENSE_SUPERCRITICAL_STEAM.getFluid(320_000));

        addRapidHeatExchangePair(provider, "nitrogen_plasma",
                Nitrogen.getFluid(FluidStorageKeys.PLASMA, 1000), 2000,
                Nitrogen.getFluid(1000),
                TSTMaterials.DENSE_SUPERCRITICAL_STEAM.getFluid(320_000));

        addRapidHeatExchangePair(provider, "oxygen_plasma",
                Oxygen.getFluid(FluidStorageKeys.PLASMA, 1000), 2000,
                Oxygen.getFluid(FluidStorageKeys.GAS, 1000),
                TSTMaterials.DENSE_SUPERCRITICAL_STEAM.getFluid(320_000));

        addRapidHeatExchangePair(provider, "argon_plasma",
                Argon.getFluid(FluidStorageKeys.PLASMA, 1000), 2000,
                Argon.getFluid(1000),
                TSTMaterials.DENSE_SUPERCRITICAL_STEAM.getFluid(320_000));

        addRapidHeatExchangePair(provider, "iron_plasma",
                Iron.getFluid(FluidStorageKeys.PLASMA, 1000), 2000,
                Iron.getFluid(1000),
                TSTMaterials.DENSE_SUPERCRITICAL_STEAM.getFluid(320_000));

        addRapidHeatExchangePair(provider, "nickel_plasma",
                Nickel.getFluid(FluidStorageKeys.PLASMA, 1000), 2000,
                Nickel.getFluid(1000),
                TSTMaterials.DENSE_SUPERCRITICAL_STEAM.getFluid(320_000));

        // 2. High-throughput Lava Thermal Exchange -> Dense Superheated Steam
        addRapidHeatExchangePair(provider, "lava_cooling",
                new FluidStack(Fluids.LAVA, 10_000), 10_000,
                TSTMaterials.DENSE_SUPERHEATED_STEAM.getFluid(1_600_000),
                DistilledWater.getFluid(2000));

        // === Rapid Cooling Recipes (UV Tier) ===
        // 1. Gas Liquefaction (Gas -> Liquid)
        TSTRecipeTypes.RAPID_COOLING.recipeBuilder(TSTModern.id("rapid_cooling/helium_liquefaction"))
                .inputFluids(Helium.getFluid(FluidStorageKeys.GAS, 1000))
                .outputFluids(Helium.getFluid(FluidStorageKeys.LIQUID, 1000))
                .duration(20)
                .EUt(VA[UV])
                .save(provider);

        TSTRecipeTypes.RAPID_COOLING.recipeBuilder(TSTModern.id("rapid_cooling/oxygen_liquefaction"))
                .inputFluids(Oxygen.getFluid(FluidStorageKeys.GAS, 1000))
                .outputFluids(Oxygen.getFluid(FluidStorageKeys.LIQUID, 1000))
                .duration(20)
                .EUt(VA[UV])
                .save(provider);

        // 2. Plasma Cooling (Plasma -> Gas/Molten)
        TSTRecipeTypes.RAPID_COOLING.recipeBuilder(TSTModern.id("rapid_cooling/helium_plasma"))
                .inputFluids(Helium.getFluid(FluidStorageKeys.PLASMA, 1000))
                .outputFluids(Helium.getFluid(FluidStorageKeys.GAS, 1000))
                .duration(20)
                .EUt(VA[UV])
                .save(provider);

        TSTRecipeTypes.RAPID_COOLING.recipeBuilder(TSTModern.id("rapid_cooling/nitrogen_plasma"))
                .inputFluids(Nitrogen.getFluid(FluidStorageKeys.PLASMA, 1000))
                .outputFluids(Nitrogen.getFluid(1000))
                .duration(20)
                .EUt(VA[UV])
                .save(provider);

        TSTRecipeTypes.RAPID_COOLING.recipeBuilder(TSTModern.id("rapid_cooling/oxygen_plasma"))
                .inputFluids(Oxygen.getFluid(FluidStorageKeys.PLASMA, 1000))
                .outputFluids(Oxygen.getFluid(FluidStorageKeys.GAS, 1000))
                .duration(20)
                .EUt(VA[UV])
                .save(provider);

        TSTRecipeTypes.RAPID_COOLING.recipeBuilder(TSTModern.id("rapid_cooling/argon_plasma"))
                .inputFluids(Argon.getFluid(FluidStorageKeys.PLASMA, 1000))
                .outputFluids(Argon.getFluid(1000))
                .duration(20)
                .EUt(VA[UV])
                .save(provider);

        TSTRecipeTypes.RAPID_COOLING.recipeBuilder(TSTModern.id("rapid_cooling/iron_plasma"))
                .inputFluids(Iron.getFluid(FluidStorageKeys.PLASMA, 1000))
                .outputFluids(Iron.getFluid(1000))
                .duration(20)
                .EUt(VA[UV])
                .save(provider);

        TSTRecipeTypes.RAPID_COOLING.recipeBuilder(TSTModern.id("rapid_cooling/nickel_plasma"))
                .inputFluids(Nickel.getFluid(FluidStorageKeys.PLASMA, 1000))
                .outputFluids(Nickel.getFluid(1000))
                .duration(20)
                .EUt(VA[UV])
                .save(provider);

        // 3. Steam Cascading Condensation (Supercritical -> Superheated -> Regular
        // Steam -> Distilled Water)
        TSTRecipeTypes.RAPID_COOLING.recipeBuilder(TSTModern.id("rapid_cooling/supercritical_to_superheated"))
                .inputFluids(TSTMaterials.DENSE_SUPERCRITICAL_STEAM.getFluid(1000))
                .outputFluids(TSTMaterials.DENSE_SUPERHEATED_STEAM.getFluid(1000))
                .duration(10)
                .EUt(VA[UV])
                .save(provider);

        TSTRecipeTypes.RAPID_COOLING.recipeBuilder(TSTModern.id("rapid_cooling/superheated_to_steam"))
                .inputFluids(TSTMaterials.DENSE_SUPERHEATED_STEAM.getFluid(1000))
                .outputFluids(Steam.getFluid(1000))
                .duration(10)
                .EUt(VA[UV])
                .save(provider);

        TSTRecipeTypes.RAPID_COOLING.recipeBuilder(TSTModern.id("rapid_cooling/steam_condensation"))
                .inputFluids(Steam.getFluid(160_000))
                .outputFluids(DistilledWater.getFluid(1000))
                .duration(10)
                .EUt(VA[UV])
                .save(provider);
    }

    private static void addRapidHeatExchangePair(
                                                  Consumer<FinishedRecipe> provider,
                                                  String recipeName,
                                                  FluidStack hotInput,
                                                  int coolingAmount,
                                                  FluidStack firstOutput,
                                                  FluidStack secondOutput) {
        for (boolean distilled : new boolean[] { false, true }) {
            String suffix = distilled ? "_distilled_water" : "";
            FluidStack coolingFluid = distilled ?
                    DistilledWater.getFluid(coolingAmount) :
                    new FluidStack(Fluids.WATER, coolingAmount);
            TSTRecipeTypes.RAPID_HEAT_EXCHANGE
                    .recipeBuilder(TSTModern.id("rapid_heat_exchange/" + recipeName + suffix))
                    .inputFluids(hotInput.copy())
                    .inputFluids(coolingFluid)
                    .outputFluids(firstOutput.copy())
                    .outputFluids(secondOutput.copy())
                    .duration(20)
                    .EUt(VA[UV])
                    .save(provider);
        }
    }

    private static void addDenseSteamUsageRecipes(Consumer<FinishedRecipe> provider) {
        // === 1. Steam Turbine Fuels (Power Generation) ===
        // Regular Steam gives 0.5 EU/mB (640 mB -> 320 EU).
        // Dense Superheated Steam (573 K) gives 150 EU/mB (300x multiplier).
        STEAM_TURBINE_FUELS.recipeBuilder(TSTModern.id("fuel/dense_superheated_steam"))
                .inputFluids(TSTMaterials.DENSE_SUPERHEATED_STEAM.getFluid(2))
                .outputFluids(DistilledWater.getFluid(1))
                .duration(10)
                .EUt(-300)
                .save(provider);

        // Dense Supercritical Steam (1073 K) gives 600 EU/mB (1200x multiplier).
        STEAM_TURBINE_FUELS.recipeBuilder(TSTModern.id("fuel/dense_supercritical_steam"))
                .inputFluids(TSTMaterials.DENSE_SUPERCRITICAL_STEAM.getFluid(1))
                .outputFluids(DistilledWater.getFluid(1))
                .duration(10)
                .EUt(-600)
                .save(provider);

        // === 2. Centrifuge / Decompressor (De-densification into Regular Steam) ===
        CENTRIFUGE_RECIPES.recipeBuilder(TSTModern.id("centrifuge/decompress_superheated_steam"))
                .inputFluids(TSTMaterials.DENSE_SUPERHEATED_STEAM.getFluid(100))
                .outputFluids(Steam.getFluid(30_000))
                .duration(20)
                .EUt(VA[MV])
                .save(provider);

        CENTRIFUGE_RECIPES.recipeBuilder(TSTModern.id("centrifuge/decompress_supercritical_steam"))
                .circuitMeta(1)
                .inputFluids(TSTMaterials.DENSE_SUPERCRITICAL_STEAM.getFluid(100))
                .outputFluids(Steam.getFluid(120_000))
                .duration(20)
                .EUt(VA[HV])
                .save(provider);

        CENTRIFUGE_RECIPES.recipeBuilder(TSTModern.id("centrifuge/supercritical_to_superheated"))
                .circuitMeta(2)
                .inputFluids(TSTMaterials.DENSE_SUPERCRITICAL_STEAM.getFluid(100))
                .outputFluids(TSTMaterials.DENSE_SUPERHEATED_STEAM.getFluid(400))
                .duration(20)
                .EUt(VA[HV])
                .save(provider);

        // === 3. High-Efficiency Supercritical Steam Cracking ===
        CRACKING_RECIPES.recipeBuilder(TSTModern.id("cracking/supercritical_heavy_fuel"))
                .inputFluids(HeavyFuel.getFluid(1000))
                .inputFluids(TSTMaterials.DENSE_SUPERCRITICAL_STEAM.getFluid(100))
                .outputFluids(SeverelySteamCrackedHeavyFuel.getFluid(1500))
                .duration(80)
                .EUt(VA[EV])
                .save(provider);

        CRACKING_RECIPES.recipeBuilder(TSTModern.id("cracking/supercritical_naphtha"))
                .inputFluids(Naphtha.getFluid(1000))
                .inputFluids(TSTMaterials.DENSE_SUPERCRITICAL_STEAM.getFluid(100))
                .outputFluids(SeverelySteamCrackedNaphtha.getFluid(1500))
                .duration(80)
                .EUt(VA[EV])
                .save(provider);

        CRACKING_RECIPES.recipeBuilder(TSTModern.id("cracking/supercritical_light_fuel"))
                .inputFluids(LightFuel.getFluid(1000))
                .inputFluids(TSTMaterials.DENSE_SUPERCRITICAL_STEAM.getFluid(100))
                .outputFluids(SeverelySteamCrackedLightFuel.getFluid(1500))
                .duration(80)
                .EUt(VA[EV])
                .save(provider);
    }
}
