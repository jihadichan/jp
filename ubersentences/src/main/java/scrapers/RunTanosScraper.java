package scrapers;

import scrapers.tanos.TanosScraper;

/**
 * Scrapes all sentences from Tanos. JLPT N5 - N1.
 * Saves output to data/tanos.csv - Ready to import into an UberSentences deck.
 * Tanos has ~287 grammar items. Takes the 10 shortest sentences of each item and throws the rest away (can be configured.)
 * The order of the output is the same you find on Tanos. Starts with 1st item from N5, ends with last one from N1.
 */
public class RunTanosScraper {

    public static void main(String[] args) {

        new TanosScraper().run();

    }

}
