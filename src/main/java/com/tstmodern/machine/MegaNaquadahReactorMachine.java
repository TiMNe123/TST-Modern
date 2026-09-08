package com.tstmodern.machine;

import java.util.ArrayList;
import java.util.List;

import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeHandler;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockDisplayText;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.misc.EnergyContainerList;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.tstmodern.machine.logic.MegaNaquadahReactorLogic;
import com.tstmodern.recipe.meganaquadah.MegaNaquadahReactorRecipeTrait;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fluids.FluidStack;

public class MegaNaquadahReactorMachine extends WorkableElectricMultiblockMachine {
    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            MegaNaquadahReactorMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    @DescSynced
    private long runTimeTicks;
    @Persisted
    @DescSynced
    private long currentOutput;
    @Persisted
    @DescSynced
    private int parallel = 1;
    @Persisted
    @DescSynced
    private int baseOutput;
    @Persisted
    @DescSynced
    private int coolantEfficiency = 100;
    @Persisted
    @DescSynced
    private int excitedMultiplier = 1;
    @Persisted
    private FluidStack lockedFluid = FluidStack.EMPTY;
    private TickableSubscription discountSubscription;

    public MegaNaquadahReactorMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    protected RecipeLogic createRecipeLogic(Object... args) {
        return new MegaNaquadahReactorRecipeTrait(this);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        discountSubscription = subscribeServerTick(discountSubscription, this::tickDiscount);
    }

    @Override
    public void onUnload() {
        if (discountSubscription != null) {
            unsubscribe(discountSubscription);
            discountSubscription = null;
        }
        super.onUnload();
    }

    private void tickDiscount() {
        if (recipeLogic.isWorking()) {
            runTimeTicks = Math.min(MegaNaquadahReactorLogic.TICKS_TO_MAX_DISCOUNT, runTimeTicks + 1);
        } else {
            runTimeTicks = Math.max(0, runTimeTicks - MegaNaquadahReactorLogic.IDLE_DECAY_PER_TICK);
        }
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (!recipeLogic.isWorking() || getOffsetTimer() % 67 != 0) return;
        Direction front = getFrontFacing();
        Direction back = RelativeDirection.BACK.getRelative(front, getUpwardsFacing(), isFlipped());
        Direction up = RelativeDirection.UP.getRelative(front, getUpwardsFacing(), isFlipped());
        double centerX = getPos().getX() + 0.5 + back.getStepX() * 15.0 + up.getStepX() * 12.0;
        double centerY = getPos().getY() + 0.5 + back.getStepY() * 15.0 + up.getStepY() * 12.0;
        double centerZ = getPos().getZ() + 0.5 + back.getStepZ() * 15.0 + up.getStepZ() * 12.0;
        double goldenAngle = Math.PI * (3.0 - Math.sqrt(5.0));
        for (int i = 0; i < 400; i++) {
            double y = 1.0 - (2.0 * i + 1.0) / 400.0;
            double ring = Math.sqrt(Math.max(0.0, 1.0 - y * y));
            double theta = goldenAngle * i;
            double dx = Math.cos(theta) * ring * 6.0;
            double dy = y * 6.0;
            double dz = Math.sin(theta) * ring * 6.0;
            getLevel().addParticle(ParticleTypes.PORTAL, centerX + dx, centerY + dy, centerZ + dz,
                    -dx / 160.0, -dy / 160.0, -dz / 160.0);
        }
    }

    @Override
    public EnergyContainerList getEnergyContainer() {
        List<IEnergyContainer> outputs = new ArrayList<>();
        for (IRecipeHandler<?> handler : getCapabilitiesFlat(IO.OUT, EURecipeCapability.CAP)) {
            if (handler instanceof IEnergyContainer container) outputs.add(container);
        }
        return new EnergyContainerList(outputs);
    }

    public long getRunTimeTicks() {
        return runTimeTicks;
    }

    public void cacheRun(int baseOutput, int parallel, int coolantEfficiency, int excitedMultiplier,
                         FluidStack lockedFluid, long currentOutput) {
        this.baseOutput = baseOutput;
        this.parallel = parallel;
        this.coolantEfficiency = coolantEfficiency;
        this.excitedMultiplier = excitedMultiplier;
        this.lockedFluid = lockedFluid == null ? FluidStack.EMPTY : lockedFluid.copy();
        this.currentOutput = currentOutput;
    }

    public void clearRunCache() {
        baseOutput = 0;
        parallel = 1;
        coolantEfficiency = 100;
        excitedMultiplier = 1;
        lockedFluid = FluidStack.EMPTY;
        currentOutput = 0;
    }

    @Override
    public void onStructureInvalid() {
        clearRunCache();
        super.onStructureInvalid();
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (!isFormed()) return;
        long seconds = runTimeTicks / 20;
        textList.add(Component.translatable("tstmodern.machine.mega_naquadah_reactor.status.running_time",
                String.format("%02d:%02d:%02d", seconds / 3600, seconds % 3600 / 60, seconds % 60))
                .withStyle(ChatFormatting.AQUA));
        textList.add(Component.translatable("tstmodern.machine.mega_naquadah_reactor.status.discount",
                MegaNaquadahReactorLogic.discountPercent(runTimeTicks)).withStyle(ChatFormatting.LIGHT_PURPLE));
        textList.add(Component.translatable("tstmodern.machine.mega_naquadah_reactor.status.output",
                currentOutput).withStyle(ChatFormatting.GOLD));
        if (recipeLogic.isActive()) {
            textList.add(Component.translatable("tstmodern.machine.mega_naquadah_reactor.status.modifiers",
                    parallel, coolantEfficiency, excitedMultiplier).withStyle(ChatFormatting.GREEN));
        }
    }
}
