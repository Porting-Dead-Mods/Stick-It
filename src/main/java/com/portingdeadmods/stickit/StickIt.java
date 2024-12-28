package com.portingdeadmods.stickit;

import com.portingdeadmods.stickit.client.ClientEvents;
import com.portingdeadmods.stickit.common.config.PlonkConfig;
import com.portingdeadmods.stickit.common.registry.RegistryBlocks;
import com.portingdeadmods.stickit.common.registry.RegistryItems;
import com.portingdeadmods.stickit.common.registry.RegistryTileEntities;
import com.portingdeadmods.stickit.common.tag.StickItTags;
import com.portingdeadmods.stickit.data.StickItDataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(StickIt.MOD_ID)
public class StickIt {
    public static final String MOD_ID = "stickit";
    public static final String NAME = "Stick It!";
    public static final String CARRY_ON_MOD_ID = "carryon";

    public StickIt(IEventBus modEventBus, ModContainer modContainer) {
        StickItDataComponents.COMPONENTS.register(modEventBus);
        modEventBus.addListener(this::onRegister);

        if (FMLEnvironment.dist.isClient()) {
            ClientEvents.init(modEventBus);
        }

        modContainer.registerConfig(ModConfig.Type.SERVER, PlonkConfig.SPEC);
        StickItTags.init();
    }

    public void onRegister(RegisterEvent event) {
        event.register(Registries.BLOCK, RegistryBlocks::init);
        event.register(Registries.ITEM, RegistryItems::init);
        event.register(Registries.BLOCK_ENTITY_TYPE, RegistryTileEntities::init);
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
