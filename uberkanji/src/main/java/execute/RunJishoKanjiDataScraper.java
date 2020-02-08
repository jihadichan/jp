package execute;

import core.JishoReadingScraper;

/**
 * Scrapes KanjiData for single kanji chars. Uses kanjiDataStatics.csv
 */
public class RunJishoKanjiDataScraper {

    public static void main(final String[] args) {
        final JishoReadingScraper jishoReadingScraper = new JishoReadingScraper();
        jishoReadingScraper.run();
    }

}
