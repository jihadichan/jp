package core;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.System.out;

public class FrequencyDictionary {
    private FrequencyDictionary() {
    }

    public static final Map<String, Integer> freqNF = loadList("freqNF");
    public static final Map<String, Integer> freqWK = loadList("freqWK");

    private static Map<String, Integer> loadList(final String fileName) {
        final Map<String, Integer> map = new LinkedHashMap<>();

        final AtomicInteger freqCounter = new AtomicInteger(1);
        try {
            for (final String line : Files.readLines(new File(fileName), Charset.defaultCharset())) {
                final String word = line.trim();
                if (containsOnlyLegalCharacters(word) && !map.containsKey(word)) {
                    map.put(word, freqCounter.getAndIncrement());
                }
            }
        } catch (final IOException e) {
            throw new IllegalStateException("Couldn't load " + fileName);
        }

        return map;
    }

    private static boolean containsOnlyLegalCharacters(final String text) {
        for (final char ch : text.toCharArray()) {
            final Character.UnicodeBlock kanji = Character.UnicodeBlock.of(ch);
            final Character.UnicodeBlock hiragana = Character.UnicodeBlock.of(ch);
            final Character.UnicodeBlock katakana = Character.UnicodeBlock.of(ch);

            if (!kanji.equals(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) &&
                    !kanji.equals(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A) &&
                    !hiragana.equals(Character.UnicodeBlock.HIRAGANA) &&
                    !katakana.equals(Character.UnicodeBlock.KATAKANA) &&
                    ch != '々') {
                out.println("Illegal char in " + text + " - char: " + ch);
                return false;
            }
        }

        return true;
    }
}
