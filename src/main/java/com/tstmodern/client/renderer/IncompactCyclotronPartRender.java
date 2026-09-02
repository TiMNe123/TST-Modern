package com.tstmodern.client.renderer;

import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.client.model.machine.IControllerModelRenderer;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderType;
import com.gregtechceu.gtceu.client.util.ModelUtils;
import com.tstmodern.machine.IncompactCyclotronMachine;
import com.tstmodern.registry.machine.IncompactCyclotronDefinition;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Keeps E-role hatches on Fusion Glass and F-role hatches on Dense Cyclotron casing. */
public final class IncompactCyclotronPartRender
                                         extends DynamicRender<IncompactCyclotronMachine, IncompactCyclotronPartRender>
                                         implements IControllerModelRenderer {

    public static final Codec<IncompactCyclotronPartRender> CODEC = Codec.unit(IncompactCyclotronPartRender::new);
    public static final DynamicRenderType<IncompactCyclotronMachine, IncompactCyclotronPartRender> TYPE =
            new DynamicRenderType<>(CODEC);

    private final Map<BlockState, BakedModel> models = new HashMap<>();

    @Override
    public DynamicRenderType<IncompactCyclotronMachine, IncompactCyclotronPartRender> getType() {
        return TYPE;
    }

    @Override
    public void render(IncompactCyclotronMachine machine, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {}

    @Override
    public boolean shouldRender(IncompactCyclotronMachine machine, Vec3 cameraPos) {
        return false;
    }

    @Override
    public boolean isBlockEntityRenderer() {
        return false;
    }

    @Override
    public void renderPartModel(List<BakedQuad> quads, IMultiController controller, IMultiPart part,
                                Direction frontFacing, @Nullable Direction side, RandomSource random,
                                @NotNull ModelData modelData, @Nullable RenderType renderType) {
        BlockState state = IncompactCyclotronDefinition.partAppearance(controller, part, side);
        BakedModel model = models.computeIfAbsent(state, ModelUtils::getModelForState);
        ModelData casingData = model.getModelData(
                controller.self().getLevel(), part.self().getPos(), state, modelData);
        quads.addAll(model.getQuads(state, side, random, casingData, renderType));
    }
}
