package core;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

import static core.Config.OUTPUT_CSV_FOLDER;

public class OutputCsvWriter {

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss");
    private final Writer writer = this.createWriter();

    public void append(final String[] row) {
        final StringBuilder sb = new StringBuilder();
        for (final String column : row) {
            sb.append("\"");
            sb.append(column);
            sb.append("\"");
            sb.append("\t");
        }
        sb.delete(sb.length() - 1, sb.length());

        sb.append("\n");
        this.appendLine(sb);
    }

    public void close() {
        try {
            this.writer.flush();
            this.writer.close();
        } catch (final IOException e) {
            throw new IllegalStateException("Couldn't close writer.", e);
        }
    }

    // ------------------------------------------------------------------------------------------ //
    // PRIVATE
    // ------------------------------------------------------------------------------------------ //

    private Writer createWriter() {
        final Path path = OUTPUT_CSV_FOLDER.resolve(this.simpleDateFormat.format(new Date()) + ".csv");
        try {
            return Files.newBufferedWriter(path, StandardOpenOption.CREATE);
        } catch (final IOException e) {
            throw new IllegalStateException("Couldn't create writer for: " + path, e);
        }
    }

    private void appendLine(final StringBuilder sb) {
        try {
            this.writer.append(sb.toString());
        } catch (final IOException e) {
            throw new IllegalStateException("Couldn't append line: " + sb.toString());
        }
    }

}
