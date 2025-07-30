package net.potatocloud.node.console;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ConsoleBanner {

    private static final String BANNER_TEXT = "              _        _             _                 _ \n" +
            "  _ __   ___ | |_ __ _| |_ ___   ___| | ___  _   _  __| |\n" +
            " | '_ \\ / _ \\| __/ _` | __/ _ \\ / __| |/ _ \\| | | |/ _` |\n" +
            " | |_) | (_) | || (_| | || (_) | (__| | (_) | |_| | (_| |\n" +
            " | .__/ \\___/ \\__\\__,_|\\__\\___/ \\___|_|\\___/ \\__,_|\\__,_|\n" +
            " |_|                                                     ";

    public void display(Console console) {
        console.println(" ");
        console.println(BANNER_TEXT);
        console.println(" ");
    }
}
