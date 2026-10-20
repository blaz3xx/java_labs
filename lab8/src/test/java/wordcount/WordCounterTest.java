package wordcount;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WordCounterTest {
    private static int count(String text, char letter) throws IOException {
        return WordCounter.countInReader(new BufferedReader(new StringReader(text)), letter);
    }

    @Test
    void countsWordsIgnoringCase() throws IOException {
        assertEquals(4, count("Apple and avocado, ANT!", 'a'));
    }

    @Test
    void upperCaseLetterArgumentWorksToo() throws IOException {
        assertEquals(4, count("Apple and avocado, ANT!", 'A'));
    }

    @Test
    void doesNotCountWordsThatOnlyContainTheLetter() throws IOException {
        assertEquals(0, count("banana cat", 'a'));
    }

    @Test
    void nonAsciiLettersAreWords() throws IOException {
        assertEquals(2, count("\u00c9lan caf\u00e9 \u00e9t\u00e9", '\u00e9'));
    }

    @Test
    void apostropheAndHyphenKeepWordTogether() throws IOException {
        assertEquals(1, count("O'Brien", 'o'));
        assertEquals(1, count("well-known", 'w'));
        assertEquals(0, count("well-known", 'k'));
    }

    @Test
    void ignoresNumbersAndPunctuation() throws IOException {
        assertEquals(0, count("123 ... --- 4a", 'a'));
    }

    @Test
    void emptyTextGivesZero() throws IOException {
        assertEquals(0, count("", 'a'));
    }

    @Test
    void countsAcrossMultipleLines() throws IOException {
        assertEquals(3, count("one\now\nsome\nocean", 'o'));
    }

    @Test
    void countInFileReadsUtf8(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("a.txt");
        Files.writeString(file, "\u00c9clair \u00e9cole apple", StandardCharsets.UTF_8);
        assertEquals(2, WordCounter.countInFile(file, '\u00e9'));
    }

    @Test
    void invalidUtf8DoesNotBreakCounting(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("bad.txt");

        Files.write(file, new byte[]{'a', 'b', ' ', (byte) 0xFF, (byte) 0xFE, ' ', 'a', 'x'});
        assertEquals(2, WordCounter.countInFile(file, 'a'));
    }

    @Test
    void missingFileThrowsIoException(@TempDir Path dir) {
        assertThrows(IOException.class, () -> WordCounter.countInFile(dir.resolve("nope.txt"), 'a'));
    }
}
