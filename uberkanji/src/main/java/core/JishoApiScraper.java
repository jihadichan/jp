package core;

import models.KanjiData;
import models.jisho.JishoResult;
import utils.JpHelpers;
import utils.JpHelpers.KanjiDataCsvType;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static core.Config.JISHO_PAGE_DEPTH;
import static core.Config.JISHO_RESULT_FOLDER;
import static utils.JpHelpers.GSON;
import static java.lang.System.out;
import static utils.JpHelpers.KanjiDataCsvType.USE_ONLY_KANJI_AND_ID;

public class JishoApiScraper {
    private final JishoResultDictionary dictionary = new JishoResultDictionary();
    private final JishoApi jishoApi;

    public JishoApiScraper() {
        this.jishoApi = new JishoApi();
    }

    public void run() {
        JpHelpers.getKanjiDataFromCsv().forEach(kanjiData -> {

            final char kanji = kanjiData.getKanji();
            if (this.dictionary.isKanjiDataAvailable(kanji)) {
                out.println("Data available, skipping: " + kanji);
            } else {
                out.print("Processing: " + kanji);
                final JishoResult jishoResult = this.getJishoResult(kanjiData);
                this.writeToFile(kanji, jishoResult);
                out.println(" - OK.");
            }
        });
    }


    // ------------------------------------------------------------------------------------------ //
    // PRIVATE
    // ------------------------------------------------------------------------------------------ //

    private void writeToFile(final char kanji, final JishoResult jishoResult) {
        final Path path = JISHO_RESULT_FOLDER.resolve(kanji + ".json");
        try (Writer writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE)) {
            writer.append(GSON.toJson(jishoResult));
            writer.flush();
        } catch (final IOException e) {
            throw new IllegalStateException("Couldn't write file: " + path);
        }
    }

    private JishoResult getJishoResult(final KanjiData kanjiData) {
        try {
            return this.jishoApi.getWordsForSingleKanji(kanjiData, JISHO_PAGE_DEPTH); // Depth == First 100 words
        } catch (final Exception e) {
            throw new IllegalArgumentException("Couldn't fetch JishoResult from JishoApi. ", e);
        }
    }


}
