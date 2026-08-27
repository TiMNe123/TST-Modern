package com.tstmodern.recipe.disassembler;

import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.wireGtSingle;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.item.IGTTool;
import com.gregtechceu.gtceu.api.item.TagPrefixItem;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.api.recipe.ingredient.FluidIngredient;
import com.gregtechceu.gtceu.api.recipe.ingredient.SizedIngredient;
import com.gregtechceu.gtceu.common.data.GTMaterials;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

/** Decodes eligible GTCEu recipes into the deterministic reverse-recipe form used by the disassembler. */
public final class DisassemblerRecipeAdapter {

    private static final TagKey<Item> BLACKLIST = TagKey.create(Registries.ITEM,
            new ResourceLocation("tstmodern", "disassembler_blacklist"));

    public Optional<DisassemblerRecipeDescriptor> adapt(GTRecipe recipe, int sourcePriority) {
        if (recipe == null) {
            return Optional.empty();
        }
        Optional<List<DecodedItemContent>> itemInputs = decodeItems(recipe.getInputContents(ItemRecipeCapability.CAP), true);
        Optional<List<DecodedFluidContent>> fluidInputs = decodeFluids(recipe.getInputContents(FluidRecipeCapability.CAP), true);
        Optional<List<DecodedItemContent>> itemOutputs = decodeItems(recipe.getOutputContents(ItemRecipeCapability.CAP), false);
        List<Content> fluidOutputs = recipe.getOutputContents(FluidRecipeCapability.CAP);
        if (itemInputs.isEmpty() || fluidInputs.isEmpty() || itemOutputs.isEmpty() || fluidOutputs == null) {
            return Optional.empty();
        }
        return adapt(new DecodedRecipe(recipe.getId(), RecipeHelper.getRecipeEUtTier(recipe), itemInputs.get(), fluidInputs.get(),
                itemOutputs.get(), fluidOutputs.size()), sourcePriority, this::isBlacklisted);
    }

    static Optional<DisassemblerRecipeDescriptor> adapt(DecodedRecipe recipe,
                                                         int sourcePriority,
                                                         Predicate<Item> blacklist) {
        if (recipe == null || blacklist == null || recipe.itemOutputs() == null || recipe.itemInputs() == null
                || recipe.fluidInputs() == null || recipe.itemOutputs().size() != 1 || recipe.fluidOutputCount() != 0) {
            return Optional.empty();
        }
        Optional<ItemAmount> output = itemAmount(recipe.itemOutputs().get(0));
        if (output.isEmpty() || blacklist.test(output.get().item()) || recipe.sourceId() == null) {
            return Optional.empty();
        }
        Optional<List<DisassemblerRecipeDescriptor.ReturnedItem>> returnedItems = itemReturns(recipe.itemInputs());
        Optional<List<DisassemblerRecipeDescriptor.ReturnedFluid>> returnedFluids = fluidReturns(recipe.fluidInputs());
        if (returnedItems.isEmpty() || returnedFluids.isEmpty()) return Optional.empty();
        return Optional.of(new DisassemblerRecipeDescriptor(
                recipe.sourceId(),
                sourcePriority,
                recipe.recipeTier(),
                output.get().item(),
                output.get().amount(),
                returnedItems.get(),
                returnedFluids.get()));
    }

    public boolean isBlacklisted(Item item) {
        if (item == null) {
            return true;
        }
        if (item instanceof IGTTool) {
            return true;
        }
        if (item instanceof TagPrefixItem prefixItem && prefixItem.tagPrefix != null
                && (("nanite".equals(prefixItem.tagPrefix.name) && prefixItem.material == GTMaterials.Carbon)
                        || prefixItem.tagPrefix == wireGtSingle)) {
            return true;
        }
        return item.builtInRegistryHolder().is(BLACKLIST);
    }

    private static Optional<List<DisassemblerRecipeDescriptor.ReturnedItem>> itemReturns(List<DecodedItemContent> contents) {
        if (contents == null) {
            return Optional.empty();
        }
        List<DisassemblerRecipeDescriptor.ReturnedItem> returned = new ArrayList<>();
        for (DecodedItemContent content : contents) {
            if (content == null) {
                return Optional.empty();
            }
            if (content.chance() == 0) {
                continue;
            }
            Optional<ItemAmount> item = itemAmount(content);
            if (item.isEmpty()) {
                return Optional.empty();
            }
            returned.add(new DisassemblerRecipeDescriptor.ReturnedItem(item.get().item(), item.get().amount()));
        }
        return Optional.of(List.copyOf(returned));
    }

    private static Optional<List<DisassemblerRecipeDescriptor.ReturnedFluid>> fluidReturns(List<DecodedFluidContent> contents) {
        if (contents == null) {
            return Optional.empty();
        }
        List<DisassemblerRecipeDescriptor.ReturnedFluid> returned = new ArrayList<>();
        for (DecodedFluidContent content : contents) {
            if (content == null) {
                return Optional.empty();
            }
            if (content.chance() == 0) {
                continue;
            }
            if (content.alternatives() == null || content.alternatives().isEmpty() || content.alternatives().get(0) == null
                    || content.amount() <= 0) {
                return Optional.empty();
            }
            returned.add(new DisassemblerRecipeDescriptor.ReturnedFluid(content.alternatives().get(0), content.amount()));
        }
        return Optional.of(List.copyOf(returned));
    }

    private static Optional<ItemAmount> itemAmount(DecodedItemContent content) {
        if (content == null || content.alternatives() == null || content.alternatives().isEmpty()
                || content.alternatives().get(0) == null || content.amount() <= 0) {
            return Optional.empty();
        }
        return Optional.of(new ItemAmount(content.alternatives().get(0), content.amount()));
    }

    static Optional<List<DecodedItemContent>> decodeItems(List<Content> contents, boolean omitChanceZero) {
        if (contents == null) {
            return Optional.empty();
        }
        List<DecodedItemContent> decoded = new ArrayList<>();
        try {
            for (Content content : contents) {
                if (content == null) {
                    return Optional.empty();
                }
                if (omitChanceZero && content.chance == 0) {
                    continue;
                }
                Ingredient ingredient = ItemRecipeCapability.CAP.of(content.content);
                if (ingredient == null || ingredient.getItems() == null) {
                    return Optional.empty();
                }
                ItemStack[] stacks = ingredient.getItems();
                List<Item> alternatives = new ArrayList<>(stacks.length);
                for (ItemStack stack : stacks) {
                    if (stack == null || stack.getItem() == null) {
                        return Optional.empty();
                    }
                    alternatives.add(stack.getItem());
                }
                int amount = ingredient instanceof SizedIngredient sized ? sized.getAmount() : 1;
                decoded.add(new DecodedItemContent(List.copyOf(alternatives), amount, content.chance));
            }
        } catch (ClassCastException | IllegalArgumentException | NullPointerException ignored) {
            return Optional.empty();
        }
        return Optional.of(List.copyOf(decoded));
    }

    static Optional<List<DecodedFluidContent>> decodeFluids(List<Content> contents, boolean omitChanceZero) {
        if (contents == null) {
            return Optional.empty();
        }
        List<DecodedFluidContent> decoded = new ArrayList<>();
        try {
            for (Content content : contents) {
                if (content == null) {
                    return Optional.empty();
                }
                if (omitChanceZero && content.chance == 0) {
                    continue;
                }
                FluidIngredient ingredient = FluidRecipeCapability.CAP.of(content.content);
                if (ingredient == null || ingredient.getStacks() == null) {
                    return Optional.empty();
                }
                FluidStack[] stacks = ingredient.getStacks();
                List<Fluid> alternatives = new ArrayList<>(stacks.length);
                for (FluidStack stack : stacks) {
                    if (stack == null || stack.getFluid() == null) {
                        return Optional.empty();
                    }
                    alternatives.add(stack.getFluid());
                }
                decoded.add(new DecodedFluidContent(List.copyOf(alternatives), ingredient.getAmount(), content.chance));
            }
        } catch (ClassCastException | IllegalArgumentException | NullPointerException ignored) {
            return Optional.empty();
        }
        return Optional.of(List.copyOf(decoded));
    }

    record DecodedRecipe(ResourceLocation sourceId, int recipeTier, List<DecodedItemContent> itemInputs,
                         List<DecodedFluidContent> fluidInputs, List<DecodedItemContent> itemOutputs, int fluidOutputCount) {}

    record DecodedItemContent(List<Item> alternatives, int amount, int chance) {}

    record DecodedFluidContent(List<Fluid> alternatives, int amount, int chance) {}

    private record ItemAmount(Item item, int amount) {}
}
