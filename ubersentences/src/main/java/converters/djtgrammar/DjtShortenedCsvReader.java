package converters.djtgrammar;

import java.util.ArrayList;
import java.util.List;

import static utils.Utils.loadCsvLinesAsList;

/**
 * Converts the Anki export of the DJT grammar deck into MarkDown files suitable for GitHub,
 * including a table of contents which links each page. Fuck yeah.
 */
public class DjtShortenedCsvReader {

    private final List<String> inputCsvLines;
    private final List<DjtGrammarPage> sentences = new ArrayList<>();

    public DjtShortenedCsvReader(final String pathToShortenedCsvBaseFile) {
        this.inputCsvLines = loadCsvLinesAsList(pathToShortenedCsvBaseFile);
        this.checkForInvalidLines();
        this.inputCsvLines.forEach(line ->
                this.sentences.add(new DjtGrammarPage(line)));
    }

    private void checkForInvalidLines() {
        if (this.inputCsvLines.isEmpty()) {
            throw new IllegalStateException("Sentence file contained no lines");
        }

        this.inputCsvLines.forEach(line -> {

        });
    }

    public List<DjtGrammarPage> getSentences() {
        return this.sentences;
    }
}
