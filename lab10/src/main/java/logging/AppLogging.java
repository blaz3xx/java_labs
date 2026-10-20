package logging;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public final class AppLogging {

    public static final String LOGGER_NAME = "app";
    public static final Level LOGGER_LEVEL = Level.FINE;
    public static final Level CONSOLE_LEVEL = Level.WARNING;
    public static final Level FILE_LEVEL = Level.FINE;
    public static final Level[] DEMO_LEVELS = {
            Level.SEVERE, Level.WARNING, Level.INFO, Level.CONFIG, Level.FINE, Level.FINER, Level.FINEST
    };

    private static final Logger ROOT = Logger.getLogger(LOGGER_NAME);

    private static Handler consoleHandler;
    private static Handler fileHandler;
    private static Path logFile;

    private AppLogging() {
    }

    public static void configure(Path file, PrintStream console) throws IOException {
        close();

        ROOT.setUseParentHandlers(false);
        ROOT.setLevel(LOGGER_LEVEL);

        consoleHandler = new PrintStreamHandler(console);
        consoleHandler.setFormatter(new LineFormatter(false));
        consoleHandler.setLevel(CONSOLE_LEVEL);
        ROOT.addHandler(consoleHandler);

        try {
            Path absolute = file.toAbsolutePath();
            Files.createDirectories(absolute.getParent());
            fileHandler = new FileHandler(absolute.toString().replace('\\', '/'), true);
            fileHandler.setEncoding(StandardCharsets.UTF_8.name());
            fileHandler.setFormatter(new LineFormatter(true));
            fileHandler.setLevel(FILE_LEVEL);
            ROOT.addHandler(fileHandler);
            logFile = absolute;
        } catch (IOException | RuntimeException e) {
            throw new IOException("Cannot open the log file " + file + ": " + e.getMessage(), e);
        }
    }

    public static void close() {
        for (Handler handler : new Handler[]{consoleHandler, fileHandler}) {
            if (handler != null) {
                ROOT.removeHandler(handler);
                handler.close();
            }
        }
        consoleHandler = null;
        fileHandler = null;
        logFile = null;
    }

    public static Logger root() {
        return ROOT;
    }

    public static Handler consoleHandler() {
        return consoleHandler;
    }

    public static Handler fileHandler() {
        return fileHandler;
    }

    public static Path logFile() {
        return logFile;
    }

    public static void sendDemoMessages(Logger logger) {
        for (Level level : DEMO_LEVELS) {
            logger.log(level, "Demo message at level {0}", level.getName());
        }
    }

    public static boolean reachesLogger(Level level) {
        return ROOT.isLoggable(level);
    }

    public static boolean reachesHandler(Handler handler, Level level) {
        return handler != null && reachesLogger(level) && handler.isLoggable(new LogRecord(level, ""));
    }
}
