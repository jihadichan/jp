package execute;

import utils.RomajiConverter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import static java.lang.System.out;

/**
 * Counts readings in backups/readings
 */
public class RunReadingStats {

    // private static final PollyTtsDirectory ttsDirectory = new PollyTtsDirectory(); todo delete, not used anymore

    private static final AtomicInteger linesInFile = new AtomicInteger();
    private static final Set<String> uniqueKanji = new HashSet<>();
    private static final Set<String> uniqueReadings = new HashSet<>();
    private static final Map<String, AtomicInteger> readingsFrequencies = new HashMap<>(); // single readings
    private static final Map<Integer, AtomicInteger> compoundReadingsSizes = new TreeMap<>(); // counts of sizes of all compound readings
    private static final Pattern fileNameCleanerPattern = Pattern.compile("(^readings/)|(\\.mp3$)");
    private static final String READINGS_FILE = "backups/readings";

    private static final Map<String, String> readingsMap = loadReadingsMap();

    public static void main(final String[] args) {


        frequencyStats(); // How often readings are used
//        compoundReadingLength(); // How long are the compounds of all readings combined
    }


    // ------------------------------------------------------------------------------------------ //
    // COMPOUND READING LENGTH
    // ------------------------------------------------------------------------------------------ //

    // todo replace PollyDict
//    private static void compoundReadingLength() {
//        final Map<Integer, AtomicInteger> lengths = new TreeMap<>();
//        ttsDirectory.getTtsDataMap().forEach((key, ttsData) -> {
//            final String[] readingsArray = fileNameCleanerPattern.matcher(ttsData.getFileName()).replaceAll("").split("-");
//            Arrays.sort(readingsArray);
//            final String compound = fileNameCleanerPattern.matcher(ttsData.getFileName()).replaceAll("").replace("-", "").trim();
//
//            final AtomicInteger lengthCounter = lengths.get(compound.length());
//            if (lengthCounter == null) {
//                lengths.put(compound.length(), new AtomicInteger(1));
//            } else {
//                lengthCounter.incrementAndGet();
//            }
//
//        });
//        out.println("Lengths: (i.e. amount of character, not syllables). NOTE: This counts the mp3 files, i.e. duplicates are not considered");
//        lengths.forEach((length, count) -> out.println("Length: " + length + "\tCount: " + count.get()));
//    }


    // ------------------------------------------------------------------------------------------ //
    // FREQUENCY STATS
    // ------------------------------------------------------------------------------------------ //

    private static void frequencyStats() {
        readingsMap.forEach((kanji, readings) -> {
            final String[] compoundReading = readings.split("\\.");

            final AtomicInteger crsCount = compoundReadingsSizes.get(compoundReading.length);
            if (crsCount == null) {
                compoundReadingsSizes.put(compoundReading.length, new AtomicInteger(1));
            } else {
                crsCount.incrementAndGet();
            }

            for (String reading : compoundReading) {
                reading = reading.trim();
                uniqueReadings.add(reading);
                final AtomicInteger counter = readingsFrequencies.get(reading);
                if (counter == null) {
                    readingsFrequencies.put(reading, new AtomicInteger(1));
                } else {
                    counter.incrementAndGet();
                }
            }
        });
        printFreqStats();
    }

    private static void printFreqStats() {
        final Map<String, Integer> comparableMap = new HashMap<>();
        readingsFrequencies.forEach((key, counter) -> comparableMap.put(key, counter.get()));
        final AtomicInteger nr = new AtomicInteger();
        comparableMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach(entry -> out.println(entry.getValue() + "|" + entry.getKey() + "|" + RomajiConverter.fromHiragana(entry.getKey())));

        out.println("----------------");
        out.println("Total Unique Readings: " + uniqueReadings.size());
        out.println("Unique Kanji: " + uniqueKanji.size());
        out.println("----------------");
        out.println("Compound Reading Sizes:");
        final AtomicInteger linesWithReadings = new AtomicInteger();
        compoundReadingsSizes.forEach((key, count) -> {
            out.println(key + " - " + count);
            linesWithReadings.addAndGet(count.get());
        });
        out.println("Total lines: " + linesInFile.get());
        final int kanjiWithoutReadings = uniqueKanji.size() - linesWithReadings.get();
        out.println("Kanji with readings: " + linesWithReadings);
        out.println("Kanji without readings: " + kanjiWithoutReadings);

    }

    private static Map<String, String> loadReadingsMap() {
        final Map<String, String> readingsMap = new HashMap<>();
        try {
            Files.readAllLines(Paths.get(READINGS_FILE)).forEach(line -> {
                linesInFile.incrementAndGet();
                final String[] split = line.split("\t");
                if (split.length == 2) {
                    readingsMap.put(split[0], split[1]);
                    uniqueKanji.add(split[0]);
                }
                if (split.length == 1 && split[0].trim().length() == 1) {
                    uniqueKanji.add(line.trim());
                }
            });
        } catch (final IOException e) {
            throw new IllegalStateException("Couldn't read file. ", e);
        }
        return readingsMap;
    }

}
