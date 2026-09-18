package com.tstmodern.registry.machine;

import java.util.Arrays;

/** User-approved 61x15x45 Draconic Crucible with a continuously tapered volcano. */
public final class DraconicCrucibleStructure {
    public static final int WIDTH = 61;
    public static final int HEIGHT = 15;
    public static final int DEPTH = 45;
    public static final int CONTROLLER_X = 30;
    public static final int CONTROLLER_Y = 1;
    public static final int CONTROLLER_Z = 0;
    public static final int VOLCANO_CENTER_X = 20;
    public static final int VOLCANO_CENTER_Z = 22;
    public static final int CORE_X = VOLCANO_CENTER_X;
    public static final int CORE_Y = 14;
    public static final int CORE_Z = VOLCANO_CENTER_Z;

    static final int[] VOLCANO_RADIUS_X = { 19, 18, 17, 16, 15, 14, 13, 12, 10, 8, 6 };
    static final int[] VOLCANO_RADIUS_Z = { 20, 19, 18, 17, 16, 15, 14, 13, 11, 9, 7 };

    public static final String[][] PATTERN_AISLES = build();

    private DraconicCrucibleStructure() {}

    private static String[][] build() {
        char[][][] cells = new char[DEPTH][HEIGHT][WIDTH];
        for (char[][] aisle : cells) {
            for (char[] row : aisle) Arrays.fill(row, ' ');
        }

        // Dense foundation and Radiant Naquadah working deck.
        fillLayer(cells, 0, 'D');
        fillLayer(cells, 1, 'R');
        replace(cells, CONTROLLER_X, CONTROLLER_Y, CONTROLLER_Z, '~');

        for (int y = 2; y <= 12; y++) {
            buildVolcanoLayer(cells, y, VOLCANO_RADIUS_X[y - 2], VOLCANO_RADIUS_Z[y - 2]);
        }
        buildCraterRim(cells);
        replace(cells, CORE_X, CORE_Y, CORE_Z, 'K');

        buildTechnologyWing(cells);

        String[][] aisles = new String[DEPTH][HEIGHT];
        for (int z = 0; z < DEPTH; z++) {
            for (int down = 0; down < HEIGHT; down++) {
                aisles[z][down] = new String(cells[z][HEIGHT - 1 - down]);
            }
        }
        return aisles;
    }

    private static void buildVolcanoLayer(char[][][] cells, int y, int radiusX, int radiusZ) {
        int hotRadiusX = radiusX - 2;
        int hotRadiusZ = radiusZ - 2;
        int chamberRadiusX = radiusX - 4;
        int chamberRadiusZ = radiusZ - 4;

        for (int z = VOLCANO_CENTER_Z - radiusZ; z <= VOLCANO_CENTER_Z + radiusZ; z++) {
            for (int x = VOLCANO_CENTER_X - radiusX; x <= VOLCANO_CENTER_X + radiusX; x++) {
                int dx = x - VOLCANO_CENTER_X;
                int dz = z - VOLCANO_CENTER_Z;
                if (!insideEllipse(dx, dz, radiusX, radiusZ)) continue;

                char casing = 'M';
                if (insideEllipse(dx, dz, hotRadiusX, hotRadiusZ)) casing = 'S';
                if (insideEllipse(dx, dz, chamberRadiusX, chamberRadiusZ)) casing = '#';
                replace(cells, x, y, z, casing);
            }
        }

        // Sloped coolant ribs follow both sides of the shrinking mountain.
        replace(cells, VOLCANO_CENTER_X - radiusX, y, VOLCANO_CENTER_Z, 'P');
        replace(cells, VOLCANO_CENTER_X + radiusX, y, VOLCANO_CENTER_Z, 'P');

        // A narrow front glass strip exposes the thermal liner without flattening the silhouette.
        if (y >= 3 && y <= 10) {
            int frontZ = VOLCANO_CENTER_Z - radiusZ;
            replace(cells, VOLCANO_CENTER_X, y, frontZ, 'G');
            replace(cells, VOLCANO_CENTER_X - 1, y, frontZ + 1, 'G');
            replace(cells, VOLCANO_CENTER_X + 1, y, frontZ + 1, 'G');
        }

        // The lower heat chamber feeds a compact fusion column beneath the Dragon Egg.
        if (y <= 6) fillEllipse(cells, y, VOLCANO_CENTER_X, VOLCANO_CENTER_Z, 4, 4, 'S');
        replace(cells, VOLCANO_CENTER_X, y, VOLCANO_CENTER_Z, 'C');
        replace(cells, VOLCANO_CENTER_X - 1, y, VOLCANO_CENTER_Z, 'C');
        replace(cells, VOLCANO_CENTER_X + 1, y, VOLCANO_CENTER_Z, 'C');
        replace(cells, VOLCANO_CENTER_X, y, VOLCANO_CENTER_Z - 1, 'C');
        replace(cells, VOLCANO_CENTER_X, y, VOLCANO_CENTER_Z + 1, 'C');
    }

    private static void buildCraterRim(char[][][] cells) {
        int y = 13;
        fillEllipse(cells, y, VOLCANO_CENTER_X, VOLCANO_CENTER_Z, 5, 6, 'F');
        fillEllipse(cells, y, VOLCANO_CENTER_X, VOLCANO_CENTER_Z, 3, 4, '#');

        int[][] anchors = {
                { -5, 0 }, { 5, 0 }, { 0, -6 }, { 0, 6 },
                { -3, -4 }, { 3, -4 }, { -3, 4 }, { 3, 4 }
        };
        for (int[] anchor : anchors) {
            replace(cells, VOLCANO_CENTER_X + anchor[0], y, VOLCANO_CENTER_Z + anchor[1], 'T');
        }
    }

    private static void buildTechnologyWing(char[][][] cells) {
        // Separate containment/diagnostic housing on the right of the volcano.
        for (int y = 2; y <= 6; y++) {
            for (int z = 18; z <= 26; z++) {
                for (int x = 51; x <= 59; x++) {
                    boolean shell = y == 2 || y == 6 || x == 51 || x == 59 || z == 18 || z == 26;
                    if (shell) replace(cells, x, y, z, y == 2 ? 'D' : 'R');
                }
            }
        }

        // Particle-beam guide from the control housing into the east coolant rib.
        for (int x = VOLCANO_CENTER_X + VOLCANO_RADIUS_X[2]; x <= 51; x++) {
            replace(cells, x, 4, VOLCANO_CENTER_Z, 'B');
        }
        replace(cells, VOLCANO_CENTER_X + VOLCANO_RADIUS_X[2], 4, VOLCANO_CENTER_Z, 'P');
        replace(cells, 51, 4, VOLCANO_CENTER_Z, 'P');

        // Front-facing observation screens and a protected Neutronium service spine.
        for (int x = 54; x <= 56; x++) {
            replace(cells, x, 4, 18, 'G');
            replace(cells, x, 5, 18, 'G');
        }
        for (int y = 3; y <= 5; y++) replace(cells, 55, y, 22, 'P');
    }

    private static boolean insideEllipse(int dx, int dz, int radiusX, int radiusZ) {
        if (radiusX <= 0 || radiusZ <= 0) return false;
        double normalizedX = (double) dx * dx / (radiusX * radiusX);
        double normalizedZ = (double) dz * dz / (radiusZ * radiusZ);
        return normalizedX + normalizedZ <= 1.0D;
    }

    private static void fillEllipse(char[][][] cells, int y, int centerX, int centerZ,
                                    int radiusX, int radiusZ, char symbol) {
        for (int z = centerZ - radiusZ; z <= centerZ + radiusZ; z++) {
            for (int x = centerX - radiusX; x <= centerX + radiusX; x++) {
                if (insideEllipse(x - centerX, z - centerZ, radiusX, radiusZ)) {
                    replace(cells, x, y, z, symbol);
                }
            }
        }
    }

    private static void fillLayer(char[][][] cells, int y, char symbol) {
        for (int z = 0; z < DEPTH; z++) Arrays.fill(cells[z][y], symbol);
    }

    private static void replace(char[][][] cells, int x, int y, int z, char symbol) {
        cells[z][y][x] = symbol;
    }
}
