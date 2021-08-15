package core;

import models.KanjiData;
import models.jisho.JishoResult;
import org.junit.Test;

import static core.TestHelpers.createKanjiDataDummy;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class JishoApiTest {

    @Test
    public void get_depth3() throws Exception {
        final JishoApi jishoApi = new JishoApi();
        final JishoResult jishoResult1 = jishoApi.getWordsForSingleKanji(createKanjiDataDummy('朝'), 3);
        assertThat(jishoResult1.getData().size(), is(60));

        final JishoResult jishoResult2 = jishoApi.getWordsForSingleKanji(createKanjiDataDummy('朝'), 3);
        assertThat(jishoResult2.getData().size(), is(44));
    }

    @Test
    public void getKanjiInfo() throws Exception {
        final JishoApi jishoApi = new JishoApi();
        KanjiData kanjiReading = jishoApi.getDataForKanji(createKanjiDataDummy('朝'));
        assertThat(kanjiReading.getKanji(), is('戦'));
        assertThat(kanjiReading.getMeanings(), is("war, battle, match"));
        assertThat(kanjiReading.getFrequency(), is(78));
        assertThat(kanjiReading.getOnReadings().size(), is(1));
        assertThat(kanjiReading.getKunReadings().size(), is(5));
        assertThat(kanjiReading.getStrokes(), is("13"));
    }

}
