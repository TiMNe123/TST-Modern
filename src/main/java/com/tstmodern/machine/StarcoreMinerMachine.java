package com.tstmodern.machine;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.widget.SlotWidget;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.IVoidable;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.lowdragmc.lowdraglib.gui.widget.ComponentPanelWidget;
import com.lowdragmc.lowdraglib.gui.widget.DraggableScrollableWidgetGroup;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.tstmodern.registry.TSTBlocks;
import com.tstmodern.registry.machine.StarcoreMinerStructure;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

public final class StarcoreMinerMachine extends WorkableElectricMultiblockMachine {
    public static final ResourceLocation ASTRAL_ARRAY_FABRICATOR_ID =
            new ResourceLocation("tstmodern", "astral_array_fabricator");

    public static final java.util.Set<PartAbility> ALLOWED_ABILITIES = java.util.Set.of(
            PartAbility.EXPORT_ITEMS,
            PartAbility.IMPORT_ITEMS,
            PartAbility.IMPORT_FLUIDS,
            PartAbility.INPUT_ENERGY,
            PartAbility.SUBSTATION_INPUT_ENERGY,
            PartAbility.INPUT_LASER
    );

    public static boolean isAllowedAbility(PartAbility ability) {
        return ALLOWED_ABILITIES.contains(ability);
    }

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER =
            new ManagedFieldHolder(StarcoreMinerMachine.class,
                    WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    public final NotifiableItemStackHandler boosterInventory =
            new NotifiableItemStackHandler(this, 1, IO.NONE, IO.BOTH)
                    .setFilter(stack -> !stack.isEmpty() &&
                            ASTRAL_ARRAY_FABRICATOR_ID.equals(ForgeRegistries.ITEMS.getKey(stack.getItem())));

    public StarcoreMinerMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
        setVoidingMode(IVoidable.VoidingMode.VOID_ITEMS);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    public static int requiredPipePieces(int controllerY) {
        return Math.max(0, controllerY - 24);
    }

    public static int pipeLocalRight() {
        return 0;
    }

    public static int pipeLocalBack() {
        return 19;
    }

    public int getBoosterCount() {
        ItemStack stack = boosterInventory.getStackInSlot(0);
        if (!stack.isEmpty() && ASTRAL_ARRAY_FABRICATOR_ID.equals(ForgeRegistries.ITEMS.getKey(stack.getItem()))) {
            return stack.getCount();
        }
        return 0;
    }

    @Override
    public Widget createUIWidget() {
        WidgetGroup group = new WidgetGroup(0, 0, 190, 125);
        group.addWidget(new DraggableScrollableWidgetGroup(4, 4, 154, 117)
                .setBackground(getScreenTexture())
                .addWidget(new LabelWidget(4, 5,
                        () -> self().getBlockState().getBlock().getName().getString()))
                .addWidget(new ComponentPanelWidget(4, 17, this::addDisplayText)
                        .textSupplier(getLevel().isClientSide ? null : this::addDisplayText)
                        .setMaxWidthLimit(148)
                        .clickHandler(this::handleDisplayClick)));
        group.addWidget(new SlotWidget(boosterInventory, 0, 166, 53, true, true)
                .setBackgroundTexture(GuiTextures.SLOT));
        group.setBackground(GuiTextures.BACKGROUND_INVERSE);
        return group;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        if (!validateMiningPipe()) {
            onStructureInvalid();
        }
    }

    public boolean validateMiningPipe() {
        Level level = getLevel();
        if (level == null) {
            return false;
        }
        BlockPos pos = getPos();
        int needPieces = requiredPipePieces(pos.getY());
        if (needPieces <= 0) {
            return true;
        }

        Direction front = getFrontFacing();
        Direction right = RelativeDirection.RIGHT.getRelative(front, getUpwardsFacing(), isFlipped());
        Direction back = RelativeDirection.BACK.getRelative(front, getUpwardsFacing(), isFlipped());

        for (int p = 0; p < needPieces; p++) {
            int y = pos.getY() - 4 - p;
            BlockPos centerPos = pos.relative(back, pipeLocalBack()).relative(right, pipeLocalRight()).atY(y);
            if (level.getBlockState(centerPos).is(Blocks.BEDROCK)) {
                break;
            }
            for (int b = 0; b < 9; b++) {
                for (int r = 0; r < 9; r++) {
                    char symbol = StarcoreMinerStructure.MIDDLE_AISLES[b][0].charAt(r);
                    if (symbol == ' ') {
                        continue;
                    }
                    BlockPos blockPos = pos.relative(back, 15 + b).relative(right, r - 4).atY(y);
                    BlockState state = level.getBlockState(blockPos);
                    if (!matchesMiddleSymbol(symbol, state)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static boolean matchesMiddleSymbol(char symbol, BlockState state) {
        return switch (symbol) {
            case 'F' -> state.is(TSTBlocks.RADIANT_NAQUADAH_ALLOY_CASING.get());
            case 'I' -> state.is(TSTBlocks.HOLLOW_CASING.get());
            case 'K' -> state.is(GTBlocks.FUSION_GLASS.get()) ||
                    state.is(TSTBlocks.RADIANT_NAQUADAH_ALLOY_CASING.get()) ||
                    state.is(com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper.getBlock(TagPrefix.frameGt, GTMaterials.NaquadahAlloy));
            case ' ' -> true;
            default -> false;
        };
    }
}
