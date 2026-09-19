package com.tstmodern.registry.machine;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

class LargeNeutronOscillatorResourcesTest {

    @Test
    void shipsEveryLockedMachineCasingAndControllerResource() {
        List<String> paths = List.of(
                "assets/tstmodern/textures/block/casings/high_power_casing.png",
                "assets/tstmodern/textures/block/casings/speeding_pipe_casing.png",
                "assets/tstmodern/textures/block/casings/speeding_pipe_casing.png.mcmeta",
                "assets/tstmodern/textures/block/casings/speeding_pipe_casing_top.png",
                "assets/tstmodern/textures/block/casings/compact_fusion_coil_t3.png",
                "assets/tstmodern/textures/block/casings/compact_fusion_coil_t3.png.mcmeta",
                "assets/tstmodern/textures/block/multiblock/large_neutron_oscillator/overlay_front.png",
                "assets/tstmodern/textures/block/multiblock/large_neutron_oscillator/overlay_front_active.png",
                "assets/tstmodern/textures/block/multiblock/large_neutron_oscillator/overlay_front_emissive.png",
                "assets/tstmodern/textures/block/multiblock/large_neutron_oscillator/overlay_front_active_emissive.png",
                "assets/tstmodern/blockstates/high_power_casing.json",
                "assets/tstmodern/blockstates/speeding_pipe_casing.json",
                "assets/tstmodern/blockstates/compact_fusion_coil_t3.json",
                "assets/tstmodern/blockstates/large_neutron_oscillator.json",
                "assets/tstmodern/models/block/high_power_casing.json",
                "assets/tstmodern/models/block/speeding_pipe_casing.json",
                "assets/tstmodern/models/block/compact_fusion_coil_t3.json",
                "assets/tstmodern/models/item/high_power_casing.json",
                "assets/tstmodern/models/item/speeding_pipe_casing.json",
                "assets/tstmodern/models/item/compact_fusion_coil_t3.json",
                "assets/tstmodern/models/item/large_neutron_oscillator.json",
                "assets/tstmodern/models/item/tesseract.json",
                "assets/tstmodern/models/item/energised_tesseract.json",
                "assets/tstmodern/models/item/neutron_activator_component.json",
                "assets/tstmodern/models/item/high_computation_station_t5.json",

                "assets/tstmodern/models/block/machine/large_neutron_oscillator.json",
                "data/tstmodern/loot_tables/blocks/compact_fusion_coil_t3.json");
        for (String path : paths) {
            assertTrue(Files.isRegularFile(Path.of("src/main/resources", path)), path);
        }
    }
}
