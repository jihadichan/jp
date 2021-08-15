package core;

import models.KanjiData;

import java.util.ArrayList;

public class TestHelpers {

    public static KanjiData createKanjiDataDummy(final char kanji) {
        return new KanjiData(
                kanji,
                0,
                null,
                0,
                new ArrayList<>(),
                new ArrayList<>(),
                0,
                null,
                null,
                0
        );
    }

}
