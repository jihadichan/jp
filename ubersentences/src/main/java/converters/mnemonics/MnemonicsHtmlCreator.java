package converters.mnemonics;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import converters.mnemonics.MnemonicsCsvLoader.Mnemonic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

import static config.Config.projectRoot;

public class MnemonicsHtmlCreator {

    private static final Path htmlPath = projectRoot.resolve("grammarguide/jihadichan.github.io/kanji/assets/mnemonics-data.js");
    private static final Gson GSON = new GsonBuilder().create();

    public static void writeFile(final List<Mnemonic> list) {
        try {
            final String json = "var data = " + GSON.toJson(list) + ";";
            Files.write(htmlPath, json.getBytes(), StandardOpenOption.CREATE);
        } catch (final IOException e) {
            throw new IllegalStateException("Failed to write file.");
        }
    }

}
