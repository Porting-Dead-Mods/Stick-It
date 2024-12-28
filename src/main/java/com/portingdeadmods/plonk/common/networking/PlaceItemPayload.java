package com.portingdeadmods.plonk.common.networking;

import com.portingdeadmods.plonk.Plonk;
import com.portingdeadmods.plonk.common.registry.RegistryItems;
import com.portingdeadmods.plonk.common.util.EntityUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * @see ServerboundUseItemOnPacket
 */
public record PlaceItemPayload(BlockHitResult result, int renderType) implements CustomPacketPayload, PayloadBase {
    public static final Type<PlaceItemPayload> TYPE = new Type<>(Plonk.rl("place_item_payload"));

    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, PlaceItemPayload> STREAM_CODEC = StreamCodec.composite(
            CustomCodecs.BLOCK_HIT_RESULT_STREAM_CODEC,
            PlaceItemPayload::result,
            ByteBufCodecs.INT,
            PlaceItemPayload::renderType,
            PlaceItemPayload::new
    );

    public static void action(PlaceItemPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();

            ItemStack toPlace = new ItemStack(RegistryItems.placed_items, 1);
            ItemStack held = player.getMainHandItem();

            RegistryItems.placed_items.setHeldStack(toPlace, held, payload.renderType);
            EntityUtils.setHeldItemSilent(player, InteractionHand.MAIN_HAND, toPlace);
            if (toPlace.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, payload.result)).consumesAction()) {
                ItemStack newHeld = RegistryItems.placed_items.getHeldStack(toPlace);
                EntityUtils.setHeldItemSilent(player, InteractionHand.MAIN_HAND, newHeld);
            } else {
                EntityUtils.setHeldItemSilent(player, InteractionHand.MAIN_HAND, held);
            }
        }).exceptionally(e -> {
            context.disconnect(Component.literal("Action failed:  " + e.getMessage()));
            return null;
        });

    }
}
