$(function () {
    "use strict";

    let results = $('#results');
    let input = $('#input');

    input.keyup(event => {
        let query = input.val();
        $.ajax({
            url: "http://localhost:8080/search?input=" + query, success: function (result) {
                results.text("");
                JSON.parse(result)["results"].map(path => 'images/' + path).forEach(x => results.append('<img height="250" src='+x+'>'))
            }
        });
    });
});