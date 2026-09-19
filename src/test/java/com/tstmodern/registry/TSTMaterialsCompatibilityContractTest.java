package com.tstmodern.registry;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class TSTMaterialsCompatibilityContractTest {

    private static final Path SOURCE = Path.of("src/main/java/com/tstmodern/registry/TSTMaterials.java");

    @Test
    void materialEventEnablesTheTriniumFrameFormRequiredByBigBroArray() throws IOException {
        String source = Files.readString(SOURCE);
        String normalized = source.replaceAll("\\s+", " ");

        assertTrue(normalized.contains(
                "GTMaterials.Trinium.addFlags( MaterialFlags.GENERATE_FRAME );"),
                "BigBroArray tier 4 requires TST Modern to generate GTCEu's missing Trinium frame form");
    }

    @Test
    void materialEventAddsTheMissingNativeEnderPearlFluid() throws IOException {
        String source = Files.readString(SOURCE).replaceAll("\\s+", " ");

        assertTrue(source.contains("GTMaterials.EnderPearl.hasProperty(PropertyKey.FLUID)"));
        assertTrue(source.contains("new FluidProperty(FluidStorageKeys.LIQUID"));
    }

    @Test
    void materialEventRegistersTitaniumPlasmaRequiredByTheNeutronActivator() throws IOException {
        String source = Files.readString(SOURCE).replaceAll("\\s+", " ");

        assertTrue(source.contains(
                "GTMaterials.Titanium.getProperty(PropertyKey.FLUID) .enqueueRegistration(FluidStorageKeys.PLASMA, new FluidBuilder())"));
    }

    @Test
    void materialEventRegistersCopperPlasmaRequiredByMetastableOganesson() throws IOException {
        String source = Files.readString(SOURCE).replaceAll("\\s+", " ");

        assertTrue(source.contains(
                "GTMaterials.Copper.getProperty(PropertyKey.FLUID) .enqueueRegistration(FluidStorageKeys.PLASMA, new FluidBuilder())"));
    }

    @Test
    void materialEventEnablesTheBlueAlloyFrameRequiredByTheSpeedingPipe() throws IOException {
        String source = Files.readString(SOURCE).replaceAll("\\s+", " ");

        assertTrue(source.contains(
                "GTMaterials.BlueAlloy.addFlags( MaterialFlags.GENERATE_FRAME );"));
    }

    @Test
    void materialEventAddsTheMissingNativeOganessonGas() throws IOException {
        String source = Files.readString(SOURCE).replaceAll("\\s+", " ");

        assertTrue(source.contains("GTMaterials.Oganesson.hasProperty(PropertyKey.FLUID)"));
        assertTrue(source.contains(
                "new FluidProperty(FluidStorageKeys.GAS, new FluidBuilder().state(FluidState.GAS))"));
    }

    @Test
    void materialEventAddsDustFormsRequiredByDepletedFuelRecovery() throws IOException {
        String source = Files.readString(SOURCE).replaceAll("\\s+", " ");

        assertTrue(source.contains("GTMaterials.Praseodymium.hasProperty(PropertyKey.DUST)"));
        assertTrue(source.contains("GTMaterials.Californium.hasProperty(PropertyKey.DUST)"));
    }

    @Test
    void infinityProvidesMechanicalFormsAndSourcePipePropertiesWithoutBypassingTheArmillary() throws IOException {
        String source = Files.readString(SOURCE).replaceAll("\\s+", " ");

        assertTrue(source.contains("MaterialFlags.NO_SMELTING"));
        assertTrue(source.contains("MaterialFlags.GENERATE_FRAME"));
        assertTrue(source.contains("MaterialFlags.GENERATE_ROTOR"));
        assertTrue(source.contains(".fluidPipeProperties(10_000_000, 60_000, true, true, true, true)"));
        assertTrue(Files.exists(Path.of(
                "src/main/resources/assets/gtceu/textures/item/material_sets/tst_infinity/ingot.png")));
        assertTrue(Files.exists(Path.of(
                "src/main/resources/assets/gtceu/textures/block/material_sets/tst_infinity/frame_gt.png")));
    }
}
