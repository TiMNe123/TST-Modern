package com.tstmodern.client.renderer;

import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderType;
import com.tstmodern.machine.DraconicCrucibleMachine;
import com.tstmodern.registry.TSTBlocks;
import com.tstmodern.registry.machine.DraconicCrucibleStructure;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mojang.serialization.Codec;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.slf4j.Logger;

public final class DraconicCrucibleDragonRender
        extends DynamicRender<DraconicCrucibleMachine, DraconicCrucibleDragonRender> {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final Codec<DraconicCrucibleDragonRender> CODEC =
            Codec.unit(DraconicCrucibleDragonRender::new);
    public static final DynamicRenderType<DraconicCrucibleMachine, DraconicCrucibleDragonRender> TYPE =
            new DynamicRenderType<>(CODEC);

    public static final ResourceLocation DRAGON_TEXTURE =
            new ResourceLocation("tstmodern", "textures/entity/draconic_crucible_dragon.png");
    public static final ResourceLocation DRAGON_SLEEPING_TEXTURE =
            new ResourceLocation("tstmodern", "textures/entity/draconic_crucible_dragon_sleeping.png");
    public static final ResourceLocation DRAGON_GLOW_TEXTURE =
            new ResourceLocation("tstmodern", "textures/entity/draconic_crucible_dragon_glow.png");
    public static final ResourceLocation DRAGON_FLAME_TEXTURE =
            new ResourceLocation("tstmodern", "textures/particles/draconic_crucible_dragon_flame.png");
    private static final float STAGE_FIVE_SCALE = 10.0F;
    private static final float DRAGON_YAW_OFFSET = 90.0F;
    private static final float FLIGHT_YAW_CORRECTION = 180.0F;
    private static final double DRAGON_RIGHT_OFFSET =
            DraconicCrucibleStructure.CONTROLLER_X - DraconicCrucibleStructure.CORE_X
                    + 4.5D;
    private static final double DRAGON_UP_OFFSET = -5.75D;
    private static final double DRAGON_BACK_OFFSET = 0.0D;
    private static final double IDLE_RIGHT_OFFSET = 35.0D;
    private static final double IDLE_UP_OFFSET = -7.0D;
    private static final double ORBIT_RIGHT_RADIUS = 28.0D;
    private static final double ORBIT_BACK_RADIUS = 18.0D;
    private static final double ORBIT_UP_OFFSET = 12.0D;

    private DraconicCrucibleDragonModel model;
    private boolean modelLoadFailed;

    @Override
    public DynamicRenderType<DraconicCrucibleMachine, DraconicCrucibleDragonRender> getType() {
        return TYPE;
    }

    @Override
    public void render(DraconicCrucibleMachine machine, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (!machine.isFormed() || machine.getLevel() == null) return;

        Direction front = machine.getFrontFacing();
        Direction right = RelativeDirection.RIGHT.getRelative(front, machine.getUpwardsFacing(), machine.isFlipped());
        Direction down = RelativeDirection.DOWN.getRelative(front, machine.getUpwardsFacing(), machine.isFlipped());
        Direction back = RelativeDirection.BACK.getRelative(front, machine.getUpwardsFacing(), machine.isFlipped());
        BlockPos core = machine.getPos()
                .relative(right, DraconicCrucibleStructure.CORE_X - DraconicCrucibleStructure.CONTROLLER_X)
                .relative(down, DraconicCrucibleStructure.CONTROLLER_Y - DraconicCrucibleStructure.CORE_Y)
                .relative(back, DraconicCrucibleStructure.CORE_Z - DraconicCrucibleStructure.CONTROLLER_Z);
        Vec3 origin = Vec3.atCenterOf(core).subtract(Vec3.atLowerCornerOf(machine.getPos()));
        Vec3 rightVector = Vec3.atLowerCornerOf(right.getNormal());
        Vec3 upVector = Vec3.atLowerCornerOf(down.getOpposite().getNormal());
        Vec3 backVector = Vec3.atLowerCornerOf(back.getNormal());
        Vec3 perchedAnchor = offset(origin, rightVector, upVector, backVector,
                DRAGON_RIGHT_OFFSET, DRAGON_UP_OFFSET, DRAGON_BACK_OFFSET);
        Vec3 idleAnchor = offset(origin, rightVector, upVector, backVector,
                IDLE_RIGHT_OFFSET, IDLE_UP_OFFSET, DRAGON_BACK_OFFSET);

        if (!ensureDragon()) return;
        boolean isWorking = machine.getRecipeLogic().isWorking();
        boolean sleeping = !machine.isDragonStartupActive() && !machine.isDragonReady() && !isWorking;
        float animation = machine.getOffsetTimer() + partialTick;
        float recipeTicks = (float) (machine.getRecipeLogic().getProgressPercent()
                * machine.getRecipeLogic().getMaxProgress());
        float perchedYaw = -back.toYRot() + DRAGON_YAW_OFFSET;
        float yaw = perchedYaw;
        float breathStrength = 0.0F;
        Vec3 anchor;

        if (machine.isDragonStartupActive()) {
            float startupTicks = Math.min(
                    DraconicCrucibleMachine.STARTUP_TICKS,
                    machine.getDragonStartupTicks() + partialTick);
            DragonTransform transform = prepareStartupTransform(
                    startupTicks, origin, idleAnchor, perchedAnchor,
                    rightVector, upVector, backVector, perchedYaw);
            anchor = transform.anchor();
            yaw = transform.yaw();
        } else if (machine.isDragonReady() || isWorking) {
            anchor = perchedAnchor;
            breathStrength = model.preparePose(
                    DraconicCrucibleDragonModel.CHARGE_POSE_TICKS + recipeTicks, isWorking);
        } else {
            anchor = idleAnchor;
            model.prepareIdlePose();
        }

        poseStack.pushPose();
        poseStack.translate(anchor.x, anchor.y, anchor.z);
        poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
        poseStack.scale(-STAGE_FIVE_SCALE, -STAGE_FIVE_SCALE, STAGE_FIVE_SCALE);
        poseStack.translate(0.0F, -1.501F, 0.0F);

        ResourceLocation texture = sleeping ? DRAGON_SLEEPING_TEXTURE : DRAGON_TEXTURE;
        model.render(poseStack, buffer.getBuffer(RenderType.entityCutoutNoCull(texture)),
                LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 1.0F);
        if (!sleeping) {
            model.render(poseStack, buffer.getBuffer(RenderType.eyes(DRAGON_GLOW_TEXTURE)),
                    LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY,
                    1.0F, 1.0F, 1.0F, 1.0F);
        }
        poseStack.popPose();

        Vec3 mouthWorld = model.calculateMouthMachinePos(anchor, yaw, STAGE_FIVE_SCALE);
        Vec3 target = origin.add(0.0D, 1.0D, 0.0D);
        renderCoreEgg(poseStack, buffer, origin, animation,
                isWorking && breathStrength > 0.0F ? breathStrength : 0.0F);

        if (isWorking && breathStrength > 0.0F) {
            renderDragonFlameStream(poseStack, buffer, mouthWorld, target, animation, breathStrength);
        }
    }

    private DragonTransform prepareStartupTransform(float ticks, Vec3 origin,
                                                     Vec3 idleAnchor, Vec3 perchedAnchor,
                                                     Vec3 right, Vec3 up, Vec3 back,
                                                     float perchedYaw) {
        float takeoffStart = DraconicCrucibleMachine.WAKE_TICKS;
        float orbitStart = takeoffStart + DraconicCrucibleMachine.TAKEOFF_TICKS;
        float landingStart = orbitStart + DraconicCrucibleMachine.ORBIT_TICKS;
        float chargeStart = landingStart + DraconicCrucibleMachine.LANDING_TICKS;
        Vec3 orbitAnchor = offset(origin, right, up, back,
                ORBIT_RIGHT_RADIUS, ORBIT_UP_OFFSET, 0.0D);
        float flightYaw = perchedYaw - 90.0F + FLIGHT_YAW_CORRECTION;

        if (ticks < takeoffStart) {
            model.prepareTakeoffPose(ticks / DraconicCrucibleMachine.WAKE_TICKS);
            return new DragonTransform(idleAnchor, perchedYaw);
        }
        if (ticks < orbitStart) {
            float progress = smoothStep((ticks - takeoffStart) / DraconicCrucibleMachine.TAKEOFF_TICKS);
            model.prepareFlightPose(ticks - takeoffStart);
            return new DragonTransform(
                    idleAnchor.lerp(orbitAnchor, progress),
                    Mth.rotLerp(progress, perchedYaw, flightYaw));
        }
        if (ticks < landingStart) {
            float progress = (ticks - orbitStart) / DraconicCrucibleMachine.ORBIT_TICKS;
            double angle = progress * Math.PI * 2.0D;
            Vec3 anchor = offset(origin, right, up, back,
                    Math.cos(angle) * ORBIT_RIGHT_RADIUS,
                    ORBIT_UP_OFFSET,
                    Math.sin(angle) * ORBIT_BACK_RADIUS);
            model.prepareFlightPose(ticks - orbitStart);
            return new DragonTransform(anchor, flightYaw - progress * 360.0F);
        }
        if (ticks < chargeStart) {
            float progress = smoothStep((ticks - landingStart) / DraconicCrucibleMachine.LANDING_TICKS);
            model.prepareLandingPose(progress, ticks - orbitStart);
            return new DragonTransform(
                    orbitAnchor.lerp(perchedAnchor, progress),
                    Mth.rotLerp(progress, flightYaw, perchedYaw));
        }

        float progress = (ticks - chargeStart) / DraconicCrucibleMachine.CHARGE_TICKS;
        model.prepareChargePose(progress);
        return new DragonTransform(perchedAnchor, perchedYaw);
    }

    private static Vec3 offset(Vec3 origin, Vec3 right, Vec3 up, Vec3 back,
                               double rightOffset, double upOffset, double backOffset) {
        return origin.add(right.scale(rightOffset)).add(up.scale(upOffset)).add(back.scale(backOffset));
    }

    private static float smoothStep(float value) {
        float clamped = Mth.clamp(value, 0.0F, 1.0F);
        return clamped * clamped * (3.0F - 2.0F * clamped);
    }

    private record DragonTransform(Vec3 anchor, float yaw) {}

    private void renderCoreEgg(PoseStack poseStack, MultiBufferSource buffer,
                               Vec3 origin, float animation, float shakeStrength) {
        poseStack.pushPose();
        poseStack.translate(origin.x - 0.5D, origin.y - 0.5D, origin.z - 0.5D);
        if (shakeStrength > 0.0F) {
            float phase = animation * 0.3F;
            poseStack.translate(0.5D, 0.32D, 0.5D);
            poseStack.mulPose(Axis.XP.rotation(-Mth.cos(phase + 1.0F) * 0.3F * shakeStrength));
            poseStack.mulPose(Axis.ZP.rotation(Mth.cos(phase) * 0.3F * shakeStrength));
            poseStack.translate(-0.5D, -0.32D, -0.5D);
        }
        var blockRenderer = Minecraft.getInstance().getBlockRenderer();
        var state = TSTBlocks.DRACONIC_CRUCIBLE_CORE.get().defaultBlockState();
        blockRenderer.getModelRenderer().renderModel(
                poseStack.last(), buffer.getBuffer(RenderType.entitySolid(TextureAtlas.LOCATION_BLOCKS)),
                state, blockRenderer.getBlockModel(state), 1.0F, 1.0F, 1.0F,
                LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.solid());
        poseStack.popPose();
    }

    private void renderDragonFlameStream(PoseStack poseStack, MultiBufferSource buffer,
                                         Vec3 start, Vec3 target, float timer, float breathStrength) {
        int particleCount = Math.max(1, Mth.ceil(72.0F * breathStrength));
        float speed = 0.045F;
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucentEmissive(DRAGON_FLAME_TEXTURE));
        PoseStack.Pose pose = poseStack.last();
        var cameraRotation = Minecraft.getInstance().gameRenderer.getMainCamera().rotation();
        var corner = new org.joml.Vector3f();

        for (int index = 0; index < particleCount; index++) {
            float cyclePosition = timer * speed + (float) index / particleCount;
            float progress = cyclePosition - Mth.floor(cyclePosition);
            int seed = index + Mth.floor(cyclePosition) * particleCount;
            float spread = 3.5F * progress * progress;
            Vec3 position = start.lerp(target, progress).add(
                    noise(seed * 3) * spread,
                    noise(seed * 3 + 1) * spread,
                    noise(seed * 3 + 2) * spread);
            float size = 0.42F * breathStrength
                    * (0.8F + Mth.sin(progress * (float) Math.PI) * 0.45F);
            renderFlameParticle(consumer, pose.pose(), pose.normal(), cameraRotation, corner, position, size);
        }
    }

    private static void renderFlameParticle(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                                            org.joml.Quaternionf cameraRotation, org.joml.Vector3f corner,
                                            Vec3 position, float size) {
        renderFlameVertex(consumer, pose, normal, cameraRotation, corner, position, -size, -size, 1.0F, 1.0F);
        renderFlameVertex(consumer, pose, normal, cameraRotation, corner, position, -size, size, 1.0F, 0.0F);
        renderFlameVertex(consumer, pose, normal, cameraRotation, corner, position, size, size, 0.0F, 0.0F);
        renderFlameVertex(consumer, pose, normal, cameraRotation, corner, position, size, -size, 0.0F, 1.0F);
    }

    private static void renderFlameVertex(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                                          org.joml.Quaternionf cameraRotation, org.joml.Vector3f corner,
                                          Vec3 position, float x, float y, float u, float v) {
        cameraRotation.transform(corner.set(x, y, 0.0F));
        consumer.vertex(pose,
                        corner.x() + (float) position.x,
                        corner.y() + (float) position.y,
                        corner.z() + (float) position.z)
                .color(255, 255, 255, 255)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(normal, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    private static float noise(int seed) {
        int value = seed * 0x1f123bb5 ^ 0x5f356495;
        value ^= value >>> 16;
        return ((value & 0xffff) / 32767.5F) - 1.0F;
    }

    @Override
    public boolean shouldRender(DraconicCrucibleMachine machine, Vec3 cameraPos) {
        return machine.isFormed() && cameraPos.distanceTo(Vec3.atCenterOf(machine.getPos())) < 128.0D;
    }

    private boolean ensureDragon() {
        if (model != null) return true;
        if (modelLoadFailed) return false;
        try {
            model = DraconicCrucibleDragonModel.load(Minecraft.getInstance().getResourceManager());
            return true;
        } catch (RuntimeException exception) {
            modelLoadFailed = true;
            LOGGER.error("Failed to load the Draconic Crucible Fire Dragon model", exception);
            return false;
        }
    }
}
