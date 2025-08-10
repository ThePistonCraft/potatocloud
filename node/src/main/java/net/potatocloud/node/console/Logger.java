package net.potatocloud.node.console;

import lombok.Getter;
import lombok.SneakyThrows;
import net.potatocloud.node.Node;
import net.potatocloud.node.screen.Screen;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Logger {

    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final String LATEST_LOG_FILENAME = "latest.log";

    private final Console console;
    private final Path logsFolder;
    private final List<String> cachedLogs = new ArrayList<>();

    public Logger(Console console, Path logsFolder) {
        this.console = console;
        this.logsFolder = logsFolder;

        final File latestLogFile = logsFolder.resolve(LATEST_LOG_FILENAME).toFile();
        if (latestLogFile.exists()) {
            latestLogFile.delete();
        }
    }

    @Getter
    public enum Level {
        INFO("&a"),
        WARN("&e"),
        ERROR("&c"),
        COMMAND("&7");

        private final String colorCode;

        Level(String colorCode) {
            this.colorCode = colorCode;
        }
    }

    public void info(String message) {
        log(Level.INFO, message);
    }

    public void warn(String message) {
        log(Level.WARN, message);
    }

    public void error(String message) {
        log(Level.ERROR, message);
    }

    public void logCommand(String command) {
        log(Level.COMMAND, command);
    }

    @SneakyThrows
    private void log(Level level, String message) {
        final String time = TIME_FORMAT.format(new Date());
        final String date = DATE_FORMAT.format(new Date());

        String uncoloredMessage;
        String coloredMessage;

        if (level.equals(Level.COMMAND)) {
            coloredMessage = console.getPrompt() + message;
            uncoloredMessage = removeColorCodes(coloredMessage);
        } else {
            uncoloredMessage = "[" + time + " " + level.name() + "] " + removeColorCodes(message);
            coloredMessage = "&8[&7" + time + " " + level.getColorCode() + level.name() + "&8] &7" + message;
        }

        final File dayLog = logsFolder.resolve(date + ".log").toFile();
        final File latestLog = logsFolder.resolve(LATEST_LOG_FILENAME).toFile();

        FileUtils.writeStringToFile(dayLog, uncoloredMessage + System.lineSeparator(), StandardCharsets.UTF_8, true);
        FileUtils.writeStringToFile(latestLog, uncoloredMessage + System.lineSeparator(), StandardCharsets.UTF_8, true);

        cachedLogs.add(coloredMessage);

        final boolean isNodeScreen = Node.getInstance().getScreenManager().getCurrentScreen().getName().equals(Screen.NODE_SCREEN);
        if (!level.equals(Level.COMMAND) && isNodeScreen) {
            console.println(coloredMessage);
        }
    }

    public List<String> getCachedLogs() {
        return Collections.unmodifiableList(cachedLogs);
    }

    private String removeColorCodes(String input) {
        String s = input;
        s = s.replaceAll("&.", "");
        s = s.replaceAll("\u001B\\[[;\\d]*m", "");
        return s;
    }
}
