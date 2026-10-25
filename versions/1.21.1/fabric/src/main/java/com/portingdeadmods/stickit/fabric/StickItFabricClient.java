package com.portingdeadmods.stickit.fabric;

import com.portingdeadmods.stickit.StickItRegistry;
import com.portingdeadmods.stickit.client.PlacedItemsRenderer;
import com.portingdeadmods.stickit.client.StickItClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.client.player.ClientPickBlockGatherCallback;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;

public class StickItFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        StickItClient.init();
        KeyBindingHelper.registerKeyBinding(StickItClient.KEY_PLACE);
        BlockEntityRenderers.register(StickItRegistry.BLOCK_ENTITY, PlacedItemsRenderer::new);
        ClientTickEvents.END_CLIENT_TICK.register(client -> StickItClient.tick());
        ClientPickBlockGatherCallback.EVENT.register((player, hit) -> hit instanceof BlockHitResult blockHit
                ? StickItRegistry.BLOCK.pickStack(player.level(), blockHit.getBlockPos(), player)
                : ItemStack.EMPTY);
    }
}
