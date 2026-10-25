package com.portingdeadmods.stickit.fabric;

import com.portingdeadmods.stickit.StickItRegistry;
import com.portingdeadmods.stickit.client.PlacedItemsRenderer;
import com.portingdeadmods.stickit.client.StickItClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public class StickItFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        StickItClient.init();
        KeyMapping.Category.register(StickItClient.CATEGORY.id());
        KeyMappingHelper.registerKeyMapping(StickItClient.KEY_PLACE);
        BlockEntityRenderers.register(StickItRegistry.BLOCK_ENTITY, PlacedItemsRenderer::new);
        ClientTickEvents.END_CLIENT_TICK.register(client -> StickItClient.tick());
    }
}
