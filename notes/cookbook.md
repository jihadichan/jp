{:toc}



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



### How to filter by intervals?

Add this to the query:

```
prop:ivl>180
```

Will show all cards with more than 180 days interval. 



### How to move vocab deck contents?

Let's say you want to archive `vocab2`.

1. Export notes, include all
2. Open in Excel
3. Copy complete column with images (`<img src="yomichan_...`) and replace `yomichan_` with `vocab2/yomichan_`
4. Same for `[sound:yomichan_` column.
5. Copy all image & mp3 files from `/.local/share/Anki2/User 1/collection.media`, into new folder `vocab2`. On Windows go to `%APPDATA%\Anki2`
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

1. Add new confs to `confs/confList`
2. Export UberKanji deck to `uberkanji/yomichan/deck/UberKanji.txt`
3. Run `uberkanji/src/main/java/confs/CreateConfJson.java`
4. Copy resulting `uberkanji/confs/confs.js` to `uberkanji/statics/` in Anki collection.
5. Copy resulting `uberkanji/confs/mnemonics.js` to `ubersenteces/statics/` in Anki collection.





# DeckFarming

- All those sub2srs decks seem to use the same card type
- Get deck from [here](https://www.mediafire.com/folder/p17g5uk4phb41/User_Uploaded_Anki_Decks)
- Note that not all have translations
- Use the template below. Don't try to press them into the UberSentences format. All cool as it is.

## Front page

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
</head>
<body>
<!--------------------------------------------------------------------------->
<!-- COPY FROM HERE -->
<!--------------------------------------------------------------------------->
<link rel="stylesheet" type="text/css" href="ubersentences/statics/commons.css">
<link rel="stylesheet" type="text/css" href="ubersentences/statics/frontpage-style.css">
<div id="container">
    <div id="debug"></div>
    <div class="wrapper">
        <div class="layout">
            <div class="fp-sentence" id="sentence-container"></div>
        </div>
    </div>
</div>
<div id="sentence-raw" class="anki-field">{{FocusVocab}}<br>{{morphHighlight:Expression}}</div>
<div id="sentence-display" class="anki-field"></div>
<!--<script src="ubersentences/statics/jquery-3.4.1.min.js" type="application/javascript"></script>-->
<script src="ubersentences/statics/frontpage.js" type="application/javascript" charset="UTF-8"></script>
<!--------------------------------------------------------------------------->
<!-- TO HERE -->
<!--------------------------------------------------------------------------->
</body>
</html>
```

# Back page

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
</head>
<body>
<!--------------------------------------------------------------------------->
<!-- COPY FROM HERE -->
<!--------------------------------------------------------------------------->
<link rel="stylesheet" type="text/css" href="ubersentences/statics/commons.css">
<link rel="stylesheet" type="text/css" href="ubersentences/statics/backpage-style.css">
<div id="debug"></div>
<div id="solution" class="wrapper">
    <div id="overlay">
        <div id="overlay-text">
            1. Listen<br>
            2. Translate<br>
            3. Click<br>
        </div>
    </div>
    <div class="layout">
        <table class="wrapper-table">
            <!--------------------------------------------------------------------------->
            <!-- SENTENCE -->
            <!--------------------------------------------------------------------------->
            <tr>
                <td>
                    <div class="sentence-table">
                        <table>
                            <tr class="options-wrapper">
                                <td id="sentence">{{furigana:Reading}}</td>
                                <td id="options" rowspan="2"></td>
                            </tr>
                            <tr>
                                <td id="notes">- {{Meaning}}<br>{{Notes}}</td>
                            </tr>
                            <tr id="source-cell"></tr>
                        </table>
                    </div>
                </td>
            </tr>

            <!--------------------------------------------------------------------------->
            <!-- RENDERED -->
            <!--------------------------------------------------------------------------->
            <tr>
                <td id="rendered-content"></td>
            </tr>
        </table>
    </div>
</div>
<div id="data" class="anki-field">{}</div>
<div id="sentence-raw" class="anki-field">{{morphHighlight:Expression}}</div>
<div id="sentence-display" class="anki-field">{{furigana:Reading}}</div>
<!-- When you use a compiled UberSentences deck and not a Yomichan deck,
then do this to enable audio: https://github.com/jihadichan/jp/blob/master/ubersentences/docs/audio.md -->
<div class="anki-field">{{Audio}}</div>
<div id="source" class="anki-field">{{Snapshot}}</div>
<script src="ubersentences/statics/mnemonics.js" type="application/javascript"></script>
<script src="ubersentences/statics/jquery-3.4.1.min.js" type="application/javascript"></script>
<script src="ubersentences/statics/jquery.modal.min.js" type="application/javascript" charset="UTF-8"></script>
<script src="ubersentences/statics/backpage.js" type="application/javascript" charset="UTF-8"></script>
<div id="modal" class="modal"></div>
<!--------------------------------------------------------------------------->
<!-- TO HERE -->
<!--------------------------------------------------------------------------->
</body>
</html>
```



# AniMelon Search

- Scroll till bottom
- Execute in JS console
- Replace array in `series.js`

```javascript
var series = []
var container = $('#trend > div > div.col-md-8.col-xs-12.right-gallery > div:nth-child(3) > div.row.ng-show-toggle-slidedown.series-content-container')
container.find('a').each(function( index ) {
	var tags = []
	$(this).find('.label').each(function() {
		tags.push($(this).text().trim())
	})	

	  var item = {
	  	name: $(this).find('.anime-name').text().trim(),
	  	image: $(this).find('.image-fit').attr('src').trim(),
	  	tags: tags,
	  	desc: $(this).find("[ng-bind='series.descriptions.en']").text().trim(),
	  	release: $(this).find('.description-title').first().text().trim(),
	  	link: $(this).attr('href')
	  }	
	  series.push(item)
});
console.log(series)
```



# Manga

Best:

- https://rawkuma.com/ (raw manga, but the site is in English)
- https://sakuramanga.org/ (raw manga, but the site is in English)

Looks like shit, but they have the good stuff

- [https://manga1001.top/?s=%E9%80%B2%E6%92%83%E3%81%AE%E5%B7%A8%E4%BA%BA](https://manga1001.top/?s=進撃の巨人)
- https://comick.top/made-in-abyss-060/
- https://mangapro.top/
- https://syosetu.top/

Shit tier:

- https://rawdevart.com/ (English, actually good, but the inventory seems to be lacking)
- https://delivery.senmanga.com/ (pop-ups, lots of them)



# Anime

- [Anime summaries](https://www.youtube.com/watch?v=rJk_mFg4jjQ)
- [Anime movies](https://9gag.com/gag/a31rvnr)
