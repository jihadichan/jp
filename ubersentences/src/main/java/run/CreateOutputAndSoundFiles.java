package run;

import core.CsvProcessor;

import static config.Config.DECK;
import static config.Config.createDeckName;

public class CreateOutputAndSoundFiles {

    public static void main(final String[] args) {
        DECK = createDeckName();
        new CsvProcessor().run();
    }

}
