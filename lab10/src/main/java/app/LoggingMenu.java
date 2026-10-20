package app;

import i18n.Messages;
import logging.AppLogging;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class LoggingMenu {

    private static final Logger LOG = Logger.getLogger(LoggingMenu.class.getName());
    private static final int TAIL_LINES = 12;

    private LoggingMenu() {
    }

    public static void run() {
        while (true) {
            System.out.println();
            System.out.println(Messages.get("log.title"));
            printConfiguration();
            System.out.println("1 - " + Messages.get("log.menu.send"));
            System.out.println("2 - " + Messages.get("log.menu.logger"));
            System.out.println("3 - " + Messages.get("log.menu.console"));
            System.out.println("4 - " + Messages.get("log.menu.file"));
            System.out.println("5 - " + Messages.get("log.menu.show"));
            System.out.println("0 - " + Messages.get("log.menu.back"));
            String choice = Console.readLine(Messages.get("prompt.choice"));

            switch (choice) {
                case "1" -> sendMessages();
                case "2" -> changeLevel(AppLogging.root());
                case "3" -> changeLevel(AppLogging.consoleHandler());
                case "4" -> changeLevel(AppLogging.fileHandler());
                case "5" -> showLogFile();
                case "0" -> {
                    return;
                }
                default -> System.out.println(Messages.get("menu.unknown"));
            }
        }
    }

    private static void printConfiguration() {
        Handler console = AppLogging.consoleHandler();
        Handler file = AppLogging.fileHandler();
        System.out.println(Messages.get("log.config",
                AppLogging.root().getLevel(),
                console == null ? "-" : console.getLevel(),
                file == null ? "-" : file.getLevel(),
                AppLogging.logFile() == null ? "-" : AppLogging.logFile()));
    }

    private static void sendMessages() {
        System.out.println();
        AppLogging.sendDemoMessages(LOG);
        System.out.println();
        System.out.println(Messages.get("log.sent"));
        System.out.println(Messages.get("log.hint"));
        System.out.printf("%-9s %-8s %-8s %-8s%n", Messages.get("log.table.level"), Messages.get("log.table.logger"),
                Messages.get("log.table.console"), Messages.get("log.table.file"));
        for (Level level : AppLogging.DEMO_LEVELS) {
            System.out.printf("%-9s %-8s %-8s %-8s%n", level.getName(),
                    yesNo(AppLogging.reachesLogger(level)),
                    yesNo(AppLogging.reachesHandler(AppLogging.consoleHandler(), level)),
                    yesNo(AppLogging.reachesHandler(AppLogging.fileHandler(), level)));
        }
    }

    private static void changeLevel(Object target) {
        if (target == null) {
            return;
        }
        String text = Console.readLine(Messages.get("log.level.prompt"));
        Level level;
        try {
            level = Level.parse(text.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println(Messages.get("log.level.wrong"));
            return;
        }
        if (target instanceof Logger logger) {
            logger.setLevel(level);
        } else {
            ((Handler) target).setLevel(level);
        }
        LOG.log(Level.INFO, "Level changed to {0}", level);
        System.out.println(Messages.get("log.level.set", level));
    }

    private static void showLogFile() {
        Path file = AppLogging.logFile();
        try {
            AppLogging.fileHandler().flush();
            List<String> lines = file == null || !Files.exists(file)
                    ? List.of() : Files.readAllLines(file, StandardCharsets.UTF_8);
            if (lines.isEmpty()) {
                System.out.println(Messages.get("log.file.empty"));
                return;
            }
            System.out.println(Messages.get("log.file.tail", Math.min(TAIL_LINES, lines.size()), file));
            lines.subList(Math.max(0, lines.size() - TAIL_LINES), lines.size()).forEach(System.out::println);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Cannot read the log file", e);
        }
    }

    private static String yesNo(boolean value) {
        return Messages.get(value ? "log.yes" : "log.no");
    }
}
