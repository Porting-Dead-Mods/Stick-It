package com.portingdeadmods.stickit.client;

import com.leclowndu93150.baguettelib.config.ConfigScreens;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.portingdeadmods.stickit.StickIt;
import com.portingdeadmods.stickit.block.PlacedItemsBlockEntity;
import com.portingdeadmods.stickit.item.PlacedItemsItem;
import com.portingdeadmods.stickit.network.PlaceItemPayload;
import com.portingdeadmods.stickit.network.RotateTilePayload;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

public class StickItClient {
    public static final KeyMapping.Category CATEGORY = new KeyMapping.Category(StickIt.id("main"));
    public static final KeyMapping KEY_PLACE = new KeyMapping("key.stickit.place", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_P, CATEGORY);
    private static final double BLOCK_OFF_AXIS = 75;
    private static final double EPSILON = 0.001;

    public static void init() {
        ConfigScreens.register(StickIt.MOD_ID, StickItConfigScreen::create);
    }

    public static void tick() {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.screen != null || minecraft.getOverlay() != null) return;
        while (KEY_PLACE.consumeClick()) {
            if (minecraft.hitResult instanceof BlockHitResult hit && hit.getType() == HitResult.Type.BLOCK) use(player, hit);
        }
    }

    private static void use(LocalPlayer player, BlockHitResult hit) {
        ItemStack held = player.getMainHandItem();
        if (!held.isEmpty()) {
            int renderType = renderType(held);
            if (PlacedItemsItem.place(player, hit, renderType)) send(new PlaceItemPayload(hit, renderType));
        } else if (player.isShiftKeyDown() && !rotate(player.level(), hit.getBlockPos())) {
            rotate(player.level(), hit.getBlockPos().relative(hit.getDirection()));
        }
    }

    private static boolean rotate(Level level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof PlacedItemsBlockEntity placedItems)) return false;
        placedItems.rotateTile();
        send(new RotateTilePayload(pos));
        return true;
    }

    private static void send(CustomPacketPayload payload) {
        Minecraft.getInstance().getConnection().send(new ServerboundCustomPayloadPacket(payload));
    }

    private static int renderType(ItemStack stack) {
        Matrix4f difference = transform(stack, ItemDisplayContext.FIXED).invert().mul(transform(stack, ItemDisplayContext.GUI));
        float scaleX = length(difference.m00(), difference.m10(), difference.m20());
        float scaleY = length(difference.m01(), difference.m11(), difference.m21());
        float scaleZ = length(difference.m02(), difference.m12(), difference.m22());
        float m02 = difference.m02() / scaleZ;
        float m12 = difference.m12() / scaleZ;
        float m22 = difference.m22() / scaleZ;
        float m10 = difference.m10() / scaleX;
        float m11 = difference.m11() / scaleY;
        double yaw = Math.toDegrees(Math.atan2(m02, m22));
        double pitch = Math.toDegrees(-Math.atan2(m12, Math.sqrt(Math.pow(m10, 2) + Math.pow(m11, 2))));
        double roll = Math.toDegrees(Math.atan2(m10, m11));
        double offAxis = offAxis(pitch) + offAxis(yaw) + offAxis(roll);
        boolean block = BLOCK_OFF_AXIS - EPSILON <= offAxis && offAxis <= BLOCK_OFF_AXIS + EPSILON;
        return block ? PlacedItemsBlockEntity.RENDER_TYPE_BLOCK : PlacedItemsBlockEntity.RENDER_TYPE_ITEM;
    }

    private static Matrix4f transform(ItemStack stack, ItemDisplayContext context) {
        Minecraft minecraft = Minecraft.getInstance();
        ItemStackRenderState state = new ItemStackRenderState();
        minecraft.getItemModelResolver().updateForTopItem(state, stack, context, minecraft.level, null, 0);
        PoseStack poseStack = new PoseStack();
        state.firstLayer().itemTransform.apply(false, poseStack.last());
        return poseStack.last().pose();
    }

    private static float length(float x, float y, float z) {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    private static double offAxis(double angle) {
        double remainder = (360 + angle) % 90;
        return Math.min(Math.abs(remainder), Math.abs(remainder - 90));
    }
}
