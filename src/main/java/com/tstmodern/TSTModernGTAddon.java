package com.tstmodern;

import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.tstmodern.data.recipe.BigBroArrayRecipes;
import com.tstmodern.data.recipe.DisassemblerRecipes;
import com.tstmodern.data.recipe.GiantVacuumDryingFurnaceRecipes;
import com.tstmodern.data.recipe.HyperThermalConvectorRecipes;
import com.tstmodern.data.recipe.MassFabricatorRecipes;
import com.tstmodern.data.recipe.MegaStoneBreakerRecipes;
import com.tstmodern.data.recipe.MegaTreeFarmRecipes;
import com.tstmodern.data.recipe.NetherInterfaceRecipes;

import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

/** Connects the Forge mod to GTCEu's addon lifecycle. */
@GTAddon
public final class TSTModernGTAddon implements IGTAddon {
    @Override
    public boolean requiresHighTier() {
        return true;
    }

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
        MegaStoneBreakerRecipes.register(provider);
        GiantVacuumDryingFurnaceRecipes.register(provider);
        NetherInterfaceRecipes.register(provider);
        HyperThermalConvectorRecipes.register(provider);
        MegaTreeFarmRecipes.register(provider);
        DisassemblerRecipes.register(provider);
        BigBroArrayRecipes.register(provider);
        MassFabricatorRecipes.register(provider);
    }
}

