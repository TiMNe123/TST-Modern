package com.tstmodern.recipe.disassembler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;

import net.minecraft.resources.ResourceLocation;

/** One ordered source that can supply reversible Disassembler recipes. */
public record DisassemblerRecipeSource(String id, int priority, DescriptorProvider provider) {

    public DisassemblerRecipeSource {
        id = Objects.requireNonNull(id, "id");
        if (id.isBlank()) {
            throw new IllegalArgumentException("id must not be blank");
        }
        provider = Objects.requireNonNull(provider, "provider");
    }

    public Collection<DisassemblerRecipeDescriptor> enumerate(DisassemblerRecipeAdapter adapter) {
        Collection<DisassemblerRecipeDescriptor> descriptors = provider.enumerate(adapter);
        if (descriptors == null || descriptors.isEmpty()) {
            return List.of();
        }
        List<DisassemblerRecipeDescriptor> normalized = new ArrayList<>(descriptors.size());
        for (DisassemblerRecipeDescriptor descriptor : descriptors) {
            if (descriptor != null) {
                normalized.add(descriptor.withSourcePriority(priority));
            }
        }
        return List.copyOf(normalized);
    }

    public static DisassemblerRecipeSource recipeType(String id, int priority,
                                                       Supplier<GTRecipeType> recipeType) {
        return recipeType(id, priority, recipeType, recipeId -> true);
    }

    public static DisassemblerRecipeSource recipeType(String id, int priority,
                                                       Supplier<GTRecipeType> recipeType,
                                                       Predicate<ResourceLocation> recipeFilter) {
        Objects.requireNonNull(recipeType, "recipeType");
        Objects.requireNonNull(recipeFilter, "recipeFilter");
        return new DisassemblerRecipeSource(id, priority, adapter -> {
            GTRecipeType type = recipeType.get();
            if (type == null) {
                return List.of();
            }
            List<DisassemblerRecipeDescriptor> descriptors = new ArrayList<>();
            for (GTRecipe recipe : DisassemblerRecipeIndex.visitAll(type.getCategories(), type::getRecipesInCategory)) {
                if (!recipeFilter.test(recipe.getId())) {
                    continue;
                }
                Optional<DisassemblerRecipeDescriptor> descriptor = adapter.adapt(recipe, priority);
                if (descriptor != null) {
                    descriptor.ifPresent(descriptors::add);
                }
            }
            return descriptors;
        });
    }

    public static DisassemblerRecipeSource special(String id, int priority,
                                                    Supplier<? extends Collection<DisassemblerRecipeDescriptor>> recipes) {
        Objects.requireNonNull(recipes, "recipes");
        return new DisassemblerRecipeSource(id, priority, adapter -> recipes.get());
    }

    @FunctionalInterface
    public interface DescriptorProvider {
        Collection<DisassemblerRecipeDescriptor> enumerate(DisassemblerRecipeAdapter adapter);
    }
}
