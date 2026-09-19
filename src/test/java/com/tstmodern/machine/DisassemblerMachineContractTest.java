package com.tstmodern.machine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.machine.feature.IVoidable.VoidingMode;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

final class DisassemblerMachineContractTest {

    @BeforeAll
    static void initMinecraft() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void disassemblerDisablesOutputVoiding() {
        assertEquals(VoidingMode.VOID_NONE, DisassemblerMachine.VOIDING_MODE);
        assertFalse(DisassemblerMachine.VOIDING_MODE.canVoid(ItemRecipeCapability.CAP));
    }

    @Test
    void emptyCasingsValidationReturnsInvalidZeroTier() {
        DisassemblerMachine.CasingTierValidation validation = DisassemblerMachine.validateCasingTiers(List.of());
        assertEquals(0, validation.tier());
        assertFalse(validation.isValid());
        assertNull(validation.mismatchPosition());
        assertNull(validation.expectedBlock());
    }

    @Test
    void invalidInitialTierReturnsInvalidZeroTier() {
        Block dummyBlock = Blocks.IRON_BLOCK;
        List<DisassemblerMachine.CasingTier> casings = List.of(
                new DisassemblerMachine.CasingTier(BlockPos.ZERO, dummyBlock, 0));
        DisassemblerMachine.CasingTierValidation validation = DisassemblerMachine.validateCasingTiers(casings);
        assertEquals(0, validation.tier());
        assertFalse(validation.isValid());
    }

    @Test
    void exactlySixtySixUniformCasingsReturnsValidTier() {
        Block dummyBlock = Blocks.IRON_BLOCK;
        List<DisassemblerMachine.CasingTier> casings = new ArrayList<>();
        for (int i = 0; i < 66; i++) {
            casings.add(new DisassemblerMachine.CasingTier(new BlockPos(i, 0, 0), dummyBlock, 5));
        }

        DisassemblerMachine.CasingTierValidation validation = DisassemblerMachine.validateCasingTiers(casings);
        assertTrue(validation.isValid());
        assertEquals(5, validation.tier());
        assertNull(validation.mismatchPosition());
        assertNull(validation.expectedBlock());
    }

    @Test
    void lessThanSixtySixCasingsReturnsInvalidWithMismatchPosition() {
        Block dummyBlock = Blocks.IRON_BLOCK;
        List<DisassemblerMachine.CasingTier> casings = new ArrayList<>();
        for (int i = 0; i < 65; i++) {
            casings.add(new DisassemblerMachine.CasingTier(new BlockPos(i, 0, 0), dummyBlock, 3));
        }

        DisassemblerMachine.CasingTierValidation validation = DisassemblerMachine.validateCasingTiers(casings);
        assertFalse(validation.isValid());
        assertEquals(0, validation.tier());
        assertEquals(new BlockPos(0, 0, 0), validation.mismatchPosition());
        assertEquals(dummyBlock, validation.expectedBlock());
    }

    @Test
    void moreThanSixtySixCasingsReturnsInvalidWithOverflowPosition() {
        Block dummyBlock = Blocks.IRON_BLOCK;
        List<DisassemblerMachine.CasingTier> casings = new ArrayList<>();
        for (int i = 0; i < 67; i++) {
            casings.add(new DisassemblerMachine.CasingTier(new BlockPos(i, 0, 0), dummyBlock, 4));
        }

        DisassemblerMachine.CasingTierValidation validation = DisassemblerMachine.validateCasingTiers(casings);
        assertFalse(validation.isValid());
        assertEquals(0, validation.tier());
        assertEquals(new BlockPos(66, 0, 0), validation.mismatchPosition());
        assertEquals(dummyBlock, validation.expectedBlock());
    }

    @Test
    void mixedCasingTiersReturnsInvalidWithMismatchPosition() {
        Block blockTier1 = Blocks.IRON_BLOCK;
        Block blockTier2 = Blocks.GOLD_BLOCK;
        List<DisassemblerMachine.CasingTier> casings = new ArrayList<>();
        for (int i = 0; i < 66; i++) {
            if (i == 10) {
                casings.add(new DisassemblerMachine.CasingTier(new BlockPos(i, 0, 0), blockTier2, 2));
            } else {
                casings.add(new DisassemblerMachine.CasingTier(new BlockPos(i, 0, 0), blockTier1, 1));
            }
        }

        DisassemblerMachine.CasingTierValidation validation = DisassemblerMachine.validateCasingTiers(casings);
        assertFalse(validation.isValid());
        assertEquals(0, validation.tier());
        assertEquals(new BlockPos(10, 0, 0), validation.mismatchPosition());
        assertEquals(blockTier1, validation.expectedBlock());
    }
}
