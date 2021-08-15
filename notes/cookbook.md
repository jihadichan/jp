# Cookbook



## Anki



### How to install / deinstall / upgrade Anki

**Install:**

Download desired version from [here](https://apps.ankiweb.net/).

```bash
sudo make install
```

**Uninstall:**

```bash
cd /usr/local/share/anki
sudo make uninstall
```

**Upgrade:**

1. Uninstall (Library at `~/.local/share/Anki2` & AddOns will not be deleted. So no worries, see `Makefile` to make sure.)
2. Install another version.



### Plugins

- **Migaku Retirement**
  See tutorial video [here](https://www.migaku.io/tools-guides/migaku-retirement/guide/#setting-a-retiring-interval). 



### How to move vocab deck contents

Let's say you want to archive `vocab2`.

1. Export notes, include all
2. Open in Excel
3. Copy complete column with images (`<img src="yomichan_...`) and replace `yomichan_` with `vocab2/yomichan_`
4. Same for `[sound:yomichan_` column.
5. Copy all image & mp3 files from `/.local/share/Anki2/User 1/collection.media`, into new folder `vocab2`
6. Reimporting, don't forget to set to "update" and allow HTML

Don't forget to set your new deck in the AnkiConnect settings in Yomichan.



## YomiChan



### Overwrite KanjiDict

1. Export kanji decks. 
   - Names must match `^UberKanji.*\.txt$`
   - Put into `/uberkanji/yomichan/deck`
2. Either leave existing KanjiDict in `/uberkanji/yomichan/dict` as it is (should be alright). Or download clean version from [website](https://foosoft.net/projects/yomichan/).
3. Run `/uberkanji/src/main/java/yomichan/_UpdateDictForYomichan.java`
4. `cp output.json kanji_bank_1.json` (in `/uberkanji/yomichan/dict`), remove all other files like `kanji_bank_*`. Output contains all entries.
5. Zip all left-over JSON files to `kanjidic_english.zip`
6. Import in Yomichan (delete `KANJIDIC (English) rev.kanjidic2` beforehand)
7. Works immediately, no browser restart needed



### CSS Settings

Settings > PopUp > Custom CSS

Popup CSS:

```css
.kanji-glyph-container {
  display:none;
}
```

Set PopUp size in section "Popup Position & Size"



### AnkiConnect Settings

Settings to import from Yomichan pop-up to Anki. Settings > Anki > Configure Anki card format

- Deck: UberSentences_Vocab_1
- Model: UberSentences
- Card tags: `vocab1/`
- Also check `_res/yomichan-settings-2021-03-07-17-30-11.json`

```
sentence: {furigana}
notes: {glossary}<br>Sentence:<br>{sentence}
source: {clipboard-image}<br>{document-title}<br>{url}
mp3: {audio}
data: {}
```



## HotKeys for Animelon & Netflix

See `/jp/tools/hotkeys/readme.md`



## UberKanji

### Update confusions

1. Export UberKanji deck to `uberkanji/yomichan/deck/UberKanji.txt`
2. Run `uberkanji/src/main/java/confs/CreateConfJson.java`
3. Copy resulting `uberkanji/confs/confs.js` to `uberkanji/statics/` in Anki collection.

