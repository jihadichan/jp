var data;
try {
    data = JSON.parse(decodeURIComponent($('#data').html()));
} catch (e) {
    data = {};
    $('#debug').text("JSON parse error. " + e.message);
}
var highlightToggle = true;
var CORS_PROXY = "https://jp-learner.herokuapp.com/";
var currentVocabMarkupObj = {};
var lastCalledJishoUrl = "";
var lastReceivedJishoResponse = "";
var lastCalledGoogleTranslateUrl = "";
var lastReceivedGoogleTranslateResponse = "";

/*********************************************************************************/
// RENDERED SECTION
/*********************************************************************************/

function removeOverlay() {
    document.getElementById("overlay").style.display = "none";
}

function addShowSolutionClickEvent() {
    removeOverlay(); // todo remove this line if you want to enable the overlay.
    $('#solution').on("click", function () {
        removeOverlay();
    });
}

function addToggleFuriganaClickEvent() {
    $('#sentence').on("click", function () {
        toggleFurigana();
    });
}

function renderAnalysisTable(shouldHighLight) {
    var table = "<div class='analysis-table'><table>";
    table += "<tr>" +
        "<th class='part-column'>Part</th>" +
        "<th class='details-column'>Details</th>" +
        "<th class='other-column'>Other</th></tr>";

    $(data).each(function (index, analysisElement) {
        if (analysisElement.partsOfSpeech && $.inArray("記号", analysisElement.partsOfSpeech) === -1) {
            var word = analysisElement.baseForm ? analysisElement.baseForm : analysisElement.surface;
            var surface = wrapWithHighlight(shouldHighLight, analysisElement, analysisElement.surface);
            var details = "<div class='analysis-inner-table'><table>";

            var partsOfSpeech = "";
            if (analysisElement.partsOfSpeech) {
                $(analysisElement.partsOfSpeech).each(function (index, value) {
                    partsOfSpeech += "<span onclick='renderJishoWithWord(\"" + value + "\")'>" + value + "</span>、"
                });
            }
            partsOfSpeech = partsOfSpeech.replace(/、$/, "");

            var conjugationForm = "";
            if (analysisElement.conjugationForm) {
                conjugationForm += "<span onclick='renderJishoWithWord(\"" + analysisElement.conjugationForm + "\")'>" + analysisElement.conjugationForm + "</span>"
            }

            var conjugationType = "";
            if (analysisElement.conjugationType) {
                conjugationType += "<span onclick='renderJishoWithWord(\"" + analysisElement.conjugationType + "\")'>" + analysisElement.conjugationType + "</span>"
            }


            details += analysisElement.pronunciation ? "<tr class='detail-highlight'><td class='detail-cell'>Pronun.:</td><td>" + analysisElement.pronunciation + "</td></tr>" : "";
            details += analysisElement.baseForm ? "<tr class='detail-highlight'><td class='detail-cell'>Base Form:</td><td>" + analysisElement.baseForm + "</td></tr>" : "";
            details += analysisElement.reading ? "<tr><td class='detail-cell'>Reading:</td><td>" + analysisElement.reading + "</td></tr>" : "";
            details += analysisElement.partsOfSpeech ? "<tr><td class='detail-cell'>PoS:</td><td>" + partsOfSpeech + "</td></tr>" : "";
            details += analysisElement.conjugationForm ? "<tr><td class='detail-cell'>Conj. Form:</td><td>" + conjugationForm + "</td></tr>" : "";
            details += analysisElement.conjugationType ? "<tr><td class='detail-cell'>Conj. Type:</td><td>" + conjugationType + "</td></tr>" : "";
            details += "</table></div>";

            var other = "<div class='analysis-inner-table'><table>";
            other += analysisElement.freqNF !== "99999" ? "<tr><td class='other-cell'>Freq. NF:</td><td>" + analysisElement.freqNF + "</td></tr>" : "";
            other += analysisElement.freqWK !== "99999" ? "<tr><td class='other-cell'>Freq. WK:</td><td>" + analysisElement.freqWK + "</td></tr>" : "";
            other += "<tr><td colspan='2' class='other-cell add-vocab'>" +
                "<a href=\"#modal\" rel=\"modal:open\" onclick='fetchWordFromJishoApi(\"" + word.trim() + "\")'>Add</a>" +
                "</td></tr>";
            other += "</table></div>";

            table += "<tr>" +
                "<td class='part-cell' onclick='renderJishoWithWord(\"" + word + "\")'>" + surface + "</td>" +
                "<td class='analysis-cell'>" + details + "</td>" +
                "<td class='analysis-cell'>" + other + "</td>" +
                "</tr>";
        }
    });

    table += "</table></div>";
    $('#rendered-content').html(table);
}

function renderJishoWithSentence() {
    var sentence = $('#sentence-raw').html();
    $('#rendered-content').html('<iframe class=\"jisho\" src=\"https://jisho.org/search/' + sentence + '\"></iframe>');
}

function renderJishoWithWord(word) {
    $('#rendered-content').html('<iframe class=\"jisho\" src=\"https://jisho.org/search/' + word + '\"></iframe>');
}

/*********************************************************************************/
// SENTENCE
/*********************************************************************************/

function renderNotes() {
    var jQuerySelection = $('#notes');
    renderNoteLists(jQuerySelection);
    renderNoteMarkers(jQuerySelection);
}

function renderNoteLists(jQuerySelection) {
    var copy = jQuerySelection.html();

    // Clean and split to lines
    copy = copy.replace(/\n/g, "");
    var lines = copy.split(/<br ?\/?>/);
    for (var y = 0; y < lines.length; y++) {
        lines[y] = lines[y].trim();
    }

    // Extract vocab
    var vocabLines = [];
    var textLines = [];
    for (var z = 0; z < lines.length; z++) {
        if (lines[z].match(/^#(.*):.*/)) {
            vocabLines.push(lines[z]);
        } else {
            textLines.push(lines[z]);
        }
    }

    // Replace lists
    var html = "";
    for (var i = 0; i < textLines.length; i++) {
        if (textLines[i].indexOf('-') === 0) {
            var x = i;
            var list = "<ul>";
            while (textLines[x] && textLines[x].indexOf('-') === 0) {
                list += "<li>" + textLines[x].replace(/^- ?/, "") + "</li>";
                x++;
            }
            list += "</ul>";
            i = x - 1;
            html += list;
        } else {
            html += textLines[i] + "<br>";
        }
    }

    // Create vocab table
    var table = createVocabTableHtml(vocabLines);
    html += table;

    jQuerySelection.html(html)
}

function createVocabTableHtml(vocabLines) {
    var table = "";
    if (vocabLines.length > 0) {
        table += "<div class='vocab'>Vocab</div><table class='vocab-table'>";

        $(vocabLines).each(function (index, value) {
            if (value !== "") {
                var split = value.split(":");
                var word = split[0].replace(/^# */, "").trim();
                var translation = split.length === 2 ? split[1].trim() : split.slice(1, split.length).join(":");

                if (word.match(/.*\[.*?]/)) {
                    var wordSplit = word.split("\[");
                    var rt = wordSplit.slice(1, wordSplit.length).join("").replace("]", "");
                    word = "<ruby>" + wordSplit[0] + "<rt>" + rt + "</rt></ruby>";
                }
                var wordCell = word === "" ? "style='border:0 !important;'" : "";
                table += "" +
                    "<tr>" +
                    "   <td class='vocab-word' " + wordCell + " nowrap>" + word + "</td>" +
                    "   <td class='vocab-translation'>" + translation + "</td>" +
                    "</tr>";
            }
        });

        table += "</table>";
    }
    return table;
}

function renderNoteMarkers(jQuerySelection) {
    var html = jQuerySelection.html();
    if (html.indexOf("!inf") > -1) {
        html = html.replace(/!inf/, "");
        html += "<div class='informal'>INFORMAL</div>"
    }
    if (html.indexOf("!frm") > -1) {
        html = html.replace(/!frm/, "");
        html += "<div class='formal'>FORMAL</div>"
    }
    if (html.indexOf("!ani") > -1) {
        html = html.replace(/!ani/, "");
        html += "<div class='anime'>ANIME</div>"
    }
    jQuerySelection.html(html);
}

function renderSourceCell() {
    var raw = $('#source').html();
    var sourceCell = $('#source-cell');
    var source = raw;
    if (sourceCell.html() === "") {
        var regex = /\bhttp.*\b/;
        var match = regex.exec(source);
        var contentForSourceCell = "";
        while (match != null) {
            source = source.replace(regex, "");
            if (match[0].indexOf("imgur.com") !== -1) {
                contentForSourceCell += raw.replace(regex, "<img alt='' src='" + match[0] + "' /><br>");
            }

            contentForSourceCell += raw.replace(regex, "<a target='_blank' href='" + match[0] + "'>" + match[0] + "</a><br>");
            match = regex.exec(source);
        }

        sourceCell.html("<td>" + contentForSourceCell + "</td>");
    } else {
        sourceCell.html("");
    }
}

function renderSentence(shouldHighlight) {
    var sentence = "";
    $.each(data, function (index, analysisElement) {
        if (analysisElement.reading) {
            sentence += wrapWithHighlight(shouldHighlight, analysisElement, "<ruby>" + analysisElement.surface + "<rt>" + analysisElement.reading + "</rt></ruby>");
        } else {
            sentence += wrapWithHighlight(shouldHighlight, analysisElement, analysisElement.surface);
        }
    });

    $('#sentence').html(sentence);
}

function wrapWithHighlight(shouldHighlight, analysisElement, sentencePart) {
    if (!shouldHighlight || !analysisElement.partsOfSpeech || !analysisElement.partsOfSpeech[0]) {
        return sentencePart;
    }

    // Base part of speech
    var pos = analysisElement.partsOfSpeech[0];

    // Case based overwrite ... yeah...
    if (analysisElement.partsOfSpeech[1]) {
        switch (analysisElement.partsOfSpeech[1]) {
            case "代名詞":
                pos = analysisElement.partsOfSpeech[1];
                break;
            default:
                break;
        }
    }

    switch (pos) {

        case "名詞":
        case "固有名詞":
        case "姓":
        case "人名":
            return wrapWithSpan(sentencePart, "hl-blue");

        case "代名詞":
        case "連体詞":
            return wrapWithSpan(sentencePart, "hl-purple");

        case "助動詞":
        case "動詞":
            return wrapWithSpan(sentencePart, "hl-green");

        case "副詞可能":
        case "副詞":
            return wrapWithSpan(sentencePart, "hl-green-light");

        case "助詞":
        case "係助詞":
        case "副助詞":
        case "副助詞／並立助詞／終助詞":
        case "格助詞":
        case "終助詞":
        case "助詞類接続":
            return wrapWithSpan(sentencePart, "hl-red");

        case "接尾":
        case "接頭辞":
        case "助数詞":
            return wrapWithSpan(sentencePart, "hl-orange");

        case "接続詞":
        case "感動詞":
            return wrapWithSpan(sentencePart, "hl-grey");

        case "形容動詞語幹":
        case "形容詞":
        case "連体化":
            return wrapWithSpan(sentencePart, "hl-yellow");

        default:
            break;
    }

    return sentencePart;
}

function wrapWithSpan(sentencePart, cssClass) {
    return "<span class='" + cssClass + "'>" + sentencePart + "</span>";
}

function renderHighlights() {
    renderSentence(highlightToggle);
    renderAnalysisTable(highlightToggle);
    highlightToggle = !highlightToggle;
}


/*********************************************************************************/
// OPTIONS
/*********************************************************************************/

function renderOptions() {
    var jQuerySelection = $('#options');
    renderReplayButton(jQuerySelection);
    renderHighLightButton(jQuerySelection);
    renderJishoButton(jQuerySelection);
    renderAnalysisButton(jQuerySelection);
    renderTranslateButton(jQuerySelection);
    renderVocabButton(jQuerySelection);
    renderGrammarButton(jQuerySelection);
    renderMailToButton(jQuerySelection);
    renderSourceButton(jQuerySelection);
    renderGoogleTranslateButton(jQuerySelection);
}

function renderVocabButton(jQuerySelection) {
    jQuerySelection.html(jQuerySelection.html() + "" +
        "<button class='options-button' onclick='renderJishoVocabLookup()'>" +
        "   <a href='#modal' rel='modal:open'>Vocab</a>" +
        "</button>");
}

function renderTranslateButton(jQuerySelection) {
    jQuerySelection.html(jQuerySelection.html() + "" +
        "<button class='options-button' onclick='renderTranslateLookup()'>" +
        "   <a href='#modal' rel='modal:open'>Translate</a>" +
        "</button>");
}

function renderGrammarButton(jQuerySelection) {
    jQuerySelection.html(jQuerySelection.html() + "" +
        "<button class='options-button' onclick='renderGrammarLookup()'>" +
        "   <a href='#modal' rel='modal:open'>Grammar</a>" +
        "</button>");
}

function renderSourceButton(jQuerySelection) {
    jQuerySelection.html(jQuerySelection.html() + "<button class='options-button' onclick='renderSourceCell()'>Source</button>");
}

function renderMailToButton(jQuerySelection) {
    var sentenceWithReading = "";
    $.each(data, function (index, analysisElement) {
        var word = analysisElement.baseForm ? analysisElement.baseForm : analysisElement.surface;
        var reading = analysisElement.reading ? " - (" + analysisElement.reading + ")" : "";
        sentenceWithReading += "!new [" + word + "]" + reading + "<br>";
    });
    // var source = $('#source').html().replace(/&/g, "%26").replace(/\?/g, "%3F");
    // sentenceWithReading += "<br>" + source;
    var mailTo = "<a href=\"mailto:?subject=" + $('#sentence-raw').html() + "&body=" + sentenceWithReading + "\">";
    jQuerySelection.html(jQuerySelection.html() + mailTo + "<button class='options-button'>Mail</button></a>");
}

function renderGoogleTranslateButton(jQuerySelection) {
    var href = "https://translate.google.de/#view=home&op=translate&sl=ja&tl=en&text=" + $('#sentence-raw').html();
    jQuerySelection.html(jQuerySelection.html() + "<a href='" + href + "'><button class='options-button'>Goog Trans</button></a>");
}

function renderJishoButton(jQuerySelection) {
    jQuerySelection.html(jQuerySelection.html() + "<button class='options-button' onclick='renderJishoWithSentence()'>Jisho</button>");
}

function renderHighLightButton(jQuerySelection) {
    jQuerySelection.html(jQuerySelection.html() + "<button class='options-button' onclick='renderHighlights()'>Highlight</button>");
}

function toggleFurigana() {
    $('#sentence rt').toggle();
}

function renderAnalysisButton(jQuerySelection) {
    jQuerySelection.html(jQuerySelection.html() + "<button class='options-button' onclick='renderAnalysisTable()'>Analysis</button>");
}

function renderReplayButton(jQuerySelection) {
    jQuerySelection.html(jQuerySelection.html() + "<button class='options-button' onclick='replaySentence()'>Replay</button>");
}

function replaySentence() {
    var filePath = "ubersentences/mp3/" + $('#mp3').text();
    try {
        var audio = new Audio(filePath);
        audio.play();
    } catch (err) {
        $('#debug').text("Couldn't play " + filePath + ". Either file does not exist or playback failed.");
    }
}


/*********************************************************************************/
// JISHO SCRAPER
/*********************************************************************************/

function createJishoApiUrl(word) {
    return CORS_PROXY + "https://jisho.org/api/v1/search/words?keyword=" + word;
}

function fetchWordFromJishoApi(word) {
    var modal = $('#modal');

    var url = createJishoApiUrl(word);
    if (url === lastCalledJishoUrl) {
        renderTranslationTable(modal, lastReceivedJishoResponse);
        return;
    }
    lastCalledJishoUrl = url;

    jQuery.ajax({
        type: "GET",
        url: url,
        beforeSend: function () {
            modal.html("<div class='loading'>Loading</div>");
        },
        success: function (body) {
            lastReceivedJishoResponse = body;
            renderTranslationTable(modal, body);
        },
        error: function (jqXHR, textStatus, errorThrown) {
            // todo show in modal
            $('#debug').text("Couldn't fetch response from Jisho API. Probably cross origin proxy failed or API is down.")
        }
    });
}

function getMarkupTextAreaHtml() {
    return "" +
        "<div class='vocab-output'>" +
        "   <div id='vocab-preview'></div>" +
        "   <label><textarea id='vocab-markup'></textarea><button style='float: right' onclick='resetMarkupObj()'>Reset</button></label>" +
        "</div>"
}

function renderTranslationTable(modal, body) {
    var data = body.data;
    var html = "";

    // OUTPUT
    html += getMarkupTextAreaHtml();

    // WORDS
    html += "<div class='translation-table'><table>";
    $(data).each(function (index, entity) {

        var word = extractWord(entity);
        var rt = word.reading ? "<rt>" + word.reading + "</rt>" : "";
        html += "" +
            "<tr class='translation-row'>" +
            "   <td class='translation-writing'>" +
            "       <ruby>" + word.writing + rt + "</ruby>" +
            "   </td>" +
            "   <td class='translation-english'><ul>";

        $(entity.senses).each(function (index2, sense) {
            var entry = {
                word: word,
                allForms: extractOtherForms(entity.japanese),
                sense: sense.english_definitions.join("; ") + createTagsAndInfo(sense)
            };
            html += "<li class='translation-sense' onclick='addToVocabMarkupTextarea(\"" + escape(encodeURIComponent(JSON.stringify(entry))) + "\")'>" + sense.english_definitions.join("; ") + "</li>";
        });
        html += "" +
            "   </ul></td>" +
            "</tr>";
    });
    html += "</table></div>";
    modal.html(html);

    updateVocabMarkUp();
}

function createTagsAndInfo(sense) {
    var info = "";
    if (!sense.tags && !sense.info) {
        return "";
    }
    if (sense.tags.length > 0 || sense.info.length > 0 || sense.parts_of_speech.length) {
        info += "<div class=\"info\">(";
        var infoArray = [];
        if (sense.parts_of_speech.length > 0) infoArray.push(sense.parts_of_speech.filter(function (val) {
            return val;
        }).join("; "));
        if (sense.info.length > 0) infoArray.push(sense.info.filter(function (val) {
            return val;
        }).join("; "));
        if (sense.tags.length > 0) infoArray.push(sense.tags.filter(function (val) {
            return val;
        }).join("; "));
        info += infoArray.filter(function (val) {
            return val;
        }).join("; ");
        info += ")</div>";
    }
    info = info.replace("Usually written using kana alone", "usu. written as kana");

    return info;
}

function resetMarkupObj() {
    currentVocabMarkupObj = {};
    updateVocabMarkUp();
}

function updateVocabMarkUp() {
    var markup = createMarkupText();
    $('#vocab-markup').val(markup);
    $('#vocab-preview').html(createVocabTableHtml(markup.split("\n")));
}

function addToVocabMarkupTextarea(entry) {
    entry = JSON.parse(decodeURIComponent(unescape(entry)));

    // Create new word
    if (!currentVocabMarkupObj[entry.word.writing]) {
        // Main word
        var word = {};
        word.word = [entry.word];

        // Other Forms
        var otherForms = addOtherForms(entry.allForms, entry.word);
        if (otherForms.length > 0) {
            word.word = word.word.concat(otherForms);
        }

        // Senses
        word.senses = [];
        word.senses.push(entry.sense);
        currentVocabMarkupObj[entry.word.writing] = word;
    } else { // Add to existing word
        var isAlreadyInSenses = false;
        $(currentVocabMarkupObj[entry.word.writing].senses).each(function (index, existingSense) {
            if (existingSense === entry.sense) {
                isAlreadyInSenses = true;
            }
        });
        if (!isAlreadyInSenses) {
            currentVocabMarkupObj[entry.word.writing].senses.push(entry.sense);
        }
    }

    updateVocabMarkUp();
}

function addOtherForms(allForms, existingForm) {
    var otherForms = [];
    $.each(allForms, function (index, word) {
        if (word.writing !== existingForm.writing) {
            otherForms.push(word);
        }
    });
    return otherForms;
}

function extractOtherForms(otherFormsRaw) {
    var otherForms = [];
    $.each(otherFormsRaw, function (index, entity) {
        var word = {};
        if (entity.word) {
            word.writing = entity.word;
        }
        if (entity.reading) {
            if (word.writing) {
                word.reading = entity.reading;
            } else {
                word.writing = entity.reading;
            }
        }
        otherForms.push(word);
    });

    return otherForms;
}

function createMarkupText() {
    var markup = "";
    $.each(currentVocabMarkupObj, function (key, row) {
        var writing = row.word[0].writing;
        var reading = row.word[0].reading ? "<rt>" + row.word[0].reading + "</rt>" : "";
        var otherForms = "";
        if (row.word.length > 1) {
            otherForms += "<li class='np'><span class='ofrms'>Other Forms　";
            for (var x = 1; x < row.word.length; x++) {
                var oFrmWriting = row.word[x].writing;
                var oFrmReading = row.word[x].reading ? "<rt>" + row.word[x].reading + "</rt>" : "";

                otherForms += "<ruby>" + oFrmWriting + oFrmReading + "</ruby>、";
            }
            otherForms = otherForms.replace(/、 ?$/, "");
            otherForms += "</span></li>";
        }
        markup += "#<ruby>" + writing + reading + "</ruby>:<ul><li>" + row.senses[0] + "</li>";

        if (row.senses.length > 1) {
            for (var i = 1; i < row.senses.length; i++) {
                markup += "<li>" + row.senses[i] + "</li>";
            }
        }
        if (otherForms !== "") {
            markup += otherForms;
        }
        markup += "</ul>\n";
    });
    return markup.replace(/\n$/, "");
}

function extractWord(entity) {
    var word = {};
    if (entity.japanese[0].word) {
        word.writing = entity.japanese[0].word;
    }
    if (entity.japanese[0].reading) {
        if (word.writing) {
            word.reading = entity.japanese[0].reading;
        } else {
            word.writing = entity.japanese[0].reading;
        }
    }
    return word;
}

function getSelectedText() {
    var selectedText = "";
    if (window.getSelection) {
        selectedText = window.getSelection().toString();
    } else if (document.selection && document.selection.type !== "Control") {
        selectedText = document.selection.createRange().text;
    }
    return selectedText;
}

function renderTranslateLookup() {
    var selectedText = getSelectedText();
    var isFullSentence = false;
    if (selectedText === "") {
        selectedText = $('#sentence-raw').html();
        isFullSentence = true;
    }

    var form = "" +
        "<div class=\"search-inputs\">" +
        "<label>" +
        "   <span class=\"hint\">Looks up words via Google Translate</span>" +
        "   <input type=\"text\" id=\"search-field\" placeholder=\" Search...\" value=\"" + selectedText + "\"/>" +
        "   <input type=\"button\" id='search-button' onclick=\"fetchTranslate(" + isFullSentence + ")\" value=\"Search\">" +
        "</label>" +
        "<div id='translate-result'></div>" +
        "</div>";

    $('#modal').html(form);
    $('#search-field').keypress(function (e) {
        if (e.keyCode === 13) {
            fetchTranslate(isFullSentence);
        }
    });
}

function fetchTranslate(isFullSentence) {
    var query = $('#search-field').val().trim();
    if (query !== "") {
        fetchWordFromGoogleTranslate(query, isFullSentence);
    }
}

function createGoogleTranslateUrl(query) {
    return CORS_PROXY + "https://translate.googleapis.com/translate_a/single?client=gtx&sl=ja&tl=en&hl=en-US&dt=t&dt=bd&dj=1&source=icon&tk=0.0&q=" + encodeURIComponent(query);
}

function renderGoogleTranslateResult(container, response, isFullSentence) {
    var orig = "";
    var trans = "";
    $(response.sentences).each(function (index, value) {
        orig += value.orig;
        trans += value.trans;
    });
    if (isFullSentence) {
        trans = "- GT: " + trans;
    } else {
        trans = "- GT: " + orig + " = " + trans;
    }
    container.html("<textarea id='translate-textarea'>" + trans + "</textarea>");
}

function fetchWordFromGoogleTranslate(query, isFullSentence) {
    var container = $('#translate-result');

    var url = createGoogleTranslateUrl(query);
    if (url === lastCalledGoogleTranslateUrl) {
        renderGoogleTranslateResult(container, lastReceivedGoogleTranslateResponse, isFullSentence);
        return;
    }
    lastCalledGoogleTranslateUrl = url;

    jQuery.ajax({
        type: "GET",
        url: url,
        beforeSend: function () {
            container.html("<div class='loading'>Loading</div>");
        },
        success: function (body) {
            lastReceivedGoogleTranslateResponse = body;
            renderGoogleTranslateResult(container, body, isFullSentence);
        },
        error: function (jqXHR, textStatus, errorThrown) {
            $('#debug').text("Couldn't fetch response from Jisho API. Probably cross origin proxy failed or API is down.")
        }
    });
}

function renderJishoVocabLookup() {
    var selectedText = getSelectedText();

    var form = "" +
        "<div class=\"search-inputs\">" +
        "<label>" +
        "   <span class=\"hint\">Looks up words via Jisho API</span>" +
        "   <input type=\"text\" id=\"search-field\" placeholder=\" Search...\" value=\"" + selectedText + "\"/>" +
        "   <input type=\"button\" id='search-button' onclick=\"searchWord()\" value=\"Search\">" +
        "</label>" +
        "</div>";

    $('#modal').html(form);
    $('#search-field').keypress(function (e) {
        if (e.keyCode === 13) {
            searchWord();
        }
    });
}

function renderGrammarLookup() {
    var selectedText = getSelectedText();

    var form = "" +
        "<div class=\"search-inputs\">" +
        "<label>" +
        "   <span class=\"hint\">Lookup grammar point:</span><br>" +
        "   <input type=\"text\" style='width: 100%;' id=\"search-field\" placeholder=\" Search...\" " +
        "value=\"" + selectedText + "\" onkeyup='updateGrammarSearchLinks()' onblur='updateGrammarSearchLinks()'/>" +
        "</label>" +
        "<div><a target='_blank' id='github-search'></a></div>" +
        "<div><a target='_blank' id='stackexchange-search'></a></div>" +
        "<div><a target='_blank' id='google-search'></a></div>" +
        "<div><a target='_blank' id='tatobea-search'></a></div>" +
        "</div>";

    $('#modal').html(form);
    updateGrammarSearchLinks();
}

function updateGrammarSearchLinks() {
    var searchTerm = $('#search-field').val();
    updateSingleGrammarLink('github-search', "https://jihadichan.github.io/?q=" + searchTerm.trim());
    updateSingleGrammarLink('stackexchange-search', "https://japanese.stackexchange.com/search?q=" + searchTerm.trim());
    updateSingleGrammarLink('google-search', "https://www.google.com/search?q=grammar+" + searchTerm.trim());
    updateSingleGrammarLink('tatobea-search', "https://tatoeba.org/eng/sentences/search?query=%22" + searchTerm.trim() + "%22&from=jpn&to=eng");
}

function updateSingleGrammarLink(cssId, url) {
    var link = $('#' + cssId);
    link.attr("href", url);
    link.text(url);
}

function searchWord() {
    var term = $('#search-field').val().trim();
    if (term !== "") {
        fetchWordFromJishoApi(term);
    }
}


/*********************************************************************************/
// RUN ON LOAD
/*********************************************************************************/

renderNotes();
renderOptions();
// renderAnalysisTable(); // Done in renderHighlights()
renderHighlights(); // Renders also sentence
renderSourceCell();
addShowSolutionClickEvent();
addToggleFuriganaClickEvent();

