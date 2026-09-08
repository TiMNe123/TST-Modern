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
    public static Material THORIUM_BASED_LIQUID_FUEL;
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
    public static Material ADAMANTIUM;
    public static Material ADAMANTINE;
    public static Material NAQUADAH_EARTH;
    public static Material CONCENTRATED_ENRICHED_NAQUADAH_SLUDGE;
    public static Material ENRICHED_NAQUADAH_SULPHATE;
    public static Material SODIUM_SULFATE;
    public static Material ZINC_SULFATE;
    public static Material MAGNESIUM_SULPHATE;
    public static Material ENRICHED_NAQUADAH_EARTH;
    public static Material TRINIUM_SULPHATE;
    public static Material ENRICHED_NAQUADAH_RICH_SOLUTION;
    public static Material NAQUADRIA_RICH_SOLUTION;
    public static Material LOW_QUALITY_NAQUADRIA_SULPHATE;
    public static Material LOW_QUALITY_NAQUADRIA_SOLUTION;
    public static Material NAQUADRIA_SULPHATE;
    public static Material SUPER_COOLANT;
    public static Material URANIUM_BASED_LIQUID_FUEL_DEPLETED;
    public static Material PLUTONIUM_BASED_LIQUID_FUEL_DEPLETED;
    public static Material NAQUADAH_BASED_FUEL_MKI;
    public static Material NAQUADAH_BASED_FUEL_MKI_DEPLETED;
    public static Material NAQUADAH_BASED_FUEL_MKII;
    public static Material NAQUADAH_BASED_FUEL_MKII_DEPLETED;
    public static Material NAQUADAH_BASED_FUEL_MKIII;
    public static Material NAQUADAH_BASED_FUEL_MKIII_DEPLETED;
    public static Material NAQUADAH_BASED_FUEL_MKIV;
    public static Material NAQUADAH_BASED_FUEL_MKIV_DEPLETED;
    public static Material SPACE;
    public static Material TIME;
    public static Material ATOMIC_SEPARATION_CATALYST;
    public static Material CRYOTHEUM;
    public static Material COOLANT;
    public static Material EXTREMELY_UNSTABLE_NAQUADAH;
    public static Material RADIOACTIVE_SLUDGE;
    public static Material ACID_NAQUADAH_EMULSION;
    public static Material NAQUADAH_EMULSION;
    public static Material NAQUADAH_SOLUTION;
    public static Material LOW_QUALITY_NAQUADAH_EMULSION;
    public static Material LOW_QUALITY_NAQUADAH_SOLUTION;
    public static Material TWO_ETHYL_1_HEXANOL;
    public static Material P507;
    public static Material FLUORINE_RICH_WASTE_LIQUID;
    public static Material WASTE_LIQUID;
    public static Material GALLIUM_HYDROXIDE;
    public static Material NAQUADAHINE;
    public static Material FLUORITE;
    public static Material TIBERIUM;
    public static Material ORUNDUM;
    public static Material ASTRAL_TITANIUM;
    public static Material INDALLOY_140;
    public static Material GRAPHITE_URANIUM_MIXTURE;
    public static Material PLUTONIUM_OXIDE_URANIUM_MIXTURE;
    public static Material URANIUM_CARBIDE_THORIUM_MIXTURE;
    public static Material WRAPPED_THORIUM;
    public static Material HIGH_DENSITY_THORIUM;
    public static Material THORIUM_232;
    public static Material THORIUM_NITRATE;
    public static Material THORIUM_HYDROXIDE;
    public static Material SODIUM_NITRATE;
    public static Material THORIUM_TETRAFLUORIDE;
    public static Material THORIUM_232_TETRAFLUORIDE;
    public static Material ZINC_CHLORIDE;
    public static Material ZINC_THORIUM_ALLOY;
    public static Material BEDROCKIUM;
    public static Material SUNNARIUM;
    public static Material NAQUADAH_GAS;
    public static Material LIGHT_NAQUADAH_FUEL;
    public static Material HEAVY_NAQUADAH_FUEL;

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
        GTMaterials.NaquadahAlloy.addFlags(MaterialFlags.GENERATE_FRAME);
        GTMaterials.Duranium.addFlags(MaterialFlags.GENERATE_DENSE);
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
        if (!GTMaterials.Praseodymium.hasProperty(PropertyKey.DUST)) {
            GTMaterials.Praseodymium.setProperty(PropertyKey.DUST, new DustProperty());
        }
        if (!GTMaterials.Californium.hasProperty(PropertyKey.DUST)) {
            GTMaterials.Californium.setProperty(PropertyKey.DUST, new DustProperty());
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

        // GoodGenerator's MkIV Naquadah fuel recipe consumes molten Praseodymium,
        // but GTCEu 7.4 registers the element without a fluid form.
        if (!GTMaterials.Praseodymium.hasProperty(PropertyKey.FLUID)) {
            GTMaterials.Praseodymium.setProperty(
                    PropertyKey.FLUID,
                    new FluidProperty(FluidStorageKeys.LIQUID, new FluidBuilder()));
        }

        // GoodGenerator's Thorium-232 separation emits liquid calcium chloride,
        // while GTCEu 7.4 only registers its dust form.
        if (!GTMaterials.CalciumChloride.hasProperty(PropertyKey.FLUID)) {
            GTMaterials.CalciumChloride.setProperty(
                    PropertyKey.FLUID,
                    new FluidProperty(FluidStorageKeys.LIQUID, new FluidBuilder().temperature(1_045)));
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
                .flags(MaterialFlags.GENERATE_PLATE, MaterialFlags.GENERATE_DENSE, MaterialFlags.GENERATE_FRAME)
                .blastTemp(7776)
                .buildAndRegister();
        METASTABLE_OGANESSON = molten("metastable_oganesson", 0x14397F, MaterialIconSet.SHINY);
        DALISENITE = molten("dalisenite", 0xB0B812, MaterialIconSet.SHINY);

        THORIUM_BASED_LIQUID_FUEL = liquid("thorium_based_liquid_fuel", 0x163207);
        THORIUM_BASED_LIQUID_FUEL_EXCITED = liquid("thorium_based_liquid_fuel_excited", 0x503266);
        THORIUM_BASED_LIQUID_FUEL_DEPLETED = liquid("thorium_based_liquid_fuel_depleted", 0x7D6C8A);
        URANIUM_BASED_LIQUID_FUEL = liquid("uranium_based_liquid_fuel", 0x00FF00);
        URANIUM_BASED_LIQUID_FUEL_EXCITED = liquid("uranium_based_liquid_fuel_excited", 0x00FF00);
        PLUTONIUM_BASED_LIQUID_FUEL = liquid("plutonium_based_liquid_fuel", 0xEF1515);
        PLUTONIUM_BASED_LIQUID_FUEL_EXCITED = liquid("plutonium_based_liquid_fuel_excited", 0xEF1515);
        URANIUM_BASED_LIQUID_FUEL_DEPLETED = liquid("uranium_based_liquid_fuel_depleted", 0x6E8B3D);
        PLUTONIUM_BASED_LIQUID_FUEL_DEPLETED = liquid("plutonium_based_liquid_fuel_depleted", 0x671919);
        NAQUADAH_BASED_FUEL_MKI = liquid("naquadah_based_fuel_mki", 0x625C5B);
        NAQUADAH_BASED_FUEL_MKI_DEPLETED = liquid("naquadah_based_fuel_mki_depleted", 0xCBC3C1);
        NAQUADAH_BASED_FUEL_MKII = liquid("naquadah_based_fuel_mkii", 0x524E4D);
        NAQUADAH_BASED_FUEL_MKII_DEPLETED = liquid("naquadah_based_fuel_mkii_depleted", 0xB5B0AE);
        NAQUADAH_BASED_FUEL_MKIII = liquid("naquadah_based_fuel_mkiii", 0x292221);
        NAQUADAH_BASED_FUEL_MKIII_DEPLETED = liquid("naquadah_based_fuel_mkiii_depleted", 0x664038);
        NAQUADAH_BASED_FUEL_MKIV = liquid("naquadah_based_fuel_mkiv", 0x0E0C0C);
        NAQUADAH_BASED_FUEL_MKIV_DEPLETED = liquid("naquadah_based_fuel_mkiv_depleted", 0x8E3422);
        NAQUADAH_BASED_FUEL_MKV = liquid("naquadah_based_fuel_mkv", 0x000000);
        NAQUADAH_BASED_FUEL_MKV_DEPLETED = liquid("naquadah_based_fuel_mkv_depleted", 0xFFFFFF);
        NAQUADAH_BASED_FUEL_MKVI = liquid("naquadah_based_fuel_mkvi", 0x300000);
        NAQUADAH_BASED_FUEL_MKVI_DEPLETED = liquid("naquadah_based_fuel_mkvi_depleted", 0x993333);

        INERT_NAQUADAH = dust("inert_naquadah", 0x3B3B3B, MaterialIconSet.METALLIC);
        INERT_ENRICHED_NAQUADAH = dust("inert_enriched_naquadah", 0x614444, MaterialIconSet.METALLIC);
        INERT_NAQUADRIA = dust("inert_naquadria", 0x000000, MaterialIconSet.METALLIC);
        NAQUADAH_ADAMANTIUM_SOLUTION = liquid("naquadah_adamantium_solution", 0x3D3838);
        NAQUADAH_RICH_SOLUTION = liquid("naquadah_rich_solution", 0x333333);
        ADAMANTIUM = new Material.Builder(TSTModern.id("adamantium"))
                .ingot().liquid(new FluidBuilder().temperature(7_200))
                .color(0xD3D3D3).iconSet(MaterialIconSet.SHINY)
                .blastTemp(7_200)
                .buildAndRegister();
        ADAMANTINE = dust("adamantine", 0xB7B7B7, MaterialIconSet.DULL);
        NAQUADAH_EARTH = dust("naquadah_earth", 0x4C4C4C, MaterialIconSet.METALLIC);
        CONCENTRATED_ENRICHED_NAQUADAH_SLUDGE = dust(
                "concentrated_enriched_naquadah_sludge", 0x523939, MaterialIconSet.METALLIC);
        ENRICHED_NAQUADAH_SULPHATE = dust("enriched_naquadah_sulphate", 0x523939, MaterialIconSet.DULL);
        SODIUM_SULFATE = dust("sodium_sulfate", 0xFFFFFF, MaterialIconSet.DULL);
        ZINC_SULFATE = dust("zinc_sulfate", 0xF9F9F9, MaterialIconSet.DULL);
        MAGNESIUM_SULPHATE = dust("magnesium_sulphate", 0x877491, MaterialIconSet.DULL);
        ENRICHED_NAQUADAH_EARTH = dust("enriched_naquadah_earth", 0x826868, MaterialIconSet.METALLIC);
        TRINIUM_SULPHATE = dust("trinium_sulphate", 0xDADADA, MaterialIconSet.METALLIC);
        ENRICHED_NAQUADAH_RICH_SOLUTION = liquid("enriched_naquadah_rich_solution", 0x523939);
        NAQUADRIA_RICH_SOLUTION = liquid("naquadria_rich_solution", 0x1F1E33);
        LOW_QUALITY_NAQUADRIA_SULPHATE = dust(
                "low_quality_naquadria_sulphate", 0x737284, MaterialIconSet.METALLIC);
        LOW_QUALITY_NAQUADRIA_SOLUTION = liquid("low_quality_naquadria_solution", 0x737284);
        NAQUADRIA_SULPHATE = dust("naquadria_sulphate", 0x1F1E33, MaterialIconSet.METALLIC);
        SUPER_COOLANT = new Material.Builder(TSTModern.id("super_coolant"))
                .liquid(new FluidBuilder().temperature(1))
                .color(0x025B6F)
                .iconSet(MaterialIconSet.FLUID)
                .buildAndRegister();
        SPACE = molten("space", 0x421C52, MaterialIconSet.SHINY);
        TIME = molten("time", 0xD9D9F3, MaterialIconSet.SHINY);
        ATOMIC_SEPARATION_CATALYST = new Material.Builder(TSTModern.id("atomic_separation_catalyst"))
                .ingot().liquid(new FluidBuilder().temperature(4_000))
                .color(0xE85E0C).iconSet(MaterialIconSet.SHINY)
                .blastTemp(5_000)
                .buildAndRegister();
        CRYOTHEUM = liquid("cryotheum", 0x46D9FF);
        COOLANT = liquid("coolant", 0x3F76E4);

        EXTREMELY_UNSTABLE_NAQUADAH = new Material.Builder(TSTModern.id("extremely_unstable_naquadah"))
                .ingot().liquid(new FluidBuilder().temperature(4200))
                .color(0x231B24).iconSet(MaterialIconSet.RADIOACTIVE)
                .radioactiveHazard(7.0f)
                .buildAndRegister();
        RADIOACTIVE_SLUDGE = dust("radioactive_sludge", 0xB3491E, MaterialIconSet.DULL);
        ACID_NAQUADAH_EMULSION = liquid("acid_naquadah_emulsion", 0x2C3522);
        NAQUADAH_EMULSION = liquid("naquadah_emulsion", 0x4A4645);
        NAQUADAH_SOLUTION = liquid("naquadah_solution", 0x848180);
        LOW_QUALITY_NAQUADAH_EMULSION = liquid("low_quality_naquadah_emulsion", 0x4C4C4C);
        LOW_QUALITY_NAQUADAH_SOLUTION = liquid("low_quality_naquadah_solution", 0x716262);
        TWO_ETHYL_1_HEXANOL = liquid("two_ethyl_1_hexanol", 0x80B557);
        P507 = liquid("p507", 0x29C22A);
        FLUORINE_RICH_WASTE_LIQUID = liquid("fluorine_rich_waste_liquid", 0x136862);
        WASTE_LIQUID = liquid("waste_liquid", 0x141C68);
        GALLIUM_HYDROXIDE = dust("gallium_hydroxide", 0xA6A6A6, MaterialIconSet.DULL);
        NAQUADAHINE = dust("naquadahine", 0x333333, MaterialIconSet.METALLIC);
        FLUORITE = dust("fluorite", 0xF5F1D5, MaterialIconSet.DULL);
        TIBERIUM = new Material.Builder(TSTModern.id("tiberium"))
                .gem()
                .color(0x5DFF3A).iconSet(MaterialIconSet.RADIOACTIVE)
                .flags(MaterialFlags.GENERATE_PLATE)
                .radioactiveHazard(4.0f)
                .buildAndRegister();
        ORUNDUM = new Material.Builder(TSTModern.id("orundum"))
                .ingot().color(0xA875D5).iconSet(MaterialIconSet.SHINY)
                .flags(MaterialFlags.GENERATE_PLATE)
                .buildAndRegister();
        ASTRAL_TITANIUM = new Material.Builder(TSTModern.id("astral_titanium"))
                .ingot().color(0x6ED8EF).iconSet(MaterialIconSet.SHINY)
                .flags(MaterialFlags.GENERATE_PLATE)
                .buildAndRegister();
        INDALLOY_140 = new Material.Builder(TSTModern.id("indalloy_140"))
                .ingot().liquid(new FluidBuilder().temperature(900))
                .color(0xC8BCC4).iconSet(MaterialIconSet.METALLIC)
                .buildAndRegister();
        GRAPHITE_URANIUM_MIXTURE = dust("graphite_uranium_mixture", 0x456E36, MaterialIconSet.RADIOACTIVE);
        PLUTONIUM_OXIDE_URANIUM_MIXTURE = dust(
                "plutonium_oxide_uranium_mixture", 0x7B3E34, MaterialIconSet.RADIOACTIVE);
        URANIUM_CARBIDE_THORIUM_MIXTURE = dust(
                "uranium_carbide_thorium_mixture", 0x163207, MaterialIconSet.DULL);
        WRAPPED_THORIUM = new Material.Builder(TSTModern.id("wrapped_thorium"))
                .ingot().color(0x315B31).iconSet(MaterialIconSet.METALLIC)
                .buildAndRegister();
        HIGH_DENSITY_THORIUM = new Material.Builder(TSTModern.id("high_density_thorium"))
                .ingot().color(0x004000).iconSet(MaterialIconSet.METALLIC)
                .buildAndRegister();
        THORIUM_232 = dust("thorium_232", 0x004000, MaterialIconSet.METALLIC);
        THORIUM_NITRATE = liquid("thorium_nitrate", 0xBAE826);
        THORIUM_HYDROXIDE = dust("thorium_hydroxide", 0x92AE89, MaterialIconSet.DULL);
        SODIUM_NITRATE = dust("sodium_nitrate", 0x846684, MaterialIconSet.ROUGH);
        THORIUM_TETRAFLUORIDE = liquid("thorium_tetrafluoride", 0x156A6A);
        THORIUM_232_TETRAFLUORIDE = liquid("thorium_232_tetrafluoride", 0x156A6A);
        ZINC_CHLORIDE = dust("zinc_chloride", 0x73A5FC, MaterialIconSet.SHINY);
        ZINC_THORIUM_ALLOY = new Material.Builder(TSTModern.id("zinc_thorium_alloy"))
                .ingot().liquid(new FluidBuilder().temperature(1_900))
                .color(0x123456).iconSet(MaterialIconSet.SHINY)
                .buildAndRegister();
        BEDROCKIUM = dust("bedrockium", 0x101010, MaterialIconSet.DULL);
        SUNNARIUM = dust("sunnarium", 0xFFFF00, MaterialIconSet.SHINY);
        NAQUADAH_GAS = new Material.Builder(TSTModern.id("naquadah_gas"))
                .gas().color(0x4A4645).iconSet(MaterialIconSet.FLUID)
                .buildAndRegister();
        LIGHT_NAQUADAH_FUEL = liquid("light_naquadah_fuel", 0x665A45);
        HEAVY_NAQUADAH_FUEL = liquid("heavy_naquadah_fuel", 0x342E29);

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
