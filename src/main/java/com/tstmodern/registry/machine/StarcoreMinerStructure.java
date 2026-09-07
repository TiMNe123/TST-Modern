package com.tstmodern.registry.machine;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPInputStream;

/** Exact audited TST source pieces in GTCEu [back][down][right] order. */
public final class StarcoreMinerStructure {
    public static final int WIDTH = 21;
    public static final int HEIGHT = 26;
    public static final int DEPTH = 31;
    public static final int CONTROLLER_RIGHT = 10;
    public static final int CONTROLLER_DOWN = 22;
    public static final int CONTROLLER_BACK = 1;
    public static final int HEIGHT_LIMIT_Y = 24;

    public static final String[][] MAIN_AISLES = read("H4sIAAAAAAACCu3a/26DIBSG4f9NL4JbsT8E0WvatQ+Eg6iHatEtsH0nWdY+adqkvKFqK4QbacffFo3gBgqN5xanE+LBu1O7juPI6dd48LFaG9X6rd4W6VA84Xml/4NWpuyniJS9PPZYTfNOd9pRqlVq82rQwlUps5SBG0Jl2lHEDaF7rOdZTSI7utOOMK/G7HLQP6Vyo5fsO7bUbetP+eS0hZahyX1H/uK+k/iENO3gqKJoNce0Z/STfSd5ntX2LbPLmXZwNlOynq3vo3ZS13fQzr8sCu1Ac4tCO9BcRTtQtANN68vOxarjSSvaqUbvdlb6orlStV5nwivaqUTvNJG+4rlMtd5kwivaqUMpGvqPdqAHdf608rcaWvjODi39FerTmJQy4RXtVNKOmA974na6ztxwS7/QEMTHGkVCmSQU7dSgUTr+ThOlM8cT1BWVoyESp+4ur3vtPB4PcVSZ7/LtDAJ6Un07w7Ydr+seJu1ylCrxumpnobvt9Gw7rPK/Dhn6AXpSXTteV+1MuunBapejoRKn63Zixb6Dfeen9h0ca+B4J/d4B+uG86zd8yysEK7v5F7fwQrhunLudWWsUNla8vdZWKHStdzv0bFCdWoJv9/BWkBF5u8G8Z5BY40aSek3RNAuZ0NFAAA=");
    public static final String[][] MIDDLE_AISLES = read("H4sIAAAAAAACClNQcAMBBQUeCAPI5AHzQVweEMMbxASxvD29FdDEYOoQehVg5gEAioco+FkAAAA=");
    public static final String[][] END_AISLES = read("H4sIAAAAAAACCosCAGdXvFkBAAAA");

    public static char symbolAt(int right, int down, int back) {
        return MAIN_AISLES[back][down].charAt(right);
    }

    private static String[][] read(String compressed) {
        try (GZIPInputStream input = new GZIPInputStream(
                new ByteArrayInputStream(Base64.getDecoder().decode(compressed)))) {
            String[] layers = new String(input.readAllBytes(), StandardCharsets.UTF_8).split("\f", -1);
            String[][] result = new String[layers.length][];
            for (int index = 0; index < layers.length; index++) result[index] = layers[index].split("\n", -1);
            return result;
        } catch (IOException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private StarcoreMinerStructure() {}
}
