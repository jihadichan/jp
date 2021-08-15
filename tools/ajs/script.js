function createStamp() {
    var input = $("#stamper-input").val();
    var output = $("#stamper-output");
    var text = Math.floor(Date.now() / 1000) + "-" + input;
    text = text.replace(/\s/g, "");
    output.val(text);
    copyToClipboard(output);
}

function copyToClipboard(element) {
    var $temp = $("<input>");
    $("body").append($temp);
    $temp.val($(element).val()).select();
    document.execCommand("copy");
    $temp.remove();
    $("#last-copy").text(element.val());
    $("#stamper-output").select();

}
