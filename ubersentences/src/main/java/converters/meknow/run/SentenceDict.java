package converters.meknow.run;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import converters.meknow.models.IKItem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * Creates a dictionary of all sentences with URLs to the MP3 and lookup sentences by original fileName.
 */
public class SentenceDict {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Pattern whiteSpacePattern = Pattern.compile("\\s+");
    private static final Pattern tillLastSlashPattern = Pattern.compile(".*/");

    public static final Map<Integer, List<String>> downloadUrlByLevel = new HashMap<>();
    public static final Set<String> uniqueUrlsByLevel = new HashSet<>();
    public static final Map<String, SentenceData> uniqueFileNames = new HashMap<>();
    public static final Map<String, SentenceData> map = load();

    public static void main(final String[] args) {
        System.out.println();
    }

    private static Map<String, SentenceData> load() {
        final Map<String, SentenceData> map = new HashMap<>();
        try {
            final AtomicInteger sentenceCount = new AtomicInteger(0);
            Files.walk(MeKnowConfig.MEKNOW_STEPS_JSON)
                    .sorted()
                    .filter(path -> path.toFile().isFile())
                    .forEach(path -> {
                        final String json = pathToString(path);
                        final IKItem ikItem = GSON.fromJson(json, IKItem.class);
                        ikItem.getGoalItems().forEach(goalItem -> {
                            goalItem.getSentences().forEach(ikSentence -> {

                                // Create sentence
                                final SentenceData sentenceData = new SentenceData();
                                final String[] levelStep = path.getFileName().toString().split("");
                                sentenceData.level = Integer.parseInt(levelStep[0]);
                                sentenceData.step = Integer.parseInt(levelStep[1]);
                                sentenceData.position = sentenceCount.incrementAndGet();
                                sentenceData.fileNamePrefix = createPrefix(sentenceData);
                                sentenceData.downloadUrl = ikSentence.getSound();
                                sentenceData.japanese = whiteSpacePattern
                                        .matcher(ikSentence.getCue().getTransliterations().getJpan())
                                        .replaceAll("")
                                        .trim();
                                sentenceData.fileName = tillLastSlashPattern.matcher(sentenceData.downloadUrl).replaceAll("").trim();

                                // Put into maps
                                map.put(sentenceData.japanese, sentenceData);
                                addToDownloadUrlMap(sentenceData);
                            });
                        });
                    });
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
        return map;
    }

    private static String pathToString(final Path path) {
        try {
            return Files.readString(path);
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String createPrefix(final SentenceData sentenceData) {
        final String prefix = "" + sentenceData.level + "" + sentenceData.step;
        String position = String.valueOf(sentenceData.position);
        switch (position.length()) {
            case 1:
                position = "000" + position;
                break;
            case 2:
                position = "00" + position;
                break;
            case 3:
                position = "0" + position;
                break;
            case 4:
                break;
            default:
                throw new IllegalStateException("Invalid position length, position: " + position);
        }
        return prefix + position;
    }

    private static void addToDownloadUrlMap(final SentenceData sentenceData) {
        if (uniqueUrlsByLevel.add(sentenceData.level + "-" + sentenceData.downloadUrl)) {
            final List<String> urlList = downloadUrlByLevel.get(sentenceData.level);
            if (urlList == null) {
                downloadUrlByLevel.put(sentenceData.level, new ArrayList<>(List.of(sentenceData.downloadUrl)));
            } else {
                urlList.add(sentenceData.downloadUrl);
            }
        }
    }

    static class SentenceData {
        int level;
        int step;
        int position;
        String fileNamePrefix;
        String downloadUrl;
        String fileName;
        String japanese;
    }

}
