package com.tstmodern.registry;

import com.tstmodern.TSTModern;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class TSTCreativeModeTabs {
    private static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TSTModern.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MAIN = TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.tstmodern.main"))
            .icon(() -> new ItemStack(TSTBlocks.DENSE_CYCLOTRON_OUTER_CASING.get()))
            .displayItems((parameters, output) -> ForgeRegistries.ITEMS.getValues().stream()
                    .filter(item -> {
                        var key = ForgeRegistries.ITEMS.getKey(item);
                        return key != null && TSTModern.MOD_ID.equals(key.getNamespace());
                    })
                    .forEach(output::accept))
            .build());

    private TSTCreativeModeTabs() {}

    public static void register(IEventBus modBus) {
        TABS.register(modBus);
    }
}
