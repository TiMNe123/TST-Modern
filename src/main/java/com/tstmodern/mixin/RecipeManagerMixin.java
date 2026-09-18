package com.tstmodern.mixin;

import com.google.gson.JsonElement;
import com.tstmodern.config.TSTConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.fml.ModList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Set;

/**
 * Filters out original mod recipes (Avaritia / Draconic Evolution) when TST-Modern's
 * corresponding machine is configured in CUSTOM mode, ensuring mutual exclusivity
 * between custom machine recipes and original mod recipes.
 */
@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {

    @Unique
    private static final Logger tstmodern$LOGGER = LoggerFactory.getLogger("TSTModern-RecipeFilter");

    @Unique
    private static final Set<ResourceLocation> tstmodern$AVARITIA_ORIGINAL_RECIPES = Set.of(
            new ResourceLocation("avaritia", "infinity_ingot"),
            new ResourceLocation("avaritia", "infinity_catalyst"),
            new ResourceLocation("avaritia", "infinity_catalyst_eternal"),
            new ResourceLocation("avaritia", "crystal_matrix_ingot"),
            new ResourceLocation("avaritia", "crystal_matrix_ingot_normal"),
            new ResourceLocation("avaritia", "diamond_lattice"),
            new ResourceLocation("avaritia", "diamond_lattice_normal"),
            new ResourceLocation("minecraft", "infinity_ingot"),
            new ResourceLocation("minecraft", "infinity_ingot_from_infinity_nugget"),
            new ResourceLocation("minecraft", "crystal_matrix_ingot"),
            new ResourceLocation("minecraft", "diamond_lattice")
    );

    @Unique
    private static final Set<ResourceLocation> tstmodern$DRACONIC_ORIGINAL_RECIPES = Set.of(
            new ResourceLocation("draconicevolution", "awakened_draconium_block"),
            new ResourceLocation("draconicevolution", "components/draconium_core")
    );

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("HEAD"))
    private void tstmodern$filterRecipes(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {
        if (ModList.get().isLoaded("avaritia") && TSTConfig.GALACTIC_ARMILLARY_RECIPE_MODE.get() == TSTConfig.RecipeMode.CUSTOM) {
            for (ResourceLocation id : tstmodern$AVARITIA_ORIGINAL_RECIPES) {
                if (map.remove(id) != null) {
                    tstmodern$LOGGER.info("Disabled Avaritia original recipe '{}' in favor of TST-Modern Galactic Armillary custom recipe", id);
                }
            }
        }

        if (ModList.get().isLoaded("draconicevolution") && TSTConfig.DRACONIC_CRUCIBLE_RECIPE_MODE.get() == TSTConfig.RecipeMode.CUSTOM) {
            for (ResourceLocation id : tstmodern$DRACONIC_ORIGINAL_RECIPES) {
                if (map.remove(id) != null) {
                    tstmodern$LOGGER.info("Disabled Draconic Evolution original recipe '{}' in favor of TST-Modern Draconic Crucible custom recipe", id);
                }
            }
        }
    }
}
