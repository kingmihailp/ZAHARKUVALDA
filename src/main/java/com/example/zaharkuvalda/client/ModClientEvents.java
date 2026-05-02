package com.example.zaharkuvalda.client;

import com.example.zaharkuvalda.ZaharkuvaldaMod;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

import java.util.Map;

@EventBusSubscriber(modid = ZaharkuvaldaMod.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientEvents {

    private static final ModelResourceLocation SLEDGEHAMMER_3D =
        ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(ZaharkuvaldaMod.MODID, "item/sledgehammer_3d"));
    private static final ModelResourceLocation REINFORCED_3D =
        ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(ZaharkuvaldaMod.MODID, "item/reinforced_sledgehammer_3d"));

    @SubscribeEvent
    public static void onRegisterAdditional(ModelEvent.RegisterAdditional event) {
        event.register(SLEDGEHAMMER_3D);
        event.register(REINFORCED_3D);
    }

    @SubscribeEvent
    public static void onBakingCompleted(ModelEvent.BakingCompleted event) {
        swap(event,
            ResourceLocation.fromNamespaceAndPath(ZaharkuvaldaMod.MODID, "sledgehammer"),
            SLEDGEHAMMER_3D);
        swap(event,
            ResourceLocation.fromNamespaceAndPath(ZaharkuvaldaMod.MODID, "reinforced_sledgehammer"),
            REINFORCED_3D);
    }

    private static void swap(ModelEvent.BakingCompleted event, ResourceLocation itemRl, ModelResourceLocation key3d) {
        ModelResourceLocation itemKey = ModelResourceLocation.inventory(itemRl);

        Map<ModelResourceLocation, BakedModel> models = event.getModels();
        BakedModel model2d = models.get(itemKey);
        BakedModel model3d = models.get(key3d);

        if (model2d != null && model3d != null) {
            models.put(itemKey, new PerspectiveAwareModel(model2d, model3d));
        }
    }
}
