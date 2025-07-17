package net.potatocloud.node.command.commands;

import net.potatocloud.node.command.Command;

import java.util.List;

public class PlayerCommand implements Command {

    @Override
    public void execute(String[] args) {

    }

    @Override
    public String getName() {
        return "player";
    }

    @Override
    public String getDescription() {
        return "Manage players";
    }

    @Override
    public List<String> getAliases() {
        return List.of();
    }

    @Override
    public List<String> complete(String[] args) {
        return List.of();
    }
}
