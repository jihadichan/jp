package converters.djtgrammar;

import java.nio.file.Path;
import java.nio.file.Paths;

public class DjtConfig {
    private DjtConfig() {
    }

    public static final Path grammarGuide = Paths.get("grammarguide");
    public static final String pathToShortenedCsvBaseFile = "data/djt/shortened.csv";
    public static final String repoBaseUrl = "https://github.com/jihadichan/jp/tree/master/ubersentences/grammarguide";
}
