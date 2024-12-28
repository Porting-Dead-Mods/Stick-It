package com.portingdeadmods.stickit.client.command;

import com.portingdeadmods.stickit.common.command.IPlonkCommand;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;

import java.util.LinkedList;
import java.util.List;

public class CommandClientPlonk implements IPlonkCommand {

    private final List<IPlonkCommand> subcommands = new LinkedList<>();

    public CommandClientPlonk() {
        addSubcommand(new CommandDumpRenderTypes());
    }

    private void addSubcommand(IPlonkCommand command) {
        subcommands.add(command);
    }

    @Override
    public String getName() {
        return "cstickit";
    }

    @Override
    public String getUsage(CommandSourceStack source) {
        return "/cstickit <subcommand> - Client Stick It! Commands";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> literal = build();
        for (IPlonkCommand subcommand : this.subcommands) {
            literal = literal.then(subcommand.build());
        }
        dispatcher.register(literal);
    }
}
