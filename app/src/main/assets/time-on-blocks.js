(function(){
    var times = [];
    $(".blokovi span.hide").each(function(){
        times.push($(this).text().split('\n')[2])
    });
    var paragraphs = $(".blokovi .thumbnail p");
    paragraphs.each(function(idx, p){
        $(p).append("<br/>" + times[idx])
    });
}());
