package com.example.zaharkuvalda.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

import java.util.Set;

public class SledgehammerItem extends PickaxeItem {

    public SledgehammerItem(Item.Properties properties) {
        super(Tiers.STONE, properties.attributes(PickaxeItem.createAttributes(Tiers.STONE, 4, -3.6f)));
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        // Very slow mining
        if (state.is(BlockTags.MINEABLE_WITH_PICKAXE)) {
            return 2.0f;
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
