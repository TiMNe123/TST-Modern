package com.tstmodern.registry.machine;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPInputStream;

/** Exact TST source pattern: [back][down][right]. */
public final class AstralComputingArrayStructure {
    public static final int WIDTH = 47;
    public static final int HEIGHT = 35;
    public static final int DEPTH = 47;
    public static final int CONTROLLER_RIGHT = 23;
    public static final int CONTROLLER_DOWN = 34;
    public static final int CONTROLLER_BACK = 0;

    private static final String COMPRESSED_ROWS =
            "H4sIAAAAAAAACu2d7XaqOhCG/++r4FZEsaJlVW/oXPtZCQn5JCEKKPKMe7c1eZtFh2QehmBSVUX2r0yOHv/QHxgvxIe9xsMmbv+NlH/c8aPHP/QHxgvx4TviYXMW1p7bszHxLiwRBo/efb7Q4x/6A+PlW+MDPHqv/9Hjnw32h25Zfdd1JfpO2ILtl/+9Y4Ze2FFY3D/N+fzz89P+tD/G+p/dEvGO/Ij+xvgi/hTH264T/ybrpTb+C3G99XWJ9kuP/wnejdlG9GN46fX3e9M0d2MSR8ejVSLrLR5NM3i01vlFv2//XK/XpGyoX0k/2Z7khXs8oT55PFrYLdR+8fGH/Mr6M5N/rXu+cnypqrquHbn6P9hQ3/NIPnpgN97bUCLr4dHz5ws9/inpD4Xx5CosLVf16+iXjofX6re6Jvhi6p/j0Wvt53nkth/mX5n2s/mU135g2fZzfPHlPl88vYCNBaReav2CqZf6pqnr2mRIA49MdiTrpb5pW31jrlVm7tK5BW3b8jxDxuAX/on0h93x6JCUm3p1PL+H3+TxDPWx9mM8SrUvdH8uj2ZtP+RXuv3Y/FfOPy4tcu2HPHLb9/VH+RrX19XB41H/T5up1zw6KN6IbMnwSJVY9ZH7dRaPuF8HX9KGfyb5Z3c8mmozXf8XtU9+5PtnzfzI4dGE/MjhEfnRU/0fPf55qT981/xRXr/08bzWPvNH880feTzKzh95POJ5hjKDR/iH/rD78cLzdXY8vAvgqALBIP04w8AjWe/zSGdHLo/sCSSer4NH8JfrE67HuP4suT6/W+SRPw/ZkVNq8UhzqP+uP3/klsIj8iPyQfJl7g9wP6TsflFIHvNK8Sj9gkfwCB7BI3gEj9bhUZxL5EcFRrzCP/QHxgvx4XUeDfNHzsuaVWL+CB7B3yLj+gT/7L4/PMcj6xkGJzti/TriD/EWvsDfPVxv3FybRS9po+3mvLSFPPIXZ/CWaCA/eu78osc/9AfGy1vjQ8H658U8ai/G2nEeDfrLw9jF6H0e5WxTPCpdf3759erHDD3+oT8wXsriQ1m8UtLob7zOo4sBUnuJ663s6H5/GCBdHla5y6M2kx+1W+JR8frzy69XX8i7UUOPf76wP4wtb22ss/Wjl4NGrhS6/cwvDPX6+LPtv/N6uCxeGV23CI8GILWXMR5Zq3nfBIY0jh5Kb1az28h+E23bTtd3St8tp3e+z847+JXxz7ih/wT/5PGiNVKfpYsR9fpIZaQs0X5Eb7Uf7hfh6U19lHfheA9490p8KIw/S/DIrM9660Gkvmr9UC/1znLePYjUV2fB7+j6dbH8yPw8E49kq0vpe7yMAGYGfdUp/XL8Ks2/uH+Y8c/e9KX3k+fVF/OiTB+vipfG24/qE8eT0Xv8Csd7wC8XVomyl/kSbTXFlyiPXL7Y69XdNIoklHS+M9T/8/c7umkUSShJvV0/U35UlL8U80jdMZyoV7iIA2MWfTG/yvKvNfKpzP5f6LcU/0P56BleRV8Yz0v5Em8pXvyMPqiJ6738y5yhgC9WfSyfivAolU9N4ZG1v0SURwm+xHjk8sVZz1vy6NKqHKnXW/UOj3R+JGDU50jufkgqP3KSoUh+ZL2BR9/Bo+5UGA/Rb8k/XXUq5EtK303Ud+/Sd1V1yvDoL6P/e6F9f7yKH05+gZdP2eM10Fv1+nxZq2fLCqG3j+iY4dEpw6NhvyPFoZPHo6PHo6PLo8vp4vJI18fzo8fj1H8jP/I7FfmRY+RH9igOjfxIe4f8aOgP5Eeflh+NGvNHzB9N4V1Z/0Gf8Q/zR8wf6a5gd4u9zB+NGs/XrTMftLD/0eOfgv4wll3aZt2P4vm6UafxfN0az9ctrC+N/8V6d/4yq4cvGf+MG/pd+CcPMGd+n88fFcSrsufx+PxRzp9Jmyf+sz7DvP5Hj3/oD8uNF9Zn2NT6DOjxD/2B8UJ8UMb6dfACPnL9wPUS14cbu35mfe/3+h89/qE/MF6ID8vFQ/Y/Ynwxvrje4HqM689PuN5mf1h4BI/gETyCRxvmUWR3WLuU5xmWOl/o8Q/9gfHyrfHhWR6lX/BoqfOFHv/QHxgv3xofAvKo/Y6m50fy46/kRx96ftHjH/rDJ4+Xve0fba3fGtHfq6ZpVIHgjl4eyPBI1Pv78fVLMWgaDUSyFmjg80drnV/0+/bP9XpNyob6lfSf9veu235+/Rav/WA/v+zxZ9bvXft8pflSVXVdO3J3BXKrvudRI6wv0NmRzpCkyfr4/rA+jz5j/Tr0+GfL/aEwnlyFpeWqfh390vHwWv1WyeMZ6udoP9xvKNN+lkfe8QfriWWPP5NPveyfHF98uc8XTy9gYwHJ3g/Jq5f6pqnrumn6zWBdHklT9dH9YT0ebXL9OvT457P6w+54dEjKTb06nt/Db/J4hvpY+5H97JLt6/2QuoXaD/mVbj+2fkLOPy4tcu2HPHLb9/XWfkhRfV0dPB4N+yF59ZpHB8UbkSFZPOrNqo+s723z6FPW90aPf7bcH3bHozXzlxgv5myf/GjO/Mja70gXJPMjbz8k8qNviIfot+Wf75o/Kp1/CfXJ48nz6LX2mT+acf7I3w8pN3+UMvKjrcY39Pjnjf0hmN+fdz+XFfaLKTp+nq9z/SMyoCE7MgmSVSLrpR4eJY14jn/oD5GA+1H7vy+/X0zp3ztm6IW52ZHjH/P5I38GyS3h87D0N8YX8YR4C1+W5G9zFtae27Mx8S4sEcbzDIxHxuOS4xE9/tlzf4BH7/U/evxDf2C8EB+qnkdl9j8mF0hHbzQBAA==";

    public static final String[][] PATTERN_AISLES = inflate();

    private AstralComputingArrayStructure() {}

    public static char symbolAt(int right, int down, int back) {
        return PATTERN_AISLES[back][down].charAt(right);
    }

    private static String[][] inflate() {
        try (var gzip = new GZIPInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(COMPRESSED_ROWS)))) {
            String[] rows = new String(gzip.readAllBytes(), StandardCharsets.UTF_8).split("\n", -1);
            if (rows.length != DEPTH * HEIGHT) {
                throw new IllegalStateException("Astral pattern row count changed: " + rows.length);
            }
            String[][] aisles = new String[DEPTH][HEIGHT];
            for (int back = 0; back < DEPTH; back++) {
                for (int down = 0; down < HEIGHT; down++) {
                    String row = rows[back * HEIGHT + down];
                    if (row.length() != WIDTH) {
                        throw new IllegalStateException("Astral pattern width changed at " + back + "," + down);
                    }
                    aisles[back][down] = row;
                }
            }
            if (symbolAt(aisles, CONTROLLER_RIGHT, CONTROLLER_DOWN, CONTROLLER_BACK) != '~') {
                throw new IllegalStateException("Astral controller offset changed");
            }
            return aisles;
        } catch (IOException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static char symbolAt(String[][] aisles, int right, int down, int back) {
        return aisles[back][down].charAt(right);
    }
}
