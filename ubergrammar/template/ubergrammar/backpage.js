var CORS_PROXY = "https://jp-learner.herokuapp.com/";
var currentVocabMarkupObj = {};
var lastCalledUrl = "";
var lastReceivedResponse = "";
var sentences = gatherSentences();

/*********************************************************************************/
// RENDERED SECTION
/*********************************************************************************/

function addToggleFuriganaClickEvent() {
    $('#sentence').on("click", function () {
        toggleFurigana();
    });
}

function renderDefaultView() {
    renderGrammaticalConcept();
    $('#summary_fld').html(createSummary());
    $('#your_notes_fld').html($('#your_notes').html());

    if($('#formation').html() !== "" || sentences.indexOf("<b>No sentences</b>") === -1) {
        renderFormation();
    } else if($('#textnotes').html() !== "") {
        renderDjtTextNotes();
    } else if($('#notes').html() !== "") {
        renderBookPage();
    }
}

function createSummary() {
    var list = [];

    var equivalent = $('#equivalent').html();
    if(equivalent) {
        list.push("<b>Equiv: </b>"+equivalent);
    }
    var usage = $('#usage').html();
    if(usage) {
        list.push("<b>Usage: </b>"+usage);
    }
    var part_of_speech = $('#part_of_speech').html();
    if(part_of_speech) {
        list.push("<b>PoS: </b>"+part_of_speech);
    }
    var related_expression = $('#related_expression').html();
    if(related_expression) {
        list.push("<b>Rel: </b>"+related_expression);
    }
    var antonym_expression = $('#antonym_expression').html();
    if(antonym_expression) {
        list.push("<b>Atym: </b>"+antonym_expression);
    }


    if(list.length === 0) {
        return "";
    }

    var html = "<ul>";
    $(list).each(function (index, value) {
        html += "<li>"+value+"</li>";
    });
    html += "</ul>";

    return html;
}

function renderGrammaticalConcept() {
    var copy = $('#grammatical_concept').html();
    // Clean divs fro Anki desktop
    copy = copy.replace(/<\/?div>/g, "");

    // Clean and split to lines
    copy = copy.replace(/\n/g, "");
    var lines = copy.split(/<br ?\/?>/);
    for (var y = 0; y < lines.length; y++) {
        lines[y] = lines[y].trim();
    }

    // Replace lists
    var html = "";
    for (var i = 0; i < lines.length; i++) {
        if (lines[i].indexOf('-') === 0) {
            var x = i;
            var list = "<ul>";
            while (lines[x] && lines[x].indexOf('-') === 0) {
                list += "<li>" + lines[x].replace(/^- ?/, "") + "</li>";
                x++;
            }
            list += "</ul>";
            i = x - 1;
            html += list;
        } else {
            html += lines[i] + "<br>";
        }
    }

    $('#grammatical_concept_fld').html(html);
}

function renderFormation() {
    var formation = $('#formation').html();
    var hr = "";
    if(formation !== "" && sentences.indexOf("<b>No sentences</b>") === -1) {
        hr = "<hr>";
    }
    $('#rendered-content').html(formation + hr + sentences);
}

function renderBookPage() {
    var notes = $('#notes').html().replace(/<img src="/, "<img src=\"ubergrammar/img/");
    console.log(notes)
    $('#rendered-content').html(notes);
}

function renderDjtTextNotes() {
    var textnotes = $('#textnotes').html();
    $('#rendered-content').html(textnotes);
}

function renderExampleSentences() {
    $('#rendered-content').html(sentences);
}

function renderJishoWithSentence() {
    $('#rendered-content').html('<iframe class=\"jisho\" src=\"https://jisho.org/\"></iframe>');
}


function gatherSentences() {
    var sentences = [];
    var sentence = {};
    var firstJp = $('#expression_jp').html();
    var firstEn = $('#expression_en').html();
    if(firstJp || firstEn) {
        sentence.jp = firstJp;
        sentence.en = firstEn;
        sentences.push(sentence);
    }

    for(var i = 1; i <= 15; i++) {
        sentence = {};
        var jp = $('#example_'+i+'_jp').html();
        var en = $('#example_'+i+'_en').html();
        if(jp || en) {
            sentence.jp = jp;
            sentence.en = en;
            sentences.push(sentence);
        }
    }
    if(sentences.length === 0) {
        return "<b>No sentences</b>";
    }
    sentences = shuffle(sentences)

    var html = "";
    $(sentences).each(function (index, value) {
        html += "<div>"+value.jp+"</div>";
        html += "<div>"+value.en+"</div><br>";
    });

    return html;
}

function shuffle(array) {
    let counter = array.length;

    // While there are elements in the array
    while (counter > 0) {
        // Pick a random index
        let index = Math.floor(Math.random() * counter);

        // Decrease counter by 1
        counter--;

        // And swap the last element with it
        let temp = array[counter];
        array[counter] = array[index];
        array[index] = temp;
    }

    return array;
}


/*********************************************************************************/
// SENTENCE
/*********************************************************************************/

function renderYourNotes() {
    var jQuerySelection = $('#your_notes');
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
    if (html.indexOf("!done") > -1) {
        html = html.replace(/!done/, "");
        html += "<div class='formal'>DONE</div>"
    }
    jQuerySelection.html(html);
}




/*********************************************************************************/
// OPTIONS
/*********************************************************************************/

function renderOptions() {
    var jQuerySelection = $('#options');
    renderOptionHeading(jQuerySelection, "Helpers");
    renderVocabButton(jQuerySelection);
    renderGrammarButton(jQuerySelection);
    renderJishoButton(jQuerySelection);
    renderMailToButton(jQuerySelection);
    renderOptionHeading(jQuerySelection, "Views");
    renderFormationButton(jQuerySelection);
    renderDjtTextNotesButton(jQuerySelection);
    renderBookPageButton(jQuerySelection);
    // renderExampleSentencesButton(jQuerySelection);
}

function renderOptionHeading(jQuerySelection, heading) {
    jQuerySelection.html(jQuerySelection.html() + "" +
        "<div style='text-align: center; margin-top: 5px;'><b>"+heading+"</b></div>");
}

function renderVocabButton(jQuerySelection) {
    jQuerySelection.html(jQuerySelection.html() + "" +
        "<button class='options-button' onclick='renderJishoVocabLookup()'>" +
        "   <a href='#modal' rel='modal:open'>Vocab</a>" +
        "</button>");
}

function renderGrammarButton(jQuerySelection) {
    jQuerySelection.html(jQuerySelection.html() + "" +
        "<button class='options-button' onclick='renderGrammarLookup()'>" +
        "   <a href='#modal' rel='modal:open'>Grammar</a>" +
        "</button>");
}

function renderMailToButton(jQuerySelection) {
    var sentenceWithReading = "";
    sentenceWithReading += $('#your_notes').html()+"<br><br>";
    sentenceWithReading += $('#equivalent').html()+"<br><br>";
    sentenceWithReading += $('#usage').html()+"<br><br>";
    var subject = encodeURI($('#grammatical_concept').html());

    var mailTo = "<a href=\"mailto:?subject=" + subject + "&body=" + encodeURI(sentenceWithReading) + "\">";
    jQuerySelection.html(jQuerySelection.html() + mailTo + "<button class='options-button'>Mail</button></a>");
}

function renderJishoButton(jQuerySelection) {
    jQuerySelection.html(jQuerySelection.html() + "<button class='options-button' onclick='renderJishoWithSentence()'>Jisho</button>");
}

function renderBookPageButton(jQuerySelection) {
    if($('#notes').html() !== "") {
        jQuerySelection.html(jQuerySelection.html() + "<button class='options-button' onclick='renderBookPage()'>Book Page</button>");
    }
}

function renderDjtTextNotesButton(jQuerySelection) {
    if($('#textnotes').html() !== "") {
        jQuerySelection.html(jQuerySelection.html() + "<button class='options-button' onclick='renderDjtTextNotes()'>DJT Notes</button>");
    }
}

function renderFormationButton(jQuerySelection) {
    if($('#formation').html() !== "" || sentences.indexOf("<b>No sentences</b>") === -1) {
        jQuerySelection.html(jQuerySelection.html() + "<button class='options-button' onclick='renderFormation()'>Formation</button>");
    }
}
// function renderExampleSentencesButton(jQuerySelection) {
//     if(sentences.indexOf("<b>No sentences</b>") === -1) {
//         jQuerySelection.html(jQuerySelection.html() + "<button class='options-button' onclick='renderExampleSentences()'>Sentences</button>");
//     }
// }

function toggleFurigana() {
    $('#sentence rt').toggle();
}


/*********************************************************************************/
// JISHO SCRAPER
/*********************************************************************************/

function createUrl(word) {
    return CORS_PROXY + "https://jisho.org/api/v1/search/words?keyword=" + word;
}

function fetchWordFromJishoApi(word) {
    var modal = $('#modal');

    var url = createUrl(word);
    if (url === lastCalledUrl) {
        renderTranslationTable(modal, lastReceivedResponse);
        return;
    }
    lastCalledUrl = url;

    jQuery.ajax({
        type: "GET",
        url: url,
        beforeSend: function () {
            modal.html("<div class='loading'>Loading</div>");
        },
        success: function (body) {
            lastReceivedResponse = body;
            renderTranslationTable(modal, body);
            console.log(body);
        },
        error: function (jqXHR, textStatus, errorThrown) {
            // todo show in modal
            $('#debug').text("Couldn't fetch response from Jisho API. Probably cross origin proxy failed or API is down.")
        }
    });
}

function renderTranslationTable(modal, body) {
    var data = body.data;
    var html = "";

    // OUTPUT
    html += "" +
        "<div class='vocab-output'>" +
        "   <div id='vocab-preview'></div>" +
        "   <label><textarea id='vocab-markup'></textarea><button style='float: right' onclick='resetMarkupObj()'>Reset</button></label>" +
        "</div>";

    // WORDS
    html += "<div class='translation-table'><table>";
    $(data).each(function (index, entity) {

        var word = extractWord(entity);
        console.log("extracted", word);
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
    console.log("vocab", entry);
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
    console.log("currentVocabMarkupObj", currentVocabMarkupObj);

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
    console.log(entity);
    var word = {};
    if (entity.japanese[0].word) {
        console.log("word: ", entity.japanese[0].word);
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
        "<div><a target='_blank' id='translate-link'></a></div>" +
        "</div>";

    $('#modal').html(form);
    updateGrammarSearchLinks();
}

function updateGrammarSearchLinks() {
    var searchTerm = $('#search-field').val();
    updateSingleGrammarLink('github-search', "https://jihadichan.github.io/?q=" + searchTerm.trim());
    updateSingleGrammarLink('stackexchange-search', "https://japanese.stackexchange.com/search?q=" + searchTerm.trim());
    updateSingleGrammarLink('google-search', "https://www.google.com/search?q=grammar+" + searchTerm.trim());
    updateSingleGrammarLink('tatobea-search', "https://tatoeba.org/eng/sentences/search?query=%22"+searchTerm.trim()+"%22&from=jpn&to=eng");
    updateSingleGrammarLink('translate-link', "https://translate.google.de/#view=home&op=translate&sl=ja&tl=en&text="+searchTerm.trim());
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

renderYourNotes();
renderOptions();
// renderAnalysisTable(); // Done in renderHighlights()
renderDefaultView(); // Renders also sentence
addToggleFuriganaClickEvent();

