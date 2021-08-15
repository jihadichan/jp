package yomichan;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class YomichanConfig {

    static final Path DIR_YOMICHAN_ROOT = Paths.get(new File("").getAbsolutePath()).resolve("yomichan");
    static final Path DIR_KANJI_DECK = DIR_YOMICHAN_ROOT.resolve("deck"); // Directory where you put your kanji deck exports in
    static final Path DIR_KANJI_DICT = DIR_YOMICHAN_ROOT.resolve("dict"); // Directory where you build the updated Yomichan dictionary
    static final Path MODIFIED_KANJI_DICT_FILE = DIR_KANJI_DICT.resolve("output.json");

}
