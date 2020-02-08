package execute;

import core.PollyTtsScraper;

import java.util.Scanner;

import static java.lang.System.out;

public class RunPollyTtsScraper {

    public static void main(final String[] args) {

        final PollyTtsScraper ttsScraper = new PollyTtsScraper();
        printConfirmation(ttsScraper);
        ttsScraper.run();

    }

    private static void printConfirmation(final PollyTtsScraper ttsScraper) {
        out.println("ttsDataList.csv lines: " + ttsScraper.ttsDataCsvSize());
        out.println("Unique readings: " + ttsScraper.uniqueReadings());
        out.println("Existing mp3 files: " + ttsScraper.existingReadingsSize());
        out.println("Required Polly requests: " + ttsScraper.requiredPollyRequests());

        out.println("Confirm with 'yes' to continue: ");
        final Scanner scanner = new Scanner(System.in);
        if (!scanner.next().equals("yes")) {
            out.println("Exiting...");
            System.exit(0);
        }
    }

}
