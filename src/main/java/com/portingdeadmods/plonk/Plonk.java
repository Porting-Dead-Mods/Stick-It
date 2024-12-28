package com.portingdeadmods.plonk;

import com.portingdeadmods.plonk.client.ClientEvents;
import com.portingdeadmods.plonk.common.config.PlonkConfig;
import com.portingdeadmods.plonk.common.registry.RegistryBlocks;
import com.portingdeadmods.plonk.common.registry.RegistryItems;
import com.portingdeadmods.plonk.common.registry.RegistryTileEntities;
import com.portingdeadmods.plonk.common.tag.PlonkTags;
import com.portingdeadmods.plonk.data.StickItDataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(Plonk.MOD_ID)
public class Plonk {
    public static final String MOD_ID = "plonk";
    public static final String NAME = "Plonk";
    public static final String CARRY_ON_MOD_ID = "carryon";

    public Plonk(IEventBus modEventBus, ModContainer modContainer) {
        StickItDataComponents.COMPONENTS.register(modEventBus);
        modEventBus.addListener(this::onRegister);

        if (FMLEnvironment.dist.isClient()) {
            ClientEvents.init(modEventBus);
        }

        modContainer.registerConfig(ModConfig.Type.SERVER, PlonkConfig.SPEC);
        PlonkTags.init();
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
