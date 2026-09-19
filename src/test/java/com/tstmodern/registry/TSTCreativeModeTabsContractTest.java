package com.tstmodern.registry;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class TSTCreativeModeTabsContractTest {

    @Test
    void tstItemsShareOneAutomaticallyPopulatedCreativeTab() throws IOException {
        String tabs = Files.readString(Path.of(
                "src/main/java/com/tstmodern/registry/TSTCreativeModeTabs.java"));
        String mod = Files.readString(Path.of("src/main/java/com/tstmodern/TSTModern.java"));
        String english = Files.readString(Path.of("src/main/resources/assets/tstmodern/lang/en_us.json"));
        String vietnamese = Files.readString(Path.of("src/main/resources/assets/tstmodern/lang/vi_vn.json"));

        assertTrue(tabs.contains("TSTModern.MOD_ID.equals"));
        assertTrue(tabs.contains("ForgeRegistries.ITEMS.getValues()"));
        assertTrue(mod.contains("TSTCreativeModeTabs.register(modBus)"));
        assertTrue(english.contains("\"itemGroup.tstmodern.main\""));
        assertTrue(vietnamese.contains("\"itemGroup.tstmodern.main\""));
    }
}
