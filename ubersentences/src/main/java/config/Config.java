package config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import static java.lang.System.out;

public class Config {

    // CONFIGURABLE
    public static final String SENTENCES_FILE = "sentences.csv";
    public static final int EXPECTED_COLUMNS = 3;
    public static final String COLUMN_DELIMITER = "\t";

    // INTERNAL
    public static final Path projectRoot = Paths.get("").toAbsolutePath();
    public static final Path MP3_FOLDER = Paths.get("anki/ubersentences/mp3/");
    public static final Path SENTENCES_FOLDER = Paths.get("sentences/");
    public static final Path DATA_FOLDER = Paths.get("data/");
    public static final Path OUTPUT_CSV_FOLDER = Paths.get("output/");
    public static final Path WAVENET_DATA = DATA_FOLDER.resolve("wavenet.json");
    public static final String FREQ_NF = "freqNF";
    public static final String FREQ_WK = "freqWK";
    public static String DECK = ""; // todo see below
    // so somehow the static setting doesn't work anymore. See below. Set DECK where you run from. Via createDeckName()
    // And this also doesn't work from tests... it worked for months. Suddenly doesn't. Something about the static probably.

    public static String createDeckName() {
        final Scanner scanner = new Scanner(System.in);
        out.print("Deck name (must comply with the MP3 folder of your Anki assets): ");
        final String name = scanner.next();
        if (!MP3_FOLDER.resolve(name).toFile().exists()) {
            out.println("'" + MP3_FOLDER.resolve(name) + "' doesn't exist. Create the folder. Exiting...");
            System.exit(0);
        }
        if (name.equals("")) {
            out.println("Deck name must be set. Exiting...");
            System.exit(0);
        }
        return name;
    }

//    static { todo this shit doesn't work suddenly???
//        final Scanner scanner = new Scanner(System.in);
//        out.print("Deck name (must comply with the MP3 folder of your Anki assets): ");
//        DECK = scanner.next();
//        if (!MP3_FOLDER.resolve(DECK).toFile().exists()) {
//            out.println("'" + MP3_FOLDER.resolve(DECK) + "' doesn't exist. Create the folder. Exiting...");
//            System.exit(0);
//        }
//        if (DECK.equals("")) {
//            out.println("Deck name must be set. Exiting...");
//            System.exit(0);
//        }
//    }


}
