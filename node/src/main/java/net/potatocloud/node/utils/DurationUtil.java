package net.potatocloud.node.utils;

import lombok.experimental.UtilityClass;

import java.time.Duration;

@UtilityClass
public class DurationUtil {

    public static String formatDuration(long millis) {
        final Duration duration = Duration.ofMillis(millis);

        final long totalDays = duration.toDays();

        final long months = totalDays / 30;
        final long weeks = (totalDays % 30) / 7;
        final long days = totalDays % 7;

        final long hours = duration.toHours() % 24;
        final long minutes = duration.toMinutes() % 60;
        final long seconds = duration.getSeconds() % 60;

        final StringBuilder builder = new StringBuilder();
        if (months > 0) {
            builder.append(months).append("mo ");
        }
        if (weeks > 0) {
            builder.append(weeks).append("w ");
        }
        if (days > 0) {
            builder.append(days).append("d ");
        }
        if (hours > 0) {
            builder.append(hours).append("h ");
        }
        if (minutes > 0) {
            builder.append(minutes).append("m ");
        }
        builder.append(seconds).append("s");

        return builder.toString().trim();
    }

}
