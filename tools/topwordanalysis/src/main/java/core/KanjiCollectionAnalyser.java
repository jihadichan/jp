package core;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static core.Helpers.loadFileAsSet;
import static java.lang.System.out;

/**
 * Compares the existing kanji collection form Anki with Top20k list, because 涅 doesn't exist there... why did I learn it...
 * Counts frequency of each kanji from collection how often they occur in topWords.
 */
public class KanjiCollectionAnalyser {

    private static final Set<String> topWords = loadFileAsSet(Paths.get("top20kwords"));
    private static final Map<String, AtomicInteger> kanjiCollection = createKanjiCountMap(loadFileAsSet(Paths.get("kanjicollection")));

    public static void main(String[] args) {

        countKanjiFrequencies(); // Freq == 0 means kanji isn't used
//        countUnknownKanjiFrequencies(); // Lists kanji which are not in collection
    }

    private static void countUnknownKanjiFrequencies() {
        Map<String, AtomicInteger> unknownKanji = new HashMap<>();
        Map<String, AtomicInteger> knownKanji = new HashMap<>();

        topWords.forEach(word -> {
            for (char character : word.toCharArray()) {
                if (KanjiDetect.isKanji(character)) {
                    String kanji = String.valueOf(character);
                    if (!kanjiCollection.containsKey(kanji)) {
                        AtomicInteger counter = unknownKanji.get(kanji);
                        if (counter == null) {
                            unknownKanji.put(kanji, new AtomicInteger(1));
                        } else {
                            counter.incrementAndGet();
                        }
                    } else {
                        AtomicInteger counter = knownKanji.get(kanji);
                        if (counter == null) {
                            knownKanji.put(kanji, new AtomicInteger(1));
                        } else {
                            counter.incrementAndGet();
                        }
                    }
                }
            }
        });


        // todo KNOWN KANJI
        knownKanji.forEach((kanji, counter) -> {
            out.println(kanji + "|" + counter);
        });

        // todo UNKNOWN KANJI
//        unknownKanji.forEach((kanji, counter) -> {
//            out.println(kanji + "|" + counter);
//        });
        out.println("topWords: " + topWords.size());
        out.println("kanjiCollection: " + kanjiCollection.size());
        out.println("unknownKanji: " + unknownKanji.size());
    }

    private static void countKanjiFrequencies() {

        topWords.forEach(word -> {
            kanjiCollection.forEach((kanji, counter) -> {
                if (word.contains(kanji)) {
                    counter.incrementAndGet();
                }
            });
        });

        kanjiCollection.forEach((kanji, counter) -> {
            out.println(kanji + "|" + counter);
        });

        out.println("topWords: " + topWords.size());
        out.println("kanjiCollection: " + kanjiCollection.size());
    }

    private static Map<String, AtomicInteger> createKanjiCountMap(Set<String> kanjiCollection) {
        Map<String, AtomicInteger> map = new HashMap<>();
        kanjiCollection.forEach(kanji -> map.put(kanji.trim(), new AtomicInteger(0)));
        return map;
    }

}
