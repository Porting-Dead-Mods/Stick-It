package com.portingdeadmods.stickit.network;

import com.portingdeadmods.stickit.StickIt;
import com.portingdeadmods.stickit.item.PlacedItemsItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.BlockHitResult;

public record PlaceItemPayload(BlockHitResult hit, int renderType) implements CustomPacketPayload {
    public static final Type<PlaceItemPayload> TYPE = new Type<>(StickIt.id("place_item_payload"));
    public static final StreamCodec<ByteBuf, PlaceItemPayload> STREAM_CODEC = StreamCodec.composite(
            BlockHitResult.STREAM_CODEC, PlaceItemPayload::hit,
            ByteBufCodecs.INT, PlaceItemPayload::renderType,
            PlaceItemPayload::new
    );

    @Override
    public Type<PlaceItemPayload> type() {
        return TYPE;
    }

    public void handle(ServerPlayer player) {
        BlockPos pos = hit.getBlockPos();
        if (!StickIt.canInteract(player, pos) || !PlacedItemsItem.place(player, hit, renderType)) player.inventoryMenu.sendAllDataToRemote();
        player.connection.send(new ClientboundBlockUpdatePacket(player.level(), pos));
        player.connection.send(new ClientboundBlockUpdatePacket(player.level(), pos.relative(hit.getDirection())));
    }
}
