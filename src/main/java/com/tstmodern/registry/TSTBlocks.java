package com.tstmodern.registry;

import com.tstmodern.TSTModern;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GlassBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

/** Blocks absent from standard GTCEu but still required by the ported structure or recipes. */
public final class TSTBlocks {
    private static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, TSTModern.MOD_ID);
    private static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, TSTModern.MOD_ID);

    public static final RegistryObject<Block> ADVANCED_IRIDIUM_CASING = casing("advanced_iridium_casing");
    public static final RegistryObject<Block> BLACK_PLUTONIUM_ITEM_PIPE_CASING = casing("black_plutonium_item_pipe_casing");
    public static final RegistryObject<Block> VENT_T2_CASING = casing("vent_t2_casing");
    public static final RegistryObject<Block> COSMIC_NEUTRONIUM_FRAME = casing("cosmic_neutronium_frame");
    public static final RegistryObject<Block> STABLE_RED_STEEL_CASING = casing("stable_red_steel_casing");
    public static final RegistryObject<Block> STABLE_TANTALLOY_61_CASING = casing("stable_tantalloy_61_casing");
    public static final RegistryObject<Block> STABALOY_FIREBOX_CASING = casing("stabaloy_firebox_casing");
    public static final RegistryObject<Block> PRESSURE_RESISTANT_WALL = casing("pressure_resistant_wall");
    public static final RegistryObject<Block> NEUTRONIUM_MINING_CASING = casing("neutronium_mining_casing");
    public static final RegistryObject<Block> VACUUM_CASING = casing("vacuum_casing");
    public static final RegistryObject<Block> MECHANICALLY_ENHANCED_OBSIDIAN = casing("mechanically_enhanced_obsidian");
    public static final RegistryObject<Block> HS188A_BLOCK = casing("hs188a_block");
    public static final RegistryObject<Block> QUANTUM_ALLOY_BLOCK = casing("quantum_alloy_block");
    public static final RegistryObject<Block> EXTREME_DENSITY_CASING = casing("extreme_density_casing");
    public static final RegistryObject<Block> OSMIRIDIUM_MINING_CASING = casing("osmiridium_mining_casing");
    public static final RegistryObject<Block> TANK_CASING_TIER_10 = casing("tank_casing_tier_10");
    public static final RegistryObject<Block> RADIANT_NAQUADAH_ALLOY_CASING = casing("radiant_naquadah_alloy_casing");
    public static final RegistryObject<Block> DYSON_SWARM_FLOOR = casing("dyson_swarm_floor");
    public static final RegistryObject<Block> IRIDIUM_REINFORCED_NEUTRONIUM_CASING = casing(
            "iridium_reinforced_neutronium_casing");
    public static final RegistryObject<Block> BOROPHENE_NANOWIRE_CASING = casing("borophene_nanowire_casing");
    public static final RegistryObject<Block> NEUTRONIUM_PIPE_CASING = casing("neutronium_pipe_casing");
    public static final RegistryObject<Block> DIMENSIONAL_BRIDGE_CASING = casing("dimensional_bridge_casing");

    // Mega Naquadah Reactor casings
    public static final RegistryObject<Block> FIELD_RESTRICTION_CASING = casing("field_restriction_casing");
    public static final RegistryObject<Block> MINING_BLACK_PLUTONIUM_CASING = casing(
            "mining_black_plutonium_casing");
    public static final RegistryObject<Block> PARTICLE_BEAM_GUIDANCE_PIPE_CASING = casing(
            "particle_beam_guidance_pipe_casing");

    // Naquadah Fuel Refinery casings
    public static final RegistryObject<Block> NAQUADAH_FUEL_REFINERY_CASING = casing(
            "naquadah_fuel_refinery_casing");
    public static final RegistryObject<Block> FIELD_RESTRICTION_COIL_T2 = casing("field_restriction_coil_t2");
    public static final RegistryObject<Block> FIELD_RESTRICTION_COIL_T3 = casing("field_restriction_coil_t3");
    public static final RegistryObject<Block> FIELD_RESTRICTION_COIL_T4 = casing("field_restriction_coil_t4");
    public static final RegistryObject<Block> FIELD_RESTRICTION_GLASS = glass("field_restriction_glass");
    public static final RegistryObject<Block> EUROPIUM_REINFORCED_RADIATION_PROOF_CASING = casing(
            "europium_reinforced_radiation_proof_casing");

    // Incompact Cyclotron casings
    public static final RegistryObject<Block> QUANTUM_FRAME = casing("quantum_frame");
    public static final RegistryObject<Block> COMPACT_CYCLOTRON_COIL = casing("compact_cyclotron_coil");
    public static final RegistryObject<Block> DENSE_CYCLOTRON_OUTER_CASING = casing(
            "dense_cyclotron_outer_casing");

    // Large Neutron Oscillator casings
    public static final RegistryObject<Block> HIGH_POWER_CASING = casing("high_power_casing");
    public static final RegistryObject<Block> SPEEDING_PIPE_CASING = casing("speeding_pipe_casing");
    public static final RegistryObject<Block> COMPACT_FUSION_COIL_T3 = casing("compact_fusion_coil_t3");

    // Astral Computing Array casings
    public static final RegistryObject<Block> FIELD_RESTRICTION_COIL_T1 = casing("field_restriction_coil_t1");
    public static final RegistryObject<Block> COMPACT_FUSION_COIL_T0 = casing("compact_fusion_coil_t0");
    public static final RegistryObject<Block> SPACE_ELEVATOR_BASE_CASING = casing("space_elevator_base_casing");
    public static final RegistryObject<Block> SPACE_ELEVATOR_SUPPORT_STRUCTURE = casing(
            "space_elevator_support_structure");
    public static final RegistryObject<Block> SPACE_ELEVATOR_INTERNAL_STRUCTURE = casing(
            "space_elevator_internal_structure");
    public static final RegistryObject<Block> COMPUTER_CASING = casing("computer_casing");
    public static final RegistryObject<Block> COMPUTER_HEAT_VENT = casing("computer_heat_vent");
    public static final RegistryObject<Block> ADVANCED_COMPUTER_CASING = casing("advanced_computer_casing");
    public static final RegistryObject<Block> ELECTROMAGNETIC_COMPUTER_COIL = casing(
            "electromagnetic_computer_coil");
    public static final RegistryObject<Block> CONTAINMENT_CASING = casing("containment_casing");
    public static final RegistryObject<Block> RADIATION_PROTECTION_STEEL_FRAME = casing(
            "radiation_protection_steel_frame");
    public static final RegistryObject<Block> ASTRAL_PYLON_CASING = casing("astral_pylon_casing");

    // Mega Tree Farm casings
    public static final RegistryObject<Block> STERILE_CASING = casing("sterile_casing");
    public static final RegistryObject<Block> ASEPTIC_GREENHOUSE_CASING = casing("aseptic_greenhouse_casing");
    public static final RegistryObject<Block> ARCANE_TRANSLUCENT_CASING = casing("arcane_translucent_casing");
    public static final RegistryObject<Block> AIR_CRYSTAL_CASING = casing("air_crystal_casing");
    public static final RegistryObject<Block> WATER_CRYSTAL_CASING = casing("water_crystal_casing");
    public static final RegistryObject<Block> EARTH_CRYSTAL_CASING = casing("earth_crystal_casing");
    public static final RegistryObject<Block> CULTIVATION_SOIL_CASING = casing("cultivation_soil_casing");
    public static final RegistryObject<Block> ADVANCED_RADIATION_PROOF_CASING = casing("advanced_radiation_proof_casing");
    public static final RegistryObject<Block> REINFORCED_STONE_BRICK_CASING = casing("reinforced_stone_brick_casing");
    public static final RegistryObject<Block> COMPOSITE_FARM_CASING = casing("composite_farm_casing");
    public static final RegistryObject<Block> INTEGRAL_FRAMEWORK_UV_CASING = casing("integral_framework_uv_casing");

    // Disassembler casings
    public static final RegistryObject<Block> COMPONENT_ASSEMBLY_LINE_CASING_LV = casing(
            "component_assembly_line_casing_lv");
    public static final RegistryObject<Block> COMPONENT_ASSEMBLY_LINE_CASING_MV = casing(
            "component_assembly_line_casing_mv");
    public static final RegistryObject<Block> COMPONENT_ASSEMBLY_LINE_CASING_HV = casing(
            "component_assembly_line_casing_hv");
    public static final RegistryObject<Block> COMPONENT_ASSEMBLY_LINE_CASING_EV = casing(
            "component_assembly_line_casing_ev");
    public static final RegistryObject<Block> COMPONENT_ASSEMBLY_LINE_CASING_IV = casing(
            "component_assembly_line_casing_iv");
    public static final RegistryObject<Block> COMPONENT_ASSEMBLY_LINE_CASING_LUV = casing(
            "component_assembly_line_casing_luv");
    public static final RegistryObject<Block> COMPONENT_ASSEMBLY_LINE_CASING_ZPM = casing(
            "component_assembly_line_casing_zpm");
    public static final RegistryObject<Block> COMPONENT_ASSEMBLY_LINE_CASING_UV = casing(
            "component_assembly_line_casing_uv");
    public static final RegistryObject<Block> COMPONENT_ASSEMBLY_LINE_CASING_UHV = casing(
            "component_assembly_line_casing_uhv");
    public static final RegistryObject<Block> COMPONENT_ASSEMBLY_LINE_CASING_UEV = casing(
            "component_assembly_line_casing_uev");
    public static final RegistryObject<Block> COMPONENT_ASSEMBLY_LINE_CASING_UIV = casing(
            "component_assembly_line_casing_uiv");
    public static final RegistryObject<Block> COMPONENT_ASSEMBLY_LINE_CASING_UMV = casing(
            "component_assembly_line_casing_umv");
    public static final RegistryObject<Block> COMPONENT_ASSEMBLY_LINE_CASING_UXV = casing(
            "component_assembly_line_casing_uxv");
    public static final RegistryObject<Block> COMPONENT_ASSEMBLY_LINE_CASING_MAX = casing(
            "component_assembly_line_casing_max");
    public static final List<RegistryObject<Block>> COMPONENT_ASSEMBLY_LINE_CASINGS = List.of(
            COMPONENT_ASSEMBLY_LINE_CASING_LV,
            COMPONENT_ASSEMBLY_LINE_CASING_MV,
            COMPONENT_ASSEMBLY_LINE_CASING_HV,
            COMPONENT_ASSEMBLY_LINE_CASING_EV,
            COMPONENT_ASSEMBLY_LINE_CASING_IV,
            COMPONENT_ASSEMBLY_LINE_CASING_LUV,
            COMPONENT_ASSEMBLY_LINE_CASING_ZPM,
            COMPONENT_ASSEMBLY_LINE_CASING_UV,
            COMPONENT_ASSEMBLY_LINE_CASING_UHV,
            COMPONENT_ASSEMBLY_LINE_CASING_UEV,
            COMPONENT_ASSEMBLY_LINE_CASING_UIV,
            COMPONENT_ASSEMBLY_LINE_CASING_UMV,
            COMPONENT_ASSEMBLY_LINE_CASING_UXV,
            COMPONENT_ASSEMBLY_LINE_CASING_MAX);
    public static final RegistryObject<Block> MOLECULAR_CASING = casing("molecular_casing");
    public static final RegistryObject<Block> HOLLOW_CASING = casing("hollow_casing");

    public static final RegistryObject<Block> PARALLEL_CASING_MK1 = casing("parallel_casing_mk1");
    public static final RegistryObject<Block> PARALLEL_CASING_MK2 = casing("parallel_casing_mk2");
    public static final RegistryObject<Block> PARALLEL_CASING_MK3 = casing("parallel_casing_mk3");
    public static final RegistryObject<Block> PARALLEL_CASING_MK4 = casing("parallel_casing_mk4");
    public static final RegistryObject<Block> PARALLEL_CASING_MK5 = casing("parallel_casing_mk5");

    public static final List<RegistryObject<Block>> PARALLEL_CASINGS = List.of(
            PARALLEL_CASING_MK1,
            PARALLEL_CASING_MK2,
            PARALLEL_CASING_MK3,
            PARALLEL_CASING_MK4,
            PARALLEL_CASING_MK5);

    public static final RegistryObject<Block> COMPRESSED_COBBLESTONE_1 = compressedCobble(1);
    public static final RegistryObject<Block> COMPRESSED_COBBLESTONE_2 = compressedCobble(2);
    public static final RegistryObject<Block> COMPRESSED_COBBLESTONE_3 = compressedCobble(3);
    public static final RegistryObject<Block> COMPRESSED_COBBLESTONE_4 = compressedCobble(4);
    public static final RegistryObject<Block> COMPRESSED_COBBLESTONE_5 = compressedCobble(5);
    public static final RegistryObject<Block> COMPRESSED_COBBLESTONE_6 = compressedCobble(6);
    public static final RegistryObject<Block> COMPRESSED_COBBLESTONE_7 = compressedCobble(7);
    public static final RegistryObject<Block> COMPRESSED_COBBLESTONE_8 = compressedCobble(8);

    private TSTBlocks() {}

    public static void register(IEventBus modBus) {
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
    }

    /** Returns the registered component assembly line casing for a 1-based LV-through-MAX tier. */
    public static RegistryObject<Block> componentAssemblyLineCasing(int tier) {
        if (tier < 1 || tier > COMPONENT_ASSEMBLY_LINE_CASINGS.size()) {
            throw new IllegalArgumentException("Component assembly line tier must be between 1 and 14: " + tier);
        }
        return COMPONENT_ASSEMBLY_LINE_CASINGS.get(tier - 1);
    }

    /** Returns the 1-based LV-through-MAX component assembly line tier, or zero for a different block. */
    public static int componentAssemblyLineTier(Block block) {
        for (int index = 0; index < COMPONENT_ASSEMBLY_LINE_CASINGS.size(); index++) {
            RegistryObject<Block> casing = COMPONENT_ASSEMBLY_LINE_CASINGS.get(index);
            if (casing.isPresent() && casing.get() == block) {
                return index + 1;
            }
        }
        return 0;
    }

    /** Returns the 1-based MK1-through-MK5 parallel casing tier, or zero for a different block. */
    public static int parallelCasingTier(Block block) {
        for (int index = 0; index < PARALLEL_CASINGS.size(); index++) {
            RegistryObject<Block> casing = PARALLEL_CASINGS.get(index);
            if (casing.isPresent() && casing.get() == block) {
                return index + 1;
            }
        }
        return 0;
    }

    private static RegistryObject<Block> casing(String name) {
        return blockWithItem(name, BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(8.0F, 12.0F));
    }

    private static RegistryObject<Block> compressedCobble(int level) {
        return blockWithItem("compressed_cobblestone_" + level,
                BlockBehaviour.Properties.copy(Blocks.COBBLESTONE).strength(2.0F + level, 6.0F + level));
    }

    private static RegistryObject<Block> glass(String name) {
        RegistryObject<Block> block = BLOCKS.register(name,
                () -> new GlassBlock(BlockBehaviour.Properties.copy(Blocks.GLASS).strength(8.0F, 12.0F).noOcclusion()));
        ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }

    private static RegistryObject<Block> blockWithItem(String name, BlockBehaviour.Properties properties) {
        RegistryObject<Block> block = BLOCKS.register(name, () -> new Block(properties));
        ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }
}
