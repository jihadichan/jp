# Hotkeys

## Notes

- Drag the bookmarklet from [this page](https://jihadichan.github.io/bookmarklet.html) into your browser toolbar.

- There are no IDs on the elements. If the script doesn't work then it's most likely due to some HTML changes. Copy the CSS selectors from the dev tools, replace in code, [minify](https://www.minifier.org/) and replace the HERE:

  ```javascript
  javascript:(function(){HERE})()
  ```



## Animelon

```javascript
    var checkBoxEnglish = document.querySelector('#control-buttons-container > div.pull-right.bottom-right > div:nth-child(3) > div > div > ul > li:nth-child(5) > input');
    var checkBoxJapanese = document.querySelector('#control-buttons-container > div.pull-right.bottom-right > div:nth-child(3) > div > div > ul > li:nth-child(3) > input');
    var checkBoxHirgana = document.querySelector('#control-buttons-container > div.pull-right.bottom-right > div:nth-child(3) > div > div > ul > li:nth-child(1) > input');
    var titleSelector = '#video-page > div:nth-child(2) > div.col-md-8.col-sm-6 > div.video-description.row > div:nth-child(1) > div.col-sm-8 > h3';
    var timestampSelector = '#videoPlayerCurrentTimeValue';
    var playerSelector = '#video-player-container';

    var english = 'English hotkey (E): ' + (checkBoxEnglish.nextSibling.innerText.toLowerCase().indexOf('english') !== -1 ? 'Should work' : 'Couldn\'t find element or adjacent text is not \'english\'.');
    var japanese = 'Japanese hotkey (W): ' + (checkBoxJapanese.nextSibling.innerText.toLowerCase().indexOf('japanese') !== -1 ? 'Should work' : 'Couldn\'t find element or adjacent text is not \'japanese\'.');
    var hiragana = 'Hiragana hotkey (Q): ' + (checkBoxHirgana.nextSibling.innerText.toLowerCase().indexOf('hiragana') !== -1 ? 'Should work' : 'Couldn\'t find element or adjacent text is not \'hiragana\'.');
    var copyHotKey = 'Copy hotkey (A)';
    var focusPlayer = 'Focus player (D)';

    alert('Hotkeys:\n' + english + '\n' + japanese + '\n' + hiragana + '\n' + copyHotKey + '\n' + focusPlayer);

    function copyText(text) {
        var textArea = document.createElement('textarea');
        textArea.value = text;

        // Avoid scrolling to bottom
        textArea.style.top = '0';
        textArea.style.left = '0';
        textArea.style.position = 'fixed';

        document.body.appendChild(textArea);
        textArea.focus();
        textArea.select();

        try {
            var successful = document.execCommand('copy');
            var msg = successful ? 'successful' : 'unsuccessful';
            console.log('Copying text command was ' + msg);
        } catch (err) {
            console.error('Oops, unable to copy', err);
        }

        document.body.removeChild(textArea);
    }

    document.onkeyup = function (e) {
        if (e.code === 'KeyE') {
            checkBoxEnglish.click();
        }
        if (e.code === 'KeyW') {
            checkBoxJapanese.click();
        }
        if (e.code === 'KeyQ') {
            checkBoxHirgana.click();
        }
        if (e.code === 'KeyD') {
            $(playerSelector).first().focus();
        }
        if (e.code === 'KeyA') {
            var obj = {
                japanese: $('.japanese.subtitle').first().text(),
                hiragana: $('.hiragana.subtitle').first().text(),
                english: $('.english.subtitle').first().text(),
                source: $(titleSelector).first().text() + ' - ' + $(timestampSelector).first().text()
            };
            copyText(JSON.stringify(obj))
        }
    };
```



## Netflix

```javascript
var english = $('#showHT');
var hiragana = $('#transliterationsJa');
var isHiraganaActive = hiragana.val() !== "ORIG";
var kanji = $('#hideSubs');
var isKanjiActive = hiragana.val() !== "OFF";

var info = "" +
    "English (J): " + (english.length === 0 ? "Couldn't find element\n" : "Should work.\n") +
    "Hiragana (K): " + (hiragana.length === 0 ? "Couldn't find element\n" : "Should work.\n") +
    "Kanji (L): " + (kanji.length === 0 ? "Couldn't find element\n" : "Should work.\n");
alert(info);

document.onkeyup = function(e) {
    if (e.code === "KeyJ") {
        english.trigger("click"); // Show english
    }
    if (e.code === "KeyK") {
        if(isHiraganaActive) {
            hiragana.val("ORIG").trigger("change");
            isHiraganaActive = false;
        } else {
            hiragana.val("ORIG+HIRAGANA").trigger("change");
            isHiraganaActive = true;
        }
    }
    if (e.code === "KeyL") {
        if(isHiraganaActive) {
            kanji.val("OFF").trigger("change");
            isHiraganaActive = false;
        } else {
            kanji.val("HB").trigger("change");
            isHiraganaActive = true;
        }
    }
}
```

