package converters.meknow.run;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static converters.meknow.run.MeKnowConfig.MEKNOW_IMMERSION_FOLDER;
import static converters.meknow.run.MeKnowConfig.MEKNOW_STEPS_FOLDER;

public class CreateImmersion {

    public static void main(final String[] args) {

        SentenceDict.map.values().stream()
                .forEach(sentenceData -> {
//                    System.out.println(sentenceData.fileNamePrefix + "-" + sentenceData.japanese);
                    final Path file = MEKNOW_STEPS_FOLDER.resolve(String.valueOf(sentenceData.level)).resolve(sentenceData.fileName);
                    final String sentence = sentenceData.japanese.replaceAll("(<b>|</b>)", "");
                    final Path target = MEKNOW_IMMERSION_FOLDER.resolve(sentenceData.fileNamePrefix + "-" + sentence + ".mp3");
                    replaceFile(file, target);
                });

    }

    private static void replaceFile(final Path file, final Path target) {
        try {
            System.out.println("From: " + file);
            System.out.println("To  : " + target);
            Files.copy(file, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }

}
