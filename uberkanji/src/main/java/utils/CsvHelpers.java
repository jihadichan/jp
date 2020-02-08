package utils;

import com.google.common.base.Joiner;
import models.JishoTerm;
import models.KanjiData;
import models.ReadingScores;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static core.Config.*;

public class CsvHelpers {
    private CsvHelpers() {
    }

    private static final DecimalFormat df = new DecimalFormat();
    private static final String listNotBeEmptyErrorMessage = "readings list must not be empty";

    static {
        df.setMaximumFractionDigits(2);
    }

    public static String toWordExampleTable(final List<JishoTerm> list) {
        final StringBuilder sb = new StringBuilder();
        Collections.sort(list);

        final AtomicInteger maxCount = new AtomicInteger(30);
        for (final JishoTerm jishoTerm : list) {
            sb.append(jishoTerm.getWord());
            sb.append("[");
            sb.append(jishoTerm.getReading());
            sb.append("]|");
            sb.append(jishoTerm.getFoundKanjiReading() == null ? "???" : jishoTerm.getFoundKanjiReading());
            sb.append("|");

            jishoTerm.getMeaningsAsList().forEach(meaning -> {
                sb.append(meaning);
                sb.append(", ");
            });
            sb.delete(sb.length() - 2, sb.length());
            sb.append("|");
            sb.append(jishoTerm.isCommon() ? "yes" : "no");

            sb.append("###\n");

            if (maxCount.decrementAndGet() == 0) {
                break;
            }
        }

        return sb.toString();
    }

    public static Map<String, Integer> sortCommonReadingsByValue(final Map<String, Integer> unsortedMap) {
        final Map<String, Integer> map = new LinkedHashMap<>();
        unsortedMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach(entry -> map.put(entry.getKey(), entry.getValue()));
        return map;
    }

    public static Map<String, ReadingScores> sortScoredReadingsByValue(final Map<String, ReadingScores> scoredReadings, final Map<String, Integer> mostCommonReadings) {
        final Map<String, ReadingScores> sortedMap = new LinkedHashMap<>();
        scoredReadings.entrySet().stream()
                .filter(entry -> entry.getValue().getScore() > 0) //
                .sorted((o1, o2) -> {
                    ReadingScores rs1 = o1.getValue();
                    ReadingScores rs2 = o2.getValue();
                    if (!rs1.getFbKJ().equals(rs2.getFbKJ())) {
                        return rs2.getFbKJ().compareTo(rs1.getFbKJ());
                    }
//                    char o1FirstChar = o1.getKey().charAt(0); // todo probably to compare by kana type. mostCommonReadings better suited.
//                    char o2FirstChar = o2.getKey().charAt(0);
//                    if (JpHelpers.isKatakana(o1FirstChar) != JpHelpers.isKatakana(o2FirstChar)) {
//                        return JpHelpers.isKatakana(o1FirstChar) ? 1 : 0;
//                    }
                    return mostCommonReadings.get(o2.getKey()).compareTo(mostCommonReadings.get(o1.getKey()));
                })
                .forEach(entry -> sortedMap.put(entry.getKey(), entry.getValue()));
        return sortedMap;
    }

    public static String createCompoundReadings(final Map<String, ReadingScores> scoredReadings) {
        final StringBuilder sb = new StringBuilder();
        scoredReadings.forEach((reading, score) -> {
            if(score.getFbKJ() > 0) {
                sb.append(JpHelpers.kataToHira(reading));
                sb.append(".");
            }
        });
        if (sb.toString().length() > 0) {
            sb.delete(sb.length() - 1, sb.length());
        }

        return sb.toString();
    }

    public static String toCommonReadingsTable(final Map<String, Integer> mostCommonReadings) {
        final StringBuilder sb = new StringBuilder();
        mostCommonReadings.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach(entry -> {
                    sb.append(entry.getKey());
                    sb.append("|");
                    sb.append(entry.getValue());
                    sb.append(" (");
                    sb.append(getPercentage(getTotal(mostCommonReadings), entry.getValue()));
                    sb.append("%)");
                    sb.append("###\n");
                });

        return sb.toString();
    }

    public static String toScoredReadingsTable(final Map<String, Integer> scoredReadings) {
        final StringBuilder sb = new StringBuilder();
        scoredReadings.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach(entry -> {
                    sb.append(entry.getKey());
                    sb.append("|");
                    sb.append(entry.getValue());
                    sb.append("###\n");
                });

        return sb.toString();
    }

    public static String toSsml(final List<String> readings) {

        final StringBuilder sb = new StringBuilder();
        sb.append("<speak>");
        final AtomicInteger count = new AtomicInteger();
        readings.forEach(reading -> {
            sb.append(JpHelpers.kataToHira(reading));
//            if (count.incrementAndGet() < readings.size()) { // todo without ending break pronunciation for ぬす is cut off. Probably also in other cases.
            sb.append("<break time='" + BREAK_BETWEEN_READINGS + "'/>");
//            }
        });
        sb.append("</speak>");

        return sb.toString();
    }

    public static List<String> getReadingsForTts(final KanjiData kanjiReading,
                                                 final Map<String, Integer> scoredReadings,
                                                 final Map<String, Integer> mostCommonReadings) {
        final List<String> scoredReadingsSorted = new ArrayList<>();
        getSortedScoredReadings(scoredReadings, mostCommonReadings).forEach((key, value) -> {
            if (value > 0) {
                scoredReadingsSorted.add(key);
            }
        });
        final List<String> mostCommonReadingsSorted = new ArrayList<>();
        mostCommonReadings.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach(entry -> mostCommonReadingsSorted.add(entry.getKey()));
        final List<String> resultList = new ArrayList<>();
        final AtomicInteger count = new AtomicInteger();

        // By score
        if (countTotalScore(scoredReadings) > 0) {
            for (final String reading : scoredReadingsSorted) {
                final int currentCount = count.incrementAndGet();
                if (currentCount <= MAX_TTS_READINGS
                        || (scoredReadings.get(reading) >= MIN_SCORE_FOR_ADDITIONAL_READING && currentCount <= MAX_TTS_READINGS + 1)) {
                    resultList.add(reading);
                }
            }
            return Contract.notEmpty(resultList, listNotBeEmptyErrorMessage);
        }

        // By most common readings
        if (!mostCommonReadingsSorted.isEmpty()) {
            return Contract.notEmpty(Collections.singletonList(mostCommonReadingsSorted.get(0)), listNotBeEmptyErrorMessage);
        }

        // First onyomi
        if (!kanjiReading.getOnReadings().isEmpty()) {
            resultList.add(kanjiReading.getOnReadings().get(0));
            return Contract.notEmpty(resultList, listNotBeEmptyErrorMessage);
        }

        // First kunyomi
        if (!kanjiReading.getKunReadings().isEmpty()) {
            resultList.add(kanjiReading.getKunReadings().get(0));
            return Contract.notEmpty(resultList, listNotBeEmptyErrorMessage);
        }

        return Contract.notEmpty(resultList, listNotBeEmptyErrorMessage);
    }

    private static Integer countTotalScore(final Map<String, Integer> scoredReadings) {
        final AtomicInteger count = new AtomicInteger();
        scoredReadings.forEach((kanji, score) -> count.addAndGet(score));
        return count.get();
    }

    public static String toReadingsFileName(final List<String> readings) {
        return READINGS_FOLDER.resolve(JpHelpers.kataToHira(Joiner.on("-").join(readings)) + ".mp3").toString();
    }

    // ------------------------------------------------------------------------------------------ //
    // PRIVATE
    // ------------------------------------------------------------------------------------------ //

    private static String getPercentage(final int total, final int fraction) {
        final double percentage = (double) fraction / total * 100;
        return df.format(percentage);
    }

    private static int getTotal(final Map<String, Integer> mostCommonReadings) {
        final AtomicInteger total = new AtomicInteger();
        mostCommonReadings.forEach((key, count) -> total.addAndGet(count));
        return total.get();
    }

    private static Map<String, Integer> getSortedScoredReadings(final Map<String, Integer> scoredReadings,
                                                                final Map<String, Integer> mostCommonReadings) {
        final Map<String, Integer> sortedByScore = new LinkedHashMap<>();
        scoredReadings.entrySet().stream()
                .sorted((o1, o2) -> {
                    if (o1.getValue().intValue() == o2.getValue().intValue()) {
                        return mostCommonReadings.get(o2.getKey()).compareTo(mostCommonReadings.get(o1.getKey()));
                    }
                    return o2.getValue().compareTo(o1.getValue());
                })
                .forEach(entry -> sortedByScore.put(entry.getKey(), entry.getValue()));

        return sortedByScore;
    }
}
