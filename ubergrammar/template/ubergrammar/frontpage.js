var concept = $('#grammatical_concept');
var formation = $('#formation');
var container = $('#container');
var isTranslationVisible = false;

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

    container.html("<div id=\"grammatical_concept_fld\">"+html+"</div>");
}

function sentencesAsHtml() {
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
        return "<table class='container-sentences'><tr><td><b>No sentences</b></td></tr></table>";
    }

    var html = "<table id='container-sentences'><tr><td>";
    $(sentences).each(function (index, value) {
        if(isTranslationVisible) {
            html += "<div>"+value.jp+"</div>";
            html += "<div>"+value.en+"</div><br>";
        } else {
            html += "<div>"+value.jp+"</div><br>";
        }
    });
    html += "</td></tr></table>";

    return html;
}

function renderFormation() {
    formation.find("table tr td:nth-child(3)").each(function (index, value) {
        $(this).html("");
    });
    container.html("<div id=\"formation_fld\">"+formation.html()+"</div>");
}

function renderFrontPage() {
    if(formation.html().trim() !== "") {
        renderFormation();
    } else {
        renderGrammaticalConcept();
    }
    var html = container.html();
    container.html(html + sentencesAsHtml());
    $('#container-sentences').click(rerender);
}

function rerender() {
    isTranslationVisible = !isTranslationVisible;
    renderFrontPage();

}

renderFrontPage();

