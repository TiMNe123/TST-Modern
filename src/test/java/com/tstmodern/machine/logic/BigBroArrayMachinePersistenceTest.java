package com.tstmodern.machine.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.gregtechceu.gtceu.api.GTValues;
import java.util.Optional;
import java.util.function.Function;

import com.gregtechceu.gtceu.api.machine.MachineDefinition;

import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class BigBroArrayMachinePersistenceTest {

    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void version1StateRoundTrip() {
        CompoundTag itemTag = new CompoundTag();
        itemTag.putString("CustomField", "SpecialValue");

        BigBroArrayEmbeddedState original = new BigBroArrayEmbeddedState(
                1,
                new ResourceLocation("gtceu", "iv_macerator"),
                BigBroArrayMode.PROCESSOR,
                GTValues.IV,
                32,
                itemTag
        );

        CompoundTag persisted = new CompoundTag();
        persisted.put(BigBroArrayEmbeddedState.NBT_KEY, original.writeToNbt());

        CompoundTag readStateTag = persisted.getCompound(BigBroArrayEmbeddedState.NBT_KEY);
        BigBroArrayEmbeddedState restored = BigBroArrayEmbeddedState.readFromNbt(
                readStateTag, processorResolver(original.definitionId(), GTValues.IV));

        assertFalse(restored.isEmpty());
        assertTrue(restored.isValid());
        assertEquals(1, restored.version());
        assertEquals(new ResourceLocation("gtceu", "iv_macerator"), restored.definitionId());
        assertEquals(BigBroArrayMode.PROCESSOR, restored.mode());
        assertEquals(GTValues.IV, restored.tier());
        assertEquals(32, restored.count());
        assertNotNull(restored.itemTag());
        assertEquals("SpecialValue", restored.itemTag().getString("CustomField"));
    }

    @Test
    void staleIdPreservedSafely() {
        BigBroArrayEmbeddedState original = new BigBroArrayEmbeddedState(
                1,
                new ResourceLocation("tstmodern", "removed_machine"),
                BigBroArrayMode.PROCESSOR,
                GTValues.LV,
                8,
                null
        );

        CompoundTag tag = original.writeToNbt();
        BigBroArrayEmbeddedState restored = BigBroArrayEmbeddedState.readFromNbt(tag);

        assertFalse(restored.isEmpty());
        assertFalse(restored.isValid());
        assertEquals(new ResourceLocation("tstmodern", "removed_machine"), restored.definitionId());
        assertEquals(8, restored.count());
        assertNull(restored.mode());
        assertEquals(0, restored.tier());
    }

    @Test
    void inventedLegacyMachineIdIsNotAcceptedAsRealSaveData() {
        CompoundTag legacyTag = new CompoundTag();
        legacyTag.putString("embeddedMachineId", "gtceu:hv_assembler");
        legacyTag.putInt("embeddedCount", 16);
        legacyTag.putInt("embeddedTier", GTValues.LV); // Corrupt/outdated legacy tier
        legacyTag.putString("embeddedMode", "GENERATOR"); // Corrupt/outdated legacy mode

        BigBroArrayEmbeddedState migrated = BigBroArrayEmbeddedState.migrateFromLegacy(legacyTag);

        assertEquals(BigBroArrayEmbeddedState.EMPTY, migrated);
    }

    @Test
    void legacySerializedItemStackMigratedSuccessfully() {
        com.gregtechceu.gtceu.api.machine.MachineDefinition def = com.gregtechceu.gtceu.api.registry.GTRegistries.MACHINES.get(new ResourceLocation("gtceu", "iv_macerator"));
        if (def != null) {
            net.minecraft.world.item.ItemStack stack = def.asStack();
            CompoundTag itemTag = new CompoundTag();
            itemTag.putString("TestTag", "Value");
            stack.setTag(itemTag);

            CompoundTag stackNbt = new CompoundTag();
            stack.save(stackNbt);

            CompoundTag legacyTag = new CompoundTag();
            legacyTag.put("embeddedMachineStack", stackNbt);
            legacyTag.putInt("embeddedCount", 8);

            BigBroArrayEmbeddedState migrated = BigBroArrayEmbeddedState.migrateFromLegacy(legacyTag);
            assertFalse(migrated.isEmpty());
            assertTrue(migrated.isValid());
            assertEquals(new ResourceLocation("gtceu", "iv_macerator"), migrated.definitionId());
            assertEquals(8, migrated.count());
            assertEquals(GTValues.IV, migrated.tier());
            assertNotNull(migrated.itemTag());
            assertEquals("Value", migrated.itemTag().getString("TestTag"));
        }
    }

    @Test
    void emptyOrCorruptedTagsProduceEmptyState() {
        assertEquals(BigBroArrayEmbeddedState.EMPTY, BigBroArrayEmbeddedState.readFromNbt(null));
        assertEquals(BigBroArrayEmbeddedState.EMPTY, BigBroArrayEmbeddedState.readFromNbt(new CompoundTag()));
        assertEquals(BigBroArrayEmbeddedState.EMPTY, BigBroArrayEmbeddedState.migrateFromLegacy(null));
        assertEquals(BigBroArrayEmbeddedState.EMPTY, BigBroArrayEmbeddedState.migrateFromLegacy(new CompoundTag()));
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
