package com.portingdeadmods.stickit.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public interface IPlonkCommand {
    String getName();

    String getUsage(CommandSourceStack sender);

    default int getRequiredPermissionLevel() {
        return 4;
    }

    default int sendUsage(CommandSourceStack source, int exitCode) {
        source.sendSuccess(() -> Component.literal(getUsage(source)), true);
        return exitCode;
    }

    default LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal(getName())
                .requires(source -> source.hasPermission(getRequiredPermissionLevel()))
                .executes(context -> sendUsage(context.getSource(), SINGLE_SUCCESS));
    }

    default void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(build());
    }
}
