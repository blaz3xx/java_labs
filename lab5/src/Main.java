import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {

    static Scanner sc = new Scanner(System.in);
    static FileManager fileManager = new FileManager();
    static TagCounter tagCounter = new TagCounter();

    static String readLine(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    static String readNotEmpty(String prompt) {
        while (true) {
            String value = readLine(prompt);
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("The value cannot be empty.");
        }
    }

    static char readKey() {
        while (true) {
            String value = readLine("Key (one Latin letter or digit): ");
            if (value.length() == 1 && value.charAt(0) < 128 && Character.isLetterOrDigit(value.charAt(0))) {
                return value.charAt(0);
            }
            System.out.println("Wrong key! Example: k");
        }
    }

    static void longestLine() {
        String path = readNotEmpty("Text file (e.g. data/text.txt): ");
        try {
            String line = fileManager.findLineWithMostWords(path);
            if (line == null) {
                System.out.println("There are no words in the file.");
            } else {
                System.out.println("Line with the most words (" + FileManager.countWords(line) + "):");
                System.out.println(line);
            }
        } catch (NoSuchFileException e) {
            System.out.println("File not found: " + path);
        } catch (IllegalArgumentException e) {
            System.out.println("Wrong path: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Cannot read the file: " + e.getMessage());
        }
    }

    static void cipherMenu() {
        System.out.println("1 - Encrypt a file");
        System.out.println("2 - Decrypt a file");
        System.out.println("3 - Encrypt text from the keyboard");
        String choice = readLine("Your choice: ");

        switch (choice) {
            case "1", "2" -> processFile(choice.equals("1"));
            case "3" -> {
                String text = readNotEmpty("Text: ");
                char key = readKey();
                String encrypted = Cipher.encrypt(text, key);
                System.out.println("Encrypted: " + encrypted);
                System.out.println("Decrypted: " + Cipher.decrypt(encrypted, key));
            }
            default -> System.out.println("Unknown command.");
        }
    }

    static void processFile(boolean encrypt) {
        String inPath = readNotEmpty("Input file: ");
        String outPath = readNotEmpty("Output file: ");
        char key = readKey();
        try {
            if (encrypt) {
                fileManager.encryptFile(inPath, outPath, key);
            } else {
                fileManager.decryptFile(inPath, outPath, key);
            }
            System.out.println("Done: " + Path.of(outPath).toAbsolutePath());
        } catch (NoSuchFileException e) {
            System.out.println("File not found: " + inPath);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("File error: " + e.getMessage());
        }
    }

    static void countTags() {
        String url = readNotEmpty("Page URL (e.g. https://example.com): ");
        try {
            TagStatistics stats = tagCounter.count(url);
            printStatistics(stats);
            if (readLine("Save the result to a file? (y/n): ").equalsIgnoreCase("y")) {
                saveStatistics(stats);
            }
        } catch (IllegalArgumentException | MalformedURLException e) {
            System.out.println("Wrong URL. It must start with http:// or https://");
        } catch (UnknownHostException e) {
            System.out.println("Unknown site or no internet connection: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Cannot load the page: " + e.getMessage());
        }
    }

    static void saveStatistics(TagStatistics stats) {
        String path = readNotEmpty("File to save (e.g. results/tags.dat): ");
        try {
            fileManager.saveObject(path, stats);
            System.out.println("Saved to " + Path.of(path).toAbsolutePath());
        } catch (IllegalArgumentException e) {
            System.out.println("Wrong path: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Cannot save the file: " + e.getMessage());
        }
    }

    static void loadStatistics() {
        String path = readNotEmpty("Saved file (e.g. results/tags.dat): ");
        try {
            printStatistics(fileManager.loadObject(path, TagStatistics.class));
        } catch (NoSuchFileException e) {
            System.out.println("File not found: " + path);
        } catch (ClassNotFoundException | ClassCastException e) {
            System.out.println("This file does not contain tag statistics.");
        } catch (IllegalArgumentException e) {
            System.out.println("Wrong path: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Cannot read the file (it may be damaged or not saved by this program).");
        }
    }

    static void printStatistics(TagStatistics stats) {
        String date = stats.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        System.out.println("Page: " + stats.getUrl() + " (counted " + date + ")");
        System.out.println("Tags in total: " + stats.getTotal());
        System.out.println();
        System.out.println("a) By tag name:");
        printEntries(stats.sortedByTag());
        System.out.println();
        System.out.println("b) By frequency:");
        printEntries(stats.sortedByFrequency());
    }

    static void printEntries(List<Map.Entry<String, Integer>> entries) {
        for (Map.Entry<String, Integer> entry : entries) {
            System.out.printf("  %-12s %d%n", entry.getKey(), entry.getValue());
        }
    }

    public static void main(String[] args) {
        while (true) {
            System.out.println();
            System.out.println("1 - Task 1: line with the most words");
            System.out.println("3 - Task 3: encrypt / decrypt");
            System.out.println("4 - Task 4: count tags on a web page");
            System.out.println("5 - Load saved tag statistics");
            System.out.println("0 - Exit");
            String choice = readLine("Your choice: ");

            switch (choice) {
                case "1" -> longestLine();
                case "3" -> cipherMenu();
                case "4" -> countTags();
                case "5" -> loadStatistics();
                case "0" -> {
                    System.out.println("Bye!");
                    return;
                }
                default -> System.out.println("Unknown command, enter 1, 3, 4, 5 or 0.");
            }
        }
    }
}
