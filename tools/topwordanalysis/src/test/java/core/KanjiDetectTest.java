package core;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class KanjiDetectTest {

    @Test
    public void isKanji() {
        char hirgana = 'む';
        char katakana = 'ム';
        char kanji = '暑';

        assertThat(KanjiDetect.isKanji(hirgana), is(false));
        assertThat(KanjiDetect.isKanji(katakana), is(false));
        assertThat(KanjiDetect.isKanji(kanji), is(true));
    }

}
