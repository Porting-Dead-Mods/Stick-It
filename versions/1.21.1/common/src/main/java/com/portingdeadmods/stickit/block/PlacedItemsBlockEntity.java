package com.portingdeadmods.stickit.block;

import com.portingdeadmods.stickit.StickItConfig;
import com.portingdeadmods.stickit.StickItRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Arrays;

public class PlacedItemsBlockEntity extends BlockEntity implements WorldlyContainer {
    public static final int RENDER_TYPE_ITEM = 0;
    public static final int RENDER_TYPE_BLOCK = 1;
    private static final int SIZE = 4;
    private static final int[] SLOTS = {0, 1, 2, 3};
    private static final int TILE_ROTATIONS = 4;
    private static final int ITEM_ROTATIONS = 16;
    private static final int FORMAT_VERSION = 1;
    private static final String TAG_VERSION = "Version";
    private static final String TAG_TILE_ROTATION = "TileRotation";
    private static final String TAG_ITEMS = "Items";
    private static final String TAG_SLOT = "Slot";
    private static final String TAG_RENDER_TYPE = "RenderType";
    private static final String TAG_ITEM_ROTATION = "ItemRotation";
    private static final String TAG_ITEM = "item";
    private static final String TAG_IS_BLOCK = "IsBlock";
    private static final AABB PLATE_BOX = new AABB(0, 0, 0, 1, 1 / 32.0, 1);
    private static final AABB SINGLE_ITEM_BOX = new AABB(0, 0, 0, 1, 3 / 32.0, 1);
    private static final AABB ITEM_BOX = new AABB(0.25, 0, 0.25, 0.75, 3 / 32.0, 0.75);
    private static final AABB BLOCK_BOX = new AABB(0.25, 0, 0.25, 0.75, 0.5, 0.75);

    private final NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
    private final int[] renderTypes = new int[SIZE];
    private final int[] rotations = new int[SIZE];
    private int tileRotation;
    private int displayCount;
    private VoxelShape[] slotShapes;
    private VoxelShape shape;

    public PlacedItemsBlockEntity(BlockPos pos, BlockState state) {
        super(StickItRegistry.BLOCK_ENTITY, pos, state);
        updateLayout();
    }

    public static double offsetX(int slot, int count) {
        return count <= 1 ? 0 : slot % 2 == 0 ? -0.25 : 0.25;
    }

    public static double offsetZ(int slot, int count) {
        return count <= 2 ? 0 : slot / 2 == 0 ? -0.25 : 0.25;
    }

    public int displayCount() {
        return displayCount;
    }

    public int renderType(int slot) {
        return renderTypes[slot];
    }

    public int itemRotation(int slot) {
        return rotations[slot];
    }

    public int tileRotation() {
        return tileRotation;
    }

    public VoxelShape shape(int slot) {
        return slot < 0 ? shape : slotShapes[slot];
    }

    public int pickSlot(Player player) {
        Vec3 from = player.getEyePosition(0);
        Vec3 to = from.add(player.getViewVector(0).scale(player.blockInteractionRange()));
        int picked = -1;
        double nearest = Double.MAX_VALUE;
        for (int slot = 0; slot < slotShapes.length; slot++) {
            BlockHitResult hit = slotShapes[slot].clip(from, to, worldPosition);
            if (hit == null || hit.getLocation().distanceToSqr(from) >= nearest) continue;
            nearest = hit.getLocation().distanceToSqr(from);
            picked = slot;
        }
        return picked;
    }

    public ItemStack insert(ItemStack stack, int renderType) {
        if (stack.isEmpty()) return stack;
        int free = (int) items.stream().filter(ItemStack::isEmpty).count();
        if (free == 0) return stack;
        ItemStack remainder = stack.copy();
        int perSlot = (int) Math.ceil((double) remainder.getCount() / free);
        for (int slot = 0; slot < SIZE && !remainder.isEmpty(); slot++) {
            if (!items.get(slot).isEmpty()) continue;
            items.set(slot, remainder.split(perSlot));
            renderTypes[slot] = renderType;
        }
        return remainder.isEmpty() ? ItemStack.EMPTY : remainder;
    }

    public void clean() {
        if (isEmpty()) {
            level.removeBlock(worldPosition, false);
            return;
        }
        if (compact()) setChanged();
        updateLayout();
    }

    public void rotateTile() {
        setTileRotation(tileRotation + 1);
    }

    public void setTileRotation(int rotation) {
        tileRotation = rotation % TILE_ROTATIONS;
        updateLayout();
    }

    public void rotateSlot(int slot) {
        rotations[slot] = (rotations[slot] + 1) % ITEM_ROTATIONS;
    }

    private boolean compact() {
        boolean changed = false;
        for (int slot = 0; slot < SIZE - 1; slot++) {
            if (!items.get(slot).isEmpty()) continue;
            for (int next = slot + 1; next < SIZE; next++) {
                if (items.get(next).isEmpty()) continue;
                items.set(slot, items.get(next));
                items.set(next, ItemStack.EMPTY);
                renderTypes[slot] = renderTypes[next];
                rotations[slot] = rotations[next];
                renderTypes[next] = RENDER_TYPE_ITEM;
                rotations[next] = 0;
                changed = true;
                break;
            }
        }
        return changed;
    }

    private void updateLayout() {
        displayCount = 0;
        for (int slot = 0; slot < SIZE; slot++) {
            if (!items.get(slot).isEmpty()) displayCount = slot + 1;
        }
        Direction facing = getBlockState().getValue(PlacedItemsBlock.FACING);
        slotShapes = new VoxelShape[Math.max(displayCount, 1)];
        shape = Shapes.empty();
        for (int slot = 0; slot < slotShapes.length; slot++) {
            AABB box = displayCount == 0 ? PLATE_BOX : renderTypes[slot] == RENDER_TYPE_BLOCK ? BLOCK_BOX : displayCount == 1 ? SINGLE_ITEM_BOX : ITEM_BOX;
            slotShapes[slot] = Shapes.create(orient(box.move(offsetX(slot, displayCount), 0, offsetZ(slot, displayCount)), facing));
            shape = Shapes.or(shape, slotShapes[slot]);
        }
    }

    private AABB orient(AABB box, Direction facing) {
        return new AABB(orient(box.minX, box.minY, box.minZ, facing), orient(box.maxX, box.maxY, box.maxZ, facing));
    }

    private Vec3 orient(double x, double y, double z, Direction facing) {
        x -= 0.5;
        y -= 0.5;
        z -= 0.5;
        for (int i = 0; i < tileRotation; i++) {
            double previousX = x;
            x = -z;
            z = previousX;
        }
        Vec3 oriented = switch (facing) {
            case UP -> new Vec3(x, y, z);
            case DOWN -> new Vec3(-x, -y, z);
            case SOUTH -> new Vec3(x, -z, y);
            case NORTH -> new Vec3(-x, -z, -y);
            case EAST -> new Vec3(y, -z, -x);
            case WEST -> new Vec3(-y, -z, x);
        };
        return oriented.add(0.5, 0.5, 0.5);
    }

    private void contentsChanged() {
        setChanged();
        if (level != null) level.scheduleTick(worldPosition, getBlockState().getBlock(), 1);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        boolean legacy = tag.getInt(TAG_VERSION) < FORMAT_VERSION;
        tileRotation = legacy ? 0 : Math.floorMod(tag.getInt(TAG_TILE_ROTATION), TILE_ROTATIONS);
        items.clear();
        Arrays.fill(renderTypes, RENDER_TYPE_ITEM);
        Arrays.fill(rotations, 0);
        ListTag entries = tag.getList(TAG_ITEMS, Tag.TAG_COMPOUND);
        for (int i = 0; i < entries.size(); i++) {
            CompoundTag entry = entries.getCompound(i);
            int slot = entry.getByte(TAG_SLOT) & 255;
            if (slot >= SIZE) continue;
            items.set(slot, ItemStack.parseOptional(registries, entry.contains(TAG_ITEM) ? entry.getCompound(TAG_ITEM) : entry));
            renderTypes[slot] = legacy && entry.contains(TAG_IS_BLOCK)
                    ? entry.getBoolean(TAG_IS_BLOCK) ? RENDER_TYPE_BLOCK : RENDER_TYPE_ITEM
                    : entry.getInt(TAG_RENDER_TYPE);
            rotations[slot] = legacy ? 0 : Math.floorMod(entry.getInt(TAG_ITEM_ROTATION), ITEM_ROTATIONS);
        }
        compact();
        updateLayout();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt(TAG_VERSION, FORMAT_VERSION);
        tag.putInt(TAG_TILE_ROTATION, tileRotation);
        ListTag entries = new ListTag();
        for (int slot = 0; slot < SIZE; slot++) {
            if (items.get(slot).isEmpty()) continue;
            CompoundTag entry = new CompoundTag();
            entry.putByte(TAG_SLOT, (byte) slot);
            entry.putInt(TAG_RENDER_TYPE, renderTypes[slot]);
            entry.putInt(TAG_ITEM_ROTATION, rotations[slot]);
            entry.put(TAG_ITEM, items.get(slot).save(registries));
            entries.add(entry);
        }
        tag.put(TAG_ITEMS, entries);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public int getContainerSize() {
        return SIZE;
    }

    @Override
    public boolean isEmpty() {
        return items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack removed = ContainerHelper.removeItem(items, slot, amount);
        if (!removed.isEmpty()) contentsChanged();
        return removed;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        stack.limitSize(getMaxStackSize());
        contentsChanged();
    }

    @Override
    public int getMaxStackSize() {
        return StickItConfig.stackLimit();
    }

    @Override
    public boolean stillValid(Player player) {
        return false;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return StickItConfig.canPlace(stack);
    }

    @Override
    public void clearContent() {
        items.clear();
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction side) {
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return true;
    }
}
