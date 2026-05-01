package com.example.zaharkuvalda.items;

import com.example.zaharkuvalda.ZaharkuvaldaMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ZaharkuvaldaMod.MODID);

    public static final Supplier<CreativeModeTab> MAIN_TAB = CREATIVE_TABS.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.zaharkuvalda.main"))
                    .icon(() -> ModItems.SLEDGEHAMMER.get().getDefaultInstance())
                    .displayItems((params, output) -> {
                        output.accept(ModItems.SLEDGEHAMMER.get());
                        output.accept(ModItems.REINFORCED_SLEDGEHAMMER.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_TABS.register(eventBus);
    }
}
