package execute;

import core.JishoApiScraper;

import static core.Config.JISHO_PAGE_DEPTH;

public class RunJishoApiScraper {

    public static void main(final String[] args) {
        final JishoApiScraper jishoScraper = new JishoApiScraper();
        jishoScraper.run();
    }

}
