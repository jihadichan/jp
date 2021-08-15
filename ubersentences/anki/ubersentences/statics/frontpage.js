var timeoutMarkerEvent;
var originalSentenceField = "";
var isDisplayFieldEmpty = true;

function replaySentence() {
    var filePath = "ubersentences/mp3/" + $('#mp3').text();
    try {
        var audio = new Audio(filePath);
        audio.play();
    } catch (err) {
        $('#debug').text("Couldn't play " + filePath + ". Either file does not exist or playback failed.");
    }
}

function startTimeoutMarker() {
    if (timeoutMarkerEvent !== undefined) {
        clearTimeout(timeoutMarkerEvent);
    }
    var container = document.getElementById("sentence-container");

    // Count focus works
    var lines = container.innerHTML.split(/<br\/?>/);
    if (lines.length === 1) { // only the sentence, so it's a new card
        return;
    }
    for (var i = 0; i < lines.length; i++) {
        if (lines[i] === "") {
            break;
        }
    }
    var timeout = i * 3000;
    timeoutMarkerEvent = setTimeout(function () {
        var html = container.innerHTML;
        html += "<div class='timeout'>TIMEOUT (" + timeout / 1000 + "s)</div>"
        container.innerHTML = html;
    }, timeout);
}

function renderSentence() {
    var html = getRawOrDisplayField();
    originalSentenceField = html;

    var split = html.split(/<br\s?\/?><br\s?\/?>/);
    if (split.length === 2) {
        // Has <br><br>, so show focus
        html = "<div class='focus'>FOCUS</div>" + split[0];
    } else if (split.length === 1) {
        // Has NO <br><br>, so show full sentence
        if(isDisplayFieldEmpty) {
            html = split[0];
        } else {
            html = "<div class='focus'>DISPLAY</div>" + split[0];
        }
    } else {
        document.getElementById("debug").innerText = "Malformed sentence field. Contains <br><br> but doesn't follow pattern [focus]<br><br>[sentence]";
    }

    document.getElementById("sentence-container").innerHTML = html;
}

function getRawOrDisplayField() {
    var display = document.getElementById('sentence-display').innerHTML;
    if(display.trim() !== "") {
        isDisplayFieldEmpty = false;
        return display;
    }
    return document.getElementById('sentence-raw').innerHTML;
}

function showOriginalSentenceField() {
    document.getElementById("sentence-container").innerHTML = originalSentenceField;
}

function addClickEvents() {
    document.getElementById("container").addEventListener("click", showOriginalSentenceField);
}

startTimeoutMarker();
addClickEvents();
renderSentence();
