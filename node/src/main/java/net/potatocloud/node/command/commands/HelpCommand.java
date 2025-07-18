package net.potatocloud.node.command.commands;

import lombok.RequiredArgsConstructor;
import net.potatocloud.node.command.Command;
import net.potatocloud.node.command.CommandManager;
import net.potatocloud.node.console.Logger;

import java.util.List;

@RequiredArgsConstructor
public class HelpCommand implements Command {

    private final Logger logger;
    private final CommandManager commandManager;

    @Override
    public void execute(String[] args) {
        for (Command command : commandManager.getCommands().values()) {
            logger.info("&8» &a" + command.getName() + getAliases(command) + " &8- " + "&7" + command.getDescription());
        }
    }

    private String getAliases(Command command) {
        if (command.getAliases().isEmpty()) {
            return "";
        }
        return " &8[&7" + String.join(", ", command.getAliases()) + "&8]&7";
    }


    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Shows all commands";
    }

    @Override
    public List<String> getAliases() {
        return List.of("?");
    }

}
