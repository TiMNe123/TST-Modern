package com.tstmodern.registry;

import com.tstmodern.TSTModern;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

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
    public static final RegistryObject<Block> CONTAINMENT_FIELD_CASING = casing("containment_field_casing");
    public static final RegistryObject<Block> IRIDIUM_REINFORCED_NEUTRONIUM_CASING = casing(
            "iridium_reinforced_neutronium_casing");
    public static final RegistryObject<Block> BOROPHENE_NANOWIRE_CASING = casing("borophene_nanowire_casing");
    public static final RegistryObject<Block> NEUTRONIUM_PIPE_CASING = casing("neutronium_pipe_casing");

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

    private static RegistryObject<Block> casing(String name) {
        return blockWithItem(name, BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).strength(8.0F, 12.0F));
    }

    private static RegistryObject<Block> compressedCobble(int level) {
        return blockWithItem("compressed_cobblestone_" + level,
                BlockBehaviour.Properties.copy(Blocks.COBBLESTONE).strength(2.0F + level, 6.0F + level));
    }

    private static RegistryObject<Block> blockWithItem(String name, BlockBehaviour.Properties properties) {
        RegistryObject<Block> block = BLOCKS.register(name, () -> new Block(properties));
        ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }
}
