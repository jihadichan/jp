package scrapers.meknow.meknow;

import scrapers.meknow.meknow.MeKnowScraper.TempSentence;

import java.util.regex.Pattern;

public class MeKnowSentence {

    private final String japanese;
    private final String english;
    private final String source;
    private static final Pattern quotePattern = Pattern.compile("\"");

    MeKnowSentence(final TempSentence tempSentence, final Course course) {
        this.japanese = this.replaceQuotes(tempSentence.japanese.trim());

        // English
        String tempEnglish = tempSentence.english;
        if (tempSentence.translit != null) {
            tempEnglish += "<br>" + tempSentence.translit;
        }
        if (tempSentence.focusWord != null) {
            tempEnglish += "<br>" + tempSentence.focusWord;
        }
        this.english = this.replaceQuotes(tempEnglish);

        // Source
        this.source = this.replaceQuotes(course.getName() + "<br>" + course.getUrl());
    }

    private String replaceQuotes(final String value) {
        return quotePattern.matcher(value).replaceAll("'");
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
}
