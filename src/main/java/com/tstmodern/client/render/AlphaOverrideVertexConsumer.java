package com.tstmodern.client.render;

import com.mojang.blaze3d.vertex.VertexConsumer;

import org.jetbrains.annotations.NotNull;

/**
 * Wraps a {@link VertexConsumer} and overrides the alpha component of every vertex colour.
 * Used by the infinity-item pulse effect to render a semi-transparent copy of the item.
 * <p>
 * Ported from Re-Avaritia (covers1624).
 */
public class AlphaOverrideVertexConsumer implements VertexConsumer {

    private final VertexConsumer delegate;
    private final int alpha;

    public AlphaOverrideVertexConsumer(VertexConsumer delegate, double alpha) {
        this(delegate, (int) (255 * alpha));
    }

    public AlphaOverrideVertexConsumer(VertexConsumer delegate, int alpha) {
        this.delegate = delegate;
        this.alpha = alpha;
    }

    // --- Delegation with alpha override ---

    @Override
    public @NotNull VertexConsumer vertex(double x, double y, double z) {
        delegate.vertex(x, y, z);
        return this;
    }

    @Override
    public @NotNull VertexConsumer color(int r, int g, int b, int a) {
        delegate.color(r, g, b, alpha);
        return this;
    }

    @Override
    public @NotNull VertexConsumer uv(float u, float v) {
        delegate.uv(u, v);
        return this;
    }

    @Override
    public @NotNull VertexConsumer overlayCoords(int u, int v) {
        delegate.overlayCoords(u, v);
        return this;
    }

    @Override
    public @NotNull VertexConsumer uv2(int u, int v) {
        delegate.uv2(u, v);
        return this;
    }

    @Override
    public @NotNull VertexConsumer normal(float x, float y, float z) {
        delegate.normal(x, y, z);
        return this;
    }

    @Override
    public void endVertex() {
        delegate.endVertex();
    }

    @Override
    public void defaultColor(int r, int g, int b, int a) {
        delegate.defaultColor(r, g, b, alpha);
    }

    @Override
    public void unsetDefaultColor() {
        delegate.unsetDefaultColor();
    }
}
