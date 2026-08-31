package com.tstmodern.machine;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IDisplayUIMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockDisplayText;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.MultiblockWorldSavedData;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
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
import com.tstmodern.machine.logic.OperationalStatus;
import com.tstmodern.machine.logic.BigBroArrayTierRules.CoreTiers;
import com.tstmodern.registry.machine.BigBroArrayStructure;
import net.minecraftforge.items.IItemHandlerModifiable;

import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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

    // Single authoritative state. It is persisted only through the versioned custom compound below.
    private BigBroArrayEmbeddedState embeddedState = BigBroArrayEmbeddedState.EMPTY;
    @DescSynced
    private String syncedEmbeddedDefinitionId = "";
    @DescSynced
    private int syncedEmbeddedCount = 0;
    @DescSynced
    private int syncedEmbeddedTier = 0;
    @DescSynced
    private int syncedEmbeddedMode = -1;
    // Derived cache for UI and unloading; catalog membership is deliberately not required.
    private ItemStack embeddedMachineStack = ItemStack.EMPTY;

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
        if (getLevel() instanceof ServerLevel serverLevel) {
            MultiblockWorldSavedData.getOrCreate(serverLevel).removeAsyncLogic(this);
        }
        if (addonScanSubscription != null) {
            unsubscribe(addonScanSubscription);
        }
        addonScanSubscription = null;
        super.onUnload();
    }

    @Override
    public void asyncCheckPattern(long periodID) {
        if (!(getLevel() instanceof ServerLevel)) {
            return;
        }
        super.asyncCheckPattern(periodID);
    }

    @Override
    public void saveCustomPersistedData(CompoundTag tag, boolean forDrop) {
        super.saveCustomPersistedData(tag, forDrop);
        if (!embeddedState.isEmpty()) {
            tag.put(BigBroArrayEmbeddedState.NBT_KEY, embeddedState.writeToNbt());
        }
    }

    @Override
    public void loadCustomPersistedData(CompoundTag tag) {
        super.loadCustomPersistedData(tag);
        BigBroArrayEmbeddedState restored;
        if (tag.contains(BigBroArrayEmbeddedState.NBT_KEY)) {
            restored = BigBroArrayEmbeddedState.readFromNbt(tag.getCompound(BigBroArrayEmbeddedState.NBT_KEY));
        } else {
            restored = BigBroArrayEmbeddedState.migrateFromLegacy(tag);
        }
        setEmbeddedState(restored);
    }

    private void setEmbeddedState(BigBroArrayEmbeddedState state) {
        this.embeddedState = state == null ? BigBroArrayEmbeddedState.EMPTY : state;
        this.syncedEmbeddedDefinitionId = embeddedState.definitionId() == null
                ? "" : embeddedState.definitionId().toString();
        this.syncedEmbeddedCount = embeddedState.count();
        this.syncedEmbeddedTier = embeddedState.tier();
        this.syncedEmbeddedMode = embeddedState.mode() == null ? -1 : embeddedState.mode().ordinal();
        updateEmbeddedStackCache();
        if (getLevel() != null) {
            recipeLogic.resetRecipeLogic();
        }
    }

    private void updateEmbeddedStackCache() {
        if (embeddedState.isEmpty() || embeddedState.definitionId() == null) {
            this.embeddedMachineStack = ItemStack.EMPTY;
            return;
        }
        MachineDefinition def = GTRegistries.MACHINES.get(embeddedState.definitionId());
        if (def != null) {
            this.embeddedMachineStack = def.asStack();
            if (embeddedState.itemTag() != null) {
                this.embeddedMachineStack.setTag(embeddedState.itemTag().copy());
            }
        } else {
            this.embeddedMachineStack = ItemStack.EMPTY;
        }
    }

    public ItemStack getEmbeddedMachineStack() {
        if (embeddedMachineStack.isEmpty() && !syncedEmbeddedDefinitionId.isEmpty()) {
            ResourceLocation id = ResourceLocation.tryParse(syncedEmbeddedDefinitionId);
            MachineDefinition definition = id == null ? null : GTRegistries.MACHINES.get(id);
            if (definition != null) {
                embeddedMachineStack = definition.asStack();
            }
        }
        return embeddedMachineStack;
    }

    public int getEmbeddedCount() {
        return syncedEmbeddedCount;
    }

    public int getEmbeddedTier() {
        return syncedEmbeddedTier;
    }

    public BigBroArrayMode getEmbeddedMode() {
        return syncedEmbeddedMode == BigBroArrayMode.GENERATOR.ordinal()
                ? BigBroArrayMode.GENERATOR : BigBroArrayMode.PROCESSOR;
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
        return BigBroArrayLogic.calculateParallelism(embeddedState.count(), parallelCasingTier, addonCount);
    }

    public boolean hasEnergyHatch(IO io) {
        for (IMultiPart part : getParts()) {
            if (part instanceof com.gregtechceu.gtceu.common.machine.multiblock.part.EnergyHatchPartMachine energyHatch) {
                if (energyHatch.energyContainer.getHandlerIO() == io) return true;
            } else if (part instanceof com.gregtechceu.gtceu.common.machine.multiblock.part.LaserHatchPartMachine) {
                for (var handlerList : ((com.gregtechceu.gtceu.api.machine.multiblock.part.MultiblockPartMachine) part).getRecipeHandlers()) {
                    if (handlerList.getHandlerIO() == io) return true;
                }
            }
        }
        return false;
    }

    public boolean hasInputEnergy() {
        return hasEnergyHatch(IO.IN);
    }

    public boolean hasOutputEnergy() {
        return hasEnergyHatch(IO.OUT);
    }

    public OperationalStatus getOperationalStatus() {
        return OperationalStatus.evaluate(embeddedState, getFrameTier(), hasInputEnergy(), hasOutputEnergy());
    }

    public static ModifierFunction recipeModifier(MetaMachine machine, GTRecipe recipe) {
        if (!(machine instanceof BigBroArrayMachine array)) {
            return ModifierFunction.NULL;
        }

        OperationalStatus status = array.getOperationalStatus();
        if (!status.canRun()) {
            return ModifierFunction.NULL;
        }

        return BigBroArrayRecipeModifiers.recipeModifier(
                machine,
                recipe,
                array.embeddedState.count(),
                array.embeddedState.tier(),
                array.getEmbeddedMode(),
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

    public List<IItemHandlerModifiable> getItemBuses(IO io) {
        List<IItemHandlerModifiable> list = new ArrayList<>();
        for (IMultiPart part : getParts()) {
            if (part instanceof ItemBusPartMachine bus && bus.getInventory().getHandlerIO() == io) {
                list.add(bus.getInventory().storage);
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

        if (!embeddedState.isEmpty()) {
            if (getEmbeddedMachineStack().isEmpty()) {
                if (playerIn != null) {
                    playerIn.sendSystemMessage(Component.translatable("tstmodern.machine.big_bro_array.status.stale_id")
                            .withStyle(ChatFormatting.RED));
                }
                return InteractionResult.FAIL;
            }

            List<IItemHandlerModifiable> exportBuses = getItemBuses(IO.OUT);

            BigBroArrayMachineTransfer.UnloadResult unloadResult = BigBroArrayMachineTransfer.planAndExecuteUnload(
                    embeddedState, exportBuses, embeddedMachineStack);

            if (playerIn != null) {
                playerIn.sendSystemMessage(Component.translatable(unloadResult.messageKey(), unloadResult.messageArgs())
                        .withStyle(unloadResult.success() ? ChatFormatting.YELLOW : ChatFormatting.RED));
            }

            if (unloadResult.success()) {
                setEmbeddedState(BigBroArrayEmbeddedState.EMPTY);
                return InteractionResult.CONSUME;
            }
            return InteractionResult.FAIL;
        }

        int maxAllowedTier = BigBroArrayTierRules.maxEmbeddedTier(getFrameTier());
        List<IItemHandlerModifiable> importBuses = getItemBuses(IO.IN);
        BigBroArrayMachineTransfer.LoadResult loadResult = BigBroArrayMachineTransfer.planAndExecuteLoad(
                importBuses, maxAllowedTier);

        if (playerIn != null) {
            playerIn.sendSystemMessage(Component.translatable(loadResult.messageKey(), loadResult.messageArgs())
                    .withStyle(loadResult.success() ? ChatFormatting.GREEN : ChatFormatting.RED));
        }

        if (loadResult.success()) {
            setEmbeddedState(loadResult.state());
            return InteractionResult.CONSUME;
        }

        return InteractionResult.FAIL;
    }

    @Override
    public GTRecipeType getRecipeType() {
        if (!embeddedState.isEmpty() && embeddedState.definitionId() != null) {
            Optional<BigBroArrayMachineCatalog.Entry> entry = BigBroArrayMachineCatalog.find(embeddedState.definitionId());
            if (entry.isPresent()) {
                return entry.get().recipeType();
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
                        if (syncedEmbeddedCount <= 0 || syncedEmbeddedDefinitionId.isEmpty()) {
                            tl.add(Component.translatable("tstmodern.machine.big_bro_array.status.no_machine")
                                     .withStyle(ChatFormatting.RED));
                        } else {
                            Component modeComponent = Component.translatable(
                                    getEmbeddedMode() == BigBroArrayMode.PROCESSOR
                                            ? "tstmodern.machine.big_bro_array.status.mode.processor"
                                            : "tstmodern.machine.big_bro_array.status.mode.generator");
                            tl.add(Component.translatable("tstmodern.machine.big_bro_array.status.embedded",
                                    syncedEmbeddedCount,
                                    getEmbeddedMachineStack().getHoverName(),
                                    com.gregtechceu.gtceu.api.GTValues.VN[syncedEmbeddedTier],
                                    modeComponent)
                                    .withStyle(ChatFormatting.AQUA));

                            long actualParallel = getActualParallel();
                            long maxParallel = BigBroArrayLogic.calculateMaxParallelism(addonCount, parallelCasingTier);
                            tl.add(Component.translatable("tstmodern.machine.big_bro_array.status.parallel",
                                    String.format("%,d", actualParallel), String.format("%,d", maxParallel))
                                    .withStyle(ChatFormatting.GOLD));

                            tl.add(Component.translatable("tstmodern.machine.big_bro_array.status.addon_info",
                                    addonCount, addonValidMask)
                                    .withStyle(ChatFormatting.GRAY));

                            tl.add(Component.translatable("tstmodern.machine.big_bro_array.status.addon_directions",
                                    addonPresence(0), addonPresence(1), addonPresence(2), addonPresence(3))
                                    .withStyle(ChatFormatting.GRAY));

                            tl.add(Component.translatable("tstmodern.machine.big_bro_array.status.minima",
                                    addonFrameTier, addonGlassTier, parallelCasingTier, coilTier)
                                    .withStyle(ChatFormatting.GRAY));

                            if (getEmbeddedMode() == BigBroArrayMode.PROCESSOR) {
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
                            } else {
                                tl.add(Component.translatable("tstmodern.machine.big_bro_array.status.generator_no_bonus")
                                        .withStyle(ChatFormatting.GRAY));
                            }

                            OperationalStatus status = getOperationalStatus();
                            if (!status.canRun()) {
                                tl.add(Component.translatable(status.messageKey()).withStyle(ChatFormatting.RED));
                            }
                        }
                    }
                });
    }

    private Component addonPresence(int placement) {
        String key = (addonValidMask & (1 << placement)) != 0
                ? "tstmodern.machine.big_bro_array.status.present"
                : "tstmodern.machine.big_bro_array.status.absent";
        return Component.translatable(key);
    }
}
