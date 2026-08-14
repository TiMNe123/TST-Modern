package com.tstmodern.mixin.client;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.category.GTRecipeCategory;
import com.gregtechceu.gtceu.integration.jei.recipe.GTRecipeJEICategory;
import com.gregtechceu.gtceu.integration.jei.recipe.GTRecipeWrapper;
import com.lowdragmc.lowdraglib.jei.ModularWrapper;
import com.tstmodern.registry.TSTRecipeTypes;

import mezz.jei.api.helpers.IJeiHelpers;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.TimeUnit;

/**
 * LDLib 1.0.40.b keeps only ten modular recipe wrappers per category. The Mega
 * Stone Breaker has more than ten recipes, so opening it through its controller
 * can evict wrappers after JEI has attached their input listeners. Drawing then
 * uses a new wrapper while clicks still target the evicted one.
 */
@Mixin(value = GTRecipeJEICategory.class, remap = false)
public abstract class MegaStoneBreakerJEICacheFixMixin {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void tstmodern$keepAllActiveRecipeWrappers(IJeiHelpers jeiHelpers,
                                                       GTRecipeCategory category,
                                                       CallbackInfo callback) {
        if (category.getRecipeType() != TSTRecipeTypes.MEGA_STONE_BREAKER) {
            return;
        }

        LoadingCache<Object, ModularWrapper<?>> cache = CacheBuilder.newBuilder()
                .expireAfterAccess(10, TimeUnit.SECONDS)
                .build(new CacheLoader<>() {
                    @Override
                    public @NotNull ModularWrapper<?> load(@NotNull Object key) {
                        return new GTRecipeWrapper((GTRecipe) key);
                    }
                });

        ((ModularUIRecipeCategoryCacheAccess) (Object) this)
                .tstmodern$setModularWrapperCache(cache);
    }
}
