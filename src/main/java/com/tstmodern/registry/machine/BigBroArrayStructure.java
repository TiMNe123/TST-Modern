package com.tstmodern.registry.machine;

/**
 * Transposed slice definitions for the BigBroArray Core structure.
 * 11 aisles (depth/Z, Z=0..10), 8 rows per aisle (height/Y, going down from top to bottom), 11 columns (width/X).
 * Top layer (Y=max, row 0): Clean Stainless Steel Casing (E) & Muffler Hatches (F) at corners.
 * Bottom layer (Y=min, row 7): Borosilicate/Tempered Glass (A) & Parallelism Casings MK1-MK5 (C).
 */
public final class BigBroArrayStructure {

    public static final String[][] CORE_AISLES = {
        // === Aisle 0 (Z=0) ===
        {
            "     F     ",
            "           ",
            "           ",
            "           ",
            "           ",
            "           ",
            "   CCCCC   ",
            "   AAAAA   "
        },
        // === Aisle 1 (Z=1) ===
        {
            "    EEE    ",
            "           ",
            "           ",
            "           ",
            "           ",
            "           ",
            "  CCCCCCC  ",
            "  AAAAAAA  "
        },
        // === Aisle 2 (Z=2) ===
        {
            "   EEEEE   ",
            "   B   B   ",
            "   B   B   ",
            "   B   B   ",
            "   B   B   ",
            "   B   B   ",
            " CCCCCCCCC ",
            " AAAAAAAAA "
        },
        // === Aisle 3 (Z=3) ===
        {
            "  EEEEEEE  ",
            "  B     B  ",
            "  B     B  ",
            "  B     B  ",
            "  B     B  ",
            "  B     B  ",
            "CCCCCCCCCCC",
            "AAAAAAAAAAA"
        },
        // === Aisle 4 (Z=4) ===
        {
            " EEEEEEEEE ",
            "    DDD    ",
            "    D~D    ",
            "    DDD    ",
            "    HHH    ",
            "    HHH    ",
            "CCCCCCCCCCC",
            "AAAAAAAAAAA"
        },
        // === Aisle 5 (Z=5) ===
        {
            "FEEEEEEEEEF",
            "    DDD    ",
            "    D D    ",
            "    DDD    ",
            "    HHH    ",
            "    HHH    ",
            "CCCCCCCCCCC",
            "AAAAAAAAAAA"
        },
        // === Aisle 6 (Z=6) ===
        {
            " EEEEEEEEE ",
            "    DDD    ",
            "    DDD    ",
            "    DDD    ",
            "    HHH    ",
            "    HHH    ",
            "CCCCCCCCCCC",
            "AAAAAAAAAAA"
        },
        // === Aisle 7 (Z=7) ===
        {
            "  EEEEEEE  ",
            "  B     B  ",
            "  B     B  ",
            "  B     B  ",
            "  B     B  ",
            "  B     B  ",
            "CCCCCCCCCCC",
            "AAAAAAAAAAA"
        },
        // === Aisle 8 (Z=8) ===
        {
            "   EEEEE   ",
            "   B   B   ",
            "   B   B   ",
            "   B   B   ",
            "   B   B   ",
            "   B   B   ",
            " CCCCCCCCC ",
            " AAAAAAAAA "
        },
        // === Aisle 9 (Z=9) ===
        {
            "    EEE    ",
            "           ",
            "           ",
            "           ",
            "           ",
            "           ",
            "  CCCCCCC  ",
            "  AAAAAAA  "
        },
        // === Aisle 10 (Z=10) ===
        {
            "     F     ",
            "           ",
            "           ",
            "           ",
            "           ",
            "           ",
            "   CCCCC   ",
            "   AAAAA   "
        }
    };

    private BigBroArrayStructure() {}
}
