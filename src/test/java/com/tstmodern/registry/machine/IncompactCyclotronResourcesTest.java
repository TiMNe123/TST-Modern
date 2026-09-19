package com.tstmodern.registry.machine;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

class IncompactCyclotronResourcesTest {

    @Test
    void shipsEveryLockedMachineCasingControllerAndParticleResource() {
        List<String> paths = List.of(
                "assets/tstmodern/textures/block/casings/quantum_frame.png",
                "assets/tstmodern/textures/block/casings/compact_cyclotron_coil.png",
                "assets/tstmodern/textures/block/casings/dense_cyclotron_outer_casing.png",
                "assets/tstmodern/textures/block/multiblock/incompact_cyclotron/overlay_front.png",
                "assets/tstmodern/textures/block/multiblock/incompact_cyclotron/overlay_front_active.png",
                "assets/tstmodern/textures/item/hydrogen_ion.png",
                "assets/tstmodern/textures/item/proton.png",
                "assets/tstmodern/textures/item/electron.png",
                "assets/tstmodern/textures/item/neutron.png",
                "assets/tstmodern/textures/item/unknown_particle.png",
                "assets/tstmodern/blockstates/incompact_cyclotron.json",
                "assets/tstmodern/models/item/incompact_cyclotron.json",
                "assets/tstmodern/models/block/machine/incompact_cyclotron.json");
        for (String path : paths) {
            assertTrue(Files.isRegularFile(Path.of("src/main/resources", path)), path);
        }
    }
}
