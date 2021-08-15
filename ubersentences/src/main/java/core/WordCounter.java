package core;

import models.Sentence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static config.Config.SENTENCES_FILE;
import static config.Config.SENTENCES_FOLDER;

public class WordCounter {

    private static final Map<String, Integer> freqNF = FrequencyDictionary.freqNF;
    private static final Set<String> wordInAnki = new HashSet<>();
    private static final Set<String> allWords = new HashSet<>();
    private static final List<Sentence> sentenceList = new ArrayList<>();
    private static final Map<Integer, AtomicInteger> topWordsProgress = new TreeMap<>();
    private static final Integer TOP_2000 = 2000;
    private static final Integer TOP_5000 = 5000;
    private static final Integer TOP_10000 = 10000;
    private static final Integer TOP_20000 = 20000;

    static {
        topWordsProgress.put(TOP_2000, new AtomicInteger(0));
        topWordsProgress.put(TOP_5000, new AtomicInteger(0));
        topWordsProgress.put(TOP_10000, new AtomicInteger(0));
        topWordsProgress.put(TOP_20000, new AtomicInteger(0));
    }


    static void count() {
        collectSentencesInAnki();
        sentenceList.forEach(sentence ->
                sentence.getTermInfos().forEach(termInfo -> {
                    final String word = termInfo.getBaseForm() != null ? termInfo.getBaseForm() : termInfo.getSurface();
                    wordInAnki.add(word);
                }));

        collectNewSentences();
        sentenceList.forEach(sentence ->
                sentence.getTermInfos().forEach(termInfo -> {
                    final String word = termInfo.getBaseForm() != null ? termInfo.getBaseForm() : termInfo.getSurface();
                    if (allWords.add(word)) {
                        countTopWords(word);
                    }
                }));
    }

    private static void countTopWords(final String word) {
        final Integer freq = freqNF.get(word);

        if (freq == null) {
            return;
        }
        if (freq <= 2000) {
            incrementTopWordsCount(TOP_2000);
        }
        if (freq <= 5000) {
            incrementTopWordsCount(TOP_5000);
        }
        if (freq <= 10000) {
            incrementTopWordsCount(TOP_10000);
        }
        if (freq <= 20000) {
            incrementTopWordsCount(TOP_20000);
        }
    }

    private static void incrementTopWordsCount(final Integer key) {
        final AtomicInteger counter = topWordsProgress.get(key);
        if (counter == null) {
            throw new IllegalStateException("Key not found in topWordsProgress Map: " + key);
        }
        counter.incrementAndGet();
    }

    private static void collectNewSentences() {
        loadSentencesAsList(Paths.get(SENTENCES_FILE))
                .forEach(line -> sentenceList.add(new Sentence(line)));
    }

    private static void collectSentencesInAnki() {
        try {
            Files.walk(SENTENCES_FOLDER)
                    .filter(file -> !file.toFile().isDirectory())
                    .filter(file -> file.getFileName().toString().endsWith("csv"))
                    .forEach(file -> loadSentencesAsList(file)
                            .forEach(line -> {
                                try {
                                    sentenceList.add(new Sentence(line));
                                } catch (Exception e) {
                                    System.out.println();
                                }
                            }));
        } catch (final IOException e) {
            throw new IllegalStateException("Couldn't walk through " + SENTENCES_FOLDER);
        }
    }

    private static List<String> loadSentencesAsList(final Path path) {
        try {
            return com.google.common.io.Files.asCharSource(path.toFile(), StandardCharsets.UTF_8).readLines();
        } catch (final IOException e) {
            throw new IllegalStateException("Couldn't read " + SENTENCES_FILE, e);
        }
    }

    static int getWordsInAnki() {
        return wordInAnki.size();
    }

    static int getAllWords() {
        return allWords.size();
    }

    static int getNewWords() {
        return allWords.size() - wordInAnki.size();
    }

    static Map<Integer, AtomicInteger> getTopWordsProgress() {
        return topWordsProgress;
    }
}
