package com.tstmodern.registry.machine;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Pattern;

import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

class MachineDisplayNameColorTest {
    private static final Pattern REGISTRATION = Pattern.compile(
            "\\.(?:multiblock|machine)\\(\"([^\"]+)\"");

    @Test
    void everyRegisteredMachineHasAColoredLocalizedName() throws Exception {
        var ids = new TreeSet<>(List.of(
                "uhv_mass_fabricator", "uev_mass_fabricator", "uiv_mass_fabricator",
                "uxv_mass_fabricator", "opv_mass_fabricator", "max_mass_fabricator"));
        try (var files = Files.list(Path.of("src/main/java/com/tstmodern/registry/machine"))) {
            for (Path file : files.filter(path -> path.toString().endsWith("Definition.java")).toList()) {
                var matcher = REGISTRATION.matcher(Files.readString(file));
                while (matcher.find()) ids.add(matcher.group(1));
            }
        }

        for (String locale : List.of("en_us", "vi_vn")) {
            var lang = JsonParser.parseString(Files.readString(Path.of(
                    "src/main/resources/assets/tstmodern/lang/" + locale + ".json"))).getAsJsonObject();
            for (String id : ids) {
                var entry = lang.get("block.tstmodern." + id);
                assertNotNull(entry, locale + ":" + id);
                String name = entry.getAsString();
                assertTrue(name.startsWith("§") && name.endsWith("§r"), locale + ":" + id);
            }
        }
    }
}
