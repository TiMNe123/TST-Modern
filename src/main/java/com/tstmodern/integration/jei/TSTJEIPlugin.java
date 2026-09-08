package com.tstmodern.integration.jei;

import com.gregtechceu.gtceu.integration.jei.recipe.GTRecipeJEICategory;
import com.tstmodern.TSTModern;
import com.tstmodern.registry.TSTRecipeTypes;
import com.tstmodern.registry.machine.MegaNaquadahReactorDefinition;
import com.tstmodern.registry.machine.NaquadahFuelRefineryDefinition;

import net.minecraft.resources.ResourceLocation;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public final class TSTJEIPlugin implements IModPlugin {
    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return TSTModern.id("jei_plugin");
    }

    @Override
    public void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(
                MegaNaquadahReactorDefinition.MACHINE.asStack(),
                GTRecipeJEICategory.TYPES.apply(TSTRecipeTypes.MEGA_NAQUADAH_REACTOR_FUELS.getCategory()));
        registration.addRecipeCatalyst(
                NaquadahFuelRefineryDefinition.MACHINE.asStack(),
                GTRecipeJEICategory.TYPES.apply(TSTRecipeTypes.NAQUADAH_FUEL_REFINERY.getCategory()));
    }
}
