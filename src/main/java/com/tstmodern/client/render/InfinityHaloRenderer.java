package com.tstmodern.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import com.gregtechceu.gtceu.api.item.TagPrefixItem;
import com.tstmodern.TSTModern;
import com.tstmodern.registry.TSTItems;
import com.tstmodern.registry.TSTMaterials;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Random;

/**
 * Renders a glowing halo quad and optional breathing pulse behind Infinity material
 * items in GUI context. Ported from Re-Avaritia's {@code HaloBakedModel} rendering
 * logic, but as a standalone utility invoked from a mixin instead of wrapping the
 * entire baked-model pipeline.
 */
public final class InfinityHaloRenderer {

    /** Resource location of the halo glow texture (32×32 radial gradient). */
    private static final ResourceLocation HALO_TEXTURE = TSTModern.id("misc/halo");

    /** ARGB colour for the halo quad — black (matches Re-Avaritia infinity_ingot.json). */
    private static final int HALO_COLOR = 0xFF000000;

    /** Spread factor: halo extends this many pixels beyond item on each side. */
    private static final int HALO_SIZE = 10;

    private static final Random RANDOM = new Random();

    /** Lazily baked halo quad; invalidated on resource reload (sprite reference changes). */
    private static BakedQuad cachedHaloQuad;
    private static TextureAtlasSprite cachedSprite;

    private InfinityHaloRenderer() {}

    // ------------------------------------------------------------------
    // Public API called from the mixin
    // ------------------------------------------------------------------

    /**
     * Whether the given item should receive the halo + pulse treatment.
     * Includes: infinity ingot, infinity nugget, infinity catalyst, infinity catalyst nugget.
     */
    public static boolean shouldRenderHalo(ItemStack stack) {
        if (stack.is(TSTItems.INFINITY_CATALYST.get()) || stack.is(TSTItems.INFINITY_CATALYST_NUGGET.get())) {
            return true;
        }
        if (!(stack.getItem() instanceof TagPrefixItem prefixItem)) return false;
        if (prefixItem.material != TSTMaterials.INFINITY) return false;
        String prefix = prefixItem.tagPrefix.name;
        return "ingot".equals(prefix) || "nugget".equals(prefix);
    }

    /**
     * Renders the halo quad behind the item. Call BEFORE the vanilla item rendering.
     */
    public static void renderHalo(ItemStack stack, PoseStack poseStack,
                                  MultiBufferSource bufferSource,
                                  int packedLight, int packedOverlay) {
        BakedQuad quad = getHaloQuad();
        if (quad == null) return;

        ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
        RenderType renderType = ItemBlockRenderTypes.getRenderType(stack, true);
        renderer.renderQuadList(
                poseStack, bufferSource.getBuffer(renderType),
                List.of(quad), stack, packedLight, packedOverlay);
    }

    /**
     * Renders both the halo quad behind the item and the breathing pulse on top.
     */
    public static void renderHaloAndPulse(ItemStack stack, PoseStack poseStack,
                                          MultiBufferSource bufferSource,
                                          int packedLight, int packedOverlay,
                                          BakedModel model) {
        renderHalo(stack, poseStack, bufferSource, packedLight, packedOverlay);
        renderPulse(stack, poseStack, bufferSource, packedLight, packedOverlay, model);
    }

    /**
     * Renders a semi-transparent, randomly-scaled copy of the item on top,
     * creating the "breathing" pulse effect.
     */
    public static void renderPulse(ItemStack stack, PoseStack poseStack,
                                   MultiBufferSource bufferSource,
                                   int packedLight, int packedOverlay,
                                   BakedModel model) {
        double scale = RANDOM.nextDouble() * 0.15D + 0.95D;
        double trans = (1.0D - scale) / 2.0D;

        poseStack.pushPose();
        poseStack.translate(trans, trans, 0.0D);
        poseStack.scale((float) scale, (float) scale, 1.0001F);

        ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
        BakedModel resolved = model.getOverrides().resolve(model, stack, null, null, 0);
        if (resolved == null) resolved = model;

        for (BakedModel pass : resolved.getRenderPasses(stack, true)) {
            for (RenderType renderType : pass.getRenderTypes(stack, true)) {
                VertexConsumer baseConsumer = ItemRenderer.getFoilBufferDirect(
                        bufferSource, renderType, true, stack.hasFoil());
                VertexConsumer alphaConsumer = new AlphaOverrideVertexConsumer(baseConsumer, 0.6D);
                renderer.renderModelLists(pass, stack, packedLight, packedOverlay,
                        poseStack, alphaConsumer);
            }
        }

        poseStack.popPose();
    }

    // ------------------------------------------------------------------
    // Halo quad generation (simplified from Re-Avaritia HaloUtils)
    // ------------------------------------------------------------------

    private static BakedQuad getHaloQuad() {
        TextureAtlasSprite sprite = Minecraft.getInstance()
                .getTextureAtlas(TextureAtlas.LOCATION_BLOCKS)
                .apply(HALO_TEXTURE);
        if (sprite == null || sprite.contents().name().equals(MissingTextureAtlasSprite.getLocation())) {
            return null;
        }
        // Re-bake if the sprite reference changed (resource reload).
        if (sprite != cachedSprite) {
            cachedSprite = sprite;
            cachedHaloQuad = generateHaloQuad(sprite, HALO_SIZE, HALO_COLOR);
        }
        return cachedHaloQuad;
    }

    /**
     * Builds a single {@link BakedQuad} for the halo: a textured quad larger than
     * the standard 0–1 item bounds, tinted with the given ARGB colour.
     * <p>
     * Uses manual int-array packing matching {@code DefaultVertexFormat.BLOCK}
     * (pos, color, uv, uv2/lightmap, normal) to avoid pulling in Re-Avaritia's
     * Quad / CachedFormat / VertexUtils classes.
     */
    private static BakedQuad generateHaloQuad(TextureAtlasSprite sprite, int size, int color) {
        float spread = size / 16.0F;
        float min = 0.0F - spread;
        float max = 1.0F + spread;
        float minU = sprite.getU0();
        float maxU = sprite.getU1();
        float minV = sprite.getV0();
        float maxV = sprite.getV1();

        // ARGB → individual channels for BLOCK format (expects ABGR packed int)
        int a = (color >> 24) & 0xFF;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int packedColor = (a << 24) | (b << 16) | (g << 8) | r; // ABGR for BLOCK format

        // DefaultVertexFormat.BLOCK: 8 ints per vertex (pos3, color1, uv2, uv2_lightmap, normal1)
        // 4 vertices = 32 ints
        int[] vertexData = new int[32];

        // Vertex 0: top-right (max, max, 0) → (maxU, minV)
        putVertex(vertexData, 0, max, max, 0, packedColor, maxU, minV);
        // Vertex 1: top-left (min, max, 0) → (minU, minV)
        putVertex(vertexData, 1, min, max, 0, packedColor, minU, minV);
        // Vertex 2: bottom-left (min, min, 0) → (minU, maxV)
        putVertex(vertexData, 2, min, min, 0, packedColor, minU, maxV);
        // Vertex 3: bottom-right (max, min, 0) → (maxU, maxV)
        putVertex(vertexData, 3, max, min, 0, packedColor, maxU, maxV);

        return new BakedQuad(vertexData, -1, net.minecraft.core.Direction.SOUTH, sprite, false);
    }

    private static void putVertex(int[] data, int vertex, float x, float y, float z,
                                  int color, float u, float v) {
        int i = vertex * 8;
        data[i]     = Float.floatToRawIntBits(x);
        data[i + 1] = Float.floatToRawIntBits(y);
        data[i + 2] = Float.floatToRawIntBits(z);
        data[i + 3] = color;
        data[i + 4] = Float.floatToRawIntBits(u);
        data[i + 5] = Float.floatToRawIntBits(v);
        data[i + 6] = 0x00F000F0; // lightmap (full bright)
        data[i + 7] = 0x007F0000; // normal (Z = 127 in byte 2, facing SOUTH)
    }
}
