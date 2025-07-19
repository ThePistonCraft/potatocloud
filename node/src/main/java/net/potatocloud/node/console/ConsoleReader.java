package net.potatocloud.node.console;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.service.Service;
import net.potatocloud.node.Node;
import net.potatocloud.node.command.CommandManager;
import net.potatocloud.node.screen.Screen;
import org.jline.jansi.Ansi;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;

@RequiredArgsConstructor
public class ConsoleReader extends Thread {

    private final Console console;
    private final CommandManager commandManager;
    private final Node node;

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                String input = console.getLineReader().readLine(console.getPrompt());
                if (input == null || input.isBlank()) {
                    console.println(Ansi.ansi().cursorUpLine().eraseLine().cursorUp(1).toString());
                    continue;
                }

                final Screen currentScreen = node.getScreenManager().getCurrentScreen();
                if (currentScreen == null || currentScreen.getName().equals("node-screen")) {
                    commandManager.executeCommand(input);
                } else {
                    if (input.equalsIgnoreCase("leave") || input.equalsIgnoreCase("exit")) {
                        node.getScreenManager().switchScreen("node-screen");
                    } else {
                        final Service service = node.getServiceManager().getService(currentScreen.getName());
                        if (service != null) {
                            service.executeCommand(input);
                        }
                    }
                }
            }
        } catch (UserInterruptException e) {
            node.shutdown();
        } catch (EndOfFileException e) {
            console.clearScreen();
            console.updateScreen();
        }
    }
}