package com.tstmodern.registry.machine;

/** Exact TST source layers transposed to GTCEu aisles: [back][down][right]. */
public final class OreProcessingFactoryStructure {
    public static final int WIDTH = 32;
    public static final int HEIGHT = 13;
    public static final int DEPTH = 15;
    public static final int CONTROLLER_RIGHT = 30;
    public static final int CONTROLLER_DOWN = 11;
    public static final int CONTROLLER_BACK = 0;

    private static final String[][] SOURCE_LAYERS = {
            {
                "                                ",
                "                             LLL",
                "                             LLL",
                "                             LLL",
                "                             LLL",
                "                             LLL",
                "                             LLL",
                "LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL",
                "LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL",
                "LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL",
                "LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL",
                "                                ",
                "                                ",
                "                                ",
                "                                "
            },
            {
                "                                ",
                "                                ",
                "                             MDM",
                "                              D ",
                "                              D ",
                "                              D ",
                "GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG",
                "FHHFFHHFFHHFFHHFFHHFFHHFFHHFFHHF",
                "FDDFFDDFFDDFFDDFFDDFFDDFFDDFFDDF",
                "FDDFFDDFFDDFFDDFFDDFFDDFFDDFFDDF",
                "FHHFFHHFFHHFFHHFFHHFFHHFFHHFFHHF",
                "GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG",
                "                                ",
                "                                ",
                "                                "
            },
            {
                "                                ",
                "                                ",
                "                             MDM",
                "                                ",
                "                                ",
                "GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG",
                "GCC  CC  CC  CC  CC  CC  CC  CCG",
                "FCC  CC  CC  CC  CC  CC  CC  CCF",
                "A                              A",
                "A                              A",
                "FCC  CC  CC  CC  CC  CC  CC  CCF",
                "GCC  CC  CC  CC  CC  CC  CC  CCG",
                "GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG",
                "                                ",
                "                                "
            },
            {
                "                                ",
                "                                ",
                "                             MDM",
                "EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE",
                "EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE",
                "GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG",
                "GCC  CC  CC  CC  CC  CC  CC  CCG",
                "FCC  CC  CC  CC  CC  CC  CC  CCF",
                "A                              A",
                "A                              A",
                "FCC  CC  CC  CC  CC  CC  CC  CCF",
                "GCC  CC  CC  CC  CC  CC  CC  CCG",
                "GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG",
                "EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE",
                "EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE"
            },
            {
                "                                ",
                "                                ",
                "                             MDM",
                " HH  HH  HH  HH  HH  HH  HH  HH ",
                "GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG",
                "GDD  DD  DD  DD  DD  DD  DD  DDG",
                "GBB  BB  BB  BB  BB  BB  BB  BBG",
                "FBB  BB  BB  BB  BB  BB  BB  BBF",
                "A                              A",
                "A                              A",
                "FBB  BB  BB  BB  BB  BB  BB  BBF",
                "GBB  BB  BB  BB  BB  BB  BB  BBG",
                "GDD  DD  DD  DD  DD  DD  DD  DDG",
                "GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG",
                " HH  HH  HH  HH  HH  HH  HH  HH "
            },
            {
                "                                ",
                "                                ",
                "                             MDM",
                " HH  HH  HH  HH  HH  HH  HH  HH ",
                "GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG",
                "EDD  DD  DD  DD  DD  DD  DD  DDE",
                "GBB  BB  BB  BB  BB  BB  BB  BBG",
                "FBB  BB  BB  BB  BB  BB  BB  BBF",
                "A                              A",
                "A                              A",
                "FBB  BB  BB  BB  BB  BB  BB  BBF",
                "GBB  BB  BB  BB  BB  BB  BB  BBG",
                "EDD  DD  DD  DD  DD  DD  DD  DDE",
                "GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG",
                " HH  HH  HH  HH  HH  HH  HH  HH "
            },
            {
                "                                ",
                "                                ",
                "                             MDM",
                " HH  HH  HH  HH  HH  HH  HH  HH ",
                "GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG",
                "EDD  DD  DD  DD  DD  DD  DD  DDE",
                "GBB  BB  BB  BB  BB  BB  BB  BBG",
                "FBB  BB  BB  BB  BB  BB  BB  BBF",
                "A                              A",
                "A                              A",
                "FBB  BB  BB  BB  BB  BB  BB  BBF",
                "GBB  BB  BB  BB  BB  BB  BB  BBG",
                "EDD  DD  DD  DD  DD  DD  DD  DDE",
                "GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG",
                " HH  HH  HH  HH  HH  HH  HH  HH "
            },
            {
                "                             JJJ",
                "                             EEE",
                "                             MDM",
                " HH  HH  HH  HH  HH  HH  HH  HH ",
                "GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG",
                "EDD  DD  DD  DD  DD  DD  DD  DDE",
                "GBB  BB  BB  BB  BB  BB  BB  BBG",
                "FBB  BB  BB  BB  BB  BB  BB  BBF",
                "A                              A",
                "A                              A",
                "FBB  BB  BB  BB  BB  BB  BB  BBF",
                "GBB  BB  BB  BB  BB  BB  BB  BBG",
                "EDD  DD  DD  DD  DD  DD  DD  DDE",
                "GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG",
                " HH  HH  HH  HH  HH  HH  HH  HH "
            },
            {
                "                             MJM",
                "                             MDM",
                "                             MDM",
                " HH  HH  HH  HH  HH  HH  HH  HH ",
                "GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG",
                "EDD  DD  DD  DD  DD  DD  DD  DDE",
                "GBB  BB  BB  BB  BB  BB  BB  BBG",
                "FBB  BB  BB  BB  BB  BB  BB  BBF",
                "A                              A",
                "A                              A",
                "FBB  BB  BB  BB  BB  BB  BB  BBF",
                "GBB  BB  BB  BB  BB  BB  BB  BBG",
                "EDD  DD  DD  DD  DD  DD  DD  DDE",
                "GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG",
                " HH  HH  HH  HH  HH  HH  HH  HH "
            },
            {
                "                             JJJ",
                "                             EEE",
                "                             EEE",
                " HH  HH  HH  HH  HH  HH  HH  HH ",
                "GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG",
                "EDD  DD  DD  DD  DD  DD  DD  DDE",
                "GBB  BB  BB  BB  BB  BB  BB  BBG",
                "FBB  BB  BB  BB  BB  BB  BB  BBF",
                "A                              A",
                "A                              A",
                "FBB  BB  BB  BB  BB  BB  BB  BBF",
                "GBB  BB  BB  BB  BB  BB  BB  BBG",
                "EDD  DD  DD  DD  DD  DD  DD  DDE",
                "GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG",
                " HH  HH  HH  HH  HH  HH  HH  HH "
            },
            {
                "                             GGG",
                "                             KKK",
                "                             GGG",
                " HH  HH  HH  HH  HH  HH  HH  HH ",
                "GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG",
                "EDD  DD  DD  DD  DD  DD  DD  DDE",
                "GBB  BB  BB  BB  BB  BB  BB  BBG",
                "FBB  BB  BB  BB  BB  BB  BB  BBF",
                "A                              A",
                "A                              A",
                "FBB  BB  BB  BB  BB  BB  BB  BBF",
                "GBB  BB  BB  BB  BB  BB  BB  BBG",
                "EDD  DD  DD  DD  DD  DD  DD  DDE",
                "GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG",
                " HH  HH  HH  HH  HH  HH  HH  HH "
            },
            {
                "                             G~G",
                "                             KIK",
                "                             GHG",
                " HH  HH  HH  HH  HH  HH  HH  HH ",
                "GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG",
                "EDD  DD  DD  DD  DD  DD  DD  DDE",
                "GBB  BB  BB  BB  BB  BB  BB  BBG",
                "FBB  BB  BB  BB  BB  BB  BB  BBF",
                "A                              A",
                "A                              A",
                "FBB  BB  BB  BB  BB  BB  BB  BBF",
                "GBB  BB  BB  BB  BB  BB  BB  BBG",
                "EDD  DD  DD  DD  DD  DD  DD  DDE",
                "GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG",
                " HH  HH  HH  HH  HH  HH  HH  HH "
            },
            {
                "                             GGG",
                "                             KKK",
                "                             GGG",
                " GGGGGGGGGGGGGGGGGGGGGGGGGGGGGG ",
                "GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG",
                "GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG",
                "GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG",
                "GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG",
                "GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG",
                "GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG",
                "GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG",
                "GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG",
                "GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG",
                "GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG",
                " GGGGGGGGGGGGGGGGGGGGGGGGGGGGGG "
            }
    };

    public static final String[][] PATTERN_AISLES = transpose(SOURCE_LAYERS);

    private OreProcessingFactoryStructure() {}

    public static char symbolAt(int right, int down, int back) {
        return PATTERN_AISLES[back][down].charAt(right);
    }

    private static String[][] transpose(String[][] layers) {
        String[][] aisles = new String[DEPTH][HEIGHT];
        for (int back = 0; back < DEPTH; back++) {
            for (int down = 0; down < HEIGHT; down++) {
                aisles[back][down] = layers[down][back];
            }
        }
        return aisles;
    }
}
