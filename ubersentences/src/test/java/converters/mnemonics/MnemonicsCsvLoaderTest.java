package converters.mnemonics;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MnemonicsCsvLoaderTest {

    private static final Pattern kanaPattern = Pattern.compile("([\\u4E00-\\u9FAF\\u3040-\\u3096\\u30A1-\\u30FA\\uFF66-\\uFF9D\\u31F0-\\u31FF]+.?)+ - .*?<br/?>");
    private static final Pattern endingBreak = Pattern.compile("<br/?>");

    @Test
    public void mnemonicsColumn() {
        final String cell = "a good country is like a box full of jewels !done<br><br>こく - there are koku birds<br>くに - in wano kuni<br><br>---<br><ruby>国民<rt>こくみん</rt></ruby> (コク, people) <br><ruby>国<rt>くに</rt></ruby> (くに, country)<br>---<br>---<br><ruby>中国<rt>ちゅうごく</rt></ruby> (ごく, China)<br><ruby>島国<rt>しまぐに</rt></ruby> (ぐに, island country)<br># <ruby>国家<rt>こっか</rt></ruby> (こっ, state, country, nation)";

        System.out.println(this.getMnemonics(cell));
    }

    private String getMnemonics(final String cell) {
        final Matcher matcher = kanaPattern.matcher(cell);

        final StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            sb.append(matcher.group());
        }

        return endingBreak.matcher(sb.toString()).replaceAll("");
    }

}
