package com.tstmodern.machine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.IOpticalComputationProvider;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockDisplayText;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.transfer.fluid.FluidHandlerList;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.tstmodern.machine.logic.AstralComputingLogic;
import com.tstmodern.registry.TSTMaterials;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

import org.jetbrains.annotations.NotNull;

public final class AstralComputingArrayMachine extends WorkableElectricMultiblockMachine
                                               implements IOpticalComputationProvider {
    private static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            AstralComputingArrayMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    @DescSynced
    private int overclockPercent = 100;
    @Persisted
    @DescSynced
    private int overvoltPercent = 100;
    @DescSynced
    private int availableCWUt;
    @DescSynced
    private long currentEUt;
    @DescSynced
    private boolean computing;
    private int potentialCWUt;
    private int allocatedCWUt;
    private AstralComputationRackMachine rack;
    private IFluidHandler coolantHandler = new FluidHandlerList(List.of());
    private TickableSubscription tickSubscription;

    public AstralComputingArrayMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        tickSubscription = subscribeServerTick(tickSubscription, this::tickAstral);
    }

    @Override
    public void onUnload() {
        if (tickSubscription != null) {
            unsubscribe(tickSubscription);
            tickSubscription = null;
        }
        super.onUnload();
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        rack = null;
        List<IFluidHandler> fluidHandlers = new ArrayList<>();
        for (IMultiPart part : getParts()) {
            if (part instanceof AstralComputationRackMachine astralRack) rack = astralRack;
            for (var handlerList : part.getRecipeHandlers()) {
                if (!handlerList.isValid(IO.IN)) continue;
                handlerList.getCapability(FluidRecipeCapability.CAP).stream()
                        .filter(IFluidHandler.class::isInstance)
                        .map(IFluidHandler.class::cast)
                        .forEach(fluidHandlers::add);
            }
        }
        coolantHandler = new FluidHandlerList(fluidHandlers);
    }

    @Override
    public void onStructureInvalid() {
        stopComputing();
        rack = null;
        coolantHandler = new FluidHandlerList(List.of());
        super.onStructureInvalid();
    }

    private void tickAstral() {
        allocatedCWUt = 0;
        if (!isFormed() || rack == null || !recipeLogic.isWorkingEnabled()) {
            stopComputing();
            return;
        }

        if (getOffsetTimer() % 20 == 0) {
            rack.passiveCool();
            int rackComputation = rack.tick(overclock(), overvolt(), () -> getLevel().random.nextDouble());
            var coolant = findCoolant();
            var cooling = AstralComputingLogic.coolWithFluid(rack.getHeat(), coolant.amount(), coolant.coefficient());
            if (cooling.fluidUsed() > 0) {
                coolantHandler.drain(new FluidStack(coolant.fluid(), cooling.fluidUsed()), FluidAction.EXECUTE);
            }
            rack.setHeat(cooling.heat());
            rack.destroyOverheatedComponents();
            potentialCWUt = (int) Math.min(Integer.MAX_VALUE,
                    rackComputation * (long) (cooling.multiplier() * cooling.multiplier()));
            currentEUt = potentialCWUt > 0
                    ? AstralComputingLogic.requiredEUt(GTValues.V[GTValues.UV], overclock(), overvolt(),
                            cooling.multiplier())
                    : (long) (GTValues.V[GTValues.UV] * overclock() * overvolt());
            if (rack.getHeat() > 40_960_000 && getLevel().isEmptyBlock(rack.getPos().above())) {
                getLevel().setBlockAndUpdate(rack.getPos().above(), Blocks.FIRE.defaultBlockState());
            }
        }

        computing = currentEUt > 0 && energyContainer != null && energyContainer.getEnergyStored() >= currentEUt &&
                energyContainer.removeEnergy(currentEUt) == currentEUt;
        availableCWUt = computing ? potentialCWUt : 0;
        recipeLogic.setStatus(computing ? RecipeLogic.Status.WORKING : RecipeLogic.Status.WAITING);
    }

    private Coolant findCoolant() {
        net.minecraft.world.level.material.Fluid selected = null;
        double selectedCoefficient = -1;
        int amount = 0;
        for (int tank = 0; tank < coolantHandler.getTanks(); tank++) {
            FluidStack stack = coolantHandler.getFluidInTank(tank);
            double coefficient = coolantCoefficient(stack);
            if (selected == null && coefficient > 0) {
                selected = stack.getFluid();
                selectedCoefficient = coefficient;
            }
            if (selected != null && stack.getFluid() == selected) amount += stack.getAmount();
        }
        return selected == null ? Coolant.NONE : new Coolant(selected, amount, selectedCoefficient);
    }

    private static double coolantCoefficient(FluidStack stack) {
        if (stack.isEmpty()) return -1;
        if (stack.getFluid() == GTMaterials.PCBCoolant.getFluid()) return 0.001;
        if (stack.getFluid() == TSTMaterials.SUPER_COOLANT.getFluid()) return 0.01;
        if (stack.getFluid() == GTMaterials.Helium.getFluid()) return 0.1;
        return -1;
    }

    private double overclock() {
        return overclockPercent / 100.0;
    }

    private double overvolt() {
        return overvoltPercent / 100.0;
    }

    private void stopComputing() {
        computing = false;
        availableCWUt = 0;
        allocatedCWUt = 0;
        currentEUt = 0;
    }

    public boolean isComputing() {
        return computing;
    }

    @Override
    public int requestCWUt(int cwut, boolean simulate, @NotNull Collection<IOpticalComputationProvider> seen) {
        seen.add(this);
        int supplied = Math.max(0, Math.min(cwut, availableCWUt - allocatedCWUt));
        if (!simulate) allocatedCWUt += supplied;
        return supplied;
    }

    @Override
    public int getMaxCWUt(@NotNull Collection<IOpticalComputationProvider> seen) {
        seen.add(this);
        return availableCWUt;
    }

    @Override
    public boolean canBridge(@NotNull Collection<IOpticalComputationProvider> seen) {
        seen.add(this);
        return true;
    }

    @Override
    protected InteractionResult onScrewdriverClick(
            Player player, InteractionHand hand, Direction side, BlockHitResult hitResult) {
        if (getLevel().isClientSide) return InteractionResult.SUCCESS;
        if (player.isShiftKeyDown()) {
            overvoltPercent = overvoltPercent >= 200 ? 70 : overvoltPercent + 10;
        } else {
            overclockPercent = overclockPercent >= 300 ? 0 : overclockPercent + 25;
        }
        player.sendSystemMessage(Component.translatable("tstmodern.machine.astral_computing_array.settings",
                overclockPercent, overvoltPercent).withStyle(ChatFormatting.AQUA));
        return InteractionResult.CONSUME;
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        MultiblockDisplayText.builder(textList, isFormed())
                .setWorkingStatus(recipeLogic.isWorkingEnabled(), computing)
                .addCustom(lines -> {
                    if (!isFormed()) return;
                    lines.add(Component.translatable("tstmodern.machine.astral_computing_array.status.computation",
                            availableCWUt).withStyle(ChatFormatting.AQUA));
                    lines.add(Component.translatable("tstmodern.machine.astral_computing_array.status.energy",
                            currentEUt).withStyle(ChatFormatting.GRAY));
                    lines.add(Component.translatable("tstmodern.machine.astral_computing_array.status.heat",
                            rack == null ? 0 : rack.getHeat()).withStyle(ChatFormatting.RED));
                    lines.add(Component.translatable("tstmodern.machine.astral_computing_array.settings",
                            overclockPercent, overvoltPercent).withStyle(ChatFormatting.YELLOW));
                })
                .addWorkingStatusLine();
    }

    private record Coolant(net.minecraft.world.level.material.Fluid fluid, int amount, double coefficient) {
        private static final Coolant NONE = new Coolant(net.minecraft.world.level.material.Fluids.EMPTY, 0, -1);
    }
}
