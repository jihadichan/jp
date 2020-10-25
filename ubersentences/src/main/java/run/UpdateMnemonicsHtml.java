package run;

import converters.mnemonics.MnemonicsCsvLoader;
import converters.mnemonics.MnemonicsCsvLoader.Mnemonic;
import converters.mnemonics.MnemonicsHtmlCreator;

import java.nio.file.Path;
import java.util.List;

import static config.Config.projectRoot;

public class UpdateMnemonicsHtml {

    private static final Path ankiExportsPath = projectRoot.resolve("ankiexports");

    private static final List<Path> exportFiles = List.of(
            ankiExportsPath.resolve("UberKanji-Core.txt"),
            ankiExportsPath.resolve("UberKanji-Rare.txt")
    );

    public static void main(final String[] args) {
        final List<Mnemonic> list = new MnemonicsCsvLoader(exportFiles).getMnemonics();
        final String html = MnemonicsHtmlCreator.createHtml(list);
        MnemonicsHtmlCreator.writeFile(html);
    }

}
