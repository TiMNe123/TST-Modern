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
        String[][] result = new String[source[0][0].length()][source.length];
        for (int right = 0; right < result.length; right++) for (int down = 0; down < source.length; down++) {
            StringBuilder row = new StringBuilder(source[0].length);
            for (int back = 0; back < source[0].length; back++) { char symbol = source[down][back].charAt(right); row.append(rename ? rename(symbol) : symbol); }
            result[right][down] = row.toString();
        }
        return result;
    }
    private static char rename(char symbol) { return switch (symbol) { case 'A' -> 'G'; case 'B' -> 'H'; case 'C' -> 'I'; case 'D' -> 'J'; case 'E' -> 'K'; case 'F' -> 'L'; default -> symbol; }; }
    private static List<AddonPlacement> createAddonPlacements() {
        List<PlacementSeed> seeds = List.of(new PlacementSeed(0, 0, -6, 23, 6), new PlacementSeed(1, 1, 7, 23, -7), new PlacementSeed(2, 2, 22, 23, 6), new PlacementSeed(3, 3, 7, 23, 21));
        List<AddonPlacement> placements = new ArrayList<>(4); for (PlacementSeed seed : seeds) placements.add(place(seed)); return List.copyOf(placements);
    }
    private static AddonPlacement place(PlacementSeed seed) {
        int width = ADDON_SOURCE.length, depth = ADDON_SOURCE[0][0].length(); List<RelativeCell> cells = new ArrayList<>();
        for (int right = 0; right < width; right++) for (int down = 0; down < ADDON_SOURCE[0].length; down++) for (int back = 0; back < depth; back++) { char symbol = ADDON_SOURCE[right][down].charAt(back); if (symbol != ' ') cells.add(rotate(right, down, back, symbol, seed.quarterTurns(), width, depth)); }
        return new AddonPlacement(seed.index(), seed.quarterTurns(), seed.offsetX(), seed.offsetY(), seed.offsetZ(), List.copyOf(cells));
    }
    private static RelativeCell rotate(int right, int down, int back, char symbol, int turns, int width, int depth) { return switch (turns) { case 0 -> new RelativeCell(right, down, back, symbol); case 1 -> new RelativeCell(back, down, width - 1 - right, symbol); case 2 -> new RelativeCell(width - 1 - right, down, depth - 1 - back, symbol); case 3 -> new RelativeCell(depth - 1 - back, down, right, symbol); default -> throw new IllegalArgumentException("quarter turns must be 0..3"); }; }
    private static String[][] readSourceAddon() {
        try (GZIPInputStream input = new GZIPInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(SOURCE_ADDON_GZIP)))) {
            String[] layers = new String(input.readAllBytes(), StandardCharsets.UTF_8).split("\\f", -1); String[][] source = new String[layers.length][]; for (int index = 0; index < layers.length; index++) source[index] = layers[index].split("\\n", -1); return source;
        } catch (IOException exception) { throw new ExceptionInInitializerError(exception); }
    }
    private BigBroArrayStructure() {}
}
