package core;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import models.WaveNetTtsData;
import utils.JpHelpers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static core.Config.*;
import static java.lang.System.out;

public class WaveNetTtsDirectory {

    private final AtomicInteger ttsDataCsvLines = new AtomicInteger();
    private final Map<String, WaveNetTtsData> ttsDataMap = this.loadTtsDataMap();
    private final Set<String> existingReadings = this.loadExistingReadings();

    private Set<String> loadExistingReadings() {
        final Set<String> set = new LinkedHashSet<>();
        try (Stream<Path> stream = Files.walk(READINGS_FOLDER)) {
            stream
                    .filter(path -> !path.toFile().isDirectory())
                    .filter(path -> path.toFile().toString().endsWith("mp3"))
                    .forEach(path -> set.add(path.getFileName().toString().replace(".mp3", "")));
        } catch (final IOException e) {
            throw new IllegalStateException("Couldn't walk path " + JISHO_RESULT_FOLDER, e);
        }
        return set;
    }

    private Map<String, WaveNetTtsData> loadTtsDataMap() {

        final Map<String, WaveNetTtsData> ttsDataList = new LinkedHashMap<>();
        try {
            Files.readAllLines(TTS_DATA_FILE, StandardCharsets.UTF_8).forEach(line -> {
                this.ttsDataCsvLines.incrementAndGet();
                final List<String> row = Splitter.on("\t")
                        .trimResults()
                        .trimResults(CharMatcher.is('"'))
                        .splitToList(line);
                if (row.size() != WAVENET_TTS_DATA_FILE_COLUMNS_EXPECTED) {
                    throw new IllegalStateException("Line can't be parsed, expected columns: " + WAVENET_TTS_DATA_FILE_COLUMNS_EXPECTED + ", " +
                            "got " + row.size() + " - Line: " + line);
                }
                this.checkForIllegalChars(row.get(0));

                for (final String reading : row.get(0).split("\\.")) {
                    ttsDataList.put(reading, new WaveNetTtsData(reading));
                }
            });
        } catch (final IOException e) {
            throw new IllegalStateException("Couldn't read ttsDataList.csv file. ", e);
        }
        return ttsDataList;
    }

    /** Only Hiragana and '.' is allowed */
    private void checkForIllegalChars(final String text) {
        for (final char ch : text.toCharArray()) {
            String charAsSting = String.valueOf(ch);
            if (!JpHelpers.isHiragana(ch) && !charAsSting.equals(".") && !charAsSting.equals("ー")) {
                throw new IllegalArgumentException("Non-Hiragana in " + text + " - char: " + ch);
            }
        }
    }

    public Map<String, WaveNetTtsData> getTtsDataMap() {
        return this.ttsDataMap;
    }

    public Set<String> getExistingReadings() {
        return this.existingReadings;
    }

    public int getTtsDataCsvLines() {
        return this.ttsDataCsvLines.get();
    }

    public int getRequiredPollyRequests() {
        final AtomicInteger count = new AtomicInteger();
        this.ttsDataMap.forEach((fileName, ttsData) -> {
            if (!this.existingReadings.contains(fileName)) {
                count.incrementAndGet();
            }
        });

        return count.get();
    }

    public List<WaveNetTtsData> getMissingTtsDataList() {
        final Map<String, WaveNetTtsData> copy = new LinkedHashMap<>(this.ttsDataMap);
        this.existingReadings.forEach(copy::remove);
        return new ArrayList<>(copy.values());
    }
}
