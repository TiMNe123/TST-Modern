package com.tstmodern.registry.machine;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.zip.GZIPInputStream;

/** Immutable source geometry for the Big Bro Array core and its four addons. */
public final class BigBroArrayStructure {
    public static final String[][] CORE_SOURCE = {
        {"   AAAAA   ", "  AAAAAAA  ", " AAAAAAAAA ", "AAAAAAAAAAA", "AAAAAAAAAAA", "AAAAAAAAAAA", "AAAAAAAAAAA", "AAAAAAAAAAA", " AAAAAAAAA ", "  AAAAAAA  ", "   AAAAA   "},
        {"   CCCCC   ", "  CCCCCCC  ", " CCCCCCCCC ", "CCCCCCCCCCC", "CCCCCCCCCCC", "CCCCCCCCCCC", "CCCCCCCCCCC", "CCCCCCCCCCC", " CCCCCCCCC ", "  CCCCCCC  ", "   CCCCC   "},
        {"           ", "           ", "   B   B   ", "  B     B  ", "           ", "           ", "           ", "  B     B  ", "   B   B   ", "           ", "           "},
        {"           ", "           ", "   B   B   ", "  B     B  ", "           ", "           ", "           ", "  B     B  ", "   B   B   ", "           ", "           "},
        {"           ", "           ", "   B   B   ", "  B     B  ", "    DDD    ", "    DDD    ", "    DDD    ", "  B     B  ", "   B   B   ", "           ", "           "},
        {"           ", "           ", "   B   B   ", "  B     B  ", "    D~D    ", "    D D    ", "    DDD    ", "  B     B  ", "   B   B   ", "           ", "           "},
        {"           ", "           ", "   B   B   ", "  B     B  ", "    DDD    ", "    DDD    ", "    DDD    ", "  B     B  ", "   B   B   ", "           ", "           "},
        {"     F     ", "    EEE    ", "   EEEEE   ", "  EEEEEEE  ", " EEEEEEEEE ", "FEEEEEEEEEF", " EEEEEEEEE ", "  EEEEEEE  ", "   EEEEE   ", "    EEE    ", "     F     "}
    };
    private static final String SOURCE_ADDON_GZIP = "H4sIAAAAAAAACu2XUbKDIAxF/zsuIltJkLf/JXUAsUCCgFDra+FHvYOYewyJAiTjAYMVrXWsaDOaFb7O+JiXrnVYzFZwklPMBW5GvAKA7rAraEZhTroOf5bI8G00uHcXc+AdrGKPVtnepjvLKWhuAUA8mMMU/iwhnlY+S6d3f5RjfgnmPKM4FpZHdg5Tcnygi0+WBs/54nuv8j6IRj2fNH/4/hJoJDVK280c7NMx3gPrxnylMpJP6ivyvmTyR2sEBOyjITgNBAB9ek4HjcRX5P2ARlznWa0TMjxVzmVCXbaUny7VZ6F/VdL44dyYdSOisY/ZUwQa83sjonGWz3d+i9Yov/yfIilEt/qH9eF8iAYA8TlUuGtd11RRa3FOuo43Hsx5SZ+iUacQc0oHNIjRoLdHeCWNglNLo0Ds/9Fg2UuDdwq17soLaexFq+3pY+qGrGwh3banXElj9pTZUxqy9/Y9RSklKVGF5MpX9BRlRnRXj3LfnvLnx3llv+hcJ6O0+noC+vm8r2sbAAA=";
    static final String[][] ADDON_RAW_SOURCE = readSourceAddon();
    public static final String[][] CORE_AISLES = transpose(CORE_SOURCE, false);
    public static final String[][] ADDON_SOURCE = transpose(ADDON_RAW_SOURCE, true);
    public static final List<AddonPlacement> ADDON_PLACEMENTS = createAddonPlacements();

    public record RelativeCell(int right, int down, int back, char symbol) {}
    public record AddonPlacement(int index, int quarterTurns, int offsetX, int offsetY, int offsetZ, List<RelativeCell> cells) {}
    private record PlacementSeed(int index, int quarterTurns, int offsetX, int offsetY, int offsetZ) {}

    private static String[][] transpose(String[][] source, boolean rename) {
        String[][] result = new String[source[0].length][source.length];
        for (int back = 0; back < result.length; back++) for (int down = 0; down < source.length; down++) {
            String row = source[down][back];
            if (rename) {
                StringBuilder renamed = new StringBuilder(row.length());
                for (int right = 0; right < row.length(); right++) renamed.append(rename(row.charAt(right)));
                row = renamed.toString();
            }
            result[back][down] = row;
        }
        return result;
    }
    private static char rename(char symbol) { return switch (symbol) { case 'A' -> 'G'; case 'B' -> 'H'; case 'C' -> 'I'; case 'D' -> 'J'; case 'E' -> 'K'; case 'F' -> 'L'; default -> symbol; }; }
    private static List<AddonPlacement> createAddonPlacements() {
        List<PlacementSeed> seeds = List.of(new PlacementSeed(0, 0, -6, 23, 6), new PlacementSeed(1, 1, 7, 23, -7), new PlacementSeed(2, 2, 22, 23, 6), new PlacementSeed(3, 3, 7, 23, 21));
        List<AddonPlacement> placements = new ArrayList<>(4); for (PlacementSeed seed : seeds) placements.add(place(seed)); return List.copyOf(placements);
    }
    private static AddonPlacement place(PlacementSeed seed) {
        int depth = ADDON_SOURCE.length, width = ADDON_SOURCE[0][0].length(); List<RelativeCell> cells = new ArrayList<>();
        for (int right = 0; right < width; right++) for (int down = 0; down < ADDON_SOURCE[0].length; down++) for (int back = 0; back < depth; back++) { char symbol = ADDON_SOURCE[back][down].charAt(right); if (symbol != ' ') cells.add(transform(right, down, back, symbol, seed.quarterTurns(), width)); }
        return new AddonPlacement(seed.index(), seed.quarterTurns(), seed.offsetX(), seed.offsetY(), seed.offsetZ(), List.copyOf(cells));
    }
    private static RelativeCell transform(
                                          int right,
                                          int down,
                                          int back,
                                          char symbol,
                                          int variant,
                                          int width) {
        return switch (variant) {
            case 0 -> new RelativeCell(right, down, back, symbol);
            case 1 -> new RelativeCell(back, down, right, symbol);
            case 2 -> new RelativeCell(width - 1 - right, down, back, symbol);
            case 3 -> new RelativeCell(back, down, width - 1 - right, symbol);
            default -> throw new IllegalArgumentException("addon variant must be 0..3");
        };
    }
    private static String[][] readSourceAddon() {
        try (GZIPInputStream input = new GZIPInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(SOURCE_ADDON_GZIP)))) {
            String[] layers = new String(input.readAllBytes(), StandardCharsets.UTF_8).split("\\f", -1); String[][] source = new String[layers.length][]; for (int index = 0; index < layers.length; index++) source[index] = layers[index].split("\\n", -1); return source;
        } catch (IOException exception) { throw new ExceptionInInitializerError(exception); }
    }
    public static final int CORE_AISLE_ORIGIN = 4;
    public static final int CORE_DOWN_ORIGIN = 5;
    public static final int CORE_COLUMN_ORIGIN = 5;

    public static List<String[][]> previewLayouts() {
        List<String[][]> previews = new ArrayList<>(5);
        for (int addonCount = 0; addonCount <= ADDON_PLACEMENTS.size(); addonCount++) {
            previews.add(coreWithAddons(ADDON_PLACEMENTS.subList(0, addonCount)));
        }
        return List.copyOf(previews);
    }

    static String[][] coreWithAddons(List<AddonPlacement> placements) {
        int minAisle = -CORE_AISLE_ORIGIN;
        int maxAisle = CORE_AISLES.length - 1 - CORE_AISLE_ORIGIN;
        int minDown = -CORE_DOWN_ORIGIN;
        int maxDown = CORE_AISLES[0].length - 1 - CORE_DOWN_ORIGIN;
        int minColumn = -CORE_COLUMN_ORIGIN;
        int maxColumn = CORE_AISLES[0][0].length() - 1 - CORE_COLUMN_ORIGIN;

        for (AddonPlacement placement : placements) {
            for (RelativeCell cell : placement.cells()) {
                minAisle = Math.min(minAisle, cell.back() - placement.offsetZ());
                maxAisle = Math.max(maxAisle, cell.back() - placement.offsetZ());
                minDown = Math.min(minDown, cell.down() - placement.offsetY());
                maxDown = Math.max(maxDown, cell.down() - placement.offsetY());
                minColumn = Math.min(minColumn, cell.right() - placement.offsetX());
                maxColumn = Math.max(maxColumn, cell.right() - placement.offsetX());
            }
        }

        char[][][] cells = new char[maxAisle - minAisle + 1][maxDown - minDown + 1]
                [maxColumn - minColumn + 1];
        for (char[][] aisle : cells) {
            for (char[] row : aisle) {
                java.util.Arrays.fill(row, ' ');
            }
        }

        for (int aisle = 0; aisle < CORE_AISLES.length; aisle++) {
            for (int down = 0; down < CORE_AISLES[aisle].length; down++) {
                String row = CORE_AISLES[aisle][down];
                for (int column = 0; column < row.length(); column++) {
                    char symbol = row.charAt(column);
                    if (symbol != ' ') {
                        put(cells,
                                aisle - CORE_AISLE_ORIGIN,
                                down - CORE_DOWN_ORIGIN,
                                column - CORE_COLUMN_ORIGIN,
                                minAisle, maxDown, minColumn, symbol);
                    }
                }
            }
        }
        for (AddonPlacement placement : placements) {
            for (RelativeCell cell : placement.cells()) {
                put(cells,
                        cell.back() - placement.offsetZ(),
                        cell.down() - placement.offsetY(),
                        cell.right() - placement.offsetX(),
                        minAisle, maxDown, minColumn, cell.symbol());
            }
        }
        injectPreviewAbilities(cells, minAisle, maxDown, minColumn);

        String[][] layout = new String[cells.length][cells[0].length];
        for (int aisle = 0; aisle < cells.length; aisle++) {
            for (int down = 0; down < cells[aisle].length; down++) {
                layout[aisle][down] = new String(cells[aisle][down]);
            }
        }
        return layout;
    }

    private static void injectPreviewAbilities(
                                               char[][][] cells,
                                               int minAisle,
                                               int maxDown,
                                               int minColumn) {
        replace(cells, 0, 0, -1, minAisle, maxDown, minColumn, 'D', 'M');
        replace(cells, 0, 0, 1, minAisle, maxDown, minColumn, 'D', 'N');
        replace(cells, 1, 0, -1, minAisle, maxDown, minColumn, 'D', 'O');
        replace(cells, 1, 0, 1, minAisle, maxDown, minColumn, 'D', 'P');
        replace(cells, 2, 0, -1, minAisle, maxDown, minColumn, 'D', 'Q');
        replace(cells, 2, 0, 1, minAisle, maxDown, minColumn, 'D', 'R');
        replace(cells, -4, 2, 0, minAisle, maxDown, minColumn, 'F', 'S');
        replace(cells, 6, 2, 0, minAisle, maxDown, minColumn, 'F', 'T');
    }

    private static void replace(
                                char[][][] cells,
                                int aisle,
                                int down,
                                int column,
                                int minAisle,
                                int maxDown,
                                int minColumn,
                                char expected,
                                char replacement) {
        int aisleIndex = aisle - minAisle;
        int downIndex = maxDown - down;
        int columnIndex = column - minColumn;
        char actual = cells[aisleIndex][downIndex][columnIndex];
        if (actual != expected) {
            throw new IllegalStateException("Big Bro Array preview expected " + expected + " at " +
                    aisle + "," + down + "," + column + " but found " + actual);
        }
        cells[aisleIndex][downIndex][columnIndex] = replacement;
    }

    private static void put(
                            char[][][] cells,
                            int aisle,
                            int down,
                            int column,
                            int minAisle,
                            int maxDown,
                            int minColumn,
                            char symbol) {
        int aisleIndex = aisle - minAisle;
        int downIndex = maxDown - down;
        int columnIndex = column - minColumn;
        if (cells[aisleIndex][downIndex][columnIndex] != ' ') {
            throw new IllegalStateException("Big Bro Array preview cells overlap at " +
                    aisle + "," + down + "," + column);
        }
        cells[aisleIndex][downIndex][columnIndex] = symbol;
    }

    private BigBroArrayStructure() {}
}
