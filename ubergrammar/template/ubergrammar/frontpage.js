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

renderGrammaticalConcept();
