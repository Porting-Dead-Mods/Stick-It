package com.portingdeadmods.stickit.fabric;

import com.portingdeadmods.stickit.StickIt;
import com.portingdeadmods.stickit.StickItConfig;
import com.portingdeadmods.stickit.StickItRegistry;
import com.portingdeadmods.stickit.block.PlacedItemsBlock;
import com.portingdeadmods.stickit.network.PlaceItemPayload;
import com.portingdeadmods.stickit.network.RotateTilePayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerPickItemEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class StickItFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        StickItConfig.load(FabricLoader.getInstance().getConfigDir());
        StickItRegistry.init(PlacedItemsBlock::new);

        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, StickIt.RENDER_TYPE_ID, StickItRegistry.RENDER_TYPE);
        Registry.register(BuiltInRegistries.BLOCK, StickIt.PLACED_ITEMS_ID, StickItRegistry.BLOCK);
        Registry.register(BuiltInRegistries.ITEM, StickIt.PLACED_ITEMS_ID, StickItRegistry.ITEM);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, StickIt.PLACED_ITEMS_ID, StickItRegistry.BLOCK_ENTITY);

        PayloadTypeRegistry.serverboundPlay().register(PlaceItemPayload.TYPE, PlaceItemPayload.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(RotateTilePayload.TYPE, RotateTilePayload.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(PlaceItemPayload.TYPE, (payload, context) -> payload.handle(context.player()));
        ServerPlayNetworking.registerGlobalReceiver(RotateTilePayload.TYPE, (payload, context) -> payload.handle(context.player()));

        PlayerPickItemEvents.BLOCK.register((player, pos, state, includeData) -> state.is(StickItRegistry.BLOCK)
                ? StickItRegistry.BLOCK.pickStack(player.level(), pos, player)
                : null);
    }
}
