package com.example.zaharkuvalda.items;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

public class SledgehammerItem extends PickaxeItem {

    // Custom tier: stone mining level, 16 durability
    public static final Tier TIER = new Tier() {
        @Override public int getUses() { return 16; }
        @Override public float getSpeed() { return 2.0f; }
        @Override public float getAttackDamageBonus() { return 0.0f; }
        @Override public TagKey<Block> getIncorrectBlocksForDrops() { return BlockTags.NEEDS_IRON_TOOL; }
        @Override public int getEnchantmentValue() { return 5; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.of(Items.IRON_INGOT); }
    };

    public SledgehammerItem(Item.Properties properties) {
        super(TIER, properties.attributes(PickaxeItem.createAttributes(TIER, 5, -3.6f)));
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (state.is(BlockTags.MINEABLE_WITH_PICKAXE)) {
            return 2.0f;
        }
        return super.getDestroySpeed(stack, state);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility toolAction) {
        return toolAction == ItemAbilities.PICKAXE_DIG || super.canPerformAction(stack, toolAction);
    }
}
