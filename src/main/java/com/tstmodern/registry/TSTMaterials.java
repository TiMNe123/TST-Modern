package com.tstmodern.registry;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.event.MaterialEvent;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconSet;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.FluidProperty;
import com.gregtechceu.gtceu.api.fluids.FluidBuilder;
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

        if (!GTMaterials.EnderPearl.hasProperty(PropertyKey.FLUID)) {
            GTMaterials.EnderPearl.setProperty(
                    PropertyKey.FLUID,
                    new FluidProperty(FluidStorageKeys.LIQUID, new FluidBuilder().temperature(300)));
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
}
