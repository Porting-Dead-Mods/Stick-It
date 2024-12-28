package com.portingdeadmods.stickit.client.registry;

import com.portingdeadmods.stickit.client.render.tile.TESRPlacedItems;
import com.portingdeadmods.stickit.common.registry.RegistryTileEntities;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public class RegistryTESRs {

    public static void init() {
        BlockEntityRenderers.register(RegistryTileEntities.placed_items, TESRPlacedItems::new);
    }
}
