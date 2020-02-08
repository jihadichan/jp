package core;

import models.jisho.JishoResult;
import utils.JpHelpers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import static core.Config.JISHO_RESULT_FOLDER;
import static utils.JpHelpers.GSON;

public class JishoResultDictionary {

    private final Map<Character, JishoResult> knownJishoResult = new LinkedHashMap<>();

    public JishoResultDictionary() {
        this.loadKnownJishoResult();
    }

    private void loadKnownJishoResult() {

        try (Stream<Path> stream = Files.walk(JISHO_RESULT_FOLDER)) {
            stream
                    .filter(path -> !path.toFile().isDirectory())
                    .filter(path -> path.toFile().toString().endsWith("json"))
                    .forEach(path -> {
                        final String fileName = path.toFile().getName().replace(".json", "");
                        if (fileName.length() != 1) {
                            throw new IllegalStateException("File name must have length == 1, got: " + fileName + " - Path: " + path.toFile());
                        }

                        try {
                            final JishoResult jishoResult = GSON.fromJson(new String(Files.readAllBytes(path)), JishoResult.class);
                            if (jishoResult == null) {
                                throw new IllegalStateException("JSON loaded from file resulted in JishoResult as null, path: " + path);
                            }

                            final char kanji = fileName.charAt(0);
                            if (!JpHelpers.isKanji(kanji)) {
                                throw new IllegalArgumentException("File name is not a kanji, got: " + kanji);
                            }
                            this.knownJishoResult.put(kanji, jishoResult);

                        } catch (final IOException e) {
                            throw new IllegalStateException("Couldn't read file, got: " + path);
                        }
                    });
        } catch (final IOException e) {
            throw new IllegalStateException("Couldn't walk path " + JISHO_RESULT_FOLDER, e);
        }
    }

    public boolean isKanjiDataAvailable(final char kanji) {
        return this.knownJishoResult.containsKey(kanji);
    }

    public JishoResult get(final char kanji) {
        return this.knownJishoResult.get(kanji);
    }

    public int size() {
        return this.knownJishoResult.size();
    }
}
