package com.tstmodern.client;

import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderManager;
import com.tstmodern.TSTModern;
import com.tstmodern.client.renderer.BigBroArrayPartRender;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Client-only registrations used by TST Modern machine models. */
@Mod.EventBusSubscriber(modid = TSTModern.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class TSTClient {

    private TSTClient() {}

    @SubscribeEvent
    public static void registerMachineModelExtensions(ModelEvent.RegisterGeometryLoaders event) {
        DynamicRenderManager.register(
                TSTModern.id("big_bro_array_parts"),
                BigBroArrayPartRender.TYPE);
    }
}
