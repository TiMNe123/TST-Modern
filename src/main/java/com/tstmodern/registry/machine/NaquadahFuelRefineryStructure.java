package com.tstmodern.registry.machine;

/** Exact 27 x 27 x 5 RIGHT/DOWN/BACK structure from MTENaquadahFuelRefinery. */
public final class NaquadahFuelRefineryStructure {
    public static final int WIDTH = 27;
    public static final int HEIGHT = 27;
    public static final int DEPTH = 5;
    public static final int CONTROLLER_RIGHT = 13;
    public static final int CONTROLLER_DOWN = 13;
    public static final int CONTROLLER_BACK = 0;

    public static final String[][] PATTERN_AISLES = {
            {
                    "                           ", "          AAAAAAA          ", "        AA       AA        ",
                    "      AA           AA      ", "     A               A     ", "    A                 A    ",
                    "   A                   A   ", "   A                   A   ", "  A                     A  ",
                    "  A                     A  ", " A           A           A ", " A          AAA          A ",
                    " A         AAAAA         A ", " A        AAA~AAA        A ", " A         AAAAA         A ",
                    " A          AAA          A ", " A           A           A ", "  A                     A  ",
                    "  A                     A  ", "   A                   A   ", "   A                   A   ",
                    "    A                 A    ", "     A               A     ", "      AA           AA      ",
                    "        AA       AA        ", "          AAAAAAA          ", "                           "
            },
            {
                    "          AAAAAAA          ", "        AACCCCCCCAA        ", "      AACCEAAAAAECCAA      ",
                    "     ACCEE   A   EECCA     ", "    AFEE     A     EEFA    ", "   AFE               EFA   ",
                    "  ACE                 ECA  ", "  ACE                 ECA  ", " ACE                   ECA ",
                    " ACE         A         ECA ", "ACE         A A         ECA", "ACA        A   A        ACA",
                    "ACA       A     A       ACA", "ACAAA    A       A    AAACA", "ACA       A     A       ACA",
                    "ACA        A   A        ACA", "ACE         A A         ECA", " ACE         A         ECA ",
                    " ACE                   ECA ", "  ACE                 ECA  ", "  ACE                 ECA  ",
                    "   AFE               EFA   ", "    AFEE     A     EEFA    ", "     ACCEE   A   EECCA     ",
                    "      AACCEAAAAAECCAA      ", "        AACCCCCCCAA        ", "          AAAAAAA          "
            },
            {
                    "          CCCCCCC          ", "        CCBBBBBBBCC        ", "      CCBBAAABAAABBCC      ",
                    "     CBBEE FAFAF EEBBC     ", "    CBEE   FAFAF   EEBC    ", "   CBE     F A F     EBC   ",
                    "  CBE        A        EBC  ", "  CBE        A        EBC  ", " CBE         A         EBC ",
                    " CBE        AFA        EBC ", "CBA        AF FA        ABC", "CBAFFF    AF   FA    FFFABC",
                    "CBAAA    AF     FA    AAABC", "CBBFFAAAAF       FAAAAFFBBC", "CBAAA    AF     FA    AAABC",
                    "CBAFFF    AF   FA    FFFABC", "CBA        AF FA        ABC", " CBE        AFA        EBC ",
                    " CBE         A         EBC ", "  CBE        A        EBC  ", "  CBE        A        EBC  ",
                    "   CBE     F A F     EBC   ", "    CBEE   FAFAF   EEBC    ", "     CBBEE FAFAF EEBBC     ",
                    "      CCBBAAABAAABBCC      ", "        CCBBBBBBBCC        ", "          CCCCCCC          "
            },
            {
                    "          AAAAAAA          ", "        AACCCCCCCAA        ", "      AACCEAAAAAECCAA      ",
                    "     ACCEE   A   EECCA     ", "    AFEE     A     EEFA    ", "   AFE               EFA   ",
                    "  ACE                 ECA  ", "  ACE                 ECA  ", " ACE                   ECA ",
                    " ACE         A         ECA ", "ACE         A A         ECA", "ACA        A   A        ACA",
                    "ACA       A     A       ACA", "ACAAA    A       A    AAACA", "ACA       A     A       ACA",
                    "ACA        A   A        ACA", "ACE         A A         ECA", " ACE         A         ECA ",
                    " ACE                   ECA ", "  ACE                 ECA  ", "  ACE                 ECA  ",
                    "   AFE               EFA   ", "    AFEE     A     EEFA    ", "     ACCEE   A   EECCA     ",
                    "      AACCEAAAAAECCAA      ", "        AACCCCCCCAA        ", "          AAAAAAA          "
            },
            {
                    "                           ", "          AAAAAAA          ", "        AA       AA        ",
                    "      AA           AA      ", "     A               A     ", "    A                 A    ",
                    "   A                   A   ", "   A                   A   ", "  A                     A  ",
                    "  A                     A  ", " A           A           A ", " A          AAA          A ",
                    " A         AAAAA         A ", " A        AAAAAAA        A ", " A         AAAAA         A ",
                    " A          AAA          A ", " A           A           A ", "  A                     A  ",
                    "  A                     A  ", "   A                   A   ", "   A                   A   ",
                    "    A                 A    ", "     A               A     ", "      AA           AA      ",
                    "        AA       AA        ", "          AAAAAAA          ", "                           "
            }
    };

    private NaquadahFuelRefineryStructure() {}

    public static char symbolAt(int right, int down, int back) {
        return PATTERN_AISLES[back][down].charAt(right);
    }
}
