package core;

import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.System.out;

public class PollyTtsScraper {

    private final PollyTtsDirectory ttsDirectory = new PollyTtsDirectory();
    private final AtomicInteger progressCount = new AtomicInteger();
    private final PollyApiScraper pollyApiScraper = new PollyApiScraper();

    public void run() {
        this.ttsDirectory.getMissingTtsDataList().forEach(ttsData -> {
            out.println("Creating " + ttsData.getFileName() + " - Nr. " + this.progressCount.incrementAndGet());
            this.pollyApiScraper.create(ttsData);
        });
    }

    public int ttsDataCsvSize() {
        return this.ttsDirectory.getTtsDataCsvLines();
    }

    public int uniqueReadings() {
        return this.ttsDirectory.getTtsDataMap().size();
    }

    public int existingReadingsSize() {
        return this.ttsDirectory.getExistingReadings().size();
    }

    public int requiredPollyRequests() {
        return this.ttsDirectory.getRequiredPollyRequests();
    }


}
