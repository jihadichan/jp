package converters.meknow.run;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class CreateWgetDownloadFiles {

    public static void main(final String[] args) {
        SentenceDict.downloadUrlByLevel.forEach((level, list) -> {
            final StringBuilder sb = new StringBuilder();
            list.forEach(url -> {
                sb.append(url);
                sb.append("\n");
            });
            writeUrlFile(MeKnowConfig.MEKNOW_STEPS_FOLDER.resolve(String.valueOf(level)).resolve("downloadUrls"), sb.toString().trim());
        });
    }

    private static void writeUrlFile(final Path path, final String content) {
        try (final Writer writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE)) {
            writer.write(content);
            writer.flush();
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }

}
