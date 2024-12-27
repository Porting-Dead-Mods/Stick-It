package com.portingdeadmods.plonk.client.registry;

import com.portingdeadmods.plonk.client.render.tile.TESRPlacedItems;
import com.portingdeadmods.plonk.common.registry.RegistryTileEntities;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public class RegistryTESRs {

    public static void init() {
        BlockEntityRenderers.register(RegistryTileEntities.placed_items, TESRPlacedItems::new);
    }
}
