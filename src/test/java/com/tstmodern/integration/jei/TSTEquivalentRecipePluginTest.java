package com.tstmodern.integration.jei;

import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TSTEquivalentRecipePluginTest {

    @Test
    @SuppressWarnings("unchecked")
    void testEquivalentsMapIsSymmetricAndComplete() throws Exception {
        Field equivalentsField = TSTEquivalentRecipePlugin.class.getDeclaredField("EQUIVALENTS");
        equivalentsField.setAccessible(true);
        Map<ResourceLocation, ResourceLocation> equivalents =
                (Map<ResourceLocation, ResourceLocation>) equivalentsField.get(null);

        assertNotNull(equivalents);
        // 7 pairs = 14 bidirectional mappings
        assertEquals(14, equivalents.size(), "Should have exactly 14 symmetric mappings (7 pairs)");

        assertSymmetric(equivalents, "gtceu:infinity_ingot", "avaritia:infinity_ingot");
        assertSymmetric(equivalents, "gtceu:awakened_draconium_ingot", "draconicevolution:awakened_draconium_ingot");
        assertSymmetric(equivalents, "tstmodern:draconium_core", "draconicevolution:draconium_core");
        assertSymmetric(equivalents, "tstmodern:dragon_heart", "draconicevolution:dragon_heart");
        assertSymmetric(equivalents, "tstmodern:crystal_matrix_ingot", "avaritia:crystal_matrix_ingot");
        assertSymmetric(equivalents, "tstmodern:diamond_lattice", "avaritia:diamond_lattice");
        assertSymmetric(equivalents, "tstmodern:infinity_catalyst", "avaritia:infinity_catalyst");
    }

    private void assertSymmetric(Map<ResourceLocation, ResourceLocation> map, String a, String b) {
        ResourceLocation ra = new ResourceLocation(a);
        ResourceLocation rb = new ResourceLocation(b);
        assertEquals(rb, map.get(ra), ra + " should map to " + rb);
        assertEquals(ra, map.get(rb), rb + " should map to " + ra);
    }
}
