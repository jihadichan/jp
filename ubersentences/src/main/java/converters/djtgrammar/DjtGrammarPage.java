package converters.djtgrammar;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import static config.Config.COLUMN_DELIMITER;

public class DjtGrammarPage {

    private static final Path ankiRootFolder = Paths.get("/home/cc/.local/share/Anki2/User 1/collection.media");
    private static final Pattern cellQuotePattern = Pattern.compile("^\"|\"$");
    private static final Pattern doubleQuotePattern = Pattern.compile("\"\"");
    private static final Pattern standardQuotePattern = Pattern.compile("\"");
    private static final Pattern doubleSpacesPattern = Pattern.compile(" {2}");
    private static final Pattern spanPatternStep1 = Pattern.compile("<span.*?\\(\\w+\\).*?span>");
    private static final Pattern spanPatternStep2 = Pattern.compile("<span.*?>");
    private static final Pattern spanPatternStep3 = Pattern.compile("</span>");
    private static final Pattern latinSpacePattern = Pattern.compile(" ");
    private static final Pattern imagePathPattern = Pattern.compile("^<img src=\\\\\"|\\\\\" ?/?>");
    private static final Pattern escapedQuotePattern = Pattern.compile("\\\\\"");
    private static final Set<String> uniqueSentences = new HashSet<>();
    private static final AtomicInteger fileNameCount = new AtomicInteger(1);


    final String grammarItem;
    final String grammarSummary;
    final String equivalent;
    final List<DjtSentence> sentences;
    final Path grammarBookImagePath; // TODO move images to project folder
    final String grammarBookHtml;
    final String syntaxHtml;
    final String partOfSpeech;
    final String relatedExpression;
    final String antonymExpression;
    final String markdownFileName;

    DjtGrammarPage(final String line) {
        final List<String> cells = this.toList(line);

        this.grammarItem = cells.get(0).trim();
        this.grammarSummary = cells.get(1).trim();
        this.equivalent = cells.get(2).trim();
        this.sentences = extractSentences(cells);
        this.grammarBookImagePath = createImagePath(cells.get(35));
        this.grammarBookHtml = removeEscapedQuotes(getAtIndexOrNull(36, cells));
        this.syntaxHtml = removeEscapedQuotes(getAtIndexOrNull(37, cells));
        this.partOfSpeech = getAtIndexOrNull(38, cells);
        this.relatedExpression = getAtIndexOrNull(39, cells);
        this.antonymExpression = getAtIndexOrNull(40, cells);
        this.markdownFileName = fileNameCount.getAndIncrement() + ".md";
    }

    private static String removeEscapedQuotes(final String html) {
        if (html != null) {
            return escapedQuotePattern.matcher(html).replaceAll("\"");
        } else {
            return null;
        }
    }

    private static Path createImagePath(final String text) {
        final Path path = ankiRootFolder.resolve(imagePathPattern.matcher(text).replaceAll(""));
        if (!path.toFile().exists()) {
            throw new IllegalStateException("Image file in card does not exist: " + path);
        }
        return path;
    }

    private static String getAtIndexOrNull(final int index, final List<String> cells) {
        return cells.size() > index ? cells.get(index).trim() : null;
    }

    private List<String> toList(final String line) {
        final List<String> list = new ArrayList<>();
        final String[] cells = line.split(COLUMN_DELIMITER);
        for (String cell : cells) {
            cell = cellQuotePattern.matcher(cell).replaceAll(""); // Remove start and trailing quotes
            cell = doubleQuotePattern.matcher(cell).replaceAll("\""); // Remove double quotes (used by Anki)
            cell = standardQuotePattern.matcher(cell).replaceAll("\\\\\""); // Remove double quotes (used by Anki)
            list.add(cell);
        }
        return list;
    }

    private static List<DjtSentence> extractSentences(final List<String> cells) {
        final List<DjtSentence> list = new ArrayList<>();

        int index = 3;

        while (index < 34) { // Till cell index 34

            final String japanese = cells.get(index).trim();
            final String english = cells.get(index + 1).trim();

            if (!japanese.equals("") && !english.equals("")) {
                // Create sentence
                final DjtSentence djtSentence = new DjtSentence(
                        cleanJapaneseSentence(japanese),
                        cleanEnglishSentence(english));

                // Make sure it's not a duplicate
                if (uniqueSentences.contains(djtSentence.japanese)) {
                    System.out.println("Duplicate: " + djtSentence.japanese);
                } else {
                    uniqueSentences.add(djtSentence.japanese);
                    list.add(djtSentence);
                }
            }

            index += 2;
        }

        list.forEach(djtSentence -> {

        });

        return list;
    }

    private static String cleanEnglishSentence(final String english) {
        return doubleSpacesPattern.matcher(english).replaceAll(" ");
    }

    private static String cleanJapaneseSentence(String japanese) {
        japanese = spanPatternStep1.matcher(japanese).replaceAll("");
        japanese = spanPatternStep2.matcher(japanese).replaceAll("");
        japanese = spanPatternStep3.matcher(japanese).replaceAll("");
        japanese = latinSpacePattern.matcher(japanese).replaceAll("");
        return japanese;
    }

    @Override
    public String toString() {
        return "DjtGrammarPage{" +
                "grammarItem='" + this.grammarItem + '\'' +
                ", grammarSummary='" + this.grammarSummary + '\'' +
                ", equivalent='" + this.equivalent + '\'' +
                ", sentences=" + this.sentences +
                ", grammarBookImagePath='" + this.grammarBookImagePath + '\'' +
                ", grammarBookHtml='" + this.grammarBookHtml + '\'' +
                ", syntaxHtml='" + this.syntaxHtml + '\'' +
                ", partOfSpeech='" + this.partOfSpeech + '\'' +
                ", relatedExpression='" + this.relatedExpression + '\'' +
                ", antonymExpression='" + this.antonymExpression + '\'' +
                '}';
    }

    public static class DjtSentence {

        final String japanese;
        final String english;

        public DjtSentence(final String japanese, final String english) {
            this.japanese = japanese;
            this.english = english;
        }

        @Override
        public String toString() {
            return "DjtSentence{" +
                    "japanese='" + this.japanese + '\'' +
                    ", english='" + this.english + '\'' +
                    '}';
        }
    }
}
