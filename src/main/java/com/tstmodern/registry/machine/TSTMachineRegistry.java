package com.tstmodern.registry.machine;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

/**
 * Initializes every machine definition in a stable order during GTCEu's registry event.
 */
public final class TSTMachineRegistry {
    private TSTMachineRegistry() {}

    public static void registerMachines(
            GTCEuAPI.RegisterEvent<ResourceLocation, MachineDefinition> event) {
        Objects.requireNonNull(MegaStoneBreakerDefinition.MACHINE);
        Objects.requireNonNull(GiantVacuumDryingFurnaceDefinition.MACHINE);
        Objects.requireNonNull(NetherInterfaceDefinition.MACHINE);
        Objects.requireNonNull(HyperThermalConvectorDefinition.MACHINE);
        Objects.requireNonNull(MegaTreeFarmDefinition.MACHINE);
        Objects.requireNonNull(DisassemblerDefinition.MACHINE);
        Objects.requireNonNull(BigBroArrayDefinition.MACHINE);
        Objects.requireNonNull(IncompactCyclotronDefinition.MACHINE);
        Objects.requireNonNull(MassFabricatorDefinition.MACHINES);
    }
}
