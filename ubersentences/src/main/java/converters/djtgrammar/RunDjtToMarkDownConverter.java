package converters.djtgrammar;

import converters.djtgrammar.DjtGrammarPage.DjtSentence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class RunDjtToMarkDownConverter {

    private static final String pathToShortenedCsvBaseFile = "data/djt/shortened.csv";
    public static final Path grammarGuide = Paths.get("grammarguide");
    private static final Path itemsFolder = Paths.get("items");
    private static final Path imageFolder = Paths.get("img");

    public static void main(final String[] args) {
        setUpGrammarFolder();
        final DjtShortenedCsvReader reader = new DjtShortenedCsvReader(pathToShortenedCsvBaseFile);

        // Index
        writeToFile(createIndexPage(reader.getSentences()), Paths.get("readme.md"));

        // Create grammar detail pages
        reader.getSentences().forEach(djtGrammarPage ->
                writeToFile(createGrammarPage(djtGrammarPage), itemsFolder.resolve(djtGrammarPage.markdownFileName + ".md")));
    }

    private static String createGrammarPage(final DjtGrammarPage djtGrammarPage) {
        final StringBuilder toc = new StringBuilder();
        final AtomicInteger tocCount = new AtomicInteger(0);
        toc.append("# " + djtGrammarPage.grammarItem + "\n\n");
        String page = "";

        // ------------------------------------------------------------------------------------------ //
        // SUMMARY
        // ------------------------------------------------------------------------------------------ //
        final String summary = "Summary";
        toc.append("![" + tocCount.incrementAndGet() + ". " + summary + "](" + summary.toLowerCase() + ")<br>\n");
        page += "## " + summary + "\n\n" +
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
        // FORMATION
        // ------------------------------------------------------------------------------------------ //
        if (djtGrammarPage.syntaxHtml != null && !djtGrammarPage.syntaxHtml.equals("")) {
            final String formation = "Formation";
            toc.append("![" + tocCount.incrementAndGet() + ". " + formation + "](" + formation.toLowerCase() + ")<br>\n");
            page += "## " + formation + "\n\n";
            page += djtGrammarPage.syntaxHtml + "\n\n";
        }


        // ------------------------------------------------------------------------------------------ //
        // EXAMPLE SENTENCES
        // ------------------------------------------------------------------------------------------ //
        final String exampleSentences = "Example Sentences";
        toc.append("![" + tocCount.incrementAndGet() + ". " + exampleSentences + "](" + exampleSentences.toLowerCase() + ")<br>\n");
        page += "## " + exampleSentences + "\n\n" +
                "<table>";

        for (final DjtSentence djtSentence : djtGrammarPage.sentences) {
            page += "<tr>" +
                    "   <td>" + djtSentence.japanese + "</td>" +
                    "   <td>" + djtSentence.english + "</td>" +
                    "</tr>";
        }
        page += "</table>\n\n";


        // ------------------------------------------------------------------------------------------ //
        // GRAMMAR DETAILED EXPLANATION
        // ------------------------------------------------------------------------------------------ //
        if (djtGrammarPage.grammarBookHtml != null && !djtGrammarPage.grammarBookHtml.equals("")) {
            final String explanation = "Explanation";
            toc.append("![" + tocCount.incrementAndGet() + ". " + explanation + "](" + explanation.toLowerCase() + ")<br>\n");
            page += "## " + explanation + "\n\n";
            page += djtGrammarPage.grammarBookHtml + "\n\n";
        }


        // ------------------------------------------------------------------------------------------ //
        // GRAMMAR DETAILED EXPLANATION
        // ------------------------------------------------------------------------------------------ //
        if (djtGrammarPage.grammarBookImagePath != null) {
            final String grammarBookPage = "Grammar Book Page";
            toc.append("![" + tocCount.incrementAndGet() + ". " + grammarBookPage + "](" + grammarBookPage.toLowerCase() + ")<br>\n");
            page += "## " + grammarBookPage + "\n\n";
            page += "![](" + djtGrammarPage.grammarBookImagePath + ")\n\n";
        }

        page = toc.toString() +"\n\n"+ page;
        return page;
    }

    private static void createItemsFolder(final Path folderName) {
        if (!grammarGuide.resolve(folderName).toFile().mkdir()) {
            throw new IllegalStateException("Couldn't create directory: " + grammarGuide.resolve(folderName));
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
                    "<a href='items/" + grammarPage.markdownFileName + ".md'>" + grammarPage.grammarItem + "</a>" +
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

    private static void setUpGrammarFolder() {
        System.out.println("Checking if " + grammarGuide + "... ");
        if (!grammarGuide.toFile().exists() || !grammarGuide.toFile().isDirectory()) {
            throw new IllegalStateException("Can't find folder " + grammarGuide + "/ - Must be in the root folder of the project");
        }

        final AtomicBoolean isClean = new AtomicBoolean(true);
        final AtomicBoolean hasItemsFolder = new AtomicBoolean(false);
        final AtomicBoolean hasImageFolder = new AtomicBoolean(false);
        try {
            Files.walk(grammarGuide)
                    .filter(path -> !path.toString().equals(grammarGuide.toString()))
                    .filter(path -> !path.toString().endsWith(".png"))
                    .forEach(path -> {
                        final String pathAsString = path.toString();
                        boolean isAllowed = false;
                        if (pathAsString.equals(grammarGuide.resolve(imageFolder).toString())) {
                            hasImageFolder.set(true);
                            isAllowed = true;
                        }
                        if (pathAsString.equals(grammarGuide.resolve(itemsFolder).toString())) {
                            hasItemsFolder.set(true);
                            isAllowed = true;
                        }
                        if (!isAllowed) {
                            isClean.set(false);
                            System.out.println("Delete this file: " + pathAsString);
                        }
                    });
        } catch (final IOException e) {
            throw new IllegalStateException("Couldn't walk through: " + grammarGuide);
        }

        if (!isClean.get()) {
            throw new IllegalStateException(grammarGuide + " folder must be clean. Delete the mentioned or simply delete all content. ");
        }
        if (!hasImageFolder.get()) {
            createItemsFolder(imageFolder);
        }
        if (!hasItemsFolder.get()) {
            createItemsFolder(itemsFolder);
        }

    }

}
