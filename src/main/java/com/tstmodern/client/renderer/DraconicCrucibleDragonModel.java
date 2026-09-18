package com.tstmodern.client.renderer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import org.joml.Matrix4f;
import org.joml.Vector4f;

/** Exact Ice and Fire Stage 5 Fire Dragon geometry posed on the Draconic Crucible. */
public final class DraconicCrucibleDragonModel {
    public static final int EXPECTED_PART_COUNT = 104;

    private static final float DEG_TO_RAD = (float) Math.PI / 180.0F;
    public static final float CHARGE_POSE_TICKS = 5.0F;
    private static final float BREATH_TRANSITION_TICKS = 5.0F;
    private static final float FLIGHT_FRAME_TICKS = 4.0F;
    private static final ResourceLocation GROUND_MODEL = new ResourceLocation(
            "tstmodern", "models/entity/draconic_crucible_fire_dragon_ground.tbl");
    private static final ResourceLocation SLEEPING_MODEL = new ResourceLocation(
            "tstmodern", "models/entity/draconic_crucible_fire_dragon_sleeping.tbl");
    private static final ResourceLocation CHARGE_MODEL = new ResourceLocation(
            "tstmodern", "models/entity/draconic_crucible_fire_dragon_charge.tbl");
    private static final ResourceLocation BREATH_MODEL = new ResourceLocation(
            "tstmodern", "models/entity/draconic_crucible_fire_dragon_breath.tbl");
    private static final List<ResourceLocation> FLIGHT_MODELS = List.of(
            flightModel(1), flightModel(2), flightModel(3),
            flightModel(4), flightModel(5), flightModel(6));
    private static final List<String> MOUTH_PATH = List.of(
            "BodyUpper", "Neck1", "Neck2", "Neck3", "Head");
    private static final float[] PERCHED_NECK_PITCH = { -45.0F, -30.0F, 30.0F, 80.0F };
    private static final float[] TAIL_PITCH = { -24.0F, -9.0F, 3.0F, -10.0F, -20.0F };
    private static final float[] TAIL_YAW = { -18.0F, -22.0F, -26.0F, -30.0F, -34.0F };
    private static final float TAIL_LENGTH_SCALE = 1.45F;
    private static final float PERCHED_THIGH_PITCH = -18.0F;
    private static final float PERCHED_LEG_PITCH = 10.0F;
    private static final float[] SLEEPING_NECK_YAW = { 0.0F, 15.0F, 20.0F, 25.0F };
    private static final float[] SLEEPING_TAIL_YAW = { -10.0F, -15.0F, -20.0F, -25.0F, -30.0F };

    private final ModelPart root;
    private final Map<String, ModelPart> parts;
    private final Map<String, Pose> groundPose;
    private final Map<String, Pose> sleepingPose;
    private final List<Map<String, Pose>> flightPoses;
    private final Map<String, Pose> chargePose;
    private final Map<String, Pose> breathPose;

    private DraconicCrucibleDragonModel(ModelPart root, Map<String, ModelPart> parts,
                                        Map<String, Pose> groundPose, Map<String, Pose> sleepingPose,
                                        List<Map<String, Pose>> flightPoses,
                                        Map<String, Pose> chargePose,
                                        Map<String, Pose> breathPose) {
        this.root = root;
        this.parts = parts;
        this.groundPose = groundPose;
        this.sleepingPose = sleepingPose;
        this.flightPoses = flightPoses;
        this.chargePose = chargePose;
        this.breathPose = breathPose;
    }

    public static DraconicCrucibleDragonModel load(ResourceManager resourceManager) {
        ModelFile ground = readModel(resourceManager, GROUND_MODEL);
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition meshRoot = mesh.getRoot();
        for (Part part : ground.parts()) addPart(meshRoot, part);

        ModelPart bakedRoot = LayerDefinition
                .create(mesh, ground.textureWidth(), ground.textureHeight())
                .bakeRoot();
        Map<String, ModelPart> bakedParts = new HashMap<>();
        for (Part part : ground.parts()) indexParts(bakedRoot, part, bakedParts);
        if (bakedParts.size() != EXPECTED_PART_COUNT) {
            throw new IllegalStateException("Expected " + EXPECTED_PART_COUNT
                    + " Ice and Fire dragon parts, found " + bakedParts.size());
        }

        Map<String, Pose> groundTransforms = poses(ground.parts());
        Map<String, Pose> sleepingTransforms = poses(readModel(resourceManager, SLEEPING_MODEL).parts());
        List<Map<String, Pose>> flightTransforms = FLIGHT_MODELS.stream()
                .map(resource -> poses(readModel(resourceManager, resource).parts()))
                .toList();
        Map<String, Pose> chargeTransforms = poses(readModel(resourceManager, CHARGE_MODEL).parts());
        Map<String, Pose> breathTransforms = poses(readModel(resourceManager, BREATH_MODEL).parts());
        requireSameParts(groundTransforms.keySet(), chargeTransforms.keySet(), CHARGE_MODEL);
        requireSameParts(groundTransforms.keySet(), breathTransforms.keySet(), BREATH_MODEL);
        requireSameParts(groundTransforms.keySet(), sleepingTransforms.keySet(), SLEEPING_MODEL);
        for (int index = 0; index < flightTransforms.size(); index++) {
            requireSameParts(groundTransforms.keySet(), flightTransforms.get(index).keySet(), FLIGHT_MODELS.get(index));
        }
        return new DraconicCrucibleDragonModel(
                bakedRoot, Map.copyOf(bakedParts), Map.copyOf(groundTransforms),
                Map.copyOf(sleepingTransforms), List.copyOf(flightTransforms),
                Map.copyOf(chargeTransforms), Map.copyOf(breathTransforms));
    }

    public void prepareIdlePose() {
        resetScales();
        applyPose(sleepingPose, sleepingPose, 0.0F);
        applySleepingCurl(1.0F);
    }

    public void prepareTakeoffPose(float blend) {
        resetScales();
        float easedBlend = smoothStep(blend);
        applyPose(sleepingPose, flightPoses.get(0), easedBlend);
        applySleepingCurl(1.0F - easedBlend);
    }

    public void prepareFlightPose(float animationTicks) {
        resetScales();
        float frame = Math.max(0.0F, animationTicks) / FLIGHT_FRAME_TICKS;
        int current = Mth.floor(frame) % flightPoses.size();
        int next = (current + 1) % flightPoses.size();
        applyPose(flightPoses.get(current), flightPoses.get(next), frame - Mth.floor(frame));
    }

    public void prepareLandingPose(float blend, float animationTicks) {
        float easedBlend = smoothStep(blend);
        prepareFlightPose(animationTicks);
        blendCurrentPoseTo(groundPose, easedBlend);
        applyPerchedPose(easedBlend);
    }

    public void prepareChargePose(float blend) {
        resetScales();
        applyPose(groundPose, chargePose, smoothStep(blend));
        applyPerchedPose(1.0F);
    }

    public float preparePose(float recipeTicks, boolean working) {
        resetScales();
        float phase = working ? Math.max(0.0F, recipeTicks) : 0.0F;
        float breathStrength;
        if (phase <= CHARGE_POSE_TICKS) {
            applyPose(groundPose, chargePose, phase / CHARGE_POSE_TICKS);
            breathStrength = 0.0F;
        } else {
            breathStrength = Mth.clamp(
                    (phase - CHARGE_POSE_TICKS) / BREATH_TRANSITION_TICKS, 0.0F, 1.0F);
            applyPose(chargePose, breathPose, breathStrength);
        }
        applyPerchedPose(1.0F);
        return breathStrength;
    }

    public void render(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
                       float red, float green, float blue, float alpha) {
        root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public Vec3 calculateMouthMachinePos(Vec3 anchor, float yawDegrees, float stageScale) {
        Matrix4f matrix = new Matrix4f()
                .translate((float) anchor.x, (float) anchor.y, (float) anchor.z)
                .rotateY(yawDegrees * DEG_TO_RAD)
                .scale(-stageScale, -stageScale, stageScale)
                .translate(0.0F, -1.501F, 0.0F);
        for (String name : MOUTH_PATH) transform(matrix, parts.get(name));

        Vector4f mouth = matrix.transform(new Vector4f(0.0F, 2.0F / 16.0F, -6.5F / 16.0F, 1.0F));
        return new Vec3(mouth.x(), mouth.y(), mouth.z());
    }

    public Vec3 calculateMouthDirection(float yawDegrees) {
        Matrix4f matrix = new Matrix4f()
                .rotateY(yawDegrees * DEG_TO_RAD)
                .scale(-1.0F, -1.0F, 1.0F);
        for (String name : MOUTH_PATH) transform(matrix, parts.get(name));

        Vector4f dir = matrix.transform(new Vector4f(0.0F, 0.0F, -1.0F, 0.0F));
        return new Vec3(dir.x(), dir.y(), dir.z()).normalize();
    }

    private void applyPerchedPose(float blend) {
        for (int index = 0; index < MOUTH_PATH.size() - 1; index++) {
            parts.get(MOUTH_PATH.get(index + 1)).xRot +=
                    PERCHED_NECK_PITCH[index] * DEG_TO_RAD * blend;
        }

        ModelPart rightWingRoot = parts.get("armR1");
        ModelPart leftWingRoot = parts.get("armL1");
        rightWingRoot.yRot = Mth.lerp(blend, rightWingRoot.yRot, -25.0F * DEG_TO_RAD);
        rightWingRoot.zRot = Mth.lerp(blend, rightWingRoot.zRot, 75.0F * DEG_TO_RAD);
        leftWingRoot.yRot = Mth.lerp(blend, leftWingRoot.yRot, 25.0F * DEG_TO_RAD);
        leftWingRoot.zRot = Mth.lerp(blend, leftWingRoot.zRot, -75.0F * DEG_TO_RAD);
        ModelPart rightForearm = parts.get("armR2");
        ModelPart leftForearm = parts.get("armL2");
        rightForearm.zRot = Mth.lerp(blend, rightForearm.zRot, -20.0F * DEG_TO_RAD);
        leftForearm.zRot = Mth.lerp(blend, leftForearm.zRot, 20.0F * DEG_TO_RAD);

        for (int index = 1; index <= 5; index++) {
            ModelPart tail = parts.get("Tail" + index);
            tail.xRot += TAIL_PITCH[index - 1] * DEG_TO_RAD * blend;
            tail.yRot += TAIL_YAW[index - 1] * DEG_TO_RAD * blend;
        }
        parts.get("Tail1").zScale = Mth.lerp(blend, 1.0F, TAIL_LENGTH_SCALE);

        parts.get("ThighR").xRot += PERCHED_THIGH_PITCH * DEG_TO_RAD * blend;
        parts.get("ThighL").xRot += PERCHED_THIGH_PITCH * DEG_TO_RAD * blend;
        parts.get("LegR").xRot += PERCHED_LEG_PITCH * DEG_TO_RAD * blend;
        parts.get("LegL").xRot += PERCHED_LEG_PITCH * DEG_TO_RAD * blend;
    }

    private void applySleepingCurl(float blend) {
        for (int index = 0; index < SLEEPING_NECK_YAW.length; index++) {
            parts.get(MOUTH_PATH.get(index + 1)).yRot +=
                    SLEEPING_NECK_YAW[index] * DEG_TO_RAD * blend;
        }
        for (int index = 1; index <= SLEEPING_TAIL_YAW.length; index++) {
            parts.get("Tail" + index).yRot +=
                    SLEEPING_TAIL_YAW[index - 1] * DEG_TO_RAD * blend;
        }
    }

    private void blendCurrentPoseTo(Map<String, Pose> next, float blend) {
        for (Map.Entry<String, ModelPart> entry : parts.entrySet()) {
            ModelPart part = entry.getValue();
            Pose to = next.get(entry.getKey());
            part.setPos(
                    Mth.lerp(blend, part.x, to.x()),
                    Mth.lerp(blend, part.y, to.y()),
                    Mth.lerp(blend, part.z, to.z()));
            part.setRotation(
                    lerpAngle(blend, part.xRot, to.xRot()),
                    lerpAngle(blend, part.yRot, to.yRot()),
                    lerpAngle(blend, part.zRot, to.zRot()));
        }
    }

    private static float smoothStep(float value) {
        float clamped = Mth.clamp(value, 0.0F, 1.0F);
        return clamped * clamped * (3.0F - 2.0F * clamped);
    }

    private void resetScales() {
        for (ModelPart part : parts.values()) {
            part.xScale = 1.0F;
            part.yScale = 1.0F;
            part.zScale = 1.0F;
        }
    }

    private static ResourceLocation flightModel(int frame) {
        return new ResourceLocation(
                "tstmodern", "models/entity/draconic_crucible_fire_dragon_flight" + frame + ".tbl");
    }

    private void applyPose(Map<String, Pose> current, Map<String, Pose> next, float blend) {
        for (Map.Entry<String, ModelPart> entry : parts.entrySet()) {
            Pose from = current.get(entry.getKey());
            Pose to = next.get(entry.getKey());
            ModelPart part = entry.getValue();
            part.setPos(
                    Mth.lerp(blend, from.x(), to.x()),
                    Mth.lerp(blend, from.y(), to.y()),
                    Mth.lerp(blend, from.z(), to.z()));
            part.setRotation(
                    lerpAngle(blend, from.xRot(), to.xRot()),
                    lerpAngle(blend, from.yRot(), to.yRot()),
                    lerpAngle(blend, from.zRot(), to.zRot()));
        }
    }

    private static float lerpAngle(float blend, float from, float to) {
        float deltaDegrees = Mth.wrapDegrees((to - from) / DEG_TO_RAD);
        return from + deltaDegrees * DEG_TO_RAD * blend;
    }

    private static void transform(Matrix4f matrix, ModelPart part) {
        matrix.translate(part.x / 16.0F, part.y / 16.0F, part.z / 16.0F);
        if (part.zRot != 0.0F) matrix.rotateZ(part.zRot);
        if (part.yRot != 0.0F) matrix.rotateY(part.yRot);
        if (part.xRot != 0.0F) matrix.rotateX(part.xRot);
    }

    private static ModelFile readModel(ResourceManager resourceManager, ResourceLocation location) {
        try (ZipInputStream zip = new ZipInputStream(
                resourceManager.getResource(location)
                        .orElseThrow(() -> new IOException("Missing dragon model " + location))
                        .open())) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                if (!"model.json".equals(entry.getName())) continue;
                JsonObject json = JsonParser.parseReader(
                        new InputStreamReader(zip, StandardCharsets.UTF_8)).getAsJsonObject();
                return new ModelFile(
                        json.get("textureWidth").getAsInt(),
                        json.get("textureHeight").getAsInt(),
                        readParts(json.getAsJsonArray("cubes")));
            }
            throw new IOException("model.json missing from " + location);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load Ice and Fire dragon model " + location, exception);
        }
    }

    private static List<Part> readParts(JsonArray jsonParts) {
        List<Part> result = new ArrayList<>(jsonParts.size());
        for (var element : jsonParts) {
            JsonObject json = element.getAsJsonObject();
            int[] dimensions = ints(json.getAsJsonArray("dimensions"));
            float[] position = floats(json.getAsJsonArray("position"));
            float[] offset = floats(json.getAsJsonArray("offset"));
            float[] rotation = floats(json.getAsJsonArray("rotation"));
            float[] scale = floats(json.getAsJsonArray("scale"));
            if (scale[0] != 1.0F || scale[1] != 1.0F || scale[2] != 1.0F) {
                throw new IllegalStateException("Unsupported scaled Ice and Fire model part " + json.get("name"));
            }
            int[] texture = ints(json.getAsJsonArray("txOffset"));
            result.add(new Part(
                    json.get("name").getAsString(),
                    dimensions[0], dimensions[1], dimensions[2],
                    offset[0], offset[1], offset[2],
                    texture[0], texture[1],
                    json.get("txMirror").getAsBoolean(),
                    json.get("mcScale").getAsFloat(),
                    new Pose(
                            position[0], position[1], position[2],
                            rotation[0] * DEG_TO_RAD,
                            rotation[1] * DEG_TO_RAD,
                            rotation[2] * DEG_TO_RAD),
                    readParts(json.getAsJsonArray("children"))));
        }
        return List.copyOf(result);
    }

    private static void addPart(PartDefinition parent, Part part) {
        CubeListBuilder cube = CubeListBuilder.create()
                .texOffs(part.textureU(), part.textureV())
                .mirror(part.mirror())
                .addBox(
                        part.offsetX(), part.offsetY(), part.offsetZ(),
                        part.width(), part.height(), part.depth(),
                        new CubeDeformation(part.deformation()));
        PartDefinition child = parent.addOrReplaceChild(part.name(), cube, part.pose().asPartPose());
        for (Part nested : part.children()) addPart(child, nested);
    }

    private static void indexParts(ModelPart parent, Part spec, Map<String, ModelPart> result) {
        ModelPart part = parent.getChild(spec.name());
        if (result.put(spec.name(), part) != null) {
            throw new IllegalStateException("Duplicate Ice and Fire dragon part " + spec.name());
        }
        for (Part child : spec.children()) indexParts(part, child, result);
    }

    private static Map<String, Pose> poses(List<Part> roots) {
        Map<String, Pose> result = new HashMap<>();
        for (Part root : roots) addPoses(root, result);
        if (result.size() != EXPECTED_PART_COUNT) {
            throw new IllegalStateException("Expected " + EXPECTED_PART_COUNT
                    + " Ice and Fire pose parts, found " + result.size());
        }
        return result;
    }

    private static void addPoses(Part part, Map<String, Pose> result) {
        if (result.put(part.name(), part.pose()) != null) {
            throw new IllegalStateException("Duplicate Ice and Fire pose part " + part.name());
        }
        for (Part child : part.children()) addPoses(child, result);
    }

    private static void requireSameParts(Set<String> expected, Set<String> actual, ResourceLocation resource) {
        if (!expected.equals(actual)) {
            throw new IllegalStateException("Ice and Fire pose has mismatched parts: " + resource);
        }
    }

    private static int[] ints(JsonArray array) {
        int[] result = new int[array.size()];
        for (int index = 0; index < result.length; index++) result[index] = array.get(index).getAsInt();
        return result;
    }

    private static float[] floats(JsonArray array) {
        float[] result = new float[array.size()];
        for (int index = 0; index < result.length; index++) result[index] = array.get(index).getAsFloat();
        return result;
    }

    private record ModelFile(int textureWidth, int textureHeight, List<Part> parts) {}

    private record Part(String name, int width, int height, int depth,
                        float offsetX, float offsetY, float offsetZ,
                        int textureU, int textureV, boolean mirror, float deformation,
                        Pose pose, List<Part> children) {}

    private record Pose(float x, float y, float z, float xRot, float yRot, float zRot) {
        PartPose asPartPose() {
            return PartPose.offsetAndRotation(x, y, z, xRot, yRot, zRot);
        }
    }
}
