package utils;

import com.google.common.base.Splitter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mariten.kanatools.KanaConverter;
import models.JishoTerm;
import models.KanjiData;
import models.jisho.JishoResult;
import models.jisho.Term;

import java.io.IOException;
import java.lang.Character.Subset;
import java.lang.Character.UnicodeBlock;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static core.Config.*;

public class JpHelpers {
    private JpHelpers() {
    }

    private static final int KATA_TO_HIRA = KanaConverter.OP_ZEN_KATA_TO_ZEN_HIRA;
    private static final int HIRA_TO_KATA = KanaConverter.OP_ZEN_HIRA_TO_ZEN_KATA;
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Set<Subset> japaneseBlocks = new HashSet();

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

    public static String hiraToKata(final String hiragana) {
        return KanaConverter.convertKana(hiragana, HIRA_TO_KATA);
    }

    public static List<JishoTerm> toJishoTermList(final JishoResult jishoResult) {
        final List<JishoTerm> jishoTerms = new ArrayList<>();
        jishoResult.getData().forEach(data -> {
            if (data.getTermList().isEmpty()) {
                throw new IllegalStateException("JishoResult term has no extractable term data: " + data.getSlug());
            }

            final Term term = data.getTermList().get(0);
            if (term.getWord() != null && term.getReading() != null) {
                jishoTerms.add(new JishoTerm(term, data));
            }
        });
        return jishoTerms;
    }


    public static List<KanjiData> getKanjiDataFromCsv() {

        final List<KanjiData> kanjiDataList = new ArrayList<>();
        try {
            Files.readAllLines(JISHO_KANJI_DATA_FILE).forEach(line -> {
                final List<String> row = Splitter.on("|").trimResults().splitToList(line);
                if (row.size() != KANJI_CSV_COLUMNS_EXPECTED_FULL_LINE && row.size() != KANJI_CSV_COLUMNS_EXPECTED_KANJI_AND_ID) {
                    throw new IllegalStateException("Line can't be parsed, expected columns: " +
                            "" + KANJI_CSV_COLUMNS_EXPECTED_FULL_LINE + " or " + KANJI_CSV_COLUMNS_EXPECTED_KANJI_AND_ID + ", " +
                            "got " + row.size() + " - Line: " + line);
                }
                kanjiDataList.add(new KanjiData(row.toArray(new String[row.size()])));

            });
        } catch (final IOException e) {
            throw new IllegalStateException("Couldn't read kanji file. ", e);
        }
        return kanjiDataList;
    }

    public static boolean isFollowedByKanjiOrNothing(final char kanji, final String word) {
        try {
            final int indexOfKanji = word.indexOf(kanji);
            if (indexOfKanji < 0) {
                return false; // kanji not in word
            }

            final String nextChar = word.substring(indexOfKanji + 1, indexOfKanji + 2);
            if (isKanji(nextChar.charAt(0))) {
                return true; // followed by kanji
            }
        } catch (final Exception e) {
            return true; // index out of bounds, end of word
        }
        return false; // catch all negative
    }

    public static boolean isFollowedByKana(final char kanji, final String word) {
        try {
            final int indexOfKanji = word.indexOf(kanji);
            if (indexOfKanji < 0) {
                return false; // kanji not in word
            }

            final String nextChar = word.substring(indexOfKanji + 1, indexOfKanji + 2);
            if (isHiragana(nextChar.charAt(0)) || isKatakana(nextChar.charAt(0))) {
                return true; // followed by kana
            }
        } catch (final Exception e) {
            return false; // index out of bounds, end of word
        }
        return false; // catch all negative
    }

    public static int countChars(final String value) {
        int count = 0;
        for (int i = 0; i < value.length(); i++) {
            System.out.println("Char: '" + value.charAt(i) + "'");
            count++;
        }
        return count;
    }


    /**
     * How to load the kanjiDataStatics.csv
     */
    public enum KanjiDataCsvType {
        USE_FULL_LINE,
        USE_ONLY_KANJI_AND_ID
    }


}
