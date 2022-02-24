package execute;

import core.WordAnalyzer;

/**
 * Create the CSV for the UberKanji deck
 */
public class RunWordAnalyzer {

    public static void main(final String[] args) {

        final WordAnalyzer readingAnalyzer = new WordAnalyzer();
        readingAnalyzer.run();

    }

}
