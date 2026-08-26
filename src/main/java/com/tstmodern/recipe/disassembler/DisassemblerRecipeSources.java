package com.tstmodern.recipe.disassembler;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;

import net.minecraft.resources.ResourceLocation;

/** Open, generation-tracked registry of recipe sources reversible by the Disassembler. */
public final class DisassemblerRecipeSources {
    public static final int PRIORITY_SPECIAL = 0;
    public static final int PRIORITY_COMPONENT_ASSEMBLY = 10;
    public static final int PRIORITY_MIRACLE_TOP = 20;
    public static final int PRIORITY_ASSEMBLY_LINE = 30;
    public static final int PRIORITY_ASSEMBLER = 40;
    public static final int PRIORITY_PHOTON_CONTROLLER = 50;

    public static final DisassemblerRecipeSources INSTANCE = createDefaults();

    private final Map<String, DisassemblerRecipeSource> sources = new LinkedHashMap<>();
    private long generation;

    public synchronized void registerRecipeType(String id, int priority, Supplier<GTRecipeType> recipeType) {
        register(DisassemblerRecipeSource.recipeType(id, priority, recipeType));
    }

    public synchronized void registerRecipeType(String id, int priority, Supplier<GTRecipeType> recipeType,
                                                Predicate<ResourceLocation> filter) {
        register(DisassemblerRecipeSource.recipeType(id, priority, recipeType, filter));
    }

    public synchronized void registerSpecial(String id, int priority,
                                             Supplier<? extends java.util.Collection<DisassemblerRecipeDescriptor>> recipes) {
        register(DisassemblerRecipeSource.special(id, priority, recipes));
    }

    public synchronized long generation() {
        return generation;
    }

    synchronized Snapshot snapshot() {
        return new Snapshot(generation, List.copyOf(sources.values()));
    }

    static boolean isPhotonControllerRecipe(ResourceLocation recipeId) {
        return hasTstPathPrefix(recipeId, "photon_controller/");
    }

    static boolean isComponentAssemblyRecipe(ResourceLocation recipeId) {
        return hasTstPathPrefix(recipeId, "component_assembly_line/");
    }

    private synchronized void register(DisassemblerRecipeSource source) {
        Objects.requireNonNull(source, "source");
        if (sources.putIfAbsent(source.id(), source) != null) {
            throw new IllegalArgumentException("Disassembler recipe source already registered: " + source.id());
        }
        generation++;
    }

    private static DisassemblerRecipeSources createDefaults() {
        DisassemblerRecipeSources registry = new DisassemblerRecipeSources();
        registry.registerSpecial("special", PRIORITY_SPECIAL, DisassemblerSpecialRecipes::all);
        registry.registerRecipeType("component_assembly_line", PRIORITY_COMPONENT_ASSEMBLY,
                () -> GTRecipeTypes.ASSEMBLER_RECIPES, DisassemblerRecipeSources::isComponentAssemblyRecipe);
        registry.registerRecipeType("assembly_line_without_research", PRIORITY_ASSEMBLY_LINE,
                () -> GTRecipeTypes.ASSEMBLY_LINE_RECIPES);
        registry.registerRecipeType("assembler", PRIORITY_ASSEMBLER,
                () -> GTRecipeTypes.ASSEMBLER_RECIPES,
                recipeId -> !isComponentAssemblyRecipe(recipeId));
        registry.registerRecipeType("photon_controller", PRIORITY_PHOTON_CONTROLLER,
                () -> GTRecipeTypes.LASER_ENGRAVER_RECIPES, DisassemblerRecipeSources::isPhotonControllerRecipe);
        return registry;
    }

    private static boolean hasTstPathPrefix(ResourceLocation recipeId, String prefix) {
        return recipeId != null && "tstmodern".equals(recipeId.getNamespace())
                && recipeId.getPath().startsWith(prefix);
    }

    record Snapshot(long generation, List<DisassemblerRecipeSource> sources) {}
}
