package com.portingdeadmods.stickit.common.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.HopperBlockEntity;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class ItemUtils {
    /**
     * Drop item on an entity
     *
     * @param entity Target entity
     * @param stack  Stack to drop
     * @return Resulting item entity if it was spawned
     */
    @Nullable
    public static ItemEntity dropItemOnEntity(LivingEntity entity, ItemStack stack) {
        if (stack.isEmpty())
            return null;
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        ItemEntity entityItem = new ItemEntity(entity.level(), x, y, z, stack.copy());

        return entity.level().addFreshEntity(entityItem) ? entityItem : null;
    }

    /**
     * Attempts to insert stack into inventory, returning the remaining items.
     *
     * @param inv   Inventory to insert into
     * @param stack Stack to insert
     * @return Remaining items if partially inserted, null if fully inserted, original stack if no insertion.
     */
    public static ItemStack insertStack(Container inv, ItemStack stack) {
        return insertStackAdv(inv, stack).remainder;
    }

    /**
     * Attempts to insert stack into inventory, returning information object about what happened.
     *
     * @param inv   Inventory to insert into
     * @param stack Stack to insert
     * @return The remaining items and slots inserted into
     * @see HopperBlockEntity
     */
    public static InsertStackResult insertStackAdv(Container inv, ItemStack stack) {
        if (stack.isEmpty())
            return new InsertStackResult(stack, new int[0]);
        int stackSizeLimit = Math.min(stack.getMaxStackSize(), inv.getMaxStackSize());
        int size = inv.getContainerSize();

        ArrayList<Integer> slots = new ArrayList<>();

        ItemStack remainder = stack;

        for (int slot = 0; slot < size && !remainder.isEmpty(); slot++) {
            if (!inv.canPlaceItem(slot, stack)) {
                continue;
            }
            ItemStack current = inv.getItem(slot);
            if (!current.isEmpty() && !ItemStack.isSameItemSameComponents(current, remainder)) continue;
            int toTransfer = Math.min(current.getCount() + remainder.getCount(), stackSizeLimit) - current.getCount();
            if (toTransfer <= 0) continue;
            if (current.isEmpty()) {
                current = remainder.copy();
                current.setCount(toTransfer);
            } else {
                current.setCount(current.getCount() + toTransfer);
            }
            // Don't modify input stack
            if (remainder == stack) remainder = stack.copy();
            remainder.setCount(remainder.getCount() - toTransfer);
            inv.setItem(slot, current);
            slots.add(slot);
        }
        int[] slotsArray = new int[slots.size()];
        for (int i = 0; i < slots.size(); i++) {
            slotsArray[i] = slots.get(i);
        }
        return new InsertStackResult(remainder, slotsArray);
    }

    /**
     * Get the maximum stack size for an ItemStack of Air.
     * This will usually be 64 but mods like StackUp! may change this value for items.
     *
     * @return maximum stack size of an air item stack
     */
    public static int fetchMaxItemStackSizeInternal() {
        // Diagnostic step: Return a constant to see if the VerifyError persists.
        // If this works, the issue is with the ItemStack creation/method call above.
        // If this still fails with aload_0, the problem is almost certainly an external
        // bytecode transformer incorrectly modifying this static method.
        return 64;
    }

    /**
     * Gets the identifier for the given stack.
     *
     * @param stack target stack
     * @return Item's Identifier
     */
    @Nullable
    public static ResourceLocation getIdentifier(ItemStack stack) {
        return BuiltInRegistries.ITEM.getKey(stack.getItem());
    }

    public static class InsertStackResult {
        /**
         * Remaining items if partially inserted, null if fully inserted, original stack if no insertion.
         */
        public final ItemStack remainder;
        /**
         * Slots in the target inventory that had items inserted into them
         */
        public final int[] slots;

        public InsertStackResult(ItemStack remainder, int[] slots) {
            this.remainder = remainder;
            this.slots = slots;
        }
    }
}
