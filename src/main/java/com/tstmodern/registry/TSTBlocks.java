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

    public static final RegistryObject<Block> CASING_C = casing("mega_stone_breaker_casing_c");
    public static final RegistryObject<Block> COSMIC_NEUTRONIUM_FRAME = casing("cosmic_neutronium_frame");
    public static final RegistryObject<Block> VACUUM_CASING = casing("vacuum_casing");

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
