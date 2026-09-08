package com.tstmodern.registry.machine;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPInputStream;

/** Exact audited TST source pattern in GTCEu [back][down][right] order. */
public final class MegaNaquadahReactorStructure {
    public static final int WIDTH = 31;
    public static final int HEIGHT = 27;
    public static final int DEPTH = 32;
    public static final int CONTROLLER_RIGHT = 15;
    public static final int CONTROLLER_DOWN = 25;
    public static final int CONTROLLER_BACK = 1;

    private static final String COMPRESSED_ROWS =
            "H4sIAAAAAAACCu2da7KjIBCF/7sKtqLp9LJm7VOCKM9uDJqo91hTMyHHNjzOF4EgY4x4DAY6dOiNOhEVdHbH+maQbrp+bzx06L/Ud/j3nxzv+ErimdzhPyBMD3Nycsd60SU9nzAYpmo88ayzCbJugkLMf+b4auFtPNofuqhzVeRFr/rP6VX/cu7/V+b/iBfLV8YPBRfcPo48P/7wgE3BCYMhqsYTOf64UMD5zSW+rpMrf6V+fPm5XMObXqt+6NfQtfYT21/zl67X/cuZ/1+Z/yN+/F9FfqwYJ2N+LMLTlPIjxxuulm+pv3dwazTBTfK96sX63/RK+0F/iK61v+If2X/7/P/a6/8onR08mPBy850ySrr4yveLi2fmSv0wO/6mij4t9TNV6t/rSv8A/aub9++09pf9w8L97yD/S/H7rp8cnq93Poai98aXGQvlG83KF/hp0/2XJvhL+Cv7a+Or7M8P+Cr7/2y+DGUFmN9x8babPRaKv/a/wU8TP+TH2eEZ4M/6p+ivdf6i4k/rv1vwlRXAphe+1uKmxUf/KOEnfRHptHaDpok+iH92/Sn+KvjTvXETvpICuFTIV1IB4x8df4zjWNPJyfaEAj929srrG2DN8ern37x+FX+V/HknvqICLK8jvqIKGG/avt6/FX0csxOGzNzxCUOAxyLM/2T8zHgF+gpYa7z6+Q35V8v/0/ZR/FXw5634CjLtX8V8BYUe78pP/k05FFq14l+x/JToCT8Wr1D3gDXGN9W/kn+1/D/mTylf7s+v8GWkE+z83iRc3s3/J1j57FOy/kJtvwfzo32/UqLnfMV6zpcY39J/uD1/Sv4yfzr9AP/L8SzINn9T/fIuPh51bd8TvLf+T+ZL67+dyI+iU6ZH/MS3r/AG1hbfnb+D+NP6n73tK/sr9adJ1y996n85/jC+3KT8lv2ML1v0U/sP4vhdGX+cyY/OVzo+T/lK9ZQvMf70/Lfxp43/uudfFH8l/vwSX9UT2Odvql7exQdTxBxMDnNh/T6TWH+dutz/CMb/3+dLnt86ny91/vYbfIn139J/7PRH7E9jDvK/En8cX4ZfYSIvf6Sbw3Xw9XC+TvFP6P/XpfnC/Qt84f5FtedTMP7C+Avjr8vOb2D+EPOHmD9M4/H7F37/wu9fp/3+hfUbl+AP6ze69Wuu38D6wwP5w/rD37XPs9cfYv187/wL1s936c9eP4/nv/p1PP/Voe9//utOfOH55UN0PL/8if7855ex/8YV+HsyP1L7P2n/jf3752j792D/qL+uP3v/KOx/CP3q+k/3PzTd/sf+vdB/qt9o/14j799b4Af7z0N/dv/yyP3nc/+r/OD/T4H+t/UT/f8fxVSsxv9rAAA=";

    public static final String[][] PATTERN_AISLES = inflate();

    public static char symbolAt(int right, int down, int back) {
        return PATTERN_AISLES[back][down].charAt(right);
    }

    private static String[][] inflate() {
        try (var gzip = new GZIPInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(COMPRESSED_ROWS)))) {
            String[] rows = new String(gzip.readAllBytes(), StandardCharsets.UTF_8).split("\n", -1);
            if (rows.length != DEPTH * HEIGHT) throw new IllegalStateException("Mega Nq pattern row count changed");
            String[][] aisles = new String[DEPTH][HEIGHT];
            for (int back = 0; back < DEPTH; back++) {
                for (int down = 0; down < HEIGHT; down++) {
                    String row = rows[back * HEIGHT + down];
                    if (row.length() != WIDTH) throw new IllegalStateException("Mega Nq pattern width changed");
                    aisles[back][down] = row;
                }
            }
            if (aisles[CONTROLLER_BACK][CONTROLLER_DOWN].charAt(CONTROLLER_RIGHT) != '~') {
                throw new IllegalStateException("Mega Nq controller offset changed");
            }
            return aisles;
        } catch (IOException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private MegaNaquadahReactorStructure() {}
}
