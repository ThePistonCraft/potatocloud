package net.potatocloud.node.command.commands;

import lombok.RequiredArgsConstructor;
import net.potatocloud.node.command.Command;
import net.potatocloud.node.console.Console;

import java.util.List;

@RequiredArgsConstructor
public class ClearCommand implements Command {

    private final Console console;

    @Override
    public void execute(String[] args) {
        console.clearScreen();
    }

    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getDescription() {
        return "Clear console screen";
    }

    @Override
    public List<String> getAliases() {
        return List.of("cls");
    }
}
