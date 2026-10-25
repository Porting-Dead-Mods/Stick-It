package com.portingdeadmods.stickit.neoforge;

import com.mojang.serialization.MapCodec;
import com.portingdeadmods.stickit.block.PlacedItemsBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

public class NeoForgePlacedItemsBlock extends PlacedItemsBlock {
    public static final MapCodec<NeoForgePlacedItemsBlock> CODEC = simpleCodec(NeoForgePlacedItemsBlock::new);

    public NeoForgePlacedItemsBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<NeoForgePlacedItemsBlock> codec() {
        return CODEC;
    }

    @Override
    protected boolean dropsContents(Level level) {
        return !level.restoringBlockSnapshots;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        return pickStack(level, pos, player);
    }
}
