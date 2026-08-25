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

import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.common.data.GCYMBlocks;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.tstmodern.TSTModern;
import com.tstmodern.machine.DisassemblerMachine;
import com.tstmodern.machine.DisassemblerStructure;
import com.tstmodern.registry.TSTBlocks;
import com.tstmodern.registry.TSTRecipeTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;

/** Definition and rendering contract for the Large Disassembler. */
public final class DisassemblerDefinition {
    public static final MultiblockMachineDefinition MACHINE = TSTModern.REGISTRATE
            .multiblock("disassembler", DisassemblerMachine::new)
            .langValue("Large Disassembler")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(TSTRecipeTypes.DISASSEMBLER)
            .appearanceBlock(TSTBlocks.MOLECULAR_CASING)
            .pattern(definition -> {
                Map<PartAbility, Collection<Block>> abilityBlocks = DisassemblerPartAbilityCandidates.knownPartAbilityBlocks();
                Set<Block> closedIAbilityBlocks = DisassemblerPartAbilityCandidates
                        .selectClosedIAbilityCandidates(abilityBlocks);
                FactoryBlockPattern pattern = FactoryBlockPattern.start(
                        RelativeDirection.RIGHT,
                        RelativeDirection.DOWN,
                        RelativeDirection.BACK);
                for (String[] aisle : DisassemblerStructure.AISLES) {
                    pattern.aisle(aisle);
                }
                return pattern
                        .where('~', Predicates.controller(blocks(definition.get())))
                        .where('A', blocks(GTBlocks.FUSION_GLASS.get()))
                        .where('B', blocks(TSTBlocks.COMPONENT_ASSEMBLY_LINE_CASINGS.stream()
                                .map(casing -> casing.get())
                                .toArray(Block[]::new)))
                        .where('C', blocks(GTBlocks.CASING_TUNGSTENSTEEL_GEARBOX.get()))
                        .where('D', blocks(GTBlocks.FUSION_CASING.get()))
                        .where('E', blocks(GCYMBlocks.CASING_LARGE_SCALE_ASSEMBLING.get()))
                        .where('F', blocks(GTBlocks.FILTER_CASING.get()))
                        .where('G', blocks(TSTBlocks.MOLECULAR_CASING.get()))
                        .where('H', blocks(TSTBlocks.HOLLOW_CASING.get()))
                        .where('I', blocks(TSTBlocks.MOLECULAR_CASING.get())
                                .or(DisassemblerPartAbilityCandidates.closedIAbility(
                                        PartAbility.IMPORT_ITEMS, abilityBlocks, closedIAbilityBlocks)
                                        .setMinGlobalLimited(1))
                                .or(DisassemblerPartAbilityCandidates.closedIAbility(
                                        PartAbility.EXPORT_ITEMS, abilityBlocks, closedIAbilityBlocks)
                                        .setMinGlobalLimited(1))
                                .or(DisassemblerPartAbilityCandidates.closedIAbility(
                                        PartAbility.EXPORT_FLUIDS, abilityBlocks, closedIAbilityBlocks)
                                        .setMinGlobalLimited(1)))
                        .where('J', Predicates.frames(GTMaterials.Neutronium))
                        .where(' ', Predicates.any())
                        .build();
            })
            .workableCasingModel(
                    TSTModern.id("block/casings/dimensional_transcendent_casing"),
                    TSTModern.id("block/multiblock/disassembler"))
            .tooltips(
                    Component.translatable("tstmodern.machine.disassembler.tooltip.0"),
                    Component.translatable("tstmodern.machine.disassembler.tooltip.1"),
                    Component.translatable("tstmodern.machine.disassembler.tooltip.2"),
                    Component.translatable("tstmodern.machine.disassembler.tooltip.3"),
                    Component.translatable("tstmodern.machine.disassembler.tooltip.4"))
            .register();

    private DisassemblerDefinition() {}
}
