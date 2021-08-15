package models;

import com.atilika.kuromoji.ipadic.Token;
import core.FrequencyDictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static utils.Utils.isKanji;
import static utils.Utils.kataToHira;

public class TermInfo {

    private final String surface;
    private final String baseForm;
    private final String pronunciation;
    private final String conjugationForm;
    private final String conjugationType;
    private final String reading;
    private final int pos;
    private final String freqNF;
    private final String freqWK;
    private final List<String> partsOfSpeech = new ArrayList<>();


    public TermInfo(final Token token) {

        // KUROMOJI
        this.surface = token.getSurface();
        this.baseForm = this.surface.equals(token.getBaseForm()) || token.getBaseForm().equals("*") ? null : token.getBaseForm();
        this.reading = containsKanji(this.surface) ? token.getReading() : null;
        this.pronunciation = token.getReading().equals(token.getPronunciation()) ? null : token.getPronunciation();
        this.conjugationForm = token.getConjugationForm().equals("*") ? null : token.getConjugationForm();
        this.conjugationType = token.getConjugationType().equals("*") ? null : token.getConjugationType();
        this.pos = token.getPosition();

        if (!token.getPartOfSpeechLevel1().equals("*")) {
            this.partsOfSpeech.add(token.getPartOfSpeechLevel1());
        }
        if (!token.getPartOfSpeechLevel2().equals("*")) {
            this.partsOfSpeech.add(token.getPartOfSpeechLevel2());
        }
        if (!token.getPartOfSpeechLevel3().equals("*")) {
            this.partsOfSpeech.add(token.getPartOfSpeechLevel3());
        }
        if (!token.getPartOfSpeechLevel4().equals("*")) {
            this.partsOfSpeech.add(token.getPartOfSpeechLevel4());
        }

        // FREQUENCIES
        this.freqNF = this.checkFrequency(FrequencyDictionary.freqNF, token);
        this.freqWK = this.checkFrequency(FrequencyDictionary.freqWK, token);
    }

    private String checkFrequency(final Map<String, Integer> freqDict, final Token token) {
        final Integer asKanji = freqDict.get(token.getBaseForm());
        if (asKanji != null) {
            return String.valueOf(asKanji);
        }
        final Integer asKatakana = freqDict.get(token.getReading());
        if (asKatakana != null) {
            return String.valueOf(asKatakana);
        }
        final Integer asHiragana = freqDict.get(kataToHira(token.getReading()));
        if (asHiragana != null) {
            return String.valueOf(asHiragana);
        }
        return "99999";
    }

    private static boolean containsKanji(final String word) {
        for (final char ch : word.toCharArray()) {
            if (isKanji(ch)) {
                return true;
            }
        }
        return false;
    }

    public String getSurface() {
        return this.surface;
    }

    public String getBaseForm() {
        return this.baseForm;
    }
}
