(function(){
    var days = document.querySelectorAll(".naziv-dan");
    var i;
    for(i = 0; days.length; i++){
        var day = days[i];
        if(day.querySelector("p").innerText.includes('%s')){
            return days[i].getBoundingClientRect().top + window.scrollY;
        }
    }
})();