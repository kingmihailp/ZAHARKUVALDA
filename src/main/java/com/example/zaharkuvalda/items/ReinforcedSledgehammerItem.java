package com.example.zaharkuvalda.items;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

public class ReinforcedSledgehammerItem extends PickaxeItem {

    // Custom tier: iron mining level, 32 durability
    public static final Tier TIER = new Tier() {
        @Override public int getUses() { return 32; }
        @Override public float getSpeed() { return 3.0f; }
        @Override public float getAttackDamageBonus() { return 0.0f; }
        @Override public TagKey<Block> getIncorrectBlocksForDrops() { return BlockTags.NEEDS_DIAMOND_TOOL; }
        @Override public int getEnchantmentValue() { return 9; }
        @Override public Ingredient getRepairIngredient() {
            return Ingredient.of(net.minecraft.core.registries.BuiltInRegistries.ITEM
                    .get(net.minecraft.resources.ResourceLocation.parse("tfmg:cast_iron_ingot")));
        }
    };

    public ReinforcedSledgehammerItem(Item.Properties properties) {
        super(TIER, properties.attributes(PickaxeItem.createAttributes(TIER, 8, -3.4f)));
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (state.is(BlockTags.MINEABLE_WITH_PICKAXE)) {
            return 3.0f;
        }
        return super.getDestroySpeed(stack, state);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility toolAction) {
        return toolAction == ItemAbilities.PICKAXE_DIG || super.canPerformAction(stack, toolAction);
    }
}
