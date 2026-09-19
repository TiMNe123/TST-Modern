package com.tstmodern.machine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.gregtechceu.gtceu.api.GTValues;
import com.tstmodern.machine.logic.BigBroArrayEmbeddedState;
import com.tstmodern.machine.logic.BigBroArrayMode;
import com.tstmodern.machine.logic.OperationalStatus;

import net.minecraft.SharedConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class BigBroArrayMachineRuntimeGateTest {

    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void emptyStateReturnsNoMachine() {
        assertEquals(OperationalStatus.NO_MACHINE,
                evaluateStatus(BigBroArrayEmbeddedState.EMPTY, 1, true, false));
    }

    @Test
    void staleIdReturnsStaleIdStatus() {
        BigBroArrayEmbeddedState staleState = new BigBroArrayEmbeddedState(
                1,
                new ResourceLocation("tstmodern", "unknown_machine_xyz"),
                null,
                0,
                16,
                null
        );
        assertEquals(OperationalStatus.STALE_ID,
                evaluateStatus(staleState, 1, true, false));
    }

    @Test
    void frameTierTooLowReturnsFrameTooLowStatus() {
        // Frame tier 1 allows up to IV (tier 5). LuV (tier 6) should be rejected.
        BigBroArrayEmbeddedState luvMacerator = new BigBroArrayEmbeddedState(
                1,
                new ResourceLocation("gtceu", "luv_macerator"),
                BigBroArrayMode.PROCESSOR,
                GTValues.LuV,
                16,
                null
        );
        assertEquals(OperationalStatus.FRAME_TOO_LOW,
                evaluateStatus(luvMacerator, 1, true, false));
    }

    @Test
    void processorMissingInputEnergyReturnsMissingInputEnergyStatus() {
        BigBroArrayEmbeddedState ivMacerator = new BigBroArrayEmbeddedState(
                1,
                new ResourceLocation("gtceu", "iv_macerator"),
                BigBroArrayMode.PROCESSOR,
                GTValues.IV,
                16,
                null
        );
        // Frame tier 1 (IV allowed), hasOutputEnergy = true, hasInputEnergy = false
        assertEquals(OperationalStatus.MISSING_INPUT_ENERGY,
                evaluateStatus(ivMacerator, 1, false, true));
    }

    @Test
    void generatorMissingOutputEnergyReturnsMissingOutputEnergyStatus() {
        BigBroArrayEmbeddedState lvGenerator = new BigBroArrayEmbeddedState(
                1,
                new ResourceLocation("gtceu", "lv_combustion"),
                BigBroArrayMode.GENERATOR,
                GTValues.LV,
                16,
                null
        );
        // Frame tier 1 (LV allowed), hasInputEnergy = true, hasOutputEnergy = false
        assertEquals(OperationalStatus.MISSING_OUTPUT_ENERGY,
                evaluateStatus(lvGenerator, 1, true, false));
    }

    @Test
    void validProcessorWithInputEnergyReturnsCanRun() {
        BigBroArrayEmbeddedState ivMacerator = new BigBroArrayEmbeddedState(
                1,
                new ResourceLocation("gtceu", "iv_macerator"),
                BigBroArrayMode.PROCESSOR,
                GTValues.IV,
                16,
                null
        );
        assertEquals(OperationalStatus.CAN_RUN,
                evaluateStatus(ivMacerator, 1, true, false));
    }

    @Test
    void validGeneratorWithOutputEnergyReturnsCanRun() {
        BigBroArrayEmbeddedState lvGenerator = new BigBroArrayEmbeddedState(
                1,
                new ResourceLocation("gtceu", "lv_combustion"),
                BigBroArrayMode.GENERATOR,
                GTValues.LV,
                16,
                null
        );
        assertEquals(OperationalStatus.CAN_RUN,
                evaluateStatus(lvGenerator, 1, false, true));
    }

    @ParameterizedTest
    @EnumSource(OperationalStatus.class)
    void operationalStatusContract(OperationalStatus status) {
        if (status == OperationalStatus.CAN_RUN) {
            assertTrue(status.canRun());
        } else {
            assertFalse(status.canRun());
        }
        assertTrue(status.messageKey().startsWith("tstmodern.machine.big_bro_array.status"));
    }

    @Test
    void controllerTransactionsUseTheItemBusInternalStorage() throws Exception {
        String source = java.nio.file.Files.readString(
                java.nio.file.Path.of("src/main/java/com/tstmodern/machine/BigBroArrayMachine.java"));

        assertTrue(source.contains("list.add(bus.getInventory().storage)"),
                "GTCEu input/output bus capability wrappers reject controller-side extraction/insertion");
        assertFalse(source.contains("list.add(bus.getInventory())"),
                "transactions must not use the externally directional capability wrapper");
    }

    /**
     * Pure evaluator matching BigBroArrayMachine's getOperationalStatus() logic.
     */
    private static OperationalStatus evaluateStatus(
            BigBroArrayEmbeddedState state,
            int frameTier,
            boolean hasInputEnergy,
            boolean hasOutputEnergy) {
        return OperationalStatus.evaluate(state, frameTier, hasInputEnergy, hasOutputEnergy);
    }
}
