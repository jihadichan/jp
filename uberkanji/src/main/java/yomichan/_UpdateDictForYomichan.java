package yomichan;

import com.google.gson.internal.LinkedTreeMap;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static utils.JpHelpers.GSON;
import static yomichan.YomichanConfig.MODIFIED_KANJI_DICT_FILE;

public class _UpdateDictForYomichan {

    static {
        KanjiDeckLoader.load();
        KanjiDictLoader.load();
    }

    private static final Map<Character, List<Object>> dict = KanjiDictLoader.getDict();
    private static final Map<Character, KanjiDeckEntry> deck = KanjiDeckLoader.getDeck();


    public static void main(final String[] args) {

        deck.forEach((kanji, deckEntry) -> {
            final List<Object> dictEntry = getDictEntry(kanji);
            mergeEntries(deckEntry, dictEntry);
        });
        writeFile();
    }

    private static void mergeEntries(final KanjiDeckEntry deckEntry, final List<Object> dictEntry) {

        dictEntry.set(1, ""); // Onyomi reading
        dictEntry.set(2, ""); // Kunyomi reading
        dictEntry.set(4, createGlossaryField(deckEntry, dictEntry)); // Glossary field
    }

    private static List<String> createGlossaryField(final KanjiDeckEntry deckEntry, final List<Object> dictEntry) {
        final List<String> list = new ArrayList<>();

        // Concept
        if (deckEntry.getConcept().isBlank()) {
            list.add("No concept");
        } else {
            list.add(deckEntry.getConcept());
        }

        // Mnemonics
        list.add("");
        if (deckEntry.getMnemonicsList().isEmpty()) {
            list.add("No mnemonics");
        } else {
            list.addAll(deckEntry.getMnemonicsList());
        }

        // Readings
        list.add("");
        if (deckEntry.getKnownReadings().isBlank()) {
            list.add("No known readings");
        } else {
            list.add(deckEntry.getKnownReadings());
        }

        // Stats
        list.add("");
        final Map<String, String> map = (LinkedTreeMap<String, String>) dictEntry.get(5);
        final String freq = map.get("freq")+""; // We need a clone. Because reference becomes null after deletion. new String() shows warning.
        list.add("Freq: " + freq);

        // Delimiter
        list.add("______________________________________________");

        return list;
    }

    private static void writeFile() {
        final String json = GSON.toJson(dict.values());
        try {
            Files.write(MODIFIED_KANJI_DICT_FILE, json.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * At the time of writing this every kanji from your deck existed in the kanji dict.
     * If you discover kanji so rare that they don't occur then you might want to remove the exception, because who
     * cares about such kanji.
     */
    private static List<Object> getDictEntry(final Character ch) {
        final List<Object> objectList = dict.get(ch);
        if (objectList != null) {
            return objectList;
        }
        throw new IllegalStateException("Kanji couldn't be found in dict, need: " + ch);
    }


}
