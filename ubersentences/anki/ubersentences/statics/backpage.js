var data;
try {
    data = JSON.parse(decodeURIComponent($('#data').html()));
} catch (e) {
    data = {};
    $('#debug').text("JSON parse error. " + e.message);
}
var highlightToggle = true;
var isRevealed = false;

/*********************************************************************************/
// RENDERED SECTION
/*********************************************************************************/

function removeOverlay() {
    document.getElementById("overlay").style.display = "none";
}

function addShowSolutionClickEvent() {
    $('#solution').on("click", function () {
        removeOverlay();
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
            other += "</table></div>";

            var word = analysisElement.baseForm ? analysisElement.baseForm : analysisElement.surface;
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
    var table = "";
    if (vocabLines.length > 0) {
        table += "<div class='vocab'>Vocab</div><table class='vocab-table'>";

        $(vocabLines).each(function (index, value) {
            var split = value.split(":");
            var word = split[0].replace(/^# */, "").trim();
            var translation = split.length === 2 ? split[1].trim() : split.slice(1, split.length).join("");

            if (word.match(/.*\[.*?]/)) {
                var wordSplit = word.split("\[");
                var rt = wordSplit.slice(1, wordSplit.length).join("").replace("]", "");
                word = "<ruby>" + wordSplit[0] + "<rt>" + rt + "</rt></ruby>";
            }

            table += "<tr><td class='vocab-word'>" + word + "</td><td class='vocab-translation'>" + translation + "</td></tr>";
        });

        table += "</table>";
    }
    html += table;

    jQuerySelection.html(html)
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
    jQuerySelection.html(html);
}

function renderSourceCell() {
    var raw = $('#source').html();
    var source = raw;
    if (source !== "") {
        var regex = /\bhttp.*\b/;
        var match = regex.exec(source);
        while (match != null) {
            source = source.replace(regex, "");
            raw = raw.replace(regex, "<a target='_blank' href='"+match[0]+"'>"+match[0]+"</a>");
            match = regex.exec(source);
        }

        $('#source-cell').html("<td>" + raw + "</td>");
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

    // Case based overwrite ...
    // todo mmmh.. can we do that better? Seems to be a crappy solution. Switch is limited, but otherwise we would
    //  need a shitload of ifs with indexOf()
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
    renderFuriganaButton(jQuerySelection);
    renderJishoButton(jQuerySelection);
    renderAnalysisButton(jQuerySelection);
    renderTranslateButton(jQuerySelection);
    renderSourceButton(jQuerySelection);
    renderMailToButton(jQuerySelection);
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
    var source = $('#source').html().replace(/&/g, "%26").replace(/\?/g, "%3F");
    sentenceWithReading += "<br>" + source;
    var mailTo = "<a href=\"mailto:?subject=" + $('#sentence-raw').html() + "&body=" + sentenceWithReading + "\">";
    jQuerySelection.html(jQuerySelection.html() + mailTo + "<button class='options-button'>Mail</button></a>");
}

function renderTranslateButton(jQuerySelection) {
    var href = "https://translate.google.de/#view=home&op=translate&sl=ja&tl=en&text=" + $('#sentence-raw').html();
    jQuerySelection.html(jQuerySelection.html() + "<a href='" + href + "'><button class='options-button'>Translate</button></a>");
}

function renderJishoButton(jQuerySelection) {
    jQuerySelection.html(jQuerySelection.html() + "<button class='options-button' onclick='renderJishoWithSentence()'>Jisho</button>");
}

function renderHighLightButton(jQuerySelection) {
    jQuerySelection.html(jQuerySelection.html() + "<button class='options-button' onclick='renderHighlights()'>Highlight</button>");
}

function renderFuriganaButton(jQuerySelection) {
    jQuerySelection.html(jQuerySelection.html() + "<button class='options-button' onclick='toggleFurigana()'>Furigana</button>");
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

// todo can't get this shit to work. HTTP requests are blocked by CORS policy. Scraping an iframe also not
//  possible for the same reason.


/*********************************************************************************/
// RUN ON LOAD
/*********************************************************************************/

renderNotes();
renderOptions();
// renderAnalysisTable(); // Done in renderHighlights()
renderHighlights(); // Renders also sentence
addShowSolutionClickEvent();
