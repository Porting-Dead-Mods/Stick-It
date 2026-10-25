package com.portingdeadmods.stickit.client;

import com.portingdeadmods.stickit.StickItConfig;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class StickItConfigScreen {
    public static Screen create(Screen parent) {
        StickItConfig config = StickItConfig.get();
        return YetAnotherConfigLib.createBuilder()
                .title(Component.translatable("stickit.config.title"))
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("stickit.config.title"))
                        .option(Option.<Integer>createBuilder()
                                .name(Component.translatable("stickit.config.max_stack_size"))
                                .description(OptionDescription.of(Component.translatable("stickit.config.max_stack_size.desc")))
                                .binding(-1, () -> config.maxStackSize, value -> config.maxStackSize = value)
                                .controller(option -> IntegerFieldControllerBuilder.create(option).min(-1))
                                .build())
                        .group(ListOption.<String>createBuilder()
                                .name(Component.translatable("stickit.config.unplaceable_items"))
                                .description(OptionDescription.of(Component.translatable("stickit.config.unplaceable_items.desc")))
                                .binding(List.of(), () -> config.unplaceableItems, value -> config.unplaceableItems = new ArrayList<>(value))
                                .controller(StringControllerBuilder::create)
                                .initial("")
                                .build())
                        .build())
                .save(StickItConfig::save)
                .build()
                .generateScreen(parent);
    }
}
