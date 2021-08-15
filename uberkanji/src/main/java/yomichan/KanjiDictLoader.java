package yomichan;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static yomichan.YomichanConfig.DIR_KANJI_DICT;

public class KanjiDictLoader {

    private static final Map<Character, List<Object>> map = new HashMap<>();
    private static final Gson GSON = new GsonBuilder().create();

    public static void load() {

        final List<Path> kanjiDeckFileList = getKanjiDictFileList();
        kanjiDeckFileList.forEach(path -> {
            final List<List<Object>> list = GSON.fromJson(loadFileToString(path), ArrayList.class);
            list.forEach(dictEntry ->
                    map.put(convertToKey(dictEntry.get(0)), dictEntry));
        });

    }

    private static char convertToKey(final Object candidate) {
        final String key = String.valueOf(candidate).trim();
        final long length = key.codePoints().count();
        if (length != 1) {
            throw new IllegalStateException("Candidate for dict key is expected to have length of 1, got length: " +
                    "" + length + ", key: '" + key + "'");
        }
        return key.charAt(0);
    }

    private static String loadFileToString(final Path path) {
        try {
            return Files.readString(path);
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static List<Path> getKanjiDictFileList() {
        try {
            final List<Path> fileList = Files.walk(DIR_KANJI_DICT)
                    .filter(KanjiDictLoader::isKanjiDictFile)
                    .collect(Collectors.toList());
            ensureFilesAreAsExpected(fileList);
            return fileList;
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static boolean isKanjiDictFile(final Path path) {
        final String fileName = path.getFileName().toString();
        return fileName.startsWith("kanji_bank") && fileName.endsWith(".json");
    }

    private static void ensureFilesAreAsExpected(final List<Path> fileList) {
        if (fileList.size() != 2 && fileList.size() != 1) { // One in case you use your edited deck as basis
            throw new IllegalStateException("Expected to have 2 kanji dict files, got: " + fileList.size());
        }

        fileList.forEach(file -> {
            if (!file.toFile().isFile()) {
                throw new IllegalStateException("Got path in list which is not a file: " + file.toAbsolutePath());
            }
        });
    }

    public static Map<Character, List<Object>> getDict() {
        return map;
    }


}
