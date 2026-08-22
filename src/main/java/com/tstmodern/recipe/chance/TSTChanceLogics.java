package com.tstmodern.recipe.chance;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.recipe.chance.boost.ChanceBoostFunction;
import com.gregtechceu.gtceu.api.recipe.chance.logic.ChanceLogic;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.tstmodern.machine.logic.NetherInterfaceLogic;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.network.chat.Component;

/** TST-specific chanced output semantics registered during GTCEu's chance-logic event. */
public final class TSTChanceLogics {

    public static ChanceLogic THREE_WEIGHTED_SCALED;
    public static ChanceLogic SINGLE_ROLL_SCALED;

    private TSTChanceLogics() {}

    public static void registerChanceLogics(GTCEuAPI.RegisterEvent<String, ChanceLogic> event) {
        // ChanceLogic registers itself in its constructor while GTCEu's registry is open.
        THREE_WEIGHTED_SCALED = new ThreeWeightedScaledChanceLogic();
        SINGLE_ROLL_SCALED = new SingleRollScaledChanceLogic();
    }

    private static final class ThreeWeightedScaledChanceLogic extends ChanceLogic {

        private ThreeWeightedScaledChanceLogic() {
            super("tstmodern:three_weighted_scaled");
        }

        @Override
        public @Unmodifiable List<@NotNull Content> roll(
                                                    RecipeCapability<?> cap,
                                                    @NotNull @Unmodifiable List<@NotNull Content> chancedEntries,
                                                    ChanceBoostFunction chanceBoostFunction,
                                                    int recipeTier,
                                                    int chanceTier,
                                                    @Nullable Object2IntMap<?> cache,
                                                    int times) {
            if (chancedEntries.isEmpty()) {
                return Collections.emptyList();
            }

            int[] weights = chancedEntries.stream().mapToInt(entry -> entry.chance).toArray();
            int[] selectedIndexes = NetherInterfaceLogic.selectThreeWeighted(GTValues.RNG::nextInt, weights);
            return java.util.Arrays.stream(selectedIndexes)
                    .mapToObj(chancedEntries::get)
                    .map(selected -> selected.copyChanced(cap, ContentModifier.multiplier(times)))
                    .toList();
        }

        @Override
        public @NotNull Component getTranslation() {
            return Component.translatable("tstmodern.chance_logic.three_weighted_scaled");
        }
    }

    private static final class SingleRollScaledChanceLogic extends ChanceLogic {

        private SingleRollScaledChanceLogic() {
            super("tstmodern:single_roll_scaled");
        }

        @Override
        public @Unmodifiable List<@NotNull Content> roll(
                                                    RecipeCapability<?> cap,
                                                    @NotNull @Unmodifiable List<@NotNull Content> chancedEntries,
                                                    ChanceBoostFunction chanceBoostFunction,
                                                    int recipeTier,
                                                    int chanceTier,
                                                    @Nullable Object2IntMap<?> cache,
                                                    int times) {
            if (chancedEntries.isEmpty()) {
                return Collections.emptyList();
            }

            Content entry = chancedEntries.get(0);
            if (!NetherInterfaceLogic.rollOnce(GTValues.RNG::nextInt, entry.chance, entry.maxChance)) {
                return Collections.emptyList();
            }
            return List.of(entry.copyChanced(cap, ContentModifier.multiplier(times)));
        }

        @Override
        public @NotNull Component getTranslation() {
            return Component.translatable("tstmodern.chance_logic.single_roll_scaled");
        }
    }
}
