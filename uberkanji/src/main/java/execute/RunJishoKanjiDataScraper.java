package execute;

import core.JishoReadingScraper;

/**
 * Scrapes KanjiData for single kanji chars. Uses kanjiDataStatics.csv
 */
public class RunJishoKanjiDataScraper {

    // todo maybe rewrite to use different files. Fucking confusing using this shit after a year.
    public static void main(final String[] args) {
        final JishoReadingScraper jishoReadingScraper = new JishoReadingScraper();
        jishoReadingScraper.run();
    }

}
