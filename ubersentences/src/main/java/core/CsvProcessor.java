package core;

import models.Sentence;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static config.Config.*;
import static java.lang.System.out;
import static utils.Utils.*;

public class CsvProcessor {

    private final List<String> inputCsvLines = loadCsvLinesAsList(SENTENCES_FILE);
    private final List<Sentence> sentences = new ArrayList<>();
    private final List<Sentence> missingMp3s = new ArrayList<>();
    private final WaveNetApiScraper waveNetApiScraper = new WaveNetApiScraper();

    // STATS
    private final AtomicInteger knownSentences = new AtomicInteger(0);
    private final AtomicInteger unknownSentences = new AtomicInteger(0);


    public void run() {
        this.checkForInvalidLines();
        this.inputCsvLines.forEach(line ->
                this.sentences.add(new Sentence(line)));

        this.collectListOfMp3sToDownload();
        this.printStats();
        this.printWarnings();
        this.userConfirmation();

        this.downloadMissingMp3s();

        this.writeToCsv();
    }

    private void printWarnings() {
        final AtomicBoolean firstWarning = new AtomicBoolean(true);
        this.sentences.forEach(sentence -> {
            if (sentence.getSentence().contains(" ")) {
                if (firstWarning.get()) {
                    out.println("-------------------------------");
                    out.println("WARNINGS (can cause errors in TTS and analyzer):");
                    firstWarning.set(false);
                }
                out.println("Contains Latin whitespaces (i.e. ' '): " + sentence.getSentence());
            }
        });
    }

    private void downloadMissingMp3s() {
        this.missingMp3s.forEach(sentence -> {
            out.println("Creating: " + sentence.getSentence() + " - (File: " + sentence.getMp3File() + ")");
            this.waveNetApiScraper.create(sentence);
        });
    }

    private void collectListOfMp3sToDownload() {
        this.sentences.forEach(sentence -> {
            Objects.requireNonNull(sentence.getMp3File());

            if (Mp3Dictionary.get(sentence.getSentence()) != null) {
                this.knownSentences.incrementAndGet();
            } else {
                this.missingMp3s.add(sentence);
                out.println(this.unknownSentences.incrementAndGet() + ". NEW: " + sentence.getSentence());
            }
        });
        if (this.missingMp3s.isEmpty()) {
            out.println("NO SENTENCES WITH MISSING MP3s");
        }
    }

    private void writeToCsv() {
        final OutputCsvWriter outputCsvWriter = new OutputCsvWriter();
        this.sentences.forEach(sentence ->
                outputCsvWriter.append(new String[]{
                        sentence.getSentence(),
                        sentence.getNotes(),
                        sentence.getSource(),
                        DECK + "/" + sentence.getMp3File(),
                        URLEncoder.encode(GSON.toJson(sentence.getTermInfos()), StandardCharsets.UTF_8)
                }));
        outputCsvWriter.close();
    }

    private void userConfirmation() {
        final Scanner scanner = new Scanner(System.in);

        out.println("Deck chosen: '" + DECK + "'. Files will be put into: " + MP3_FOLDER.resolve(DECK));
        out.print("Confirm with 'yes' to continue: ");
        if (!scanner.next().equals("yes")) {
            out.println("Exiting...");
            System.exit(0);
        }
    }

    private void printStats() {
        WordCounter.count();

        out.println("-------------------------------");
        out.println("Known Sentences  : " + this.knownSentences.get());
        out.println("Unknown Sentences: " + this.unknownSentences.get());
        out.println("Total Sentences  : " + this.sentences.size());
        out.println();
        out.println("Progress:");
        out.println("-------------------------------");
        out.println("Words in Anki    : " + WordCounter.getWordsInAnki());
        out.println("New words        : " + WordCounter.getNewWords());
        out.println("All words        : " + WordCounter.getAllWords());
        out.println("Hits in Netflix frequency list:");
        WordCounter.getTopWordsProgress().forEach((key, counter) ->
                out.println("Top " + key + ": " + counter.get() + " (" + toPercentage(key, counter.get()) + "%)"));
    }

    private static String toPercentage(final int principal, final int value) {
        final double percentage = (double) value / principal * 100;
        return String.format("%.2f", percentage);
    }

    private void checkForInvalidLines() {
        if (this.inputCsvLines.isEmpty()) {
            throw new IllegalStateException("Sentence file contained no lines");
        }

        this.inputCsvLines.forEach(line -> {
            final String[] cells = line.split(COLUMN_DELIMITER);
            this.ensureExpectedColumnCount(line, cells);
            this.ensureAllCellsAreQuoted(cells);
            this.ensureFirstColumnContainsJapanese(cells[0]);
        });
    }

    private void ensureFirstColumnContainsJapanese(final String firstCell) {
        for (final char ch : firstCell.toCharArray()) {
            if (isKanji(ch) || isHiragana(ch) || isKatakana(ch)) {
                return;
            }
        }
        throw new IllegalStateException("FATAL: First column is expected to contain at least one Japanese character. Cell: " + firstCell);
    }

    private void ensureAllCellsAreQuoted(final String[] cells) {
        Stream.of(cells).forEach(cell -> {
            if (!cell.startsWith("\"") || !cell.endsWith("\"")) {
                throw new IllegalStateException("FATAL: Cells are expected to be quoted with '\"'. Cell: " + cell);
            }
        });
    }

    private void ensureExpectedColumnCount(final String line, final String[] cells) {
        if (cells.length != EXPECTED_COLUMNS && cells.length != EXPECTED_COLUMNS - 1) {
            throw new IllegalStateException("FATAL: Lines are expected to have " + EXPECTED_COLUMNS + ",\n" +
                    "but got " + cells.length + ". Delimiter must be '" + COLUMN_DELIMITER + "'. Line: " + line);
        }
    }


}
