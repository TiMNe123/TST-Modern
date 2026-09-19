package com.tstmodern.machine.logic;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

class BigBroArrayLocalizationContractTest {

    @Test
    void testAllKeysAreLocalized() throws IOException {
        Path enUs = Paths.get("src/main/resources/assets/tstmodern/lang/en_us.json");
        Path viVn = Paths.get("src/main/resources/assets/tstmodern/lang/vi_vn.json");
        
        String enContent = Files.readString(enUs);
        String viContent = Files.readString(viVn);

        for (String key : new String[] {
                "tstmodern.machine.big_bro_array.status.addon_directions",
                "tstmodern.machine.big_bro_array.status.present",
                "tstmodern.machine.big_bro_array.status.absent",
                "tstmodern.machine.big_bro_array.status.empty_template"
        }) {
            assertTrue(enContent.contains("\"" + key + "\""), "Missing key in en_us.json: " + key);
            assertTrue(viContent.contains("\"" + key + "\""), "Missing key in vi_vn.json: " + key);
        }

        Path machineSrc = Paths.get("src/main/java/com/tstmodern/machine/BigBroArrayMachine.java");
        Path transferSrc = Paths.get("src/main/java/com/tstmodern/machine/logic/BigBroArrayMachineTransfer.java");

        String machineCode = Files.readString(machineSrc);
        String transferCode = Files.readString(transferSrc);

        Pattern keyPattern = Pattern.compile("\"(tstmodern\\.machine\\.big_bro_array\\.status\\.[a-z_.]+)\"");
        
        Matcher m1 = keyPattern.matcher(machineCode);
        while (m1.find()) {
            String key = m1.group(1);
            assertTrue(enContent.contains("\"" + key + "\""), "Missing key in en_us.json: " + key);
            assertTrue(viContent.contains("\"" + key + "\""), "Missing key in vi_vn.json: " + key);
        }

        Matcher m2 = keyPattern.matcher(transferCode);
        while (m2.find()) {
            String key = m2.group(1);
            assertTrue(enContent.contains("\"" + key + "\""), "Missing key in en_us.json: " + key);
            assertTrue(viContent.contains("\"" + key + "\""), "Missing key in vi_vn.json: " + key);
        }
        
        // Check enum OperationalStatus which might return its name as a key part
        for (OperationalStatus status : OperationalStatus.values()) {
            String key = status.messageKey();
            if (key != null && key.startsWith("tstmodern.machine.big_bro_array.status")) {
                assertTrue(enContent.contains("\"" + key + "\""), "Missing key in en_us.json: " + key);
                assertTrue(viContent.contains("\"" + key + "\""), "Missing key in vi_vn.json: " + key);
            }
        }
    }
}
