package com.tstmodern.registry.machine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

class AstralComputingArrayTooltipTest {

    @Test
    void followsTheEstablishedColoredControllerTooltipLayout() throws Exception {
        var lang = JsonParser.parseString(Files.readString(Path.of(
                "src/main/resources/assets/tstmodern/lang/en_us.json"))).getAsJsonObject();
        assertEquals("§d§lAstral Computing Array§r",
                lang.get("block.tstmodern.astral_computing_array").getAsString());
        assertEquals("§7Controller block for the Astral Computing Array§r",
                lang.get("tstmodern.machine.astral_computing_array.tooltip.0").getAsString());
        assertTrue(lang.get("tstmodern.machine.astral_computing_array.tooltip.2").getAsString()
                .startsWith("§6Requirements§7:"));
        assertEquals("§dTwist Space Technology - Modern Edition§r",
                lang.get("tstmodern.machine.astral_computing_array.tooltip.6").getAsString());

        String definition = Files.readString(Path.of(
                "src/main/java/com/tstmodern/registry/machine/AstralComputingArrayDefinition.java"));
        assertTrue(definition.contains("astral_computing_array.tooltip.6"));
    }

    @Test
    void givesEveryAstralCasingAColoredDisplayName() throws Exception {
        var lang = JsonParser.parseString(Files.readString(Path.of(
                "src/main/resources/assets/tstmodern/lang/en_us.json"))).getAsJsonObject();
        for (String id : List.of(
                "field_restriction_coil_t1", "compact_fusion_coil_t0",
                "space_elevator_base_casing", "space_elevator_support_structure",
                "space_elevator_internal_structure", "computer_casing", "computer_heat_vent",
                "advanced_computer_casing", "electromagnetic_computer_coil", "containment_casing",
                "radiation_protection_steel_frame", "astral_pylon_casing")) {
            String name = lang.get("block.tstmodern." + id).getAsString();
            assertTrue(name.startsWith("§") && name.endsWith("§r"), id);
        }
    }
}
