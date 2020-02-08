package core;

import com.google.common.base.Joiner;
import models.JishoTerm;
import models.jisho.JishoResult;
import org.junit.Test;
import utils.JpHelpers;

import java.util.List;

import static core.TestHelpers.createKanjiDataDummy;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class JpHelpersTest {

    private final JishoApi jishoApi = new JishoApi();

    @Test
    public void kanaConverter() {
        assertThat(JpHelpers.kataToHira("チョウ"), is("ちょう"));
        assertThat(JpHelpers.kataToHira("ハン"), is("はん"));
        assertThat(JpHelpers.kataToHira("ちょうせんはんとう"), is("ちょうせんはんとう"));
    }

    @Test
    public void toTermList() throws Exception {
        final JishoResult jishoResult = this.jishoApi.getWordsForSingleKanji(createKanjiDataDummy('朝'), 1);
        final List<JishoTerm> termList = JpHelpers.toJishoTermList(jishoResult);
        termList.forEach(termData -> {
            System.out.println(termData.getWord() + " - " + termData.getReading() + " - " + Joiner.on(", ").join(termData.getMeaningsAsList()));
        });
    }

    @Test
    public void isHiragana() {
        final String text1 = "にくびふぃ";
        final String text2 = "にくasdf";
        final String text3 = "にく日ふぃ";
        final String text4 = "にくフィ";
        assertThat(this.areAllCharsHiragana(text1), is(true));
        assertThat(this.areAllCharsHiragana(text2), is(false));
        assertThat(this.areAllCharsHiragana(text3), is(false));
        assertThat(this.areAllCharsHiragana(text4), is(false));
    }

    @Test
    public void isKatakana() {
        final String text1 = "ニクビフィ";
        final String text2 = "ニクasdf";
        final String text3 = "ニク日フィ";
        final String text4 = "にくびふぃ";
        assertThat(this.areAllCharsKatakana(text1), is(true));
        assertThat(this.areAllCharsKatakana(text2), is(false));
        assertThat(this.areAllCharsKatakana(text3), is(false));
        assertThat(this.areAllCharsKatakana(text4), is(false));
    }

    private boolean areAllCharsHiragana(final String text) {
        for (final char ch : text.toCharArray()) {
            if (!JpHelpers.isHiragana(ch)) {
                System.out.println("Non-Hiragana in " + text + " - char: " + ch);
                return false;
            }
        }
        return true;
    }

    private boolean areAllCharsKatakana(final String text) {
        for (final char ch : text.toCharArray()) {
            if (!JpHelpers.isKatakana(ch)) {
                System.out.println("Non-Katakana in " + text + " - char: " + ch);
                return false;
            }
        }
        return true;
    }

    @Test
    public void isFollowedByKanjiOrNothing() {
        char kanji = '申';
        assertThat(JpHelpers.isFollowedByKanjiOrNothing(kanji, "申す"), is(false));
        assertThat(JpHelpers.isFollowedByKanjiOrNothing(kanji, "申ク"), is(false));
        assertThat(JpHelpers.isFollowedByKanjiOrNothing(kanji, "申請請請"), is(true));
        assertThat(JpHelpers.isFollowedByKanjiOrNothing(kanji, "請申請請"), is(true));
        assertThat(JpHelpers.isFollowedByKanjiOrNothing(kanji, "申"), is(true));
        assertThat(JpHelpers.isFollowedByKanjiOrNothing(kanji, "請請請申"), is(true));
        assertThat(JpHelpers.isFollowedByKanjiOrNothing(kanji, "請請請請sdfgすすす"), is(false));
        assertThat(JpHelpers.isFollowedByKanjiOrNothing(kanji, "申っ"), is(false));
        assertThat(JpHelpers.isFollowedByKanjiOrNothing(kanji, "申ッ"), is(false));
        assertThat(JpHelpers.isFollowedByKanjiOrNothing(kanji, "申々"), is(false));
    }

    @Test
    public void isFollowedByKana() {
        char kanji = '申';
        assertThat(JpHelpers.isFollowedByKana(kanji, "申す"), is(true));
        assertThat(JpHelpers.isFollowedByKana(kanji, "申ク"), is(true));
        assertThat(JpHelpers.isFollowedByKana(kanji, "申請請請"), is(false));
        assertThat(JpHelpers.isFollowedByKana(kanji, "請申請請"), is(false));
        assertThat(JpHelpers.isFollowedByKana(kanji, "申"), is(false));
        assertThat(JpHelpers.isFollowedByKana(kanji, "請請請申"), is(false));
        assertThat(JpHelpers.isFollowedByKana(kanji, "請請請請sdfgすすす"), is(false));
        assertThat(JpHelpers.isFollowedByKana(kanji, "申っ"), is(true));
        assertThat(JpHelpers.isFollowedByKana(kanji, "申ッ"), is(true));
        assertThat(JpHelpers.isFollowedByKana(kanji, "申々"), is(false));
    }


}
