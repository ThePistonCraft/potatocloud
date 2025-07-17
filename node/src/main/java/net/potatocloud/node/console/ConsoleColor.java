package net.potatocloud.node.console;

import org.jline.jansi.Ansi;

public enum ConsoleColor {

    DARK_GRAY('8', 81, 81, 81),
    GRAY('7', 200, 200, 200),
    RED('c', 200, 70, 80),
    BLUE('9', 101, 101, 252),
    YELLOW('e', 255, 183, 3),
    GREEN('a', 0, 200, 120),
    WHITE('f', 255, 255, 255);

    private final char code;
    private final String ansiColor;

    ConsoleColor(char code, int red, int green, int blue) {
        this.code = code;
        this.ansiColor = Ansi.ansi().reset().fgRgb(red, green, blue).toString();
    }

    public static String format(String text) {
        for (ConsoleColor color : values()) {
            text = text.replace("&" + color.code, color.ansiColor);
        }
        return text + Ansi.ansi().reset();
    }
}
