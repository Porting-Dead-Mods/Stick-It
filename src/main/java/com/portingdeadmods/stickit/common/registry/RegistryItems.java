package com.portingdeadmods.stickit.common.registry;

import com.portingdeadmods.stickit.StickIt;
import com.portingdeadmods.stickit.common.item.ItemBlockPlacedItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;


public class RegistryItems {
    public static final ItemBlockPlacedItems placed_items = new ItemBlockPlacedItems(new Item.Properties());
    private static final Logger LOG = LogManager.getLogger();

    public static void init(RegisterEvent.RegisterHelper<Item> helper) {
        for (Field f : RegistryItems.class.getDeclaredFields()) {
            try {
                if (Modifier.isStatic(f.getModifiers())) {
                    if (Item.class.isAssignableFrom(f.getType())) {
                        ResourceLocation rl = ResourceLocation.fromNamespaceAndPath(StickIt.MOD_ID, f.getName());
                        LOG.info("Registering item: {}", rl);
                        Item item = (Item) f.get(null);
                        helper.register(rl, item);
                    }
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
