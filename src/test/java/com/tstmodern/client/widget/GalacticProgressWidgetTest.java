package com.tstmodern.client.widget;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;

final class GalacticProgressWidgetTest {
    @Test
    void rocketUsesActualAtlasDimensionsWithoutArrowPixelsOrLostColors() throws Exception {
        BufferedImage atlas = ImageIO.read(Path.of(
                "src/main/resources/assets/tstmodern/textures/gui/progress/galactic_armillary.png").toFile());
        boolean[][] copied = new boolean[19][18];
        // Inspect the blit contract without initializing Minecraft's GPU-only GuiGraphics.
        String source = Files.readString(Path.of(
                "src/main/java/com/tstmodern/client/widget/GalacticProgressWidget.java"));
        var blits = Pattern.compile("graphics\\.blit\\(texture, x(?: \\+ (\\d+))?, y(?: \\+ (\\d+))?, "
                + "(\\d+), (\\d+), (\\d+), (\\d+), (\\d+), (\\d+)\\);").matcher(source);
        int count = 0;
        while (blits.find()) {
            count++;
            int x = blits.group(1) == null ? 0 : Integer.parseInt(blits.group(1));
            int y = blits.group(2) == null ? 0 : Integer.parseInt(blits.group(2));
            int u = Integer.parseInt(blits.group(3)), v = Integer.parseInt(blits.group(4));
            int width = Integer.parseInt(blits.group(5)), height = Integer.parseInt(blits.group(6));
            int textureWidth = Integer.parseInt(blits.group(7)), textureHeight = Integer.parseInt(blits.group(8));
            assertEquals(atlas.getWidth(), textureWidth);
            assertEquals(atlas.getHeight(), textureHeight);
            for (int dx = 0; dx < width; dx++) {
                for (int dy = 0; dy < height; dy++) {
                    int sx = u + dx;
                    int sy = v + dy;
                    assertEquals(sx - 43, x + dx);
                    assertEquals(sy - 66, y + dy);
                    copied[x + dx][y + dy] = true;
                    int pixel = atlas.getRGB(sx, sy);
                    int r = (pixel >>> 16) & 255;
                    int g = (pixel >>> 8) & 255;
                    int b = pixel & 255;
                    assertFalse((pixel >>> 24) != 0 && r == g && g == b && r > 40,
                            "Arrow pixel included at " + sx + "," + sy);
                }
            }
        }
        assertTrue(count > 0, "Rocket blits must be checked");
        for (int x = 0; x < 19; x++) {
            for (int y = 0; y < 18; y++) {
                int pixel = atlas.getRGB(43 + x, 66 + y);
                int r = (pixel >>> 16) & 255;
                int g = (pixel >>> 8) & 255;
                int b = pixel & 255;
                if ((pixel >>> 24) != 0 && (r != g || g != b)) {
                    assertTrue(copied[x][y], "Rocket color lost at " + x + "," + y);
                }
            }
        }
    }
}
