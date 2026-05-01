package com.example.zaharkuvalda;

import com.example.zaharkuvalda.config.ModConfig;
import com.example.zaharkuvalda.events.ModEvents;
import com.example.zaharkuvalda.items.ModCreativeTabs;
import com.example.zaharkuvalda.items.ModItems;
import com.example.zaharkuvalda.recipe.ModRecipes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(ZaharkuvaldaMod.MODID)
public class ZaharkuvaldaMod {
    public static final String MODID = "zaharkuvalda";

    public ZaharkuvaldaMod(IEventBus modEventBus, ModContainer modContainer) {
        ModItems.register(modEventBus);
        ModRecipes.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        modContainer.registerConfig(Type.COMMON, ModConfig.SPEC);
        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.register(ModEvents.class);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
    }
}
