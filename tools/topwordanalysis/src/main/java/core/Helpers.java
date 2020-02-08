package core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;

public class Helpers {
    private Helpers() {
    }

    public static Set<String> loadFileAsSet(Path wordListPath) {
        Set<String> set = new LinkedHashSet<>();
        try {
            Files.readAllLines(wordListPath).forEach(line -> {
                line = line.trim();
                if (set.contains(line)) {
                    System.out.println("Duplicate word: " + line);
                }
                set.add(line);
            });
        } catch (IOException e) {
            throw new IllegalStateException("Can't open file: " + wordListPath, e);
        }
        return set;
    }
}
