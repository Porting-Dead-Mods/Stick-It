package com.portingdeadmods.stickit.network;

import com.portingdeadmods.stickit.StickIt;
import com.portingdeadmods.stickit.block.PlacedItemsBlockEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public record RotateTilePayload(BlockPos pos) implements CustomPacketPayload {
    public static final Type<RotateTilePayload> TYPE = new Type<>(StickIt.id("rotate_tile_payload"));
    public static final StreamCodec<ByteBuf, RotateTilePayload> STREAM_CODEC = BlockPos.STREAM_CODEC.map(RotateTilePayload::new, RotateTilePayload::pos);

    @Override
    public Type<RotateTilePayload> type() {
        return TYPE;
    }

    public void handle(ServerPlayer player) {
        if (StickIt.canInteract(player, pos) && player.level().getBlockEntity(pos) instanceof PlacedItemsBlockEntity placedItems) {
            placedItems.rotateTile();
            placedItems.setChanged();
        }
    }
}
