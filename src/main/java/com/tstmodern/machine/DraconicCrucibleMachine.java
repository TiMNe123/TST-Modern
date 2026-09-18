package com.tstmodern.machine;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.error.PatternStringError;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.OverclockingLogic;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;

import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.tstmodern.block.DraconicCrucibleCoreBlock;
import com.tstmodern.registry.TSTBlocks;
import com.tstmodern.registry.machine.DraconicCrucibleStructure;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.List;

/** Single-batch Draconic Crucible runtime with normal GTCEu overclocking. */
public final class DraconicCrucibleMachine extends WorkableElectricMultiblockMachine {

    public static final int WAKE_TICKS = 10;
    public static final int TAKEOFF_TICKS = 30;
    public static final int ORBIT_TICKS = 100;
    public static final int LANDING_TICKS = 40;
    public static final int CHARGE_TICKS = 20;
    public static final int STARTUP_TICKS =
            WAKE_TICKS + TAKEOFF_TICKS + ORBIT_TICKS + LANDING_TICKS + CHARGE_TICKS;

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER =
            new ManagedFieldHolder(DraconicCrucibleMachine.class,
                    WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    @DescSynced
    private int dragonStartupTicks;

    @Persisted
    @DescSynced
    private boolean dragonStartupActive;

    @Persisted
    @DescSynced
    private boolean dragonReady;

    private long lastEligibleRecipeTick = Long.MIN_VALUE;
    private TickableSubscription dragonTickSubscription;

    public DraconicCrucibleMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (dragonStartupActive) {
            lastEligibleRecipeTick = getOffsetTimer();
        }
        dragonTickSubscription = subscribeServerTick(dragonTickSubscription, this::tickDragonStartup);
    }

    @Override
    public void onUnload() {
        if (dragonTickSubscription != null) {
            unsubscribe(dragonTickSubscription);
            dragonTickSubscription = null;
        }
        super.onUnload();
    }

    @Override
    public void onStructureInvalid() {
        setCoreFormed(false);
        resetDragonStartup();
        super.onStructureInvalid();
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        int itemInputs = 0;
        int itemOutputs = 0;
        int energyInputs = 0;
        for (IMultiPart part : getParts()) {
            if (hasAbility(part, PartAbility.IMPORT_ITEMS)) itemInputs++;
            if (hasAbility(part, PartAbility.EXPORT_ITEMS)) itemOutputs++;
            if (hasAbility(part, PartAbility.INPUT_ENERGY) ||
                    hasAbility(part, PartAbility.SUBSTATION_INPUT_ENERGY) ||
                    hasAbility(part, PartAbility.INPUT_LASER)) {
                energyInputs++;
            }
        }
        if (getParts().size() > 10 || itemInputs < 1 || itemOutputs < 1 ||
                energyInputs < 1 || energyInputs > 2) {
            getMultiblockState().setError(new PatternStringError(
                    "tstmodern.machine.draconic_crucible.error.part_limits"));
            onStructureInvalid();
            return;
        }
        setCoreFormed(true);
    }

    private static boolean hasAbility(IMultiPart part, PartAbility ability) {
        return ability.getAllBlocks().contains(part.self().getBlockState().getBlock());
    }

    private void setCoreFormed(boolean formed) {
        if (getLevel() == null) return;
        var right = RelativeDirection.RIGHT.getRelative(getFrontFacing(), getUpwardsFacing(), isFlipped());
        var down = RelativeDirection.DOWN.getRelative(getFrontFacing(), getUpwardsFacing(), isFlipped());
        var back = RelativeDirection.BACK.getRelative(getFrontFacing(), getUpwardsFacing(), isFlipped());
        var corePos = getPos()
                .relative(right, DraconicCrucibleStructure.CORE_X - DraconicCrucibleStructure.CONTROLLER_X)
                .relative(down, DraconicCrucibleStructure.CONTROLLER_Y - DraconicCrucibleStructure.CORE_Y)
                .relative(back, DraconicCrucibleStructure.CORE_Z - DraconicCrucibleStructure.CONTROLLER_Z);
        var state = getLevel().getBlockState(corePos);
        if (state.is(TSTBlocks.DRACONIC_CRUCIBLE_CORE.get()) &&
                state.getValue(DraconicCrucibleCoreBlock.FORMED) != formed) {
            getLevel().setBlock(corePos, state.setValue(DraconicCrucibleCoreBlock.FORMED, formed), 3);
        }
    }

    @Override
    public boolean beforeWorking(GTRecipe recipe) {
        if (dragonReady) {
            return super.beforeWorking(recipe);
        }

        lastEligibleRecipeTick = getOffsetTimer();
        if (!dragonStartupActive) {
            dragonStartupActive = true;
            dragonStartupTicks = 0;
        }
        if (dragonStartupTicks < STARTUP_TICKS) {
            return false;
        }

        dragonStartupActive = false;
        dragonReady = true;
        return super.beforeWorking(recipe);
    }

    private void tickDragonStartup() {
        if (dragonStartupActive) {
            if (getOffsetTimer() - lastEligibleRecipeTick > 10) {
                resetDragonStartup();
            } else if (dragonStartupTicks < STARTUP_TICKS) {
                dragonStartupTicks++;
            }
        } else if (dragonReady && !recipeLogic.isActive()) {
            resetDragonStartup();
        }
    }

    private void resetDragonStartup() {
        dragonStartupTicks = 0;
        dragonStartupActive = false;
        dragonReady = false;
        lastEligibleRecipeTick = Long.MIN_VALUE;
    }

    public int getDragonStartupTicks() {
        return dragonStartupTicks;
    }

    public boolean isDragonStartupActive() {
        return dragonStartupActive;
    }

    public boolean isDragonReady() {
        return dragonReady;
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (dragonStartupActive) {
            textList.add(Component.translatable("tstmodern.machine.draconic_crucible.status.animation_starting")
                    .withStyle(ChatFormatting.GOLD));
        }
    }

    public static ModifierFunction recipeModifier(MetaMachine machine, GTRecipe recipe) {
        if (!(machine instanceof DraconicCrucibleMachine crucible) || recipe == null) {
            return ModifierFunction.NULL;
        }
        return OverclockingLogic.NON_PERFECT_OVERCLOCK
                .getModifier(machine, recipe, crucible.getOverclockVoltage());
    }
}
