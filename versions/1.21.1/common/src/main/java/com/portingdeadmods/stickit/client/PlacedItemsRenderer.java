package com.portingdeadmods.stickit.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.portingdeadmods.stickit.block.PlacedItemsBlock;
import com.portingdeadmods.stickit.block.PlacedItemsBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class PlacedItemsRenderer implements BlockEntityRenderer<PlacedItemsBlockEntity> {
    private final ItemRenderer itemRenderer;

    public PlacedItemsRenderer(BlockEntityRendererProvider.Context context) {
        itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(PlacedItemsBlockEntity placedItems, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
        int count = placedItems.displayCount();
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        switch (placedItems.getBlockState().getValue(PlacedItemsBlock.FACING)) {
            case DOWN -> {
                poseStack.mulPose(Axis.XP.rotationDegrees(180));
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
            }
            case SOUTH -> poseStack.mulPose(Axis.XP.rotationDegrees(90));
            case NORTH -> {
                poseStack.mulPose(Axis.XP.rotationDegrees(90));
                poseStack.mulPose(Axis.ZP.rotationDegrees(180));
            }
            case EAST -> {
                poseStack.mulPose(Axis.XP.rotationDegrees(90));
                poseStack.mulPose(Axis.ZP.rotationDegrees(-90));
            }
            case WEST -> {
                poseStack.mulPose(Axis.XP.rotationDegrees(90));
                poseStack.mulPose(Axis.ZP.rotationDegrees(90));
            }
        }
        poseStack.translate(0, -0.5, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(-90f * placedItems.tileRotation()));
        int seed = (int) placedItems.getBlockPos().asLong();
        for (int slot = 0; slot < count; slot++) {
            ItemStack stack = placedItems.getItem(slot);
            if (stack.isEmpty()) continue;
            poseStack.pushPose();
            poseStack.translate(PlacedItemsBlockEntity.offsetX(slot, count), 0, PlacedItemsBlockEntity.offsetZ(slot, count));
            poseStack.mulPose(Axis.YP.rotationDegrees(180f - 22.5f * placedItems.itemRotation(slot)));
            if (placedItems.renderType(slot) == PlacedItemsBlockEntity.RENDER_TYPE_BLOCK) {
                poseStack.translate(0, 0.25, 0);
            } else {
                poseStack.translate(0, 2f / 48, 0);
                poseStack.mulPose(Axis.XP.rotationDegrees(90));
                if (count > 1) poseStack.scale(0.5F, 0.5F, 0.5F);
            }
            itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, light, OverlayTexture.NO_OVERLAY, poseStack, buffers, placedItems.getLevel(), seed + slot);
            poseStack.popPose();
        }
        poseStack.popPose();
    }
}
