package converters.mnemonics;

import converters.mnemonics.MnemonicsCsvLoader.Mnemonic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

import static config.Config.projectRoot;

public class MnemonicsHtmlCreator {

    private static final Path htmlPath = projectRoot.resolve("grammarguide/jihadichan.github.io/kanji/mnemonics.html");

    public static String createHtml(final List<Mnemonic> list) {
        final StringBuilder sb = new StringBuilder();

        sb.append(createHeader());
        list.forEach(mnemonic -> {
            sb.append("<tr>");

            sb.append("<td>");
            sb.append(mnemonic.kanji);
            sb.append("</td>");

            sb.append("<td>");
            sb.append(mnemonic.mnemonics);
            sb.append("</td>");

            sb.append("</tr>\n");
        });
        sb.append(createFooter());

        return sb.toString();
    }

    public static void writeFile(final String html) {
        try {
            Files.write(htmlPath, html.getBytes(), StandardOpenOption.CREATE);
        } catch (final IOException e) {
            throw new IllegalStateException("Failed to write file.");
        }
    }

    private static String createHeader() {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Mnemonics</title>\n" +
                "    <link rel=\"stylesheet\" type=\"text/css\" href=\"datatables.min.css\">\n" +
                "    <link rel=\"stylesheet\" type=\"text/css\" href=\"styles.css\">\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1>Mnemonics</h1>\n" +
                "<table id=\"data-table\">\n" +
                "    <thead>\n" +
                "    <tr><th>Kanji</th><th>Mnemonics</th></tr>\n" +
                "    </thead>\n" +
                "    <tbody>\n";
    }

    private static String createFooter() {
        return "    </tbody>\n" +
                "</table>\n" +
                "<script src=\"jquery-3.4.1.min.js\" type=\"application/javascript\"></script>\n" +
                "<script src=\"datatables.min.js\" type=\"application/javascript\"></script>\n" +
                "<script type=\"application/javascript\">\n" +
                "    $(document).ready( function () {\n" +
                "        $('#data-table').DataTable({\n" +
                "            /* No ordering applied by DataTables during initialisation */\n" +
                "            \"order\": [],\n" +
                "            \"pageLength\": 100\n" +
                "        });\n" +
                "    } );\n" +
                "</script>\n" +
                "</body>\n" +
                "</html>";
    }

}
