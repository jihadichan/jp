var timeoutMarkerEvent;

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
    var container = document.getElementById("sentence-container");
    var html = container.innerHTML;

    var split = html.split(/<br\s?\/?><br\s?\/?>/);
    if (html.indexOf("!e") === -1) {
        // Show sentence
        html = split[1];
    } else {
        // Show focus
        if(split.length !== 2) {
            document.getElementById("debug").innerText = "Malformed sentence field. Contains !e but doesn't follow pattern [focus]<br><br>[sentence]";
        }
        html = "<div class='focus'>FOCUS</div>" + split[0];
    }
    container.innerHTML = hideMarker(html);
}

function hideMarker(text) {
    return text.replace("!e", "");
}

// startTimeoutMarker(); todo no time on front page?
renderSentence();
