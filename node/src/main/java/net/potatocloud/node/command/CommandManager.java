package net.potatocloud.node.command;

import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
public class CommandManager {

    private final Map<String, Command> commands = new HashMap<>();
    private final Map<String, Command> aliases = new HashMap<>();

    public void registerCommand(Command command) {
        commands.put(command.getName().toLowerCase(), command);
        command.getAliases().forEach(alias -> aliases.put(alias.toLowerCase(), command));
    }

    public void executeCommand(String line) {
        if (line.isBlank()) {
            return;
        }

        final String[] parts = line.trim().split(" ");
        final String input = parts[0].toLowerCase();

        Command command = commands.get(input);
        if (command == null) {
            command = aliases.get(input);
        }

        if (command == null) {
            return;
        }

        final String[] args = parts.length > 1 ? Arrays.copyOfRange(parts, 1, parts.length) : new String[0];
        command.execute(args);
    }

    public Command getCommand(String name) {
        final Command command = commands.get(name);
        if (command != null) {
            return command;
        }
        return aliases.get(name);
    }
}
