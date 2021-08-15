package core;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Config {
    private Config() {
    }

    public static final Path JISHO_RESULT_FOLDER = Paths.get("kanji/");
    public static final Path OUTPUT_CSV_FOLDER = Paths.get("output/");
    public static final Path READINGS_FOLDER = Paths.get("readings/");
    public static final Path AWS_API_KEYS = Paths.get("apiKeys");

    public static final Path JISHO_KANJI_DATA_FILE = Paths.get("kanjiDataStatics.csv");
    public static final Path TTS_DATA_FILE = Paths.get("ttsDataList.csv");
    public static final int KANJI_CSV_COLUMNS_EXPECTED_FULL_LINE = 10; // kanji, id, meanings, frequency, on-reading, kun-reading, strokes, components, rtk keyword, rtk index
    public static final int KANJI_CSV_COLUMNS_EXPECTED_KANJI_AND_ID = 2; // kanji, id
    public static final int POLLY_TTS_DATA_FILE_COLUMNS_EXPECTED = 2; // ssml, file name
    public static final int WAVENET_TTS_DATA_FILE_COLUMNS_EXPECTED = 1; // compound reading

    public static final int JISHO_PAGE_DEPTH = 5; // == 100 results, 20 per page
    public static final int VOICE_ID = 1; // 0 = female, 1 = male


    // ------------------------------------------------------------------------------------------ //
    // ALGORITHM
    // ------------------------------------------------------------------------------------------ //
    public static final int MAX_TTS_READINGS = 1;
    public static final int MIN_SCORE_FOR_ADDITIONAL_READING = 999; // If MAX_TTS_READINGS is reached but word has enough score we add it (max == MAX_TTS_READINGS + 1)
    public static final String BREAK_BETWEEN_READINGS = "1ms"; // there's a minimum way above 1ms, but can't do anything about it. Old value: 300ms
}
