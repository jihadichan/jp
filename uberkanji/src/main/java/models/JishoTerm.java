package models;

import com.google.common.base.Joiner;
import core.FrequencyDictionary;
import models.jisho.Data;
import models.jisho.Sense;
import models.jisho.Term;
import org.jetbrains.annotations.NotNull;
import utils.JpHelpers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class JishoTerm implements Comparable<JishoTerm>, Serializable {

    private final String word;
    private final String reading;
    private final transient List<String> meaningsAsList;
    private final String meanings;
    private final Integer score; // 1 = isCommon, 1 = isJLPT, 0 = uncommon
    private final String usuallyWrittenAs;
    private final transient Boolean isCommon;
    private final transient Integer kanjiCount;
    private final String freqNF;
    private final String freqWK;
    private String foundKanjiReading = "???";
    private String type = "unset";

    public JishoTerm(final Term term, final Data data) {
        this.word = term.getWord();
        this.reading = JpHelpers.kataToHira(term.getReading());
        this.meaningsAsList = this.extractMeanings(data.getSenses());
        this.meanings = Joiner.on("<br>").join(this.extractMeanings(data.getSenses()));
        this.usuallyWrittenAs = this.extractWriting(data);
        this.isCommon = data.getIsCommon();

        final AtomicInteger kanjiCounter = new AtomicInteger();
        for (final char character : term.getWord().toCharArray()) {
            if (JpHelpers.isKanji(character)) {
                kanjiCounter.incrementAndGet();
            }
        }
        this.kanjiCount = kanjiCounter.get();

        // Frequencies
        final Integer freqNfInt = FrequencyDictionary.freqNF.get(this.word);
        this.freqNF = freqNfInt != null ? String.valueOf(freqNfInt) : null;
        final Integer freqWkInt = FrequencyDictionary.freqWK.get(this.word);
        this.freqWK = freqWkInt != null ? String.valueOf(freqWkInt) : null;

        this.score = this.extractScore(data);
    }

    private String extractWriting(final Data data) {
        for (final Sense sense : data.getSenses()) {
            for (final String tag : sense.getTags()) {
                if (tag.contains("written using kana alone")) {
                    return "Kana";
                }
            }
        }
        return "Kanji";
    }

    private int extractScore(final Data data) {
        if (data.getIsCommon()) {
            return 1; // 2 is unnecessary and just confusing, JLPT is an uncommon occurrence and can just be counted as common word.
        }
        if (!data.getJlpt().isEmpty()) {
            return 1;
        }
        // In some cases Jisho doesn't marks words as common although they're in frequency lists
        final Integer freqNfObj = FrequencyDictionary.freqNF.get(this.word);
        final int freqNfInt = freqNfObj == null ? 99_999 : freqNfObj;
        final Integer freqWkObj = FrequencyDictionary.freqWK.get(this.word);
        final int freqWkInt = freqWkObj == null ? 99_999 : freqWkObj;
        if (freqNfInt <= 20_000 || freqWkInt <= 20_000) {
            return 1;
        }
        return 0;
    }

    private List<String> extractMeanings(final List<Sense> senses) {
        final List<String> list = new ArrayList<>();
        senses.forEach(sense -> {
            final boolean isWikipediaDef;
            final String partsOfSpeech;
            if (!sense.getPartsOfSpeech().isEmpty()) {
                isWikipediaDef = sense.getPartsOfSpeech().get(0).toLowerCase().contains("wikipedia");
                partsOfSpeech = sense.getPartsOfSpeech().get(0);
            } else {
                isWikipediaDef = false;
                partsOfSpeech = null;
            }

            String meanings = Joiner.on(", ").join(sense.getEnglishDefinitions()) + addPartsOfSpeech(partsOfSpeech);
            meanings = meanings.replaceAll("\"", "'");
            if (!isWikipediaDef) {
                list.add(meanings);
            }
            if (list.isEmpty() && isWikipediaDef) {
                list.add(meanings);
            }
        });
        return list;
    }

    private static String addPartsOfSpeech(final String partsOfSpeech) {
        if (partsOfSpeech != null) {
            return " (" + partsOfSpeech + ")";
        }
        return "";
    }

    public void setType(final char kanji) {
        if (JpHelpers.isFollowedByKanjiOrNothing(kanji, this.word)) {
            this.type = "fnKJ";
        } else if (JpHelpers.isFollowedByKana(kanji, this.word)) {
            this.type = "fnKN";
        } else {
            this.type = "oFrm";
        }
    }

    public String getUsuallyWrittenAs() {
        return this.usuallyWrittenAs;
    }

    public String getWord() {
        return this.word;
    }

    public String getReading() {
        return this.reading;
    }

    public List<String> getMeaningsAsList() {
        return this.meaningsAsList;
    }

    public int getScore() {
        return this.score;
    }

    public String getFoundKanjiReading() {
        return this.foundKanjiReading;
    }

    public void setFoundKanjiReading(final String foundKanjiReading) {
        this.foundKanjiReading = foundKanjiReading;
    }

    public Boolean isCommon() {
        return this.isCommon;
    }

    public String getType() {
        return this.type;
    }

    @Override
    public int compareTo(@NotNull final JishoTerm o) {
        if (!o.score.equals(this.score)) {
            return o.score.compareTo(this.score);
        }
        if (!o.kanjiCount.equals(this.kanjiCount)) {
            return this.kanjiCount.compareTo(o.kanjiCount);
        }
        return Integer.compare(this.word.length(), o.word.length());
    }
}
