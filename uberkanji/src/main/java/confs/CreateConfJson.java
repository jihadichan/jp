package confs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import confs.MnemonicsCsvLoader.ConfData;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * This create the confs.json which is used to render the !conf lines into the kanji cards
 */
public class CreateConfJson {

    private static final List<Path> uberKanjiExportPaths = List.of(
            Paths.get("yomichan/deck/UberKanji.txt")
    );
    private static final AtomicInteger groupIndex = new AtomicInteger();
    private static final Map<String, Integer> confGroupRefs = new HashMap<>(); // key=kanji, val=id of group
    private static final Map<Integer, Map<String, ConfData>> confGroups = new HashMap<>(); // key=kanji, val=Map of readings,meaning
    private static final Map<String, ConfData> confDataMap = new MnemonicsCsvLoader(uberKanjiExportPaths).getConfDataMap(); // key=kanji, val=reading,meaning
    private static final Pattern kanjiPattern = Pattern.compile("^\\p{InCJK_Unified_Ideographs}$");
    private static final Path confsFolder = Paths.get("confs");
    private static final Gson GSON = new GsonBuilder().create();

    public static void main(final String[] args) throws Exception {
        final List<String> confs = loadFileAsList(confsFolder.resolve("confList"));
        confs.forEach(line -> {
            final String[] kanjis = extractKanji(line);
            final Integer groupId = getConfGroupId(kanjis);
            putIntoConfGroups(kanjis, groupId);
        });

        final Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("refs", confGroupRefs);
        jsonMap.put("groups", confGroups);


        // Write confs.js
        final String confJsAsString = "var confMap = " + GSON.toJson(jsonMap) + ";";
        final byte[] confJsAsBytes = confJsAsString.getBytes(StandardCharsets.UTF_8);
        final Path confJs = confsFolder.resolve("confs.js");
        if (confJs.toFile().exists()) {
            if (!confJs.toFile().delete()) {
                throw new IllegalStateException("Failed to delete conf.js");
            }
        }
        Files.write(confsFolder.resolve("confs.js"), confJsAsBytes, StandardOpenOption.CREATE);

        // Write mnemonics.js
        final String mnemonicJsAsString = "var mnemonicsMap = " + GSON.toJson(confDataMap) + ";";
        final byte[] mnemonicJsAsBytes = mnemonicJsAsString.getBytes(StandardCharsets.UTF_8);
        final Path mnemonicsJs = confsFolder.resolve("mnemonics.js");
        if (mnemonicsJs.toFile().exists()) {
            if (!mnemonicsJs.toFile().delete()) {
                throw new IllegalStateException("Failed to delete mnemonics.js");
            }
        }
        Files.write(confsFolder.resolve("mnemonics.js"), mnemonicJsAsBytes, StandardOpenOption.CREATE);
    }

    private static void putIntoConfGroups(final String[] kanjis, final Integer groupId) {
        // Get confGroup or create an empty one if not exists
        final Map<String, ConfData> confGroup = confGroups.computeIfAbsent(groupId, k -> new HashMap<>());

        for (final String kanji : kanjis) {
            // Check if given kanji is actually known in the deck
            final ConfData confData = confDataMap.get(asUnicode(kanji.charAt(0)));
            if (confData == null) {
                throw new IllegalStateException("Failed to get ConfData for kanji, need key: '" + kanji + "'");
            }
            confGroup.put(asUnicode(kanji.charAt(0)), confData);
        }
    }

    private static String[] extractKanji(final String line) {
        final String[] split = line.split(" cf ");
        for (final String kanji : split) {
            // Line has proper format
            if (!kanjiPattern.matcher(kanji).matches() || kanji.length() != 1) {
                throw new IllegalArgumentException("Failed to parse confList, need pattern '自 cf 白( cf 目)*', but line was: '" + line + "'");
            }
        }
        return split;
    }

    // Check if an ID exists for any for the kanji. If so, then it's a known group and every kanji in that line belongs to it.
    private static Integer getConfGroupId(final String[] kanjis) {
        Integer id = null;
        for (final String kanji : kanjis) {
            final Integer index = confGroupRefs.get(kanji);
            if (index != null) {
                id = index;
                break;
            }
        }
        if (id != null) {
            return id;
        }
        id = groupIndex.incrementAndGet();
        for (final String kanji : kanjis) {
            confGroupRefs.put(asUnicode(kanji.charAt(0)), id);
        }
        return id;
    }


    private static List<String> loadFileAsList(final Path path) {
        try {
            return Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (final IOException e) {
            throw new IllegalStateException("Failed to load file at: " + path);
        }
    }

    public static String asUnicode(final char character) {
        return Integer.toHexString(character);
    }

}
