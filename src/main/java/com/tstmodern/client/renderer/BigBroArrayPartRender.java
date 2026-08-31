package com.tstmodern.client.renderer;

import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.model.machine.IControllerModelRenderer;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderType;
import com.gregtechceu.gtceu.client.util.ModelUtils;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.tstmodern.machine.BigBroArrayMachine;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Supplies the correct casing beneath formed Big Bro Array parts.
 *
 * <p>GTCEu's machine model replaces hatch base textures from the controller's
 * {@code texture_overrides}; {@code MultiblockMachineDefinition#partAppearance}
 * is only used for block appearance queries. The Big Bro core has two ability
 * planes: D is level with the controller and F is two cells down.</p>
 */
public final class BigBroArrayPartRender
                                    extends DynamicRender<BigBroArrayMachine, BigBroArrayPartRender>
                                    implements IControllerModelRenderer {

    public static final Codec<BigBroArrayPartRender> CODEC = Codec.unit(BigBroArrayPartRender::new);
    public static final DynamicRenderType<BigBroArrayMachine, BigBroArrayPartRender> TYPE =
            new DynamicRenderType<>(CODEC);

    private static final int F_PLANE_DOWN_OFFSET = 2;

    private BakedModel robustCasingModel;
    private BakedModel cleanStainlessModel;

    @Override
    public DynamicRenderType<BigBroArrayMachine, BigBroArrayPartRender> getType() {
        return TYPE;
    }

    @Override
    public void render(
                       BigBroArrayMachine machine,
                       float partialTick,
                       PoseStack poseStack,
                       MultiBufferSource buffer,
                       int packedLight,
                       int packedOverlay) {}

    @Override
    public boolean shouldRender(BigBroArrayMachine machine, Vec3 cameraPos) {
        return false;
    }

    @Override
    public boolean isBlockEntityRenderer() {
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderPartModel(
                                List<BakedQuad> quads,
                                IMultiController controller,
                                IMultiPart part,
                                Direction frontFacing,
                                @Nullable Direction side,
                                RandomSource random,
                                @NotNull ModelData modelData,
                                @Nullable RenderType renderType) {
        BlockState robustState = GTBlocks.CASING_TUNGSTENSTEEL_ROBUST.get().defaultBlockState();
        BlockState cleanState = GTBlocks.CASING_STAINLESS_CLEAN.get().defaultBlockState();
        if (robustCasingModel == null) {
            robustCasingModel = ModelUtils.getModelForState(robustState);
        }
        if (cleanStainlessModel == null) {
            cleanStainlessModel = ModelUtils.getModelForState(cleanState);
        }

        var machine = controller.self();
        BlockPos controllerPos = machine.getPos();
        Direction relativeDown = RelativeDirection.DOWN.getRelative(
                machine.getFrontFacing(), machine.getUpwardsFacing(), machine.isFlipped());
        BlockPos fPlane = controllerPos.relative(relativeDown, F_PLANE_DOWN_OFFSET);
        boolean isFPlane = fPlane.get(relativeDown.getAxis()) ==
                part.self().getPos().get(relativeDown.getAxis());

        if (isFPlane) {
            emitQuads(quads, cleanStainlessModel, machine.getLevel(), part.self().getPos(), cleanState,
                    side, random, modelData, renderType);
        } else {
            emitQuads(quads, robustCasingModel, machine.getLevel(), part.self().getPos(), robustState,
                    side, random, modelData, renderType);
        }
    }

    private static void emitQuads(
                                  List<BakedQuad> quads,
                                  @Nullable BakedModel model,
                                  BlockAndTintGetter level,
                                  BlockPos pos,
                                  BlockState state,
                                  @Nullable Direction side,
                                  RandomSource random,
                                  ModelData modelData,
                                  @Nullable RenderType renderType) {
        if (model == null) {
            return;
        }
        ModelData casingData = model.getModelData(level, pos, state, modelData);
        quads.addAll(model.getQuads(state, side, random, casingData, renderType));
    }
}
