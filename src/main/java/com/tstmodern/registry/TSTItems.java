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


    private TSTItems() {}

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
    }

    private static RegistryObject<Item> item(String name) {
        return ITEMS.register(name, () -> new Item(new Item.Properties()));
    }
}
