package models;

import java.nio.file.Path;
import java.util.regex.Pattern;

public class Mp3 {

    private final String sentence;
    private final Path fileName;
    private static final Pattern fileTypePattern = Pattern.compile("\\d{13}-|\\.mp3$");

    public Mp3(final Path path) {
        this.fileName = path.getFileName();
        this.sentence = fileTypePattern.matcher(this.fileName.toString()).replaceAll("");
    }

    public String getSentence() {
        return this.sentence;
    }

    public Path getFileName() {
        return this.fileName;
    }
}
