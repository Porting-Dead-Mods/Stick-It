package com.portingdeadmods.stickit;

import com.mojang.serialization.Codec;
import com.portingdeadmods.stickit.block.PlacedItemsBlock;
import com.portingdeadmods.stickit.block.PlacedItemsBlockEntity;
import com.portingdeadmods.stickit.item.PlacedItemsItem;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Set;
import java.util.function.Function;

public final class StickItRegistry {
    public static final DataComponentType<Integer> RENDER_TYPE = DataComponentType.<Integer>builder()
            .persistent(Codec.INT)
            .networkSynchronized(ByteBufCodecs.INT)
            .build();
    public static PlacedItemsBlock BLOCK;
    public static PlacedItemsItem ITEM;
    public static BlockEntityType<PlacedItemsBlockEntity> BLOCK_ENTITY;

    public static void init(Function<BlockBehaviour.Properties, PlacedItemsBlock> blockFactory) {
        BLOCK = blockFactory.apply(BlockBehaviour.Properties.of()
                .setId(ResourceKey.create(Registries.BLOCK, StickIt.PLACED_ITEMS_ID))
                .strength(0.3F)
                .sound(SoundType.STONE)
                .noOcclusion()
                .noTerrainParticles()
                .isValidSpawn((state, level, pos, type) -> false)
                .isRedstoneConductor((state, level, pos) -> false)
                .isSuffocating((state, level, pos) -> false)
                .isViewBlocking((state, level, pos) -> false));
        ITEM = new PlacedItemsItem(BLOCK, new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, StickIt.PLACED_ITEMS_ID))
                .useBlockDescriptionPrefix());
        BLOCK_ENTITY = new BlockEntityType<>(PlacedItemsBlockEntity::new, Set.of(BLOCK));
    }
}
