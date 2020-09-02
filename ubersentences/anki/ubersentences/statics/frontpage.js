var nextMarkerEvent;

function replaySentence() {
    var filePath = "ubersentences/mp3/" + $('#mp3').text();
    try {
        var audio = new Audio(filePath);
        audio.play();
    } catch (err) {
        $('#debug').text("Couldn't play " + filePath + ". Either file does not exist or playback failed.");
    }
}

function renderNextMarker() {
    if (nextMarkerEvent !== undefined) {
        clearTimeout(nextMarkerEvent);
    }
    var container = document.getElementById("sentence-container");

    // Count focus works
    var lines = container.innerHTML.split(/<br\/?>/);
    if(lines.length === 1) { // only the sentence, so it's a new card
        return;
    }
    for(var i = 0; i < lines.length; i++) {
        if(lines[i] === "") {
            break;
        }
    }
    var timeout = i * 3000;
    nextMarkerEvent = setTimeout(function () {
        var html = container.innerHTML;
        html += "<div class='timeout'>TIMEOUT (" + timeout / 1000 + "s)</div>"
        container.innerHTML = html;
    }, timeout);
}

renderNextMarker();
