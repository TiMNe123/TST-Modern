package com.tstmodern.machine.logic;

import com.gregtechceu.gtceu.api.item.MetaMachineItem;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Transactional machine loader and unloader for the Big Bro Array item buses.
 */
public final class BigBroArrayMachineTransfer {

    public record LoadResult(
            boolean success,
            BigBroArrayEmbeddedState state,
            String messageKey,
            Object... messageArgs
    ) {}

    public record UnloadResult(
            boolean success,
            BigBroArrayEmbeddedState state,
            String messageKey,
            Object... messageArgs
    ) {}

    private record ExtractionStep(int busIndex, int slotIndex, int count) {}
    private record InsertionStep(int busIndex, int slotIndex, ItemStack stack) {}

    private BigBroArrayMachineTransfer() {}

    /**
     * Attempts to find valid embeddable machines across all import buses and extract them.
     */
    public static LoadResult planAndExecuteLoad(
            List<? extends IItemHandlerModifiable> importBuses,
            int maxAllowedTier) {
        if (importBuses == null || importBuses.isEmpty()) {
            return new LoadResult(false, BigBroArrayEmbeddedState.EMPTY, "tstmodern.machine.big_bro_array.load.no_buses");
        }

        // 1. Find the first valid machine in deterministic order
        ResourceLocation targetId = null;
        BigBroArrayMachineCatalog.Entry targetCatalogEntry = null;
        CompoundTag targetTag = null;

        findFirst:
        for (IItemHandlerModifiable bus : importBuses) {
            for (int slot = 0; slot < bus.getSlots(); slot++) {
                ItemStack stack = bus.getStackInSlot(slot);
                if (!stack.isEmpty() && stack.getItem() instanceof MetaMachineItem machineItem) {
                    MachineDefinition def = machineItem.getDefinition();
                    if (def != null) {
                        Optional<BigBroArrayMachineCatalog.Entry> entry = BigBroArrayMachineCatalog.find(def.getId());
                        if (entry.isPresent()) {
                            targetId = def.getId();
                            targetCatalogEntry = entry.get();
                            targetTag = stack.getTag() != null ? stack.getTag().copy() : null;
                            break findFirst;
                        }
                    }
                }
            }
        }

        if (targetCatalogEntry == null) {
            return new LoadResult(false, BigBroArrayEmbeddedState.EMPTY, "tstmodern.machine.big_bro_array.load.no_valid_machine");
        }

        if (targetCatalogEntry.tier() > maxAllowedTier) {
            return new LoadResult(false, BigBroArrayEmbeddedState.EMPTY, "tstmodern.machine.big_bro_array.load.tier_too_high",
                    targetCatalogEntry.tier(), maxAllowedTier);
        }

        // 2. Scan and aggregate all matching machines across all buses
        List<ExtractionStep> steps = new ArrayList<>();
        int totalCount = 0;

        for (int b = 0; b < importBuses.size(); b++) {
            IItemHandlerModifiable bus = importBuses.get(b);
            for (int slot = 0; slot < bus.getSlots(); slot++) {
                ItemStack stack = bus.getStackInSlot(slot);
                if (!stack.isEmpty() && stack.getItem() instanceof MetaMachineItem machineItem) {
                    MachineDefinition def = machineItem.getDefinition();
                    if (def != null && def.getId().equals(targetId)) {
                        CompoundTag stackTag = stack.getTag();
                        if (Objects.equals(stackTag, targetTag)) {
                            int count = stack.getCount();
                            steps.add(new ExtractionStep(b, slot, count));
                            totalCount += count;
                        }
                    }
                }
            }
        }

        if (totalCount <= 0) {
            return new LoadResult(false, BigBroArrayEmbeddedState.EMPTY, "tstmodern.machine.big_bro_array.load.no_valid_machine");
        }

        // 3. Execute extractions atomically
        for (ExtractionStep step : steps) {
            IItemHandlerModifiable bus = importBuses.get(step.busIndex());
            bus.extractItem(step.slotIndex(), step.count(), false);
        }

        BigBroArrayEmbeddedState newState = new BigBroArrayEmbeddedState(
                BigBroArrayEmbeddedState.CURRENT_VERSION,
                targetId,
                targetCatalogEntry.mode(),
                targetCatalogEntry.tier(),
                totalCount,
                targetTag
        );

        return new LoadResult(true, newState, "tstmodern.machine.big_bro_array.load.success", totalCount);
    }

    /**
     * Attempts to unload the embedded machines into the export buses.
     */
    public static UnloadResult planAndExecuteUnload(
            BigBroArrayEmbeddedState currentState,
            List<? extends IItemHandlerModifiable> exportBuses,
            ItemStack machineStackTemplate) {
        if (currentState == null || currentState.isEmpty()) {
            return new UnloadResult(false, currentState, "tstmodern.machine.big_bro_array.unload.already_empty");
        }

        if (exportBuses == null || exportBuses.isEmpty()) {
            return new UnloadResult(false, currentState, "tstmodern.machine.big_bro_array.unload.no_buses");
        }

        int remainingToInsert = currentState.count();
        int maxStackSize = machineStackTemplate.isEmpty() ? 64 : machineStackTemplate.getMaxStackSize();

        // 1. Simulate insertion across all export buses
        List<InsertionStep> steps = new ArrayList<>();

        for (int b = 0; b < exportBuses.size() && remainingToInsert > 0; b++) {
            IItemHandlerModifiable bus = exportBuses.get(b);
            for (int slot = 0; slot < bus.getSlots() && remainingToInsert > 0; slot++) {
                int insertCount = Math.min(remainingToInsert, maxStackSize);
                ItemStack stackToInsert = machineStackTemplate.copy();
                stackToInsert.setCount(insertCount);
                if (currentState.itemTag() != null) {
                    stackToInsert.setTag(currentState.itemTag().copy());
                }

                ItemStack remainder = bus.insertItem(slot, stackToInsert, true);
                int accepted = insertCount - remainder.getCount();
                if (accepted > 0) {
                    ItemStack committedStack = stackToInsert.copy();
                    committedStack.setCount(accepted);
                    steps.add(new InsertionStep(b, slot, committedStack));
                    remainingToInsert -= accepted;
                }
            }
        }

        if (remainingToInsert > 0) {
            return new UnloadResult(false, currentState, "tstmodern.machine.big_bro_array.unload.no_space");
        }

        // 2. Execute insertions atomically
        for (InsertionStep step : steps) {
            IItemHandlerModifiable bus = exportBuses.get(step.busIndex());
            bus.insertItem(step.slotIndex(), step.stack(), false);
        }

        return new UnloadResult(true, BigBroArrayEmbeddedState.EMPTY, "tstmodern.machine.big_bro_array.unload.success", currentState.count());
    }
}
