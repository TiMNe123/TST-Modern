package com.tstmodern.registry.machine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;

final class DisassemblerDefinitionContractTest {

    @Test
    void selectClosedIAbilityCandidatesRetainsOnlyAllowedAbilitiesWithoutDisallowedIntersections() {
        String allowedBlock1 = "allowed_import_item";
        String allowedBlock2 = "allowed_export_fluid";
        String sharedDisallowedBlock = "shared_energy_hatch";
        String disallowedBlock = "disallowed_maintenance";

        Map<PartAbility, Set<String>> abilityBlocks = Map.of(
                PartAbility.IMPORT_ITEMS, Set.of(allowedBlock1),
                PartAbility.EXPORT_FLUIDS, Set.of(allowedBlock2, sharedDisallowedBlock),
                PartAbility.INPUT_ENERGY, Set.of(sharedDisallowedBlock),
                PartAbility.MAINTENANCE, Set.of(disallowedBlock));

        Set<String> result = DisassemblerDefinition.PartAbilities.selectClosedIAbilityCandidates(abilityBlocks);

        assertTrue(result.contains(allowedBlock1));
        assertTrue(result.contains(allowedBlock2));
        assertFalse(result.contains(sharedDisallowedBlock), "Shared energy block should be disallowed");
        assertFalse(result.contains(disallowedBlock), "Disallowed maintenance block should not be in result");
        assertEquals(2, result.size());
    }

    @Test
    void compatibleExportFluidMetadataAliasesAreNotDisallowed() {
        String fluidExportBlock = "fluid_export_hatch";

        Map<PartAbility, Set<String>> abilityBlocks = Map.of(
                PartAbility.EXPORT_FLUIDS, Set.of(fluidExportBlock),
                PartAbility.EXPORT_FLUIDS_4X, Set.of(fluidExportBlock));

        Set<String> result = DisassemblerDefinition.PartAbilities.selectClosedIAbilityCandidates(abilityBlocks);

        assertTrue(result.contains(fluidExportBlock), "Export fluid metadata variant must remain allowed");
        assertEquals(1, result.size());
    }
}
