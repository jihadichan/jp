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
var copyHotKey = 'Copy hotkey (C)';
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
    if (e.code === 'KeyC') {
        var obj = {
            japanese: removeFirstBrackets(concatSubtitle('.japanese.subtitle')),
            hiragana: concatSubtitle('.hiragana.subtitle'),
            english: concatSubtitle('.english.subtitle'),
            source: $(titleSelector).first().text() + ' - ' + $(timestampSelector).first().text()
        };
        copyText(JSON.stringify(obj))
    }
};
