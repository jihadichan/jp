package converters.djtgrammar;

import converters.djtgrammar.DjtGrammarPage.DjtSentence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static converters.djtgrammar.DjtConfig.pathToShortenedCsvBaseFile;

/**
 * Builds the raw sentences.csv
 */
public class RunDjtSentencesConverter {

    private static final Set<String> uniqueSentences = new HashSet<>();
    private static final List<String> csvLines = new ArrayList<>();
    private static final Path outputPath = Paths.get("data/djt");

    public static void main(final String[] args) {
        if (!outputPath.toFile().isDirectory()) {
            throw new IllegalStateException("Output path '" + outputPath + "' doesn't exist or is not a directory.");
        }
        final DjtShortenedCsvReader reader = new DjtShortenedCsvReader(pathToShortenedCsvBaseFile);

        // Create grammar detail pages
        reader.getSentences().forEach(djtGrammarPage -> {
            djtGrammarPage.sentences.forEach(djtSentence -> {
                final String japanese = djtSentence.japanese.trim();
                if (!uniqueSentences.contains(japanese)) {
                    uniqueSentences.add(japanese);
                    csvLines.add(convertToCsvLine(djtSentence, djtGrammarPage));
                } else {
                    System.out.println("Duplicate (not added): " + japanese);
                }
            });
        });

        try {
            final StringBuilder sb = new StringBuilder();
            csvLines.forEach(line -> {
                sb.append(line);
                sb.append("\n");
            });

            final Path outputFile = outputPath.resolve("sentences.csv");
            Files.write(outputFile, sb.toString().getBytes(), StandardOpenOption.CREATE);
        } catch (final IOException e) {
            throw new IllegalStateException("Couldn't ");
        }
    }

    private static String convertToCsvLine(final DjtSentence djtSentence, final DjtGrammarPage djtGrammarPage) {

        final String japanese = djtSentence.japanese.trim();
        final String english = createDetails(
                djtSentence.english,
                djtGrammarPage.grammarItem,
                djtGrammarPage.grammarSummary,
                djtGrammarPage.partOfSpeech,
                djtGrammarPage.equivalent);
        final String markdownFileName = djtGrammarPage.markdownFileName;

        return japanese + "\t" + english + "\t" + createSource(djtGrammarPage, markdownFileName);
    }

    private static String createSource(final DjtGrammarPage djtGrammarPage, final String markdownFileName) {
        String html = "";

        if (djtGrammarPage.formationHtml != null && !djtGrammarPage.formationHtml.equals("")) {
            html += djtGrammarPage.formationHtml.replaceAll("\"", "'") + "<br><br>";
        }

        final StringBuilder sb = new StringBuilder();
        djtGrammarPage.sentences.forEach(djtSentence -> {
            sb.append(djtSentence.japanese);
            sb.append("<br>");
            sb.append(djtSentence.english);
            sb.append("<br><br>");
        });

        html += sb.toString();

        html += "<br><br>" + createSourceLink(markdownFileName);
        return html;
    }

    private static String createSourceLink(final String markdownFileName) {
        return DjtConfig.repoBaseUrl + "/items/" + markdownFileName + ".md";
    }

    // can't you make it even more unmanageable?!
    private static String createDetails(final String english,
                                        final String grammarPoint,
                                        final String summary,
                                        final String partOfSpeech,
                                        final String equivalent) {
        String result = "";
        result += getOrNull(english) != null ? getOrNull(english) + "<br>" : "";

        result += getOrNull(grammarPoint) != null ? "- Grammar point: " + getOrNull(grammarPoint) : "";
        result += getOrNull(partOfSpeech) != null ? " (" + getOrNull(partOfSpeech) + ")" : "";
        result += "<br>";
        result += getOrNull(summary) != null ? "- " + getOrNull(summary) + "<br>" : "";
        result += getOrNull(equivalent) != null ? "- Equivalent: " + getOrNull(equivalent) + "<br>" : "";

        return result.replaceAll("<br>$", "");
    }

    private static String getOrNull(final String text) {
        if (text == null || text.equals("")) {
            return null;
        }
        return text;
    }

}
