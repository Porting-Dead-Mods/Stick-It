package com.portingdeadmods.stickit.neoforge;

import com.portingdeadmods.stickit.StickItRegistry;
import com.portingdeadmods.stickit.client.PlacedItemsRenderer;
import com.portingdeadmods.stickit.client.StickItClient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.NeoForge;

public class StickItNeoForgeClient {
    public static void init(IEventBus modBus) {
        StickItClient.init();
        StickItClient.KEY_PLACE.setKeyConflictContext(KeyConflictContext.IN_GAME);
        modBus.addListener(RegisterKeyMappingsEvent.class, event -> event.register(StickItClient.KEY_PLACE));
        modBus.addListener(EntityRenderersEvent.RegisterRenderers.class, event -> event.registerBlockEntityRenderer(StickItRegistry.BLOCK_ENTITY, PlacedItemsRenderer::new));
        NeoForge.EVENT_BUS.addListener(ClientTickEvent.Post.class, event -> StickItClient.tick());
    }
}
