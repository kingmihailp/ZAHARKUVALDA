package com.example.zaharkuvalda.items;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

public class ReinforcedSledgehammerItem extends PickaxeItem {

    public ReinforcedSledgehammerItem(Item.Properties properties) {
        super(Tiers.IRON, properties.attributes(PickaxeItem.createAttributes(Tiers.IRON, 4, -3.4f)));
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        // Slightly faster than regular sledgehammer but still slow
        if (state.is(BlockTags.MINEABLE_WITH_PICKAXE)) {
            return 3.0f;
        }
        return super.getDestroySpeed(stack, state);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility toolAction) {
        return toolAction == ItemAbilities.PICKAXE_DIG || super.canPerformAction(stack, toolAction);
    }

    public int getMiningRadius() {
        return 1; // 1 extra block in each direction = 3x3
    }
}
