package com.portingdeadmods.stickit;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;

public final class StickIt {
    public static final String MOD_ID = "stickit";
    public static final ResourceLocation PLACED_ITEMS_ID = id("placed_items");
    public static final ResourceLocation RENDER_TYPE_ID = id("render_type");
    public static final TagKey<Item> UNPLACEABLE = TagKey.create(Registries.ITEM, id("unplaceable"));

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static boolean canInteract(ServerPlayer player, BlockPos pos) {
        double reach = player.blockInteractionRange() + 2;
        return player.distanceToSqr(Vec3.atCenterOf(pos)) < reach * reach && player.level().mayInteract(player, pos);
    }
}
