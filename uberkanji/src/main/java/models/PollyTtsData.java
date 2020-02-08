package models;

public class PollyTtsData {

    private final String ssml;
    private final String fileName;

    public PollyTtsData(final String ssml, final String fileName) {
        this.ssml = ssml;
        this.fileName = fileName;
    }

    public String getSsml() {
        return this.ssml;
    }

    public String getFileName() {
        return this.fileName;
    }
}
