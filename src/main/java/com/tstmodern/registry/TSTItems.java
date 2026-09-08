package com.tstmodern.registry;

import com.tstmodern.TSTModern;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class TSTItems {
    private static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, TSTModern.MOD_ID);

    public static final RegistryObject<Item> HYDROGEN_ION = item("hydrogen_ion");
    public static final RegistryObject<Item> PROTON = item("proton");
    public static final RegistryObject<Item> ELECTRON = item("electron");
    public static final RegistryObject<Item> NEUTRON = item("neutron");
    public static final RegistryObject<Item> UNKNOWN_PARTICLE = item("unknown_particle");
    public static final RegistryObject<Item> SPECIAL_LASER_LENS = item("special_laser_lens");
    public static final RegistryObject<Item> STRANGE_DUST = item("strange_dust");
    public static final RegistryObject<Item> TESSERACT = item("tesseract");
    public static final RegistryObject<Item> ENERGISED_TESSERACT = item("energised_tesseract");
    public static final RegistryObject<Item> NEUTRON_ACTIVATOR_COMPONENT = item("neutron_activator_component");
    public static final RegistryObject<Item> HIGH_COMPUTATION_STATION_T5 = item("high_computation_station_t5");
    public static final RegistryObject<Item> NAQUADAH_REACTOR_CORE = item("naquadah_reactor_core");
    public static final RegistryObject<Item> RADIATION_PROTECTION_PLATE = item("radiation_protection_plate");
    public static final RegistryObject<Item> ADVANCED_RADIATION_PROTECTION_PLATE = item(
            "advanced_radiation_protection_plate");
    public static final RegistryObject<Item> RADIATION_PROOF_PRISMATIC_NAQUADAH_COMPOSITE_SHEET = item(
            "radiation_proof_prismatic_naquadah_composite_sheet");
    public static final RegistryObject<Item> UHV_VOLTAGE_COIL = item("uhv_voltage_coil");
    public static final RegistryObject<Item> RAW_ATOMIC_SEPARATION_CATALYST = item(
            "raw_atomic_separation_catalyst");
    public static final RegistryObject<Item> WRAPPED_URANIUM_INGOT = item("wrapped_uranium_ingot");
    public static final RegistryObject<Item> HIGH_DENSITY_URANIUM_NUGGET = item("high_density_uranium_nugget");
    public static final RegistryObject<Item> HIGH_DENSITY_URANIUM = item("high_density_uranium");
    public static final RegistryObject<Item> WRAPPED_PLUTONIUM_INGOT = item("wrapped_plutonium_ingot");
    public static final RegistryObject<Item> HIGH_DENSITY_PLUTONIUM_NUGGET = item("high_density_plutonium_nugget");
    public static final RegistryObject<Item> HIGH_DENSITY_PLUTONIUM = item("high_density_plutonium");


    private TSTItems() {}

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
    }

    private static RegistryObject<Item> item(String name) {
        return ITEMS.register(name, () -> new Item(new Item.Properties()));
    }
}
