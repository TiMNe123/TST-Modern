package com.tstmodern.registry;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.recipe.GTRecipeSerializer;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.tstmodern.TSTModern;
import com.tstmodern.recipe.disassembler.DisassemblerRecipeIndex;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

/** Recipe map definitions for ported TST multiblocks. */
public final class TSTRecipeTypes {
    public static GTRecipeType MEGA_STONE_BREAKER;
    public static GTRecipeType VACUUM_FURNACE;
    public static GTRecipeType CHEMICAL_DEHYDRATOR;
    public static GTRecipeType NETHER_INTERFACE;
    public static GTRecipeType RAPID_HEAT_EXCHANGE;
    public static GTRecipeType RAPID_COOLING;
    public static GTRecipeType TREE_GROWTH_SIMULATOR;
    public static GTRecipeType AQUATIC_ZONE_SIMULATOR;
    public static GTRecipeType DISASSEMBLER;
    public static GTRecipeType MASS_FABRICATOR;
    public static GTRecipeType CYCLOTRON_RECIPES;
    public static GTRecipeType NEUTRON_ACTIVATOR;

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

        TREE_GROWTH_SIMULATOR = register(event, "tree_growth_simulator")
                .setMaxIOSize(3, 6, 1, 0)
                .setEUIO(IO.IN);

        AQUATIC_ZONE_SIMULATOR = register(event, "aquatic_zone_simulator")
                .setMaxIOSize(1, 9, 1, 0)
                .setEUIO(IO.IN);

        DISASSEMBLER = configureDisassembler(register(event, "disassembler"));

        MASS_FABRICATOR = register(event, "mass_fabricator", GTRecipeTypes.ELECTRIC)
                .setMaxIOSize(1, 0, 1, 1)
                .setEUIO(IO.IN)
                .setSlotOverlay(false, false, true, com.gregtechceu.gtceu.api.gui.GuiTextures.SOLIDIFIER_OVERLAY)
                .setProgressBar(com.gregtechceu.gtceu.api.gui.GuiTextures.PROGRESS_BAR_MASS_FAB, com.lowdragmc.lowdraglib.gui.texture.ProgressTexture.FillDirection.LEFT_TO_RIGHT);

        CYCLOTRON_RECIPES = register(event, "cyclotron")
                .setMaxIOSize(9, 9, 1, 1)
                .setEUIO(IO.IN);

        NEUTRON_ACTIVATOR = register(event, "neutron_activator")
                .setMaxIOSize(9, 9, 1, 1)
                .setEUIO(IO.IN);
    }

    static GTRecipeType configureDisassembler(GTRecipeType type) {
        return disassemblerConfiguration().apply(type);
    }

    static DisassemblerRecipeTypeConfiguration disassemblerConfiguration() {
        return new DisassemblerRecipeTypeConfiguration(16, 16, 0, 4, DisassemblerRecipeIndex.INSTANCE);
    }

    record DisassemblerRecipeTypeConfiguration(int maxItemInputs, int maxItemOutputs, int maxFluidInputs,
                                               int maxFluidOutputs, DisassemblerRecipeIndex customLogic) {
        GTRecipeType apply(GTRecipeType type) {
            return type.setMaxIOSize(maxItemInputs, maxItemOutputs, maxFluidInputs, maxFluidOutputs)
                    .addCustomRecipeLogic(customLogic);
        }
    }

    private static GTRecipeType register(GTCEuAPI.RegisterEvent<ResourceLocation, GTRecipeType> event, String name) {
        return register(event, name, GTRecipeTypes.MULTIBLOCK);
    }

    @SuppressWarnings("deprecation")
    private static GTRecipeType register(GTCEuAPI.RegisterEvent<ResourceLocation, GTRecipeType> event, String name, String group) {
        GTRecipeType type = new GTRecipeType(TSTModern.id(name), group);
        com.gregtechceu.gtceu.api.registry.GTRegistries.register(BuiltInRegistries.RECIPE_TYPE,
                type.registryName, type);
        com.gregtechceu.gtceu.api.registry.GTRegistries.register(BuiltInRegistries.RECIPE_SERIALIZER,
                type.registryName, new GTRecipeSerializer());
        event.register(type.registryName, type);
        return type;
    }
}
