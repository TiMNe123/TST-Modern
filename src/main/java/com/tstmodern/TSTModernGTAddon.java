package com.tstmodern;

import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.tstmodern.data.TSTRecipes;

import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

/** Connects the Forge mod to GTCEu's addon lifecycle. */
@GTAddon
public final class TSTModernGTAddon implements IGTAddon {
    @Override
    public GTRegistrate getRegistrate() {
        return TSTModern.REGISTRATE;
    }

    @Override
    public void initializeAddon() {
        // Content is registered through GTCEu's typed registry events.
    }

    @Override
    public String addonModId() {
        return TSTModern.MOD_ID;
    }

    @Override
    public void addRecipes(Consumer<FinishedRecipe> provider) {
        TSTRecipes.addMegaStoneBreakerRecipes(provider);
        TSTRecipes.addConstructionRecipes(provider);
    }
}
