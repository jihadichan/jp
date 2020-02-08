package scrapers.tanos;

import org.jetbrains.annotations.NotNull;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

public class TanosSentence implements Comparable<TanosSentence> {

    private final String japanese;
    private final String english;
    private final String source;
    private static final String GRAMMAR_GUIDE_BASE_URL = "https://www.kanshudo.com/grammar/";
    private static final Pattern splitterPattern = Pattern.compile(" ");
    private static final Pattern nonLetterPattern = Pattern.compile("[^\\p{L}]");
    private static final Pattern quotePattern = Pattern.compile("\"");
    private static final Pattern tildePattern = Pattern.compile("～");

    public TanosSentence(final String rawText,
                         final String sourceUrl,
                         final String grammarItemName) throws Exception {
        // Check if sentence can be parsed
        final String[] split = splitterPattern.matcher(rawText).replaceFirst("#SPACE#").split("#SPACE#");
        if (split.length != 2 || rawText.contains("�")) {
            throw new IllegalStateException("Failed to split: " + rawText);
        }

        this.japanese = quotePattern.matcher(split[0].trim()).replaceAll("'");
        this.english = quotePattern.matcher(split[1].trim()+"<br>Grammar Item: "+grammarItemName).replaceAll("'");
        this.source = this.createSource(sourceUrl, grammarItemName).trim();
    }

    private String createSource(final String sourceUrl, final String grammarItemName) {
        final String cleanGrammarItemName = URLEncoder.encode(nonLetterPattern.matcher(grammarItemName).replaceAll(""), Charset.defaultCharset());
        return "Grammar Item: " + grammarItemName + "<br>" +
                "Source: " + sourceUrl + "<br>" +
                "Grammar Info: " + GRAMMAR_GUIDE_BASE_URL + cleanGrammarItemName;
    }

    public String getJapanese() {
        return this.japanese;
    }

    public String getEnglish() {
        return this.english;
    }

    public String getSource() {
        return this.source;
    }

    @Override
    public int compareTo(@NotNull final TanosSentence tanosSentence) {
        return Integer.compare(this.japanese.length(), tanosSentence.japanese.length());
    }
}
