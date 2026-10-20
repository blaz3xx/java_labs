package wordcount;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DirectoryWordCounterTest {
    private static Path write(Path file, String text) throws IOException {
        Files.createDirectories(file.getParent());
        return Files.writeString(file, text, StandardCharsets.UTF_8);
    }

    @Test
    void countsFilesInRootAndInNestedDirectories(@TempDir Path root) throws Exception {
        Path a = write(root.resolve("a.txt"), "apple avocado banana");
        Path b = write(root.resolve("sub1/b.txt"), "Ant ant cat");
        Path c = write(root.resolve("sub1/deep/deeper/c.txt"), "alpha beta");
        Path d = write(root.resolve("sub2/d.txt"), "zebra");

        ScanResult result = new DirectoryWordCounter().scan(root, 'a');

        assertEquals(Map.of(a, 2, b, 2, c, 1), result.counts());
        assertEquals(4, result.textFilesScanned());
        assertEquals(5, result.totalWords());
        assertTrue(result.errors().isEmpty());
        assertFalse(result.counts().containsKey(d));
    }

    @Test
    void everyDirectoryIsVisited(@TempDir Path root) throws Exception {
        Files.createDirectories(root.resolve("x/y/z"));
        Files.createDirectories(root.resolve("empty"));

        ScanResult result = new DirectoryWordCounter().scan(root, 'a');

        assertEquals(5, result.directoriesVisited());
        assertTrue(result.counts().isEmpty());
    }

    @Test
    void eachDirectoryIsProcessedByItsOwnThread(@TempDir Path root) throws Exception {
        for (int i = 0; i < 5; i++) {
            write(root.resolve("dir" + i + "/f.txt"), "alpha");
        }
        Map<Thread, Boolean> threads = new ConcurrentHashMap<>();
        AtomicInteger logged = new AtomicInteger();
        DirectoryWordCounter counter = new DirectoryWordCounter(Set.of("txt"), null, message -> {
            threads.put(Thread.currentThread(), true);
            logged.incrementAndGet();
        });

        ScanResult result = counter.scan(root, 'a');

        assertEquals(6, logged.get());
        assertEquals(6, result.directoriesVisited());
        assertTrue(threads.size() > 1, "expected more than one thread but got " + threads.size());
    }

    @Test
    void onlyTxtFilesAreCounted(@TempDir Path root) throws Exception {
        Path txt = write(root.resolve("a.txt"), "alpha");
        write(root.resolve("b.java"), "alpha alpha");
        write(root.resolve("c.md"), "alpha");
        write(root.resolve("noextension"), "alpha");
        Path upper = write(root.resolve("UPPER.TXT"), "alpha alpha alpha");

        ScanResult result = new DirectoryWordCounter().scan(root, 'a');

        assertEquals(Map.of(txt, 1, upper, 3), result.counts());
    }

    @Test
    void excludedOutputFileIsNotCounted(@TempDir Path root) throws Exception {
        Path source = write(root.resolve("source.txt"), "alpha");
        Path output = write(root.resolve("result.txt"), "alpha alpha alpha alpha");

        ScanResult result = new DirectoryWordCounter(Set.of("txt"), output, message -> { }).scan(root, 'a');

        assertEquals(Map.of(source, 1), result.counts());
    }

    @Test
    void directoryNamedLikeTextFileIsNotTreatedAsFile(@TempDir Path root) throws Exception {
        Path good = write(root.resolve("good.txt"), "alpha");
        Files.createDirectory(root.resolve("folder.txt"));

        ScanResult result = new DirectoryWordCounter().scan(root, 'a');

        assertEquals(Map.of(good, 1), result.counts());
        assertTrue(result.errors().isEmpty());
    }

    @Test
    void resultIsSortedByPath(@TempDir Path root) throws Exception {
        write(root.resolve("c.txt"), "a");
        write(root.resolve("a.txt"), "a");
        write(root.resolve("b/z.txt"), "a");
        write(root.resolve("b/m.txt"), "a");

        ScanResult result = new DirectoryWordCounter().scan(root, 'a');

        List<Path> keys = List.copyOf(result.counts().keySet());
        assertEquals(keys.stream().sorted().toList(), keys);
    }

    @Test
    void reportContainsFileNamesAndCounts(@TempDir Path root) throws Exception {
        write(root.resolve("a.txt"), "alpha atom");
        write(root.resolve("sub/b.txt"), "apple");

        ScanResult result = new DirectoryWordCounter().scan(root, 'a');
        Path output = root.resolve("out/report.txt");
        Files.createDirectories(output.getParent());
        WordCountApp.writeReport(output, WordCountApp.buildReport(root, 'a', result));

        List<String> lines = Files.readAllLines(output, StandardCharsets.UTF_8);
        assertTrue(lines.contains("a.txt: 2"), lines.toString());
        assertTrue(lines.contains(Path.of("sub", "b.txt") + ": 1"), lines.toString());
        assertTrue(lines.get(lines.size() - 1).contains("total words: 3"), lines.toString());
    }

    @Test
    void manyDirectoriesDoNotDeadlock(@TempDir Path root) throws Exception {
        for (int i = 0; i < 200; i++) {
            write(root.resolve("d" + i + "/f.txt"), "alpha");
        }
        Path deep = root;
        for (int i = 0; i < 50; i++) {
            deep = deep.resolve("level" + i);
        }
        write(deep.resolve("deep.txt"), "alpha");

        ScanResult result = org.junit.jupiter.api.Assertions.assertTimeoutPreemptively(
                java.time.Duration.ofSeconds(20), () -> new DirectoryWordCounter().scan(root, 'a'));

        assertEquals(201, result.counts().size());
        assertEquals(201, result.totalWords());
        assertEquals(1 + 200 + 50, result.directoriesVisited());
    }
}
