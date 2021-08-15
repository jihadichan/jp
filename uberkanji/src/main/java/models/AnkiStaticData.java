package models;

import java.util.List;
import java.util.Map;

/**
 * JSON for Anki field for data which doesn't need manual manipulation
 */
public class AnkiStaticData {

    private final String meanings;
    private final int frequency;
    private final String components;
    private final int strokes;
    private final String rtkKeyword;
    private final int rtkIndex;
    private final String onReadings;
    private final String kunReadings;
    private final List<JishoTerm> wordExamples;
    private final List<JishoTerm> otherForms;
    private final List<JishoTerm> unclassifiedReadings;
    private final Map<String, Integer> mostCommonReadings;
    private final Map<String, ReadingScores> scoredReadings;
    private final String compoundReading;
    private final String searchKey;

    public AnkiStaticData(final char kanji,
                          final String meanings,
                          final int frequency,
                          final String components,
                          final int strokes,
                          final String rtkKeyword,
                          final int rtkIndex,
                          final String onReadings,
                          final String kunReadings,
                          final List<JishoTerm> wordExamples,
                          final List<JishoTerm> otherForms,
                          final List<JishoTerm> unclassifiedReadings,
                          final Map<String, Integer> mostCommonReadings,
                          final Map<String, ReadingScores> scoredReadings,
                          final String compoundReading) {
        this.searchKey = "ｘ" + kanji;
        this.meanings = meanings;
        this.frequency = frequency;
        this.components = components;
        this.strokes = strokes;
        this.rtkKeyword = rtkKeyword;
        this.rtkIndex = rtkIndex;
        this.onReadings = onReadings.isEmpty() ? null : onReadings;
        this.kunReadings = kunReadings.isEmpty() ? null : kunReadings;
        this.wordExamples = wordExamples;
        this.otherForms = otherForms;
        this.unclassifiedReadings = unclassifiedReadings;
        this.mostCommonReadings = mostCommonReadings;
        this.scoredReadings = scoredReadings;
        this.compoundReading = compoundReading;
    }

    public String getOnReadings() {
        return this.onReadings;
    }

    public String getKunReadings() {
        return this.kunReadings;
    }

    public String getSearchKey() {
        return this.searchKey;
    }
}
