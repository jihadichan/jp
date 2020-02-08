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
    private final String sentence;
    private final String notes;
    private final String source;

    // GENERATED FIELDS
    private final String mp3File;
    private final List<TermInfo> termInfos = new ArrayList<>();

    // HELPERS
    private static final Pattern quotePattern = Pattern.compile("^\"|\"$");
    private static final Pattern nonWordCharPattern = Pattern.compile("[^\\p{L}]");
    private static final Tokenizer kuromoji = new Tokenizer.Builder().build();


    public Sentence(final String line) {
        final List<String> cells = this.toList(line);

        // FROM CSV FILE
        this.sentence = cells.get(0);
        this.notes = cells.get(1);
        this.source = cells.size() > 2 ? cells.get(2) : "";

        // GENERATED FIELDS
        final Mp3 mp3FileName = Mp3Dictionary.get(this.sentence);
        this.mp3File = mp3FileName == null ? createMp3FileName(this.sentence) : mp3FileName.getFileName().toString();
        kuromoji.tokenize(this.sentence)
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

    public String getSentence() {
        return this.sentence;
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
