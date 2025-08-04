package net.potatocloud.node.console;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.service.Service;
import net.potatocloud.node.Node;
import net.potatocloud.node.command.CommandManager;
import net.potatocloud.node.screen.Screen;
import net.potatocloud.node.screen.ScreenManager;
import org.jline.jansi.Ansi;
import org.jline.reader.EndOfFileException;
import org.jline.reader.UserInterruptException;

@RequiredArgsConstructor
public class ConsoleReader extends Thread {

    private final Console console;
    private final CommandManager commandManager;
    private final ScreenManager screenManager;
    private final Node node;

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                final String input = console.getLineReader().readLine(console.getPrompt());

                if (input == null || input.isBlank()) {
                    // remove blank inputs
                    console.println(Ansi.ansi().cursorUpLine().eraseLine().cursorUp(1).toString());
                    continue;
                }

                final Screen currentScreen = screenManager.getCurrentScreen();
                final boolean isNodeScreen = currentScreen.getName().equals(Screen.NODE_SCREEN);

                if (isNodeScreen) {
                    // add executed commands into log file
                    node.getLogger().addCachedLog(input);

                    commandManager.executeCommand(input);
                    continue;
                }

                if (input.equalsIgnoreCase("leave") || input.equalsIgnoreCase("exit")) {
                    screenManager.switchScreen(Screen.NODE_SCREEN);
                    continue;
                }

                final Service service = node.getServiceManager().getService(currentScreen.getName());
                if (service == null) {
                    return;
                }

                service.executeCommand(input);
            }
        } catch (UserInterruptException e) {
            node.shutdown();
        } catch (EndOfFileException e) {
            console.clearScreen();
            console.updateScreen();
        }
    }
}