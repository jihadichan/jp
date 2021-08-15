package core;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static java.lang.Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS;
import static java.lang.Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A;

public class ConfusionDirectory {

    private static final String confusionBaseFile = "confusionBase";
    public static final Map<Character, Set<Character>> dict = loadConfusionBaseFile();

    private ConfusionDirectory() {
    }

    private static Map<Character, Set<Character>> loadConfusionBaseFile() {
        final Map<Character, Set<Character>> map = new LinkedHashMap<>();

        try {
            for (final String line : Files.readLines(new File(confusionBaseFile), Charset.defaultCharset())) {
                final String cleanLine = line.trim();
                ensureOnlyLegalCharacters(cleanLine);

                final Set<Character> kanjiSet = fromCharArray(cleanLine.toCharArray());
                for(char character : kanjiSet) {
                    if (!map.containsKey(character)) {
                        map.put(character, kanjiSet);
                    } else {
                        addCharsFromOtherEntries(map);
                    }
                }
            }
        } catch (final IOException e) {
            throw new IllegalStateException("Couldn't load " + confusionBaseFile);
        }

        return map;
    }

    private static void addCharsFromOtherEntries(Map<Character, Set<Character>> map) {
        // todo
    }

    private static void ensureOnlyLegalCharacters(final String text) {
        for (final char ch : text.toCharArray()) {
            final Character.UnicodeBlock kanji = Character.UnicodeBlock.of(ch);

            if (!kanji.equals(CJK_UNIFIED_IDEOGRAPHS) &&
                    !kanji.equals(CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A)) {
                throw new IllegalArgumentException("Illegal char in " + text + " - char: '" + ch + "'");
            }
        }
    }

    private static Set<Character> fromCharArray(final char[] charArray) {
        final Set<Character> set = new HashSet<>();
        for (final char character : charArray) {
            set.add(character);
        }
        return set;
    }

}
