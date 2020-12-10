package utils;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mariten.kanatools.KanaConverter;
import com.moji4j.MojiConverter;

import java.io.File;
import java.io.IOException;
import java.lang.Character.Subset;
import java.lang.Character.UnicodeBlock;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static config.Config.SENTENCES_FILE;

public class Utils {

    private static final int KATA_TO_HIRA = KanaConverter.OP_ZEN_KATA_TO_ZEN_HIRA;
    private static final int HIRA_TO_KATA = KanaConverter.OP_ZEN_HIRA_TO_ZEN_KATA;
    public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
    private static final Set<Subset> japaneseBlocks = new HashSet();
    private static final MojiConverter mojiConverter = new MojiConverter();

    static {
        japaneseBlocks.add(Character.UnicodeBlock.KATAKANA);
        japaneseBlocks.add(Character.UnicodeBlock.HIRAGANA);
        japaneseBlocks.add(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS);
        japaneseBlocks.add(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A);
        // add other blocks as necessary
    }

    public static boolean isKanji(final char input) {
        final Character.UnicodeBlock block = Character.UnicodeBlock.of(input);
        return block.equals(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) || block.equals(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A);
    }

    public static boolean isHiragana(final char input) {
        final Character.UnicodeBlock block = Character.UnicodeBlock.of(input);
        return block.equals(Character.UnicodeBlock.HIRAGANA);
    }

    public static boolean isKatakana(final char input) {
        final Character.UnicodeBlock block = Character.UnicodeBlock.of(input);
        return block.equals(UnicodeBlock.KATAKANA);
    }

    public static String kataToHira(final String katakana) {
        return KanaConverter.convertKana(katakana, KATA_TO_HIRA);
    }

    public static String kanaToRomaji(String kana) {
        return mojiConverter.convertKanaToRomaji(kana);
    }

    public static List<String> loadCsvLinesAsList(String pathToFile) {
        final File file = new File(pathToFile);
        if (!file.exists()) {
            throw new IllegalStateException("Can't find " + pathToFile);
        }
        try {
            return Files.asCharSource(file, StandardCharsets.UTF_8).readLines();
        } catch (final IOException e) {
            throw new IllegalStateException("Couldn't read " + pathToFile, e);
        }
    }

}
