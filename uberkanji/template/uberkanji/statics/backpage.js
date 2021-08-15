var data;
try {
    data = JSON.parse(decodeURIComponent($('#data').html().replace(/\+/g, ' ')));
} catch (e) {
    data = {};
    $('#debug').text("JSON parse error. " + e.message);
}
var fonts = [1]; //, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15]; // todo if you want more, check HTML in template -> renderFonts(), also check CSS
var amountOfFontsToShow = 1;
var readings = $('#play-sound').text().trim().split(".");
var kanjiField = $('#kanji');
var confusions = extractConfusions();
// Top
// $('#meanings').text(data.meanings); // no meanings on top, useless

// Metadata
function renderMetaData() {
    var table = '<div class="metadata-content"><table>';
    table += '<tr><td>RTK Keyword</td><td>' + data.rtkKeyword + '</td></tr>';
    table += '<tr><td>RTK Index</td><td>' + data.rtkIndex + '</td></tr>';
    table += '<tr><td>Meanings</td><td>' + data.meanings + '</td></tr>';
    table += '<tr><td>Frequency</td><td>' + data.frequency + '</td></tr>';
    table += '<tr><td>Components</td><td>' + data.components + '</td></tr>';
    table += '<tr><td>Strokes</td><td>' + data.strokes + '</td></tr>';
    table += '<tr><td>On Readings</td><td>' + data.onReadings + '</td></tr>';
    table += '<tr><td>Kun Readings</td><td>' + data.kunReadings + '</td></tr>';
    table += '</table></div>';
    $('#rendered-content').html(table);
}

function createMostCommonReadingsTable() {
    var total = 0;
    $.each(data.mostCommonReadings, function (key, value) {
        total += value;
    });

    var table = '<table>' +
        '<tr><td colspan="3" style="text-align: center;">All Readings</td></tr>' +
        '<tr><td>Rdg</td><td>Count</td></tr>';

    if (!jQuery.isEmptyObject(data.mostCommonReadings)) {
        $.each(data.mostCommonReadings, function (key, value) {
            table += '<tr><td><span onclick="filterSearch(\'' + key + '\')">' + key + '</span><span style="float:right" onclick="playSingleReading(\'' + toHiragana(key) + '\')">&#9658;</span></td>' +
                '<td>' + value + ' (' + (value / total * 100).toFixed(2) + '%)</td></tr>';
        });
    }

    // Add additional readings form mnemonic
    var readingCounts = {};
    $.each(getReadingsFromMnemonic(), function (index, reading) {
        var shouldAppend = true;
        $.each(data.scoredReadings, function (key, readingScore) {
            if (toHiragana(reading) === toHiragana(key)) {
                shouldAppend = false;
                return false;
            }
        });
        if (shouldAppend) {
            countReadings(reading, readingCounts, false);
            if (readingCounts[reading]) {
                table += '<tr>' +
                    '<td><span onclick="filterSearch(\'' + reading + '\')">' + reading + '*</span><span style="float:right" onclick="playSingleReading(\'' + toHiragana(reading) + '\')">&#9658;</span></td>' +
                    '<td>' + readingCounts[reading].total + '</td>' +
                    '</tr>';
                getScoredReadingsHighlight(reading);
            }
        }
    });

    table += '</table>';
    $('#most-common-readings').append(table);
}

function createScoredReadingsTable() {
    var table = '<table>' +
        '<tr><td colspan="4" style="text-align: center;">Scored Readings <a class="suggestions" href="#modal" rel="modal:open" onclick="fillModalWithSuggestions()">&lt;ruby&gt;</a></td></tr>';
    table += '<tr><td>Rdg</td><td>Score</td><td>fbKJ</td><td>fbKN</td></tr>';

    if (!jQuery.isEmptyObject(data.scoredReadings)) {
        // Create from JSON
        $.each(data.scoredReadings, function (key, readingScore) {
            var highLightKey = getStrikeThroughClass(key);
            table += '<tr class="' + getScoredReadingsHighlight(key) + '">' +
                '<td class="' + highLightKey + '" onclick="filterSearch(\'' + key + '\')">' + key + '</td>' +
                '<td class="' + highLightKey + '">' + readingScore.score + '</td>' +
                '<td class="' + highLightKey + '">' + readingScore.fbKJ + '</td>' +
                '<td class="' + highLightKey + '">' + readingScore.fbKN + '</td>' +
                '</tr>';
        });
    }
    // Add additional readings form mnemonic
    var readingCounts = {};
    $.each(getReadingsFromMnemonic(), function (index, reading) {
        var shouldAppend = true;
        $.each(data.scoredReadings, function (key, readingScore) {
            if (toHiragana(reading) === toHiragana(key)) {
                shouldAppend = false;
                return false;
            }
        });
        if (shouldAppend) {
            countReadings(reading, readingCounts, true);
            if (readingCounts[reading]) {
                var highlightKey = getScoredReadingsHighlight(reading);
                table += '<tr class="' + highlightKey + '">' +
                    '<td onclick="filterSearch(\'' + reading + '\')">' + reading + '*</td>' +
                    '<td class="' + highlightKey + '">' + readingCounts[reading].total + '</td>' +
                    '<td class="' + highlightKey + '">' + readingCounts[reading].fnKJ + '</td>' +
                    '<td class="' + highlightKey + '">' + readingCounts[reading].fnKN + '</td>' +
                    '</tr>';
            }
        }
    });


    table += '</table>';
    $('#scored-readings').append(table);
}

function countReadings(reading, readingCounts, shouldOnlyCountCommonWords) {

    $.each(data.unclassifiedReadings, function (index, value) {
        if (toHiragana(value.reading).indexOf(toHiragana(reading)) !== -1) {
            if (shouldOnlyCountCommonWords) {
                if (value.score >= 1) {
                    incrementCount(reading, readingCounts, value.type);
                }
            } else {
                incrementCount(reading, readingCounts, value.type);
            }
        }
    });
    if (!readingCounts[reading]) {
        $.each(data.wordExamples, function (index, value) {
            if (toHiragana(value.reading).indexOf(toHiragana(reading)) !== -1) {
                if (value.type === "fnKJ") {
                    if (shouldOnlyCountCommonWords) {
                        if (value.score >= 1) {
                            incrementCount(reading, readingCounts, value.type);
                        }
                    } else {
                        incrementCount(reading, readingCounts, value.type);
                    }
                }
            }
        });
    }
    // todo extract to function. Only thing that changes is 'fnKN'
    if (!readingCounts[reading]) {
        $.each(data.wordExamples, function (index, value) {
            if (toHiragana(value.reading).indexOf(toHiragana(reading)) !== -1) {
                if (value.type === "fnKN") {
                    if (shouldOnlyCountCommonWords) {
                        if (value.score >= 1) {
                            incrementCount(reading, readingCounts, value.type);
                        }
                    } else {
                        incrementCount(reading, readingCounts, value.type);
                    }
                }
            }
        });
    }
}

function incrementCount(reading, readingCounts, type) {
    var counts = readingCounts[reading];
    if (counts) {
        readingCounts[reading] = {
            total: ++counts.total,
            fnKJ: type === "fnKJ" ? ++counts.fnKJ : counts.fnKJ,
            fnKN: type === "fnKN" ? ++counts.fnKN : counts.fnKN
        };
    } else {
        readingCounts[reading] = {
            total: 1,
            fnKJ: type === "fnKJ" ? 1 : 0,
            fnKN: type === "fnKN" ? 1 : 0
        };
    }
}

function getScoredReadingsHighlight(reading) {
    var mnemonicField = $('#manual-edits-mnemonic').html();
    var regex = /\(.*?\)/;
    var sections = mnemonicField.split(/---+/);

    var sectionIndex = -1;
    for (var i = 0; i < sections.length; i++) {
        var match = regex.exec(sections[i]);
        while (match != null) {
            if (match[0].indexOf("(" + reading + ",") !== -1) {
                sectionIndex = i;
                break;
            }
            sections[i] = sections[i].replace(regex, "");
            match = regex.exec(sections[i]);
        }
        if (sectionIndex !== -1) {
            break;
        }
    }

    switch (sectionIndex) {
        case 1:
            return "section-fnkj";
        case 2:
            return "section-fnkn";
        case 3:
            return "section-ofrm";
        case 4:
            return "section-gray-bg";
        case -1:
        default:
            return "";
    }
}

// [].includes() does not work in Anki
function getStrikeThroughClass(reading) {
    var strikeThroughClass = "strike-through";

    // If reading does exist in #play-sound, then don't strike through
    var i = readings.length;
    while (i--) {
        if (readings[i] === toHiragana(reading)) {
            strikeThroughClass = "";
        }
    }

    // If reading does exist in mnemonic field, then don't strike through
    var readingsFromMnemonic = getReadingsFromMnemonic();
    for (i = 0; i < readingsFromMnemonic.length; i++) {
        if (readingsFromMnemonic[i] === toHiragana(reading)) {
            strikeThroughClass = "";
        }
    }
    return strikeThroughClass;
}

function getReadingsFromMnemonic() {
    var mnemonicField = $('#manual-edits-mnemonic').html();
    var regex = /(<ruby.*?(\(\W+,).*?\))/;

    var readings = [];
    var match = regex.exec(mnemonicField);
    while (match != null) {
        var candidateReading = toHiragana(match[2].replace("(", "").replace(",", "").trim());
        var shouldAdd = true;
        $.each(readings, function (index, existingReading) {
            if (existingReading === candidateReading) {
                shouldAdd = false;
            }
        });
        if (shouldAdd) {
            readings.push(candidateReading);
        }

        mnemonicField = mnemonicField.replace(regex, "");
        match = regex.exec(mnemonicField);
    }
    return readings;
}

function getRandomFontIds(n) {
    var result = new Array(n),
        len = fonts.length,
        taken = new Array(len);
    if (n > len)
        throw new RangeError("getRandom: more elements taken than available");
    while (n--) {
        var x = Math.floor(Math.random() * len);
        result[n] = fonts[x in taken ? taken[x] : x];
        taken[x] = --len in taken ? taken[len] : len;
    }
    return result;
}

function renderFonts(amount, breakAfter) {
    var kanji = kanjiField.text();
    var container = '<div class="fonts">';
    var count = 1;
    $.each(getRandomFontIds(amount), function (i, value) {
        container += '<span class="kanji font-' + value + '">' + kanji + '</span>';
        if (count++ % breakAfter === 0) {
            container += '<br>';
        }
    });

    container += '</div>';
    $('#rendered-content').html(container);
}

function renderFontSelection() {
    var ids = getRandomFontIds(amountOfFontsToShow);
    var kanji = kanjiField.text();
    var html = '';
    $.each(ids, function (index, value) {
        html += '<div class="kanji font-' + value + '">' + kanji + '</div>'
    });
    $('#side-fonts').html(html);
}

function renderJisho() {
    var kanji = kanjiField.text();
    $('#rendered-content').html('<iframe class=\"embed\" src=\"https://jisho.org/search/*' + kanji + '*\"></iframe>');
}

function renderJishoWord(word) {
    $('#rendered-content').html('<iframe class=\"embed\" src=\"https://jisho.org/search/' + word + '\"></iframe>');
}

function renderWordTable(type) {
    var words = {};
    var title = "";
    switch (type) {
        case "wordExamples":
            words = data.wordExamples;
            title = "Word Examples";
            break;
        case "otherForms":
            words = data.otherForms;
            title = "Other Forms";
            break;
        case "unclassifiedReadings":
            words = data.unclassifiedReadings;
            title = "Unclassified Readings";
            break;
    }

    // Find longest word (for <td> width)
    var maxLength = 1;
    $.each(words, function (index, word) {
        if (word.word.length > maxLength) {
            maxLength = word.word.length;
        }
    });
    var tdWidth = maxLength * 23 + 10;

    var table = "<div><h2 style='text-align: center'>" + title + "</h2><table id='example-table' class='example-table'>";
    table += '<thead><tr><th>Word</th><th>Rdg</th><th>Meanings</th><th>N</th><th>W</th><th>J</th></tr></thead><tbody>';
    $.each(words, function (index, word) {
        table += '<tr>';
        table += '<td align="center" class="word" style="width: ' + tdWidth + 'px"><a class="kanji-link" href="#modal" rel="modal:open" onclick="fillModal(\'' + toEncodedJson(word) + '\')"><ruby>' + word.word + '<rt>' + word.reading + '</rt></ruby></a></td>';
        table += '<td onclick="filterSearch(\'' + word.foundKanjiReading + ' ' + word.type + '\')" class="reading-column">' + word.foundKanjiReading + '<br>' + word.type + '</td>';
        table += '<td>' + word.meanings + '<span class="tts-link"><a href="' + createTranslateLink(word.word.trim()) + '" >&#x2197;</a>&nbsp;&nbsp;&nbsp;<span onclick="playWordReading(\'' + word.word + '\', \'' + word.reading + '\')">&#9658;</span></span></td>';
        table += '<td class="small-column">' + getFrequency(word.freqNF) + '</td>';
        table += '<td class="small-column">' + getFrequency(word.freqWK) + '</td>';
        table += '<td class="' + getCommonalityClass(word.score, word.freqNF, word.freqWK) + ' word-link small-column" onclick="renderJishoWord(\'' + word.word.trim() + '\')">' + usuWrittenAs(word.usuallyWrittenAs) + '</td>';
        table += '</tr>';
    });
    table += "</tbody></table>" +
        "Note:<br>" +
        "green = common word, light green = only common on Jisho or JLPT, gray = uncommon.<br>" +
        "J = Jisho, kji = only written in kanji, kn = cases where it's usually written in kana<br>" +
        "W = Wikipedia frequency rank, N = Netflix frequency rank. Both out of top 20k words. 99999 = not in list.<br>" +
        "* word is extracted from mnemonic. Only Unclassified Readings are counted. If no occurrences found then Word Examples " +
        "are counted, but only if they're marked as fbKJ. If no occurrence then fbKN are counted. This is error prone, due to false positives." +
        "</div>";

    $('#rendered-content').html(table);
    $(document).ready(function () {
        $('#example-table').DataTable({
            /* No ordering applied by DataTables during initialisation */
            "order": [],
            "pageLength": 100
        });
    });
}

function getFrequency(value) {
    if (value) {
        return parseInt(value);
    }
    return 99999;
}

function usuWrittenAs(value) {
    switch (value) {
        case "Kanji":
            return "<span class='writtenAsKanji'>kji</span>";
        case "Kana":
            return "<span class='writtenAsKana'>kn</span>";
        default:
            return value;
    }
}

function createTranslateLink(word) {
    return "https://translate.google.de/#view=home&op=translate&sl=ja&tl=en&text=" + word;
}

function getCommonalityClass(score, freqNF, freqWK) {
    switch (score) {
        case 2:
        case 1: // 1 == common, formerly: return "jlpt";
            if (freqNF <= 20000 || freqWK <= 20000) {
                return "common";
            }
            return "common-no-freq";
        case 0:
            return "uncommon";
        default:
            return "";
    }
}

function renderButtonTexts() {
    $('#wordExamples').text('Word Examples (' + data.wordExamples.length + ')');
    $('#otherForms').text('Other Forms (' + data.otherForms.length + ')');
    $('#unclassifiedReadings').html('Unclassified (' + countUnclassifiedCommons() + '/' + data.unclassifiedReadings.length + ')');
}

function countUnclassifiedCommons() {
    var commonWordsCount = 0;
    var hasFrequentWord = false;
    var hasWordsWrittenInKana = false;
    $.each(data.unclassifiedReadings, function (index, word) {
        if (word.score >= 1) {
            commonWordsCount++;
        }
        if (word.usuallyWrittenAs === "Kana") {
            hasWordsWrittenInKana = true;
        }
        if (word.freqNF <= 20000 || word.freqWK <= 20000) {
            hasFrequentWord = true;
        }
    });

    var cssClass = "";
    if (hasFrequentWord) {
        cssClass += "font-red";
    } else if (hasWordsWrittenInKana) {
        cssClass += "font-yellow";
    }

    return "<span class='" + cssClass + "'>" + commonWordsCount + "</span>";
}

function toEncodedJson(word) {
    var obj = {};
    obj.word = word.word;
    obj.reading = word.reading;
    obj.foundKanjiReading = word.foundKanjiReading;
    obj.meanings = word.meanings;
    return btoa(encodeURIComponent(JSON.stringify(obj)));
}

function fillModal(encodedJson) {
    var term = JSON.parse(decodeURIComponent(atob(encodedJson)));
    var meaningsArray = term.meanings.split("<br>");
    var firstMeaning = meaningsArray[0].replace(/\([^\(]*(noun|verb|adjective|adverb|expression)[^\)]*\)/i, "").trim();
    // firstMeaning = firstMeaning.replace(/,.*$/, "").trim();

    var text = "<ruby>" + term.word + "<rt>" + term.reading + "</rt></ruby> (" + term.foundKanjiReading + ", " + firstMeaning + ")";

    $('#word-area').val(text);
    $('#modal-meanings').html(term.meanings);
    updateRuby();
    copyWord(text);
}

function fillModalWithSuggestions() {
    // Find most suited words

    var suggestions = [];
    $.each(data.scoredReadings, function (scoredReading, score) {

        // Create list from matching words for reading
        var extract = [];
        $.each(data.wordExamples, function (index, wordData) {
            if (wordData.foundKanjiReading === scoredReading) {
                extract.push(wordData);
            }
        });

        // Sort by frequency
        extract.sort(function (a, b) {
            var aFreqNF = a.freqNF === undefined ? 99999 : parseInt(a.freqNF);
            var bFreqNF = b.freqNF === undefined ? 99999 : parseInt(b.freqNF);
            var aFreqWK = a.freqWK === undefined ? 99999 : parseInt(a.freqWK);
            var bFreqWK = b.freqWK === undefined ? 99999 : parseInt(b.freqWK);

            // By Netflix frequency
            if (aFreqNF !== bFreqNF) {
                return aFreqNF > bFreqNF ? 1 : -1;
            }
            // By Wikipedia frequency
            if (aFreqWK !== bFreqWK) {
                return aFreqWK > bFreqWK ? 1 : -1;
            }
            // Sort by score
            return a.score < b.score ? 1 : -1;
        });
        suggestions.push(extract[0]);
    });

    // Create HTML output of suggestions
    var suggestionsHtml = "";
    $.each(suggestions, function (index, wordData) {
        var meaningsArray = wordData.meanings.split("<br>");
        var firstMeaning = meaningsArray[0].replace(/\([^\(]*(noun|verb|adjective|adverb|expression)[^\)]*\)/i, "").trim();
        suggestionsHtml += "<ruby>" + wordData.word + "<rt>" + wordData.reading + "</rt></ruby> (" + wordData.foundKanjiReading + ", " + firstMeaning + ")<br>\n";
    });

    // Create HTML for additional meanings
    var meaningsHtml = "";
    $.each(suggestions, function (index, wordData) {
        meaningsHtml += wordData.word + "<br>";
        meaningsHtml += wordData.meanings + "<br><br>";
    });

// Fill modal
    $('#modal-result').text("Suggestions");
    $('#word-area').val(suggestionsHtml);
    $('#modal-meanings').html(meaningsHtml);
}

function updateRuby() {
    $('#modal-result').html($('#word-area').val());
}

// Doesn't work in Anki. Probably browser too old.
function copyWord(text) {
    var $temp = $("<input>");
    $("body").append($temp);
    $temp.val(text).select();
    document.execCommand("copy");
    $temp.remove();
}

// No use on mobile, only annoying.
function selectOnClick(element) {
    // element.select();
    // try {
    //     element.setSelectionRange(0, 99999);
    // } catch (err) {
    // }
}

function appendSearchOnClickToRubyElements() {
    $.each($('#manual-edits-mnemonic').find('ruby'), function (index, element) {
        var search = element.innerHTML.replace(/<rt.*/, "").replace(/<\/?span.*?>/g, "");
        $(element).click(function () {
            filterSearch(search);
        });
    });
}

var mnemonicFull = $('#manual-edits-mnemonic').clone();

function showMnemonic() {
    if (isToggled) {
        var words = "";
        $.each($(mnemonicFull).find('ruby'), function (index, value) {
            words += "<ruby>" + value.innerHTML.replace(/<.*>/, "") + "</ruby><br>";
        });
        $('#manual-edits-mnemonic').html(words);
    } else {
        $('#manual-edits-mnemonic').html(mnemonicFull.html());
    }
    highlightKanjiInMnemonic();
    makeCommentsGray();
}

function makeCommentsGray() {
    var element = $('#manual-edits-mnemonic');
    var mnemonic = element.html();
    var commentRegex = new RegExp("# ?<ruby.*?ruby>.*?(<(br>)?|\n)");
    var extract = commentRegex.exec(mnemonic);
    var i = 1;
    var replacements = [];

    while (extract !== null) {
        var marker = "@" + i + "@";
        var cleanExtract = extract[0]
            .replace(/<$/, "")
            .replace(/#/, "<span class='bighash'>#</span>")
            .replace(/class="highlight"/g, "");
        var replacement = "<span class='comment'>" + cleanExtract.replace(/<$/, "") + "</span>";
        replacements.push({marker: marker, replacement: replacement});
        mnemonic = mnemonic.replace(commentRegex, marker);
        extract = commentRegex.exec(mnemonic);
    }
    $.each(replacements, function (index, value) {
        mnemonic = mnemonic.replace(value.marker, value.replacement);
    });
    element.html(mnemonic);
}

var isToggled = false;

function toggleFurigana() {
    showMnemonic();
    addHighlightsToMnemonicField();
    appendSearchOnClickToRubyElements();
    isToggled = !isToggled;
}

function filterSearch(value) {
    $("input[type='search']").val(value).trigger("input");
}


function highlightKanjiInMnemonic() {
    var kanji = kanjiField.text();

    // Regex
    var kanjiRegex = new RegExp(String(kanji));
    var rubyRegex = new RegExp("<ruby.*?ruby>\\s\\(.*?,");
    var readingRegex = new RegExp("\\(.*?,");

    var element = $('#manual-edits-mnemonic');
    var mnemonic = element.html();
    var extract = rubyRegex.exec(mnemonic);
    var i = 1;
    var replacements = [];
    while (extract !== null) {
        var marker = "#" + i + "#"; // replace occurrence with marker, for later replacement
        var reading = toHiragana(readingRegex.exec(extract[0])[0].replace(/[^\w]/, "").replace(",", ""));
        var replacement = "";
        if (extract[0].indexOf("?") === -1) {
            replacement = extract[0]
                .replace(kanjiRegex, "<span class='highlight'>" + kanji + "</span>")
                .replace(reading, "<span class='highlight'>" + reading + "</span>");
        } else {
            replacement = extract[0];
        }
        replacements.push({marker: marker, replacement: replacement});

        mnemonic = mnemonic.replace(rubyRegex, marker);
        extract = rubyRegex.exec(mnemonic);
        i++;
    }

    $.each(replacements, function (index, value) {
        mnemonic = mnemonic.replace(value.marker, value.replacement);
    });

    element.html(mnemonic);
}

function highlightUnclassifiedReadings() {
    var hasCommonWords = false;
    $.each(data.unclassifiedReadings, function (index, word) {
        if (word.freqNF <= 20000 || word.freqWK <= 20000) {
            $('#unclassifiedReadings').attr('style', 'background-color: rgba(140, 175, 73, 0.6)');
            return false;
        }
        if (word.score >= 1) {
            hasCommonWords = true;
        }
    });
    if (hasCommonWords) {
        $('#unclassifiedReadings').attr('style', 'background-color: rgba(113, 175, 36, 0.31)');
    }
}

function addHighlightsToMnemonicField() {

    // Ruby highlighting
    var mnemonicField = $('#manual-edits-mnemonic');
    var sections = mnemonicField.html().split(/---+/);
    if (sections[1]) {
        sections[1] = deleteFirstBreak(sections[1]);
        sections[1] = sections[1].trim() !== "" ? "<div class='section-fnkj section-box'>" + sections[1] + "</div>" : "";
    }
    if (sections[2]) {
        sections[2] = deleteFirstBreak(sections[2]);
        sections[2] = sections[2].trim() !== "" ? "<div class='section-fnkn section-box'>" + sections[2] + "</div>" : "";
    }
    if (sections[3]) {
        sections[3] = deleteFirstBreak(sections[3]);
        sections[3] = sections[3].trim() !== "" ? "<div class='section-ofrm section-box'>" + sections[3] + "</div>" : "";
    }
    if (sections[4]) {
        sections[4] = deleteFirstBreak(sections[4]);
        sections[4] = sections[4].trim() !== "" ? "<div class='section-notes section-box'>" + sections[4] + "</div>" : "";
    }

    // !done marker highlight
    var html = sections.join("");
    html = addDoneMarker(html);
    html = addConfMarker(html);
    html = addNewMarker(html);

    mnemonicField.html(html);
}

function addConfMarker(html) {

    if (html.indexOf("!conf") > -1) {
        html += "<div style='margin-top: 5px'><span class='confusion'>Confusions: </span>";
        for (var key in confusions) {
            if (confusions.hasOwnProperty(key)) {
                var conf = confusions[key];
                var meaning = conf.cp ? conf.cp : conf.rtk;
                console.log(conf.cp)
                meaning = meaning.replace(/<br>.*/, "")
                html += "<br>" + conf.kj + " (" + conf.r + ", " + meaning + ")";
            }
        }
        html += "<br></div>"
    }

    return html;
}

function extractConfusions() {
    var kanji = kanjiField.text().trim();
    var confGroup = confMap.refs[kanji];
    if (confGroup) {
        console.log(confMap.groups[confGroup])
        delete confMap.groups[confGroup][kanjiField.text()];
        return confMap.groups[confGroup];
    }
    return [];
}

function addDoneMarker(html) {
    if (html.indexOf("!done") > -1) {
        html = html.replace(/!done/, "");
        html += "<div class='done'>DONE</div>"
    }
    return html;
}

function addNewMarker(html) {
    // var regex = new RegExp("!new\\s?(\[.*?\])");
    var regex = new RegExp("!new ?.*?\]");
    var extract = regex.exec(html);
    if (extract !== null) {
        var marker = extract[0].replace(/!new ?/, "<div id='new' onclick='searchNew(\"" + extract[0] + "\")'>NEW ") + "</div>";
        html = html.replace(regex, "");
        html += marker;
    }

    return html;
}

function searchNew(value) {
    var term = value.replace(/^!new \[/, "").replace(/]$/, "");
    filterSearch(term);
}

function deleteFirstBreak(segment) {
    return segment.replace(/^\s*<br>/, "");
}

// --------------------------------------------------
// PLAY SOUNDS
// --------------------------------------------------
function playSingleReading(reading) {
    try {
        new Audio(createPathToMp3(reading)).play();
    } catch (err) {
        $('#debug').text("Couldn't play " + readings[i] + ". Probably file does not exist.");
    }
}

function createPathToMp3(reading) {
    return "uberkanji/readings/" + reading + ".mp3";
}

var sounds = [];
$.each(readings, function (key, val) {
    sounds.push(new Audio(createPathToMp3(val)));
});
var i = -1;

function playCompoundReading() {
    i++;
    if (i === readings.length) {
        i = -1;
        return;
    }
    sounds[i].addEventListener('ended', playCompoundReading);
    sounds[i].play();
    // DOESN'T WORK IN ANKI DROID, JS crashes. TRY-CATCH also doesn't work. Doesn't catch exception, because Audio.play() is a Promise.
    // .catch(function (err) {
    //     $('#debug').html("Couldn't play " + readings[i] + ". Either file does not exist or playback failed.<br>" + err);
    //     i = -1;
    // })
}

// Same as above, but doesn't use Audio in array. Doesn't work
// var i = -1;
// function playCompoundReading() {
//     i++;
//     if (i === readings.length) {
//         i = -1;
//         return;
//     }
//     var audio = new Audio();
//     audio.src = "uberkanji/readings/"+readings[i]+".mp3";
//     audio.addEventListener('ended', playCompoundReading);
//     audio.play();
// }

// Click play button. Doesn't work
// function clickPlay() {
//         $("#click-play").trigger("click");
// }

function playWordReading(kanji, kana) {
    new Audio("https://assets.languagepod101.com/dictionary/japanese/audiomp3.php?kanji=" + kanji.trim() + "&kana=" + kana.trim()).play();
}

// --------------------------------------------------
// KANA CONVERTER
// --------------------------------------------------

var KATAKANA_HIRAGANA_SHIFT = "\u3041".charCodeAt(0) - "\u30a1".charCodeAt(0);

function toHiragana(str) {
    var result = "";
    $.each(str.split(''), function (index, value) {
        if (value > "\u30a0" && value < "\u30f7") {
            result += String.fromCharCode(value.charCodeAt(0) + KATAKANA_HIRAGANA_SHIFT);
        } else {
            result += value;
        }
    });
    return result;
}

// --------------------------------------------------
// EXECUTE ON LOAD
// --------------------------------------------------

playCompoundReading();
createMostCommonReadingsTable();
createScoredReadingsTable();
renderFontSelection();
renderButtonTexts();
renderMetaData(); // SLOW: renderWordTable('wordExamples');
highlightUnclassifiedReadings();
addHighlightsToMnemonicField();
appendSearchOnClickToRubyElements();
toggleFurigana();
