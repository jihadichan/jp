package tryouts;


import com.google.common.base.Joiner;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetConfsFromDeck {

    private static final Pattern confPattern = Pattern.compile("!conf\\s.*?<");
    private static final Pattern kanjiPattern = Pattern.compile("\\p{InCJK_Unified_Ideographs}");

    @Test
    public void run() throws Exception {
        for (String line : Files.readAllLines(Paths.get("yomichan/deck/UberKanji.txt"), StandardCharsets.UTF_8)) {
            Matcher confMatcher = confPattern.matcher(line);
            List<String> confList = new ArrayList<>();
            confList.add(String.valueOf(line.charAt(0)));
            if(confMatcher.find()) {
                Matcher kanjiMatcher = kanjiPattern.matcher(confMatcher.group(0));
                while (kanjiMatcher.find()) {
                    confList.add(kanjiMatcher.group(0));
                }
                System.out.println(Joiner.on(" cf ").join(confList));
            }
        }
    }

}
