package com.portingdeadmods.stickit;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;

public final class StickIt {
    public static final String MOD_ID = "stickit";
    public static final Identifier PLACED_ITEMS_ID = id("placed_items");
    public static final Identifier RENDER_TYPE_ID = id("render_type");
    public static final TagKey<Item> UNPLACEABLE = TagKey.create(Registries.ITEM, id("unplaceable"));

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    public static boolean canInteract(ServerPlayer player, BlockPos pos) {
        double reach = player.blockInteractionRange() + 2;
        return player.distanceToSqr(Vec3.atCenterOf(pos)) < reach * reach && player.mayInteract(player.level(), pos);
    }
}
