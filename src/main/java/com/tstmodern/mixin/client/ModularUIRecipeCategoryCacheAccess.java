package com.tstmodern.mixin.client;

import com.google.common.cache.LoadingCache;
import com.lowdragmc.lowdraglib.jei.ModularUIRecipeCategory;
import com.lowdragmc.lowdraglib.jei.ModularWrapper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ModularUIRecipeCategory.class, remap = false)
public interface ModularUIRecipeCategoryCacheAccess {
    @Mutable
    @Accessor("modularWrapperCache")
    void tstmodern$setModularWrapperCache(LoadingCache<Object, ModularWrapper<?>> cache);
}
