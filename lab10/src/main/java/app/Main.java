package app;

import i18n.Messages;
import logging.AppLogging;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    static final Path LOG_FILE = Path.of("logs", "app.log");

    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    static FileManager fileManager = new FileManager();
    static TagCounter tagCounter = new TagCounter();

    static void longestLine() {
        String path = Console.readNotEmpty(Messages.get("longest.prompt"));
        try {
            String line = fileManager.findLineWithMostWords(path);
            if (line == null) {
                System.out.println(Messages.get("longest.none"));
            } else {
                System.out.println(Messages.get("longest.result", FileManager.countWords(line)));
                System.out.println(line);
            }
        } catch (NoSuchFileException e) {
            System.out.println(Messages.get("error.notfound", path));
            LOG.log(Level.WARNING, "File not found: {0}", path);
        } catch (IllegalArgumentException e) {
            System.out.println(Messages.get("error.path", e.getMessage()));
            LOG.log(Level.WARNING, "Wrong path: {0}", path);
        } catch (IOException e) {
            System.out.println(Messages.get("error.read", e.getMessage()));
            LOG.log(Level.SEVERE, "Cannot read the file " + path, e);
        }
    }

    static void cipherMenu() {
        System.out.println("1 - " + Messages.get("cipher.encrypt.file"));
        System.out.println("2 - " + Messages.get("cipher.decrypt.file"));
        System.out.println("3 - " + Messages.get("cipher.encrypt.text"));
        String choice = Console.readLine(Messages.get("prompt.choice"));

        switch (choice) {
            case "1", "2" -> processFile(choice.equals("1"));
            case "3" -> {
                String text = Console.readNotEmpty(Messages.get("cipher.text"));
                char key = Console.readKey();
                String encrypted = Cipher.encrypt(text, key);
                LOG.log(Level.INFO, "Encrypted a text of {0} characters", text.length());
                System.out.println(Messages.get("cipher.encrypted", encrypted));
                System.out.println(Messages.get("cipher.decrypted", Cipher.decrypt(encrypted, key)));
            }
            default -> {
                System.out.println(Messages.get("error.unknown"));
                LOG.log(Level.WARNING, "Unknown cipher menu choice: {0}", choice);
            }
        }
    }

    static void processFile(boolean encrypt) {
        String inPath = Console.readNotEmpty(Messages.get("cipher.in"));
        String outPath = Console.readNotEmpty(Messages.get("cipher.out"));
        char key = Console.readKey();
        try {
            if (encrypt) {
                fileManager.encryptFile(inPath, outPath, key);
            } else {
                fileManager.decryptFile(inPath, outPath, key);
            }
            System.out.println(Messages.get("cipher.done", Path.of(outPath).toAbsolutePath()));
        } catch (NoSuchFileException e) {
            System.out.println(Messages.get("error.notfound", inPath));
            LOG.log(Level.WARNING, "File not found: {0}", inPath);
        } catch (IllegalArgumentException e) {
            System.out.println(Messages.get("error.generic", e.getMessage()));
            LOG.log(Level.WARNING, "Rejected input: {0}", e.getMessage());
        } catch (IOException e) {
            System.out.println(Messages.get("error.fileio", e.getMessage()));
            LOG.log(Level.SEVERE, "File error while processing " + inPath, e);
        }
    }

    static void countTags() {
        String url = Console.readNotEmpty(Messages.get("tags.prompt"));
        try {
            TagStatistics stats = tagCounter.count(url);
            printStatistics(stats);
            if (Console.isYes(Console.readLine(Messages.get("tags.save.ask")))) {
                saveStatistics(stats);
            }
        } catch (IllegalArgumentException | MalformedURLException e) {
            System.out.println(Messages.get("tags.badurl"));
            LOG.log(Level.WARNING, "Wrong URL: {0}", url);
        } catch (UnknownHostException e) {
            System.out.println(Messages.get("tags.unknownhost", e.getMessage()));
            LOG.log(Level.SEVERE, "Unknown host or no connection: " + url, e);
        } catch (IOException e) {
            System.out.println(Messages.get("tags.cannotload", e.getMessage()));
            LOG.log(Level.SEVERE, "Cannot load the page " + url, e);
        }
    }

    static void saveStatistics(TagStatistics stats) {
        String path = Console.readNotEmpty(Messages.get("tags.save.path"));
        try {
            fileManager.saveObject(path, stats);
            System.out.println(Messages.get("tags.saved", Path.of(path).toAbsolutePath()));
        } catch (IllegalArgumentException e) {
            System.out.println(Messages.get("error.path", e.getMessage()));
            LOG.log(Level.WARNING, "Wrong path: {0}", path);
        } catch (IOException e) {
            System.out.println(Messages.get("tags.savefail", e.getMessage()));
            LOG.log(Level.SEVERE, "Cannot save the file " + path, e);
        }
    }

    static void loadStatistics() {
        String path = Console.readNotEmpty(Messages.get("load.prompt"));
        try {
            printStatistics(fileManager.loadObject(path, TagStatistics.class));
        } catch (NoSuchFileException e) {
            System.out.println(Messages.get("error.notfound", path));
            LOG.log(Level.WARNING, "File not found: {0}", path);
        } catch (ClassNotFoundException | ClassCastException e) {
            System.out.println(Messages.get("load.notstats"));
            LOG.log(Level.WARNING, "File {0} does not contain tag statistics", path);
        } catch (IllegalArgumentException e) {
            System.out.println(Messages.get("error.path", e.getMessage()));
            LOG.log(Level.WARNING, "Wrong path: {0}", path);
        } catch (IOException e) {
            System.out.println(Messages.get("load.damaged"));
            LOG.log(Level.SEVERE, "Cannot read the file " + path, e);
        }
    }

    static void printStatistics(TagStatistics stats) {
        String date = stats.getCreatedAt()
                .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(Messages.getLocale()));
        System.out.println(Messages.get("stats.page", stats.getUrl(), date));
        System.out.println(Messages.get("stats.total", stats.getTotal()));
        System.out.println();
        System.out.println(Messages.get("stats.byname"));
        printEntries(stats.sortedByTag());
        System.out.println();
        System.out.println(Messages.get("stats.byfreq"));
        printEntries(stats.sortedByFrequency());
    }

    static void printEntries(List<Map.Entry<String, Integer>> entries) {
        for (Map.Entry<String, Integer> entry : entries) {
            System.out.printf("  %-12s %d%n", entry.getKey(), entry.getValue());
        }
    }

    static void chooseLanguage() {
        System.out.println(Messages.get("lang.title"));
        List<Locale> locales = Messages.SUPPORTED;
        for (int i = 0; i < locales.size(); i++) {
            System.out.println((i + 1) + " - " + Messages.languageName(locales.get(i)));
        }
        String choice = Console.readLine(Messages.get("prompt.choice"));
        try {
            Locale locale = locales.get(Integer.parseInt(choice) - 1);
            Messages.setLocale(locale);
            LOG.log(Level.INFO, "Language changed to {0}", locale);
            System.out.println(Messages.get("lang.changed", Messages.languageName(locale)));
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            System.out.println(Messages.get("lang.unknown"));
            LOG.log(Level.WARNING, "Unknown language choice: {0}", choice);
        }
    }

    static void printMenu() {
        System.out.println();
        System.out.println("1 - " + Messages.get("menu.longest"));
        System.out.println("3 - " + Messages.get("menu.cipher"));
        System.out.println("4 - " + Messages.get("menu.tags"));
        System.out.println("5 - " + Messages.get("menu.load"));
        System.out.println("6 - " + Messages.get("menu.language"));
        System.out.println("7 - " + Messages.get("menu.logging"));
        System.out.println("0 - " + Messages.get("menu.exit"));
    }

    public static void main(String[] args) {
        try {
            AppLogging.configure(LOG_FILE, System.err);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        LOG.log(Level.INFO, "Application started");

        while (true) {
            printMenu();
            String choice = Console.readLine(Messages.get("prompt.choice"));
            LOG.log(Level.FINE, "Menu choice: {0}", choice);

            switch (choice) {
                case "1" -> longestLine();
                case "3" -> cipherMenu();
                case "4" -> countTags();
                case "5" -> loadStatistics();
                case "6" -> chooseLanguage();
                case "7" -> LoggingMenu.run();
                case "0" -> {
                    System.out.println(Messages.get("exit.bye"));
                    LOG.log(Level.INFO, "Application finished");
                    AppLogging.close();
                    return;
                }
                default -> {
                    System.out.println(Messages.get("menu.unknown"));
                    LOG.log(Level.WARNING, "Unknown menu choice: {0}", choice);
                }
            }
        }
    }
}
