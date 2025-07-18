package net.potatocloud.node.command.commands;

import lombok.RequiredArgsConstructor;
import net.potatocloud.node.Node;
import net.potatocloud.node.command.Command;

import java.util.List;

@RequiredArgsConstructor
public class ShutdownCommand implements Command {

    private final Node node;

    @Override
    public void execute(String[] args) {
        node.shutdown();
    }

    @Override
    public String getName() {
        return "shutdown";
    }

    @Override
    public String getDescription() {
        return "Shutdown the cloud";
    }

    @Override
    public List<String> getAliases() {
        return List.of("stop", "end");
    }

}
