package execute;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import models.AnkiStaticData;
import models.ReadingScores;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import static utils.CsvHelpers.sortCommonReadingsByValue;
import static utils.CsvHelpers.sortScoredReadingsByValue;

/**
 * Creates a single Markdown file which lists the reading scoring tables for all kanji
 */
public class RunCreateReadingScoreOverviewInMarkdown {

    private static final Path uberKanjiExportPaths = Paths.get("yomichan/deck/UberKanji.txt");
    private static final Path outputFile = Paths.get("output/readings.md");
    private static final Gson GSON = new GsonBuilder().create();
    private static final DecimalFormat df = new DecimalFormat("#.##");

    public static void main(final String[] args) throws Exception {

        final List<Tuple> tuples = loadDataColumnsFromUberKanjiExport();
        final StringBuilder sb = new StringBuilder();
        tuples.forEach(tuple -> {
            String s = "";
            s += "# " + tuple.kanji + " (" + tuple.asd.frequency + ")\n\n";

            // ------------------------------------------------------------------------------------------ //
            // ALL READINGS
            // ------------------------------------------------------------------------------------------ //
            s += "**All Readings**\n\n";

            s += "" +
                    "| Reading | Count       |\n" +
                    "| ------- | ----------- |\n";

            final AtomicInteger total = new AtomicInteger();
            tuple.asd.mostCommonReadings.forEach((key, count) -> total.addAndGet(count));
            for (final Entry<String, Integer> entry : sortCommonReadingsByValue(tuple.asd.mostCommonReadings).entrySet()) {
                final double percent = (double) entry.getValue() / total.get() * 100;
                s += "| " + entry.getKey() + " | " + entry.getValue() + " (" + df.format(percent) + "%) |\n";
            }

            s += "\n\n";


            // ------------------------------------------------------------------------------------------ //
            // ALL READINGS
            // ------------------------------------------------------------------------------------------ //
            s += "**Scored Readings**\n\n";

            s += "" +
                    "| Reading | Score       | fbKJ        | fbKN        |\n" +
                    "| ------- | ----------- | ----------- | ----------- |\n";

            for (final Entry<String, ReadingScores> entry : sortScoredReadingsByValue(tuple.asd.scoredReadings, tuple.asd.mostCommonReadings).entrySet()) {
                final ReadingScores rds = entry.getValue();
                s += "| " + entry.getKey() + " | " + rds.getScore() + " | " + rds.getFbKJ() + " | "+rds.getFbKN()+" |\n";
            }

            s += "\n\n";

            sb.append(s).append("\n\n");
        });

        Files.write(outputFile, sb.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }


    private static List<Tuple> loadDataColumnsFromUberKanjiExport() {
        try {
            final List<Tuple> tuples = new ArrayList<>();
            final List<String> lines = Files.readAllLines(uberKanjiExportPaths, StandardCharsets.UTF_8);
            lines.forEach(line -> {
                final String[] columns = line.split("\t");
                final String json = URLDecoder.decode(columns[6], StandardCharsets.UTF_8);
                final AnkiStaticData asd = GSON.fromJson(json, AnkiStaticData.class);
                final String kanji = columns[0];

                tuples.add(new Tuple(kanji, asd));
            });

            return tuples;
        } catch (final IOException e) {
            throw new IllegalStateException("Failed to load file at: " + uberKanjiExportPaths);
        }
    }

    static class Tuple {
        String kanji;
        AnkiStaticData asd;

        public Tuple(final String kanji, final AnkiStaticData asd) {
            this.kanji = kanji;
            this.asd = asd;
        }
    }

}
