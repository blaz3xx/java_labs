import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileManager {

    public String findLineWithMostWords(String path) throws IOException {
        String bestLine = null;
        int bestCount = 0;
        try (BufferedReader reader = Files.newBufferedReader(Path.of(path), StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                int count = countWords(line);
                if (count > bestCount) {
                    bestLine = line;
                    bestCount = count;
                }
            }
        }
        return bestLine;
    }

    public static int countWords(String line) {
        int count = 0;
        for (String token : line.trim().split("\\s+")) {
            if (token.matches(".*[\\p{L}\\p{N}].*")) {
                count++;
            }
        }
        return count;
    }

    public void encryptFile(String inPath, String outPath, char key) throws IOException {
        checkDifferent(inPath, outPath);
        try (Reader reader = Files.newBufferedReader(Path.of(inPath), StandardCharsets.UTF_8);
             Writer writer = new EncryptWriter(
                     Files.newBufferedWriter(prepareFile(outPath), StandardCharsets.UTF_8), key)) {
            reader.transferTo(writer);
        }
    }

    public void decryptFile(String inPath, String outPath, char key) throws IOException {
        checkDifferent(inPath, outPath);
        try (Reader reader = new DecryptReader(
                     Files.newBufferedReader(Path.of(inPath), StandardCharsets.UTF_8), key);
             Writer writer = Files.newBufferedWriter(prepareFile(outPath), StandardCharsets.UTF_8)) {
            reader.transferTo(writer);
        }
    }

    public void saveObject(String path, Serializable object) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new BufferedOutputStream(Files.newOutputStream(prepareFile(path))))) {
            out.writeObject(object);
        }
    }

    public <T> T loadObject(String path, Class<T> type) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(
                new BufferedInputStream(Files.newInputStream(Path.of(path))))) {
            return type.cast(in.readObject());
        }
    }

    private Path prepareFile(String path) throws IOException {
        Path file = Path.of(path).toAbsolutePath();
        Files.createDirectories(file.getParent());
        return file;
    }

    private void checkDifferent(String inPath, String outPath) {
        if (Path.of(inPath).toAbsolutePath().normalize().equals(Path.of(outPath).toAbsolutePath().normalize())) {
            throw new IllegalArgumentException("The output file must be different from the input file.");
        }
    }
}
