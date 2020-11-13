(function(){
    var observer = new MutationObserver(function(mutations) {
        if ($(".uwy").length) {
            /*userway shit was added*/
            $(".uwy").remove();
            $(".uw-s10-reading-guide").remove();
            $(".uw-s12-tooltip").remove();
            observer.disconnect();
        }
    });
    observer.observe(document.body, {childList: true, subtree: false});

    $("#pagewrap").children().not(".narrow-down").remove();
    $(".narrow-down").children().not("#content-contain").remove();
    $("#content").children().not("#raspored").remove();
    $("#raspored").children().not(".vrijeme, .vrijeme-mobitel, .dan, .odabir").remove();
    $("#raspored").children(".odabir").hide();
    $("#izbor-studija").remove();
    $(".naziv-dan a").removeAttr("href");
}());
