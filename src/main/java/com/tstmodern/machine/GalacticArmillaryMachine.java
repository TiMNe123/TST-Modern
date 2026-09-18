package com.tstmodern.machine;

import static com.gregtechceu.gtceu.api.GTValues.UEV;

import java.util.List;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.tstmodern.config.TSTConfig;
import com.tstmodern.client.widget.GalacticProgressWidget;

import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.lowdragmc.lowdraglib.gui.widget.ComponentPanelWidget;
import com.lowdragmc.lowdraglib.gui.widget.DraggableScrollableWidgetGroup;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

/** Fixed-voltage Galactic Armillary with a persistent, charge-driven ignition gate. */
public final class GalacticArmillaryMachine extends WorkableElectricMultiblockMachine {
    public static final long CORE_CAPACITY = 2_560_000_000L;
    public static final int IGNITION_BURST_TICKS = 30;
    private static final int RECIPE_TRANSITION_GRACE_TICKS = 40;

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER =
            new ManagedFieldHolder(GalacticArmillaryMachine.class,
                    WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    @DescSynced
    private long coreEnergy;

    @Persisted
    @DescSynced
    private boolean charging;

    @Persisted
    @DescSynced
    private boolean stable;

    @Persisted
    @DescSynced
    private int startupTicks;

    @Persisted
    @DescSynced
    private int ignitionTicks;

    private long lastEligibleRecipeTick = Long.MIN_VALUE;
    private TickableSubscription chargeSubscription;

    public GalacticArmillaryMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (charging || stable) lastEligibleRecipeTick = getOffsetTimer();
        chargeSubscription = subscribeServerTick(chargeSubscription, this::tickCharge);
    }

    @Override
    public void onUnload() {
        if (chargeSubscription != null) {
            unsubscribe(chargeSubscription);
            chargeSubscription = null;
        }
        super.onUnload();
    }

    @Override
    public void onStructureInvalid() {
        resetCore();
        super.onStructureInvalid();
    }

    @Override
    public boolean beforeWorking(GTRecipe recipe) {
        lastEligibleRecipeTick = getOffsetTimer();
        if (stable) return super.beforeWorking(recipe);
        charging = true;
        return false;
    }

    private void tickCharge() {
        if (!isFormed() || !recipeLogic.isWorkingEnabled()) {
            resetCore();
            return;
        }

        long now = getOffsetTimer();
        if (stable && ignitionTicks < IGNITION_BURST_TICKS) ignitionTicks++;
        if (recipeLogic.isWorking()) {
            if (stable) lastEligibleRecipeTick = now;
            return;
        }
        if (charging) {
            int requiredTicks = requiredStartupTicks();
            if (chargeCore()) startupTicks = advanceStartupTicks(startupTicks, requiredTicks);
            if (startupComplete(coreEnergy, startupTicks, requiredTicks)) {
                charging = false;
                stable = true;
                ignitionTicks = 0;
                lastEligibleRecipeTick = now;
                recipeLogic.markLastRecipeDirty();
            }
        } else if (stable && eligibilityExpired(now, lastEligibleRecipeTick)) {
            resetCore();
        }
    }

    private boolean chargeCore() {
        if (energyContainer == null) energyContainer = getEnergyContainer();
        long missing = CORE_CAPACITY - coreEnergy;
        if (missing <= 0) return true;
        long drained = -energyContainer.changeEnergy(-missing);
        coreEnergy = advanceCharge(coreEnergy, drained);
        return drained > 0;
    }

    private void resetCore() {
        coreEnergy = 0;
        charging = false;
        stable = false;
        startupTicks = 0;
        ignitionTicks = 0;
        lastEligibleRecipeTick = Long.MIN_VALUE;
    }

    static long advanceCharge(long stored, long received) {
        if (received <= 0) return Math.max(0, Math.min(CORE_CAPACITY, stored));
        return Math.min(CORE_CAPACITY, Math.max(0, stored) + received);
    }

    static boolean eligibilityExpired(long now, long lastEligible) {
        return lastEligible == Long.MIN_VALUE || now - lastEligible > RECIPE_TRANSITION_GRACE_TICKS;
    }

    static int startupTicksForSeconds(int seconds) {
        return (int) Math.min(Integer.MAX_VALUE, (long) Math.max(20, seconds) * 20L);
    }

    static int advanceStartupTicks(int current, int required) {
        return Math.min(required, Math.max(0, current) + 1);
    }

    static boolean startupComplete(long storedEnergy, int elapsedTicks, int requiredTicks) {
        return storedEnergy >= CORE_CAPACITY && elapsedTicks >= requiredTicks;
    }

    static int progressPercent(long current, long maximum) {
        if (maximum <= 0) return 100;
        return (int) Math.min(100, Math.max(0, current) * 100 / maximum);
    }

    static float activationProgress(long energy, int elapsedTicks, int requiredTicks) {
        float energyProgress = Math.min(1, Math.max(0, energy) / (float) CORE_CAPACITY);
        float timeProgress = requiredTicks <= 0 ? 1 : Math.min(1, Math.max(0, elapsedTicks) / (float) requiredTicks);
        return Math.min(energyProgress, timeProgress);
    }

    private static int requiredStartupTicks() {
        return startupTicksForSeconds(TSTConfig.GALACTIC_ARMILLARY_STARTUP_SECONDS.get());
    }

    @Override
    public long getMaxVoltage() {
        return GTValues.V[UEV];
    }

    @Override
    public long getOverclockVoltage() {
        return GTValues.V[UEV];
    }

    public long getCoreEnergy() {
        return coreEnergy;
    }

    public boolean isCharging() {
        return charging;
    }

    public boolean isStable() {
        return stable && isFormed();
    }

    public int getStartupTicks() {
        return startupTicks;
    }

    public int getIgnitionTicks() {
        return ignitionTicks;
    }

    public float getActivationProgress() {
        return activationProgress(coreEnergy, startupTicks, requiredStartupTicks());
    }

    private double getEnergyProgress() {
        return Math.min(1, Math.max(0, coreEnergy) / (double) CORE_CAPACITY);
    }

    private double getWarmupProgress() {
        return Math.min(1, Math.max(0, startupTicks) / (double) requiredStartupTicks());
    }

    @Override
    public Widget createUIWidget() {
        WidgetGroup group = new WidgetGroup(0, 0, 190, 142);
        DraggableScrollableWidgetGroup screen = new DraggableScrollableWidgetGroup(4, 4, 182, 134);
        screen.setBackground(getScreenTexture());
        screen.addWidget(new LabelWidget(4, 5,
                () -> self().getBlockState().getBlock().getName().getString()));
        screen.addWidget(new ComponentPanelWidget(4, 17, this::addDisplayText)
                .textSupplier(getLevel().isClientSide ? null : this::addDisplayText)
                .setMaxWidthLimit(174)
                .clickHandler(this::handleDisplayClick));
        screen.addWidget(new ComponentPanelWidget(4, 62, this::addEnergyText)
                .textSupplier(getLevel().isClientSide ? null : this::addEnergyText)
                .setMaxWidthLimit(174));
        screen.addWidget(new GalacticProgressWidget(this::getEnergyProgress, this::isCharging, 4, 73));
        screen.addWidget(new ComponentPanelWidget(4, 99, this::addWarmupText)
                .textSupplier(getLevel().isClientSide ? null : this::addWarmupText)
                .setMaxWidthLimit(174));
        screen.addWidget(new GalacticProgressWidget(this::getWarmupProgress, this::isCharging, 4, 110));
        group.addWidget(screen);
        return group;
    }

    private void addEnergyText(List<Component> text) {
        if (!charging) return;
        text.add(Component.translatable("tstmodern.machine.galactic_armillary.status.energy",
                coreEnergy, CORE_CAPACITY).withStyle(ChatFormatting.AQUA));
    }

    private void addWarmupText(List<Component> text) {
        if (!charging) return;
        int requiredTicks = requiredStartupTicks();
        text.add(Component.translatable("tstmodern.machine.galactic_armillary.status.warmup",
                startupTicks / 20, requiredTicks / 20).withStyle(ChatFormatting.GOLD));
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (stable) {
            textList.add(Component.translatable("tstmodern.machine.galactic_armillary.status.stable")
                    .withStyle(ChatFormatting.LIGHT_PURPLE));
        }
    }
}
