package com.tstmodern.client;

import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.dust;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.block.MaterialBlock;
import com.gregtechceu.gtceu.api.block.MaterialPipeBlock;
import com.gregtechceu.gtceu.api.item.MaterialBlockItem;
import com.gregtechceu.gtceu.api.item.TagPrefixItem;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderManager;
import com.tstmodern.TSTModern;
import com.tstmodern.client.renderer.BigBroArrayPartRender;
import com.tstmodern.client.renderer.IncompactCyclotronPartRender;
import com.tstmodern.client.renderer.OreProcessingFactoryPartRender;
import com.tstmodern.client.renderer.DraconicCrucibleDragonRender;
import com.tstmodern.registry.TSTMaterials;
import com.tstmodern.registry.TSTBlocks;
import com.tstmodern.registry.TSTItems;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/** Client-only registrations used by TST Modern machine models. */
@Mod.EventBusSubscriber(modid = TSTModern.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class TSTClient {

    private static final ItemColor INFINITY_RAINBOW_COLOR = (stack, tintIndex) -> {
        if (stack.getItem() instanceof TagPrefixItem) return -1;
        return tintIndex == 0 || (tintIndex == 1 && isInfinityFluidPipe(stack.getItem())) ?
                infinityRainbowColor() : -1;
    };
    private static final Set<BlockPos> INFINITY_RENDER_POSITIONS = ConcurrentHashMap.newKeySet();
    private static final BlockColor INFINITY_RAINBOW_BLOCK_COLOR = (state, level, pos, tintIndex) -> {
        if (pos != null) INFINITY_RENDER_POSITIONS.add(pos.immutable());
        return tintIndex == 0 || (tintIndex == 1 && state.getBlock() instanceof MaterialPipeBlock<?, ?, ?>) ?
                infinityRainbowColor() : -1;
    };
    private static ClientLevel trackedLevel;

    private TSTClient() {}

    @SubscribeEvent
    public static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MinecraftForge.EVENT_BUS.addListener(TSTClient::addNeptuniumDecayTooltip);
            MinecraftForge.EVENT_BUS.addListener(TSTClient::animateInfinityBlocks);
        });
    }

    @SubscribeEvent
    public static void registerMachineModelExtensions(ModelEvent.RegisterGeometryLoaders event) {
        DynamicRenderManager.register(
                TSTModern.id("big_bro_array_parts"),
                BigBroArrayPartRender.TYPE);
        DynamicRenderManager.register(
                TSTModern.id("incompact_cyclotron_parts"),
                IncompactCyclotronPartRender.TYPE);
        DynamicRenderManager.register(
                TSTModern.id("large_neutron_oscillator_parts"),
                com.tstmodern.client.renderer.LargeNeutronOscillatorPartRender.TYPE);
        DynamicRenderManager.register(
                TSTModern.id("ore_processing_factory_parts"),
                OreProcessingFactoryPartRender.TYPE);
        DynamicRenderManager.register(
                TSTModern.id("draconic_crucible_dragon"),
                DraconicCrucibleDragonRender.TYPE);
        DynamicRenderManager.register(
                TSTModern.id("galactic_armillary_galaxy"),
                com.tstmodern.client.renderer.GalacticArmillaryGalaxyRender.TYPE);
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register(MaterialBlock.tintedColor(), TSTBlocks.DRACONIUM_ORE.get());
        Block[] infinityBlocks = ForgeRegistries.BLOCKS.getValues().stream()
                .filter(TSTClient::isInfinityWorldBlock)
                .toArray(Block[]::new);
        event.register(INFINITY_RAINBOW_BLOCK_COLOR, infinityBlocks);
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(MaterialBlockItem.tintColor(TSTMaterials.DRACONIUM), TSTBlocks.DRACONIUM_ORE.get());
        event.register(TagPrefixItem.tintColor(TSTMaterials.DRACONIUM), TSTItems.DRACONIUM_CORE.get());
        event.register(TagPrefixItem.tintColor(TSTMaterials.AWAKENED_DRACONIUM), TSTItems.DRAGON_HEART.get());
        ForgeRegistries.ITEMS.getValues().stream()
                .filter(item -> (item instanceof TagPrefixItem prefixItem &&
                        prefixItem.material == TSTMaterials.INFINITY) ||
                        (item instanceof MaterialBlockItem blockItem && blockItem.material == TSTMaterials.INFINITY) ||
                        isInfinityFluidPipe(item))
                .forEach(item -> event.register(INFINITY_RAINBOW_COLOR, item));
    }

    private static boolean isInfinityFluidPipe(Item item) {
        var id = ForgeRegistries.ITEMS.getKey(item);
        return id != null && "gtceu".equals(id.getNamespace()) &&
                id.getPath().startsWith("infinity_") && id.getPath().endsWith("_fluid_pipe");
    }

    private static boolean isInfinityWorldBlock(Block block) {
        return block instanceof MaterialBlock materialBlock && materialBlock.material == TSTMaterials.INFINITY ||
                block instanceof MaterialPipeBlock<?, ?, ?> pipeBlock && pipeBlock.material == TSTMaterials.INFINITY;
    }

    private static int infinityRainbowColor() {
        return Mth.hsvToRgb((Util.getMillis() % 4_000L) / 4_000.0F, 0.7F, 1.0F);
    }

    private static void animateInfinityBlocks(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level != trackedLevel) {
            trackedLevel = level;
            INFINITY_RENDER_POSITIONS.clear();
        }
        if (level == null || level.getGameTime() % 4 != 0) return;

        // ponytail: targeted 5 FPS rebakes; use a custom pipe renderer if huge Infinity networks become common.
        Iterator<BlockPos> positions = INFINITY_RENDER_POSITIONS.iterator();
        while (positions.hasNext()) {
            BlockPos pos = positions.next();
            if (!isInfinityWorldBlock(level.getBlockState(pos).getBlock())) {
                positions.remove();
                continue;
            }
            minecraft.levelRenderer.setBlocksDirty(
                    pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
        }
    }

    private static void addNeptuniumDecayTooltip(ItemTooltipEvent event) {
        if (TSTMaterials.NEPTUNIUM_238 != null &&
                event.getItemStack().is(ChemicalHelper.get(dust, TSTMaterials.NEPTUNIUM_238).getItem())) {
            event.getToolTip().add(Component.translatable("tstmodern.material.neptunium_238.decay"));
        }
    }
}
