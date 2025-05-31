package com.portingdeadmods.stickit.common.config;

import com.portingdeadmods.stickit.StickIt;
import com.portingdeadmods.stickit.common.tag.StickItTags;
import com.portingdeadmods.stickit.common.util.ItemUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@EventBusSubscriber(modid = StickIt.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class PlonkConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.IntValue MAX_STACK_SIZE;
    private static final ModConfigSpec.ConfigValue<List<? extends String>> UNPLACEABLE_ITEMS;
    private static Set<ResourceLocation> unplaceableItemsSet = Collections.emptySet();

    static {
        BUILDER.comment("StickIt Configuration");

        MAX_STACK_SIZE = BUILDER
                .comment("Max stack size per slot (-1 or 0 to use default). Going above 64 needs a mod like StackUp!.")
                .defineInRange("maxStackSize", -1, -1, Integer.MAX_VALUE);

        UNPLACEABLE_ITEMS = BUILDER
                .comment("Items that cannot be placed down, in the format \"mod_id:item_id\" e.g. [\"minecraft:carrot\"]",
                        "You can also use the " + StickItTags.Items.UNPLACEABLE.location() + " item tag as well.")
                .defineList("unplaceableItems", Collections.emptyList(),
                        o -> o instanceof String && ResourceLocation.tryParse((String) o) != null);

        SPEC = BUILDER.build();
    }

    public static int getInventoryStackLimit() {
        final int maxStackSize = MAX_STACK_SIZE.get();
        return maxStackSize <= 0 ? ItemUtils.fetchMaxItemStackSizeInternal() : maxStackSize;
    }

    public static boolean canPlace(ItemStack stack) {
        if (stack.is(StickItTags.Items.UNPLACEABLE))
            return false;
        return !unplaceableItemsSet.contains(ItemUtils.getIdentifier(stack));
    }

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.Loading event) {
        refreshConfig();
    }

    @SubscribeEvent
    public static void onReload(final ModConfigEvent.Reloading event) {
        refreshConfig();
    }

    private static void refreshConfig() {
        unplaceableItemsSet = UNPLACEABLE_ITEMS.get().stream()
                .map(ResourceLocation::tryParse)
                .collect(Collectors.toSet());
    }
}