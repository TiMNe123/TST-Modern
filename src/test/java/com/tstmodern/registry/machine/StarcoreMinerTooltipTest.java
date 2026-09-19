package com.tstmodern.registry.machine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

final class StarcoreMinerTooltipTest {
    @Test
    void followsTheEstablishedMulticolorControllerTooltipLayout() throws Exception {
        JsonObject english = lang("en_us");
        JsonObject vietnamese = lang("vi_vn");
        List<String> colors = List.of("§7", "§3", "§f", "§6", "§b", "§e", "§a", "§5", "§d");

        assertEquals("§3§lStarcore Miner§r",
                english.get("block.tstmodern.starcore_miner").getAsString());
        assertEquals("§3§lMáy Khai Thác Lõi Sao§r",
                vietnamese.get("block.tstmodern.starcore_miner").getAsString());
        assertEquals("§bDimensional Bridge Casing§r",
                english.get("block.tstmodern.dimensional_bridge_casing").getAsString());
        assertEquals("§bVỏ Cầu Nối Chiều Không Gian§r",
                vietnamese.get("block.tstmodern.dimensional_bridge_casing").getAsString());

        for (int index = 0; index < colors.size(); index++) {
            String key = "tstmodern.machine.starcore_miner.tooltip." + index;
            assertTrue(english.get(key).getAsString().startsWith(colors.get(index)), key + " en_us");
            assertTrue(vietnamese.get(key).getAsString().startsWith(colors.get(index)), key + " vi_vn");
            assertTrue(english.get(key).getAsString().endsWith("§r"), key + " en_us reset");
            assertTrue(vietnamese.get(key).getAsString().endsWith("§r"), key + " vi_vn reset");
        }
        for (int index = 4; index <= 7; index++) {
            String key = "tstmodern.machine.starcore_miner.tooltip." + index;
            assertTrue(english.get(key).getAsString().contains("§7:"), key + " en_us gray body");
            assertTrue(vietnamese.get(key).getAsString().contains("§7:"), key + " vi_vn gray body");
        }
        assertEquals("§dTwist Space Technology - Modern Edition§r",
                english.get("tstmodern.machine.starcore_miner.tooltip.8").getAsString());
        assertEquals("§dTwist Space Technology - Phiên bản Hiện đại§r",
                vietnamese.get("tstmodern.machine.starcore_miner.tooltip.8").getAsString());
        assertTrue(english.get("tstmodern.machine.starcore_miner.tooltip.6").getAsString().contains("Y=21"));
        assertTrue(english.get("tstmodern.machine.starcore_miner.tooltip.7").getAsString()
                .contains("Astral Array Fabricator"));
    }

    private static JsonObject lang(String code) throws Exception {
        return JsonParser.parseString(Files.readString(
                Path.of("src/main/resources/assets/tstmodern/lang/" + code + ".json"))).getAsJsonObject();
    }
}
