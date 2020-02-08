package core;

import core.KanjiSet.KanjiType;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static core.Helpers.loadFileAsSet;

public class TopWordAnalyser {

    private static final Path wordListPath = Paths.get("top20kwords");

    public static void main(String[] args) {

//        listWordsWithSpecificKanjiAmount(2);
        countKanjiTypes();
    }

    private static void countKanjiTypes() {
        Set<String> uniqueKanji = new HashSet<>();
        Map<KanjiType, AtomicInteger> kanjiTypeCounts = new HashMap<>();
        loadFileAsSet(wordListPath).forEach(line -> {
            line.codePoints().forEach(codePoint -> {
                if (Character.UnicodeScript.of(codePoint).equals(Character.UnicodeScript.HAN)) {
                    String kanji = new String(Character.toChars(codePoint));
                    if (!uniqueKanji.contains(kanji)) {
                        uniqueKanji.add(kanji);

                        AtomicInteger count = kanjiTypeCounts.get(KanjiSet.determineType(kanji));
                        if (count == null) {
                            kanjiTypeCounts.put(KanjiSet.determineType(kanji), new AtomicInteger(1));
                        } else {
                            count.incrementAndGet();
                        }
                    }
                }
            });
        });

        System.out.println("Unique kanji: " + uniqueKanji.size());
        kanjiTypeCounts.forEach((type, count) -> {
            System.out.println("Type: " + type + " - Count: " + count.get());
        });
    }

    private static void listWordsWithSpecificKanjiAmount(int desiredKanjiAmount) {
        AtomicInteger matchedWordCount = new AtomicInteger(0);
        AtomicInteger lineNr = new AtomicInteger(1);
        loadFileAsSet(wordListPath).forEach(line -> {

            AtomicInteger kanjiCount = new AtomicInteger(0);
            line.codePoints().forEach(codePoint -> {
                if (Character.UnicodeScript.of(codePoint).equals(Character.UnicodeScript.HAN)) {
                    kanjiCount.incrementAndGet();
                }
            });

            if (kanjiCount.get() == desiredKanjiAmount) {
                matchedWordCount.incrementAndGet();
                System.out.println(lineNr.get() + " - " + line + " - Kanji: " + kanjiCount.get());
            }
            lineNr.incrementAndGet();
        });

        System.out.println("Total: " + matchedWordCount.get());
    }

}
