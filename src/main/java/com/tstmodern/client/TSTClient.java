package com.tstmodern.client;

import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.dust;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderManager;
import com.tstmodern.TSTModern;
import com.tstmodern.client.renderer.BigBroArrayPartRender;
import com.tstmodern.client.renderer.IncompactCyclotronPartRender;
import com.tstmodern.registry.TSTMaterials;

import net.minecraft.network.chat.Component;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.common.Mod;

/** Client-only registrations used by TST Modern machine models. */
@Mod.EventBusSubscriber(modid = TSTModern.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class TSTClient {

    private TSTClient() {}

    @SubscribeEvent
    public static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> MinecraftForge.EVENT_BUS.addListener(TSTClient::addNeptuniumDecayTooltip));
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
    }

    private static void addNeptuniumDecayTooltip(ItemTooltipEvent event) {
        if (TSTMaterials.NEPTUNIUM_238 != null &&
                event.getItemStack().is(ChemicalHelper.get(dust, TSTMaterials.NEPTUNIUM_238).getItem())) {
            event.getToolTip().add(Component.translatable("tstmodern.material.neptunium_238.decay"));
        }
    }
}
