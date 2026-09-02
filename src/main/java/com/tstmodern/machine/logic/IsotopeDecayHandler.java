package com.tstmodern.machine.logic;

import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.dust;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.tstmodern.config.TSTConfig;
import com.tstmodern.registry.TSTMaterials;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;

public final class IsotopeDecayHandler {
    private static final String DECAY_AGE = "tstmodernDecayAge";

    private IsotopeDecayHandler() {}

    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) return;

        Inventory inventory = event.player.getInventory();
        ItemStack neptunium = ChemicalHelper.get(dust, TSTMaterials.NEPTUNIUM_238);
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (!stack.is(neptunium.getItem())) continue;

            CompoundTag tag = stack.getOrCreateTag();
            int age = tag.getInt(DECAY_AGE) + 1;
            if (age < decayLifetimeTicks()) {
                tag.putInt(DECAY_AGE, age);
                continue;
            }

            inventory.setItem(slot, ChemicalHelper.get(dust, TSTMaterials.PLUTONIUM_238, stack.getCount()));
        }
    }

    static int decayLifetimeTicks() {
        return TSTConfig.NEPTUNIUM_238_DECAY_TICKS.get();
    }
}
