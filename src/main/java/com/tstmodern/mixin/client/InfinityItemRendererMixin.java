package com.tstmodern.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tstmodern.client.render.InfinityHaloRenderer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Injects halo glow and breathing pulse rendering for Infinity items into ItemRenderer.
 * Ported from Re-Avaritia's halo rendering behavior.
 */
@Mixin(ItemRenderer.class)
public abstract class InfinityItemRendererMixin {

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/resources/model/BakedModel;isCustomRenderer()Z"
            )
    )
    private void tstmodern$onRenderItem(ItemStack stack, ItemDisplayContext context, boolean leftHand,
                                        PoseStack poseStack, MultiBufferSource bufferSource,
                                        int packedLight, int packedOverlay,
                                        BakedModel model, CallbackInfo ci) {
        if (context == ItemDisplayContext.GUI && InfinityHaloRenderer.shouldRenderHalo(stack)) {
            InfinityHaloRenderer.renderHaloAndPulse(stack, poseStack, bufferSource, packedLight, packedOverlay, model);
        }
    }
}
