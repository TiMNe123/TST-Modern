package com.tstmodern.registry.machine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

class IncompactCyclotronLocalizationTest {

    @Test
    void exposesFormattedNamesAndEveryUsedTooltipInBothLocales() throws IOException {
        JsonObject english = lang("en_us");
        JsonObject vietnamese = lang("vi_vn");
        String[] keys = {
                "block.tstmodern.incompact_cyclotron",
                "block.tstmodern.quantum_frame",
                "block.tstmodern.compact_cyclotron_coil",
                "block.tstmodern.dense_cyclotron_outer_casing",
                "item.tstmodern.hydrogen_ion",
                "item.tstmodern.proton",
                "item.tstmodern.electron",
                "item.tstmodern.neutron",
                "item.tstmodern.unknown_particle",
                "tstmodern.material.neptunium_238.decay",
                "tstmodern.cyclotron",
                "recipetype.tstmodern.cyclotron",
                "gtceu.recipe_type.tstmodern.cyclotron"
        };
        for (String key : keys) {
            assertTrue(english.has(key), "en_us missing " + key);
            assertTrue(vietnamese.has(key), "vi_vn missing " + key);
        }
        assertTrue(english.get("block.tstmodern.incompact_cyclotron").getAsString().startsWith("§5§l"));
        assertTrue(vietnamese.get("block.tstmodern.incompact_cyclotron").getAsString().startsWith("§5§l"));

        String definition = Files.readString(Path.of(
                "src/main/java/com/tstmodern/registry/machine/IncompactCyclotronDefinition.java"));
        for (int index = 0; index < 9; index++) {
            String key = "tstmodern.machine.incompact_cyclotron.tooltip." + index;
            assertTrue(definition.contains("Component.translatable(\"" + key + "\")"));
            assertTrue(english.has(key), "en_us missing " + key);
            assertTrue(vietnamese.has(key), "vi_vn missing " + key);
        }
        assertEquals(9, occurrences(definition, "tstmodern.machine.incompact_cyclotron.tooltip."));
    }

    private static JsonObject lang(String locale) throws IOException {
        return JsonParser.parseString(Files.readString(Path.of(
                "src/main/resources/assets/tstmodern/lang/" + locale + ".json"))).getAsJsonObject();
    }

    private static int occurrences(String text, String needle) {
        return (text.length() - text.replace(needle, "").length()) / needle.length();
    }
}
