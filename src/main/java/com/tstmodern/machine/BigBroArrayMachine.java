package com.tstmodern.machine;

import java.util.List;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.block.ICoilType;
import com.gregtechceu.gtceu.api.item.MetaMachineItem;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IDisplayUIMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockDisplayText;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.tstmodern.machine.logic.BigBroArrayLogic;
import com.tstmodern.machine.logic.BigBroArrayTierRules;
import com.tstmodern.machine.logic.BigBroArrayTierRules.CoreTiers;

import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Runtime execution engine for the Mega Array (BigBroArray).
 * Manages embedded single-block machine state, dynamic recipe overclocking, and parallel scaling.
 */
public final class BigBroArrayMachine extends WorkableMultiblockMachine implements IDisplayUIMachine {
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER =
            new ManagedFieldHolder(BigBroArrayMachine.class,
                    WorkableMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    @DescSynced
    private ItemStack embeddedMachineStack = ItemStack.EMPTY;

    @Persisted
    @DescSynced
    private int embeddedCount = 0;

    @Persisted
    @DescSynced
    private int embeddedTier = 0;

    @Persisted
    @DescSynced
    private int parallelCasingTier = 0;

    @Persisted
    @DescSynced
    private int coilTier = 0;

    @Persisted
    @DescSynced
    private boolean hasAddon = false;

    /** One immutable snapshot published only after all core tier channels validate. */
    private CoreTiers coreTiers;

    public BigBroArrayMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    public ItemStack getEmbeddedMachineStack() {
        return embeddedMachineStack;
    }

    public int getEmbeddedCount() {
        return embeddedCount;
    }

    public int getEmbeddedTier() {
        return embeddedTier;
    }

    public CoreTiers getCoreTiers() {
        return coreTiers;
    }

    public int getFrameTier() {
        return coreTiers == null ? 0 : coreTiers.frameTier();
    }

    public int getGlassTier() {
        return coreTiers == null ? 0 : coreTiers.glassTier();
    }

    public int getMachineCasingTier() {
        return coreTiers == null ? 0 : coreTiers.machineCasingTier();
    }

    public int getMaxEmbeddedTier() {
        return BigBroArrayTierRules.maxEmbeddedTier(getFrameTier());
    }

    public int getParallelCasingTier() {
        return parallelCasingTier;
    }

    public int getCoilTier() {
        return coilTier;
    }

    public boolean hasAddon() {
        return hasAddon;
    }

    public long getActualParallel() {
        return BigBroArrayLogic.calculateParallelism(embeddedCount, parallelCasingTier, getAddonCount());
    }

    /** Number of attached addon structures (0 for core-only builds). */
    public int getAddonCount() {
        return hasAddon ? 1 : 0;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();

        var state = getMultiblockState();
        CoreTiers validatedCoreTiers = state == null ? null :
                BigBroArrayTierRules.validatedCoreTiers(state.getMatchContext());
        if (validatedCoreTiers == null) {
            clearStructureDerivedState();
            return;
        }

        int formedCoilTier = 0;
        Object coilType = state.getMatchContext().get("CoilType");
        if (coilType instanceof ICoilType coil) {
            formedCoilTier = coil.getTier();
        }

        int formedParallelTier = 0;
        boolean formedAddon = false;
        if (state.getCache() != null) {
            for (BlockPos pos : state.getCache()) {
                int candidateTier = BigBroArrayTierRules.parallelCasingTier(
                        state.getWorld().getBlockState(pos).getBlock());
                if (candidateTier > 0) {
                    formedParallelTier = Math.max(formedParallelTier, candidateTier);
                    formedAddon = true;
                }
            }
        }

        this.coilTier = formedCoilTier;
        this.parallelCasingTier = formedParallelTier;
        this.hasAddon = formedAddon;
        this.coreTiers = validatedCoreTiers;
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        clearStructureDerivedState();
    }

    private void clearStructureDerivedState() {
        this.coreTiers = null;
        this.parallelCasingTier = 0;
        this.coilTier = 0;
        this.hasAddon = false;
    }

    @Override
    protected InteractionResult onScrewdriverClick(Player playerIn, InteractionHand hand, Direction gridSide,
                                                   BlockHitResult hitResult) {
        if (!isFormed() || isRemote()) {
            return InteractionResult.SUCCESS;
        }

        if (playerIn == null) {
            return InteractionResult.PASS;
        }

        // If machine already embedded, unload it back to player or drop it.
        if (!embeddedMachineStack.isEmpty() && embeddedCount > 0) {
            ItemStack returnStack = embeddedMachineStack.copy();
            returnStack.setCount(embeddedCount);
            if (!playerIn.getInventory().add(returnStack)) {
                playerIn.drop(returnStack, false);
            }
            playerIn.sendSystemMessage(Component.translatable("tstmodern.machine.big_bro_array.status.no_machine")
                    .withStyle(ChatFormatting.YELLOW));
            this.embeddedMachineStack = ItemStack.EMPTY;
            this.embeddedCount = 0;
            this.embeddedTier = 0;
            return InteractionResult.CONSUME;
        }

        // Otherwise scan player offhand for a single-block machine unlocked by the frame tier.
        ItemStack offhand = playerIn.getOffhandItem();
        int candidateTier = extractTier(offhand);
        if (isValidEmbeddableMachine(offhand) &&
                BigBroArrayTierRules.isEmbeddedTierEligible(getFrameTier(), candidateTier)) {
            this.embeddedMachineStack = offhand.copy();
            this.embeddedMachineStack.setCount(1);
            this.embeddedCount = offhand.getCount();
            this.embeddedTier = candidateTier;
            offhand.setCount(0);
            playerIn.sendSystemMessage(Component.translatable("tstmodern.machine.big_bro_array.status.embedded",
                    embeddedCount, embeddedMachineStack.getHoverName(), GTValues.VN[embeddedTier])
                    .withStyle(ChatFormatting.GREEN));
            return InteractionResult.CONSUME;
        }

        return super.onScrewdriverClick(playerIn, hand, gridSide, hitResult);
    }

    private static boolean isValidEmbeddableMachine(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof MetaMachineItem machineItem)) {
            return false;
        }
        MachineDefinition definition = machineItem.getDefinition();
        return definition != null && definition.getRecipeTypes() != null && definition.getRecipeTypes().length > 0;
    }

    private static int extractTier(ItemStack stack) {
        if (stack.getItem() instanceof MetaMachineItem machineItem) {
            MachineDefinition definition = machineItem.getDefinition();
            if (definition != null) {
                return definition.getTier();
            }
        }
        return 0;
    }

    @Override
    public GTRecipeType getRecipeType() {
        if (!embeddedMachineStack.isEmpty() && embeddedMachineStack.getItem() instanceof MetaMachineItem machineItem) {
            MachineDefinition definition = machineItem.getDefinition();
            if (definition != null && definition.getRecipeTypes() != null && definition.getRecipeTypes().length > 0) {
                return definition.getRecipeTypes()[0];
            }
        }
        return super.getRecipeType();
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        MultiblockDisplayText.builder(textList, isFormed())
                .setWorkingStatus(recipeLogic.isWorkingEnabled(), recipeLogic.isActive())
                .addCustom(tl -> {
                    if (isFormed()) {
                        if (embeddedMachineStack.isEmpty()) {
                            tl.add(Component.translatable("tstmodern.machine.big_bro_array.status.no_machine")
                                    .withStyle(ChatFormatting.RED));
                        } else {
                            tl.add(Component.translatable("tstmodern.machine.big_bro_array.status.embedded",
                                    embeddedCount, embeddedMachineStack.getHoverName(), GTValues.VN[embeddedTier])
                                    .withStyle(ChatFormatting.AQUA));
                            tl.add(Component.translatable("tstmodern.machine.big_bro_array.status.parallel",
                                    String.format("%,d", getActualParallel()))
                                    .withStyle(ChatFormatting.GOLD));
                            int discountPercent = (int) Math.round(
                                    (1.0 - BigBroArrayLogic.calculateEnergyDiscount(coilTier)) * 100);
                            tl.add(Component.translatable("tstmodern.machine.big_bro_array.status.coil",
                                    coilTier, discountPercent)
                                    .withStyle(ChatFormatting.GREEN));
                            if (parallelCasingTier > 0) {
                                double speedBoost = BigBroArrayLogic.calculateSpeedBoostDisplay(parallelCasingTier);
                                tl.add(Component.translatable("tstmodern.machine.big_bro_array.status.speed",
                                        parallelCasingTier, String.format("%.2f", speedBoost))
                                        .withStyle(ChatFormatting.YELLOW));
                            }
                        }
                    }
                });
    }

    public static ModifierFunction recipeModifier(MetaMachine machine, GTRecipe recipe) {
        if (!(machine instanceof BigBroArrayMachine arrayMachine)) {
            return ModifierFunction.NULL;
        }

        if (arrayMachine.getEmbeddedMachineStack().isEmpty() || arrayMachine.getEmbeddedCount() <= 0) {
            return ModifierFunction.NULL;
        }

        if (!BigBroArrayTierRules.isEmbeddedTierEligible(
                arrayMachine.getFrameTier(), arrayMachine.getEmbeddedTier())) {
            return ModifierFunction.NULL;
        }

        int recipeTier = RecipeHelper.getRecipeEUtTier(recipe);
        if (recipeTier > arrayMachine.getEmbeddedTier()) {
            return ModifierFunction.NULL;
        }

        int parallelLimit = (int) Math.min(Integer.MAX_VALUE, arrayMachine.getActualParallel());
        int parallel = ParallelLogic.getParallelAmount(machine, recipe, parallelLimit);
        if (parallel <= 0) {
            return ModifierFunction.NULL;
        }

        double energyDiscount = BigBroArrayLogic.calculateEnergyDiscount(arrayMachine.getCoilTier());
        double durationMultiplier = BigBroArrayLogic.calculateDurationMultiplier(arrayMachine.getParallelCasingTier());
        double eutMultiplier = parallel * energyDiscount;

        return ModifierFunction.builder()
                .inputModifier(ContentModifier.multiplier(parallel))
                .outputModifier(ContentModifier.multiplier(parallel))
                .eutMultiplier(eutMultiplier)
                .durationMultiplier(durationMultiplier)
                .parallels(parallel)
                .build();
    }
}
