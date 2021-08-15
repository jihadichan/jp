package core;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import models.PollyTtsData;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static core.Config.*;

public class PollyTtsDirectory {

    private final AtomicInteger ttsDataCsvLines = new AtomicInteger();
    private final Map<String, PollyTtsData> ttsDataMap = this.loadTtsDataMap();
    private final Set<String> existingReadings = this.loadExistingReadings();

    private Set<String> loadExistingReadings() {
        final Set<String> set = new LinkedHashSet<>();
        try (Stream<Path> stream = Files.walk(READINGS_FOLDER)) {
            stream
                    .filter(path -> !path.toFile().isDirectory())
                    .filter(path -> path.toFile().toString().endsWith("mp3"))
                    .forEach(path -> set.add(path.toString()));
        } catch (final IOException e) {
            throw new IllegalStateException("Couldn't walk path " + JISHO_RESULT_FOLDER, e);
        }
        return set;
    }

    private Map<String, PollyTtsData> loadTtsDataMap() {

        final Map<String, PollyTtsData> ttsDataList = new LinkedHashMap<>();
        try {
            Files.readAllLines(TTS_DATA_FILE, StandardCharsets.UTF_8).forEach(line -> {
                this.ttsDataCsvLines.incrementAndGet();
                final List<String> row = Splitter.on("\t")
                        .trimResults()
                        .trimResults(CharMatcher.is('"'))
                        .splitToList(line);
                if (row.size() != POLLY_TTS_DATA_FILE_COLUMNS_EXPECTED) {
                    throw new IllegalStateException("Line can't be parsed, expected columns: " + POLLY_TTS_DATA_FILE_COLUMNS_EXPECTED + ", " +
                            "got " + row.size() + " - Line: " + line);
                }
                if (!row.get(1).endsWith(".mp3")) {
                    throw new IllegalStateException("Column 1 must be file name ending with '.mp3', got: " + row.get(1));
                }

                ttsDataList.put(row.get(1), new PollyTtsData(row.get(0), row.get(1)));
            });
        } catch (final IOException e) {
            throw new IllegalStateException("Couldn't read ttsDataList.csv file. ", e);
        }
        return ttsDataList;
    }

    public Map<String, PollyTtsData> getTtsDataMap() {
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

    public List<PollyTtsData> getMissingTtsDataList() {
        final Map<String, PollyTtsData> copy = new LinkedHashMap<>(this.ttsDataMap);
        this.existingReadings.forEach(copy::remove);
        return new ArrayList<>(copy.values());
    }
}
