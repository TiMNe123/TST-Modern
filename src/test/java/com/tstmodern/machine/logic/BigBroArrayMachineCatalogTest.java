package com.tstmodern.machine.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;

import net.minecraft.resources.ResourceLocation;

import org.junit.jupiter.api.Test;

class BigBroArrayMachineCatalogTest {

    @Test
    void catalogUsesOnlyConcreteDefinitionsSuppliedByRegistry() {
        MachineDefinition macerator = definition("gtceu:lv_macerator", GTValues.LV);
        MachineDefinition assembler = definition("gtceu:hv_assembler", GTValues.HV);
        MachineDefinition generator = definition("gtceu:lv_combustion", GTValues.LV);
        MachineDefinition massFabricator = definition("tstmodern:uhv_mass_fabricator", GTValues.UHV);
        MachineDefinition unsupported = definition("gtceu:lv_packager", GTValues.LV);
        MachineDefinition unsupportedGeneratorTier = definition("gtceu:iv_combustion", GTValues.IV);

        Map<ResourceLocation, BigBroArrayMachineCatalog.Entry> catalog =
                BigBroArrayMachineCatalog.buildCatalogForDefinitions(List.of(
                        macerator, assembler, generator, massFabricator, unsupported, unsupportedGeneratorTier));

        assertEquals(4, catalog.size());
        assertEntry(catalog, macerator, BigBroArrayMode.PROCESSOR, GTValues.LV);
        assertEntry(catalog, assembler, BigBroArrayMode.PROCESSOR, GTValues.HV);
        assertEntry(catalog, generator, BigBroArrayMode.GENERATOR, GTValues.LV);
        assertEntry(catalog, massFabricator, BigBroArrayMode.PROCESSOR, GTValues.UHV);
        assertFalse(catalog.containsKey(unsupported.getId()));
        assertFalse(catalog.containsKey(unsupportedGeneratorTier.getId()));
    }

    @Test
    void duplicateConcreteDefinitionsAreRejected() {
        MachineDefinition first = definition("gtceu:lv_macerator", GTValues.LV);
        MachineDefinition duplicate = definition("gtceu:lv_macerator", GTValues.LV);

        assertThrows(IllegalStateException.class,
                () -> BigBroArrayMachineCatalog.buildCatalogForDefinitions(List.of(first, duplicate)));
    }

    private static MachineDefinition definition(String id, int tier) {
        MachineDefinition definition = new MachineDefinition(new ResourceLocation(id));
        definition.setTier(tier);
        return definition;
    }

    private static void assertEntry(
            Map<ResourceLocation, BigBroArrayMachineCatalog.Entry> catalog,
            MachineDefinition definition,
            BigBroArrayMode expectedMode,
            int expectedTier) {
        BigBroArrayMachineCatalog.Entry entry = catalog.get(definition.getId());
        assertTrue(entry != null, "Expected " + definition.getId());
        assertSame(definition, entry.definition());
        assertEquals(expectedMode, entry.mode());
        assertEquals(expectedTier, entry.tier());
    }
}
