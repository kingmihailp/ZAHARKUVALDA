package com.example.zaharkuvalda.recipe;

import com.example.zaharkuvalda.items.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class ReinforcedSledgehammerRecipe extends CustomRecipe {

    public ReinforcedSledgehammerRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput container, Level level) {
        if (container.width() != 3 || container.height() != 3) return false;

        int castIronCount = 0;
        int sledgeCount = 0;

        for (int i = 0; i < container.size(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) return false;

            if (i == 4) {
                if (stack.getItem() == ModItems.SLEDGEHAMMER.get()) {
                    sledgeCount++;
                } else {
                    return false;
                }
            } else {
                ResourceLocation castIronId = ResourceLocation.parse("tfmg:cast_iron_ingot");
                if (net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).equals(castIronId)) {
                    castIronCount++;
                } else {
                    return false;
                }
            }
        }

        return sledgeCount == 1 && castIronCount == 8;
    }

    @Override
    public ItemStack assemble(CraftingInput container, HolderLookup.Provider registries) {
        ItemStack sledgehammer = container.getItem(4);
        if (sledgehammer.isEmpty() || sledgehammer.getItem() != ModItems.SLEDGEHAMMER.get()) {
            return ItemStack.EMPTY;
        }

        int currentDamage = sledgehammer.getDamageValue();
        int maxDurability = sledgehammer.getMaxDamage();
        int remainingDurability = maxDurability - currentDamage;

        int newMax = ModItems.REINFORCED_SLEDGEHAMMER.get().getMaxDamage();
        int newDurability = Math.min(remainingDurability * 2, newMax);
        int newDamage = newMax - newDurability;

        ItemStack result = new ItemStack(ModItems.REINFORCED_SLEDGEHAMMER.get());
        result.setDamageValue(Math.max(0, newDamage));
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.REINFORCED_SLEDGEHAMMER_SERIALIZER.get();
    }
}
