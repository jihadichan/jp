package converters.meknow.run;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Replaces MP3 files with WaveNet TTS with originals from iKnow.
 */
public class ReplaceMp3Files {
    // todo
    //  - Create dictionary of sentences, sentence - old sentence file name
    //  - Have some dry run statistics to see if that shit actually work
    //  - Run through each entry in SentenceDict, find sentence, replace old file with original file
    //  !!! WORK IN BACKUP DIR !!!
    private static final Map<String, List<String>> oldFilesDict = load();
    private static final AtomicInteger hits = new AtomicInteger();
    private static final AtomicInteger misses = new AtomicInteger();


    public static void main(final String[] args) {

//        oldFilesDict.forEach((sentence, fileList) -> {
//            SentenceDict.map.forEach((key, sentenceData) -> {
//                if (sentence.equals(sentenceData.japanese)) {
//                    hits.incrementAndGet();
//                }
//            });
//        });

        SentenceDict.map.forEach((key, sentenceData) -> {
            final List<String> fileList = oldFilesDict.get(sentenceData.japanese.trim());
            if (fileList != null) {
                final Path oldFile = MeKnowConfig.OLD_FILES_FOLDER.resolve(fileList.get(0));
                final Path newFile = MeKnowConfig.MEKNOW_STEPS_FOLDER.resolve(String.valueOf(sentenceData.level)).resolve(sentenceData.fileName);
                if (newFile.toFile().exists() && oldFile.toFile().exists()) {
                    hits.incrementAndGet();
                    replaceFile(oldFile, newFile);
                }
            } else {
                misses.incrementAndGet();
            }
        });

        printStats();

    }

    private static void replaceFile(final Path oldFile, final Path newFile) {
        try {
            Files.copy(newFile, oldFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void printStats() {
        System.out.println("Total OldFiles: " + oldFilesDict.size());
        System.out.println("Total SentenceDict: " + SentenceDict.map.size());
        System.out.println("Hits: " + hits.get());
        System.out.println("Misses: " + misses.get());
    }

    private static Map<String, List<String>> load() {
        final Map<String, List<String>> map = new HashMap<>();
        try {
            Files.readAllLines(MeKnowConfig.MEKNOW_FOLDER.resolve("oldFileDict.txt")).forEach(line -> {
                final String[] columns = line.split("\t");
                final String sentence = columns[0].trim();
                final String filePath = columns[1].replaceAll("meknow/", "").trim();

                final List<String> fileList = map.get(sentence);
                if (fileList == null) {
                    map.put(sentence, new ArrayList<>(List.of(filePath)));
                } else {
                    fileList.add(filePath);
                }

            });
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
        return map;
    }

}
