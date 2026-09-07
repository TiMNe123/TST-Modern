package com.tstmodern.machine;

import java.util.Comparator;
import java.util.List;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.gui.fancy.IFancyConfigurator;
import com.gregtechceu.gtceu.api.gui.fancy.IFancyUIProvider;
import com.gregtechceu.gtceu.api.gui.fancy.TooltipsPanel;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.IFancyUIMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IDisplayUIMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockDisplayText;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.error.PatternError;
import com.tstmodern.registry.TSTBlocks;

import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.widget.ComponentPanelWidget;
import com.lowdragmc.lowdraglib.gui.widget.DraggableScrollableWidgetGroup;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

/**
 * Formation-time casing-tier validation and runtime state for the Large Disassembler.
 */
public final class DisassemblerMachine extends WorkableMultiblockMachine implements IDisplayUIMachine, IFancyUIMachine {
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER =
            new ManagedFieldHolder(DisassemblerMachine.class,
                    WorkableMultiblockMachine.MANAGED_FIELD_HOLDER);

    private static final int REQUIRED_TIER_CASING_COUNT = 66;
    static final VoidingMode VOIDING_MODE = VoidingMode.VOID_NONE;

    @Persisted
    @DescSynced
    private int casingTier;

    public DisassemblerMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        IDisplayUIMachine.super.addDisplayText(textList);
        MultiblockDisplayText.builder(textList, isFormed())
                .setWorkingStatus(recipeLogic.isWorkingEnabled(), recipeLogic.isActive())
                .addWorkingStatusLine()
                .addProgressLine(recipeLogic)
                .addCustom(builder -> {
                    if (isFormed() && casingTier > 0) {
                        String tierName = casingTier < GTValues.VN.length ? GTValues.VN[casingTier] : String.valueOf(casingTier);
                        builder.add(Component.translatable("tstmodern.machine.disassembler.gui.casing_tier", tierName));
                    }
                })
                .addOutputLines(recipeLogic.getLastRecipe());
        getDefinition().getAdditionalDisplay().accept(this, textList);
    }

    @Override
    public ModularUI createUI(Player player) {
        return IFancyUIMachine.super.createUI(player);
    }

    @Override
    public Widget createUIWidget() {
        WidgetGroup group = new WidgetGroup(0, 0, 182, 117);
        DraggableScrollableWidgetGroup screen = new DraggableScrollableWidgetGroup(4, 4, 174, 109);
        screen.setBackground(getScreenTexture());
        screen.addWidget(new LabelWidget(4, 5,
                () -> self().getBlockState().getBlock().getName().getString()));
        screen.addWidget(new ComponentPanelWidget(4, 17, this::addDisplayText)
                .setMaxWidthLimit(166)
                .clickHandler(this::handleDisplayClick));
        group.addWidget(screen);
        return group;
    }

    @Override
    public List<IFancyUIProvider> getSubTabs() {
        return getParts().stream()
                .filter(IFancyUIProvider.class::isInstance)
                .map(IFancyUIProvider.class::cast)
                .toList();
    }

    @Override
    public void attachTooltips(TooltipsPanel tooltipsPanel) {
        IFancyUIMachine.super.attachTooltips(tooltipsPanel);
        for (IMultiPart part : getParts()) {
            part.attachFancyTooltipsToController(this, tooltipsPanel);
        }
    }

    @Override
    public void attachConfigurators(com.gregtechceu.gtceu.api.gui.fancy.ConfiguratorPanel configuratorPanel) {
        IFancyUIMachine.super.attachConfigurators(configuratorPanel);
        configuratorPanel.attachConfigurators(new IFancyConfigurator[0]);
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
