package com.tstmodern.client.widget;

import java.util.function.DoubleSupplier;
import java.util.function.BooleanSupplier;

import com.lowdragmc.lowdraglib.gui.widget.ProgressWidget;
import com.tstmodern.TSTModern;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

/** Compact segmented progress bar with a moving pixel rocket. */
public final class GalacticProgressWidget extends ProgressWidget {
    private static final int SEGMENTS = 20;
    private final BooleanSupplier visible;

    public GalacticProgressWidget(DoubleSupplier progress, BooleanSupplier visible, int x, int y) {
        super(progress, x, y, 146, 18);
        this.visible = visible;
    }

    @Override
    public void drawInBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (!visible.getAsBoolean()) return;
        double progress = Mth.clamp(getLastProgressValue(), 0, 1);
        int x = getPositionX() + 9;
        int y = getPositionY() + 9;
        int filled = (int) Math.round(progress * SEGMENTS);
        for (int segment = 0; segment < SEGMENTS; segment++) {
            graphics.fill(x + segment * 5, y, x + segment * 5 + 4, y + 4,
                    segment < filled ? 0xFFCA52FF : 0xFF34343E);
        }

        int rocketX = x + (int) Math.round(progress * 80);
        drawRocket(graphics, rocketX, y - 9);
        graphics.drawString(Minecraft.getInstance().font,
                (int) Math.round(progress * 100) + "%", x + 108, y - 3, 0xFFE8E8E8, false);
    }

    private static void drawRocket(GuiGraphics graphics, int x, int y) {
        // Fornax is 90x98 (two 90x49 frames). These strips isolate the original
        // 19x18 rocket at (43,66), excluding the arrow crossing its middle rows.
        var texture = TSTModern.id("textures/gui/progress/galactic_armillary.png");
        graphics.blit(texture, x, y, 43, 66, 19, 7, 90, 98);
        graphics.blit(texture, x + 2, y + 7, 45, 73, 15, 1, 90, 98);
        graphics.blit(texture, x + 4, y + 8, 47, 74, 12, 1, 90, 98);
        graphics.blit(texture, x + 5, y + 9, 48, 75, 11, 1, 90, 98);
        graphics.blit(texture, x, y + 10, 43, 76, 16, 8, 90, 98);
    }
}
