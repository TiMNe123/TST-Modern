package com.tstmodern.machine;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleSupplier;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.widget.SlotWidget;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.IMachineLife;
import com.gregtechceu.gtceu.api.machine.multiblock.part.MultiblockPartMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredPartMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.common.data.machines.GTResearchMachines;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.tstmodern.data.recipe.TSTCircuitTags;
import com.tstmodern.machine.logic.AstralComputingLogic;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.tags.TagKey;

import org.jetbrains.annotations.NotNull;

public final class AstralComputationRackMachine extends TieredPartMachine implements IMachineLife {
    private static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            AstralComputationRackMachine.class, MultiblockPartMachine.MANAGED_FIELD_HOLDER);

    private static final AstralComputingLogic.Component[] CIRCUITS = {
            new AstralComputingLogic.Component(4, 8, 0, 1000, true),
            new AstralComputingLogic.Component(6, 9, -0.05, 1500, true),
            new AstralComputingLogic.Component(8, 10, -0.1, 2500, true),
            new AstralComputingLogic.Component(9, 1, -0.1, 3000, true),
            new AstralComputingLogic.Component(12, 10, -0.15, 3500, true),
            new AstralComputingLogic.Component(16, 12, -0.2, 5000, true),
            new AstralComputingLogic.Component(22, 14, -0.3, 5200, true),
            new AstralComputingLogic.Component(30, 18, -0.35, 5500, true),
            new AstralComputingLogic.Component(38, 25, -0.4, 6000, true),
            new AstralComputingLogic.Component(44, 28, -0.4, 6000, true)
    };
    private static final AstralComputingLogic.Component HEAT_SINK =
            new AstralComputingLogic.Component(0, -40, 0, 5000, false);
    private static final AstralComputingLogic.Component ACTIVE_COOLER =
            new AstralComputingLogic.Component(0, -80, 0, 10_000, false);

    @Persisted
    public final NotifiableItemStackHandler inventory;
    @Persisted
    @DescSynced
    private int heat;

    public AstralComputationRackMachine(IMachineBlockEntity holder) {
        super(holder, GTValues.UEV);
        this.inventory = new NotifiableItemStackHandler(this, 64, IO.NONE, IO.BOTH) {
            @Override
            public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                return canEdit() && component(stack) != null ? super.insertItem(slot, stack, simulate) : stack;
            }

            @Override
            public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
                return canEdit() ? super.extractItem(slot, amount, simulate) : ItemStack.EMPTY;
            }
        };
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    public int getHeat() {
        return heat;
    }

    public int tick(double overclock, double overvolt, DoubleSupplier random) {
        var result = AstralComputingLogic.tick(stacks(), heat, overclock, overvolt, random);
        heat = result.heat();
        return result.computation();
    }

    public void setHeat(int heat) {
        this.heat = Math.max(-10_000, heat);
    }

    public void passiveCool() {
        heat = AstralComputingLogic.passiveCool(stacks(), heat);
    }

    public void destroyOverheatedComponents() {
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            var component = component(stack);
            if (component != null && heat > component.maxHeat() * 64 * 64) {
                inventory.setStackInSlot(slot, ItemStack.EMPTY);
            }
        }
    }

    private List<AstralComputingLogic.Stack> stacks() {
        List<AstralComputingLogic.Stack> stacks = new ArrayList<>();
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            var component = component(stack);
            if (component != null && !stack.isEmpty()) {
                stacks.add(new AstralComputingLogic.Stack(component, stack.getCount()));
            }
        }
        return stacks;
    }

    private static AstralComputingLogic.Component component(ItemStack stack) {
        if (stack.isEmpty()) return null;
        for (int tier = GTValues.LV; tier <= GTValues.UEV; tier++) {
            if (stack.is(circuitTag(tier))) return CIRCUITS[tier - GTValues.LV];
        }
        if (stack.is(GTResearchMachines.HPCA_HEAT_SINK_COMPONENT.asStack().getItem())) return HEAT_SINK;
        if (stack.is(GTResearchMachines.HPCA_ACTIVE_COOLER_COMPONENT.asStack().getItem())) return ACTIVE_COOLER;
        return null;
    }

    private static TagKey<Item> circuitTag(int tier) {
        return switch (tier) {
            case GTValues.LV -> CustomTags.LV_CIRCUITS;
            case GTValues.MV -> CustomTags.MV_CIRCUITS;
            case GTValues.HV -> CustomTags.HV_CIRCUITS;
            case GTValues.EV -> CustomTags.EV_CIRCUITS;
            case GTValues.IV -> CustomTags.IV_CIRCUITS;
            case GTValues.LuV -> CustomTags.LuV_CIRCUITS;
            case GTValues.ZPM -> CustomTags.ZPM_CIRCUITS;
            case GTValues.UV -> CustomTags.UV_CIRCUITS;
            case GTValues.UHV -> CustomTags.UHV_CIRCUITS;
            default -> TSTCircuitTags.get(tier);
        };
    }

    private boolean canEdit() {
        return getControllers().isEmpty() ||
                !(getControllers().first() instanceof AstralComputingArrayMachine controller) ||
                (!controller.isComputing() && heat <= 500);
    }

    @Override
    public Widget createUIWidget() {
        WidgetGroup group = new WidgetGroup(0, 0, 144, 144);
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                group.addWidget(new SlotWidget(inventory, y * 8 + x, x * 18, y * 18, true, true)
                        .setBackgroundTexture(GuiTextures.SLOT));
            }
        }
        return group;
    }

    @Override
    public void onMachineRemoved() {
        clearInventory(inventory.storage);
    }
}
