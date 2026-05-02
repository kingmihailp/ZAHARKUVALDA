package com.example.zaharkuvalda.client;

import com.example.zaharkuvalda.ZaharkuvaldaMod;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

@EventBusSubscriber(modid = ZaharkuvaldaMod.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientEvents {

    @SubscribeEvent
    public static void onRegisterGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register(
            ResourceLocation.fromNamespaceAndPath(ZaharkuvaldaMod.MODID, PerspectiveModelLoader.ID),
            new PerspectiveModelLoader()
        );
    }
}
