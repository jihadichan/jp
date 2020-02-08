package converters.djtgrammar;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class RunDjtToMarkDownConverter {

    private static final String pathToShortenedCsvBaseFile = "data/djt/shortened.csv";
    private static final Path grammarGuide = Paths.get("grammarguide");
    private static final Path itemsFolder = grammarGuide.resolve(Paths.get("items"));

    public static void main(final String[] args) {
        makeSureGrammarGuideFolderIsOk();
        final DjtShortenedCsvReader reader = new DjtShortenedCsvReader(pathToShortenedCsvBaseFile);

        // Index
        writeToFile(createIndexPage(reader.getSentences()), Paths.get("readme.md"));

        // Create grammar detail pages
        createItemsFolder();
        reader.getSentences().forEach(djtGrammarPage -> {
            writeToFile(createGrammarPage(djtGrammarPage), itemsFolder.resolve(djtGrammarPage.markdownFileName));
        });
    }

    private static String createGrammarPage(final DjtGrammarPage djtGrammarPage) {
        String page = "# Grammar Guide\n\n<table>";


        page += "</table>";

        return page;
    }

    private static void createItemsFolder() {
        if (!itemsFolder.toFile().mkdir()) {
            throw new IllegalStateException("Couldn't create directory: " + itemsFolder);
        }
    }

    private static void writeToFile(final String page, final Path filePath) {
        final Path path = grammarGuide.resolve(filePath);
        try {
            Files.write(path, page.getBytes());
        } catch (final IOException e) {
            throw new IllegalStateException("Couldn't write " + path);
        }
    }

    private static String createIndexPage(final List<DjtGrammarPage> grammarPages) {
        String page = "# Grammar Guide\n\n<table>";

        for (final DjtGrammarPage grammarPage : grammarPages) {
            page += "<tr>" +
                    "<td>" + grammarPage.markdownFileName + ". " +
                    "<a href='items/" + grammarPage.markdownFileName + "'>" + grammarPage.grammarItem + "</a>" +
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
