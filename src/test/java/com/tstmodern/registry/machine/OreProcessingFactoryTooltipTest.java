package com.tstmodern.registry.machine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

final class OreProcessingFactoryTooltipTest {
    @Test
    void followsTheEstablishedMulticolorControllerTooltipLayout() throws Exception {
        JsonObject english = lang("en_us");
        JsonObject vietnamese = lang("vi_vn");
        List<String> colors = List.of("§7", "§3", "§f", "§6", "§b", "§e", "§a", "§d");

        assertEquals("§3§lOre Processing Factory§r",
                english.get("block.tstmodern.ore_processing_factory").getAsString());
        assertEquals("§3§lNhà Máy Xử Lý Quặng§r",
                vietnamese.get("block.tstmodern.ore_processing_factory").getAsString());
        assertEquals("§bAdvanced Iridium Casing§r",
                english.get("block.tstmodern.advanced_iridium_casing").getAsString());
        assertEquals("§bVỏ Iridium Cao Cấp§r",
                vietnamese.get("block.tstmodern.advanced_iridium_casing").getAsString());
        assertEquals("§cHigh Power Casing§r",
                english.get("block.tstmodern.high_power_casing").getAsString());
        assertEquals("§cVỏ Khối Công Suất Cao§r",
                vietnamese.get("block.tstmodern.high_power_casing").getAsString());
        assertEquals("§dElectromagnetic Computer Coil§r",
                english.get("block.tstmodern.electromagnetic_computer_coil").getAsString());
        assertEquals("§dCuộn máy tính điện từ§r",
                vietnamese.get("block.tstmodern.electromagnetic_computer_coil").getAsString());
        assertTooltipText(english, List.of(
                "§7Controller block for the General Ore Processing Factory TST§r",
                "§3§lOre Processor§r",
                "§fEngineering is the art of directing the great sources of power in nature for the use and convenience of man.§r",
                "§6Processing§7: The ores will line up and go in through the entrance and out through the exit.§r",
                "§bOverclock§7: This machine will not do overclock. Progress time is always §e6.4s§7 (default).§r",
                "§eParallel§7: It will process as many inputs as possible at once, if power allow.§r",
                "§aLubricant§7: Consume §e3200L§7 Lubricant every §e12.8s§7 (default).§r",
                "§dTwist Space Technology - Modern Edition§r"));
        assertTooltipText(vietnamese, List.of(
                "§7Khối điều khiển của Nhà Máy Xử Lý Quặng Tổng Hợp TST§r",
                "§3§lMáy xử lý quặng§r",
                "§fKỹ thuật là nghệ thuật định hướng những nguồn năng lượng vĩ đại trong tự nhiên để phục vụ và mang lại tiện ích cho con người.§r",
                "§6Xử lý§7: Quặng sẽ xếp hàng, đi vào từ lối vào rồi đi ra từ lối ra.§r",
                "§bÉp xung§7: Máy này không ép xung. Thời gian xử lý luôn là §e6,4 giây§7 (mặc định).§r",
                "§eSong song§7: Máy sẽ xử lý đồng thời nhiều nguyên liệu đầu vào nhất có thể, nếu nguồn điện cho phép.§r",
                "§aDầu bôi trơn§7: Tiêu thụ §e3200 L§7 Dầu bôi trơn mỗi §e12,8 giây§7 (mặc định).§r",
                "§dTwist Space Technology - Phiên bản Hiện đại§r"));
        for (int index = 0; index < colors.size(); index++) {
            String key = "tstmodern.machine.ore_processing_factory.tooltip." + index;
            assertTrue(english.get(key).getAsString().startsWith(colors.get(index)), key + " en_us");
            assertTrue(vietnamese.get(key).getAsString().startsWith(colors.get(index)), key + " vi_vn");
        }
        for (int index = 3; index <= 6; index++) {
            String key = "tstmodern.machine.ore_processing_factory.tooltip." + index;
            assertTrue(english.get(key).getAsString().contains("§7:"), key + " en_us gray body");
            assertTrue(vietnamese.get(key).getAsString().contains("§7:"), key + " vi_vn gray body");
        }
        assertEquals("§dTwist Space Technology - Modern Edition§r",
                english.get("tstmodern.machine.ore_processing_factory.tooltip.7").getAsString());
        assertEquals("§dTwist Space Technology - Phiên bản Hiện đại§r",
                vietnamese.get("tstmodern.machine.ore_processing_factory.tooltip.7").getAsString());
    }

    private static void assertTooltipText(JsonObject lang, List<String> expected) {
        for (int index = 0; index < expected.size(); index++) {
            assertEquals(expected.get(index),
                    lang.get("tstmodern.machine.ore_processing_factory.tooltip." + index).getAsString());
        }
    }

    private static JsonObject lang(String locale) throws Exception {
        return JsonParser.parseString(Files.readString(Path.of(
                "src/main/resources/assets/tstmodern/lang/" + locale + ".json"))).getAsJsonObject();
    }
}
