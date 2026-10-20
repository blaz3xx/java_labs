package wordcount;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public record ScanResult(Map<Path, Integer> counts,
                         int textFilesScanned,
                         int directoriesVisited,
                         List<String> errors) {
    public int totalWords() {
        return counts.values().stream().mapToInt(Integer::intValue).sum();
    }
}
