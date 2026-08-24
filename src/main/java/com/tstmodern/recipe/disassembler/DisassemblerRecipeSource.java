package com.tstmodern.recipe.disassembler;

import java.util.Objects;
import java.util.function.Supplier;

import com.gregtechceu.gtceu.api.recipe.GTRecipeType;

/** One ordered GTCEu recipe source that can supply reversible recipes. */
public record DisassemblerRecipeSource(String id, int priority, Supplier<GTRecipeType> recipeType) {
    public DisassemblerRecipeSource {
        id = Objects.requireNonNull(id, "id");
        recipeType = Objects.requireNonNull(recipeType, "recipeType");
    }
}
