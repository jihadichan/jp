package converters.djtgrammar;

import converters.djtgrammar.DjtGrammarPage.DjtSentence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class RunDjtToMarkDownConverter {

    private static final String pathToShortenedCsvBaseFile = "data/djt/shortened.csv";
    private static final Path grammarGuide = Paths.get("grammarguide");
    private static final Path itemsFolder = Paths.get("items");

    public static void main(final String[] args) {
        makeSureGrammarGuideFolderIsOk();
        final DjtShortenedCsvReader reader = new DjtShortenedCsvReader(pathToShortenedCsvBaseFile);

        // Index
        writeToFile(createIndexPage(reader.getSentences()), Paths.get("readme.md"));

        // Create grammar detail pages
        createItemsFolder();
        reader.getSentences().forEach(djtGrammarPage -> {
            writeToFile(createGrammarPage(djtGrammarPage), itemsFolder.resolve(djtGrammarPage.markdownFileName + ".md"));
        });
    }

    private static String createGrammarPage(final DjtGrammarPage djtGrammarPage) {
        String page = "# " + djtGrammarPage.grammarItem + "\n\n";

        // ------------------------------------------------------------------------------------------ //
        // SUMMARY
        // ------------------------------------------------------------------------------------------ //
        page += "## Summary\n\n" +
                "<table>";

        if (djtGrammarPage.grammarSummary != null && !djtGrammarPage.grammarSummary.equals("")) {
            page += "<tr>" +
                    "   <td>Summary</td>" +
                    "   <td>" + djtGrammarPage.grammarSummary + "</td>" +
                    "</tr>";
        }
        if (djtGrammarPage.equivalent != null && !djtGrammarPage.equivalent.equals("")) {
            page += "<tr>" +
                    "   <td>English</td>" +
                    "   <td>" + djtGrammarPage.equivalent + "</td>" +
                    "</tr>";
        }
        if (djtGrammarPage.partOfSpeech != null && !djtGrammarPage.partOfSpeech.equals("")) {
            page += "<tr>" +
                    "   <td>Part of speech</td>" +
                    "   <td>" + djtGrammarPage.partOfSpeech + "</td>" +
                    "</tr>";
        }
        if (djtGrammarPage.relatedExpression != null && !djtGrammarPage.relatedExpression.equals("")) {
            page += "<tr>" +
                    "   <td>Related expression</td>" +
                    "   <td>" + djtGrammarPage.relatedExpression + "</td>" +
                    "</tr>";
        }
        if (djtGrammarPage.antonymExpression != null && !djtGrammarPage.antonymExpression.equals("")) {
            page += "<tr>" +
                    "   <td>Antonym expression</td>" +
                    "   <td>" + djtGrammarPage.antonymExpression + "</td>" +
                    "</tr>";
        }
        page += "</table>\n\n";

        // ------------------------------------------------------------------------------------------ //
        // EXAMPLE SENTENCES
        // ------------------------------------------------------------------------------------------ //
        page += "## Example Sentences\n\n" +
                "<table>";

        for (final DjtSentence djtSentence : djtGrammarPage.sentences) {
            page += "<tr>" +
                    "   <td>" + djtSentence.japanese + "</td>" +
                    "   <td>" + djtSentence.english + "</td>" +
                    "</tr>";
        }
        page += "</table>\n\n";

//        System.out.println(page);
//        System.exit(0);
        return page;
    }

    private static void createItemsFolder() {
        if (!grammarGuide.resolve(itemsFolder).toFile().mkdir()) {
            throw new IllegalStateException("Couldn't create directory: " + grammarGuide.resolve(itemsFolder));
        }
    }

    private static void writeToFile(final String page, final Path filePath) {
        final Path path = grammarGuide.resolve(filePath);
        try {
            Files.write(path, page.getBytes());
        } catch (final IOException e) {
            throw new IllegalStateException("Couldn't write " + path, e);
        }
    }

    private static String createIndexPage(final List<DjtGrammarPage> grammarPages) {
        String page = "# Grammar Guide\n\n<table>";

        for (final DjtGrammarPage grammarPage : grammarPages) {
            page += "<tr>" +
                    "<td>" + grammarPage.markdownFileName + ". " +
                    "<a href='items/" + grammarPage.markdownFileName + "'>" + grammarPage.grammarItem + ".md</a>" +
                    "</td>" +
                    "<td>" +
                    "<ul>";
            page += grammarPage.grammarSummary.equals("") ? "" : "<li>" + grammarPage.grammarSummary + "</li>";
            page += grammarPage.equivalent.equals("") ? "" : "<li>Meaning: " + grammarPage.equivalent + "</li>";
            page += grammarPage.partOfSpeech == null ? "" : "<li>" + grammarPage.partOfSpeech + "</li>";
            page += "</ul>" +
                    "</td></tr>";
        }
        page += "</table>";

        return page;
    }

    private static void makeSureGrammarGuideFolderIsOk() {
        if (!grammarGuide.toFile().exists() || !grammarGuide.toFile().isDirectory()) {
            throw new IllegalStateException("Can't find folder " + grammarGuide + "/ - Must be in the root folder of the project");
        }

        final Optional<String[]> fileList = Optional.ofNullable(grammarGuide.toFile().list());
        if (fileList.isPresent() && fileList.get().length > 0) {
            throw new IllegalStateException("Folder " + grammarGuide + " must be empty.");
        }
    }

}
