package net.potatocloud.core.console;

public class ExceptionMessageHandler {

    public ExceptionMessageHandler(Logger logger) {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            logger.error("Exception in thread \"" + t.getName() + "\": " + e);
            for (StackTraceElement el : e.getStackTrace()) {
                logger.error("  at " + el);
            }
        });
    }
}
