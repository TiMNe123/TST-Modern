package com.tstmodern.machine.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;
import java.util.Map;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.category.GTRecipeCategory;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.api.recipe.ingredient.EnergyStack;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;

import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.crafting.Ingredient;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class BigBroArrayRecipeModifiersTest {

    @BeforeAll
    static void bootstrap() throws Exception {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();

        try {
            sun.misc.Unsafe unsafe;
            java.lang.reflect.Field unsafeField = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            unsafe = (sun.misc.Unsafe) unsafeField.get(null);

            net.minecraftforge.fml.ModContainer container = (net.minecraftforge.fml.ModContainer) unsafe.allocateInstance(
                    Class.forName("net.minecraftforge.fml.javafmlmod.FMLModContainer", true, BigBroArrayRecipeModifiersTest.class.getClassLoader())
            );
            java.lang.reflect.Field modIdField = net.minecraftforge.fml.ModContainer.class.getDeclaredField("modId");
            modIdField.setAccessible(true);
            modIdField.set(container, "gtceu");

            net.minecraftforge.fml.ModList mockModList = (net.minecraftforge.fml.ModList) unsafe.allocateInstance(net.minecraftforge.fml.ModList.class);
            java.lang.reflect.Field indexedModsField = net.minecraftforge.fml.ModList.class.getDeclaredField("indexedMods");
            indexedModsField.setAccessible(true);
            indexedModsField.set(mockModList, Map.of("gtceu", container));

            java.lang.reflect.Field modListField = net.minecraftforge.fml.ModList.class.getDeclaredField("INSTANCE");
            modListField.setAccessible(true);
            modListField.set(null, mockModList);

            net.minecraftforge.fml.ModLoadingContext.get().setActiveContainer(container);
        } catch (Throwable e) {
            // If already initialized, ignore
        }
    }

    @Test
    void nullAndEmptyInputsReturnNullModifier() {
        assertSame(ModifierFunction.NULL, BigBroArrayRecipeModifiers.recipeModifier(
                null, null, 0, GTValues.LV, BigBroArrayMode.PROCESSOR, 0, 0, 0));
        assertSame(ModifierFunction.NULL, BigBroArrayRecipeModifiers.recipeModifier(
                null, null, -1, GTValues.LV, BigBroArrayMode.PROCESSOR, 0, 0, 0));
        assertSame(ModifierFunction.NULL, BigBroArrayRecipeModifiers.recipeModifier(
                null, null, 0, GTValues.LV, BigBroArrayMode.GENERATOR, 0, 0, 0));
        assertSame(ModifierFunction.NULL, BigBroArrayRecipeModifiers.recipeModifier(
                null, null, -5, GTValues.LV, BigBroArrayMode.GENERATOR, 0, 0, 0));
    }

    @Test
    void invalidTierGuardsReturnNullModifier() {
        // Out-of-bounds tiers (< ULV or > MAX) must safely return ModifierFunction.NULL
        assertSame(ModifierFunction.NULL, BigBroArrayRecipeModifiers.processorModifier(
                null, null, 16, -1, 0, 0, 0));
        assertSame(ModifierFunction.NULL, BigBroArrayRecipeModifiers.processorModifier(
                null, null, 16, GTValues.MAX + 1, 0, 0, 0));
    }

    @Test
    void processorRejectsBaseVoltageAboveEmbeddedTier() {
        // Recipe requires HV (128 EU/t base voltage). If embedded tier is LV (32 EU/t max), must reject.
        GTRecipe hvRecipe = createMockRecipe(128, 100, 1, 1);
        ModifierFunction modifier = BigBroArrayRecipeModifiers.processorModifier(
                null, hvRecipe, 16, GTValues.LV, 0, 0, 0);
        assertSame(ModifierFunction.NULL, modifier);
    }

    @Test
    void processorAppliesOverclockAndParallelAndDiscountAndSpeed() {
        // Recipe: 30 EU/t (LV tier), duration 100 ticks.
        // Embedded tier: MV (128V) -> 1 regular OC step:
        //   voltage * 4 = 120 EU/t, duration / 2 = 50 ticks
        // Array parameters: machineCount = 4, parallelCasingTier = 1 (MK1), addonCount = 1, coilTier = 1 (Cupronickel)
        //   With machine = null, ParallelLogic.getParallelAmount returns 1
        //   coil discount (tier 1): 0.9
        //   speed durationMultiplier (tier 1): 0.66
        // Expected outputs:
        //   duration = (int)(50 * 0.66) = 33 ticks
        //   EUt = (long)(120 * 1 * 0.9) = 108 EU/t
        GTRecipe lvRecipe = createMockRecipe(30, 100, 1, 1);
        ModifierFunction modifier = BigBroArrayRecipeModifiers.processorModifier(
                null, lvRecipe, 4, GTValues.MV, 1, 1, 1);

        GTRecipe result = modifier.apply(lvRecipe);
        assertNotNull(result, "Modified recipe must not be null");
        assertEquals(33, result.duration, "Duration after OC and speed multiplier");
        assertEquals(108, result.getInputEUt().voltage(), "Voltage after OC, parallel (1), and coil discount (0.9)");
    }

    @Test
    void generatorPreservesDurationRegardlessOfCoilOrParallelCasing() {
        // Generator recipe: duration 200 ticks, output 32 EU/t
        // Array: 8 machines, MK3 casing (tier 3), 2 addons -> parallel = min(8 << 3, 3072) = 64
        // With machine = null, ParallelLogic.getParallelAmount returns 1
        GTRecipe genRecipe = createMockGeneratorRecipe(32, 200, 1);
        ModifierFunction modifier = BigBroArrayRecipeModifiers.generatorModifier(
                null, genRecipe, 8, 3, 2);

        GTRecipe result = modifier.apply(genRecipe);
        assertNotNull(result);
        assertEquals(200, result.duration, "Generator duration must remain unchanged");
        assertEquals(32, result.getOutputEUt().voltage(), "Generator output EU when parallelAmount=1");
    }

    private static GTRecipe createMockRecipe(long eut, int duration, int inputItems, int outputItems) {
        int maxChance = com.gregtechceu.gtceu.api.recipe.chance.logic.ChanceLogic.getMaxChancedValue();
        return new GTRecipe(
                GTRecipeTypes.DUMMY_RECIPES,
                new net.minecraft.resources.ResourceLocation("tstmodern", "mock_proc"),
                Map.of(ItemRecipeCapability.CAP, List.of(new Content(new MockIngredient(inputItems), maxChance, maxChance, 0))),
                Map.of(ItemRecipeCapability.CAP, List.of(new Content(new MockIngredient(outputItems), maxChance, maxChance, 0))),
                Map.of(EURecipeCapability.CAP, List.of(new Content(new EnergyStack(eut), maxChance, maxChance, 0))),
                Map.of(),
                Map.of(), Map.of(), Map.of(), Map.of(),
                List.of(), List.of(), new CompoundTag(),
                duration, GTRecipeCategory.DEFAULT);
    }

    private static GTRecipe createMockGeneratorRecipe(long outputEut, int duration, int inputItems) {
        int maxChance = com.gregtechceu.gtceu.api.recipe.chance.logic.ChanceLogic.getMaxChancedValue();
        return new GTRecipe(
                GTRecipeTypes.DUMMY_RECIPES,
                new net.minecraft.resources.ResourceLocation("tstmodern", "mock_gen"),
                Map.of(ItemRecipeCapability.CAP, List.of(new Content(new MockIngredient(inputItems), maxChance, maxChance, 0))),
                Map.of(),
                Map.of(),
                Map.of(EURecipeCapability.CAP, List.of(new Content(new EnergyStack(outputEut), maxChance, maxChance, 0))),
                Map.of(), Map.of(), Map.of(), Map.of(),
                List.of(), List.of(), new CompoundTag(),
                duration, GTRecipeCategory.DEFAULT);
    }

    @SuppressWarnings("unused")
    private static class MockIngredient extends Ingredient {
        int amount;

        MockIngredient(int amount) {
            super(java.util.stream.Stream.empty());
            this.amount = amount;
        }
    }
}

