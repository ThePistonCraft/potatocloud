package net.potatocloud.node.console;

import lombok.RequiredArgsConstructor;
import net.potatocloud.node.Node;
import net.potatocloud.node.command.CommandManager;
import org.jline.reader.EndOfFileException;
import org.jline.reader.UserInterruptException;

@RequiredArgsConstructor
public class ConsoleReader extends Thread {

    private final Console console;
    private final CommandManager commandManager;
    private final Node node;

    @Override
    public void run() {
        try {
            String input;
            while (!isInterrupted()) {
                input = console.getLineReader().readLine(console.getPrompt());
                if (input != null) {
                    commandManager.executeCommand(input);
                }
            }
        } catch (UserInterruptException | EndOfFileException e) {
            node.shutdown();
        }
    }
}