package scrapers.meknow.meknow;

public class Course {

    private final String name;
    private final String url;

    public Course(final String name, final String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return this.name;
    }

    public String getUrl() {
        return this.url;
    }
}
