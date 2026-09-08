package com.tstmodern.recipe.meganaquadah;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeHandler;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;
import com.tstmodern.TSTModern;
import com.tstmodern.config.TSTConfig;
import com.tstmodern.machine.MegaNaquadahReactorMachine;
import com.tstmodern.machine.logic.MegaNaquadahReactorLogic;
import com.tstmodern.registry.TSTMaterials;
import com.tstmodern.registry.TSTRecipeTypes;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

public final class MegaNaquadahReactorRecipeLogic implements GTRecipeType.ICustomRecipeLogic {
    private static final ResourceLocation RUNTIME_ID = TSTModern.id("runtime/mega_naquadah_reactor");

    private record Fuel(String id, Material input, Material output, int duration, int euPerTick) {}
    private record Modifier(Material fluid, int amountPerSecond, int value) {}

    @Override
    public GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder) {
        if (!(holder instanceof MegaNaquadahReactorMachine machine) || !machine.isFormed()) return null;
        machine.clearRunCache();
        Map<Fluid, Long> fluids = readFluids(holder);
        Fuel fuel = fuels().stream().filter(candidate -> amount(fluids, candidate.input()) > 0).findFirst().orElse(null);
        if (fuel == null) return null;

        int parallel = MegaNaquadahReactorLogic.parallelLimit(
                TSTConfig.MEGA_NAQUADAH_REACTOR_MAX_PARALLEL.get(), amount(fluids, fuel.input()), 1);
        if (parallel <= 0) return null;

        Modifier excited = firstAvailable(fluids, excited(), false, parallel, machine.getRunTimeTicks());
        Modifier coolant = firstAvailable(fluids, coolants(), true, parallel, machine.getRunTimeTicks());
        int excitedMultiplier = excited == null ? 1 : excited.value();
        int coolantEfficiency = coolant == null ? 100 : coolant.value();
        int air = MegaNaquadahReactorLogic.totalFluidAmount(
                MegaNaquadahReactorLogic.LIQUID_AIR_PER_SECOND, parallel, fuel.duration());
        int coolantAmount = totalModifierAmount(coolant, parallel, fuel.duration(), machine.getRunTimeTicks());
        int excitedAmount = totalModifierAmount(excited, parallel, fuel.duration(), machine.getRunTimeTicks());
        if (air < 0 || coolantAmount < 0 || excitedAmount < 0) return null;
        if (amount(fluids, GTMaterials.LiquidAir) < air ||
                coolant != null && amount(fluids, coolant.fluid()) < coolantAmount ||
                excited != null && amount(fluids, excited.fluid()) < excitedAmount) return null;

        long output = MegaNaquadahReactorLogic.outputEuPerTick(
                fuel.euPerTick(), coolantEfficiency, excitedMultiplier, parallel);
        List<FluidStack> inputs = new ArrayList<>(4);
        inputs.add(fuel.input().getFluid(parallel));
        inputs.add(GTMaterials.LiquidAir.getFluid(air));
        if (coolant != null) inputs.add(coolant.fluid().getFluid(coolantAmount));
        if (excited != null) inputs.add(excited.fluid().getFluid(excitedAmount));

        machine.cacheRun(fuel.euPerTick(), parallel, coolantEfficiency, excitedMultiplier,
                excited == null ? FluidStack.EMPTY : excited.fluid().getFluid(excited.amountPerSecond()), output);
        GTRecipe recipe = new GTRecipeBuilder(RUNTIME_ID, TSTRecipeTypes.MEGA_NAQUADAH_REACTOR_FUELS)
                .inputFluids(inputs.toArray(FluidStack[]::new))
                .outputFluids(fuel.output().getFluid(parallel))
                .duration(fuel.duration())
                .EUt(-output)
                .buildRawRecipe();
        recipe.id = RUNTIME_ID;
        return recipe;
    }

    @Override
    public void buildRepresentativeRecipes() {
        for (Fuel fuel : fuels()) {
            ResourceLocation id = TSTModern.id("mega_naquadah_reactor_fuels/" + fuel.id());
            GTRecipe recipe = new GTRecipeBuilder(id, TSTRecipeTypes.MEGA_NAQUADAH_REACTOR_FUELS)
                    .inputFluids(fuel.input().getFluid(1))
                    .outputFluids(fuel.output().getFluid(1))
                    .duration(fuel.duration())
                    .EUt(-fuel.euPerTick())
                    .buildRawRecipe();
            recipe.id = id;
            TSTRecipeTypes.MEGA_NAQUADAH_REACTOR_FUELS.addToMainCategory(recipe);
        }
    }

    private static Modifier firstAvailable(Map<Fluid, Long> stored, List<Modifier> tiers, boolean scaleParallel,
                                            int parallel, long runTimeTicks) {
        for (Modifier tier : tiers) {
            int perSecond = MegaNaquadahReactorLogic.discountedPerSecond(tier.amountPerSecond(), runTimeTicks);
            long required = scaleParallel ? (long) perSecond * parallel : tier.amountPerSecond();
            if (amount(stored, tier.fluid()) >= required) return tier;
        }
        return null;
    }

    private static int totalModifierAmount(Modifier modifier, int parallel, int duration, long runTimeTicks) {
        if (modifier == null) return 0;
        return MegaNaquadahReactorLogic.totalFluidAmount(
                MegaNaquadahReactorLogic.discountedPerSecond(modifier.amountPerSecond(), runTimeTicks),
                parallel, duration);
    }

    private static Map<Fluid, Long> readFluids(IRecipeCapabilityHolder holder) {
        Map<Fluid, Long> result = new IdentityHashMap<>();
        for (IRecipeHandler<?> handler : holder.getCapabilitiesFlat(IO.IN, FluidRecipeCapability.CAP)) {
            for (Object content : handler.getContents()) {
                if (content instanceof FluidStack stack && !stack.isEmpty()) {
                    result.merge(stack.getFluid(), (long) stack.getAmount(), Long::sum);
                }
            }
        }
        return result;
    }

    private static long amount(Map<Fluid, Long> stored, Material material) {
        return stored.getOrDefault(material.getFluid(), 0L);
    }

    private static List<Fuel> fuels() {
        return List.of(
                new Fuel("uranium", TSTMaterials.URANIUM_BASED_LIQUID_FUEL_EXCITED,
                        TSTMaterials.URANIUM_BASED_LIQUID_FUEL_DEPLETED, 100, 12_960),
                new Fuel("thorium", TSTMaterials.THORIUM_BASED_LIQUID_FUEL_EXCITED,
                        TSTMaterials.THORIUM_BASED_LIQUID_FUEL_DEPLETED, 500, 2_200),
                new Fuel("plutonium", TSTMaterials.PLUTONIUM_BASED_LIQUID_FUEL_EXCITED,
                        TSTMaterials.PLUTONIUM_BASED_LIQUID_FUEL_DEPLETED, 150, 32_400),
                new Fuel("mki", TSTMaterials.NAQUADAH_BASED_FUEL_MKI,
                        TSTMaterials.NAQUADAH_BASED_FUEL_MKI_DEPLETED, 60, 975_000),
                new Fuel("mkii", TSTMaterials.NAQUADAH_BASED_FUEL_MKII,
                        TSTMaterials.NAQUADAH_BASED_FUEL_MKII_DEPLETED, 70, 2_300_000),
                new Fuel("mkiii", TSTMaterials.NAQUADAH_BASED_FUEL_MKIII,
                        TSTMaterials.NAQUADAH_BASED_FUEL_MKIII_DEPLETED, 80, 9_511_000),
                new Fuel("mkiv", TSTMaterials.NAQUADAH_BASED_FUEL_MKIV,
                        TSTMaterials.NAQUADAH_BASED_FUEL_MKIV_DEPLETED, 100, 88_540_000),
                new Fuel("mkv", TSTMaterials.NAQUADAH_BASED_FUEL_MKV,
                        TSTMaterials.NAQUADAH_BASED_FUEL_MKV_DEPLETED, 160, 399_576_000),
                new Fuel("mkvi", TSTMaterials.NAQUADAH_BASED_FUEL_MKVI,
                        TSTMaterials.NAQUADAH_BASED_FUEL_MKVI_DEPLETED, 240, 2_077_795_200));
    }

    private static List<Modifier> excited() {
        return List.of(
                new Modifier(TSTMaterials.SPACE, 20, 64),
                new Modifier(TSTMaterials.ATOMIC_SEPARATION_CATALYST, 20, 16),
                new Modifier(GTMaterials.Naquadah, 20, 4),
                new Modifier(GTMaterials.Uranium235, 180, 3),
                new Modifier(GTMaterials.Caesium, 180, 2));
    }

    private static List<Modifier> coolants() {
        return List.of(
                new Modifier(TSTMaterials.TIME, 20, 500),
                new Modifier(TSTMaterials.CRYOTHEUM, 1_000, 275),
                new Modifier(TSTMaterials.SUPER_COOLANT, 1_000, 150),
                new Modifier(TSTMaterials.COOLANT, 1_000, 105));
    }
}
