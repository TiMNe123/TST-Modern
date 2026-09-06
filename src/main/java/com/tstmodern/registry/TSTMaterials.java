package com.tstmodern.registry;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.event.MaterialEvent;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconSet;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.DustProperty;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.FluidProperty;
import com.gregtechceu.gtceu.api.fluids.FluidBuilder;
import com.gregtechceu.gtceu.api.fluids.FluidState;
import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKeys;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.tstmodern.TSTModern;

/** Custom materials and material flag extensions for Twist Space Technology Modern. */
public final class TSTMaterials {
    public static Material HELLISH_METAL;
    public static Material POOR_NETHER_WASTE;
    public static Material DENSE_SUPERHEATED_STEAM;
    public static Material DENSE_SUPERCRITICAL_STEAM;
    public static Material NEPTUNIUM_238;
    public static Material PLUTONIUM_238;
    public static Material FLUORCAPHITE;
    public static Material SAMARSKITE_Y;
    public static Material TITANITE;

    // Large Neutron Oscillator dependency closure (GoodGenerator/GT++ authority).
    public static Material BLACK_TITANIUM_PREMIX;
    public static Material BLACK_TITANIUM;
    public static Material METASTABLE_OGANESSON;
    public static Material DALISENITE;
    public static Material THORIUM_BASED_LIQUID_FUEL_EXCITED;
    public static Material THORIUM_BASED_LIQUID_FUEL_DEPLETED;
    public static Material URANIUM_BASED_LIQUID_FUEL;
    public static Material URANIUM_BASED_LIQUID_FUEL_EXCITED;
    public static Material PLUTONIUM_BASED_LIQUID_FUEL;
    public static Material PLUTONIUM_BASED_LIQUID_FUEL_EXCITED;
    public static Material NAQUADAH_BASED_FUEL_MKV;
    public static Material NAQUADAH_BASED_FUEL_MKV_DEPLETED;
    public static Material NAQUADAH_BASED_FUEL_MKVI;
    public static Material NAQUADAH_BASED_FUEL_MKVI_DEPLETED;
    public static Material INERT_NAQUADAH;
    public static Material INERT_ENRICHED_NAQUADAH;
    public static Material INERT_NAQUADRIA;
    public static Material NAQUADAH_ADAMANTIUM_SOLUTION;
    public static Material NAQUADAH_RICH_SOLUTION;
    public static Material ADAMANTINE;
    public static Material NAQUADAH_EARTH;
    public static Material CONCENTRATED_ENRICHED_NAQUADAH_SLUDGE;
    public static Material ENRICHED_NAQUADAH_SULPHATE;
    public static Material SODIUM_SULFATE;
    public static Material NAQUADRIA_RICH_SOLUTION;
    public static Material LOW_QUALITY_NAQUADRIA_SULPHATE;
    public static Material NAQUADRIA_SULPHATE;
    public static Material SUPER_COOLANT;

    private TSTMaterials() {}

    public static void registerMaterials(MaterialEvent event) {
        // Enable Rods and Frame Box generation for Obsidian (used in Nether Interface structure & recipes)
        GTMaterials.Obsidian.addFlags(
                MaterialFlags.GENERATE_ROD,
                MaterialFlags.GENERATE_LONG_ROD,
                MaterialFlags.GENERATE_FRAME
        );

        // GTCEu 7.4 defines Trinium but does not generate its frame form. BigBroArray
        // uses this approved Modern frame as the fourth (UV-unlock) source tier.
        GTMaterials.Trinium.addFlags(
                MaterialFlags.GENERATE_FRAME
        );

        // GoodGenerator's Speeding Pipe recipe uses a Blue Alloy frame, which
        // GTCEu 7.4 does not generate by default.
        GTMaterials.BlueAlloy.addFlags(
                MaterialFlags.GENERATE_FRAME
        );

        // Recipe forms used by the approved TST casing ports but omitted by
        // GTCEu's default material generation.
        GTMaterials.Palladium.addFlags(MaterialFlags.GENERATE_FRAME);
        GTMaterials.SterlingSilver.addFlags(MaterialFlags.GENERATE_FRAME);
        GTMaterials.NiobiumTitanium.addFlags(MaterialFlags.GENERATE_FRAME);
        GTMaterials.RutheniumTriniumAmericiumNeutronate.addFlags(
                MaterialFlags.GENERATE_PLATE,
                MaterialFlags.GENERATE_DENSE);
        GTMaterials.Gold.addFlags(MaterialFlags.GENERATE_DENSE);
        GTMaterials.Bronze.addFlags(MaterialFlags.GENERATE_DENSE);
        GTMaterials.Lapis.addFlags(MaterialFlags.GENERATE_DENSE);
        GTMaterials.Tin.addFlags(MaterialFlags.GENERATE_DENSE);
        GTMaterials.Tritanium.addFlags(MaterialFlags.GENERATE_DENSE);
        GTMaterials.Iridium.addFlags(MaterialFlags.GENERATE_ROTOR);

        if (!GTMaterials.Erbium.hasProperty(PropertyKey.DUST)) {
            GTMaterials.Erbium.setProperty(PropertyKey.DUST, new DustProperty());
        }

        GTMaterials.TungstenCarbide.addFlags(
                MaterialFlags.GENERATE_ROTOR
        );

        // Enable Plates and Dense Plates generation for Netherite (used in Nether Interface recipes)
        GTMaterials.Netherite.addFlags(
                MaterialFlags.GENERATE_PLATE,
                MaterialFlags.GENERATE_DENSE
        );

        // Enable Bolts & Screws for Europium (used in Hollow Casing recipe)
        GTMaterials.Europium.addFlags(
                MaterialFlags.GENERATE_ROD,
                MaterialFlags.GENERATE_BOLT_SCREW
        );

        // Register Hydrogen Plasma for Incompact Cyclotron recipes
        GTMaterials.Hydrogen.getProperty(PropertyKey.FLUID)
                .enqueueRegistration(FluidStorageKeys.PLASMA, new FluidBuilder());

        // GoodGenerator's neutron activation chain requires Titanium Plasma.
        GTMaterials.Titanium.getProperty(PropertyKey.FLUID)
                .enqueueRegistration(FluidStorageKeys.PLASMA, new FluidBuilder());

        GTMaterials.Copper.getProperty(PropertyKey.FLUID)
                .enqueueRegistration(FluidStorageKeys.PLASMA, new FluidBuilder());

        if (!GTMaterials.EnderPearl.hasProperty(PropertyKey.FLUID)) {
            GTMaterials.EnderPearl.setProperty(
                    PropertyKey.FLUID,
                    new FluidProperty(FluidStorageKeys.LIQUID, new FluidBuilder().temperature(300)));
        }

        if (!GTMaterials.Oganesson.hasProperty(PropertyKey.FLUID)) {
            GTMaterials.Oganesson.setProperty(
                    PropertyKey.FLUID,
                    new FluidProperty(FluidStorageKeys.GAS, new FluidBuilder().state(FluidState.GAS)));
        }

        NEPTUNIUM_238 = new Material.Builder(TSTModern.id("neptunium_238"))
                .dust()
                .color(0xAFF04B)
                .iconSet(MaterialIconSet.RADIOACTIVE)
                .radioactiveHazard(5.0f)
                .buildAndRegister();

        PLUTONIUM_238 = new Material.Builder(TSTModern.id("plutonium_238"))
                .dust()
                .color(0xC13D3D)
                .iconSet(MaterialIconSet.RADIOACTIVE)
                .radioactiveHazard(5.0f)
                .buildAndRegister();

        // GT++ ores recovered from Radioactive Mineral Mix (Strange Dust).
        FLUORCAPHITE = dust("fluorcaphite", 0xFFFF1E, MaterialIconSet.FINE);
        SAMARSKITE_Y = dust("samarskite_y", 0x41A3A4, MaterialIconSet.SHINY);
        TITANITE = dust("titanite", 0xB8C669, MaterialIconSet.METALLIC);

        BLACK_TITANIUM_PREMIX = dust("black_titanium_premix", 0x292934, MaterialIconSet.METALLIC);
        BLACK_TITANIUM = new Material.Builder(TSTModern.id("black_titanium"))
                .ingot().liquid(new FluidBuilder().temperature(7776))
                .color(0x1A1A22).iconSet(MaterialIconSet.METALLIC)
                .flags(MaterialFlags.GENERATE_PLATE, MaterialFlags.GENERATE_DENSE)
                .blastTemp(7776)
                .buildAndRegister();
        METASTABLE_OGANESSON = molten("metastable_oganesson", 0x14397F, MaterialIconSet.SHINY);
        DALISENITE = molten("dalisenite", 0xB0B812, MaterialIconSet.SHINY);

        THORIUM_BASED_LIQUID_FUEL_EXCITED = liquid("thorium_based_liquid_fuel_excited", 0x503266);
        THORIUM_BASED_LIQUID_FUEL_DEPLETED = liquid("thorium_based_liquid_fuel_depleted", 0x7D6C8A);
        URANIUM_BASED_LIQUID_FUEL = liquid("uranium_based_liquid_fuel", 0x00FF00);
        URANIUM_BASED_LIQUID_FUEL_EXCITED = liquid("uranium_based_liquid_fuel_excited", 0x00FF00);
        PLUTONIUM_BASED_LIQUID_FUEL = liquid("plutonium_based_liquid_fuel", 0xEF1515);
        PLUTONIUM_BASED_LIQUID_FUEL_EXCITED = liquid("plutonium_based_liquid_fuel_excited", 0xEF1515);
        NAQUADAH_BASED_FUEL_MKV = liquid("naquadah_based_fuel_mkv", 0x000000);
        NAQUADAH_BASED_FUEL_MKV_DEPLETED = liquid("naquadah_based_fuel_mkv_depleted", 0xFFFFFF);
        NAQUADAH_BASED_FUEL_MKVI = liquid("naquadah_based_fuel_mkvi", 0x300000);
        NAQUADAH_BASED_FUEL_MKVI_DEPLETED = liquid("naquadah_based_fuel_mkvi_depleted", 0x993333);

        INERT_NAQUADAH = dust("inert_naquadah", 0x3B3B3B, MaterialIconSet.METALLIC);
        INERT_ENRICHED_NAQUADAH = dust("inert_enriched_naquadah", 0x614444, MaterialIconSet.METALLIC);
        INERT_NAQUADRIA = dust("inert_naquadria", 0x000000, MaterialIconSet.METALLIC);
        NAQUADAH_ADAMANTIUM_SOLUTION = liquid("naquadah_adamantium_solution", 0x3D3838);
        NAQUADAH_RICH_SOLUTION = liquid("naquadah_rich_solution", 0x333333);
        ADAMANTINE = dust("adamantine", 0xB7B7B7, MaterialIconSet.DULL);
        NAQUADAH_EARTH = dust("naquadah_earth", 0x4C4C4C, MaterialIconSet.METALLIC);
        CONCENTRATED_ENRICHED_NAQUADAH_SLUDGE = dust(
                "concentrated_enriched_naquadah_sludge", 0x523939, MaterialIconSet.METALLIC);
        ENRICHED_NAQUADAH_SULPHATE = dust("enriched_naquadah_sulphate", 0x523939, MaterialIconSet.DULL);
        SODIUM_SULFATE = dust("sodium_sulfate", 0xFFFFFF, MaterialIconSet.DULL);
        NAQUADRIA_RICH_SOLUTION = liquid("naquadria_rich_solution", 0x1F1E33);
        LOW_QUALITY_NAQUADRIA_SULPHATE = dust(
                "low_quality_naquadria_sulphate", 0x737284, MaterialIconSet.METALLIC);
        NAQUADRIA_SULPHATE = dust("naquadria_sulphate", 0x1F1E33, MaterialIconSet.METALLIC);
        SUPER_COOLANT = new Material.Builder(TSTModern.id("super_coolant"))
                .liquid(new FluidBuilder().temperature(1))
                .color(0x025B6F)
                .iconSet(MaterialIconSet.FLUID)
                .buildAndRegister();

        // Custom Hellish Metal
        HELLISH_METAL = new Material.Builder(TSTModern.id("hellish_metal"))
                .ingot()
                .liquid(new FluidBuilder().temperature(2400))
                .color(0x991b1e).secondaryColor(0x5a0b0d)
                .iconSet(MaterialIconSet.METALLIC)
                .flags(MaterialFlags.GENERATE_PLATE, MaterialFlags.GENERATE_ROD, MaterialFlags.GENERATE_DENSE)
                .blastTemp(2800)
                .buildAndRegister();

        POOR_NETHER_WASTE = new Material.Builder(TSTModern.id("poor_nether_waste"))
                .liquid(new FluidBuilder().temperature(330))
                .color(0x3A2420).secondaryColor(0x6B3A2D)
                .iconSet(MaterialIconSet.DULL)
                .buildAndRegister();

        // Dense Superheated Steam (573 K)
        DENSE_SUPERHEATED_STEAM = new Material.Builder(TSTModern.id("dense_superheated_steam"))
                .gas(new FluidBuilder().temperature(573))
                .color(0xC4D6E8)
                .iconSet(MaterialIconSet.DULL)
                .buildAndRegister();

        // Dense Supercritical Steam (1073 K)
        DENSE_SUPERCRITICAL_STEAM = new Material.Builder(TSTModern.id("dense_supercritical_steam"))
                .gas(new FluidBuilder().temperature(1073))
                .color(0xE6F2FF)
                .iconSet(MaterialIconSet.DULL)
                .buildAndRegister();
    }

    private static Material liquid(String id, int color) {
        return new Material.Builder(TSTModern.id(id))
                .liquid(new FluidBuilder().temperature(300))
                .color(color)
                .iconSet(MaterialIconSet.FLUID)
                .buildAndRegister();
    }

    private static Material molten(String id, int color, MaterialIconSet iconSet) {
        return new Material.Builder(TSTModern.id(id))
                .ingot()
                .liquid(new FluidBuilder().temperature(4000))
                .color(color)
                .iconSet(iconSet)
                .buildAndRegister();
    }

    private static Material dust(String id, int color, MaterialIconSet iconSet) {
        return new Material.Builder(TSTModern.id(id))
                .dust()
                .color(color)
                .iconSet(iconSet)
                .buildAndRegister();
    }
}
