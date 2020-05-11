var concept = $('#grammatical_concept');
var formation = $('#formation');

function renderGrammaticalConcept() {
    var copy = concept.html();

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

    $('#container').html("<div id=\"grammatical_concept_fld\">"+html+"</div>");
}

function renderFormation() {
    formation.find("table tr td:nth-child(3)").each(function (index, value) {
        $(this).html("");
    });
    $('#container').html("<div id=\"formation_fld\">"+formation.html()+"</div>");
}

function renderFrontPage() {
    if(formation.html().trim() !== "") {
        renderFormation();
    } else {
        renderGrammaticalConcept();
    }
}

renderFrontPage();

