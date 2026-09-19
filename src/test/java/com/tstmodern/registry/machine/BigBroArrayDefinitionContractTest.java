package com.tstmodern.registry.machine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.pattern.util.PatternMatchContext;
import com.tstmodern.machine.logic.BigBroArrayTierRules;
import com.tstmodern.machine.logic.BigBroArrayTierRules.CoreTiers;

import org.junit.jupiter.api.Test;

class BigBroArrayDefinitionContractTest {
    @Test
    void sourceGeometryIsFrozen() throws Exception {
        assertPattern(BigBroArrayStructure.CORE_SOURCE, 11, 11, 8,
                "9ea00dcdd9c1e86f0031c7ef3a94072fe4b99ad59d813f1b6a44cc06083a2041");
        assertPattern(BigBroArrayStructure.ADDON_RAW_SOURCE, 17, 15, 26,
                "77d3efd59df29b979854c3b1f4c266d8b43a45daeaf82993e0d112481430f250");
        assertDimensions(BigBroArrayStructure.CORE_AISLES, 11, 8, 11);
        assertDimensions(BigBroArrayStructure.ADDON_SOURCE, 17, 26, 15);
        assertCounts(BigBroArrayStructure.CORE_AISLES,
                Map.of('~', 1, 'A', 97, 'B', 40, 'C', 97, 'D', 25, 'E', 57, 'F', 4));
        assertCounts(BigBroArrayStructure.ADDON_SOURCE,
                Map.of('G', 134, 'H', 44, 'I', 42, 'J', 64, 'K', 530, 'L', 86));
    }

    @Test
    void transposedSourcesUseStandardBackDownRightOrder() {
        for (int down = 0; down < BigBroArrayStructure.CORE_SOURCE.length; down++) {
            for (int back = 0; back < BigBroArrayStructure.CORE_SOURCE[down].length; back++) {
                assertEquals(BigBroArrayStructure.CORE_SOURCE[down][back],
                        BigBroArrayStructure.CORE_AISLES[back][down]);
            }
        }
        for (int down = 0; down < BigBroArrayStructure.ADDON_RAW_SOURCE.length; down++) {
            for (int back = 0; back < BigBroArrayStructure.ADDON_RAW_SOURCE[down].length; back++) {
                String expected = BigBroArrayStructure.ADDON_RAW_SOURCE[down][back]
                        .replace('A', 'G')
                        .replace('B', 'H')
                        .replace('C', 'I')
                        .replace('D', 'J')
                        .replace('E', 'K')
                        .replace('F', 'L');
                assertEquals(expected, BigBroArrayStructure.ADDON_SOURCE[back][down]);
            }
        }
    }

    @Test
    void coreTierSnapshotRequiresAllThreeUniformChannels() {
        PatternMatchContext context = new PatternMatchContext();
        assertNull(BigBroArrayTierRules.validatedCoreTiers(context));

        BigBroArrayTierRules.matchUniformTier(context, BigBroArrayTierRules.FRAME_TIER_CONTEXT, 2);
        BigBroArrayTierRules.matchUniformTier(context, BigBroArrayTierRules.GLASS_TIER_CONTEXT, GTValues.IV);
        assertNull(BigBroArrayTierRules.validatedCoreTiers(context));

        BigBroArrayTierRules.matchUniformTier(
                context, BigBroArrayTierRules.MACHINE_CASING_TIER_CONTEXT, GTValues.UV);
        assertEquals(new CoreTiers(2, GTValues.IV, GTValues.UV),
                BigBroArrayTierRules.validatedCoreTiers(context));
    }

    @Test
    void definitionSourceContainsNoGlobalOverclockModifier() throws Exception {
        String source = java.nio.file.Files.readString(
                java.nio.file.Path.of("src/main/java/com/tstmodern/registry/machine/BigBroArrayDefinition.java"));
        assertFalse(source.contains("OC_NON_PERFECT"), "Must not register global OC_NON_PERFECT");
        assertFalse(source.contains("OC_PERFECT"), "Must not register global OC_PERFECT");
        org.junit.jupiter.api.Assertions.assertTrue(
                source.contains(".recipeModifiers(BigBroArrayMachine::recipeModifier)"),
                "Must register only BigBroArrayMachine::recipeModifier");
    }

    @Test
    void coreAbilityContractAcceptsEitherItemOrFluidIoWithoutParallelHatch() throws Exception {
        String source = java.nio.file.Files.readString(
                java.nio.file.Path.of("src/main/java/com/tstmodern/registry/machine/BigBroArrayDefinition.java"));

        assertTrue(source.contains("Predicates.autoAbilities(true, true, false)"),
                "D must use GTCEu's config-aware maintenance and required muffler contract");
        assertTrue(source.contains("PartAbility.IMPORT_ITEMS,\n" +
                "                                                PartAbility.IMPORT_FLUIDS"),
                "D must accept either an item or fluid input hatch");
        assertTrue(source.contains("PartAbility.EXPORT_ITEMS,\n" +
                "                                                PartAbility.EXPORT_FLUIDS"),
                "D must accept either an item or fluid output hatch");
        assertFalse(source.contains("PartAbility.PARALLEL_HATCH"),
                "The BigBroArray core must not gain a GTCEu parallel hatch");
    }

    @Test
    void formedPartRendererUsesCleanStainlessForTheFPlane() throws Exception {
        String definitionSource = java.nio.file.Files.readString(
                java.nio.file.Path.of("src/main/java/com/tstmodern/registry/machine/BigBroArrayDefinition.java"));
        String rendererSource = java.nio.file.Files.readString(
                java.nio.file.Path.of("src/main/java/com/tstmodern/client/renderer/BigBroArrayPartRender.java"));
        String clientSource = java.nio.file.Files.readString(
                java.nio.file.Path.of("src/main/java/com/tstmodern/client/TSTClient.java"));
        String model = java.nio.file.Files.readString(
                java.nio.file.Path.of("src/main/resources/assets/tstmodern/models/block/machine/big_bro_array.json"));

        assertTrue(definitionSource.contains(".partAppearance(BigBroArrayDefinition::partAppearance)"),
                "Block appearance queries should retain the per-part casing contract");
        assertTrue(rendererSource.contains("implements IControllerModelRenderer"),
                "GTCEu formed-part textures are replaced by the controller model renderer");
        assertTrue(rendererSource.contains("controllerPos.relative(relativeDown, F_PLANE_DOWN_OFFSET)"),
                "F must be selected in controller-relative coordinates so machine rotation remains valid");
        assertTrue(rendererSource.contains("F_PLANE_DOWN_OFFSET = 2"),
                "The frozen core geometry places all four F cells two rows below the controller");
        assertTrue(rendererSource.contains("GTBlocks.CASING_STAINLESS_CLEAN"));
        assertTrue(rendererSource.contains("GTBlocks.CASING_TUNGSTENSTEEL_ROBUST"));
        assertTrue(clientSource.contains("DynamicRenderManager.register"));
        assertTrue(model.contains("tstmodern:big_bro_array_parts"));
    }

    @Test
    void addonPlacementsAreCompleteNonOverlappingAndBounded() throws Exception {
        assertEquals(4, BigBroArrayStructure.ADDON_PLACEMENTS.size());
        List<int[]> expectedBounds = List.of(
                new int[] { 6, 22, -23, 2, -6, 8 },
                new int[] { -7, 7, -23, 2, 7, 23 },
                new int[] { -22, -6, -23, 2, -6, 8 },
                new int[] { -7, 7, -23, 2, -21, -5 });
        List<String> expectedDigests = List.of(
                "5b278de36ece92b5af0d020bd491e25eb59d7739506c0e8ca5a5805145562da3",
                "7858f67fda65479a8955aa0a2c72006676c68a07d44d51385b44439e122b6290",
                "55c8b3440a1df6a09a764b7216a00279db7a0ec2b004209abc71ac0b1bee45dc",
                "b182495736ba9421fb9725bbea126d6c0694e31fccd8575664a0d4a53f04953a");
        Set<String> occupied = new HashSet<>();
        int minX = 0;
        int maxX = 0;
        int minY = 0;
        int maxY = 0;
        int minZ = 0;
        int maxZ = 0;
        for (BigBroArrayStructure.AddonPlacement placement : BigBroArrayStructure.ADDON_PLACEMENTS) {
            assertEquals(900, placement.cells().size());
            assertPlacementBounds(placement, expectedBounds.get(placement.index()));
            assertEquals(expectedDigests.get(placement.index()), placementDigest(placement));
            for (BigBroArrayStructure.RelativeCell cell : placement.cells()) {
                int x = cell.right() - placement.offsetX();
                int y = cell.down() - placement.offsetY();
                int z = cell.back() - placement.offsetZ();
                assertEquals(true, occupied.add(x + "," + y + "," + z));
                minX = Math.min(minX, x);
                maxX = Math.max(maxX, x);
                minY = Math.min(minY, y);
                maxY = Math.max(maxY, y);
                minZ = Math.min(minZ, z);
                maxZ = Math.max(maxZ, z);
            }
        }
        for (int back = 0; back < 11; back++) {
            for (int down = 0; down < 8; down++) {
                for (int right = 0; right < 11; right++) {
                    if (BigBroArrayStructure.CORE_AISLES[back][down].charAt(right) != ' ') {
                        occupied.add((right - 5) + "," + (down - 5) + "," + (back - 4));
                    }
                }
            }
        }
        assertEquals(3921, occupied.size());
        assertEquals(-22, minX);
        assertEquals(22, maxX);
        assertEquals(-23, minY);
        assertEquals(2, maxY);
        assertEquals(-21, minZ);
        assertEquals(23, maxZ);
    }

    private static void assertPlacementBounds(
                                              BigBroArrayStructure.AddonPlacement placement,
                                              int[] expected) {
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxZ = Integer.MIN_VALUE;
        for (BigBroArrayStructure.RelativeCell cell : placement.cells()) {
            int x = cell.right() - placement.offsetX();
            int y = cell.down() - placement.offsetY();
            int z = cell.back() - placement.offsetZ();
            minX = Math.min(minX, x);
            maxX = Math.max(maxX, x);
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);
            minZ = Math.min(minZ, z);
            maxZ = Math.max(maxZ, z);
        }
        org.junit.jupiter.api.Assertions.assertArrayEquals(
                expected, new int[] { minX, maxX, minY, maxY, minZ, maxZ });
    }

    private static String placementDigest(BigBroArrayStructure.AddonPlacement placement) throws Exception {
        String joined = placement.cells().stream()
                .map(cell -> (cell.right() - placement.offsetX()) + "," +
                        (cell.down() - placement.offsetY()) + "," +
                        (cell.back() - placement.offsetZ()) + "," + cell.symbol())
                .sorted()
                .collect(Collectors.joining("\n"));
        byte[] hash = MessageDigest.getInstance("SHA-256")
                .digest(joined.getBytes(StandardCharsets.UTF_8));
        StringBuilder actual = new StringBuilder();
        for (byte value : hash) {
            actual.append(String.format("%02x", value));
        }
        return actual.toString();
    }

    @Test
    void previewsAreCumulativeAndConvertSourceDownToJeiUp() {
        List<String[][]> previews = BigBroArrayStructure.previewLayouts();

        assertEquals(5, previews.size());
        assertEquals(List.of(321, 1221, 2121, 3021, 3921),
                previews.stream().map(BigBroArrayDefinitionContractTest::occupiedCount).toList());

        String[][] core = previews.get(0);
        assertDimensions(core, 11, 8, 11);
        assertEquals('~', core[4][2].charAt(5));
        assertEquals(61, countRow(core, 0, 'E') + countRow(core, 0, 'F') +
                countRow(core, 0, 'S') + countRow(core, 0, 'T'));
        assertEquals(97, countRow(core, 7, 'A'));
        for (char addonSymbol = 'G'; addonSymbol <= 'L'; addonSymbol++) {
            assertFalse(contains(core, addonSymbol));
        }

        String[][] full = previews.get(4);
        assertDimensions(full, 45, 26, 45);
        assertEquals('~', full[21][2].charAt(22));
        assertEquals(4 * 86, countRow(full, 0, 'L'));
        assertTrue(countRow(full, 25, 'K') > 0);
    }

    @Test
    void definitionKeepsStandardPatternAxesAndFacesPreviewControllerOutward() throws Exception {
        String source = java.nio.file.Files.readString(
                java.nio.file.Path.of("src/main/java/com/tstmodern/registry/machine/BigBroArrayDefinition.java"));
        assertTrue(source.contains("FactoryBlockPattern.start(\n" +
                "                        RelativeDirection.RIGHT,\n" +
                "                        RelativeDirection.DOWN,\n" +
                "                        RelativeDirection.BACK)"));
        assertTrue(source.contains(".where('~', definition, Direction.NORTH)"));
    }

    @Test
    void machineGuardsAsyncPatternChecksAndDeregistersBeforeUnload() throws Exception {
        String source = java.nio.file.Files.readString(
                java.nio.file.Path.of("src/main/java/com/tstmodern/machine/BigBroArrayMachine.java"));
        assertTrue(source.contains("public void asyncCheckPattern(long periodID)"));
        assertTrue(source.contains("if (!(getLevel() instanceof ServerLevel))"));
        int unload = source.indexOf("public void onUnload()");
        int deregister = source.indexOf("removeAsyncLogic(this)", unload);
        int superUnload = source.indexOf("super.onUnload()", unload);
        assertTrue(deregister > unload && deregister < superUnload,
                "BigBroArray must leave GTCEu's async controller list before superclass unload");
    }

    @Test
    void persistedStateLoadDoesNotInitializeMultiblockStateBeforeLevelIsAttached() throws Exception {
        String source = java.nio.file.Files.readString(
                java.nio.file.Path.of("src/main/java/com/tstmodern/machine/BigBroArrayMachine.java"));
        assertTrue(source.contains("if (getLevel() != null) {\n            recipeLogic.resetRecipeLogic();\n        }"),
                "NBT loading must not reach WorkableMultiblockMachine#getMultiblockState while Level is null");
    }

    @Test
    void everyPreviewInjectsOneStableSetOfRequiredCoreAbilities() {
        for (String[][] preview : BigBroArrayStructure.previewLayouts()) {
            for (char symbol = 'M'; symbol <= 'T'; symbol++) {
                assertEquals(1, count(preview, symbol), "preview symbol " + symbol);
            }
            assertEquals(25, count(preview, 'D') + 6);
            assertEquals(4, count(preview, 'F') + 2);

            int[] controller = find(preview, '~');
            assertAt(preview, controller, 0, 0, -1, 'M');
            assertAt(preview, controller, 0, 0, 1, 'N');
            assertAt(preview, controller, 1, 0, -1, 'O');
            assertAt(preview, controller, 1, 0, 1, 'P');
            assertAt(preview, controller, 2, 0, -1, 'Q');
            assertAt(preview, controller, 2, 0, 1, 'R');
            assertAt(preview, controller, -4, -2, 0, 'S');
            assertAt(preview, controller, 6, -2, 0, 'T');
        }
    }

    private static void assertDimensions(String[][] pattern, int width, int height, int depth) {
        assertEquals(depth, pattern.length);
        for (String[] aisle : pattern) {
            assertEquals(height, aisle.length);
            for (String row : aisle) {
                assertEquals(width, row.length());
            }
        }
    }

    private static void assertPattern(String[][] pattern, int width, int height, int depth, String expected)
            throws Exception {
        assertDimensions(pattern, width, height, depth);
        StringBuilder joined = new StringBuilder();
        for (int layer = 0; layer < depth; layer++) {
            for (int row = 0; row < height; row++) {
                if (joined.length() > 0) {
                    joined.append(row == 0 ? '\f' : '\n');
                }
                joined.append(pattern[layer][row]);
            }
        }
        byte[] hash = MessageDigest.getInstance("SHA-256")
                .digest(joined.toString().getBytes(StandardCharsets.UTF_8));
        StringBuilder actual = new StringBuilder();
        for (byte value : hash) {
            actual.append(String.format("%02x", value));
        }
        assertEquals(expected, actual.toString());
    }

    private static void assertCounts(String[][] pattern, Map<Character, Integer> expected) {
        Map<Character, Integer> actual = new HashMap<>();
        for (String[] aisle : pattern) {
            for (String row : aisle) {
                for (char symbol : row.toCharArray()) {
                    if (symbol != ' ') {
                        actual.merge(symbol, 1, Integer::sum);
                    }
                }
            }
        }
        assertEquals(expected, actual);
    }

    private static boolean contains(String[][] pattern, char symbol) {
        return count(pattern, symbol) > 0;
    }

    private static int count(String[][] pattern, char symbol) {
        int count = 0;
        for (String[] aisle : pattern) {
            for (String row : aisle) {
                for (char actual : row.toCharArray()) {
                    if (actual == symbol) count++;
                }
            }
        }
        return count;
    }

    private static int occupiedCount(String[][] pattern) {
        int count = 0;
        for (String[] aisle : pattern) {
            for (String row : aisle) {
                for (char symbol : row.toCharArray()) {
                    if (symbol != ' ') count++;
                }
            }
        }
        return count;
    }

    private static int countRow(String[][] pattern, int rowIndex, char symbol) {
        int count = 0;
        for (String[] aisle : pattern) {
            for (char actual : aisle[rowIndex].toCharArray()) {
                if (actual == symbol) count++;
            }
        }
        return count;
    }

    private static int[] find(String[][] pattern, char symbol) {
        for (int aisle = 0; aisle < pattern.length; aisle++) {
            for (int row = 0; row < pattern[aisle].length; row++) {
                int column = pattern[aisle][row].indexOf(symbol);
                if (column >= 0) return new int[] { aisle, row, column };
            }
        }
        throw new AssertionError("missing preview symbol " + symbol);
    }

    private static void assertAt(
                                 String[][] pattern,
                                 int[] origin,
                                 int aisleOffset,
                                 int rowOffset,
                                 int columnOffset,
                                 char expected) {
        assertEquals(expected,
                pattern[origin[0] + aisleOffset][origin[1] + rowOffset]
                        .charAt(origin[2] + columnOffset));
    }

}
