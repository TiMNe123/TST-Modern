package com.tstmodern.registry;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.recipe.GTRecipeSerializer;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.tstmodern.TSTModern;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

/** Recipe map ported from TST's GTCMRecipe.MegaStoneBreakerRecipes. */
public final class TSTRecipeTypes {
    public static GTRecipeType MEGA_STONE_BREAKER;
    public static GTRecipeType VACUUM_FURNACE;
    public static GTRecipeType CHEMICAL_DEHYDRATOR;
    public static GTRecipeType NETHER_INTERFACE;
    public static GTRecipeType RAPID_HEAT_EXCHANGE;
    public static GTRecipeType RAPID_COOLING;

    private TSTRecipeTypes() {}

    public static void registerRecipeTypes(GTCEuAPI.RegisterEvent<ResourceLocation, GTRecipeType> event) {
        MEGA_STONE_BREAKER = register(event, "mega_stone_breaker")
                .setMaxIOSize(3, 1, 0, 0)
                .setEUIO(IO.IN);

        VACUUM_FURNACE = register(event, "vacuum_furnace")
                .setMaxIOSize(3, 3, 2, 2)
                .setEUIO(IO.IN);

        CHEMICAL_DEHYDRATOR = register(event, "chemical_dehydrator")
                .setMaxIOSize(2, 6, 2, 2)
                .setEUIO(IO.IN);

        NETHER_INTERFACE = register(event, "nether_interface")
                .setMaxIOSize(1, 5, 1, 2)
                .setEUIO(IO.IN);

        RAPID_HEAT_EXCHANGE = register(event, "rapid_heat_exchange")
                .setMaxIOSize(0, 0, 2, 2)
                .setEUIO(IO.IN);

        RAPID_COOLING = register(event, "rapid_cooling")
                .setMaxIOSize(0, 0, 2, 2)
                .setEUIO(IO.IN);
    }

    @SuppressWarnings("deprecation")
    private static GTRecipeType register(GTCEuAPI.RegisterEvent<ResourceLocation, GTRecipeType> event, String name) {
        GTRecipeType type = new GTRecipeType(TSTModern.id(name), GTRecipeTypes.MULTIBLOCK);
        com.gregtechceu.gtceu.api.registry.GTRegistries.register(BuiltInRegistries.RECIPE_TYPE,
                type.registryName, type);
        com.gregtechceu.gtceu.api.registry.GTRegistries.register(BuiltInRegistries.RECIPE_SERIALIZER,
                type.registryName, new GTRecipeSerializer());
        event.register(type.registryName, type);
        return type;
    }
}
