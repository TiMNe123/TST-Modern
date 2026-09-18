package com.tstmodern.registry.machine;

import java.util.Arrays;

/** User-approved 41x39x41 Galactic Armillary structure prototype. */
public final class GalacticArmillaryStructure {
    public static final int WIDTH = 41;
    public static final int HEIGHT = 39;
    public static final int DEPTH = 41;
    public static final int CONTROLLER_X = 20;
    public static final int CONTROLLER_Y = 3;
    public static final int CONTROLLER_Z = 20;
    public static final int CENTER_X = 20;
    public static final int CENTER_Y = 18;
    public static final int CENTER_Z = 20;

    public static final int[][] ENERGY_SOCKET_COORDINATES = {
            { 18, 2, 18 }, { 19, 2, 18 }, { 20, 2, 18 }, { 21, 2, 18 }, { 22, 2, 18 },
            { 22, 2, 19 }, { 22, 2, 20 }, { 22, 2, 21 },
            { 22, 2, 22 }, { 21, 2, 22 }, { 20, 2, 22 }, { 19, 2, 22 }, { 18, 2, 22 },
            { 18, 2, 21 }, { 18, 2, 20 }, { 18, 2, 19 }
    };

    public static final String[][] PATTERN_AISLES = build();

    private GalacticArmillaryStructure() {}

    public static char symbolAt(int x, int y, int z) {
        return PATTERN_AISLES[z][HEIGHT - 1 - y].charAt(x);
    }

    private static String[][] build() {
        char[][][] cells = new char[DEPTH][HEIGHT][WIDTH];
        for (char[][] aisle : cells) {
            for (char[] row : aisle) Arrays.fill(row, ' ');
        }

        buildFoundation(cells);
        buildArches(cells);
        buildArmillary(cells);
        buildCrown(cells);
        buildSatelliteShell(cells);
        for (int x = CENTER_X - 1; x <= CENTER_X + 1; x++) {
            for (int z = CENTER_Z - 1; z <= CENTER_Z + 1; z++) set(cells, x, 2, z, 'H');
        }
        for (int[] socket : ENERGY_SOCKET_COORDINATES) {
            set(cells, socket[0], socket[1], socket[2], 'E');
        }
        set(cells, CONTROLLER_X, CONTROLLER_Y, CONTROLLER_Z, '~');

        String[][] aisles = new String[DEPTH][HEIGHT];
        for (int z = 0; z < DEPTH; z++) {
            for (int down = 0; down < HEIGHT; down++) {
                aisles[z][down] = new String(cells[z][HEIGHT - 1 - down]);
            }
        }
        return aisles;
    }

    private static void buildFoundation(char[][][] cells) {
        fillDisk(cells, 0, 20, 'D');
        fillDisk(cells, 1, 20, 'D');
        fillDisk(cells, 2, 18, 'D');
        fillRing(cells, 2, 17, 0.55, 'H');
        fillRing(cells, 2, 11, 0.45, 'H');
        for (int radius = 3; radius <= 17; radius++) {
            set(cells, CENTER_X + radius, 2, CENTER_Z, 'H');
            set(cells, CENTER_X - radius, 2, CENTER_Z, 'H');
            set(cells, CENTER_X, 2, CENTER_Z + radius, 'H');
            set(cells, CENTER_X, 2, CENTER_Z - radius, 'H');
        }
    }

    private static void buildArches(char[][][] cells) {
        for (int arch = 0; arch < 4; arch++) {
            double angle = Math.PI / 4 + arch * Math.PI / 2;
            for (int y = 3; y <= 31; y++) {
                double progress = (y - 3) / 28.0;
                double radius = 17 - 7 * Math.sin(progress * Math.PI / 2);
                int x = (int) Math.round(CENTER_X + radius * Math.cos(angle));
                int z = (int) Math.round(CENTER_Z + radius * Math.sin(angle));
                fillBall(cells, x, y, z, 1.45, 'D');
                if (y % 5 == 1) set(cells, x, y, z, 'H');
                if (y == 7 || y == 14 || y == 21 || y == 28) set(cells, x, y, z, 'A');
            }
        }
    }

    private static void buildArmillary(char[][][] cells) {
        int radius = 9;
        double tilt = Math.toRadians(30);
        fillTiltedRing(cells, radius, tilt, 'I');
        fillTiltedRing(cells, radius, -tilt, 'O');
        fillBall(cells, CENTER_X, CENTER_Y, CENTER_Z + radius, 1, 'A');
        fillBall(cells, CENTER_X, CENTER_Y, CENTER_Z - radius, 1, 'A');
    }

    private static void fillTiltedRing(char[][][] cells, double radius, double tilt, char symbol) {
        double cosine = Math.cos(tilt);
        double sine = Math.sin(tilt);
        double verticalScale = 1.5;
        for (int z = 0; z < DEPTH; z++) {
            for (int y = 0; y < HEIGHT; y++) {
                for (int x = 0; x < WIDTH; x++) {
                    int dx = x - CENTER_X;
                    double dy = (y - CENTER_Y) / verticalScale;
                    int dz = z - CENTER_Z;
                    double inPlane = dx * cosine + dy * sine;
                    double planeDistance = -dx * sine + dy * cosine;
                    if (Math.abs(planeDistance) <= 0.55 &&
                            Math.abs(Math.hypot(inPlane, dz) - radius) <= 0.55) {
                        set(cells, x, y, z, symbol);
                    }
                }
            }
        }
    }

    private static void buildCrown(char[][][] cells) {
        fillRing(cells, 31, 10, 0.65, 'D');
        fillRing(cells, 31, 16, 0.65, 'D');
        fillRing(cells, 32, 11, 0.65, 'D');
        fillRing(cells, 32, 16, 0.65, 'D');
        fillRing(cells, 33, 12, 0.65, 'D');
        fillRing(cells, 33, 15, 0.65, 'D');
        fillRing(cells, 34, 13, 0.7, 'D');

        for (int index = 0; index < 8; index++) {
            double angle = index * Math.PI / 4;
            int outerX = (int) Math.round(CENTER_X + 15 * Math.cos(angle));
            int outerZ = (int) Math.round(CENTER_Z + 15 * Math.sin(angle));
            fillBall(cells, outerX, 33, outerZ, 1, 'A');

            int innerX = (int) Math.round(CENTER_X + 12 * Math.cos(angle));
            int innerZ = (int) Math.round(CENTER_Z + 12 * Math.sin(angle));
            set(cells, innerX, 34, innerZ, 'H');
        }
    }

    private static void buildSatelliteShell(char[][][] cells) {
        int satelliteY = 37;
        fillRing(cells, satelliteY, 9, 0.55, 'D');

        for (int y = satelliteY - 1; y <= satelliteY + 1; y++) {
            for (int z = CENTER_Z - 3; z <= CENTER_Z + 3; z++) {
                for (int x = CENTER_X - 3; x <= CENTER_X + 3; x++) {
                    if (Math.max(Math.abs(x - CENTER_X), Math.abs(z - CENTER_Z)) == 3) {
                        set(cells, x, y, z, 'D');
                    }
                }
            }
        }

        for (int radius = 4; radius <= 8; radius++) {
            set(cells, CENTER_X + radius, satelliteY, CENTER_Z, 'H');
            set(cells, CENTER_X - radius, satelliteY, CENTER_Z, 'H');
            set(cells, CENTER_X, satelliteY, CENTER_Z + radius, 'H');
            set(cells, CENTER_X, satelliteY, CENTER_Z - radius, 'H');

            set(cells, CENTER_X + radius, satelliteY, CENTER_Z - 1, 'D');
            set(cells, CENTER_X + radius, satelliteY, CENTER_Z + 1, 'D');
            set(cells, CENTER_X - radius, satelliteY, CENTER_Z - 1, 'D');
            set(cells, CENTER_X - radius, satelliteY, CENTER_Z + 1, 'D');
            set(cells, CENTER_X - 1, satelliteY, CENTER_Z + radius, 'D');
            set(cells, CENTER_X + 1, satelliteY, CENTER_Z + radius, 'D');
            set(cells, CENTER_X - 1, satelliteY, CENTER_Z - radius, 'D');
            set(cells, CENTER_X + 1, satelliteY, CENTER_Z - radius, 'D');
        }

        set(cells, CENTER_X - 4, satelliteY, CENTER_Z, 'A');
        set(cells, CENTER_X + 4, satelliteY, CENTER_Z, 'A');

        for (int y = 33; y <= 35; y++) fillRing(cells, y, 3, 0.55, 'D');
        for (int y = 30; y <= 32; y++) fillRing(cells, y, 2, 0.55, 'D');
        set(cells, CENTER_X - 3, 32, CENTER_Z, 'D');
        set(cells, CENTER_X + 3, 32, CENTER_Z, 'D');
        set(cells, CENTER_X, 32, CENTER_Z - 3, 'D');
        set(cells, CENTER_X, 32, CENTER_Z + 3, 'D');

        fillRing(cells, 29, 1, 0.55, 'H');
        for (int y = 27; y <= 28; y++) {
            for (int xOffset = -1; xOffset <= 1; xOffset += 2) {
                for (int zOffset = -1; zOffset <= 1; zOffset += 2) {
                    set(cells, CENTER_X + xOffset, y, CENTER_Z + zOffset, 'H');
                }
            }
        }
        set(cells, CENTER_X - 2, 29, CENTER_Z, 'D');
        set(cells, CENTER_X + 2, 29, CENTER_Z, 'D');
        set(cells, CENTER_X, 29, CENTER_Z - 2, 'D');
        set(cells, CENTER_X, 29, CENTER_Z + 2, 'D');
    }

    private static void fillDisk(char[][][] cells, int y, int radius, char symbol) {
        for (int z = 0; z < DEPTH; z++) {
            for (int x = 0; x < WIDTH; x++) {
                int dx = x - CENTER_X;
                int dz = z - CENTER_Z;
                if (dx * dx + dz * dz <= radius * radius) set(cells, x, y, z, symbol);
            }
        }
    }

    private static void fillRing(char[][][] cells, int y, double radius, double thickness, char symbol) {
        for (int z = 0; z < DEPTH; z++) {
            for (int x = 0; x < WIDTH; x++) {
                if (nearRing(x - CENTER_X, z - CENTER_Z, radius, thickness)) {
                    set(cells, x, y, z, symbol);
                }
            }
        }
    }

    private static boolean nearRing(int first, int second, double radius, double thickness) {
        return Math.abs(Math.hypot(first, second) - radius) <= thickness;
    }

    private static void fillBall(char[][][] cells, int centerX, int centerY, int centerZ,
                                 double radius, char symbol) {
        int extent = (int) Math.ceil(radius);
        double radiusSquared = radius * radius;
        for (int z = centerZ - extent; z <= centerZ + extent; z++) {
            for (int y = centerY - extent; y <= centerY + extent; y++) {
                for (int x = centerX - extent; x <= centerX + extent; x++) {
                    int dx = x - centerX;
                    int dy = y - centerY;
                    int dz = z - centerZ;
                    if (dx * dx + dy * dy + dz * dz <= radiusSquared) {
                        set(cells, x, y, z, symbol);
                    }
                }
            }
        }
    }

    private static void set(char[][][] cells, int x, int y, int z, char symbol) {
        if (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT && z >= 0 && z < DEPTH) {
            cells[z][y][x] = symbol;
        }
    }
}
