package net.potatocloud.core.console;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

@RequiredArgsConstructor
public class Logger {

    private final Console console;
    private final File logsFolder;

    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public void info(String message) {
        log("INFO", "&a", message);
    }

    public void warn(String message) {
        log("WARN", "&e", message);
    }

    public void error(String message) {
        log("ERROR", "&c", message);
    }

    @SneakyThrows
    private void log(String logName, String logColor, String message) {
        final String time = TIME_FORMAT.format(new Date());

        final String colorizedMessage = "&8[&7" + time + " " + logColor + logName + "&8] &7" + message;

        final String uncoloredMessage = "[" + time + " " + logName + "] " + removeColorCodes(message);

        console.println(colorizedMessage);

        final String date = DATE_FORMAT.format(new Date());
        final File dayLog = new File(logsFolder, date + ".log");
        final File latestLog = new File(logsFolder, "latest.log");

        FileUtils.writeStringToFile(dayLog, uncoloredMessage + System.lineSeparator(), StandardCharsets.UTF_8, true);
        FileUtils.writeStringToFile(latestLog, uncoloredMessage + System.lineSeparator(), StandardCharsets.UTF_8, true);
    }

    private String removeColorCodes(String input) {
        return input.replaceAll("&.", "");
    }
}
