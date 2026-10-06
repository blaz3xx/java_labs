import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TagStatistics implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String url;
    private final LocalDateTime createdAt;
    private final HashMap<String, Integer> counts;

    public TagStatistics(String url, HashMap<String, Integer> counts) {
        this.url = url;
        this.counts = counts;
        this.createdAt = LocalDateTime.now();
    }

    public String getUrl() {
        return url;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public int getTotal() {
        int total = 0;
        for (int count : counts.values()) {
            total += count;
        }
        return total;
    }

    public List<Map.Entry<String, Integer>> sortedByTag() {
        return new ArrayList<>(new TreeMap<>(counts).entrySet());
    }

    public List<Map.Entry<String, Integer>> sortedByFrequency() {
        List<Map.Entry<String, Integer>> list = new ArrayList<>(counts.entrySet());
        list.sort(Comparator.comparing((Map.Entry<String, Integer> e) -> e.getValue())
                .thenComparing(e -> e.getKey()));
        return list;
    }
}
