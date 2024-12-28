package com.portingdeadmods.stickit.data;

import com.portingdeadmods.stickit.StickIt;
import com.portingdeadmods.stickit.common.registry.RegistryItems;
import com.portingdeadmods.stickit.common.tag.StickItTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

import static com.portingdeadmods.stickit.data.DataGenUtils.carryOn;

public class ItemTags extends ItemTagsProvider {
    public ItemTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTagProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTagProvider, StickIt.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        tag(StickItTags.Items.UNPLACEABLE)
                .add(RegistryItems.placed_items)
                .addOptional(carryOn("entity_item"))
                .addOptional(carryOn("tile_item"))
        ;
    }

    @Override
    public String getName() {
        return StickIt.NAME + " Item Tags";
    }
}
