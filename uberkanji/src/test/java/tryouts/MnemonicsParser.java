package tryouts;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MnemonicsParser {

    String mn = "!done<br>国 - a good country is like a box full of jewels<br>こく - there are koku birds<br>くに - in wano kuni<br><br>---<br><ruby>国民<rt>こくみん</rt></ruby> (コク, people) <br><ruby>国<rt>くに</rt></ruby> (くに, country)<br>---<br>---<br><ruby>中国<rt>ちゅうごく</rt></ruby> (ごく, China)<br><ruby>島国<rt>しまぐに</rt></ruby> (ぐに, island country)<br><ruby>国家<rt>こっか</rt></ruby> (こっ, state, country, nation)";

    private static final Pattern readingPattern = Pattern.compile("([一-龯ぁ-んァ-ン]+) -.*");
    private static final Pattern vocabPattern = Pattern.compile("<ruby>.*(\\(([ぁ-んァ-ン]+),.*\\))");

    @Test
    public void asdf() {
        final String normalizedMn = normalizeMnemonicField(this.mn);

        System.out.println(extractKnownReadings(normalizedMn));
        System.out.println(extractMnemonics(normalizedMn));
    }

    private static String extractKnownReadings(final String normalizedMnemonicsField) {
        final StringBuilder sb = new StringBuilder();

        final Matcher matcher = vocabPattern.matcher(normalizedMnemonicsField);
        while (matcher.find()) {
            sb.append(matcher.group(2).trim());
            sb.append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());

        return sb.toString();
    }

    private static List<String> extractMnemonics(final String normalizedMnemonicsField) {
        final List<String> mnemonics = new ArrayList<>();
        final Matcher matcher = readingPattern.matcher(normalizedMnemonicsField);
        while (matcher.find()) {
            mnemonics.add(matcher.group(0).trim());
        }
        return mnemonics;
    }

    private static String normalizeMnemonicField(final String mnemonic) {
        return mnemonic.replaceAll("<br>", "\n");
    }


}
