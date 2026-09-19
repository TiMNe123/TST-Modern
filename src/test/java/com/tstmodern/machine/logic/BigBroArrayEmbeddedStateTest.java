package com.tstmodern.machine.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.function.Function;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;

import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class BigBroArrayEmbeddedStateTest {

    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void emptyStateBehavesCorrectly() {
        assertTrue(BigBroArrayEmbeddedState.EMPTY.isEmpty());
        assertFalse(BigBroArrayEmbeddedState.EMPTY.isValid());
        CompoundTag tag = BigBroArrayEmbeddedState.EMPTY.writeToNbt();
        BigBroArrayEmbeddedState restored = BigBroArrayEmbeddedState.readFromNbt(tag);
        assertTrue(restored.isEmpty());
        assertFalse(restored.isValid());
    }

    @Test
    void nbtRoundTripPreservesFieldsAndItemTag() {
        CompoundTag innerTag = new CompoundTag();
        innerTag.putString("custom_data", "test_value");
        innerTag.putInt("some_count", 42);

        // This ID must exist in the catalog for isValid() to be true
        ResourceLocation id = new ResourceLocation("gtceu", "lv_macerator");

        BigBroArrayEmbeddedState state = new BigBroArrayEmbeddedState(
                BigBroArrayEmbeddedState.CURRENT_VERSION,
                id,
                BigBroArrayMode.PROCESSOR,
                GTValues.LV,
                16,
                innerTag
        );

        assertFalse(state.isEmpty());
        assertTrue(state.isValid());

        CompoundTag written = state.writeToNbt();
        BigBroArrayEmbeddedState read = BigBroArrayEmbeddedState.readFromNbt(
                written, processorResolver(id, GTValues.LV));

        assertFalse(read.isEmpty());
        assertTrue(read.isValid());
        assertEquals(state.version(), read.version());
        assertEquals(state.definitionId(), read.definitionId());
        assertEquals(state.mode(), read.mode());
        assertEquals(state.tier(), read.tier());
        assertEquals(state.count(), read.count());
        assertEquals(state.itemTag(), read.itemTag());
    }

    @Test
    void unknownIdBecomesInvalidButPreservesData() {
        ResourceLocation unknownId = new ResourceLocation("tstmodern", "non_existent_machine");
        BigBroArrayEmbeddedState state = new BigBroArrayEmbeddedState(
                BigBroArrayEmbeddedState.CURRENT_VERSION,
                unknownId,
                BigBroArrayMode.PROCESSOR,
                GTValues.LV,
                10,
                null
        );

        CompoundTag written = state.writeToNbt();
        BigBroArrayEmbeddedState read = BigBroArrayEmbeddedState.readFromNbt(written);

        assertFalse(read.isEmpty()); // Contains data
        assertFalse(read.isValid()); // Not in catalog
        assertEquals(unknownId, read.definitionId());
        assertEquals(10, read.count());
        // Mode and tier are null/0 because catalog resolution failed
        assertNull(read.mode());
        assertEquals(0, read.tier());
    }

    @Test
    void inventedLegacyIdWithoutSerializedMachineIsRejected() {
        CompoundTag legacyParent = new CompoundTag();
        legacyParent.putString("embeddedMachineId", "gtceu:hv_arc_furnace");
        legacyParent.putInt("embeddedCount", 24);
        legacyParent.putInt("embeddedTier", GTValues.LV); // Incorrect legacy data (HV machine saved as LV)
        legacyParent.putString("embeddedMode", "GENERATOR"); // Incorrect legacy mode (Arc Furnace is processor)

        BigBroArrayEmbeddedState migrated = BigBroArrayEmbeddedState.migrateFromLegacy(legacyParent);

        assertEquals(BigBroArrayEmbeddedState.EMPTY, migrated);
    }

    private static Function<ResourceLocation, Optional<BigBroArrayMachineCatalog.Entry>> processorResolver(
            ResourceLocation expectedId, int tier) {
        MachineDefinition definition = new MachineDefinition(expectedId);
        definition.setTier(tier);
        BigBroArrayMachineCatalog.Entry entry = new BigBroArrayMachineCatalog.Entry(
                definition, () -> null, tier, BigBroArrayMode.PROCESSOR);
        return id -> expectedId.equals(id) ? Optional.of(entry) : Optional.empty();
    }
}
