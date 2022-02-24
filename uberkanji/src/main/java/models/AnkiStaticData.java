package models;

import java.util.List;
import java.util.Map;

/**
 * JSON for Anki field for data which doesn't need manual manipulation
 */
public class AnkiStaticData {

    public final String meanings;
    public final int frequency;
    public final String components;
    public final int strokes;
    public final String rtkKeyword;
    public final int rtkIndex;
    public final String onReadings;
    public final String kunReadings;
    public final List<JishoTerm> wordExamples;
    public final List<JishoTerm> otherForms;
    public final List<JishoTerm> unclassifiedReadings;
    public final Map<String, Integer> mostCommonReadings;
    public final Map<String, ReadingScores> scoredReadings;
    public final String compoundReading;
    public final String searchKey;

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
