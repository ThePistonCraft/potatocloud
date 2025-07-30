package net.potatocloud.node.console;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.potatocloud.node.Node;
import net.potatocloud.node.command.CommandManager;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;

import java.nio.charset.StandardCharsets;

@Getter
public class Console {

    private final Terminal terminal;
    private final LineReader lineReader;

    @Setter
    private String prompt;

    private final ConsoleReader consoleReader;

    @SneakyThrows
    public Console(CommandManager commandManager, Node node) {
        terminal = TerminalBuilder.builder()
                .name("potatocloud-console")
                .system(true)
                .encoding(StandardCharsets.UTF_8)
                .build();

        lineReader = LineReaderBuilder.builder()
                .terminal(terminal)
                .completer(new ConsoleCommandCompleter(commandManager))
                .build();

        this.prompt = getDefaultPrompt();

        consoleReader = new ConsoleReader(this, commandManager, node);
    }

    public void start() {
        clearScreen();

        if (Node.getInstance().getConfig().isEnableBanner()) {
            ConsoleBanner.display(this);
        }

        consoleReader.start();
    }

    public void println(String message) {
        lineReader.printAbove(ConsoleColor.format(message));
    }

    public String getDefaultPrompt() {
        return ConsoleColor.format(Node.getInstance().getConfig().getPrompt().replace("%user%", System.getProperty("user.name")));
    }

    public void clearScreen() {
        terminal.puts(InfoCmp.Capability.clear_screen);
        terminal.writer().print("\033[H\033[2J");
        updateScreen();
    }

    public void updateScreen() {
        terminal.flush();
        if (lineReader.isReading()) {
            lineReader.callWidget(LineReader.REDRAW_LINE);
            lineReader.callWidget(LineReader.REDISPLAY);
        }
    }

    @SneakyThrows
    public void close() {
        consoleReader.interrupt();
        terminal.close();
    }
}
