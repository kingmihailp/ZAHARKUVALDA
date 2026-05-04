package com.example.zaharkuvalda.events;

import com.example.zaharkuvalda.config.ModConfig;
import com.example.zaharkuvalda.items.ModItems;
import com.example.zaharkuvalda.items.ReinforcedSledgehammerItem;
import com.example.zaharkuvalda.items.SledgehammerItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.*;

public class ModEvents {

    // ==================== BRUSH + GRASS ====================

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        // Only process main hand to avoid double-firing
        if (event.getHand() != InteractionHand.MAIN_HAND) return;

        Level level = event.getLevel();
        Player player = event.getEntity();
        BlockPos pos = event.getPos();
        ItemStack stack = event.getItemStack();

        if (level.isClientSide()) return;

        // === BRUSH + GRASS ===
        if (stack.getItem() instanceof net.minecraft.world.item.BrushItem) {
            handleBrush(event, level, player, pos, stack);
            return;
        }

        boolean isSledge = stack.getItem() instanceof SledgehammerItem
                || stack.getItem() instanceof ReinforcedSledgehammerItem;
        if (!isSledge) return;

        BlockState state = level.getBlockState(pos);

        // === CAMPFIRE: Cast iron smelting ===
        if (state.is(Blocks.CAMPFIRE) || state.is(Blocks.SOUL_CAMPFIRE)) {
            handleCampfire(level, player, pos, state, stack);
            event.setCanceled(true);
            return;
        }

        // === ANVIL: Sheet pressing ===
        if (state.is(BlockTags.ANVIL)) {
            handleAnvil(level, player, pos, state, stack);
            event.setCanceled(true);
            return;
        }

        // === OBSIDIAN: Crushing ===
        if (state.is(Blocks.OBSIDIAN) || state.is(Blocks.CRYING_OBSIDIAN)) {
            handleObsidian(level, player, pos, stack);
            event.setCanceled(true);
            return;
        }
    }

    private static void handleBrush(PlayerInteractEvent.RightClickBlock event, Level level,
                                     Player player, BlockPos pos, ItemStack stack) {
        BlockState state = level.getBlockState(pos);
        if (!state.is(Blocks.GRASS_BLOCK)) return;

        // Convert grass to dirt
        level.setBlock(pos, Blocks.DIRT.defaultBlockState(), 3);

        // Damage the brush
        if (!player.isCreative()) {
            stack.hurtAndBreak(1, (ServerLevel) level, (ServerPlayer) player,
                    item -> player.onEquippedItemBroken(item, net.minecraft.world.entity.EquipmentSlot.MAINHAND));
        }

        // Try to spawn a resource
        double chance = ModConfig.BRUSH_RESOURCE_CHANCE.get();
        if (level.random.nextDouble() < chance) {
            String resourceId = ModConfig.BRUSH_RESOURCE.get();
            ResourceLocation rl = ResourceLocation.tryParse(resourceId);
            if (rl != null && BuiltInRegistries.ITEM.containsKey(rl)) {
                Item item = BuiltInRegistries.ITEM.get(rl);
                int amount = ModConfig.BRUSH_RESOURCE_AMOUNT.get();
                ItemStack drop = new ItemStack(item, amount);
                spawnItem(level, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, drop);
            }
        }

        level.playSound(null, pos, SoundEvents.BRUSH_SAND, SoundSource.BLOCKS, 1.0f, 1.0f);
        event.setCanceled(true);
    }

    // ==================== CAMPFIRE HANDLER ====================

    private static void handleCampfire(Level level, Player player, BlockPos pos,
                                        BlockState state, ItemStack sledge) {
        if (!state.getValue(BlockStateProperties.LIT)) return;

        // Search for ItemEntity items thrown onto/near the campfire
        AABB searchBox = new AABB(pos.getX() - 0.5, pos.getY(), pos.getZ() - 0.5,
                pos.getX() + 1.5, pos.getY() + 1.5, pos.getZ() + 1.5);
        List<ItemEntity> nearby = level.getEntitiesOfClass(ItemEntity.class, searchBox);

        ItemEntity ironEntity = null;
        ItemEntity coalEntity = null;

        for (ItemEntity ie : nearby) {
            if (ironEntity == null && ie.getItem().is(Items.IRON_INGOT)) {
                ironEntity = ie;
            } else if (coalEntity == null
                    && (ie.getItem().is(Items.COAL) || ie.getItem().is(Items.CHARCOAL))) {
                coalEntity = ie;
            }
            if (ironEntity != null && coalEntity != null) break;
        }

        if (ironEntity == null || coalEntity == null) return;

        // Consume one iron ingot
        ironEntity.getItem().shrink(1);
        if (ironEntity.getItem().isEmpty()) ironEntity.discard();

        // Consume one coal
        coalEntity.getItem().shrink(1);
        if (coalEntity.getItem().isEmpty()) coalEntity.discard();

        // Extinguish campfire
        level.setBlock(pos, state.setValue(BlockStateProperties.LIT, false), 3);

        // Spawn cast iron ingot
        ItemStack castIron = getCastIronIngot();
        spawnItem(level, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, castIron);

        damageSledgehammer(level, player, sledge, 1);
        level.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 1.0f, 0.8f);
    }

    private static ItemStack getCastIronIngot() {
        ResourceLocation castIronId = ResourceLocation.parse("tfmg:cast_iron_ingot");
        if (BuiltInRegistries.ITEM.containsKey(castIronId)) {
            return new ItemStack(BuiltInRegistries.ITEM.get(castIronId));
        }
        return new ItemStack(Items.IRON_INGOT); // fallback
    }

    // ==================== ANVIL HANDLER ====================

    private static void handleAnvil(Level level, Player player, BlockPos pos,
                                     BlockState state, ItemStack sledge) {
        AABB searchBox = new AABB(pos.getX(), pos.getY() + 0.5, pos.getZ(),
                pos.getX() + 1, pos.getY() + 1.5, pos.getZ() + 1);
        List<ItemEntity> entities = level.getEntitiesOfClass(ItemEntity.class, searchBox);

        if (entities.isEmpty()) return;

        ItemEntity target = null;
        for (ItemEntity ie : entities) {
            if (isIngot(ie.getItem())) {
                target = ie;
                break;
            }
        }

        if (target == null) return;

        // Take one ingot
        ItemStack ingot = target.getItem().copyWithCount(1);
        target.getItem().shrink(1);
        if (target.getItem().isEmpty()) target.discard();

        ItemStack sheet = getSheetForIngot(ingot);
        spawnItem(level, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, sheet);

        damageSledgehammer(level, player, sledge, 1);

        // 5% chance to damage anvil
        if (level.random.nextFloat() < 0.05f) {
            BlockState damagedState = AnvilBlock.damage(state);
            if (damagedState == null) {
                level.removeBlock(pos, false);
            } else {
                level.setBlock(pos, damagedState, 2);
            }
        }

        level.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 1.0f, 1.2f);
    }

    // Ingot → Sheet map. Key = ingot item id, Value = sheet item id.
    private static final Map<String, String> INGOT_TO_SHEET = buildIngotSheetMap();

    private static Map<String, String> buildIngotSheetMap() {
        Map<String, String> m = new LinkedHashMap<>();
        // Vanilla
        m.put("minecraft:iron_ingot",     "create:iron_sheet");
        m.put("minecraft:copper_ingot",   "create:copper_sheet");
        m.put("minecraft:gold_ingot",     "create:golden_sheet");
        m.put("minecraft:netherite_ingot","create:netherite_sheet");
        // TFMG
        m.put("tfmg:cast_iron_ingot",        "tfmg:cast_iron_sheet");
        m.put("tfmg:lead_ingot",             "tfmg:lead_sheet");
        m.put("tfmg:aluminum_ingot",         "tfmg:aluminum_sheet");
        m.put("tfmg:magnetic_alloy_ingot",   "tfmg:magnetic_alloy_sheet");
        // Create
        m.put("create:brass_ingot",       "create:brass_sheet");
        m.put("create:andesite_alloy",    "createdeco:andesite_alloy_sheet");
        // Create: Crafts & Additions
        m.put("createaddition:electrum_ingot", "createaddition:electrum_sheet");
        m.put("create:zinc_ingot",             "createaddition:zinc_sheet");
        // Create Deco
        m.put("createdeco:industrial_iron_ingot", "createdeco:industrial_iron_sheet");
        // Vanilla blocks
        m.put("minecraft:lapis_block",    "ccbr:lapis_sheet");
        return m;
    }

    private static boolean isIngot(ItemStack stack) {
        String id = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        return INGOT_TO_SHEET.containsKey(id)
                || stack.is(Items.IRON_INGOT)
                || stack.is(Items.GOLD_INGOT)
                || stack.is(Items.COPPER_INGOT)
                || stack.is(Items.NETHERITE_INGOT)
                || id.endsWith("_ingot")
                || id.equals("create:andesite_alloy")
                || id.endsWith("_block") && INGOT_TO_SHEET.containsKey(id);
    }

    private static ItemStack getSheetForIngot(ItemStack ingot) {
        String ingotId = BuiltInRegistries.ITEM.getKey(ingot.getItem()).toString();

        String sheetId = INGOT_TO_SHEET.get(ingotId);
        if (sheetId != null) {
            ResourceLocation rl = ResourceLocation.tryParse(sheetId);
            if (rl != null && BuiltInRegistries.ITEM.containsKey(rl)) {
                return new ItemStack(BuiltInRegistries.ITEM.get(rl));
            }
        }

        // Return original item as fallback
        return ingot.copy();
    }

    // ==================== OBSIDIAN CRUSHING HANDLER ====================

    private static void handleObsidian(Level level, Player player, BlockPos pos, ItemStack sledge) {
        AABB searchBox = new AABB(pos.getX(), pos.getY() + 0.5, pos.getZ(),
                pos.getX() + 1, pos.getY() + 1.5, pos.getZ() + 1);
        List<ItemEntity> entities = level.getEntitiesOfClass(ItemEntity.class, searchBox);

        if (entities.isEmpty()) return;

        ItemEntity target = entities.get(0);
        ItemStack input = target.getItem().copyWithCount(1);
        target.getItem().shrink(1);
        if (target.getItem().isEmpty()) target.discard();

        ItemStack result = getCrushingResult(level, input);
        spawnItem(level, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, result);

        damageSledgehammer(level, player, sledge, 1);
        level.playSound(null, pos, SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS, 1.0f, 0.8f);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static ItemStack getCrushingResult(Level level, ItemStack input) {
        if (level instanceof ServerLevel serverLevel) {
            try {
                ResourceLocation crushingTypeId = ResourceLocation.parse("create:crushing");
                RecipeType<?> crushingType = BuiltInRegistries.RECIPE_TYPE.get(crushingTypeId);

                if (crushingType != null) {
                    Collection<RecipeHolder<?>> recipes =
                            serverLevel.getRecipeManager().getAllRecipesFor((RecipeType) crushingType);

                    for (RecipeHolder<?> holder : recipes) {
                        // Check if any ingredient of this recipe matches the input item
                        boolean matches = holder.value().getIngredients().stream()
                                .anyMatch(ing -> ing.test(input));
                        if (matches) {
                            ItemStack result = holder.value().getResultItem(serverLevel.registryAccess());
                            if (!result.isEmpty()) {
                                return result.copy();
                            }
                        }
                    }
                }
            } catch (Exception ignored) {
                // Create not installed or API mismatch — fall through to ash
            }
        }

        return getAsh();
    }

    private static ItemStack getAsh() {
        ResourceLocation ashId = ResourceLocation.parse("supplementaries:ash");
        if (BuiltInRegistries.ITEM.containsKey(ashId)) {
            return new ItemStack(BuiltInRegistries.ITEM.get(ashId));
        }
        return new ItemStack(Items.SAND);
    }

    // ==================== 3x3 MINING ====================

    private static final ThreadLocal<Boolean> IS_BREAKING = ThreadLocal.withInitial(() -> false);

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = player.getMainHandItem();
        Level level = player.level();

        if (level.isClientSide()) return;
        if (IS_BREAKING.get()) return;

        boolean isSledge = stack.getItem() instanceof SledgehammerItem;
        boolean isReinforced = stack.getItem() instanceof ReinforcedSledgehammerItem;
        if (!isSledge && !isReinforced) return;

        IS_BREAKING.set(true);
        try {
            BlockPos center = event.getPos();
            Direction playerFacing = player.getDirection();
            Direction.Axis axis = playerFacing.getAxis();

            // Determine plane perpendicular to player's looking direction
            List<BlockPos> toBreak = get3x3Positions(center, player);

            for (BlockPos bp : toBreak) {
                if (bp.equals(center)) continue;
                BlockState bs = level.getBlockState(bp);
                if (bs.isAir() || bs.getDestroySpeed(level, bp) < 0) continue;
                // Skip unbreakable blocks like bedrock
                if (bs.getDestroySpeed(level, bp) == -1) continue;

                level.destroyBlock(bp, true, player);
            }
        } finally {
            IS_BREAKING.set(false);
        }
    }

    private static List<BlockPos> get3x3Positions(BlockPos center, Player player) {
        List<BlockPos> positions = new ArrayList<>();

        // Use pitch to determine if player is looking mostly up/down
        float pitch = player.getXRot();
        if (Math.abs(pitch) > 65) {
            // Looking up or down - horizontal 3x3
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    positions.add(center.offset(dx, 0, dz));
                }
            }
        } else {
            // Looking sideways - vertical 3x3 perpendicular to facing
            Direction facing = player.getDirection();
            if (facing.getAxis() == Direction.Axis.X) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        positions.add(center.offset(0, dy, dz));
                    }
                }
            } else {
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        positions.add(center.offset(dx, dy, 0));
                    }
                }
            }
        }
        return positions;
    }

    // ==================== HELPERS ====================

    private static void spawnItem(Level level, double x, double y, double z, ItemStack stack) {
        ItemEntity entity = new ItemEntity(level, x, y, z, stack);
        level.addFreshEntity(entity);
    }

    private static void damageSledgehammer(Level level, Player player, ItemStack stack, int amount) {
        if (!player.isCreative() && level instanceof ServerLevel serverLevel) {
            stack.hurtAndBreak(amount, serverLevel, (ServerPlayer) player,
                    item -> player.onEquippedItemBroken(item, net.minecraft.world.entity.EquipmentSlot.MAINHAND));
        }
    }
}
