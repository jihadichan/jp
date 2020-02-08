package execute;

import core.PollyTtsScraper;
import core.WaveNetTtsScraper;

import java.util.Scanner;

import static java.lang.System.out;

public class RunWaveNetTtsScraper {

    public static void main(final String[] args) {

        final WaveNetTtsScraper ttsScraper = new WaveNetTtsScraper();
        printConfirmation(ttsScraper);
        ttsScraper.run();

    }

    private static void printConfirmation(final WaveNetTtsScraper ttsScraper) {
        out.println("ttsDataList.csv lines: " + ttsScraper.ttsDataCsvSize());
        out.println("Unique readings: " + ttsScraper.uniqueReadings());
        out.println("Existing mp3 files: " + ttsScraper.existingReadingsSize());
        out.println("Required WaveNet requests: " + ttsScraper.requiredPollyRequests());

        out.println("Confirm with 'yes' to continue: ");
        final Scanner scanner = new Scanner(System.in);
        if (!scanner.next().equals("yes")) {
            out.println("Exiting...");
            System.exit(0);
        }
    }

}
