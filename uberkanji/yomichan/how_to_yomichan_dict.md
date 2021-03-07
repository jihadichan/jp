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

Set PopUp size in section "Popup Position & Size"



## AnkiConnect Settings

Settings to import from Yomichan pop-up to Anki. Settings > Anki > Configure Anki card format

- Deck: UberSentences_Vocab_1
- Model: UberSentences
- Card tags: `vocab1/`
- Also check `yomichan-settings-2021-03-07-17-30-11.json`

```
sentence: {furigana}
notes: {glossary}<br>Sentence:<br>{sentence}
source: {clipboard-image}<br>{document-title}<br>{url}
mp3: {audio}
data: {}
```

