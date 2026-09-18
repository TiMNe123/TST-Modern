package com.tstmodern.client.renderer;

import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.tstmodern.machine.GalacticArmillaryMachine;
import com.tstmodern.registry.machine.GalacticArmillaryDefinition;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.model.machine.IControllerModelRenderer;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderType;
import com.gregtechceu.gtceu.client.util.ModelUtils;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mojang.serialization.Codec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Random;

/** Procedural spiral galaxy dynamic renderer for Galactic Armillary. */
@SuppressWarnings("removal")
public class GalacticArmillaryGalaxyRender
        extends DynamicRender<WorkableElectricMultiblockMachine, GalacticArmillaryGalaxyRender>
        implements IControllerModelRenderer {
    public static final Codec<GalacticArmillaryGalaxyRender> CODEC = Codec.unit(new GalacticArmillaryGalaxyRender());
    public static final DynamicRenderType<WorkableElectricMultiblockMachine, GalacticArmillaryGalaxyRender> TYPE =
            new DynamicRenderType<>(CODEC);

    private static final float TAU = (float) (Math.PI * 2);
    private static final float RADIUS = 5.4f;
    private static final float HALO_RADIUS = 6.2f;
    private static final float TILT = 0.0f;
    private static final int STAR_COUNT = 3600;
    private static final int BAND_SAMPLES = 512;
    private static final int ROTATION_TICKS = 1200;
    private static final float CLOUD_GLOW_STRENGTH = 0.12f;
    private static final float BEAM_TOP = 9.5f;
    private static final float SATELLITE_CORE_UP = 19.0f;
    private static final ResourceLocation WHITE = new ResourceLocation("minecraft", "block/white_concrete");
    private static final ResourceLocation STONE = new ResourceLocation("minecraft", "block/cobblestone");
    private static final Cloud[] CLOUDS = new Cloud[BAND_SAMPLES * 2 + 384];
    private static final Cloud[] INTERWOVEN = new Cloud[1536];
    private static final Star[] STARS = new Star[STAR_COUNT];
    private static final Asteroid[] ASTEROIDS = new Asteroid[7];
    private static final float[] GLOW_X = new float[16];
    private static final float[] GLOW_Y = new float[16];
    private BakedModel highPowerModel;
    private BakedModel astralModel;

    // Heights for Galactic Armillary
    private static final float FORMED_UP = 15.0f;
    private static final float CENTER_BACK = 0.0f;

    // Own the batches: caller-owned buffers may flush solid AFTER emissive geometry.
    private static final class GalaxyBuffers {
        private static final MultiBufferSource.BufferSource SOURCE =
                MultiBufferSource.immediate(new BufferBuilder(262144));
        private static final IdentityHashMap<WorkableElectricMultiblockMachine, GalacticArmillaryGalaxyRender> PENDING = new IdentityHashMap<>();
        private static final Cloud[] SORTED = new Cloud[CLOUDS.length + INTERWOVEN.length];
    }

    /** Reuse vanilla shaders/blending, but route late geometry to Forge's Fabulous particles target. */
    private static final class LateType extends RenderType {
        private static final RenderType DUST = new LateType("tstmodern_galaxy_dust",
                RenderType.entityTranslucentEmissive(InventoryMenu.BLOCK_ATLAS));
        private static final RenderType LIGHT = new LateType("tstmodern_galaxy_light",
                RenderType.eyes(InventoryMenu.BLOCK_ATLAS));

        private LateType(String name, RenderType base) {
            // Cloud fans are sorted together below, not split into separately sorted triangles.
            super(name, base.format(), base.mode(), 262144, false, false,
                    () -> { base.setupRenderState(); NO_CULL.setupRenderState(); PARTICLES_TARGET.setupRenderState(); },
                    () -> { base.clearRenderState(); NO_CULL.clearRenderState(); PARTICLES_TARGET.clearRenderState(); });
        }
    }

    @Mod.EventBusSubscriber(modid = "tstmodern", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static final class WorldPass {
        @SubscribeEvent
        public static void renderLevel(RenderLevelStageEvent event) {
            if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY) {
                GalaxyBuffers.PENDING.clear();
                return;
            }
            if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;
            Minecraft minecraft = Minecraft.getInstance();
            PoseStack stack = event.getPoseStack();
            Vec3 camera = event.getCamera().getPosition();
            try {
                for (var entry : GalaxyBuffers.PENDING.entrySet()) {
                    WorkableElectricMultiblockMachine machine = entry.getKey();
                    if (machine.getLevel() != minecraft.level) continue;
                    stack.pushPose();
                    try {
                        stack.translate(machine.getPos().getX() - camera.x,
                                machine.getPos().getY() - camera.y, machine.getPos().getZ() - camera.z);
                        entry.getValue().draw(machine, event.getPartialTick(), stack, 0, true);
                    } finally {
                        stack.popPose();
                    }
                }
            } finally {
                GalaxyBuffers.PENDING.clear();
            }
        }
    }

    private static final int[][] FACES = {
            {0, 4, 2}, {2, 4, 1}, {1, 4, 3}, {3, 4, 0},
            {2, 5, 0}, {1, 5, 2}, {3, 5, 1}, {0, 5, 3}
    };

    private record Cloud(float x, float y, float z, float size,
                         float red, float green, float blue, float alpha, boolean dust) {}
    private record Star(float x, float y, float z, float size, float phase, int period) {}
    private record Asteroid(float radius, float phase, int period, float height, float size,
                            Vector3f[] points, Vector3f[] normals) {}

    static {
        Random random = new Random(0x464F524E4158L);

        for (int i = 0; i < GLOW_X.length; i++) {
            GLOW_X[i] = Mth.cos(TAU * i / GLOW_X.length);
            GLOW_Y[i] = Mth.sin(TAU * i / GLOW_Y.length);
        }
        // Two interleaved, densely sampled bands each wind 2.4 full turns into the center.
        // Adjacent puffs overlap along the entire path; trailing dust and diffuse haze
        // break up the bands without leaving four isolated spokes or a hard central surface.
        for (int i = 0; i < CLOUDS.length; i++) {
            boolean band = i < BAND_SAMPLES * 2;
            boolean dust = !band && i < BAND_SAMPLES * 2 + 256;
            boolean halo = !band && !dust;
            float radius = band ? RADIUS * (i % BAND_SAMPLES) / (BAND_SAMPLES - 1)
                    : (halo ? HALO_RADIUS : RADIUS) * (float) Math.sqrt(random.nextFloat());
            float outer = Mth.clamp(radius / RADIUS, 0, 1);
            float angle = halo ? random.nextFloat() * TAU : spiral(radius)
                    + (band ? i / BAND_SAMPLES : i % 2) * (float) Math.PI + (dust ? 0.23f : 0);
            float y = 0.2f * Mth.sin(angle * 2 + radius) * (1 - outer * 0.5f)
                    + (random.nextFloat() - 0.5f) * (halo ? 0.9f : 0.14f);
            float size = 0.48f + 0.22f * outer + random.nextFloat() * 0.16f;
            float cool = Mth.clamp((radius - 1.4f) / 2.8f, 0, 1);
            float gold = (float) Math.exp(-radius * radius / 4f);
            CLOUDS[i] = new Cloud(radius * Mth.cos(angle), y, radius * Mth.sin(angle),
                    halo ? size * 1.6f : size,
                    dust ? 0.17f : Mth.lerp(cool, 1f, 0.22f),
                    dust ? 0.085f : Mth.lerp(gold, Mth.lerp(cool, 0.73f, 0.7f), 0.82f),
                    dust ? 0.035f : Mth.lerp(gold, Mth.lerp(cool, 0.32f, 1f), 0.12f),
                    halo ? 0.09f : dust ? 0.3f : 0.42f + 0.18f * gold, dust);
        }
        // Area-covering cloud bed bridges the space BETWEEN successive spiral turns.
        // Golden-angle placement prevents both empty annuli and a regular grid of puffs.
        for (int i = 0; i < INTERWOVEN.length; i++) {
            float radius = RADIUS * (float) Math.sqrt((i + 0.5f) / INTERWOVEN.length);
            float angle = i * 2.39996323f;
            float phase = 2 * (angle - spiral(radius));
            float grain = Mth.sin(angle * 7 + radius * 19) * 0.35f;
            float filament = Mth.sin(phase + grain);
            float core = (float) Math.exp(-radius * radius / 4f);
            float red, green, blue;
            if (filament > 0.5f) {
                red = 0.92f; green = 0.98f; blue = 1f;
            } else if (filament > -0.3f) {
                red = 0.24f; green = 0.72f; blue = 0.48f;
            } else {
                red = 0.11f; green = 0.12f; blue = 0.14f;
            }
            INTERWOVEN[i] = new Cloud(radius * Mth.cos(angle),
                    (random.nextFloat() - 0.5f) * 0.5f, radius * Mth.sin(angle),
                    0.5f + random.nextFloat() * 0.22f,
                    Mth.lerp(core, red, 1), Mth.lerp(core, green, 0.82f),
                    Mth.lerp(core, blue, 0.12f), 0.32f + core * 0.15f, filament <= -0.3f);
        }
        for (int i = 0; i < STARS.length; i++) {
            float radius = RADIUS * (float) Math.sqrt(random.nextFloat());
            float angle = i % 5 == 0 ? random.nextFloat() * TAU
                    : spiral(radius) + (i % 2) * (float) Math.PI + (float) random.nextGaussian() * 0.13f;
            float outer = radius / RADIUS;
            STARS[i] = new Star(radius * Mth.cos(angle),
                    Mth.clamp((float) random.nextGaussian(), -2, 2) * (0.12f + 0.38f * (1 - outer)),
                    radius * Mth.sin(angle), 0.018f + (float) Math.pow(random.nextFloat(), 5) * 0.043f,
                    random.nextFloat() * TAU, 80 + random.nextInt(161));
        }
        for (int i = 0; i < ASTEROIDS.length; i++) {
            Vector3f[] points = {
                    new Vector3f(1, 0, 0), new Vector3f(-1, 0, 0),
                    new Vector3f(0, 0, 1), new Vector3f(0, 0, -1),
                    new Vector3f(0, 1, 0), new Vector3f(0, -1, 0)
            };
            for (Vector3f point : points) {
                point.mul(0.7f + random.nextFloat() * 0.5f);
                point.y *= 0.75f;
            }
            Vector3f[] normals = new Vector3f[FACES.length];
            for (int face = 0; face < FACES.length; face++) {
                int[] indices = FACES[face];
                normals[face] = new Vector3f(points[indices[1]]).sub(points[indices[0]])
                        .cross(new Vector3f(points[indices[2]]).sub(points[indices[0]])).normalize();
            }
            ASTEROIDS[i] = new Asteroid(4.5f + random.nextFloat() * 2.1f,
                    random.nextFloat() * TAU, 2400 + i * 600, (random.nextFloat() - 0.5f) * 1.6f,
                    0.12f + random.nextFloat() * 0.16f, points, normals);
        }
    }

    private static float spiral(float radius) {
        return TAU * 2.4f * (float) Math.pow(radius / RADIUS, 0.7);
    }

    private static float rotationAngle(long ticks, float partialTick) {
        return (Math.floorMod(ticks, ROTATION_TICKS) + partialTick) * TAU / ROTATION_TICKS;
    }

    private static float twinkle(Star star, long ticks, float partialTick) {
        float phase = (Math.floorMod(ticks, star.period) + partialTick) * TAU / star.period + star.phase;
        return 0.55f + 0.45f * (0.5f + 0.5f * Mth.sin(phase));
    }

    public GalacticArmillaryGalaxyRender() {}

    @Override
    public DynamicRenderType<WorkableElectricMultiblockMachine, GalacticArmillaryGalaxyRender> getType() { return TYPE; }

    @Override
    public void renderPartModel(List<BakedQuad> quads, IMultiController controller, IMultiPart part,
                                Direction frontFacing, @Nullable Direction side, RandomSource random,
                                @NotNull ModelData modelData, @Nullable RenderType renderType) {
        BlockState state = GalacticArmillaryDefinition.partAppearance(controller, part, side);
        boolean astral = state.is(com.tstmodern.registry.TSTBlocks.ASTRAL_PYLON_CASING.get());
        BakedModel model = astral ? astralModel : highPowerModel;
        if (model == null) {
            model = ModelUtils.getModelForState(state);
            if (astral) astralModel = model;
            else highPowerModel = model;
        }
        BlockAndTintGetter level = controller.self().getLevel();
        var casingData = model.getModelData(level, part.self().getPos(), state, modelData);
        quads.addAll(model.getQuads(state, side, random, casingData, renderType));
    }

    @Override
    public boolean shouldRender(WorkableElectricMultiblockMachine machine, Vec3 cameraPos) {
        return machine instanceof GalacticArmillaryMachine armillary &&
                (armillary.isCharging() || armillary.isStable());
    }

    @Override
    public void render(WorkableElectricMultiblockMachine machine, float partialTick, PoseStack stack,
                       MultiBufferSource buffers, int packedLight, int packedOverlay) {
        if (!(machine instanceof GalacticArmillaryMachine armillary) ||
                (!armillary.isCharging() && !armillary.isStable())) return;
        GalaxyBuffers.PENDING.put(machine, this);
        if (armillary.isStable() && armillary.getIgnitionTicks() >= GalacticArmillaryMachine.IGNITION_BURST_TICKS) {
            draw(machine, partialTick, stack, packedLight, false);
        }
    }

    private void draw(WorkableElectricMultiblockMachine machine, float partialTick, PoseStack stack,
                      int packedLight, boolean late) {
        var back = RelativeDirection.BACK.getRelative(machine.getFrontFacing(),
                machine.getUpwardsFacing(), machine.isFlipped());
        var up = RelativeDirection.UP.getRelative(machine.getFrontFacing(),
                machine.getUpwardsFacing(), machine.isFlipped());

        float upOffset = FORMED_UP;

        // Like the original disk and planets, ambient motion continues during idle/suspend.
        // Reduce the long timer before converting to float to retain partial-tick precision.
        long ticks = machine.getOffsetTimer();
        float rotation = rotationAngle(ticks, partialTick);
        Quaternionf plane = new Quaternionf().rotationX(TILT).rotateY(rotation);
        Quaternionf billboard = new Quaternionf(plane).conjugate()
                .mul(Minecraft.getInstance().gameRenderer.getMainCamera().rotation());
        Vector3f right = billboard.transform(new Vector3f(1, 0, 0));
        Vector3f vertical = billboard.transform(new Vector3f(0, 1, 0));
        Vector3f normal = billboard.transform(new Vector3f(0, 0, 1));
        var atlas = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
        // Resolve each frame so resource-pack reloads cannot leave stale sprite UVs.
        TextureAtlasSprite white = atlas.apply(WHITE);
        TextureAtlasSprite stone = atlas.apply(STONE);
        GalacticArmillaryMachine armillary = (GalacticArmillaryMachine) machine;
        if (!armillary.isStable()) {
            if (!late) return;
            stack.pushPose();
            try {
                stack.translate(0.5 + back.getStepX() * CENTER_BACK + up.getStepX() * upOffset,
                        0.5 + back.getStepY() * CENTER_BACK + up.getStepY() * upOffset,
                        0.5 + back.getStepZ() * CENTER_BACK + up.getStepZ() * upOffset);
                renderChargingEffects(stack, white, ticks, partialTick, armillary.getActivationProgress());
            } finally {
                stack.popPose();
            }
            return;
        }
        stack.pushPose();
        try {
            stack.translate(0.5 + back.getStepX() * CENTER_BACK + up.getStepX() * upOffset,
                    0.5 + back.getStepY() * CENTER_BACK + up.getStepY() * upOffset,
                    0.5 + back.getStepZ() * CENTER_BACK + up.getStepZ() * upOffset);
            if (late && armillary.getIgnitionTicks() < GalacticArmillaryMachine.IGNITION_BURST_TICKS) {
                renderIgnitionBurst(stack, white, armillary.getIgnitionTicks(), partialTick);
                return;
            }
            if (!late && armillary.getIgnitionTicks() < GalacticArmillaryMachine.IGNITION_BURST_TICKS) return;
            stack.mulPose(plane);
            MultiBufferSource.BufferSource galaxyBuffers = GalaxyBuffers.SOURCE;
            if (!late) {
                RenderType solid = RenderType.entitySolid(InventoryMenu.BLOCK_ATLAS);
                renderAsteroids(stack, galaxyBuffers.getBuffer(solid),
                        stone, ticks, partialTick, packedLight);
                galaxyBuffers.endBatch(solid);
                return;
            }
            // Sorted whole cloud fans, depth testing and no back-face culling.
            RenderType emissive = LateType.DUST;
            VertexConsumer buffer = galaxyBuffers.getBuffer(emissive);
            float u = white.getU(8), v = white.getV(8);
            Cloud[] sorted = GalaxyBuffers.SORTED;
            System.arraycopy(CLOUDS, 0, sorted, 0, CLOUDS.length);
            System.arraycopy(INTERWOVEN, 0, sorted, CLOUDS.length, INTERWOVEN.length);
            Arrays.sort(sorted, (a, b) -> Float.compare(cloudDepth(a, normal), cloudDepth(b, normal)));
            for (Cloud cloud : sorted) {
                glow(stack, buffer, cloud.x, cloud.y, cloud.z, cloud.size,
                        cloud.red, cloud.green, cloud.blue, cloud.alpha, right, vertical, normal, u, v, false);
            }
            galaxyBuffers.endBatch(emissive);
            // ONE/ONE blending adds emitted light independently of the background and draw order.
            // Premultiply RGB, including zero RGB at puff edges: eyes() does not multiply by alpha.
            RenderType light = LateType.LIGHT;
            buffer = galaxyBuffers.getBuffer(light);
            for (Cloud cloud : INTERWOVEN) {
                if (cloud.dust) continue;
                glow(stack, buffer, cloud.x, cloud.y, cloud.z, cloud.size,
                        cloud.red, cloud.green, cloud.blue, cloudEmission(cloud), right, vertical, normal, u, v, true);
            }
            for (Cloud cloud : CLOUDS) {
                if (cloud.dust) continue;
                glow(stack, buffer, cloud.x, cloud.y, cloud.z, cloud.size,
                        cloud.red, cloud.green, cloud.blue, cloudEmission(cloud), right, vertical, normal, u, v, true);
            }
            galaxyBuffers.endBatch(light);
            // White stars use bounded alpha blending: overlapping stars cannot add past white.
            buffer = galaxyBuffers.getBuffer(emissive);
            for (Star star : STARS) {
                float brightness = twinkle(star, ticks, partialTick);
                // A small solid white center survives distance better than a feathered fan alone.
                float size = star.size * (0.75f + 0.25f * brightness);
                for (int corner = 0; corner < 4; corner++) {
                    float dx = (corner == 0 || corner == 3 ? -size : size);
                    float dy = (corner < 2 ? -size : size);
                    vertex(stack, buffer, star.x + right.x * dx + vertical.x * dy,
                            star.y + right.y * dx + vertical.y * dy,
                            star.z + right.z * dx + vertical.z * dy, 1, 1, 1,
                            brightness, u, v, normal.x, normal.y, normal.z, LightTexture.FULL_BRIGHT);
                }
            }
            galaxyBuffers.endBatch(emissive);
        } finally {
            stack.popPose();
        }
    }

    private static float cloudDepth(Cloud cloud, Vector3f towardCamera) {
        return cloud.x * towardCamera.x + cloud.y * towardCamera.y + cloud.z * towardCamera.z;
    }

    private static void renderChargingEffects(PoseStack stack, TextureAtlasSprite sprite,
                                              long ticks, float partialTick, float progress) {
        float pulse = 0.75f + 0.25f * Mth.sin((ticks + partialTick) * 0.35f);
        MultiBufferSource.BufferSource source = GalaxyBuffers.SOURCE;
        VertexConsumer buffer = source.getBuffer(LateType.LIGHT);
        float u = sprite.getU(8), v = sprite.getV(8);
        beam(stack, buffer, 0.55f * pulse, 0.45f, BEAM_TOP, 0.12f, 0.5f, 1.0f, u, v);
        beam(stack, buffer, 0.28f * pulse, 0.45f, BEAM_TOP, 0.75f, 0.95f, 1.0f, u, v);

        Quaternionf billboard = Minecraft.getInstance().gameRenderer.getMainCamera().rotation();
        Vector3f right = billboard.transform(new Vector3f(1, 0, 0));
        Vector3f vertical = billboard.transform(new Vector3f(0, 1, 0));
        Vector3f normal = billboard.transform(new Vector3f(0, 0, 1));
        glow(stack, buffer, 0, SATELLITE_CORE_UP, 0, 0.9f + 0.15f * pulse,
                0.2f, 0.75f, 1.0f, pulse, right, vertical, normal, u, v, true);
        float coreSize = 0.65f + 3.35f * (float) Math.cbrt(progress);
        glow(stack, buffer, 0, 0, 0, coreSize, 0.12f + 0.88f * progress,
                0.35f + 0.55f * progress, 1.0f, 0.75f + 0.25f * pulse,
                right, vertical, normal, u, v, true);
        glow(stack, buffer, 0, 0, 0, coreSize * 0.58f, 1.0f, 1.0f, 1.0f, pulse,
                right, vertical, normal, u, v, true);
        source.endBatch(LateType.LIGHT);
    }

    private static void renderIgnitionBurst(PoseStack stack, TextureAtlasSprite sprite,
                                            int ignitionTicks, float partialTick) {
        float progress = Mth.clamp((ignitionTicks + partialTick) /
                GalacticArmillaryMachine.IGNITION_BURST_TICKS, 0, 1);
        float expansion = Mth.clamp(progress / 0.55f, 0, 1);
        float detonation = Mth.clamp((progress - 0.55f) / 0.45f, 0, 1);
        Quaternionf billboard = Minecraft.getInstance().gameRenderer.getMainCamera().rotation();
        Vector3f right = billboard.transform(new Vector3f(1, 0, 0));
        Vector3f vertical = billboard.transform(new Vector3f(0, 1, 0));
        Vector3f normal = billboard.transform(new Vector3f(0, 0, 1));
        MultiBufferSource.BufferSource source = GalaxyBuffers.SOURCE;
        VertexConsumer buffer = source.getBuffer(LateType.LIGHT);
        float u = sprite.getU(8), v = sprite.getV(8);
        if (detonation == 0) {
            float size = 4.0f + expansion * 1.8f;
            glow(stack, buffer, 0, 0, 0, size, 1.0f, 0.3f + 0.65f * expansion, 0.05f, 1,
                    right, vertical, normal, u, v, true);
            glow(stack, buffer, 0, 0, 0, size * 0.55f, 1.0f, 1.0f, 0.8f, 1,
                    right, vertical, normal, u, v, true);
        } else {
            float alpha = 1 - detonation;
            float size = 5.8f + detonation * 7.0f;
            glow(stack, buffer, 0, 0, 0, size, 1.0f, 0.25f, 0.02f, alpha,
                    right, vertical, normal, u, v, true);
            glow(stack, buffer, 0, 0, 0, size * 0.62f, 1.0f, 1.0f, 0.72f, alpha,
                    right, vertical, normal, u, v, true);
        }
        source.endBatch(LateType.LIGHT);
    }

    private static void beam(PoseStack stack, VertexConsumer buffer, float width,
                             float bottom, float top, float red, float green, float blue, float u, float v) {
        vertex(stack, buffer, -width, bottom, 0, red, green, blue, 1, u, v, 0, 0, 1, LightTexture.FULL_BRIGHT);
        vertex(stack, buffer, width, bottom, 0, red, green, blue, 1, u, v, 0, 0, 1, LightTexture.FULL_BRIGHT);
        vertex(stack, buffer, width * 0.55f, top, 0, red, green, blue, 1, u, v, 0, 0, 1, LightTexture.FULL_BRIGHT);
        vertex(stack, buffer, -width * 0.55f, top, 0, red, green, blue, 1, u, v, 0, 0, 1, LightTexture.FULL_BRIGHT);
        vertex(stack, buffer, 0, bottom, -width, red, green, blue, 1, u, v, 1, 0, 0, LightTexture.FULL_BRIGHT);
        vertex(stack, buffer, 0, bottom, width, red, green, blue, 1, u, v, 1, 0, 0, LightTexture.FULL_BRIGHT);
        vertex(stack, buffer, 0, top, width * 0.55f, red, green, blue, 1, u, v, 1, 0, 0, LightTexture.FULL_BRIGHT);
        vertex(stack, buffer, 0, top, -width * 0.55f, red, green, blue, 1, u, v, 1, 0, 0, LightTexture.FULL_BRIGHT);
    }

    private static float cloudEmission(Cloud cloud) {
        return cloud.dust ? 0 : cloud.alpha * CLOUD_GLOW_STRENGTH;
    }

    /** Soft octagonal cloud puffs and four-point stars; all centers have actual 3D positions. */
    private static void glow(PoseStack stack, VertexConsumer buffer, float x, float y, float z,
                             float size, float red, float green, float blue, float alpha,
                             Vector3f right, Vector3f vertical, Vector3f normal, float u, float v, boolean additive) {
        if (additive) { red *= alpha; green *= alpha; blue *= alpha; }
        int edges = size > 0.1f ? 8 : 4;
        for (int i = 0; i < edges; i++) {
            vertex(stack, buffer, x, y, z, red, green, blue, alpha, u, v,
                    normal.x, normal.y, normal.z, LightTexture.FULL_BRIGHT);
            for (int j = 0; j < 2; j++) {
                int corner = ((i + j) * 16 / edges) % 16;
                float dx = GLOW_X[corner] * size, dy = GLOW_Y[corner] * size;
                vertex(stack, buffer, x + right.x * dx + vertical.x * dy,
                        y + right.y * dx + vertical.y * dy, z + right.z * dx + vertical.z * dy,
                        additive ? 0 : red, additive ? 0 : green, additive ? 0 : blue, 0, u, v, normal.x, normal.y, normal.z, LightTexture.FULL_BRIGHT);
            }
            vertex(stack, buffer, x, y, z, red, green, blue, alpha, u, v,
                    normal.x, normal.y, normal.z, LightTexture.FULL_BRIGHT);
        }
    }

    private static void renderAsteroids(PoseStack stack, VertexConsumer buffer, TextureAtlasSprite sprite,
                                        long ticks, float partialTick, int light) {
        for (Asteroid asteroid : ASTEROIDS) {
            float angle = asteroid.phase + TAU * (Math.floorMod(ticks, asteroid.period) + partialTick) / asteroid.period;
            stack.pushPose();
            try {
                stack.translate(asteroid.radius * Mth.cos(angle),
                        asteroid.height + 0.2f * Mth.sin(angle * 2), asteroid.radius * Mth.sin(angle));
                stack.mulPose(Axis.YP.rotation(angle * 3));
                stack.mulPose(Axis.ZP.rotation(asteroid.phase));
                stack.scale(asteroid.size, asteroid.size, asteroid.size);
                for (int face = 0; face < FACES.length; face++) {
                    Vector3f normal = asteroid.normals[face];
                    for (int corner = 0; corner < 4; corner++) {
                        Vector3f point = asteroid.points[FACES[face][corner == 3 ? 2 : corner]];
                        vertex(stack, buffer, point.x, point.y, point.z, 0.68f, 0.61f, 0.54f, 1,
                                sprite.getU(corner == 0 ? 0 : 16), sprite.getV(corner < 2 ? 0 : 16),
                                normal.x, normal.y, normal.z, light);
                    }
                }
            } finally {
                stack.popPose();
            }
        }
    }

    private static void vertex(PoseStack stack, VertexConsumer buffer, float x, float y, float z,
                                float red, float green, float blue, float alpha, float u, float v,
                                float nx, float ny, float nz, int light) {
        buffer.vertex(stack.last().pose(), x, y, z).color(red, green, blue, alpha).uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light)
                .normal(stack.last().normal(), nx, ny, nz).endVertex();
    }

    @Override
    public boolean shouldRenderOffScreen(WorkableElectricMultiblockMachine machine) { return true; }

    @Override
    public int getViewDistance() { return 128; }

    @Override
    public AABB getRenderBoundingBox(WorkableElectricMultiblockMachine machine) {
        // Includes every orientation of the offset center, halo and outer asteroid orbits.
        return new AABB(machine.getPos()).inflate(45);
    }

    public static void init() {
        // WorldPass is registered automatically by Forge's client-only EventBusSubscriber.
    }
}
