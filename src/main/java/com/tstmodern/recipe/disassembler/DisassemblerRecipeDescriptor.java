package com.tstmodern.recipe.disassembler;

import java.util.List;
import java.util.Objects;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

/** Immutable reverse-recipe information used by the disassembler policy. */
public record DisassemblerRecipeDescriptor(
                                           ResourceLocation sourceId,
                                           int sourcePriority,
                                           int recipeTier,
                                           Item outputItem,
                                           int outputAmount,
                                           List<ReturnedItem> returnedItems,
                                           List<ReturnedFluid> returnedFluids) {

    public DisassemblerRecipeDescriptor {
        sourceId = Objects.requireNonNull(sourceId, "sourceId");
        outputItem = Objects.requireNonNull(outputItem, "outputItem");
        if (outputAmount <= 0) {
            throw new IllegalArgumentException("outputAmount must be positive");
        }
        returnedItems = List.copyOf(Objects.requireNonNull(returnedItems, "returnedItems"));
        returnedFluids = List.copyOf(Objects.requireNonNull(returnedFluids, "returnedFluids"));
    }

    public record ReturnedItem(Item item, int amount) {
        public ReturnedItem {
            item = Objects.requireNonNull(item, "item");
            if (amount <= 0) {
                throw new IllegalArgumentException("amount must be positive");
            }
        }
    }

    public record ReturnedFluid(Fluid fluid, int amount) {
        public ReturnedFluid {
            fluid = Objects.requireNonNull(fluid, "fluid");
            if (amount <= 0) {
                throw new IllegalArgumentException("amount must be positive");
            }
        }
    }
}
