package core;

import com.google.common.base.Joiner;
import models.KanjiData;
import utils.JpHelpers;

import static java.lang.System.out;

public class JishoReadingScraper {

    private final JishoApi jishoApi = new JishoApi();

    public void run() {
        JpHelpers.getKanjiDataFromCsv().forEach(kanjiData -> {
            try {
                final KanjiData data = this.jishoApi.getDataForKanji(kanjiData);
                final StringBuilder sb = new StringBuilder();

                sb.append(data.getKanji());
                sb.append("|");
                sb.append(data.getId());
                sb.append("|");
                sb.append(data.getMeanings());
                sb.append("|");
                sb.append(data.getFrequency());
                sb.append("|");
                sb.append(Joiner.on("、").join(data.getOnReadings()));
                sb.append("|");
                sb.append(Joiner.on("、").join(data.getKunReadings()));
                sb.append("|");
                sb.append(data.getStrokes());
                sb.append("|");
                sb.append(data.getComponents());
                sb.append("|");
                sb.append(data.getRtkKeyword());
                sb.append("|");
                sb.append(data.getRtkIndex());

                out.println(sb.toString());
            } catch (final Exception e) {
                throw new IllegalStateException("Couldn't get readings for kanji: " + kanjiData.getKanji(), e);
            }
        });
    }

}
