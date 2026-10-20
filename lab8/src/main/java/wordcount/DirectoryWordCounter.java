package wordcount;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class DirectoryWordCounter {
    public static final Set<String> TEXT_EXTENSIONS = Set.of("txt");

    private final Set<String> extensions;
    private final Path excludedFile;
    private final Consumer<String> logger;

    public DirectoryWordCounter(Set<String> extensions, Path excludedFile, Consumer<String> logger) {
        this.extensions = extensions;
        this.excludedFile = excludedFile == null ? null : excludedFile.toAbsolutePath().normalize();
        this.logger = logger;
    }

    public DirectoryWordCounter() {
        this(TEXT_EXTENSIONS, null, message -> { });
    }

    private static final class Context {
        final char letter;
        final ExecutorService pool;
        final Map<Path, Integer> counts = new ConcurrentSkipListMap<>();
        final Queue<String> errors = new ConcurrentLinkedQueue<>();
        final AtomicInteger files = new AtomicInteger();
        final AtomicInteger directories = new AtomicInteger();

        Context(char letter, ExecutorService pool) {
            this.letter = letter;
            this.pool = pool;
        }
    }

    public ScanResult scan(Path root, char letter) throws InterruptedException, ExecutionException {
        try (ExecutorService pool = Executors.newCachedThreadPool()) {
            Context context = new Context(letter, pool);
            pool.submit(directoryTask(root, context)).get();
            return new ScanResult(context.counts, context.files.get(), context.directories.get(),
                    new ArrayList<>(context.errors));
        }
    }

    private Callable<Void> directoryTask(Path directory, Context context) {
        return () -> {
            processDirectory(directory, context);
            return null;
        };
    }

    private void processDirectory(Path directory, Context context) throws InterruptedException, ExecutionException {
        context.directories.incrementAndGet();
        logger.accept("[" + Thread.currentThread().getName() + "] directory: " + directory);

        List<Future<Void>> children = new ArrayList<>();

        try (DirectoryStream<Path> entries = Files.newDirectoryStream(directory)) {
            for (Path entry : entries) {
                if (Files.isDirectory(entry, LinkOption.NOFOLLOW_LINKS)) {
                    children.add(context.pool.submit(directoryTask(entry, context)));
                } else if (isCountableTextFile(entry)) {
                    processFile(entry, context);
                }
            }
        } catch (IOException | RuntimeException e) {
            context.errors.add(directory + ": " + e);
        }

        for (Future<Void> child : children) {
            child.get();
        }
    }

    private boolean isCountableTextFile(Path path) {
        if (!Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS)) {
            return false;
        }

        if (excludedFile != null && path.toAbsolutePath().normalize().equals(excludedFile)) {
            return false;
        }
        String name = path.getFileName().toString().toLowerCase(Locale.ROOT);
        int dot = name.lastIndexOf('.');
        return dot >= 0 && extensions.contains(name.substring(dot + 1));
    }

    private void processFile(Path file, Context context) {
        try {
            int count = WordCounter.countInFile(file, context.letter);
            context.files.incrementAndGet();

            if (count > 0) {
                context.counts.put(file, count);
            }
        } catch (IOException e) {
            context.errors.add(file + ": " + e);
        }
    }
}
