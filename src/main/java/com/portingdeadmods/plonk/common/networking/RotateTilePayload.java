package com.portingdeadmods.plonk.common.networking;

import com.portingdeadmods.plonk.Plonk;
import com.portingdeadmods.plonk.common.tile.TilePlacedItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record RotateTilePayload(BlockPos pos) implements CustomPacketPayload, PayloadBase {
    public static final Type<RotateTilePayload> TYPE = new Type<>(Plonk.rl("rotate_tile_payload"));

    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, RotateTilePayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            RotateTilePayload::pos,
            RotateTilePayload::new
    );

    public static void action(RotateTilePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            ServerLevel level = player.serverLevel();

            double reach = player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE) + 2;

            if (player.distanceToSqr(payload.pos().getX() + 0.5, payload.pos().getY() + 0.5, payload.pos().getZ() + 0.5) < reach * reach) {
                if (level.getBlockEntity(payload.pos()) instanceof TilePlacedItems te) {
                    te.rotateTile();
                }
            }
        }).exceptionally(e -> {
            context.disconnect(Component.literal("Action failed:  " + e.getMessage()));
            return null;
        });

    }
}
