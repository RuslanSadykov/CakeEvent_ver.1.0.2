/**
 * Created by Phaenir on 09.12.2014.
 */
$(document).ready(function() {

    // Expand Panel
    $("#open").click(function(){
        $("div#panel").slideDown("slow");

    });

    // Collapse Panel
    $("#close").click(function(){
        $("div#panel").slideUp("slow");
    });

    // Switch buttons from "Log In | Register" to "Close Panel" on click
    $("#toggle").find("a").click(function () {
        $("#toggle").find("a").toggle();
    });

});
