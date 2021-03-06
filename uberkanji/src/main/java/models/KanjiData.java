package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static core.Config.KANJI_CSV_COLUMNS_EXPECTED_FULL_LINE;
import static core.Config.KANJI_CSV_COLUMNS_EXPECTED_KANJI_AND_ID;

public class KanjiData {

    private final char kanji;
    private final int id;
    private final List<String> onReadings = new ArrayList<>();
    private final List<String> kunReadings = new ArrayList<>();
    private final String meanings;
    private final int frequency;
    private final int strokes;
    private final String components;
    private final String rtkKeyword;
    private final int rtkIndex;

    // 齟|3005|uneven, bite, disagree|9999|ソ、サ、ショ|かむ|20

    /** Columns: kanji, id, meanings, frequency, on-reading, kun-reading, strokes, components, rtk keyword, rtk index */
    public KanjiData(final String[] line) {
        this.validate(line);

        this.kanji = line[0].charAt(0);
        this.id = Integer.parseInt(line[1]);

        switch (line.length) {

            case KANJI_CSV_COLUMNS_EXPECTED_KANJI_AND_ID:
                this.meanings = "";
                this.frequency = 9999;
                this.strokes = 0;
                this.components = "";
                this.rtkKeyword = "";
                this.rtkIndex = 9999;
                break;

            case KANJI_CSV_COLUMNS_EXPECTED_FULL_LINE:
                this.meanings = line[2];
                this.frequency = Integer.parseInt(line[3]);
                if (line[4].length() > 0) this.onReadings.addAll(Arrays.asList(line[4].split("、")));
                if (line[5].length() > 0) this.kunReadings.addAll(Arrays.asList(line[5].split("、")));
                this.strokes = Integer.parseInt(line[6]);

                this.components = line[7];
                this.rtkKeyword = line[8];
                this.rtkIndex = line[9].equals("") ? 9999 : Integer.parseInt(line[9]);

                break;

            default:
                throw new IllegalStateException("Unrecognized column amount, allowed are " +
                        "" + KANJI_CSV_COLUMNS_EXPECTED_KANJI_AND_ID + " or " + KANJI_CSV_COLUMNS_EXPECTED_FULL_LINE);
        }
    }

    public KanjiData(final char kanji,
                     final int id,
                     final String meanings,
                     final int frequency,
                     final List<String> onReadings,
                     final List<String> kunReadings,
                     final int strokes,
                     final String components,
                     final String rtkKeyword,
                     final int rtkIndex) {
        this.kanji = kanji;
        this.id = id;
        this.meanings = meanings;
        this.frequency = frequency;
        this.onReadings.addAll(onReadings);
        this.kunReadings.addAll(kunReadings);
        this.strokes = strokes;
        this.components = components;
        this.rtkKeyword = rtkKeyword;
        this.rtkIndex = rtkIndex;
    }

    @Override
    public String toString() {
        return "KanjiReading{" +
                "\nkanji=" + this.kanji +
                ", \nonReadings=" + this.onReadings +
                ", \nkunReadings=" + this.kunReadings +
                ", \nmeanings='" + this.meanings + '\'' +
                ", \nfrequency=" + this.frequency +
                ", \nstrokes=" + this.strokes +
                '}';
    }


    // ------------------------------------------------------------------------------------------ //
    // PRIVATE
    // ------------------------------------------------------------------------------------------ //

    /** Columns: kanji, id, meanings, frequency, on-reading, kun-reading, strokes */
    private void validate(final String[] line) {
        if (line.length != KANJI_CSV_COLUMNS_EXPECTED_FULL_LINE && line.length != KANJI_CSV_COLUMNS_EXPECTED_KANJI_AND_ID) {
            throw new IllegalArgumentException("Cannot parse line, must have " +
                    "" + KANJI_CSV_COLUMNS_EXPECTED_FULL_LINE + " or " + KANJI_CSV_COLUMNS_EXPECTED_KANJI_AND_ID + " " +
                    "columns, got: " + Arrays.toString(line));
        }

        // kanji
        if (line[0].length() != 1) {
            throw new IllegalArgumentException("Cannot parse column 1 as char, got: " + line[0]);
        }

    }

    // ------------------------------------------------------------------------------------------ //
    // GETTER & SETTER
    // ------------------------------------------------------------------------------------------ //

    public char getKanji() {
        return this.kanji;
    }

    public List<String> getOnReadings() {
        return this.onReadings;
    }

    public List<String> getKunReadings() {
        return this.kunReadings;
    }

    public String getMeanings() {
        return this.meanings;
    }

    public int getFrequency() {
        return this.frequency;
    }

    public int getStrokes() {
        return this.strokes;
    }

    public int getId() {
        return this.id;
    }

    public String getComponents() {
        return this.components;
    }

    public String getRtkKeyword() {
        return this.rtkKeyword;
    }

    public int getRtkIndex() {
        return this.rtkIndex;
    }
}
