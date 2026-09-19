package com.tstmodern.machine.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import com.gregtechceu.gtceu.api.GTValues;

import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.items.ItemStackHandler;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class BigBroArrayMachineTransferTest {

    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void loadFromEmptyBusesReturnsFailure() {
        ItemStackHandler bus1 = new ItemStackHandler(4);
        BigBroArrayMachineTransfer.LoadResult result = BigBroArrayMachineTransfer.planAndExecuteLoad(
                List.of(bus1), GTValues.MAX);

        assertFalse(result.success());
        assertTrue(result.state().isEmpty());
    }

    @Test
    void loadFromNullBusListReturnsFailure() {
        BigBroArrayMachineTransfer.LoadResult result = BigBroArrayMachineTransfer.planAndExecuteLoad(
                null, GTValues.MAX);
        assertFalse(result.success());
    }

    @Test
    void loadFromEmptyBusListReturnsFailure() {
        BigBroArrayMachineTransfer.LoadResult result = BigBroArrayMachineTransfer.planAndExecuteLoad(
                List.of(), GTValues.MAX);
        assertFalse(result.success());
    }

    @Test
    void unloadToFullBusesAbortsWithoutModifyingState() {
        ItemStackHandler bus1 = new ItemStackHandler(1);
        bus1.setStackInSlot(0, new ItemStack(Items.DIRT, 64));

        BigBroArrayEmbeddedState state = new BigBroArrayEmbeddedState(
                1,
                new ResourceLocation("gtceu", "lv_macerator"),
                BigBroArrayMode.PROCESSOR,
                GTValues.LV,
                16,
                null
        );

        ItemStack template = new ItemStack(Items.STONE, 1);
        BigBroArrayMachineTransfer.UnloadResult result = BigBroArrayMachineTransfer.planAndExecuteUnload(
                state, List.of(bus1), template);

        assertFalse(result.success());
        assertEquals(state, result.state());
        // Bus should not be modified
        assertEquals(64, bus1.getStackInSlot(0).getCount());
    }

    @Test
    void unloadSucceedsAndDistributesAcrossSlots() {
        ItemStackHandler bus1 = new ItemStackHandler(2);

        BigBroArrayEmbeddedState state = new BigBroArrayEmbeddedState(
                1,
                new ResourceLocation("gtceu", "lv_macerator"),
                BigBroArrayMode.PROCESSOR,
                GTValues.LV,
                80,
                null
        );

        ItemStack template = new ItemStack(Items.STONE, 1);
        BigBroArrayMachineTransfer.UnloadResult result = BigBroArrayMachineTransfer.planAndExecuteUnload(
                state, List.of(bus1), template);

        assertTrue(result.success());
        assertTrue(result.state().isEmpty());
        assertEquals(64, bus1.getStackInSlot(0).getCount());
        assertEquals(16, bus1.getStackInSlot(1).getCount());
    }

    @Test
    void unloadEmptyStateReturnsFailure() {
        ItemStackHandler bus1 = new ItemStackHandler(4);
        BigBroArrayMachineTransfer.UnloadResult result = BigBroArrayMachineTransfer.planAndExecuteUnload(
                BigBroArrayEmbeddedState.EMPTY, List.of(bus1), new ItemStack(Items.STONE));

        assertFalse(result.success());
    }

    @Test
    void unloadNullBusesReturnsFailure() {
        BigBroArrayEmbeddedState state = new BigBroArrayEmbeddedState(
                1, new ResourceLocation("gtceu", "lv_macerator"),
                BigBroArrayMode.PROCESSOR, GTValues.LV, 16, null);

        BigBroArrayMachineTransfer.UnloadResult result = BigBroArrayMachineTransfer.planAndExecuteUnload(
                state, null, new ItemStack(Items.STONE));
        assertFalse(result.success());
    }

    @Test
    void unloadRejectsEmptyMachineTemplateWithoutTouchingBus() {
        ItemStackHandler bus = new ItemStackHandler(1);
        BigBroArrayEmbeddedState state = processorState("gtceu:lv_macerator", 8, null);

        BigBroArrayMachineTransfer.UnloadResult result = BigBroArrayMachineTransfer.planAndExecuteUnload(
                state, List.of(bus), ItemStack.EMPTY);

        assertFalse(result.success());
        assertEquals(state, result.state());
        assertTrue(bus.getStackInSlot(0).isEmpty());
    }

    @Test
    void nbtCopiedNotSharedBetweenUnloadedStacks() {
        ItemStackHandler bus1 = new ItemStackHandler(2);

        CompoundTag tag = new CompoundTag();
        tag.putString("test", "value");

        BigBroArrayEmbeddedState state = new BigBroArrayEmbeddedState(
                1,
                new ResourceLocation("gtceu", "lv_macerator"),
                BigBroArrayMode.PROCESSOR,
                GTValues.LV,
                80,
                tag
        );

        ItemStack template = new ItemStack(Items.STONE, 1);
        BigBroArrayMachineTransfer.UnloadResult result = BigBroArrayMachineTransfer.planAndExecuteUnload(
                state, List.of(bus1), template);

        assertTrue(result.success());

        // Verify NBT is present on both stacks and is independent (not same reference)
        ItemStack slot0 = bus1.getStackInSlot(0);
        ItemStack slot1 = bus1.getStackInSlot(1);
        assertEquals("value", slot0.getTag().getString("test"));
        assertEquals("value", slot1.getTag().getString("test"));

        // Mutate one; the other should be unaffected
        slot0.getTag().putString("test", "mutated");
        assertEquals("value", slot1.getTag().getString("test"));
    }

    @Test
    void unloadMultipleBusesDistributesCorrectly() {
        ItemStackHandler bus1 = new ItemStackHandler(1);
        ItemStackHandler bus2 = new ItemStackHandler(1);

        BigBroArrayEmbeddedState state = new BigBroArrayEmbeddedState(
                1,
                new ResourceLocation("gtceu", "lv_macerator"),
                BigBroArrayMode.PROCESSOR,
                GTValues.LV,
                100,
                null
        );

        ItemStack template = new ItemStack(Items.STONE, 1);
        BigBroArrayMachineTransfer.UnloadResult result = BigBroArrayMachineTransfer.planAndExecuteUnload(
                state, List.of(bus1, bus2), template);

        assertTrue(result.success());
        assertEquals(64, bus1.getStackInSlot(0).getCount());
        assertEquals(36, bus2.getStackInSlot(0).getCount());
    }

    @Test
    void unloadTransactionMismatchRollsBackCompletely() {
        FailingInsertHandler bus = new FailingInsertHandler(2);
        bus.setStackInSlotDirect(0, ItemStack.EMPTY);
        bus.setStackInSlotDirect(1, ItemStack.EMPTY);

        BigBroArrayEmbeddedState state = new BigBroArrayEmbeddedState(
                1,
                new ResourceLocation("gtceu", "lv_macerator"),
                BigBroArrayMode.PROCESSOR,
                GTValues.LV,
                10,
                null
        );

        ItemStack template = new ItemStack(Items.STONE, 1);
        BigBroArrayMachineTransfer.UnloadResult result = BigBroArrayMachineTransfer.planAndExecuteUnload(
                state, List.of(bus), template);

        assertFalse(result.success());
        assertEquals(state, result.state());
        // Verify slots remained/rolled back to empty
        assertTrue(bus.getStackInSlot(0).isEmpty());
        assertTrue(bus.getStackInSlot(1).isEmpty());
    }

    @Test
    void unloadWrongNbtCommitRollsBackCompletely() {
        WrongNbtInsertHandler bus = new WrongNbtInsertHandler(1);
        CompoundTag expectedTag = new CompoundTag();
        expectedTag.putString("owner", "player");
        BigBroArrayEmbeddedState state = processorState("gtceu:lv_macerator", 4, expectedTag);
        ItemStack template = new ItemStack(Items.STONE);

        BigBroArrayMachineTransfer.UnloadResult result = BigBroArrayMachineTransfer.planAndExecuteUnload(
                state, List.of(bus), template);

        assertFalse(result.success());
        assertEquals(state, result.state());
        assertTrue(bus.getStackInSlot(0).isEmpty());
    }

    private static BigBroArrayEmbeddedState processorState(String id, int count, CompoundTag tag) {
        ResourceLocation definitionId = new ResourceLocation(id);
        return new BigBroArrayEmbeddedState(
                1, definitionId, BigBroArrayMode.PROCESSOR, GTValues.LV, count, tag);
    }

    private static class FailingInsertHandler extends ItemStackHandler {
        FailingInsertHandler(int size) {
            super(size);
        }

        void setStackInSlotDirect(int slot, ItemStack stack) {
            super.setStackInSlot(slot, stack);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (simulate) {
                // Simulate success
                return ItemStack.EMPTY;
            }
            // Commit fails by rejecting insertion
            return stack;
        }
    }

    private static class WrongNbtInsertHandler extends ItemStackHandler {
        WrongNbtInsertHandler(int size) {
            super(size);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (simulate) {
                return ItemStack.EMPTY;
            }
            ItemStack corrupted = stack.copy();
            CompoundTag wrongTag = new CompoundTag();
            wrongTag.putString("owner", "someone_else");
            corrupted.setTag(wrongTag);
            super.setStackInSlot(slot, corrupted);
            return ItemStack.EMPTY;
        }
    }
}
