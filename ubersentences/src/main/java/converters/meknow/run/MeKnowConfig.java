package converters.meknow.run;

import java.nio.file.Path;
import java.nio.file.Paths;

public class MeKnowConfig {

    public static final Path OLD_FILES_FOLDER = Paths.get("anki/ubersentences/mp3/meknow_BAK");
    public static final Path MEKNOW_FOLDER = Paths.get("data/meknow/");
    public static final Path MEKNOW_PAGES = MEKNOW_FOLDER.resolve("pages.json");
    public static final Path MEKNOW_STEPS_FOLDER = MEKNOW_FOLDER.resolve("steps/");
    public static final Path MEKNOW_STEPS_JSON = MEKNOW_STEPS_FOLDER.resolve("json/");

}
