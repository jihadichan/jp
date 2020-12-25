package models;

import com.atilika.kuromoji.ipadic.Tokenizer;
import core.Mp3Dictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static config.Config.COLUMN_DELIMITER;
import static core.Mp3Dictionary.createMp3FileName;

public class Sentence {

    // FROM CSV FILE
    private final String sentenceWithFocusForAnki;
    private final String sentenceForTts;
    private final String notes;
    private final String source;

    // GENERATED FIELDS
    private final String mp3File;
    private final List<TermInfo> termInfos = new ArrayList<>();

    // HELPERS
    private static final Pattern quotePattern = Pattern.compile("^\"|\"$");
    private static final Pattern nonWordCharPattern = Pattern.compile("[^\\p{L}]");
    private static final Pattern focusWords = Pattern.compile("^.*<br/?>");
    private static final Tokenizer kuromoji = new Tokenizer.Builder().build();


    public Sentence(final String line) {
        final List<String> cells = this.toList(line);

        // FROM CSV FILE
        this.sentenceWithFocusForAnki = cells.get(0).trim();
        this.sentenceForTts = focusWords.matcher(cells.get(0)).replaceAll("").trim(); // NOTE: All ^.*<br/?> will be deleted.
        this.notes = cells.get(1);
        this.source = cells.size() > 2 ? cells.get(2) : "";

        // GENERATED FIELDS
        final Mp3 mp3FileName = Mp3Dictionary.get(this.sentenceForTts);
        this.mp3File = mp3FileName == null ? createMp3FileName(this.sentenceForTts) : mp3FileName.getFileName().toString();
        kuromoji.tokenize(this.sentenceForTts)
                .forEach(token -> this.termInfos.add(new TermInfo(token)));
    }

    private List<String> toList(final String line) {
        final List<String> list = new ArrayList<>();
        final String[] cells = line.split(COLUMN_DELIMITER);
        for (final String cell : cells) {
            list.add(quotePattern.matcher(cell).replaceAll(""));
        }
        return list;
    }


    // ------------------------------------------------------------------------------------------ //
    // GETTER & SETTER
    // ------------------------------------------------------------------------------------------ //

    public String getSentenceForTts() {
        return this.sentenceForTts;
    }

    public String getSentenceWithFocusForAnki() {
        return this.sentenceWithFocusForAnki;
    }

    public String getNotes() {
        return this.notes;
    }

    public String getSource() {
        return this.source;
    }

    public String getMp3File() {
        return this.mp3File;
    }

    public List<TermInfo> getTermInfos() {
        return this.termInfos;
    }
}
