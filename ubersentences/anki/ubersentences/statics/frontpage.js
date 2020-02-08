function replaySentence() {
    var filePath = "ubersentences/mp3/" + $('#mp3').text();
    try {
        var audio = new Audio(filePath);
        audio.play();
    } catch (err) {
        $('#debug').text("Couldn't play " + filePath + ". Either file does not exist or playback failed.");
    }
}
