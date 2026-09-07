package com.tstmodern.recipe.starcore;

import java.util.ArrayList;
import java.util.List;

import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.data.worldgen.GTOreDefinition;
import com.gregtechceu.gtceu.api.data.worldgen.generator.VeinGenerator;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;
import com.tstmodern.machine.StarcoreMinerMachine;
import com.tstmodern.machine.logic.StarcoreMinerLogic;
import com.tstmodern.registry.TSTRecipeTypes;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class StarcoreMinerRecipeLogic implements GTRecipeType.ICustomRecipeLogic {
    @Override
    public GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder) {
        if (!(holder instanceof StarcoreMinerMachine machine) || !machine.isFormed()) {
            return null;
        }
        Level level = machine.getLevel();
        if (level == null) {
            return null;
        }

        List<StarcoreMinerLogic.WeightedEntry<ItemStack>> rawEntries = new ArrayList<>();
        for (GTOreDefinition def : GTRegistries.ORE_VEINS) {
            if (def.dimensionFilter().isEmpty() || def.dimensionFilter().contains(level.dimension())) {
                VeinGenerator generator = def.veinGenerator();
                if (generator == null) continue;
                for (VeinGenerator.VeinEntry entry : generator.getAllEntries()) {
                    ItemStack stack = entry.map(
                            state -> new ItemStack(state.getBlock().asItem()),
                            material -> ChemicalHelper.get(TagPrefix.ore, material));
                    if (stack.isEmpty()) continue;
                    int weight = StarcoreMinerLogic.positiveWeightProduct(def.weight(), entry.chance());
                    rawEntries.add(new StarcoreMinerLogic.WeightedEntry<>(stack, weight));
                }
            }
        }
        if (rawEntries.isEmpty()) {
            return null;
        }

        List<StarcoreMinerLogic.WeightedEntry<ItemStack>> aggregated =
                StarcoreMinerLogic.aggregateItemStackWeights(rawEntries);
        int totalWeight = StarcoreMinerLogic.totalWeight(aggregated);
        if (totalWeight <= 0) {
            return null;
        }

        int stackSize = StarcoreMinerLogic.boostedStackSize(machine.getBoosterCount());
        ItemStack[] outputs = new ItemStack[StarcoreMinerLogic.OUTPUT_STACKS];
        for (int i = 0; i < StarcoreMinerLogic.OUTPUT_STACKS; i++) {
            int roll = level.random.nextInt(totalWeight);
            ItemStack chosen = StarcoreMinerLogic.select(aggregated, roll).copy();
            chosen.setCount(stackSize);
            outputs[i] = chosen;
        }

        ResourceLocation id = new ResourceLocation("tstmodern", "runtime/starcore_miner/" + System.nanoTime());
        GTRecipeBuilder builder = new GTRecipeBuilder(id, TSTRecipeTypes.STARCORE_MINING)
                .duration(StarcoreMinerLogic.DURATION)
                .EUt(StarcoreMinerLogic.EUT)
                .outputItems(outputs);
        GTRecipe recipe = builder.buildRawRecipe();
        recipe.id = id;
        return recipe;
    }
}
