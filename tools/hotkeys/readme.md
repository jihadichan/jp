# Hotkeys

## Notes

- Drag the bookmarklet from [this page](https://jihadichan.github.io/bookmarklet.html) into your browser toolbar.

- There are no IDs on the elements. If the script doesn't work then it's most likely due to some HTML changes. Copy the CSS selectors from the dev tools, replace in code, [minify](https://www.minifier.org/) ([alternative](https://javascript-minifier.com/)) and replace the HERE:

  ```javascript
  javascript:(function(){HERE})()
  ```



## Animelon

```javascript
var checkBoxEnglish = document.querySelector('#control-buttons-container > div.pull-right.bottom-right > div:nth-child(3) > div > div > ul > li:nth-child(5) > input');
var checkBoxJapanese = document.querySelector('#control-buttons-container > div.pull-right.bottom-right > div:nth-child(3) > div > div > ul > li:nth-child(3) > input');
var checkBoxHirgana = document.querySelector('#control-buttons-container > div.pull-right.bottom-right > div:nth-child(3) > div > div > ul > li:nth-child(1) > input');
var backward = document.querySelector('#control-buttons-container > div.pull-center.bottom-center > button:nth-child(4)');
var forward = document.querySelector('#control-buttons-container > div.pull-center.bottom-center > button:nth-child(5)');
var titleSelector = '#video-page > div:nth-child(2) > div.col-md-8.col-sm-6 > div.video-description.row > div:nth-child(1) > div.col-sm-8 > h3';
var timestampSelector = '#videoPlayerCurrentTimeValue';
var playerSelector = '#video-player-container';

var english = 'English hotkey (E): ' + (checkBoxEnglish.nextSibling.innerText.toLowerCase().indexOf('english') !== -1 ? 'Should work' : 'Couldn\'t find element or adjacent text is not \'english\'.');
var japanese = 'Japanese hotkey (W): ' + (checkBoxJapanese.nextSibling.innerText.toLowerCase().indexOf('japanese') !== -1 ? 'Should work' : 'Couldn\'t find element or adjacent text is not \'japanese\'.');
var hiragana = 'Hiragana hotkey (Q): ' + (checkBoxHirgana.nextSibling.innerText.toLowerCase().indexOf('hiragana') !== -1 ? 'Should work' : 'Couldn\'t find element or adjacent text is not \'hiragana\'.');
var copyHotKey = 'Copy as JSON (J)';
var focusPlayer = 'Focus player (F)';
var forwardInfo = 'Forward (D)';
var backwardInfo = 'Backward (A)';

alert('Hotkeys:\n' + english + '\n' + japanese + '\n' + hiragana + '\n' + copyHotKey + '\n' + focusPlayer + "\n" + forwardInfo + "\n" + backwardInfo);

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

function concatSubtitle(cssSelector) {
  var text = '';
  $(cssSelector).each(function (index, elem) {
    text += elem.innerHTML.replace(/\u200C/g, '');
  });
  return text.trim();
}

function removeFirstBrackets(sentence) {
  return sentence.replace(/（.*?）/, '');
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
  if (e.code === 'KeyF') {
    $(playerSelector).first().focus();
  }
  if (e.code === 'KeyD') {
    forward.click();
  }
  if (e.code === 'KeyA') {
    backward.click();
  }
  if (e.code === 'KeyJ') {
    var obj = {
      japanese: removeFirstBrackets(concatSubtitle('.japanese.subtitle')),
      hiragana: concatSubtitle('.hiragana.subtitle'),
      english: concatSubtitle('.english.subtitle'),
      source: $(titleSelector).first().text() + ' - ' + $(timestampSelector).first().text()
    };
    copyText(JSON.stringify(obj))
  }
};
```

Minified

```
javascript:(function(){var checkBoxEnglish=document.querySelector("#control-buttons-container > div.pull-right.bottom-right > div:nth-child(3) > div > div > ul > li:nth-child(5) > input"),checkBoxJapanese=document.querySelector("#control-buttons-container > div.pull-right.bottom-right > div:nth-child(3) > div > div > ul > li:nth-child(3) > input"),checkBoxHirgana=document.querySelector("#control-buttons-container > div.pull-right.bottom-right > div:nth-child(3) > div > div > ul > li:nth-child(1) > input"),backward=document.querySelector("#control-buttons-container > div.pull-center.bottom-center > button:nth-child(4)"),forward=document.querySelector("#control-buttons-container > div.pull-center.bottom-center > button:nth-child(5)"),titleSelector="#video-page > div:nth-child(2) > div.col-md-8.col-sm-6 > div.video-description.row > div:nth-child(1) > div.col-sm-8 > h3",timestampSelector="#videoPlayerCurrentTimeValue",playerSelector="#video-player-container",english="English hotkey (E): "+(-1!==checkBoxEnglish.nextSibling.innerText.toLowerCase().indexOf("english")?"Should work":"Couldn't find element or adjacent text is not 'english'."),japanese="Japanese hotkey (W): "+(-1!==checkBoxJapanese.nextSibling.innerText.toLowerCase().indexOf("japanese")?"Should work":"Couldn't find element or adjacent text is not 'japanese'."),hiragana="Hiragana hotkey (Q): "+(-1!==checkBoxHirgana.nextSibling.innerText.toLowerCase().indexOf("hiragana")?"Should work":"Couldn't find element or adjacent text is not 'hiragana'."),copyHotKey="Copy hotkey (C)",focusPlayer="Focus player (F)",forwardInfo="Forward (D)",backwardInfo="Backward (A)";function copyText(e){var t=document.createElement("textarea");t.value=e,t.style.top="0",t.style.left="0",t.style.position="fixed",document.body.appendChild(t),t.focus(),t.select();try{var o=document.execCommand("copy")?"successful":"unsuccessful";console.log("Copying text command was "+o)}catch(e){console.error("Oops, unable to copy",e)}document.body.removeChild(t)}function concatSubtitle(e){var t="";return $(e).each(function(e,o){t+=o.innerHTML.replace(/\u200C/g,"")}),t.trim()}function removeFirstBrackets(e){return e.replace(/（.*?）/,"")}alert("Hotkeys:\n"+english+"\n"+japanese+"\n"+hiragana+"\n"+copyHotKey+"\n"+focusPlayer+"\n"+forwardInfo+"\n"+backwardInfo),document.onkeyup=function(e){if("KeyE"===e.code&&checkBoxEnglish.click(),"KeyW"===e.code&&checkBoxJapanese.click(),"KeyQ"===e.code&&checkBoxHirgana.click(),"KeyF"===e.code&&$(playerSelector).first().focus(),"KeyD"===e.code&&forward.click(),"KeyA"===e.code&&backward.click(),"KeyC"===e.code){var t={japanese:removeFirstBrackets(concatSubtitle(".japanese.subtitle")),hiragana:concatSubtitle(".hiragana.subtitle"),english:concatSubtitle(".english.subtitle"),source:$(titleSelector).first().text()+" - "+$(timestampSelector).first().text()};copyText(JSON.stringify(t))}};})()
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



## Toggle Furigana

Works only with \<rt\> elements. If no \<rt\> available it tries to find tags with class 'furigana' (works on Jisho).

```javascript
function toggleRtElements() {
	if(isFuriganaOn) {
	for(var rt of rts) rt.style.display = 'none';	
		isFuriganaOn = false;
	} else {
		for(var rt of rts) rt.style.display = null;	
		isFuriganaOn = true;
	}
}

document.onkeyup = function (e) {
    if (e.ctrlKey && e.code === 'KeyB') {
        toggleRtElements();
    }
}

if(isFuriganaOn === undefined) {
	var isFuriganaOn = true;
}

var rts = document.getElementsByTagName('rt');
if(rts.length == 0) {
	console.log('DOCUMENT DOES NOT CONTAIN <RT> ELEMENTS!');
	rts = document.getElementsByClassName('furigana');
	if(rts.length == 0) {
		console.log('DOCUMENT ALSO DOES ELEMENTS WITH class=furigana!');
	}
}
toggleRtElements();
```

