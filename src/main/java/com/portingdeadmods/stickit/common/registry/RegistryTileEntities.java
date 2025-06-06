package com.portingdeadmods.stickit.common.registry;

import com.portingdeadmods.stickit.StickIt;
import com.portingdeadmods.stickit.common.tile.TilePlacedItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;


public class RegistryTileEntities {
    public static final BlockEntityType<TilePlacedItems> placed_items = BlockEntityType.Builder.of(TilePlacedItems::new, RegistryBlocks.placed_items).build(null);
    private static final Logger LOG = LogManager.getLogger();

    public static void init(RegisterEvent.RegisterHelper<BlockEntityType<?>> helper) {
        for (Field f : RegistryTileEntities.class.getDeclaredFields()) {
            try {
                if (Modifier.isStatic(f.getModifiers())) {
                    if (f.getType() == BlockEntityType.class) {
                        ResourceLocation rl = ResourceLocation.fromNamespaceAndPath(StickIt.MOD_ID, f.getName());
                        LOG.info("Registering tile entity: {}", rl);
                        BlockEntityType<?> type = (BlockEntityType<?>) f.get(null);
                        helper.register(rl, type);
                    }
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
