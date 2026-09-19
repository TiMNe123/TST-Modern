package com.tstmodern.registry.machine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

final class DraconicCrucibleIntegrationContractTest {
    private static final Path RESOURCES = Path.of("src/main/resources");

    @Test
    void keepsLocalizedPalettePartColorsAndFooter() throws Exception {
        for (String locale : List.of("en_us", "vi_vn")) {
            JsonObject lang = json(RESOURCES.resolve("assets/tstmodern/lang/" + locale + ".json"));
            assertEquals("§c§l", lang.get("block.tstmodern.draconic_crucible").getAsString().substring(0, 4));
            for (String key : List.of(
                    "block.tstmodern.draconic_crucible_core",
                    "block.tstmodern.draconium_ore",
                    "item.tstmodern.draconium_core",
                    "item.tstmodern.dragon_heart",
                    "material.tstmodern.draconium",
                    "material.tstmodern.awakened_draconium")) {
                String value = lang.get(key).getAsString();
                assertTrue(value.startsWith("§") && value.endsWith("§r"), locale + ":" + key);
            }

            Set<Character> accents = IntStream.rangeClosed(1, 6)
                    .mapToObj(index -> lang.get("tstmodern.machine.draconic_crucible.tooltip." + index)
                            .getAsString())
                    .filter(value -> value.length() >= 2 && value.charAt(0) == '§' && value.charAt(1) != '7')
                    .map(value -> value.charAt(1))
                    .collect(Collectors.toSet());
            assertTrue(accents.size() >= 3, locale + " tooltip palette");
            assertTrue(lang.get("tstmodern.machine.draconic_crucible.tooltip.7").getAsString()
                    .contains("Twist Space Technology"));
            assertTrue(lang.get("tstmodern.machine.draconic_crucible.status.animation_starting")
                    .getAsString().endsWith("..."));
        }
    }

    @Test
    void prefersDraconicEvolutionItemsAndRetainsFallbacks() throws Exception {
        for (String item : List.of(
                "draconium_ore", "draconium_dust", "draconium_ingot", "draconium_core",
                "dragon_heart", "awakened_draconium_nugget", "awakened_draconium_ingot")) {
            JsonObject tag = json(RESOURCES.resolve("data/tstmodern/tags/items/preferred/" + item + ".json"));
            var values = tag.getAsJsonArray("values");
            JsonObject external = values.get(0).getAsJsonObject();
            assertEquals("draconicevolution:" + externalId(item), external.get("id").getAsString());
            assertTrue(!external.get("required").getAsBoolean());
            assertEquals(fallbackId(item), values.get(1).getAsString());
        }
    }

    @Test
    void gatesFallbackOreAndStarcoreIntegrationToTheEnd() throws Exception {
        String worldgen = Files.readString(Path.of("src/main/java/com/tstmodern/registry/TSTWorldgen.java"));
        assertTrue(worldgen.contains("isLoaded(\"draconicevolution\")"));
        assertTrue(worldgen.contains(".clusterSize(UniformInt.of(16, 24))"));
        assertTrue(worldgen.contains(".heightRangeUniform(10, 80)"));
        assertTrue(worldgen.contains(".biomes(BiomeTags.IS_END)"));

        String starcore = Files.readString(Path.of(
                "src/main/java/com/tstmodern/recipe/starcore/StarcoreMinerRecipeLogic.java"));
        assertTrue(starcore.contains("level.dimension() == Level.END"));
        assertTrue(starcore.contains("new ResourceLocation(\"draconicevolution\", \"end_draconium_ore\")"));
    }

    @Test
    void registersFallbackOreWithTheGtceuOreRenderer() throws Exception {
        String blocks = Files.readString(Path.of("src/main/java/com/tstmodern/registry/TSTBlocks.java"));
        assertTrue(blocks.contains("new OreBlock("));
        String materials = Files.readString(Path.of("src/main/java/com/tstmodern/registry/TSTMaterials.java"))
                .replace("\r\n", "\n");
        assertTrue(materials.contains(".ingot()\n                .ore()\n                .color(0x6F32A8)"));
    }

    @Test
    void allowsTenFlexibleBusesAndAtMostTwoEnergyInputs() throws Exception {
        String definition = Files.readString(Path.of(
                "src/main/java/com/tstmodern/registry/machine/DraconicCrucibleDefinition.java"))
                .replace("\r\n", "\n");
        String structure = Files.readString(Path.of(
                "src/main/java/com/tstmodern/registry/machine/DraconicCrucibleStructure.java"));
        String machine = Files.readString(Path.of(
                "src/main/java/com/tstmodern/machine/DraconicCrucibleMachine.java"));

        assertTrue(definition.contains("setMaxGlobalLimited(10)"));
        assertTrue(definition.contains(".where('R', blocks(TSTBlocks.RADIANT_NAQUADAH_ALLOY_CASING.get())\n"
                + "                                .or(flexibleParts))"));
        assertTrue(!structure.contains("replace(cells, 27, 1, 0, 'I')"));
        assertTrue(!structure.contains("replace(cells, 33, 1, 0, 'O')"));
        assertTrue(!structure.contains("replace(cells, 36, 1, 0, 'E')"));
        assertTrue(machine.contains("energyInputs < 1 || energyInputs > 2"));
        assertTrue(machine.contains("itemInputs < 1 || itemOutputs < 1"));
    }

    @Test
    void rendersAndShakesTheExactFireDragonEggWhileBreathing() throws Exception {
        String blocks = Files.readString(Path.of("src/main/java/com/tstmodern/registry/TSTBlocks.java"));
        String renderer = Files.readString(Path.of(
                "src/main/java/com/tstmodern/client/renderer/DraconicCrucibleDragonRender.java"));
        String machine = Files.readString(Path.of(
                "src/main/java/com/tstmodern/machine/DraconicCrucibleMachine.java"));
        JsonObject blockstate = json(RESOURCES.resolve(
                "assets/tstmodern/blockstates/draconic_crucible_core.json"));
        JsonObject model = json(RESOURCES.resolve(
                "assets/tstmodern/models/block/draconic_crucible_core.json"));

        assertTrue(blocks.contains("new DraconicCrucibleCoreBlock"));
        assertTrue(blockstate.getAsJsonObject("variants").has("formed=false"));
        assertTrue(blockstate.getAsJsonObject("variants").has("formed=true"));
        assertEquals("tstmodern:block/draconic_crucible_fire_dragon_egg",
                model.getAsJsonObject("textures").get("egg").getAsString());
        assertTrue(renderer.contains("TSTBlocks.DRACONIC_CRUCIBLE_CORE.get().defaultBlockState()"));
        assertTrue(renderer.contains("getModelRenderer().renderModel"));
        assertTrue(renderer.contains("RenderType.entitySolid(TextureAtlas.LOCATION_BLOCKS)"));
        assertTrue(!renderer.contains("renderSingleBlock"));
        assertTrue(!renderer.contains("createEggModel"));
        assertTrue(renderer.contains("renderCoreEgg"));
        assertTrue(renderer.contains("LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY"));
        assertTrue(renderer.contains("isWorking && breathStrength > 0.0F"));
        assertTrue(machine.contains("setCoreFormed(true)"));
        assertTrue(machine.contains("setCoreFormed(false)"));
        assertTrue(Files.isRegularFile(RESOURCES.resolve(
                "assets/tstmodern/textures/block/draconic_crucible_fire_dragon_egg.png")));
    }

    @Test
    void rendersTheExactStageFiveIceAndFireDragon() throws Exception {
        String renderer = Files.readString(Path.of(
                "src/main/java/com/tstmodern/client/renderer/DraconicCrucibleDragonRender.java"));
        String model = Files.readString(Path.of(
                "src/main/java/com/tstmodern/client/renderer/DraconicCrucibleDragonModel.java"));
        String machine = Files.readString(Path.of(
                "src/main/java/com/tstmodern/machine/DraconicCrucibleMachine.java"));
        assertTrue(renderer.contains("DraconicCrucibleDragonModel"));
        assertTrue(renderer.contains("textures/entity/draconic_crucible_dragon.png"));
        assertTrue(renderer.contains("textures/entity/draconic_crucible_dragon_sleeping.png"));
        assertTrue(renderer.contains("textures/entity/draconic_crucible_dragon_glow.png"));
        assertTrue(renderer.contains("textures/particles/draconic_crucible_dragon_flame.png"));
        assertTrue(renderer.contains("STAGE_FIVE_SCALE = 10.0F"));
        assertTrue(renderer.contains("RenderType.entityCutoutNoCull(texture)"));
        assertTrue(renderer.contains("RenderType.eyes(DRAGON_GLOW_TEXTURE)"));
        assertTrue(renderer.contains("if (!sleeping)"));
        assertTrue(renderer.contains("renderDragonFlameStream"));
        assertTrue(!renderer.contains("renderBeamCylinder"));
        assertTrue(!renderer.contains("OverlayTexture.pack(0.0F, true)"));
        assertTrue(Files.isRegularFile(
                RESOURCES.resolve("assets/tstmodern/textures/particles/draconic_crucible_dragon_flame.png")));
        assertTrue(renderer.contains("DRAGON_YAW_OFFSET = 90.0F"));
        assertTrue(renderer.contains("-back.toYRot() + DRAGON_YAW_OFFSET"));
        assertTrue(renderer.contains("DRAGON_UP_OFFSET = -5.75D"));
        assertTrue(renderer.contains("DRAGON_BACK_OFFSET = 0.0D"));
        assertTrue(renderer.contains("DRAGON_RIGHT_OFFSET, DRAGON_UP_OFFSET, DRAGON_BACK_OFFSET"));
        assertTrue(renderer.contains("if (isWorking && breathStrength > 0.0F)"));
        assertTrue(!renderer.contains("isWorking ? 72 : 36"));
        assertTrue(!renderer.contains("ModelLayers.ENDER_DRAGON"));
        assertTrue(!renderer.contains("new EnderDragon"));
        assertTrue(model.contains("ZipInputStream"));
        assertTrue(model.contains("draconic_crucible_fire_dragon_ground.tbl"));
        assertTrue(model.contains("draconic_crucible_fire_dragon_sleeping.tbl"));
        assertTrue(model.contains("draconic_crucible_fire_dragon_charge.tbl"));
        assertTrue(model.contains("draconic_crucible_fire_dragon_breath.tbl"));
        assertTrue(model.contains("draconic_crucible_fire_dragon_flight"));
        assertTrue(model.contains("EXPECTED_PART_COUNT = 104"));
        assertTrue(model.contains("TAIL_PITCH"));
        assertTrue(model.contains("PERCHED_NECK_PITCH = { -45.0F, -30.0F, 30.0F, 80.0F }"));
        assertTrue(model.contains("Vector4f mouth = matrix.transform(new Vector4f(0.0F, 2.0F / 16.0F, -6.5F / 16.0F, 1.0F))"));
        assertTrue(renderer.contains("target = origin.add(0.0D, 1.0D, 0.0D)"));
        assertTrue(model.contains("TAIL_PITCH = { -24.0F, -9.0F, 3.0F, -10.0F, -20.0F }"));
        assertTrue(model.contains("TAIL_YAW = { -18.0F, -22.0F, -26.0F, -30.0F, -34.0F }"));
        assertTrue(model.contains("TAIL_LENGTH_SCALE = 1.45F"));
        assertTrue(model.contains("PERCHED_THIGH_PITCH = -18.0F"));
        assertTrue(model.contains("PERCHED_LEG_PITCH = 10.0F"));
        assertTrue(model.contains("applyPerchedPose"));
        assertTrue(model.contains("TAIL_LENGTH_SCALE"));
        assertTrue(model.contains("PERCHED_THIGH_PITCH * DEG_TO_RAD * blend"));
        assertTrue(model.contains("75.0F * DEG_TO_RAD"));
        assertTrue(machine.contains("STARTUP_TICKS ="));
        assertTrue(machine.contains("WAKE_TICKS = 10"));
        assertTrue(machine.contains("TAKEOFF_TICKS = 30"));
        assertTrue(machine.contains("ORBIT_TICKS = 100"));
        assertTrue(machine.contains("LANDING_TICKS = 40"));
        assertTrue(machine.contains("CHARGE_TICKS = 20"));
        assertTrue(machine.contains("WAKE_TICKS + TAKEOFF_TICKS + ORBIT_TICKS + LANDING_TICKS + CHARGE_TICKS"));
        assertTrue(machine.contains("if (dragonStartupTicks < STARTUP_TICKS)"));
        assertTrue(machine.contains("return false;"));
        assertTrue(machine.contains("status.animation_starting"));
        assertTrue(renderer.contains("FLIGHT_YAW_CORRECTION = 180.0F"));
        assertTrue(renderer.contains("IDLE_UP_OFFSET = -7.0D"));
        assertTrue(model.contains("applySleepingCurl"));
        assertTrue(machine.contains("OverclockingLogic.NON_PERFECT_OVERCLOCK"));
        assertTrue(machine.contains("crucible.getOverclockVoltage()"));

        Path modelRoot = RESOURCES.resolve("assets/tstmodern/models/entity");
        assertTrue(Files.isRegularFile(modelRoot.resolve("draconic_crucible_fire_dragon_ground.tbl")));
        assertTrue(Files.isRegularFile(modelRoot.resolve("draconic_crucible_fire_dragon_sleeping.tbl")));
        assertTrue(Files.isRegularFile(
                RESOURCES.resolve("assets/tstmodern/textures/entity/draconic_crucible_dragon_sleeping.png")));
        assertTrue(Files.isRegularFile(modelRoot.resolve("draconic_crucible_fire_dragon_charge.tbl")));
        assertTrue(Files.isRegularFile(modelRoot.resolve("draconic_crucible_fire_dragon_breath.tbl")));
        for (int frame = 1; frame <= 6; frame++) {
            assertTrue(Files.isRegularFile(modelRoot.resolve(
                    "draconic_crucible_fire_dragon_flight" + frame + ".tbl")));
        }
    }

    private static String externalId(String fallbackId) {
        return switch (fallbackId) {
            case "draconium_ore" -> "end_draconium_ore";
            default -> fallbackId;
        };
    }

    private static String fallbackId(String item) {
        return switch (item) {
            case "draconium_dust", "draconium_ingot",
                    "awakened_draconium_nugget", "awakened_draconium_ingot" -> "gtceu:" + item;
            default -> "tstmodern:" + item;
        };
    }

    private static JsonObject json(Path path) throws Exception {
        return JsonParser.parseString(Files.readString(path)).getAsJsonObject();
    }
}
