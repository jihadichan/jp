package core;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import models.AnkiStaticData;
import models.JishoTerm;
import models.NormalizedReading;
import models.ReadingScores;
import models.jisho.JishoResult;
import utils.JpHelpers;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.System.out;
import static java.lang.System.setOut;
import static utils.Contract.notEmpty;
import static utils.Contract.notNull;
import static utils.CsvHelpers.*;
import static utils.JpHelpers.toJishoTermList;

public class WordAnalyzer {

    private final JishoResultDictionary dictionary = new JishoResultDictionary();
    private final OutputCsvWriter outputCsvWriter = new OutputCsvWriter();
    private static final Gson gson = new GsonBuilder()
            .enableComplexMapKeySerialization()
            .disableHtmlEscaping()
            .create();

    public void run() {
        JpHelpers.getKanjiDataFromCsv().forEach(kanjiData -> {
            final JishoResult jishoResult = notNull(this.dictionary.get(kanjiData.getKanji()),
                    "Can't find JishoResult for " + kanjiData.getKanji());
            final List<JishoTerm> jishoTermList = toJishoTermList(jishoResult);

            final List<NormalizedReading> normalizedReadingList = notEmpty(
                    NormalizedReading.createList(kanjiData.getOnReadings(), kanjiData.getKunReadings()),
                    "Kanji has no readings and can't be processed. Kanji: " + kanjiData.getKanji());

            Map<String, Integer> mostCommonReadings = new LinkedHashMap<>();
            Map<String, ReadingScores> scoredReadings = new LinkedHashMap<>();
            final List<JishoTerm> wordExamples = new ArrayList<>(); // Has found reading
            final List<JishoTerm> otherFormTerms = new ArrayList<>(); // No reading on Jisho fits
            final List<JishoTerm> unknownReadingTerms = new ArrayList<>(); // No reading on Jisho fits

            final Set<String> duplicateFilter = new HashSet<>();
            for (final JishoTerm jishoTerm : jishoTermList) {
                if (duplicateFilter.contains(jishoTerm.getWord() + " - " + jishoTerm.getReading())) {
                    continue;
                } else {
                    duplicateFilter.add(jishoTerm.getWord() + " - " + jishoTerm.getReading());
                }
                jishoTerm.setType(kanjiData.getKanji());

                final AtomicBoolean hasReading = new AtomicBoolean(false);
                for (final NormalizedReading reading : normalizedReadingList) {
                    if (jishoTerm.getReading().contains(reading.getNormalized())) {
                        jishoTerm.setFoundKanjiReading(reading.getRawCleaned());
                        if (jishoTerm.getWord().contains(String.valueOf(kanjiData.getKanji()))) {
                            wordExamples.add(jishoTerm);
                            this.addToMostCommonReadings(reading, mostCommonReadings);
                            this.addToScoredReadings(kanjiData.getKanji(), reading, scoredReadings, jishoTerm);
                        } else {
                            otherFormTerms.add(jishoTerm);
                        }

                        hasReading.set(true);
                        break;
                    }
                }
                if (!hasReading.get()) {
                    if(jishoTerm.getWord().contains(String.valueOf(kanjiData.getKanji()))) {
                        unknownReadingTerms.add(jishoTerm);
                    } else {
                        otherFormTerms.add(jishoTerm);
                    }
                }

            }

            // DEBUG
            out.println("---------------------- Kanji: " + kanjiData.getKanji() + " --------------------------");
//            out.println("------------- validTerms");
//            out.println(CsvHelpers.toWordExampleTable(validTerms));
//            out.println("------------- otherFormTerms");
//            out.println(CsvHelpers.toWordExampleTable(otherFormTerms));
//            out.println("------------- unknownReadingTerms");
//            out.println(CsvHelpers.toWordExampleTable(unknownReadingTerms));
//            out.println("Most Common Readings");
//            out.println(mostCommonReadings.size() > 0 ? CsvHelpers.toCommonReadingsTable(mostCommonReadings) : "NO READINGS");
//            out.println("Scored Readings");
//            out.println(toScoredReadingsTable(scoredReadings));
//            out.println("Total score: " + countTotalScore(scoredReadings));
//            final List<String> ttsReadings = getReadingsForTts(kanjiData, scoredReadings, mostCommonReadings);
//            out.println(toSsml(ttsReadings));
//            out.println(toReadingsFileName(ttsReadings));
//            out.println();


            // Sort and clean data collections
            Collections.sort(wordExamples);
            Collections.sort(otherFormTerms);
            Collections.sort(unknownReadingTerms);
            mostCommonReadings = sortCommonReadingsByValue(mostCommonReadings);
            scoredReadings = sortScoredReadingsByValue(scoredReadings, mostCommonReadings);
            final String compoundReadings = createCompoundReadings(scoredReadings);

            final AnkiStaticData ankiStaticData = new AnkiStaticData(
                    kanjiData.getKanji(),
                    kanjiData.getMeanings().equals("") ? null : kanjiData.getMeanings(),
                    kanjiData.getFrequency(),
                    kanjiData.getComponents().equals("") ? null : kanjiData.getComponents(),
                    kanjiData.getStrokes(),
                    kanjiData.getRtkKeyword().equals("") ? null : kanjiData.getRtkKeyword(),
                    kanjiData.getRtkIndex(),
                    Joiner.on(", ").join(kanjiData.getOnReadings()),
                    Joiner.on(", ").join(kanjiData.getKunReadings()),
                    wordExamples,
                    otherFormTerms,
                    unknownReadingTerms,
                    mostCommonReadings,
                    scoredReadings,
                    compoundReadings.equals("") ? null : compoundReadings
            );
//            out.println(gson.toJson(ankiStaticData));

            // kanji, id, frequency, rtk index, static data json
            final int compoundReadingsSize = getCompoundReadingsSize(compoundReadings);
            this.outputCsvWriter.append(new String[]{
                    String.valueOf(kanjiData.getKanji()),
                    String.valueOf(kanjiData.getId()),
                    compoundReadings,
                    String.valueOf(kanjiData.getFrequency()),
                    String.valueOf(kanjiData.getRtkIndex()),
                    this.createTags(compoundReadingsSize),
                    this.validateJson(gson.toJson(ankiStaticData))
            });
        });

        this.outputCsvWriter.close();
    }

    private String createTags(final int compoundReadingsSize) {
        if (compoundReadingsSize > 1) {
            return "multireading";
        } else {
            return "monoreading";
        }
    }

    private static int getCompoundReadingsSize(final String compoundReadings) {
        if (compoundReadings == null) {
            return 0;
        }
        return compoundReadings.split("\\.").length;
    }

    private void addToMostCommonReadings(final NormalizedReading reading, final Map<String, Integer> mostCommonReadings) {
        Integer count = mostCommonReadings.get(reading.getRawCleaned());
        if (count == null) {
            mostCommonReadings.put(reading.getRawCleaned(), 1);
        } else {
            mostCommonReadings.put(reading.getRawCleaned(), ++count);
        }
    }

    private void addToScoredReadings(final char kanji, final NormalizedReading reading, final Map<String, ReadingScores> scoredReadings, final JishoTerm jishoTerm) {
        final ReadingScores existingReadingsScore = scoredReadings.get(reading.getRawCleaned());
        if (existingReadingsScore == null) {
            final ReadingScores readingScores = new ReadingScores(jishoTerm.getScore());
            this.addFollowedByScores(kanji, jishoTerm, readingScores);
            scoredReadings.put(reading.getRawCleaned(), readingScores);
        } else {
            this.addFollowedByScores(kanji, jishoTerm, existingReadingsScore);
            existingReadingsScore.incrementScoreBy(jishoTerm.getScore());
        }
    }

    private void addFollowedByScores(final char kanji, final JishoTerm jishoTerm, final ReadingScores readingScores) {
        if(jishoTerm.getScore() > 0) {
            switch (jishoTerm.getType()) {
                case "fnKJ":
                    readingScores.incrementFbKJ();
                    break;
                case "fnKN":
                    readingScores.incrementFbKN();
                    break;
                case "oFrm":
                    readingScores.decrementScore();
                    break;
                default:
                    throw new IllegalArgumentException("JishoTerm type not recognized: " + jishoTerm.getType());
            }
        }
    }

    private String validateJson(final String json) {
        try {
            gson.fromJson(json, AnkiStaticData.class);
            return json;
        } catch (final Exception e) {
            throw new IllegalArgumentException("Produced JSON is invalid:\n" + json + "\n");
        }
    }


}
