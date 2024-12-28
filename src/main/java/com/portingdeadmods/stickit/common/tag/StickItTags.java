package com.portingdeadmods.stickit.common.tag;

import com.portingdeadmods.stickit.StickIt;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class StickItTags {
    public static void init() {
        Items.init();
    }

    public static class Items {

        public static final TagKey<Item> UNPLACEABLE = tag("unplaceable");

        public static void init() {
        }

        @SuppressWarnings("SameParameterValue")
        private static TagKey<Item> tag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(StickIt.MOD_ID, name));
        }
    }
}
