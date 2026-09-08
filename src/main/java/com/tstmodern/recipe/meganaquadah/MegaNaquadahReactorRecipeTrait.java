package com.tstmodern.recipe.meganaquadah;

import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.ActionResult;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.tstmodern.machine.MegaNaquadahReactorMachine;

public final class MegaNaquadahReactorRecipeTrait extends RecipeLogic {
    private boolean insufficientDynamo;

    public MegaNaquadahReactorRecipeTrait(MegaNaquadahReactorMachine machine) {
        super(machine);
    }

    @Override
    public ActionResult handleTickRecipe(GTRecipe recipe) {
        ActionResult result = super.handleTickRecipe(recipe);
        insufficientDynamo = !result.isSuccess() && result.io() == IO.OUT &&
                result.capability() == EURecipeCapability.CAP;
        return result;
    }

    @Override
    public void handleRecipeWorking() {
        super.handleRecipeWorking();
        if (!insufficientDynamo) return;
        insufficientDynamo = false;
        ((MegaNaquadahReactorMachine) machine).clearRunCache();
        resetRecipeLogic();
        setWorkingEnabled(false);
    }

    @Override
    public void onRecipeFinish() {
        // Re-run custom lookup so the next batch sees the updated 24-hour discount and current fluid tiers.
        markLastRecipeDirty();
        super.onRecipeFinish();
    }
}
