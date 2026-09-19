package com.tstmodern.machine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.tstmodern.machine.logic.StarcoreMinerLogic;
import com.tstmodern.registry.machine.StarcoreMinerDefinition;

import net.minecraft.resources.ResourceLocation;

final class StarcoreMinerMachineTest {
    private static final Set<PartAbility> ALLOWED_ABILITIES = Set.of(
            PartAbility.EXPORT_ITEMS,
            PartAbility.IMPORT_ITEMS,
            PartAbility.IMPORT_FLUIDS,
            PartAbility.INPUT_ENERGY,
            PartAbility.SUBSTATION_INPUT_ENERGY,
            PartAbility.INPUT_LASER
    );

    @Test
    void acceptsMiningAbilitiesAndRejectsOutputsOtherThanItems() {
        for (PartAbility ability : ALLOWED_ABILITIES) {
            assertTrue(StarcoreMinerMachine.isAllowedAbility(ability), "Should allow " + ability);
        }
        assertFalse(StarcoreMinerMachine.isAllowedAbility(PartAbility.EXPORT_FLUIDS));
        assertFalse(StarcoreMinerMachine.isAllowedAbility(PartAbility.OUTPUT_ENERGY));
        assertFalse(StarcoreMinerMachine.isAllowedAbility(PartAbility.OUTPUT_LASER));
        assertFalse(StarcoreMinerMachine.isAllowedAbility(PartAbility.SUBSTATION_OUTPUT_ENERGY));
    }

    @Test
    void reservesFutureAstralArrayFabricatorId() {
        assertEquals(new ResourceLocation("tstmodern", "astral_array_fabricator"),
                StarcoreMinerMachine.ASTRAL_ARRAY_FABRICATOR_ID);
    }

    @Test
    void computesPipeRequirementFromControllerY() {
        assertEquals(0, StarcoreMinerMachine.requiredPipePieces(24));
        assertEquals(0, StarcoreMinerMachine.requiredPipePieces(10));
        assertEquals(4, StarcoreMinerMachine.requiredPipePieces(28));
    }

    @Test
    void computesPipeSliceCenterOffsets() {
        int controllerY = 28;
        for (int p = 0; p < 4; p++) {
            int sliceWorldY = controllerY - 4 - p;
            assertEquals(24 - p, sliceWorldY);
            assertEquals(0, StarcoreMinerMachine.pipeLocalRight());
            assertEquals(19, StarcoreMinerMachine.pipeLocalBack());
        }
    }

    @Test
    void usesControllerOrientationForPipeCoordinates() throws IOException {
        String source = Files.readString(Path.of(
                "src/main/java/com/tstmodern/machine/StarcoreMinerMachine.java"));
        assertTrue(source.contains(
                "RelativeDirection.RIGHT.getRelative(front, getUpwardsFacing(), isFlipped())"));
        assertTrue(source.contains(
                "RelativeDirection.BACK.getRelative(front, getUpwardsFacing(), isFlipped())"));
        assertFalse(source.contains("getRelative(front, Direction.UP, false)"));
    }

    @Test
    void verifiesBoostCalculation() {
        assertEquals(131_072, StarcoreMinerLogic.boostedStackSize(0));
        assertEquals(131_072 * 2 * 8, StarcoreMinerLogic.boostedStackSize(4));
    }

    @Test
    void exposesAndFiltersTheControllerBoosterSlot() throws IOException {
        String source = Files.readString(Path.of(
                "src/main/java/com/tstmodern/machine/StarcoreMinerMachine.java"));
        assertTrue(source.contains("new LabelWidget(4, 5,"));
        assertTrue(source.contains("() -> self().getBlockState().getBlock().getName().getString()"));
        assertTrue(source.contains("new SlotWidget(boosterInventory, 0"));
        assertTrue(source.contains(".setFilter(stack ->"));
        assertTrue(source.contains("ASTRAL_ARRAY_FABRICATOR_ID.equals(ForgeRegistries.ITEMS.getKey"));
    }
}
