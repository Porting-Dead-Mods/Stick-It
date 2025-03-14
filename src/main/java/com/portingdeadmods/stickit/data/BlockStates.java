package com.portingdeadmods.stickit.data;

import com.portingdeadmods.stickit.StickIt;
import com.portingdeadmods.stickit.common.registry.RegistryBlocks;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;

import static com.portingdeadmods.stickit.data.DataGenUtils.*;

public class BlockStates extends BlockStateProvider {
    public BlockStates(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, StickIt.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        final BlockModelBuilder placed_items_model = models().withExistingParent("placed_items", minecraft("block/block"))
                .renderType("cutout")
                .texture("all", stickit("block/placed_items"))
                .texture("particle", "#all")
                .element()
                .from(0, -0.01f, 0)
                .to(16, 0.01f, 16)
                .face(Direction.UP).texture("#all").end()
                .face(Direction.DOWN).uvs(0, 16, 16, 0).texture("#all").cullface(Direction.DOWN).end()
                .end();
        getVariantBuilder(RegistryBlocks.placed_items)
                .forAllStates(state -> {
                    Direction dir = state.getValue(BlockStateProperties.FACING);
                    return ConfiguredModel.builder()
                            .modelFile(placed_items_model)
                            .rotationX(dir == Direction.DOWN ? 180 : dir.getAxis().isHorizontal() ? 270 : 0)
                            .rotationY(dir == Direction.DOWN ? 180 : dir.getAxis().isHorizontal() ? ((int) dir.toYRot()) % 360 : 0)
                            .build();
                });
        itemModels().getBuilder("placed_items")
                .parent(placed_items_model);
    }

    @Nonnull
    @Override
    public String getName() {
        return StickIt.NAME + " Block States";
    }

}
