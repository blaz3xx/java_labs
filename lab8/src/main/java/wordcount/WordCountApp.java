package wordcount;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class WordCountApp {
    public static final String DEFAULT_OUTPUT = "result.txt";

    public static List<String> buildReport(Path root, char letter, ScanResult result) {
        List<String> lines = new ArrayList<>();
        lines.add("Words starting with '" + letter + "' in text files of " + root);
        for (Map.Entry<Path, Integer> entry : result.counts().entrySet()) {
            lines.add(root.relativize(entry.getKey()) + ": " + entry.getValue());
        }
        lines.add("Files with matches: " + result.counts().size() + " of " + result.textFilesScanned()
                + ", total words: " + result.totalWords());
        return lines;
    }

    public static void writeReport(Path output, List<String> lines) throws IOException {
        Files.write(output, lines, StandardCharsets.UTF_8);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

        String dirText = args.length > 0 ? args[0] : ask(scanner, "Enter directory path: ");
        Path root = Paths.get(dirText).toAbsolutePath().normalize();
        if (!Files.isDirectory(root)) {
            System.err.println("Not a directory or does not exist: " + root);
            return;
        }

        String letterText = args.length > 1 ? args[1] : ask(scanner, "Enter a letter: ");
        if (letterText.codePointCount(0, letterText.length()) != 1
                || !Character.isLetter(letterText.charAt(0))) {
            System.err.println("Expected a single letter but got: \"" + letterText + "\"");
            return;
        }
        char letter = letterText.charAt(0);

        Path output = Paths.get(args.length > 2 ? args[2] : DEFAULT_OUTPUT).toAbsolutePath().normalize();

        DirectoryWordCounter counter = new DirectoryWordCounter(
                DirectoryWordCounter.TEXT_EXTENSIONS, output, System.out::println);

        try {
            ScanResult result = counter.scan(root, letter);
            writeReport(output, buildReport(root, letter, result));

            System.out.println();
            System.out.println("Directories processed (threads): " + result.directoriesVisited());
            for (String error : result.errors()) {
                System.err.println("Skipped: " + error);
            }

            System.out.println("=== Contents of " + output + " ===");
            Files.readAllLines(output, StandardCharsets.UTF_8).forEach(System.out::println);
        } catch (IOException e) {
            System.err.println("Result file error: " + e.getMessage());
        } catch (ExecutionException e) {
            System.err.println("Processing error: " + e.getCause());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Interrupted.");
        }
    }

    private static String ask(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.hasNextLine() ? scanner.nextLine().trim() : "";
    }
}
