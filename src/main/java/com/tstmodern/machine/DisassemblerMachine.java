package com.tstmodern.machine;

import java.util.Comparator;
import java.util.List;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.IVoidable.VoidingMode;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.error.PatternError;
import com.tstmodern.registry.TSTBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

/**
 * Formation-time casing-tier validation for the Large Disassembler.
 */
public final class DisassemblerMachine extends WorkableMultiblockMachine {
    private static final int REQUIRED_TIER_CASING_COUNT = 66;
    static final VoidingMode VOIDING_MODE = VoidingMode.VOID_ITEMS_FLUIDS;

    private int casingTier;

    public DisassemblerMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();

        List<CasingTier> tierCasings = getMultiblockState().getCache().stream()
                .sorted(Comparator.comparingLong(BlockPos::asLong))
                .map(position -> {
                    Block block = getMultiblockState().getWorld().getBlockState(position).getBlock();
                    return new CasingTier(position, block, TSTBlocks.componentAssemblyLineTier(block));
                })
                .filter(casing -> casing.tier() != 0)
                .toList();
        CasingTierValidation validation = validateCasingTiers(tierCasings);
        casingTier = validation.tier();
        if (!validation.isValid()) {
            getMultiblockState().setError(new CasingTierMismatchError(
                    validation.mismatchPosition() == null ? getPos() : validation.mismatchPosition(),
                    validation.expectedBlock()));
            onStructureInvalid();
        }
    }

    @Override
    public void onStructureInvalid() {
        casingTier = 0;
        super.onStructureInvalid();
    }

    @Override
    public VoidingMode getVoidingMode() {
        return VOIDING_MODE;
    }

    /** Returns the 1-based LV-through-MAX casing tier, or zero while unformed. */
    public int getCasingTier() {
        return casingTier;
    }

    static CasingTierValidation validateCasingTiers(List<CasingTier> casings) {
        if (casings.isEmpty() || casings.get(0).tier() < 1 || casings.get(0).tier() > 14) {
            return new CasingTierValidation(0, null, null);
        }

        CasingTier expected = casings.get(0);
        if (casings.size() < REQUIRED_TIER_CASING_COUNT) {
            return new CasingTierValidation(0, expected.position(), expected.block());
        }
        if (casings.size() > REQUIRED_TIER_CASING_COUNT) {
            return new CasingTierValidation(0, casings.get(REQUIRED_TIER_CASING_COUNT).position(), expected.block());
        }
        for (int index = 1; index < casings.size(); index++) {
            CasingTier casing = casings.get(index);
            if (casing.tier() != expected.tier()) {
                return new CasingTierValidation(0, casing.position(), expected.block());
            }
        }
        return new CasingTierValidation(expected.tier(), null, null);
    }

    record CasingTier(BlockPos position, Block block, int tier) {}

    record CasingTierValidation(int tier, BlockPos mismatchPosition, Block expectedBlock) {
        boolean isValid() {
            return tier != 0;
        }
    }

    private static final class CasingTierMismatchError extends PatternError {
        private final BlockPos mismatchPosition;
        private final Block expectedBlock;

        private CasingTierMismatchError(BlockPos mismatchPosition, Block expectedBlock) {
            this.mismatchPosition = mismatchPosition;
            this.expectedBlock = expectedBlock;
        }

        @Override
        public BlockPos getPos() {
            return mismatchPosition;
        }

        @Override
        public List<List<ItemStack>> getCandidates() {
            return expectedBlock == null ? List.of() : List.of(List.of(new ItemStack(expectedBlock)));
        }

        @Override
        public Component getErrorInfo() {
            return Component.translatable("tstmodern.machine.disassembler.error.mixed_casing");
        }
    }
}
