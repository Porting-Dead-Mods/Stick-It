package com.portingdeadmods.stickit.item;

import com.portingdeadmods.stickit.StickItConfig;
import com.portingdeadmods.stickit.StickItRegistry;
import com.portingdeadmods.stickit.block.PlacedItemsBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;

public class PlacedItemsItem extends BlockItem {
    public PlacedItemsItem(Block block, Properties properties) {
        super(block, properties);
    }

    public static boolean place(Player player, BlockHitResult hit, int renderType) {
        ItemStack carrier = new ItemStack(StickItRegistry.ITEM);
        hold(carrier, player.getMainHandItem());
        carrier.set(StickItRegistry.RENDER_TYPE, renderType);
        if (!carrier.useOn(new UseOnContext(player.level(), player, InteractionHand.MAIN_HAND, carrier, hit)).consumesAction()) return false;
        Inventory inventory = player.getInventory();
        inventory.setItem(inventory.selected, held(carrier));
        return true;
    }

    private static void hold(ItemStack carrier, ItemStack stack) {
        carrier.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(List.of(stack)));
    }

    private static ItemStack held(ItemStack carrier) {
        ItemContainerContents contents = carrier.get(DataComponents.CONTAINER);
        return contents == null ? ItemStack.EMPTY : contents.copyOne();
    }

    private static PlacedItemsBlockEntity placedItemsAt(Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof PlacedItemsBlockEntity placedItems ? placedItems : null;
    }

    private static boolean insert(UseOnContext context, PlacedItemsBlockEntity placedItems) {
        ItemStack carrier = context.getItemInHand();
        ItemStack held = held(carrier);
        ItemStack remainder = placedItems.insert(held, carrier.getOrDefault(StickItRegistry.RENDER_TYPE, PlacedItemsBlockEntity.RENDER_TYPE_ITEM));
        if (remainder == held) return false;
        placedItems.setChanged();
        placedItems.clean();
        hold(carrier, remainder);
        return true;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack held = held(context.getItemInHand());
        if (held.isEmpty() || !StickItConfig.canPlace(held)) return InteractionResult.FAIL;
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        PlacedItemsBlockEntity target = placedItemsAt(level, pos);
        if (target == null) target = placedItemsAt(level, pos.relative(context.getClickedFace()));
        if (target == null || !insert(context, target)) return super.useOn(context);
        BlockState clicked = level.getBlockState(pos);
        SoundType sound = clicked.getSoundType();
        level.playSound(context.getPlayer(), pos, sound.getPlaceSound(), SoundSource.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
        return InteractionResult.SUCCESS;
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        if (held(context.getItemInHand()).isEmpty() || !super.placeBlock(context, state)) return false;
        PlacedItemsBlockEntity placed = placedItemsAt(context.getLevel(), context.getClickedPos());
        return placed != null && insert(context, placed);
    }
}
