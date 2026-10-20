package app;

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
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileManager {

    private static final Logger LOG = Logger.getLogger(FileManager.class.getName());

    public String findLineWithMostWords(String path) throws IOException {
        LOG.log(Level.INFO, "Searching for the line with the most words in {0}", path);
        String bestLine = null;
        int bestCount = 0;
        int lineNumber = 0;
        try (BufferedReader reader = Files.newBufferedReader(Path.of(path), StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                int count = countWords(line);
                LOG.log(Level.FINEST, "Line {0}: {1} words", new Object[]{lineNumber, count});
                if (count > bestCount) {
                    bestLine = line;
                    bestCount = count;
                    LOG.log(Level.FINE, "New longest line: number {0}, {1} words", new Object[]{lineNumber, count});
                }
            }
        }
        LOG.log(Level.INFO, "Read {0} lines, the longest has {1} words", new Object[]{lineNumber, bestCount});
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
        LOG.log(Level.INFO, "Encrypting {0} into {1}", new Object[]{inPath, outPath});
        try (Reader reader = Files.newBufferedReader(Path.of(inPath), StandardCharsets.UTF_8);
             Writer writer = new EncryptWriter(
                     Files.newBufferedWriter(prepareFile(outPath), StandardCharsets.UTF_8), key)) {
            reader.transferTo(writer);
        }
        LOG.log(Level.FINE, "Encrypted file written: {0} bytes", Files.size(Path.of(outPath)));
    }

    public void decryptFile(String inPath, String outPath, char key) throws IOException {
        checkDifferent(inPath, outPath);
        LOG.log(Level.INFO, "Decrypting {0} into {1}", new Object[]{inPath, outPath});
        try (Reader reader = new DecryptReader(
                     Files.newBufferedReader(Path.of(inPath), StandardCharsets.UTF_8), key);
             Writer writer = Files.newBufferedWriter(prepareFile(outPath), StandardCharsets.UTF_8)) {
            reader.transferTo(writer);
        }
        LOG.log(Level.FINE, "Decrypted file written: {0} bytes", Files.size(Path.of(outPath)));
    }

    public void saveObject(String path, Serializable object) throws IOException {
        LOG.log(Level.INFO, "Saving an object to {0}", path);
        try (ObjectOutputStream out = new ObjectOutputStream(
                new BufferedOutputStream(Files.newOutputStream(prepareFile(path))))) {
            out.writeObject(object);
        }
    }

    public <T> T loadObject(String path, Class<T> type) throws IOException, ClassNotFoundException {
        LOG.log(Level.INFO, "Loading an object from {0}", path);
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
