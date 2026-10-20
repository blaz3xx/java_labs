package app;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CipherTest {

    @Test
    void decryptRestoresTheOriginalText() {
        String text = "Hello, Привіт!";

        String encrypted = Cipher.encrypt(text, 'k');

        assertNotEquals(text, encrypted);
        assertEquals(text, Cipher.decrypt(encrypted, 'k'));
    }

    @Test
    void fileRoundTripWorks(@TempDir Path dir) throws Exception {
        Path source = dir.resolve("source.txt");
        Files.writeString(source, "line one\nрядок два\n");
        FileManager manager = new FileManager();

        manager.encryptFile(source.toString(), dir.resolve("enc.txt").toString(), 'x');
        manager.decryptFile(dir.resolve("enc.txt").toString(), dir.resolve("dec.txt").toString(), 'x');

        assertEquals(Files.readString(source), Files.readString(dir.resolve("dec.txt")));
    }

    @Test
    void findsTheLineWithTheMostWords(@TempDir Path dir) throws Exception {
        Path file = dir.resolve("text.txt");
        Files.writeString(file, "one two\nthree four five six\nseven\n");

        assertEquals("three four five six", new FileManager().findLineWithMostWords(file.toString()));
    }

    @Test
    void missingFileThrows(@TempDir Path dir) {
        assertThrows(NoSuchFileException.class,
                () -> new FileManager().findLineWithMostWords(dir.resolve("none.txt").toString()));
    }

    @Test
    void outputMustDifferFromInput(@TempDir Path dir) throws Exception {
        Path file = dir.resolve("a.txt");
        Files.writeString(file, "text");

        assertThrows(IllegalArgumentException.class,
                () -> new FileManager().encryptFile(file.toString(), file.toString(), 'k'));
    }
}
