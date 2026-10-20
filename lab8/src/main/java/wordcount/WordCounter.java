package wordcount;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class WordCounter {
    private static final Pattern WORD = Pattern.compile("(?<![\\p{L}\\p{N}\\p{M}])\\p{L}[\\p{L}\\p{M}'\u2019-]*");

    private WordCounter() {
    }

    public static int countInFile(Path file, char letter) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Files.newInputStream(file), StandardCharsets.UTF_8))) {
            return countInReader(reader, letter);
        }
    }

    public static int countInReader(BufferedReader reader, char letter) throws IOException {
        char wanted = Character.toLowerCase(letter);
        int count = 0;
        String line;
        while ((line = reader.readLine()) != null) {
            Matcher matcher = WORD.matcher(line);
            while (matcher.find()) {
                if (Character.toLowerCase(matcher.group().charAt(0)) == wanted) {
                    count++;
                }
            }
        }
        return count;
    }
}
