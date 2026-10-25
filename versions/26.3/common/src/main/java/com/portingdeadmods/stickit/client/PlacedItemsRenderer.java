package com.portingdeadmods.stickit.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.portingdeadmods.stickit.block.PlacedItemsBlock;
import com.portingdeadmods.stickit.block.PlacedItemsBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;

public class PlacedItemsRenderer implements BlockEntityRenderer<PlacedItemsBlockEntity, PlacedItemsRenderer.State> {
    private final ItemModelResolver itemModelResolver;

    public PlacedItemsRenderer(BlockEntityRendererProvider.Context context) {
        itemModelResolver = context.itemModelResolver();
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(PlacedItemsBlockEntity placedItems, State state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(placedItems, state, partialTicks, cameraPosition, breakProgress);
        state.facing = placedItems.getBlockState().getValue(PlacedItemsBlock.FACING);
        state.tileRotation = placedItems.tileRotation();
        state.count = placedItems.displayCount();
        int seed = (int) placedItems.getBlockPos().asLong();
        for (int slot = 0; slot < state.count; slot++) {
            itemModelResolver.updateForTopItem(state.items[slot], placedItems.getItem(slot), ItemDisplayContext.FIXED, placedItems.getLevel(), null, seed + slot);
            state.renderTypes[slot] = placedItems.renderType(slot);
            state.rotations[slot] = placedItems.itemRotation(slot);
        }
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        int count = state.count;
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        switch (state.facing) {
            case DOWN -> {
                poseStack.rotateDegrees(Axis.XP, 180);
                poseStack.rotateDegrees(Axis.YP, 180);
            }
            case SOUTH -> poseStack.rotateDegrees(Axis.XP, 90);
            case NORTH -> {
                poseStack.rotateDegrees(Axis.XP, 90);
                poseStack.rotateDegrees(Axis.ZP, 180);
            }
            case EAST -> {
                poseStack.rotateDegrees(Axis.XP, 90);
                poseStack.rotateDegrees(Axis.ZP, -90);
            }
            case WEST -> {
                poseStack.rotateDegrees(Axis.XP, 90);
                poseStack.rotateDegrees(Axis.ZP, 90);
            }
        }
        poseStack.translate(0, -0.5, 0);
        poseStack.rotateDegrees(Axis.YP, -90f * state.tileRotation);
        for (int slot = 0; slot < count; slot++) {
            ItemStackRenderState item = state.items[slot];
            if (item.isEmpty()) continue;
            poseStack.pushPose();
            poseStack.translate(PlacedItemsBlockEntity.offsetX(slot, count), 0, PlacedItemsBlockEntity.offsetZ(slot, count));
            poseStack.rotateDegrees(Axis.YP, 180f - 22.5f * state.rotations[slot]);
            if (state.renderTypes[slot] == PlacedItemsBlockEntity.RENDER_TYPE_BLOCK) {
                poseStack.translate(0, 0.25, 0);
            } else {
                poseStack.translate(0, 2f / 48, 0);
                poseStack.rotateDegrees(Axis.XP, 90);
                if (count > 1) poseStack.scale(0.5F, 0.5F, 0.5F);
            }
            item.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
        poseStack.popPose();
    }

    public static class State extends BlockEntityRenderState {
        private final ItemStackRenderState[] items = new ItemStackRenderState[PlacedItemsBlockEntity.SIZE];
        private final int[] renderTypes = new int[PlacedItemsBlockEntity.SIZE];
        private final int[] rotations = new int[PlacedItemsBlockEntity.SIZE];
        private Direction facing = Direction.UP;
        private int tileRotation;
        private int count;

        private State() {
            Arrays.setAll(items, slot -> new ItemStackRenderState());
        }
    }
}
