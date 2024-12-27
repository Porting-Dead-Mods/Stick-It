package com.portingdeadmods.plonk.common.item;

import com.portingdeadmods.plonk.common.config.PlonkConfig;
import com.portingdeadmods.plonk.common.registry.RegistryBlocks;
import com.portingdeadmods.plonk.common.tile.TilePlacedItems;
import com.portingdeadmods.plonk.data.DataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

public class ItemBlockPlacedItems extends BlockItem {

    public ItemBlockPlacedItems(Item.Properties builder) {
        super(RegistryBlocks.placed_items, builder);
    }

    public void setHeldStack(ItemStack stack, ItemStack held, int renderType) {
        stack.set(DataComponents.TAG_HELD, true);
        stack.set(DataComponents.TAG_RENDER_TYPE, renderType);
    }

    public ItemStack getHeldStack(ItemStack stack) {
        if (!stack.has(DataComponents.TAG_HELD))
            return ItemStack.EMPTY;

        ItemStack held = new ItemStack(stack.getItem());
        held.set(DataComponents.TAG_HELD,stack.get(DataComponents.TAG_HELD));
        return held;
    }

    public int getHeldRenderType(ItemStack stack) {
        if (!stack.has(DataComponents.TAG_RENDER_TYPE))
            return 0;
        return stack.get(DataComponents.TAG_RENDER_TYPE);
    }

    /**
     * Try to insert held item into the tile
     *
     * @param context UseOnContext that has the ItemBlockPlacedItems reference stack, which should contain renderType information.
     * @param tile    TilePlacedItems to insert into
     * @return true   if stack was at least partially successfully inserted
     */
    protected boolean tryInsertStack(UseOnContext context, TilePlacedItems tile) {
        ItemStack heldItem = getHeldStack(context.getItemInHand());
        int renderType = getHeldRenderType(context.getItemInHand());
        ItemStack remainder = tile.insertStack(heldItem, renderType);
        tile.setChanged();
        tile.clean();
        if (remainder != heldItem) {
            setHeldStack(context.getItemInHand(), remainder, renderType);
            return true;
        }
        return false;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack heldStack = getHeldStack(context.getItemInHand());
        if (heldStack.isEmpty() || !PlonkConfig.canPlace(heldStack))
            return InteractionResult.FAIL;

        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction facing = context.getClickedFace();

        TilePlacedItems tile = null;
        if (world.getBlockState(pos).getBlock() == RegistryBlocks.placed_items) {
            tile = (TilePlacedItems) world.getBlockEntity(pos);
        } else {
            BlockPos pos2 = pos.relative(facing);
            if (world.getBlockState(pos2).getBlock() == RegistryBlocks.placed_items) {
                tile = (TilePlacedItems) world.getBlockEntity(pos2);
            }
        }

        Player player = context.getPlayer();

        if (tile != null && tryInsertStack(context, tile)) {
            BlockState state = world.getBlockState(pos);
            SoundType soundtype = state.getBlock().getSoundType(state, world, pos, player);
            world.playSound(player, pos, soundtype.getPlaceSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
            return InteractionResult.SUCCESS;
        }

        // Upon failing to insert anything into existing placed items, try place a new block instead
        return super.useOn(context);
    }

    @Override
    public boolean placeBlock(BlockPlaceContext context, BlockState newState) {
        ItemStack heldStack = getHeldStack(context.getItemInHand());
        if (heldStack.isEmpty())
            return false;
        if (!super.placeBlock(context, newState))
            return false;

        TilePlacedItems tile = (TilePlacedItems) context.getLevel().getBlockEntity(context.getClickedPos());
        if (tile == null)
            return false;

        // Insert into freshly placed tile
        return tryInsertStack(context, tile);
    }
}
