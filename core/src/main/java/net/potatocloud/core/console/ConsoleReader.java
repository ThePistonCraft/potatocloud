package net.potatocloud.core.console;

import lombok.RequiredArgsConstructor;
import net.potatocloud.core.command.CommandManager;
import net.potatocloud.core.shutdown.ShutdownHandler;
import org.jline.reader.EndOfFileException;
import org.jline.reader.UserInterruptException;

@RequiredArgsConstructor
public class ConsoleReader extends Thread {

    private final Console console;
    private final CommandManager commandManager;
    private final ShutdownHandler shutdownHandler;

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
            shutdownHandler.shutdown();
        }
    }
}