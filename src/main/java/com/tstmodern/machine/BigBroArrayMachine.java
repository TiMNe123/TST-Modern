package com.tstmodern.machine;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.item.MetaMachineItem;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IDisplayUIMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockDisplayText;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.part.MultiblockPartMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.common.machine.multiblock.part.ItemBusPartMachine;
import com.tstmodern.machine.logic.BigBroArrayAddonMatcher;
import com.tstmodern.machine.logic.BigBroArrayAddonMatcher.BlockLookup;
import com.tstmodern.machine.logic.BigBroArrayAddonScanner;
import com.tstmodern.machine.logic.BigBroArrayAddonState;
import com.tstmodern.machine.logic.BigBroArrayEmbeddedState;
import com.tstmodern.machine.logic.BigBroArrayLogic;
import com.tstmodern.machine.logic.BigBroArrayMachineCatalog;
import com.tstmodern.machine.logic.BigBroArrayMachineTransfer;
import com.tstmodern.machine.logic.BigBroArrayMode;
import com.tstmodern.machine.logic.BigBroArrayRecipeModifiers;
import com.tstmodern.machine.logic.BigBroArrayTierRules;
import com.tstmodern.machine.logic.BigBroArrayTierRules.CoreTiers;
import com.tstmodern.registry.machine.BigBroArrayStructure;
import net.minecraftforge.items.IItemHandlerModifiable;

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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
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
    private BigBroArrayMode embeddedMode = BigBroArrayMode.PROCESSOR;

    @DescSynced
    private int addonCount = 0;

    @DescSynced
    private int addonValidMask = 0;

    @DescSynced
    private int addonFrameTier = 0;

    @DescSynced
    private int addonGlassTier = 0;

    @DescSynced
    private int parallelCasingTier = 0;

    @DescSynced
    private int coilTier = 0;

    /** One immutable snapshot published only after all core tier channels validate. */
    private CoreTiers coreTiers;
    private final BigBroArrayAddonScanner addonScanner;
    private TickableSubscription addonScanSubscription;

    public BigBroArrayMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
        BlockLookup lookup = new BlockLookup() {
            @Override
            public boolean isLoaded(BlockPos pos) {
                Level level = getLevel();
                return level != null && level.isLoaded(pos);
            }

            @Override
            public BlockState getLoadedState(BlockPos pos) {
                Level level = getLevel();
                return level != null && level.isLoaded(pos) ? level.getBlockState(pos) : null;
            }
        };
        this.addonScanner = new BigBroArrayAddonScanner(
                BigBroArrayStructure.ADDON_PLACEMENTS,
                new BigBroArrayAddonMatcher(),
                lookup,
                this::getPos,
                this::getFrontFacing,
                this::getUpwardsFacing,
                this::isFlipped,
                this::getCoreTiers,
                this::onAddonStateChanged);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (addonScanSubscription == null) {
            addonScanSubscription = subscribeServerTick(this::scanAddonTick);
        }
    }

    @Override
    public void onUnload() {
        if (addonScanSubscription != null) {
            unsubscribe(addonScanSubscription);
        }
        addonScanSubscription = null;
        super.onUnload();
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
        return coreTiers == null ? 0 : addonFrameTier;
    }

    public int getGlassTier() {
        return coreTiers == null ? 0 : addonGlassTier;
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
        return addonCount > 0;
    }

    public int getAddonCount() {
        return addonCount;
    }

    public int getAddonValidMask() {
        return addonValidMask;
    }

    public long getActualParallel() {
        return BigBroArrayLogic.calculateParallelism(embeddedCount, parallelCasingTier, addonCount);
    }

    public static ModifierFunction recipeModifier(MetaMachine machine, GTRecipe recipe) {
        if (!(machine instanceof BigBroArrayMachine array)) {
            return ModifierFunction.NULL;
        }
        return BigBroArrayRecipeModifiers.recipeModifier(
                machine,
                recipe,
                array.embeddedCount,
                array.embeddedTier,
                array.embeddedMode,
                array.parallelCasingTier,
                array.addonCount,
                array.coilTier
        );
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();

        var state = getMultiblockState();
        this.coreTiers = state == null ? null :
                BigBroArrayTierRules.validatedCoreTiers(state.getMatchContext());
        if (coreTiers == null) {
            addonScanner.clear();
            return;
        }
        addonScanner.scanAllNow();
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        this.coreTiers = null;
        addonScanner.clear();
    }

    private void scanAddonTick() {
        if (isFormed() && getOffsetTimer() % 5 == 0) {
            addonScanner.scanNext();
        }
    }

    private void onAddonStateChanged(BigBroArrayAddonState state) {
        this.addonCount = state.addonCount();
        this.addonValidMask = state.validMask();
        this.addonFrameTier = state.frameTier();
        this.addonGlassTier = state.glassTier();
        this.parallelCasingTier = state.parallelTier();
        this.coilTier = state.coilTier();
        if (isFormed()) {
            recipeLogic.resetRecipeLogic();
        }
    }

    private List<IItemHandlerModifiable> getItemImportBuses() {
        List<IItemHandlerModifiable> list = new ArrayList<>();
        for (IMultiPart part : getParts()) {
            if (part instanceof ItemBusPartMachine bus && bus.getInventory().getHandlerIO() == IO.IN) {
                list.add(bus.getInventory());
            }
        }
        return list;
    }

    private List<IItemHandlerModifiable> getItemExportBuses() {
        List<IItemHandlerModifiable> list = new ArrayList<>();
        for (IMultiPart part : getParts()) {
            if (part instanceof ItemBusPartMachine bus && bus.getInventory().getHandlerIO() == IO.OUT) {
                list.add(bus.getInventory());
            }
        }
        return list;
    }

    @Override
    protected InteractionResult onScrewdriverClick(
            Player playerIn,
            InteractionHand hand,
            Direction gridSide,
            BlockHitResult hitResult) {
        if (getLevel() == null || getLevel().isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (!isFormed()) {
            if (playerIn != null) {
                playerIn.sendSystemMessage(Component.translatable("tstmodern.machine.big_bro_array.status.not_formed")
                        .withStyle(ChatFormatting.RED));
            }
            return InteractionResult.PASS;
        }

        if (!embeddedMachineStack.isEmpty() && embeddedCount > 0) {
            List<IItemHandlerModifiable> exportBuses = getItemExportBuses();
            BigBroArrayEmbeddedState currentState = new BigBroArrayEmbeddedState(
                    BigBroArrayEmbeddedState.CURRENT_VERSION,
                    embeddedMachineStack.getItem() instanceof MetaMachineItem m ? m.getDefinition().getId() : null,
                    embeddedMode,
                    embeddedTier,
                    embeddedCount,
                    embeddedMachineStack.getTag() != null ? embeddedMachineStack.getTag().copy() : null
            );

            BigBroArrayMachineTransfer.UnloadResult unloadResult = BigBroArrayMachineTransfer.planAndExecuteUnload(
                    currentState, exportBuses, embeddedMachineStack);

            if (playerIn != null) {
                playerIn.sendSystemMessage(Component.translatable(unloadResult.messageKey(), unloadResult.messageArgs())
                        .withStyle(unloadResult.success() ? ChatFormatting.YELLOW : ChatFormatting.RED));
            }

            if (unloadResult.success()) {
                this.embeddedMachineStack = ItemStack.EMPTY;
                this.embeddedCount = 0;
                this.embeddedTier = 0;
                this.embeddedMode = BigBroArrayMode.PROCESSOR;
                this.recipeLogic.resetRecipeLogic();
                return InteractionResult.CONSUME;
            }
            return InteractionResult.FAIL;
        }

        int maxAllowedTier = BigBroArrayTierRules.maxEmbeddedTier(getFrameTier());
        List<IItemHandlerModifiable> importBuses = getItemImportBuses();
        BigBroArrayMachineTransfer.LoadResult loadResult = BigBroArrayMachineTransfer.planAndExecuteLoad(
                importBuses, maxAllowedTier);

        if (playerIn != null) {
            playerIn.sendSystemMessage(Component.translatable(loadResult.messageKey(), loadResult.messageArgs())
                    .withStyle(loadResult.success() ? ChatFormatting.GREEN : ChatFormatting.RED));
        }

        if (loadResult.success()) {
            BigBroArrayEmbeddedState state = loadResult.state();
            MachineDefinition def = GTRegistries.MACHINES.get(state.definitionId());
            if (def != null) {
                this.embeddedMachineStack = def.asStack();
                if (state.itemTag() != null) {
                    this.embeddedMachineStack.setTag(state.itemTag().copy());
                }
                this.embeddedCount = state.count();
                this.embeddedTier = state.tier();
                this.embeddedMode = state.mode() != null ? state.mode() : BigBroArrayMode.PROCESSOR;
                this.recipeLogic.resetRecipeLogic();
                return InteractionResult.CONSUME;
            }
        }

        return InteractionResult.FAIL;
    }

    @Override
    public GTRecipeType getRecipeType() {
        Optional<BigBroArrayMachineCatalog.Entry> entry = BigBroArrayMachineCatalog.find(embeddedMachineStack);
        if (entry.isPresent()) {
            return entry.get().recipeType();
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
                                        parallelCasingTier, (int) Math.round((speedBoost - 1.0) * 100))
                                        .withStyle(ChatFormatting.YELLOW));
                            }
                        }
                    }
                });
    }
}
