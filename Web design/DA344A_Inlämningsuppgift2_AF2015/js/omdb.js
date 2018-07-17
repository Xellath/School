
$(function(){
    $("#search-button").click(function() {
        makeRequest();
    });

    $(document).keypress(function(e) {
        if(e.which == 13) {
            makeRequest();
        }
    });
});

function makeRequest() {
    $("#success").addClass("hidden");
    $("#error").addClass("hidden");

    $.ajax({
        url: "https://www.omdbapi.com/?s=" + $("#search-query").val().trim() + "&plot=short&r=json",
        dataType: "json",
        success: function(json) {
            if(json.Response == "False") {
                $("#error").removeClass("hidden").hide().fadeIn(1000);

                setTimeout(function (){
                    $("#error").fadeOut(1000);
                }, 8000);
            } else {
                var searchResults = json.Search.length;
                $("table").bootstrapTable('destroy');
                $("table").bootstrapTable({
                    data: json.Search
                });

                $("#success").removeClass("hidden").hide().fadeIn(1000);
                $("#resultat-title").removeClass("hidden").hide().fadeIn(1000);
                $("table").removeClass("hidden").hide().fadeIn(1000);

                setTimeout(function (){
                    $("#success").fadeOut(1000);
                }, 8000);

                $("a.tooltip-icon").tooltip();
                $("a.tooltip-icon").click(function(e) {
                    e.preventDefault();
                    var aElement = $(this);
                    var iconElement = $(this).children("i");
                    var imdbId = aElement.data("imdb");
                    if(aElement.data("favorite") == true) {
                        var storedItems = localStorage.getItem("favorites");
                        if(storedItems !== null) {
                            var jsonItems = JSON.parse(storedItems);
                            getImdbEntryData(imdbId, function(output) {
                                var item = {"imdb": imdbId, "title": output.Title, "poster": output.Poster, "year": output.Year, "runtime": output.Runtime};
                                if(!containsItemF(jsonItems, imdbId)) {
                                    jsonItems.favorites.push(item);

                                    localStorage.setItem("favorites", JSON.stringify(jsonItems));

                                    aElement.attr('data-original-title', "Favorit!").tooltip('fixTitle').tooltip('show');
                                    iconElement.removeClass("fa-heart-o").addClass("fa-heart").hide().fadeIn(1000);
                                    setTimeout(function (){
                                        aElement.tooltip('hide');
                                    }, 1000);
                                }
                            });
                        } else {
                            getImdbEntryData(imdbId, function(output) {
                                var jsonItems = { "favorites": [{"imdb": imdbId, "title": output.Title, "poster": output.Poster, "year": output.Year, "runtime": output.Runtime}] };

                                localStorage.setItem("favorites", JSON.stringify(jsonItems));

                                aElement.attr('data-original-title', "Favorit!").tooltip('fixTitle').tooltip('show');
                                iconElement.removeClass("fa-heart-o").addClass("fa-heart").hide().fadeIn(1000);
                                setTimeout(function (){
                                    aElement.tooltip('hide');
                                }, 1000);
                            });
                        }
                    } else {
                        var storedItems = localStorage.getItem("archived");
                        if(storedItems !== null) {
                            var jsonItems = JSON.parse(storedItems);
                            getImdbEntryData(imdbId, function(output) {
                                var item = {"imdb": imdbId, "title": output.Title, "poster": output.Poster, "year": output.Year, "runtime": output.Runtime};
                                if(!containsItemA(jsonItems, imdbId)) {
                                    jsonItems.archived.push(item);

                                    localStorage.setItem("archived", JSON.stringify(jsonItems));

                                    aElement.attr('data-original-title', "Arkiverad!").tooltip('fixTitle').tooltip('show');
                                    iconElement.removeClass("fa-archive").addClass("fa-check-circle").hide().fadeIn(1000);
                                    setTimeout(function (){
                                        aElement.tooltip('hide');
                                    }, 1000);
                                }
                            });
                        } else {
                            getImdbEntryData(imdbId, function(output) {
                                var jsonItems = { "archived": [{"imdb": imdbId, "title": output.Title, "poster": output.Poster, "year": output.Year, "runtime": output.Runtime}] };

                                localStorage.setItem("archived", JSON.stringify(jsonItems));

                                aElement.attr('data-original-title', "Arkiverad!").tooltip('fixTitle').tooltip('show');
                                iconElement.removeClass("fa-archive").addClass("fa-check-circle").hide().fadeIn(1000);
                                setTimeout(function (){
                                    aElement.tooltip('hide');
                                }, 1000);
                            });
                        }
                    }
                });
            }
        },
        error: function(msg) {
            $("#error").removeClass("hidden").hide().fadeIn(1000);

            setTimeout(function (){
                $("#error").fadeOut(1000);
            }, 8000);
        }
    });
}

function containsItemF(jsonItems, imdb) {
    var i = null;
    for(i = 0; i < jsonItems.favorites.length; i++) {
        if(jsonItems.favorites[i].imdb == imdb) {
            return true;
        }
    }

    return false;
}

function containsItemA(jsonItems, imdb) {
    var i = null;
    for(i = 0; i < jsonItems.archived.length; i++) {
        if(jsonItems.archived[i].imdb == imdb) {
            return true;
        }
    }

    return false;
}

function getImdbEntryData(imdb, output) {
    $.ajax({
        url: "https://www.omdbapi.com/?i=" + imdb + "&plot=short&r=json",
        dataType: "json",
        success: function(json) {
            if(json.Response == "True") {
                output(json);
            }
        }
    });
}

function imageFormatter(value) {
    return '<img class="table-image" src="' + value + '" alt="Movie Poster">';
}

function titleFormatter(value) {
    return '<h3>' + value + '</h3>';
}

function actionFormatter(value) {
    var storedFItems = localStorage.getItem("favorites");
    var storedAItems = localStorage.getItem("archived");
    var itemFExists = false;
    var itemAExists = false;

    if(storedFItems !== null) {
        var jsonItems = JSON.parse(storedFItems);

        var itemFExists = containsItemF(jsonItems, value);
    }

    if(storedAItems !== null) {
        var jsonItems = JSON.parse(storedAItems);

        var itemAExists = containsItemA(jsonItems, value);
    }

    return '<a class="tooltip-icon" href="#" data-toggle="tooltip" data-placement="top" data-imdb="' + value + '" data-favorite="true" title="' + (itemFExists == true ? 'Favorit!' : 'Markera som favorit') + '">' +
                '<i class="fa ' + (itemFExists == true ? 'fa-heart' : 'fa-heart-o') + ' fa-3x"></i>' +
                '</a>' +
                '<a class="tooltip-icon " href="#" data-toggle="tooltip" data-placement="bottom" data-imdb="' + value + '" data-favorite="false" title="' + (itemAExists == true ? 'Arkiverad!' : 'Arkivera') + '">' +
                '<i class="fa ' + (itemAExists == true ? 'fa-check-circle' : 'fa-archive') + ' fa-3x"></i>' +
                '</a>';
}
