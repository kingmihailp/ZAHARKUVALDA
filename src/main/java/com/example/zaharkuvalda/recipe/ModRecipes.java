package com.example.zaharkuvalda.recipe;

import com.example.zaharkuvalda.ZaharkuvaldaMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, ZaharkuvaldaMod.MODID);

    public static final Supplier<RecipeSerializer<ReinforcedSledgehammerRecipe>> REINFORCED_SLEDGEHAMMER_SERIALIZER =
            SERIALIZERS.register("reinforced_sledgehammer",
                    () -> new SimpleCraftingRecipeSerializer<>(ReinforcedSledgehammerRecipe::new));

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
