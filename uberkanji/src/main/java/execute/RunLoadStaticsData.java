package execute;

import com.google.common.base.Splitter;
import com.google.common.io.Files;
import com.google.gson.Gson;
import models.NormalizedReading;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Oops. Deleted kanji file.
 * Loads JSONs from output file and outputs ALL readings in Hiragana.
 * How the F did Anki import this...
 */
public class RunLoadStaticsData {

    private static final Pattern csvQuotes = Pattern.compile("^\"|\"$");
    private static final Pattern doubleQuotes = Pattern.compile("\"\"");
    private static final Gson gson = new Gson();

    public static void main(final String[] args) {

        final Path path = Paths.get("kanjiDataStatics.csv");
        try {
            for (final String line : Files.readLines(path.toFile(), Charset.defaultCharset())) {
                final List<String> row = Splitter.on("|").trimResults().splitToList(line);
//                NormalizedReading normalizedReading = new NormalizedReading()
                final List<NormalizedReading> list = NormalizedReading.createList(asList(row.get(1)), asList(row.get(2)));
                list.forEach(reading -> {
                    System.out.println(reading.getNormalized().replace("、", "."));
                });
            }
        } catch (final IOException e) {
            throw new IllegalStateException("Couldn't load file.");
        }

    }

    private static List<String> asList(final String rawReadings) {
        return Splitter.on("|").trimResults().splitToList(rawReadings);
    }

}
