package confs;

import com.google.gson.Gson;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static confs.CreateConfJson.asUnicode;

public class MnemonicsCsvLoader {

    private static final Pattern kanaPattern = Pattern.compile("([\\u4E00-\\u9FAF\\u3040-\\u3096\\u30A1-\\u30FA\\uFF66-\\uFF9D\\u31F0-\\u31FF]+.?)+ [-=] .*?<br/?>");
    private static final Pattern endingBreak = Pattern.compile("<br/?>$");
    private static final CSVFormat format = CSVFormat.RFC4180.withDelimiter('\t');
//    private static final Pattern brPattern = Pattern.compile("<br.*");
    private static final Gson GSON = new Gson();
    public final Map<String, ConfData> mnemonics;

    public MnemonicsCsvLoader(final List<Path> exportFiles) {
        this.mnemonics = load(exportFiles);
    }

    private static Map<String, ConfData> load(final List<Path> exportFiles) {
        for (final Path path : exportFiles) {
            if (!path.toFile().exists()) {
                throw new IllegalStateException("Configured mnemonic file does not exist, need: " + path);
            }
        }

        final Map<String, ConfData> map = new HashMap<>();
        exportFiles.forEach(path -> {
            for (final CSVRecord record : createReader(path)) {
                final String kanji = record.get(0);
                if (kanji.length() > 1) {
                    throw new IllegalStateException("Kanji is more than 1 char, got: " + record.get(0));
                }
                final ConfData mnemonic = new ConfData();
                mnemonic.kj = kanji.charAt(0);
                mnemonic.m = getConfDataMap(record.get(3));
                mnemonic.r = getMainReading(record.get(4));
                addKeywords(mnemonic, record.get(2), record.get(8));
                map.put(asUnicode(kanji.charAt(0)), mnemonic);
            }
        });

        return map;
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

    private static String getConfDataMap(final String cell) {
        final Matcher matcher = kanaPattern.matcher(cell);

        final StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            sb.append(matcher.group());
        }

        return endingBreak.matcher(sb.toString()).replaceAll("");
    }

    private static String getMainReading(String cell) {
        cell = cell.trim();
        if (cell.isBlank()) {
            return "";
        }
        final String[] parts = cell.split("\\.");
//        return kanaToRomaji(parts[0].trim());
        return parts[0].trim();
    }

    private static void addKeywords(final ConfData mnemonic, final String concept, final String encodedJson) {

        // Concept
        if (concept == null || concept.isBlank()) {
            mnemonic.cp = "";
        } else {
//            mnemonic.cp = brPattern.matcher(concept.trim()).replaceAll("");
            mnemonic.cp = concept.trim();
        }

        // RTK keyword
        final String json = URLDecoder.decode(encodedJson, Charset.defaultCharset());
        final KanjiJson kanjiJson = GSON.fromJson(json, KanjiJson.class);
        if (kanjiJson == null || kanjiJson.rtkKeyword == null) {
            mnemonic.rtk = "";
        } else {
            mnemonic.rtk = kanjiJson.rtkKeyword.trim();
        }
    }

    private static class KanjiJson {
        String rtkKeyword;
    }

    public static class ConfData {

        public char kj; // kanji
        public String m; // mnemonic
        public String r; // main reading
        public String rtk; // rtk keyword
        public String cp; // concept

    }


    // ------------------------------------------------------------------------------------------ //
    // GETTER
    // ------------------------------------------------------------------------------------------ //

    public Map<String, ConfData> getConfDataMap() {
        return this.mnemonics;
    }
}
