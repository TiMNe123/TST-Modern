package com.tstmodern.registry.machine;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import net.minecraft.resources.ResourceLocation;

/**
 * Initializes every machine definition in a stable order during GTCEu's registry event.
 */
public final class TSTMachineRegistry {
    private TSTMachineRegistry() {}

    public static void registerMachines(
            GTCEuAPI.RegisterEvent<ResourceLocation, MachineDefinition> event) {
        var megaStoneBreaker = MegaStoneBreakerDefinition.MACHINE;
        var giantVacuumDryingFurnace = GiantVacuumDryingFurnaceDefinition.MACHINE;
        var netherInterface = NetherInterfaceDefinition.MACHINE;
        var hyperThermalConvector = HyperThermalConvectorDefinition.MACHINE;
        var megaTreeFarm = MegaTreeFarmDefinition.MACHINE;
    }
}
