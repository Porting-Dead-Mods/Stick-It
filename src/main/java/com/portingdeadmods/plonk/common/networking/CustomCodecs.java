package com.portingdeadmods.plonk.common.networking;

import com.jcraft.jorbis.Block;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public final class CustomCodecs {
    public static final StreamCodec<FriendlyByteBuf, BlockHitResult> BLOCK_HIT_RESULT_STREAM_CODEC = new StreamCodec<FriendlyByteBuf, BlockHitResult>() {
        @Override
        public void encode(FriendlyByteBuf buffer, BlockHitResult result) {
            BlockPos blockpos = result.getBlockPos();
            buffer.writeBlockPos(blockpos);
            buffer.writeEnum(result.getDirection());
            Vec3 vec3 = result.getLocation();
            buffer.writeFloat((float) (vec3.x - (double) blockpos.getX()));
            buffer.writeFloat((float) (vec3.y - (double) blockpos.getY()));
            buffer.writeFloat((float) (vec3.z - (double) blockpos.getZ()));
            buffer.writeBoolean(result.isInside());
        }

        @Override
        public BlockHitResult decode(FriendlyByteBuf buffer) {
            BlockPos blockpos = buffer.readBlockPos();
            Direction direction = buffer.readEnum(Direction.class);
            float x = buffer.readFloat();
            float y = buffer.readFloat();
            float z = buffer.readFloat();
            boolean inside = buffer.readBoolean();
            Vec3 vec3 = new Vec3((double) blockpos.getX() + (double) x, (double) blockpos.getY() + (double) y, (double) blockpos.getZ() + (double) z);
            return new BlockHitResult(vec3, direction, blockpos, inside);
        }
    };
}