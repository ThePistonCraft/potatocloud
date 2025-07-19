package net.potatocloud.node.screen;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.potatocloud.node.console.Console;
import net.potatocloud.node.console.Logger;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class ScreenManager {

    private final Console console;
    private final Logger logger;

    private Screen currentScreen = null;
    private List<Screen> screens = new ArrayList<>();

    public void switchScreen(String screenName) {
        final Screen screen = getScreen(screenName);
        if (screen == null) {
            return;
        }

        currentScreen = screen;

        console.clearScreen();

        if (screen.getName().equals("node-screen")) {
            // get cached logs directly from the logger
            for (String log : logger.getCachedLogs()) {
                console.println(log);
            }
            console.setPrompt(console.getDefaultPrompt());
            return;
        }

        console.setPrompt("[" + screen.getName() + "] ");
        for (String log : screen.getCachedLogs()) {
            console.println(log);
        }
    }

    public Screen getScreen(String screenName) {
        return screens.stream()
                .filter(screen -> screen.getName().equals(screenName))
                .findFirst()
                .orElse(null);
    }

    public void addScreen(Screen screen) {
        screens.add(screen);
    }

    public void removeScreen(Screen screen) {
        screens.remove(screen);
    }
}
