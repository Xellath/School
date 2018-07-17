
$(function(){
    var storedFavorites = localStorage.getItem("favorites");
    if(storedFavorites !== null) {
        var jsonTable = JSON.parse(storedFavorites);
        $("#favorites-table").bootstrapTable('destroy');
        $("#favorites-table").bootstrapTable({
            data: jsonTable.favorites
        });
    } else {
        $("#favorites-table").bootstrapTable();
    }

    var storedArchived = localStorage.getItem("archived");
    if(storedArchived !== null) {
        var jsonTable = JSON.parse(storedArchived);
        $("#archived-table").bootstrapTable('destroy');
        $("#archived-table").bootstrapTable({
            data: jsonTable.archived
        });
    } else {
        $("#archived-table").bootstrapTable();
    }

    $("a.tooltip-icon").tooltip();
    $(".tab-content").on("click", "a.tooltip-icon", function(e) {
        e.preventDefault();
        var aElement = $(this);
        var parentElement = $(this).parents("tr");
        var imdbId = aElement.data("imdb");
        var removalType = aElement.data("remove");

        var jsonTable = JSON.parse(localStorage.getItem(removalType));
        if(removalType === "favorites") {
            removeItemF(jsonTable, imdbId);

            $("#favorites-table").bootstrapTable('removeByUniqueId', imdbId);
        } else if(removalType === "archived") {
            removeItemA(jsonTable, imdbId);
            
            $("#archived-table").bootstrapTable('removeByUniqueId', imdbId);
        }
        localStorage.setItem(removalType, JSON.stringify(jsonTable));
    });
});

function removeItemF(jsonItems, imdb) {
    var i = null;
    for(i = 0; i < jsonItems.favorites.length; i++) {
        if(jsonItems.favorites[i].imdb == imdb) {
            jsonItems.favorites.splice(i, 1);
        }
    }
}

function removeItemA(jsonItems, imdb) {
    var i = null;
    for(i = 0; i < jsonItems.archived.length; i++) {
        if(jsonItems.archived[i].imdb == imdb) {
            jsonItems.archived.splice(i, 1);
        }
    }
}

function imageFormatter(value) {
    return '<img class="table-image" src="' + value + '" alt="Film Poster">';
}

function titleFormatter(value) {
    return '<h3>' + value + '</h3>';
}

function removeFFormatter(value) {
    return '<a class="tooltip-icon" href="#" data-toggle="tooltip" data-placement="top" data-imdb="' + value + '" data-remove="favorites" title="Ta bort">' +
                '<i class="fa fa-trash fa-3x"></i>' +
                '</a>';
}

function removeAFormatter(value) {
    return '<a class="tooltip-icon" href="#" data-toggle="tooltip" data-placement="top" data-imdb="' + value + '" data-remove="archived" title="Ta bort">' +
                '<i class="fa fa-trash fa-3x"></i>' +
                '</a>';
}
