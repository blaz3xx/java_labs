package logging;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppLoggingTest {

    private final ByteArrayOutputStream consoleBytes = new ByteArrayOutputStream();
    private final Logger logger = Logger.getLogger("app.AppLoggingTest");
    private Path file;

    @BeforeEach
    void setUp(@TempDir Path dir) throws Exception {
        file = dir.resolve("logs").resolve("test.log");
        AppLogging.configure(file, new PrintStream(consoleBytes, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void tearDown() {
        AppLogging.close();
        AppLogging.root().setLevel(AppLogging.LOGGER_LEVEL);
    }

    private String console() {
        return consoleBytes.toString(StandardCharsets.UTF_8);
    }

    private String fileText() throws Exception {
        AppLogging.fileHandler().flush();
        return Files.readString(file, StandardCharsets.UTF_8);
    }

    @Test
    void consoleReceivesOnlyWarningsAndErrors() {
        AppLogging.sendDemoMessages(logger);

        assertTrue(console().contains("level SEVERE"));
        assertTrue(console().contains("level WARNING"));
        assertFalse(console().contains("level INFO"));
        assertFalse(console().contains("level FINE"));
    }

    @Test
    void fileReceivesProgressMessagesToo() throws Exception {
        AppLogging.sendDemoMessages(logger);

        String text = fileText();
        for (String level : new String[]{"SEVERE", "WARNING", "INFO", "CONFIG", "FINE"}) {
            assertTrue(text.contains("level " + level + System.lineSeparator()), level);
        }
    }

    @Test
    void loggerLevelStopsFinerMessagesEverywhere() throws Exception {
        AppLogging.sendDemoMessages(logger);

        assertFalse(fileText().contains("level FINER"));
        assertFalse(fileText().contains("level FINEST"));
        assertFalse(console().contains("level FINER"));
        assertFalse(AppLogging.reachesLogger(Level.FINER));
    }

    @Test
    void loweringTheLoggerLevelLetsFinestReachTheFileButNotTheConsole() throws Exception {
        AppLogging.root().setLevel(Level.ALL);
        AppLogging.fileHandler().setLevel(Level.ALL);

        AppLogging.sendDemoMessages(logger);

        assertTrue(fileText().contains("level FINEST"));
        assertFalse(console().contains("level FINEST"));
    }

    @Test
    void handlerLevelIsCheckedIndependentlyOfTheLoggerLevel() throws Exception {
        AppLogging.fileHandler().setLevel(Level.SEVERE);

        AppLogging.sendDemoMessages(logger);

        assertTrue(fileText().contains("level SEVERE"));
        assertFalse(fileText().contains("level WARNING"));
        assertTrue(console().contains("level WARNING"));
    }

    @Test
    void destinationTableMatchesTheConfiguration() {
        assertTrue(AppLogging.reachesHandler(AppLogging.consoleHandler(), Level.SEVERE));
        assertFalse(AppLogging.reachesHandler(AppLogging.consoleHandler(), Level.INFO));
        assertTrue(AppLogging.reachesHandler(AppLogging.fileHandler(), Level.FINE));
        assertFalse(AppLogging.reachesHandler(AppLogging.fileHandler(), Level.FINER));
    }

    @Test
    void fileContainsStackTraceButConsoleOnlyTheExceptionName() throws Exception {
        logger.log(Level.SEVERE, "Something failed", new IllegalStateException("boom"));

        assertTrue(console().contains("java.lang.IllegalStateException: boom"));
        assertFalse(console().contains("\tat "));
        assertTrue(fileText().contains("\tat "));
    }

    @Test
    void fileIsWrittenInUtf8() throws Exception {
        logger.log(Level.INFO, "Файл {0}", "дані.txt");

        assertTrue(fileText().contains("Файл дані.txt"));
        assertNotNull(AppLogging.logFile());
    }

    @Test
    void lineHasTimeLevelLoggerAndMessage() throws Exception {
        logger.info("hello");

        assertTrue(fileText().matches("(?s)\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3} INFO    AppLoggingTest - hello\\R"));
    }
}
