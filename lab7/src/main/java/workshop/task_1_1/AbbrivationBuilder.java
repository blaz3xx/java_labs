package workshop.task_1_1;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AbbrivationBuilder {

    public static String build(List<String> list) {
        return list.stream()
                .filter(Objects::nonNull)
                .filter(word -> !word.isEmpty())
                .map(word -> word.substring(0, 1))
                .collect(Collectors.joining());
    }

}
