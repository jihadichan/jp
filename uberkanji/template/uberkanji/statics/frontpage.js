var data;
try {
    data = JSON.parse(decodeURIComponent($('#data').html().replace(/\+/g, ' ')));
} catch (e) {
    data = {};
    $('#debug').text("JSON parse error. " + e.message);
}
var mnemonicField = $('#mnemonic');
var playSoundField = $('#play-sound');
var confusionQuestion = $('#confusion-question');
var kanjiField = $('#kanji');
var fonts = [1]; // , 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15];
var readings = playSoundField.text().trim().split(".");
var sections = [];
var compoundReading = "";
var countFnKJ = 0;
var countFnKN = 0;
var countOFrm = 0;
var countUnsorted = 0;
var renderedSteps = [];
var currentStep = 0;
var currentSectionIndex = 1;
var currentSectionRubies = [];
var shouldRefillSectionRubies = true;
var shouldAddChallengesArea = true;
var isFinished = false;
var lapsStore = [];
var lapNr = 0;
var currentRtId = 1000;
var lastRtId = 999;
var confusions = extractConfusions();


function debug(stage) {
    console.log(stage + " DEBUG", {
        sectionsLength: sections.length,
        renderedSteps: renderedSteps,
        currentStep: currentStep,
        currentSectionIndex: currentSectionIndex,
        currentSectionRubies: currentSectionRubies,
        shouldAddChallengesArea: shouldAddChallengesArea,
        shouldRefillSectionRubies: shouldRefillSectionRubies
    });
}

function nextStep() {
    // while (!isFinished) {
    if (!isFinished) {
        var html;
        if (currentStep === 0) {
            html = $(renderedSteps[0]);
            html.find("#compound-reading").html(createSolution());
            $('#words-toggle').html(html);
            pushToRenderedSteps(html);
            stopwatch.stop();
            // addLap();
            return;
        }
        stopwatch.start();

        if (shouldAddChallengesArea) {
            html = "" +
                "<div id='frontpage-challenges'>" +
                "   <div id='frontpage-fnkj'></div>" +
                "   <div id='frontpage-fnkn'></div>" +
                "   <div id='frontpage-ofrm'></div>" +
                "   <div id='frontpage-conf'></div>" +
                "   <div id='frontpage-done'></div>" +
                "</div>";
            $('#words-toggle').html(html);
            // incrementSteps(); mmh... works also without this?
            shouldAddChallengesArea = false;
            addClickEvents();
        }

        if (shouldRefillSectionRubies) {
            currentSectionRubies = [];
            while (currentSectionRubies.length === 0 && currentSectionIndex <= 3) {
                currentSectionRubies = getRubyElements(sections[currentSectionIndex]);
                shouldRefillSectionRubies = false;
                if (currentSectionRubies.length === 0) {
                    skipSection();
                }
            }
        }

        switch (currentSectionIndex) {
            case 1:
                appendNextWord('#frontpage-fnkj');
                updateShouldRefill();
                checkIfShouldUpdateRenderedSteps();
                break;
            case 2:
                appendNextWord('#frontpage-fnkn');
                updateShouldRefill();
                checkIfShouldUpdateRenderedSteps();
                break;
            case 3:
                appendNextWord('#frontpage-ofrm');
                updateShouldRefill();
                checkIfShouldUpdateRenderedSteps();
                break;
            case 4:
                revealLastRtId();
                currentSectionIndex++;
                createConfusionStep();
                break;
            default:
                showConfusionStepSolution();
                var wordField = $('#frontpage-done');
                // wordField.html("<hr><div class='compound-reading-solution'>" + compoundReading + "</div>");
                stopwatch.stop();
                wordField.html("<hr><div class='compound-reading-solution'>" + parseFloat(stopwatch.get()) + "s</div>");
                isFinished = true;
        }
    }
}

function createSolution() {
    var solution = "";

    // Concept
    var concept = $('#concept').html();
    if (concept === "") {
        concept = "No concept set";
    }
    solution += concept + "<hr>";

    // Compound Reading
    solution += getMnemonicsSolution();
    solution += compoundReading;

    return solution;
}

function getMnemonicsSolution() {
    var kana = "([\u4E00-\u9FAF\u3040-\u3096\u30A1-\u30FA\uFF66-\uFF9D\u31F0-\u31FF]+\.?)+";
    var readingHtml = mnemonicField.html();
    var readingRegex = new RegExp(kana + " - .*?<br\/?>");
    var readings = readingRegex.exec(readingHtml);
    if (readings == null) {
        return "";
    }

    var html = "<div class='mnemonic-solution'><table>";
    while (readings != null) {
        var split = readings[0].split(" - ");
        html += "<tr>";
        html += "<td class='nowrap'>" + split[0] + "</td>";
        html += "<td>" + split[1] + "</td>";
        html += "</tr>";
        readingHtml = readingHtml.replace(readingRegex, "");
        readings = readingRegex.exec(readingHtml);
    }
    html += "</table></div><hr>";
    return html;
}

function createConfusionStep() {
    var html = "<div class='challenge'>";
    for (var key in confusions) {
        if (confusions.hasOwnProperty(key)) {
            var conf = confusions[key];
            html += conf.kj + "　";
        }
    }
    html = html.trim();
    html += "</div>";

    $('#frontpage-conf').html(html);
}

function showConfusionStepSolution() {
    $('#frontpage-conf').html(getConfusionHtml())
}

function addLap() {
    // Count laps
    var laps = $("#laps");
    var currentTime = parseFloat(stopwatch.get());

    var pastLaps = 0;
    $.each(lapsStore, function (index, value) {
        pastLaps += value;
    });
    var lap = currentTime - pastLaps;
    lapsStore.push(lap);

    var total = 0;
    $.each(lapsStore, function (index, value) {
        total += value;
    });

    // Choose coloring
    var color = "";
    if (lap < 5.0) {
        color = "green";
    } else if (lap < 10.0) {
        color = "orange";
    } else {
        color = "red";
    }

    if (lap > 0) {
        lapNr++;
        laps.html(laps.html() + "<div style='color: " + color + "'><span style='color:black'>" + lapNr + ". </span>" + lap.toFixed(1) + "</div>");
    }
    $("#laps-total").html(total.toFixed(1));
}

function addStopWatchClickEvents() {
    $('#words-container').click(function () {
        addLap();
        // stopwatch.stop();
    });
    $('#stopwatch').click(function () {
        addLap();
        // stopwatch.stop();
    });
}

function toggleSolution(cssId) {
    $('#' + cssId + ' rt').each(function () {
        $(this).toggle();
    });
    $('#' + cssId + ' span').each(function () {
        $(this).toggle();
    });
}

function appendNextWord(cssSelector) {
    var wordField = $(cssSelector);
    var html = currentSectionRubies.shift();
    var split = html.split("(");
    html = split[0].replace(/<rt>/, "<rt class='" + currentRtId + "' style='display: none'>") + "<div class='" + currentRtId + "' style='font-size:0.8em; margin-top:-2px; display: none'>(" + split.slice(1, split.length).join("(") + "</div>";

    wordField.html(wordField.html() + "<div class='challenge'>" + html + "</div>");
    revealLastRtId();
}

function revealLastRtId() {
    currentRtId++;
    $('.' + lastRtId).toggle();
    lastRtId++;
}

function updateShouldRefill() {
    if (currentSectionRubies.length === 0) {
        shouldRefillSectionRubies = true;
        currentSectionIndex++;
    }
}

function skipSection() {
    shouldRefillSectionRubies = true;
    currentSectionIndex++;
}

function incrementSteps() {
    currentStep++;
    updateStepButton();
}

function updateStepButton() {
    $('#step-back').html("Back (" + currentStep + ")");
}

function resetFrontpage() {
    currentStep = 0;
    currentSectionIndex = 1;
    shouldRefillSectionRubies = true;
    shouldAddChallengesArea = true;
    $('#words-toggle').html(renderedSteps[0]);
    updateStepButton();
    stopwatch.reset();
    stopwatch.start();
    isFinished = false;
    $("#laps").html("");
    $("#laps-total").html("");
    lapsStore = [];

    $('#words-container').off();
    $('#stopwatch').off();
    // addStopWatchClickEvents();
    renderConfusions();
}

function stepBack() {
    if (currentStep === 0) {
        return;
    }

    if (currentStep > 1) {
        if (currentSectionIndex > 1) {
            currentSectionIndex--;
        }
        shouldRefillSectionRubies = true;
    }
    if (currentStep === 2) {
        shouldAddChallengesArea = true;
        shouldRefillSectionRubies = true;
    }

    currentStep--;
    $('#words-toggle').html(renderedSteps[currentStep]);
    if (renderedSteps.length > 2) {
        renderedSteps.pop();
    }
    updateStepButton();
}

function checkIfShouldUpdateRenderedSteps() {
    if (currentSectionRubies.length === 0) {
        pushToRenderedSteps();
    }
}

function pushToRenderedSteps(html) {
    if (html) {
        renderedSteps.push($(html));
    } else {
        renderedSteps.push($('#words-toggle').html());
    }
    incrementSteps();
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

function renderConfusions() {
    var line = "";
    for (var key in confusions) {
        if (confusions.hasOwnProperty(key)) {
            var conf = confusions[key];
            line += conf.kj + " ";
        }
    }
    confusionQuestion.html(line);
    confusionQuestion.click(renderConfusionSolution);
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

function renderConfusionSolution() {
    if (Object.keys(confusions).length > 0) {
        confusionQuestion.html(getConfusionHtmlShort());
    }
}

function getConfusionHtml() {
    var html = "<div class='confusion-solution'>";
    html += "<table>";
    for (var key in confusions) {
        if (confusions.hasOwnProperty(key)) {
            var conf = confusions[key];

            var meaning = conf.cp ? conf.cp : conf.rtk;
            html += "" +
                "<tr>" +
                "   <th>" +
                "       <table style='width: 100%'>" +
                "           <tr>" +
                "               <td style='width: 15%'>" + conf.kj + "</td>" +
                "               <td style='width: 25%'>" + conf.r + "</td>" +
                "               <td style='width: 60%'>" + meaning + "</td>" +
                "           </tr>" +
                "       </table>" +
                "   </th>" +
                "</tr>";

            html += "" +
                "<tr>" +
                "   <td>" + conf.m + "</td>" +
                "</tr>";
        }
    }
    html += "</table>";
    html += "</div>";
    return html;
}

function getConfusionHtmlShort() {
    var html = "<div class='confusion-solution'>";
    for (var key in confusions) {
        if (confusions.hasOwnProperty(key)) {
            var conf = confusions[key];
            var meaning = conf.cp ? conf.cp : conf.rtk;
            console.log(conf.cp)
            meaning = meaning.replace(/<br>.*/, "")
            html += conf.kj + " (" + conf.r + ", " + meaning + ")<br>";
        }
    }
    html += "</div>";
    return html;
}

function printExtractRuby(shouldShuffle) {
    var words = [];
    $.each(mnemonicField.find('ruby'), function (index, value) {
        words.push(value.innerHTML.replace(/<.*>/, "") + "<br>");
    });
    if (shouldShuffle === true) {
        $('#front-page-words').html(shuffle(words).join(""));
    } else {
        $('#front-page-words').html(words.join(""));
    }
}

function toggleFrontPageWordsSection() {
    $('#front-page-words-section').toggle();
    $('#words-toggle').toggle();
}

function shuffle(a) {
    var j, x, i;
    for (i = a.length - 1; i > 0; i--) {
        j = Math.floor(Math.random() * (i + 1));
        x = a[i];
        a[i] = a[j];
        a[j] = x;
    }
    return a;
}

function addShowWords() {
    // try {
    sections = mnemonicField.html().split(/---+/);
    var playSounds = playSoundField.text().trim();
    var playSoundReadings = playSounds.split('.');
    var requiredCompoundReadingsCount = playSounds !== "" ? playSoundReadings.length : 0;
    // var existingCompoundReadingsCount = countReadingsFromPlaySoundInSection(playSoundReadings, getReadingsFromSection(sections[1]));

    // Crap. This way you won't get readings from other sections.
    // var firstSectionWithRuby = 1;
    // $.each(sections, function (index, value) {
    //     if (index > 0 && value.indexOf("<ruby>") > -1) {
    //         firstSectionWithRuby = index;
    //         return false;
    //     }
    // });
    // compoundReading = getReadingsFromPlaySoundInSection(playSoundReadings, getReadingsFromSection(sections[firstSectionWithRuby])).join(".");
    var firstSectionWithRuby = 1;
    $.each(sections, function (index, value) {
        if (index > 0 && value.indexOf("<ruby>") > -1) {
            firstSectionWithRuby = index;
            return false;
        }
    });
    compoundReading = getReadingsFromPlaySoundInSection(playSoundReadings, getReadingsFromSection(sections.join("\n"))).join(".");

    countFnKJ = countRubyElements(sections[1]);
    countFnKN = countRubyElements(sections[2]);
    countOFrm = countRubyElements(sections[3]);
    countUnsorted = countRubyElements(sections[4]);

    var table = "<table>";
    table += "<tr><th colspan='2' id='compound-reading'>" + maskCompoundReading(compoundReading) + "</th></tr>";
    table += "<tr><td colspan='2'></td></tr>";
    if (sections[1] && countFnKJ > 0) {
        table += "<tr id='frontpage-fnkj'><td colspan='2'>" + countFnKJ + "</td></tr>";
    }
    if (sections[2] && countFnKN > 0) {
        table += "<tr id='frontpage-fnkn'><td colspan='2'>" + countFnKN + "</td></tr>";
    }
    if (sections[3] && countOFrm > 0) {
        table += "<tr id='frontpage-ofrm'><td colspan='2'>" + countOFrm + "</td></tr>";
    }
    // Unsorted is for notes. Not learning words.
    // if (sections[4] && countUnsorted > 0) {
    //     table += "<tr id='frontpage-notes'><td colspan='2'>" + countUnsorted + "</td></tr>";
    // }

    // var total = countFnKJ + countFnKN + countOFrm;
    // table += "<tr><td class='border-top section-total'>" + total + "</td></tr>";
    table += "</table>";
    renderedSteps.push(table);
    $('#words-toggle').html(renderedSteps[0]).toggle();
    // } catch (e) {
    //     $('#debug').text("error: " + e)
    // }
}


function maskCompoundReading(compoundReadings) {
    if (compoundReadings.trim() === "") {
        return "No &lt;ruby&gt; elements found<br>" +
            "or no readings match readings<br>" +
            "in PlaySound field";
    }
    return compoundReadings.replace(/[^\\.]+/g, "⭘");
}

function countRubyElements(section) {
    var regex = new RegExp(/(# ?)?<ruby>.*\)/i);
    var match = regex.exec(section);

    if (section) {
        section = section.replace(/<br>/g, "\n");
    }

    var count = 0;
    while (match != null) {
        if (match[0].indexOf("#") !== 0) {
            count++;
        }
        section = section.replace(regex, "");
        match = regex.exec(section);
    }
    return count;
}

function getRubyElements(section) {
    var regex = new RegExp(/(# ?)?<ruby>.*\)/i);

    if (section) {
        section = section.replace(/<br>/g, "\n");
    }
    var match = regex.exec(section);
    var rubies = [];
    while (match != null) {
        if (match[0].indexOf("#") !== 0) {
            rubies.push(match[0]);
        }
        section = section.replace(regex, "");
        match = regex.exec(section);
    }
    return rubies;
}


function getReadingsFromSection(section) {
    var regex = /\((\W{1,5}),/;

    section = section.replace(/<br\/?>/g, "\n");
    section = cleanFromCommentedOutRubies(section);

    var match = regex.exec(section);
    var readings = [];
    while (match != null) {
        if (match[1] && readings.indexOf(match[1]) === -1) {
            readings.push(match[1]);
        }
        section = section.replace(regex, "");
        match = regex.exec(section);
    }
    return readings;
}

function cleanFromCommentedOutRubies(section) {
    var regex = new RegExp(/(# ?)<ruby>.*\)/i);
    var match = regex.exec(section);
    while (match != null) {
        if (match[0].indexOf("#") === 0) {
            section = section.replace(regex, "");
        }
        match = regex.exec(section);
    }
    return section;
}

function getReadingsFromPlaySoundInSection(playSoundReadings, sectionReadings) {
    var compoundReading = [];
    $.each(playSoundReadings, function (i1, playSoundReading) {
        $.each(sectionReadings, function (i2, sectionReading) {
            if (toHiragana(playSoundReading) === toHiragana(sectionReading)) {
                compoundReading.push(toHiragana(playSoundReading));
            }
        });
    });

    return compoundReading;
}

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


document.onkeyup = function (e) {
    if (e.which === 81) {
        toggleFrontPageWordsSection();
    }
};


var Stopwatch = function (elem, options) {

    var timer = createTimer(),
        offset,
        clock,
        interval;

    // default options
    options = options || {};
    options.delay = options.delay || 100;

    // append elements
    elem.appendChild(timer);

    // initialize
    reset();

    // private functions
    function createTimer() {
        return document.createElement("span");
    }

    function start() {
        if (!interval) {
            offset = Date.now();
            interval = setInterval(update, options.delay);
        }
    }

    function stop() {
        if (interval) {
            clearInterval(interval);
            interval = null;
        }
    }

    function reset() {
        clock = 0;
        render(0);
    }

    function update() {
        clock += delta();
        render();
    }

    function get() {
        return (clock / 1000).toFixed(1);
    }

    function render() {
        timer.innerHTML = (clock / 1000).toFixed(1);
    }

    function delta() {
        var now = Date.now(),
            d = now - offset;

        offset = now;
        return d;
    }

    // public API
    this.start = start;
    this.stop = stop;
    this.reset = reset;
    this.get = get;
};

function addClickEvents() {
    $('#frontpage-fnkj').click(function (e) {
        toggleSolution('frontpage-fnkj');
        e.stopPropagation();
    });
    $('#frontpage-fnkn').click(function (e) {
        toggleSolution('frontpage-fnkn');
        e.stopPropagation();
    });
    $('#frontpage-ofrm').click(function (e) {
        toggleSolution('frontpage-ofrm');
        e.stopPropagation();
    });
}

// todo crap and buggy and useless.
function addStepBack() {
    $('#frontpage-fnkj').click(function (e) {
        stepBack();
        e.stopPropagation();
    });
}

function addResetButtonClickEvent() {
    $('#reset-button').click(function (e) {
        resetFrontpage();
        e.stopPropagation();
    });
}

// addStopWatchClickEvents();
addShowWords();
renderFonts(1, 5);
renderConfusions();
printExtractRuby(false);
toggleFrontPageWordsSection();
// addStepBack(); crap.
addResetButtonClickEvent();
var stopwatch = new Stopwatch(document.getElementById('stopwatch-total'));
stopwatch.start();

