package workshop.task_2_1;

import java.util.List;
import java.util.stream.Collectors;

public class DeveloperService {

    public static List<String> getLanguages(List<Developer> team) {
        return team.stream()
                .map(Developer::getLanguages)
                .flatMap(languages -> languages.stream())
                .distinct()
                .collect(Collectors.toList());
    }

}
