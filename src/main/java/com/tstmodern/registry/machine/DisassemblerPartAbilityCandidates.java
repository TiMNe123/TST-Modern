package com.tstmodern.registry.machine;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;

import net.minecraft.world.level.block.Block;

/**
 * Candidate seam kept separate so the closed-set behavior can run without Forge machine bootstrap.
 */
public final class DisassemblerPartAbilityCandidates {
    private static final Set<PartAbility> I_ALLOWED_ABILITIES = Set.of(
            PartAbility.IMPORT_ITEMS,
            PartAbility.EXPORT_ITEMS,
            PartAbility.EXPORT_FLUIDS);
    private static final Set<PartAbility> I_COMPATIBLE_ABILITY_METADATA = Set.of(
            PartAbility.EXPORT_FLUIDS_1X,
            PartAbility.EXPORT_FLUIDS_4X,
            PartAbility.EXPORT_FLUIDS_9X);

    private DisassemblerPartAbilityCandidates() {}

    public static TraceabilityPredicate closedIAbility(
            PartAbility ability,
            Map<PartAbility, Collection<Block>> abilityBlocks,
            Set<Block> closedCandidates) {
        Collection<Block> blocksForAbility = abilityBlocks.getOrDefault(ability, Set.of());
        return blocks(blocksForAbility.stream()
                .filter(closedCandidates::contains)
                .toArray(Block[]::new));
    }

    /** Retains an allowed part only when no known PartAbility outside the I contract registered that block. */
    public static <T> Set<T> selectClosedIAbilityCandidates(Map<PartAbility, ? extends Collection<T>> abilityBlocks) {
        Set<T> allowed = new LinkedHashSet<>();
        Set<T> disallowed = new LinkedHashSet<>();
        abilityBlocks.forEach((ability, candidateBlocks) -> {
            if (I_ALLOWED_ABILITIES.contains(ability)) {
                allowed.addAll(candidateBlocks);
            } else if (!I_COMPATIBLE_ABILITY_METADATA.contains(ability)) {
                disallowed.addAll(candidateBlocks);
            }
        });
        allowed.removeAll(disallowed);
        return Set.copyOf(allowed);
    }

    /**
     * GTCEu 7.4 exposes each built-in ability as a public static field but no complete runtime registry.
     * Scanning those fields catches every current GTCEu intersection, including both ME Pattern Buffers.
     */
    public static Map<PartAbility, Collection<Block>> knownPartAbilityBlocks() {
        return Arrays.stream(PartAbility.class.getFields())
                .filter(field -> Modifier.isStatic(field.getModifiers()) && field.getType() == PartAbility.class)
                .map(DisassemblerPartAbilityCandidates::partAbilityFromField)
                .collect(Collectors.toMap(
                        ability -> ability,
                        PartAbility::getAllBlocks,
                        (first, ignored) -> first,
                        LinkedHashMap::new));
    }

    private static PartAbility partAbilityFromField(Field field) {
        try {
            return (PartAbility) field.get(null);
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException("Unable to inspect GTCEu part abilities", exception);
        }
    }
}
