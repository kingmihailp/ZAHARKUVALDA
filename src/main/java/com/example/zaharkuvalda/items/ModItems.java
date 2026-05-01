package com.example.zaharkuvalda.items;

import com.example.zaharkuvalda.ZaharkuvaldaMod;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ZaharkuvaldaMod.MODID);

    public static final DeferredItem<SledgehammerItem> SLEDGEHAMMER = ITEMS.register(
            "sledgehammer",
            () -> new SledgehammerItem(new Item.Properties().durability(16))
    );

    public static final DeferredItem<ReinforcedSledgehammerItem> REINFORCED_SLEDGEHAMMER = ITEMS.register(
            "reinforced_sledgehammer",
            () -> new ReinforcedSledgehammerItem(new Item.Properties().durability(32))
    );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
