# How To

1. Export kanji decks. 
   - Names must match `^UberKanji.*\.txt$`
   - Put into `/uberkanji/yomichan/deck`
2. Either leave existing KanjiDict in `/uberkanji/yomichan/dict` as it is (should be alright). Or download clean version from [website](https://foosoft.net/projects/yomichan/).
3. Run `/uberkanji/src/main/java/yomichan/_UpdateDictForYomichan.java`
4. `cp output.json kanji_bank_1.json` (in `/uberkanji/yomichan/dict`), remove all other files like `kanji_bank_*`. Output contains all entries.
5. Zip all left-over JSON files to `kanjidic_english.zip`
6. Import in Yomichan (delete `KANJIDIC (English) rev.kanjidic2` beforehand)
7. Works immediately, no browser restart needed



## CSS Settings

Settings > PopUp > Custom CSS

Popup CSS:

```css
.kanji-glyph-container {
  display:none;
}
```

Popup outer CSS:

```css
.yomichan-popup {
  height: 350px !important;
}
```

