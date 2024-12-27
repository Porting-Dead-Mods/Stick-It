package com.portingdeadmods.plonk.data;

import com.mojang.serialization.Codec;
import com.portingdeadmods.plonk.Plonk;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class DataComponents {

    public static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(Plonk.MOD_ID);

    public static final Supplier<DataComponentType<Boolean>> HELD = COMPONENTS.registerComponentType("Held",
            builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    public static final Supplier<DataComponentType<Integer>> RENDER_TYPE = COMPONENTS.registerComponentType("RenderType",
            builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    public static final Supplier<DataComponentType<Integer>> TAG_VERSION = COMPONENTS.registerComponentType("TagVersion",
            builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    public static final Supplier<DataComponentType<Integer>> TILE_ROTATION = COMPONENTS.registerComponentType("TileRotation",
            builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    public static final Supplier<DataComponentType<Integer>> TAG_ITEM_ROTATION = COMPONENTS.registerComponentType("TagItemRotation",
            builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));



}
