package com.tstmodern.machine.logic;

import com.gregtechceu.gtceu.api.item.MetaMachineItem;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Transactional machine loader and unloader for the Big Bro Array item buses.
 * All operations are two-phase: simulate first, then commit only if simulation
 * succeeds completely. On commit mismatch, snapshots are restored.
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
    private record SlotSnapshot(int busIndex, int slotIndex, ItemStack savedStack) {}

    private BigBroArrayMachineTransfer() {}

    /**
     * Attempts to find valid embeddable machines across all import buses and extract them.
     * Two-phase: simulate extraction, take snapshot, execute, verify commit consistency.
     */
    public static LoadResult planAndExecuteLoad(
            List<? extends IItemHandlerModifiable> importBuses,
            int maxAllowedTier) {
        if (importBuses == null || importBuses.isEmpty()) {
            return new LoadResult(false, BigBroArrayEmbeddedState.EMPTY, "tstmodern.machine.big_bro_array.status.no_input_bus");
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
            return new LoadResult(false, BigBroArrayEmbeddedState.EMPTY, "tstmodern.machine.big_bro_array.status.empty_bus");
        }

        if (targetCatalogEntry.tier() > maxAllowedTier) {
            return new LoadResult(false, BigBroArrayEmbeddedState.EMPTY, "tstmodern.machine.big_bro_array.status.tier_exceeded",
                    targetCatalogEntry.tier(), maxAllowedTier);
        }

        // 2. Scan and aggregate all matching machines across all buses
        List<ExtractionStep> steps = new ArrayList<>();
        long totalCountLong = 0;

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
                            totalCountLong += count;
                            // Overflow protection
                            if (totalCountLong > Integer.MAX_VALUE) {
                                return new LoadResult(false, BigBroArrayEmbeddedState.EMPTY,
                                        "tstmodern.machine.big_bro_array.status.overflow");
                            }
                        }
                    }
                }
            }
        }

        int totalCount = (int) totalCountLong;
        if (totalCount <= 0) {
            return new LoadResult(false, BigBroArrayEmbeddedState.EMPTY, "tstmodern.machine.big_bro_array.status.empty_bus");
        }

        // 3. Simulate every extraction before touching inventory.
        for (ExtractionStep step : steps) {
            IItemHandlerModifiable bus = importBuses.get(step.busIndex());
            ItemStack expected = bus.getStackInSlot(step.slotIndex()).copy();
            expected.setCount(step.count());
            ItemStack simulated = bus.extractItem(step.slotIndex(), step.count(), true);
            if (!sameStackAndCount(expected, simulated)) {
                return new LoadResult(false, BigBroArrayEmbeddedState.EMPTY,
                        "tstmodern.machine.big_bro_array.status.transaction_mismatch");
            }
        }

        // 4. Take snapshots before commit
        List<SlotSnapshot> snapshots = new ArrayList<>(steps.size());
        for (ExtractionStep step : steps) {
            IItemHandlerModifiable bus = importBuses.get(step.busIndex());
            snapshots.add(new SlotSnapshot(step.busIndex(), step.slotIndex(),
                    bus.getStackInSlot(step.slotIndex()).copy()));
        }

        // 5. Execute and verify exact extracted item/count/NBT.
        boolean commitValid = true;
        for (int i = 0; i < steps.size(); i++) {
            ExtractionStep step = steps.get(i);
            SlotSnapshot snapshot = snapshots.get(i);
            IItemHandlerModifiable bus = importBuses.get(step.busIndex());
            ItemStack expectedExtracted = snapshot.savedStack().copy();
            expectedExtracted.setCount(step.count());
            ItemStack extracted = bus.extractItem(step.slotIndex(), step.count(), false);
            if (!sameStackAndCount(expectedExtracted, extracted)) {
                commitValid = false;
                break;
            }
        }

        // 6. Verify the exact post-commit contents, not only the remaining count.
        for (int i = 0; commitValid && i < steps.size(); i++) {
            ExtractionStep step = steps.get(i);
            SlotSnapshot snapshot = snapshots.get(i);
            IItemHandlerModifiable bus = importBuses.get(step.busIndex());
            ItemStack postCommit = bus.getStackInSlot(step.slotIndex());
            ItemStack expectedRemaining = snapshot.savedStack().copy();
            expectedRemaining.shrink(step.count());
            if (!sameStackAndCount(expectedRemaining, postCommit)) {
                commitValid = false;
                break;
            }
        }

        if (!commitValid) {
            // Rollback: restore all snapshotted slots
            for (SlotSnapshot snapshot : snapshots) {
                IItemHandlerModifiable bus = importBuses.get(snapshot.busIndex());
                bus.setStackInSlot(snapshot.slotIndex(), snapshot.savedStack());
            }
            return new LoadResult(false, BigBroArrayEmbeddedState.EMPTY,
                    "tstmodern.machine.big_bro_array.status.transaction_mismatch");
        }

        BigBroArrayEmbeddedState newState = new BigBroArrayEmbeddedState(
                BigBroArrayEmbeddedState.CURRENT_VERSION,
                targetId,
                targetCatalogEntry.mode(),
                targetCatalogEntry.tier(),
                totalCount,
                targetTag
        );

        Component machineName = targetCatalogEntry.definition().asStack().getHoverName();
        return new LoadResult(true, newState, "tstmodern.machine.big_bro_array.status.loaded", totalCount, machineName);
    }

    /**
     * Attempts to unload the embedded machines into the export buses.
     * Two-phase: simulate insertion, take snapshot, execute, verify commit consistency.
     */
    public static UnloadResult planAndExecuteUnload(
            BigBroArrayEmbeddedState currentState,
            List<? extends IItemHandlerModifiable> exportBuses,
            ItemStack machineStackTemplate) {
        if (currentState == null || currentState.isEmpty()) {
            return new UnloadResult(false, currentState, "tstmodern.machine.big_bro_array.status.no_machine");
        }

        if (exportBuses == null || exportBuses.isEmpty()) {
            return new UnloadResult(false, currentState, "tstmodern.machine.big_bro_array.status.no_export_bus");
        }

        if (machineStackTemplate == null || machineStackTemplate.isEmpty()) {
            return new UnloadResult(false, currentState,
                    "tstmodern.machine.big_bro_array.status.empty_template");
        }

        int remainingToInsert = currentState.count();
        int maxStackSize = machineStackTemplate.getMaxStackSize();

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
            return new UnloadResult(false, currentState, "tstmodern.machine.big_bro_array.status.export_bus_full", currentState.count());
        }

        // 2. Take snapshots before commit
        List<SlotSnapshot> snapshots = new ArrayList<>(steps.size());
        for (InsertionStep step : steps) {
            IItemHandlerModifiable bus = exportBuses.get(step.busIndex());
            snapshots.add(new SlotSnapshot(step.busIndex(), step.slotIndex(),
                    bus.getStackInSlot(step.slotIndex()).copy()));
        }

        // 3. Execute insertions and verify exact post-commit item/count/NBT.
        boolean commitValid = true;
        for (int i = 0; i < steps.size(); i++) {
            InsertionStep step = steps.get(i);
            SlotSnapshot snapshot = snapshots.get(i);
            IItemHandlerModifiable bus = exportBuses.get(step.busIndex());
            ItemStack remainder = bus.insertItem(step.slotIndex(), step.stack(), false);
            if (!remainder.isEmpty()) {
                commitValid = false;
                break;
            }

            ItemStack expected = snapshot.savedStack().copy();
            if (expected.isEmpty()) {
                expected = step.stack().copy();
            } else if (ItemStack.isSameItemSameTags(expected, step.stack())) {
                expected.grow(step.stack().getCount());
            } else {
                commitValid = false;
                break;
            }
            if (!sameStackAndCount(expected, bus.getStackInSlot(step.slotIndex()))) {
                commitValid = false;
                break;
            }
        }

        if (!commitValid) {
            for (SlotSnapshot snapshot : snapshots) {
                IItemHandlerModifiable snapshotBus = exportBuses.get(snapshot.busIndex());
                snapshotBus.setStackInSlot(snapshot.slotIndex(), snapshot.savedStack());
            }
            return new UnloadResult(false, currentState,
                    "tstmodern.machine.big_bro_array.status.transaction_mismatch", currentState.count());
        }

        Component machineName = machineStackTemplate.getHoverName();

        return new UnloadResult(true, BigBroArrayEmbeddedState.EMPTY,
                "tstmodern.machine.big_bro_array.status.unloaded", currentState.count(), machineName);
    }

    private static boolean sameStackAndCount(ItemStack expected, ItemStack actual) {
        if (expected.isEmpty() || actual.isEmpty()) {
            return expected.isEmpty() && actual.isEmpty();
        }
        return expected.getCount() == actual.getCount() && ItemStack.isSameItemSameTags(expected, actual);
    }
}
