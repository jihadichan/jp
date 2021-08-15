package yomichan;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static yomichan.YomichanConfig.DIR_KANJI_DECK;

public class KanjiDeckLoader {

    private static final Map<Character, KanjiDeckEntry> map = new HashMap<>();

    public static void load() {

        final List<Path> kanjiDeckFileList = getKanjiDeckFileList();
        kanjiDeckFileList.forEach(path -> {
            final List<String> lines = loadFileToList(path);
            lines.forEach(line -> {
                final KanjiDeckEntry kanjiDeckEntry = new KanjiDeckEntry(line);
                map.put(kanjiDeckEntry.getKanji(), kanjiDeckEntry);
            });
        });

    }

    private static List<String> loadFileToList(final Path path) {
        try {
            return Files.readAllLines(path);
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static List<Path> getKanjiDeckFileList() {
        try {
            final List<Path> fileList = Files.walk(DIR_KANJI_DECK)
                    .filter(KanjiDeckLoader::isKanjiDeckExportFile)
                    .collect(Collectors.toList());
            ensureFilesAreAsExpected(fileList);
            return fileList;
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static boolean isKanjiDeckExportFile(final Path path) {
        final String fileName = path.getFileName().toString();
        return fileName.startsWith("UberKanji") && fileName.endsWith(".txt");
    }

    private static void ensureFilesAreAsExpected(final List<Path> fileList) {
        if (fileList.size() != 2) {
            throw new IllegalStateException("Expected to have 2 kanji deck file, got: " + fileList.size());
        }

        fileList.forEach(file -> {
            if (!file.toFile().isFile()) {
                throw new IllegalStateException("Got path in list which is not a file: " + file.toAbsolutePath());
            }
        });
    }

    public static Map<Character, KanjiDeckEntry> getDeck() {
        return map;
    }
}
