package com.tstmodern.machine;

import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.tstmodern.registry.TSTRecipeTypes;

import java.util.ArrayList;
import java.util.List;

/**
 * Behavioural port of TST_MegaTreeFarm (Eco-Sphere Simulator).
 *
 * <p>The original TST tier multiplier controls the yield of one run. A GTCEu
 * Parallel Control Hatch controls how many of those runs happen simultaneously.</p>
 */
public final class MegaTreeFarmMachine extends WorkableElectricMultiblockMachine {

    private static final long TREE_WATER_PER_RUN = 1_000L;
    private static final long AQUATIC_WATER_PER_RUN = 10_000L;

    public enum OutputMode {
        LOG,
        SAPLING,
        LEAVES,
        FRUIT
    }

    private record ProcessingProfile(
            int parallel,
            long tierMultiplier,
            long outputMultiplier,
            long totalFluidCost,
            long totalEuPerTick,
            int duration,
            int independentChanceRolls) {}

    public MegaTreeFarmMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    /** Applies one source-accurate TST run, then expands it through the parallel hatch. */
    public static ModifierFunction recipeModifier(MetaMachine machine, GTRecipe recipe) {
        if (!(machine instanceof MegaTreeFarmMachine treeFarm)) {
            return ModifierFunction.NULL;
        }

        boolean aquatic = recipe.recipeType == TSTRecipeTypes.AQUATIC_ZONE_SIMULATOR;
        ProcessingProfile oneRun = aquatic ?
                aquaticProfile(treeFarm.getTier(), 0, 1) :
                treeProfile(treeFarm.getTier(), 0, 1);

        long recipeEUt = RecipeHelper.getRealEUtWithIO(recipe).getTotalEU();
        if (recipeEUt <= 0L || recipe.duration <= 0) {
            return ModifierFunction.NULL;
        }

        GTRecipe singleRun = ModifierFunction.builder()
                .outputModifier(ContentModifier.multiplier(oneRun.tierMultiplier()))
                .eutMultiplier((double) oneRun.totalEuPerTick() / recipeEUt)
                .durationMultiplier((double) oneRun.duration() / recipe.duration)
                .build()
                .apply(recipe);
        if (singleRun == null) {
            return ModifierFunction.NULL;
        }

        // Only TST's liquid cost grows with voltage tier. Item resources remain
        // one-per-run and selectors (chance 0 / notConsumable) remain untouched.
        long baseFluid = aquatic ? 10_000L : 1_000L;
        scaleContents(singleRun.inputs, FluidRecipeCapability.CAP,
                ContentModifier.multiplier((double) oneRun.totalFluidCost() / baseFluid), false);

        // The generic output modifier intentionally skips chanced contents. TST's
        // aquatic yields still need the tier multiplier before chance rolls occur.
        if (aquatic) {
            scaleContents(singleRun.outputs, null,
                    ContentModifier.multiplier(oneRun.tierMultiplier()), true);
        }

        int requestedParallel = treeFarm.getParallelHatch()
                .map(part -> part.getCurrentParallel())
                .orElse(1);
        requestedParallel = Math.max(1, requestedParallel);
        int parallel = ParallelLogic.getParallelAmount(machine, singleRun, requestedParallel);
        if (parallel <= 0) {
            return ModifierFunction.NULL;
        }

        ModifierFunction hatchRuns = ModifierFunction.builder()
                .inputModifier(ContentModifier.multiplier(parallel))
                .outputModifier(ContentModifier.multiplier(parallel))
                .eutMultiplier(parallel)
                .parallels(parallel)
                .build();
        return ignored -> hatchRuns.apply(singleRun);
    }

    public static int baseOutputAmount(OutputMode mode) {
        return switch (mode) {
            case LOG -> 20;
            case SAPLING -> 3;
            case LEAVES -> 8;
            case FRUIT -> 1;
        };
    }

    private static long tierMultiplier(int tier) {
        if (tier < 1) {
            return 1L;
        }
        double exponent = 0.1D * (tier - 1D) *
                (8D + Math.log(25D + Math.exp(25D - tier)) / Math.log(5D));
        double result = Math.floor(3D * Math.pow(2D, exponent));
        return result >= Long.MAX_VALUE ? Long.MAX_VALUE : (long) result;
    }

    private static ProcessingProfile treeProfile(int voltageTier, int controllerTier, int hatchParallel) {
        return profile(voltageTier, controllerTier, hatchParallel, TREE_WATER_PER_RUN);
    }

    private static ProcessingProfile aquaticProfile(int voltageTier, int controllerTier, int hatchParallel) {
        return profile(voltageTier, controllerTier, hatchParallel, AQUATIC_WATER_PER_RUN);
    }

    private static ProcessingProfile profile(
            int voltageTier, int controllerTier, int hatchParallel, long baseFluidPerRun) {
        int parallel = Math.max(1, hatchParallel);
        long tierMultiplier = tierMultiplier(voltageTier);
        long fluidPerRun = saturatingMultiply(baseFluidPerRun / (controllerTier > 0 ? 10L : 1L),
                saturatingPower(2L, voltageTier));
        long euPerRun = sourceEuPerTick(voltageTier);

        return new ProcessingProfile(
                parallel,
                tierMultiplier,
                saturatingMultiply(tierMultiplier, parallel),
                saturatingMultiply(fluidPerRun, parallel),
                saturatingMultiply(euPerRun, parallel),
                controllerTier > 0 ? 20 : 100,
                parallel);
    }

    private static long sourceEuPerTick(int voltageTier) {
        long scaled = saturatingMultiply(8L, saturatingPower(4L, voltageTier));
        return saturatingMultiply(scaled, 15L) / 16L;
    }

    private static long saturatingPower(long base, int exponent) {
        if (exponent <= 0) {
            return 1L;
        }
        long result = 1L;
        for (int i = 0; i < exponent; i++) {
            result = saturatingMultiply(result, base);
        }
        return result;
    }

    private static long saturatingMultiply(long left, long right) {
        if (left == 0L || right == 0L) {
            return 0L;
        }
        if (left > Long.MAX_VALUE / right) {
            return Long.MAX_VALUE;
        }
        return left * right;
    }

    private static void scaleContents(
            java.util.Map<RecipeCapability<?>, List<Content>> contents,
            RecipeCapability<?> onlyCapability,
            ContentModifier modifier,
            boolean onlyChanced) {
        for (var entry : new ArrayList<>(contents.entrySet())) {
            RecipeCapability<?> capability = entry.getKey();
            if (onlyCapability != null && capability != onlyCapability) {
                continue;
            }
            List<Content> scaled = new ArrayList<>(entry.getValue().size());
            for (Content content : entry.getValue()) {
                if (onlyChanced && !content.isChanced()) {
                    scaled.add(content);
                } else if (onlyChanced) {
                    scaled.add(content.copyChanced(capability, modifier));
                } else {
                    scaled.add(content.copy(capability, modifier));
                }
            }
            contents.put(capability, scaled);
        }
    }
}
