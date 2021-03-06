package yomichan;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static utils.JpHelpers.isKanji;

public class KanjiDeckEntry {

    private final char kanji;
    private final String concept;
    private final String normalizedMnemonicField;
    private final List<String> mnemonicsList;
    private final String knownReadings;

    private static final Pattern readingPattern = Pattern.compile("([一-龯ぁ-んァ-ン]+) [-|=].*");
    private static final Pattern vocabPattern = Pattern.compile("<ruby>.*(\\(([ぁ-んァ-ン]+),.*\\))");

    public KanjiDeckEntry(final String line) {
        final String[] split = splitLine(line);

        this.kanji = split[0].charAt(0);
        this.concept = normalizeConcept(split[2]);
        this.normalizedMnemonicField = normalizeMnemonicField(split[3]);
        this.mnemonicsList = extractMnemonics(this.normalizedMnemonicField);
        this.knownReadings = extractKnownReadings(this.normalizedMnemonicField);
    }

    private static String[] splitLine(final String line) {
        final String[] split = normalizeCells(normalizeLine(line).split("\t"));
        // Ensure data is clean
        if (split.length != 9) {
            throw new IllegalStateException("Every line in kanji deck is expected to have 10 columns, got at least one with: " +
                    "" + split.length + "\n" + line);
        }
        if (split[0].length() != 1) { // todo if this throws use count method as in KanjiDictLoader
            throw new IllegalStateException("First column in kanji deck line is expected to be kanji and have max length of 1, got:\n" +
                    split[0]);
        }
        return split;
    }


    private static String normalizeLine(final String line) {
        return line.replaceAll("&nbsp;", " ");
    }

    private static String[] normalizeCells(final String[] cells) {
        for (int i = 0; i < cells.length; i++) {
            cells[i] = cells[i].trim();
        }
        return cells;
    }

    private static String normalizeConcept(final String concept) {
        return concept.replaceAll("<br>", " ");
    }

    private static String extractKnownReadings(final String normalizedMnemonicsField) {
        final StringBuilder sb = new StringBuilder();
        final Set<String> uniqueReadings = new LinkedHashSet<>();
        final Matcher matcher = vocabPattern.matcher(normalizedMnemonicsField);
        while (matcher.find()) {
            uniqueReadings.add(matcher.group(2).trim());
        }
        uniqueReadings.forEach(reading -> {
            sb.append(reading);
            sb.append(", ");
        });
        if(sb.toString().endsWith(", ")) {
            sb.delete(sb.length() - 2, sb.length());
        }

        return sb.toString();
    }

    private static List<String> extractMnemonics(final String normalizedMnemonicsField) {
        final List<String> mnemonics = new ArrayList<>();
        final Matcher matcher = readingPattern.matcher(normalizedMnemonicsField);
        while (matcher.find()) {
            mnemonics.add(matcher.group(0).trim());
        }
        return mnemonics;
    }

    private static String normalizeMnemonicField(final String mnemonic) {
        return mnemonic.replaceAll("<br>", "\n");
    }


    // ------------------------------------------------------------------------------------------ //
    // GETTER & SETTER
    // ------------------------------------------------------------------------------------------ //

    public char getKanji() {
        return this.kanji;
    }

    public String getConcept() {
        return this.concept;
    }

    public List<String> getMnemonicsList() {
        return this.mnemonicsList;
    }

    public String getKnownReadings() {
        return this.knownReadings;
    }
}
