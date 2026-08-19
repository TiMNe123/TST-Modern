package com.tstmodern.registry;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.event.MaterialEvent;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconSet;
import com.gregtechceu.gtceu.api.fluids.FluidBuilder;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.tstmodern.TSTModern;

/** Custom materials and material flag extensions for Twist Space Technology Modern. */
public final class TSTMaterials {
    public static Material HELLISH_METAL;

    private TSTMaterials() {}

    public static void registerMaterials(MaterialEvent event) {
        // Enable Rods and Frame Box generation for Obsidian (used in Nether Interface structure & recipes)
        GTMaterials.Obsidian.addFlags(
                MaterialFlags.GENERATE_ROD,
                MaterialFlags.GENERATE_LONG_ROD,
                MaterialFlags.GENERATE_FRAME
        );

        // Enable Plates and Dense Plates generation for Netherite (used in Nether Interface recipes)
        GTMaterials.Netherite.addFlags(
                MaterialFlags.GENERATE_PLATE,
                MaterialFlags.GENERATE_DENSE
        );

        // Custom Hellish Metal
        HELLISH_METAL = new Material.Builder(TSTModern.id("hellish_metal"))
                .ingot()
                .liquid(new FluidBuilder().temperature(2400))
                .color(0x991b1e).secondaryColor(0x5a0b0d)
                .iconSet(MaterialIconSet.METALLIC)
                .flags(MaterialFlags.GENERATE_PLATE, MaterialFlags.GENERATE_ROD, MaterialFlags.GENERATE_DENSE)
                .blastTemp(2800)
                .buildAndRegister();
    }
}
