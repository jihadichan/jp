package execute;

import utils.RomajiConverter;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.System.out;

/**
 * Prints /backups/readings where hiragana is transformed to romaji. For saving stupid manual work.
 */
public class RunHiraToRoma {

    public static void main(final String[] args) throws Exception {

        Files.readAllLines(Paths.get("backups/readings")).forEach(line -> {
            if (line.contains("\t")) {
                final String[] split = line.split("\t");
                out.println(split[0] + "\t" + truncateToMaxThreeReadings(RomajiConverter.fromHiragana(split[1])));
            } else {
                out.println(line);
            }
        });

    }

    private static String truncateToMaxThreeReadings(final String line) {
        final String[] readings = line.split("\\.");
        final StringBuilder sb = new StringBuilder();
        final AtomicInteger count = new AtomicInteger(0);
        for (final String reading : readings) {
            if (count.getAndIncrement() >= 3) {
                break;
            }
            sb.append(reading);
            sb.append(".");
        }
        sb.delete(sb.length() - 1, sb.length());

        return sb.toString();
    }

}
