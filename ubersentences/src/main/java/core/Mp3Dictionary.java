package core;

import models.Mp3;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

import static config.Config.DECK;
import static config.Config.MP3_FOLDER;

public class Mp3Dictionary {

    private static final Pattern nonWordCharPattern = Pattern.compile("[^\\p{L}\\d-]");
    private static final Pattern timestampAndSentencePattern = Pattern.compile("\\d{13}-[\\p{L}\\d-]+\\.mp3$");
    private static final Map<String, Mp3> sentenceMap = loadSentences();

    private static Map<String, Mp3> loadSentences() {
        final Map<String, Mp3> sentenceMap = new HashMap<>();
        if (DECK.equals("")) {
            throw new IllegalStateException("Deck is not set.");
        }
        try {
            Files.walk(MP3_FOLDER.resolve(DECK))
                    .filter(file -> !file.toFile().isDirectory())
                    .filter(file -> file.getFileName().toString().endsWith("mp3"))
                    .filter(file -> timestampAndSentencePattern.matcher(file.getFileName().toString()).matches())
                    .forEach(file -> {
                        final Mp3 mp3File = new Mp3(file);
                        sentenceMap.put(mp3File.getSentence(), mp3File);
                    });
        } catch (final IOException e) {
            throw new IllegalStateException("Couldn't walk through " + MP3_FOLDER);
        }

        return sentenceMap;
    }

    public static Mp3 get(final String sentence) {
        return sentenceMap.get(cleanSentence(sentence));
    }

    public static String createMp3FileName(final String sentence) {
        Objects.requireNonNull(sentence);

        if (sentence.length() < 1) {
            throw new IllegalStateException("Sentence has no characters, sentence: " + sentence);
        }
        return new Date().getTime() + "-" + cleanSentence(sentence) + ".mp3";
    }

    private static String cleanSentence(String sentence) {
        if (sentence.length() > 35) {
            sentence = sentence.substring(0, 35) + "-" + DigestUtils.md5Hex(sentence).substring(0,4);
        }
        return nonWordCharPattern.matcher(sentence).replaceAll("");
    }


}
