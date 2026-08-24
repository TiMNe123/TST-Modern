package com.tstmodern;

import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.recipe.chance.logic.ChanceLogic;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.tstmodern.recipe.chance.TSTChanceLogics;
import com.tstmodern.recipe.disassembler.DisassemblerRecipeIndex;
import com.tstmodern.registry.TSTBlocks;
import com.tstmodern.registry.TSTMaterials;
import com.tstmodern.registry.machine.TSTMachineRegistry;
import com.tstmodern.registry.TSTRecipeTypes;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;


/** Forge entry point for the TST Modern port. */
@Mod(TSTModern.MOD_ID)
public final class TSTModern {
    public static final String MOD_ID = "tstmodern";
    public static final GTRegistrate REGISTRATE = GTRegistrate.create(MOD_ID);

    public TSTModern() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        TSTBlocks.register(modBus);
        modBus.addListener(TSTMaterials::registerMaterials);

        // GTCEu opens its recipe-type registry and posts this generic event before freezing it.
        modBus.addGenericListener(GTRecipeType.class, TSTRecipeTypes::registerRecipeTypes);
        modBus.addGenericListener(ChanceLogic.class, TSTChanceLogics::registerChanceLogics);
        modBus.addGenericListener(MachineDefinition.class, TSTMachineRegistry::registerMachines);
        MinecraftForge.EVENT_BUS.addListener(TSTModern::addReloadListeners);

        REGISTRATE.registerRegistrate();
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    private static void addReloadListeners(AddReloadListenerEvent event) {
        event.addListener((stage, resources, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor) -> {
            return DisassemblerRecipeIndex.INSTANCE.invalidateAfterReload(stage, gameExecutor);
        });
    }
}
