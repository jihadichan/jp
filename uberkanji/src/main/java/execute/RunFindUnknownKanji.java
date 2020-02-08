package execute;

import com.google.common.io.Files;
import utils.JpHelpers;
import utils.JpHelpers.KanjiDataCsvType;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

import static java.lang.System.out;
import static utils.JpHelpers.KanjiDataCsvType.USE_FULL_LINE;

/**
 * Compares kanji in file "top2500freq" with "kanjiDataStatics.csv" (via JpHelpers.getKanjiDataFromCsv()) to check for unknown kanji
 */
public class RunFindUnknownKanji {

    public static void main(final String[] args) {
        Set<Character> potentialUnknownKanjiSet = getPotentialUnknownKanjiSet();
        Set<Character> knownKanjiSet = new HashSet<>();
        JpHelpers.getKanjiDataFromCsv().forEach(kanjiData -> knownKanjiSet.add(kanjiData.getKanji()));

        out.println("Unknown Kanji");
        potentialUnknownKanjiSet.forEach(kanji -> {
            if(!knownKanjiSet.contains(kanji)) {
                out.println(kanji);
            }
        });
    }

    private static Set<Character> getPotentialUnknownKanjiSet() {
        final Set<Character> set = new HashSet<>();
        try {
            Files.readLines(new File("top2500freq"), Charset.defaultCharset()).forEach(line -> {
                char[] charArray = line.trim().toCharArray();
                if(charArray.length > 1) {
                    throw new IllegalStateException("Resulting char array is expected to have length==1, got '"+line.trim()+"'");
                }
                set.add(charArray[0]);
            });
        } catch (final IOException e) {
            throw new IllegalStateException("Couldn't read file. ", e);
        }
        return set;
    }

}
