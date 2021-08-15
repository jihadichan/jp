package core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static core.KanjiSet.KanjiType.Jinmeiyou;
import static core.KanjiSet.KanjiType.Jouyou;

public class KanjiSet {

    private static final Map<String, KanjiType> map = new HashMap<>();
    private static final Path jouyouPath = Paths.get("jouyou");
    private static final Path jinmeiyouPath = Paths.get("jinmeiyou");

    static {
        loadSets(map, jouyouPath, Jouyou);
        loadSets(map, jinmeiyouPath, Jinmeiyou);
    }

    public static KanjiType determineType(String kanji) {
        if (kanji.length() > 1) {
            throw new IllegalStateException("Length of string must be 1, problem with: " + kanji);
        }
        Optional<KanjiType> kanjiType = Optional.ofNullable(map.get(kanji));
        return kanjiType.orElse(KanjiType.None);
    }

    private static void loadSets(Map<String, KanjiType> map, Path path, KanjiType kanjiType) {
        try {
            Files.readAllLines(path).forEach(line -> {
                if (map.putIfAbsent(line.trim(), kanjiType) != null) {
                    throw new IllegalStateException("Duplicate kanji: " + line + " from " + path + " - Already got it as: " + map.get(line));
                }
            });
        } catch (IOException e) {
            throw new IllegalStateException("Can't open file: " + path, e);
        }
    }

    public enum KanjiType {
        Jouyou,
        Jinmeiyou,
        None
    }

}
