package core;

import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.System.out;

public class WaveNetTtsScraper {

    private final WaveNetTtsDirectory ttsDirectory = new WaveNetTtsDirectory();
    private final AtomicInteger progressCount = new AtomicInteger();
    private final WaveNetApiScraper waveNetApiScraper = new WaveNetApiScraper();

    public void run() {
        this.ttsDirectory.getMissingTtsDataList().forEach(ttsData -> {
            out.println("Creating " + WaveNetApiScraper.createFileName(ttsData.getReading()) + " - Nr. " + this.progressCount.incrementAndGet());
            this.waveNetApiScraper.create(ttsData);
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
