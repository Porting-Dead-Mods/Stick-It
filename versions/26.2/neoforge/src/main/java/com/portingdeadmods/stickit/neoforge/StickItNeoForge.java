package com.portingdeadmods.stickit.neoforge;

import com.portingdeadmods.stickit.StickIt;
import com.portingdeadmods.stickit.StickItConfig;
import com.portingdeadmods.stickit.StickItRegistry;
import com.portingdeadmods.stickit.network.PlaceItemPayload;
import com.portingdeadmods.stickit.network.RotateTilePayload;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(StickIt.MOD_ID)
public class StickItNeoForge {
    public StickItNeoForge(IEventBus modBus) {
        StickItConfig.load(FMLPaths.CONFIGDIR.get());

        modBus.addListener(RegisterEvent.class, event -> {
            if (event.getRegistryKey().equals(Registries.BLOCK)) StickItRegistry.init(NeoForgePlacedItemsBlock::new);
            event.register(Registries.DATA_COMPONENT_TYPE, StickIt.RENDER_TYPE_ID, () -> StickItRegistry.RENDER_TYPE);
            event.register(Registries.BLOCK, StickIt.PLACED_ITEMS_ID, () -> StickItRegistry.BLOCK);
            event.register(Registries.ITEM, StickIt.PLACED_ITEMS_ID, () -> StickItRegistry.ITEM);
            event.register(Registries.BLOCK_ENTITY_TYPE, StickIt.PLACED_ITEMS_ID, () -> StickItRegistry.BLOCK_ENTITY);
        });

        modBus.addListener(RegisterPayloadHandlersEvent.class, event -> {
            PayloadRegistrar registrar = event.registrar(StickIt.MOD_ID);
            registrar.playToServer(PlaceItemPayload.TYPE, PlaceItemPayload.STREAM_CODEC, (payload, context) -> payload.handle((ServerPlayer) context.player()));
            registrar.playToServer(RotateTilePayload.TYPE, RotateTilePayload.STREAM_CODEC, (payload, context) -> payload.handle((ServerPlayer) context.player()));
        });

        if (FMLEnvironment.getDist().isClient()) StickItNeoForgeClient.init(modBus);
    }
}
