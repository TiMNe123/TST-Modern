package com.tstmodern.machine;

import java.util.List;
import java.util.Optional;

import com.gregtechceu.gtceu.api.GTValues;
import com.tstmodern.machine.logic.BigBroArrayMachineCatalog;
import com.gregtechceu.gtceu.api.item.MetaMachineItem;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IDisplayUIMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockDisplayText;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.tstmodern.machine.logic.BigBroArrayAddonMatcher;
import com.tstmodern.machine.logic.BigBroArrayAddonMatcher.BlockLookup;
import com.tstmodern.machine.logic.BigBroArrayAddonScanner;
import com.tstmodern.machine.logic.BigBroArrayAddonState;
import com.tstmodern.machine.logic.BigBroArrayLogic;
import com.tstmodern.machine.logic.BigBroArrayTierRules;
import com.tstmodern.machine.logic.BigBroArrayTierRules.CoreTiers;
import com.tstmodern.registry.machine.BigBroArrayStructure;

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
