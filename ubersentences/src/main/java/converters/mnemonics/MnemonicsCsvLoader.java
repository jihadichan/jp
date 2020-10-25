package converters.mnemonics;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MnemonicsCsvLoader {

    private static final Pattern kanaPattern = Pattern.compile("([\\u4E00-\\u9FAF\\u3040-\\u3096\\u30A1-\\u30FA\\uFF66-\\uFF9D\\u31F0-\\u31FF]+.?)+ - .*?<br/?>");
    private static final Pattern endingBreak = Pattern.compile("<br/?>$");
    private static final CSVFormat format = CSVFormat.RFC4180.withDelimiter('\t');
    public final List<Mnemonic> mnemonics;

    public MnemonicsCsvLoader(final List<Path> exportFiles) {
        this.mnemonics = load(exportFiles);
    }

    private static List<Mnemonic> load(final List<Path> exportFiles) {
        for (final Path path : exportFiles) {
            if (!path.toFile().exists()) {
                throw new IllegalStateException("Configured mnemonic file does not exist, need: " + path);
            }
        }

        final List<Mnemonic> list = new ArrayList<>();
        exportFiles.forEach(path -> {
            for (final CSVRecord record : createReader(path)) {
                final String kanji = record.get(0);
                if (kanji.length() > 1) {
                    throw new IllegalStateException("Kanji is more than 1 char");
                }
                list.add(new Mnemonic(kanji.charAt(0), getMnemonics(record.get(3))));
            }
        });

        return list;
    }


    // ------------------------------------------------------------------------------------------ //
    // HELPERS
    // ------------------------------------------------------------------------------------------ //

    private static CSVParser createReader(final Path path) {
        try {
            return new CSVParser(Files.newBufferedReader(path), format);
        } catch (final IOException e) {
            throw new IllegalStateException("Failed to load " + path);
        }
    }

    private static String getMnemonics(final String cell) {
        final Matcher matcher = kanaPattern.matcher(cell);

        final StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            sb.append(matcher.group());
        }

        return endingBreak.matcher(sb.toString()).replaceAll("");
    }

    public static class Mnemonic {

        public char kanji;
        public String mnemonics;

        public Mnemonic(final char kanji, final String mnemonics) {
            this.kanji = kanji;
            this.mnemonics = mnemonics;
        }
    }


    // ------------------------------------------------------------------------------------------ //
    // GETTER
    // ------------------------------------------------------------------------------------------ //

    public List<Mnemonic> getMnemonics() {
        return this.mnemonics;
    }
}
