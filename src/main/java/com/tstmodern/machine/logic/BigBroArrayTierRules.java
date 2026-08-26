package com.tstmodern.machine.logic;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.block.MaterialBlock;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.pattern.util.PatternMatchContext;
import com.gregtechceu.gtceu.common.block.CoilBlock;
import com.gregtechceu.gtceu.common.data.GTMaterials;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

/** Tier lookup and structure-channel rules shared by the Big Bro Array pattern and controller. */
public final class BigBroArrayTierRules {
    public static final String FRAME_TIER_CONTEXT = "tstmodern_big_bro_array_frame_tier";
    public static final String GLASS_TIER_CONTEXT = "tstmodern_big_bro_array_glass_tier";
    public static final String MACHINE_CASING_TIER_CONTEXT = "tstmodern_big_bro_array_machine_casing_tier";

    private BigBroArrayTierRules() {}

    /** Titanium through Tritanium frame tier, or {@code -1} for a non-frame/unsupported material. */
    public static int frameTier(Block block) {
        if (!(block instanceof MaterialBlock frame) || frame.tagPrefix != TagPrefix.frameGt) {
            return -1;
        }
        if (frame.material == GTMaterials.Titanium) return 1;
        if (frame.material == GTMaterials.TungstenSteel) return 2;
        if (frame.material == GTMaterials.NaquadahAlloy) return 3;
        if (frame.material == GTMaterials.Trinium) return 4;
        if (frame.material == GTMaterials.Neutronium) return 5;
        if (frame.material == GTMaterials.Tritanium) return 6;
        return -1;
    }

    /** IV tempered glass or MAX fusion glass, or {@code -1} for another block. */
    public static int glassTier(Block block) {
        ResourceLocation id = ForgeRegistries.BLOCKS.getKey(block);
        if (id == null || !"gtceu".equals(id.getNamespace())) return -1;
        return switch (id.getPath()) {
            case "tempered_glass" -> GTValues.IV;
            case "fusion_glass" -> GTValues.MAX;
            default -> -1;
        };
    }

    /** Native LV-through-MAX machine casing tier, or {@code -1} for another block. */
    public static int machineCasingTier(Block block) {
        ResourceLocation id = ForgeRegistries.BLOCKS.getKey(block);
        if (id == null || !"gtceu".equals(id.getNamespace())) return -1;
        return switch (id.getPath()) {
            case "lv_machine_casing" -> GTValues.LV;
            case "mv_machine_casing" -> GTValues.MV;
            case "hv_machine_casing" -> GTValues.HV;
            case "ev_machine_casing" -> GTValues.EV;
            case "iv_machine_casing" -> GTValues.IV;
            case "luv_machine_casing" -> GTValues.LuV;
            case "zpm_machine_casing" -> GTValues.ZPM;
            case "uv_machine_casing" -> GTValues.UV;
            case "uhv_machine_casing" -> GTValues.UHV;
            case "uev_machine_casing" -> GTValues.UEV;
            case "uiv_machine_casing" -> GTValues.UIV;
            case "uxv_machine_casing" -> GTValues.UXV;
            case "opv_machine_casing" -> GTValues.OpV;
            case "max_machine_casing" -> GTValues.MAX;
            default -> -1;
        };
    }

    /** MK1-through-MK5 parallel casing tier, or {@code -1} for another block. */
    public static int parallelCasingTier(Block block) {
        ResourceLocation id = ForgeRegistries.BLOCKS.getKey(block);
        if (id == null || !"tstmodern".equals(id.getNamespace())) return -1;
        return switch (id.getPath()) {
            case "parallel_casing_mk1" -> 1;
            case "parallel_casing_mk2" -> 2;
            case "parallel_casing_mk3" -> 3;
            case "parallel_casing_mk4" -> 4;
            case "parallel_casing_mk5" -> 5;
            default -> -1;
        };
    }

    /** Native heating-coil tier, or {@code -1} for a non-coil state. */
    public static int coilTier(BlockState state) {
        if (state != null && state.getBlock() instanceof CoilBlock coil && coil.coilType != null) {
            return coil.coilType.getTier();
        }
        return -1;
    }

    /** Maximum embedded single-block machine tier unlocked by the uniform frame channel. */
    public static int maxEmbeddedTier(int frameTier) {
        return switch (frameTier) {
            case 1 -> GTValues.IV;
            case 2 -> GTValues.LuV;
            case 3 -> GTValues.ZPM;
            case 4 -> GTValues.UV;
            case 5 -> GTValues.UHV;
            case 6 -> GTValues.MAX;
            default -> -1;
        };
    }

    /** Whether the formed frame channel currently permits this embedded machine tier to execute. */
    public static boolean isEmbeddedTierEligible(int frameTier, int embeddedTier) {
        int maximumTier = maxEmbeddedTier(frameTier);
        return embeddedTier >= GTValues.ULV && maximumTier >= 0 && embeddedTier <= maximumTier;
    }

    /** Stores the first valid tier for a channel and rejects every later mismatch. */
    public static boolean matchUniformTier(PatternMatchContext context, String key, int tier) {
        if (context == null || tier < 0) return false;
        Integer firstTier = context.get(key);
        if (firstTier == null) {
            context.set(key, tier);
            return true;
        }
        return firstTier == tier;
    }

    /** Returns one validated core snapshot, or {@code null} when the context is incomplete or invalid. */
    public static CoreTiers validatedCoreTiers(PatternMatchContext context) {
        if (context == null) return null;
        Integer frameTier = context.get(FRAME_TIER_CONTEXT);
        Integer glassTier = context.get(GLASS_TIER_CONTEXT);
        Integer machineCasingTier = context.get(MACHINE_CASING_TIER_CONTEXT);
        if (frameTier == null || glassTier == null || machineCasingTier == null ||
                maxEmbeddedTier(frameTier) < 0 ||
                glassTier != GTValues.IV && glassTier != GTValues.MAX ||
                machineCasingTier < GTValues.LV || machineCasingTier > GTValues.MAX) {
            return null;
        }
        return new CoreTiers(frameTier, glassTier, machineCasingTier);
    }

    public record CoreTiers(int frameTier, int glassTier, int machineCasingTier) {}
}
