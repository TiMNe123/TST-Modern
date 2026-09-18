package com.tstmodern.registry;

import com.gregtechceu.gtceu.api.data.worldgen.GTLayerPattern;
import com.gregtechceu.gtceu.api.data.worldgen.WorldGenLayers;
import com.gregtechceu.gtceu.common.data.GTOres;
import com.tstmodern.TSTModern;

import net.minecraft.tags.BiomeTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraftforge.fml.ModList;

/** Optional fallback world generation for content normally supplied by Draconic Evolution. */
public final class TSTWorldgen {
    private TSTWorldgen() {}

    public static void register() {
        if (ModList.get().isLoaded("draconicevolution")) {
            return;
        }
        GTOres.blankOreDefinition()
                .clusterSize(UniformInt.of(16, 24))
                .density(0.20F)
                .weight(5)
                .layer(WorldGenLayers.ENDSTONE)
                .heightRangeUniform(10, 80)
                .biomes(BiomeTags.IS_END)
                .layeredVeinGenerator(generator -> generator.withLayerPattern(() ->
                        GTLayerPattern.builder(GTOres.END_RULES)
                                .layer(layer -> layer.weight(1)
                                        .state(() -> TSTBlocks.DRACONIUM_ORE.get().defaultBlockState())
                                        .size(1, 1))
                                .build()))
                .register(TSTModern.id("draconium_vein_end"));
    }
}
