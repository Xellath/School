$(document).ready(function(){
    getTheme();
    $("#theme-questions").hide();
    $("#theme-no-questions").hide();
    $("#submit-edit-button").hide();
    $("#inputEditTheme").hide();
});

var toggle = true;
$("#edit-button").on("click", function() {
    if($("#theme-select").val() != "") {
        if(toggle) {
            $("#submit-edit-button").fadeIn();
            $("#inputEditTheme").val("").fadeIn();
            $("#edit-button").removeClass("fa-edit").addClass("fa-close");
        } else {
            $("#submit-edit-button").fadeOut();
            $("#inputEditTheme").fadeOut();
            $("#edit-button").removeClass("fa-close").addClass("fa-edit");
        }

        toggle = !toggle;
    }
});

$("#submit-edit-button").on("click", function() {
    var newTheme = $("#inputEditTheme").val();
    $("#submit-edit-button").fadeOut();
    $("#inputEditTheme").fadeOut();

    $("#edit-button").removeClass("fa-close").addClass("fa-edit");
    toggle = !toggle;

    var selectedTheme = $("#theme-select").val();
    var body = {
        'theme': newTheme
    };

    var data = JSON.stringify(body);
    $.ajax({
        type: "PUT",
        url: "http://"+url+"/api/themes/" + selectedTheme,
        data: data,
        contentType: "application/json",
        dataType: "json",
        beforeSend: function(xhr) {
            xhr.setRequestHeader('Authorization', 'key='+apiKey+',user=' + localStorage.getItem("user") + ',password=' + localStorage.getItem("password"));
        },
        statusCode: {
            200: function (response){
                $("#success").html("<strong>Lyckades!</strong> Uppdatering av tema lyckades!").fadeIn();

                setTimeout(function(){
                    $("#success").fadeOut();
                }, 5000);

                $("#theme-group").empty();
                getTheme();

                $("#theme-questions").hide();
                $("#theme-no-questions").hide();
                $("#question-list").hide();
            },
            400: function (response){
                $("#error").html("<strong>Misslyckades!</strong> Uppdatering av tema misslyckades!").fadeIn();

                setTimeout(function(){
                    $("#error").fadeOut();
                }, 5000);
            },
            415: function (response){
                $("#error").html("<strong>Misslyckades!</strong> Uppdatering av tema misslyckades!").fadeIn();

                setTimeout(function(){
                    $("#error").fadeOut();
                }, 5000);
            }
        }
    });
});

var questions = [];
var promises = [];

$("#theme-select").on('changed.bs.select', function(event, clickedIndex, newValue, oldValue) {
    $.LoadingOverlay("show");
    var selectedTheme = $("#theme-select").val();
    populateQuestionList(selectedTheme);
    $("#warning").hide();
});

$("#trash-button").on("click", function() {
    var theme = $("#theme-select").val();
    if(theme != ""){
        $("#trash-theme-text").text('Temat "' + theme + '" och alla tillhörande frågor kommer att tas bort, vill du fortsätta?');
        $("#warning").show();
    }
});

$("#regret-trash-button").on("click", function() {
  $("#warning").hide();
});

$("#trash-theme-button").on("click", function() {
    var theme = $("#theme-select :selected");
    var themeValue = $("#theme-select").val();

    $("#theme-questions").hide();
    $("#theme-no-questions").hide();
    $("#question-list").hide();

    $.ajax({
        type: "DELETE",
        url: "http://"+url+"/api/themes/" + themeValue,
        contentType: "application/json",
        dataType: "json",
        beforeSend: function(xhr) {
            console.log(apiKey);
            xhr.setRequestHeader('Authorization', 'key='+apiKey+',user=' + localStorage.getItem("user") + ',password=' + localStorage.getItem("password"));
        },
        statusCode: {
            204: function(response) {
                theme.remove();
                $("#theme-select").selectpicker('refresh');
                $("#success").html("<strong>Lyckades!</strong> Borttagning av tema lyckades!").fadeIn();

                setTimeout(function(){
                    $("#success").fadeOut();
                }, 5000);
            },
            400: function(response) {
                $("#error").html("<strong>Misslyckades!</strong> Borttagning av tema misslyckades!").fadeIn();

                setTimeout(function(){
                    $("#error").fadeOut();
                }, 5000);
            }
        }
    });
    $("#warning").hide();
});


$("#question-list").on("click", "button.btn.btn-sm.btn-warning", function() {
    var id = $(this).data("id");

    $('#inputTheme').val($("#theme-select").val());
    $("#theme-selectModal").selectpicker("val", $("#theme-select").val());
    $('#inputQuestion').val(questions[id].question);
    $('#inputCorrectAnswer').val(questions[id].correctanswer);

    $('#wrongAnswers').empty();
    var incorrectAnswers = questions[id].incorrectanswers;
    var i = 0;
    for(i = 0; i < incorrectAnswers.length; i++) {
        $('#wrongAnswers').append(wrongAnswerElement(incorrectAnswers[i]));
    }

    $('#updateQuestion').data('id', questions[id].id);

    $('#image').hide();
    $('#audio').hide();
    $('#video').hide();
    switch(questions[id].mediaType) {
        case "image":
        $('#image').attr("src", "http://" + questions[id].mediaURL).show();
        break;
        case "audio":
        $('#audio').attr("src", "http://" + questions[id].mediaURL).show();
        break;
        case "video":
        $('#video').attr("src", "http://" + questions[id].mediaURL).show();
        break;
    }
});

function wrongAnswerElement(answer) {
    return '<input type="text" class="form-control wrongAnswerForm" maxlength="140" value="' + answer + '" placeholder="Ange fel svar..." data-validation="length" data-validation-length="min1">';
}

$("#question-list").on("click", "button.btn.btn-sm.btn-danger", function() {
    var element = $(this);
    var id = $(this).data("id");
    $.ajax({
        type: "DELETE",
        url: "http://"+url+"/api/questions/" + questions[id].id,
        contentType: "application/json",
        dataType: "json",
        beforeSend: function(xhr) {
            xhr.setRequestHeader('Authorization', 'key='+apiKey+',user=' + localStorage.getItem("user") + ',password=' + localStorage.getItem("password"));
        },
        statusCode: {
            204: function(response) {
                $("#success").html("<strong>Lyckades!</strong> Borttagning av fråga lyckades!").fadeIn();

                element.parent().parent().remove();

                setTimeout(function(){
                    $("#success").fadeOut();
                }, 5000);
            },
            400: function(response) {
                $("#error").html("<strong>Misslyckades!</strong> Borttagning av fråga misslyckades!").fadeIn();

                setTimeout(function(){
                    $("#error").fadeOut();
                }, 5000);
            }
        }
    });
});

function populateQuestionList(theme) {
    $("#question-list").empty().show();
    $("#theme-questions").hide();
    $("#theme-no-questions").hide();
    questions = [];
    promises = [];

    $.ajax({
        type: "GET",
        url: "http://"+url+"/api/themes/" + theme,
        contentType: "application/json",
        dataType: "json",
        beforeSend: function(xhr) {
            xhr.setRequestHeader('Authorization', 'key='+apiKey);
        },
        success: function(data) {
            $("#theme-questions").fadeIn();
            if(data.length == 0) {
                $("#theme-no-questions").fadeIn();
            }

            var id = 0;
            for (id = 0; id < data.length; id++) {
                $("#question-list").append(listElement(data[id], id));
                questions.push(data[id]);
            }

            $.LoadingOverlay("hide");
        },
        failure: function(data) {
            $.LoadingOverlay("hide");
        }
    });
}

function listElement(question, id) {
    return  '<div class="list-group-item clearfix">' +
    '<span class="fa fa-question-circle pull-left"></span>' +
    '<span class= "question-list-text">' + question.question +
    '</span>' +
    '<span class="pull-right">' +
    '<button class="btn btn-sm btn-warning" data-id="' + id + '" data-toggle="modal" data-target="#edit-modal">' +
    '<span class="fa fa-edit"></span>' +
    '</button>' +
    '<button class="btn btn-sm btn-danger" data-id="' + id + '">' +
    '<span class="fa fa-trash"></span>' +
    '</button>' +
    '</span>' +
    '</div>';
}

$("#updateQuestion").on("click", function() {
    var id = $(this).data('id');
    questionCreation(id);
});

function questionCreation(id) {
    var question = $("#inputQuestion").val();
    var correctAnswer = $("#inputCorrectAnswer").val();
    var inputWrongAnswer = $(".wrongAnswerForm").val();
    var theme = $("#theme-selectModal").val();
    var inputs = document.getElementsByClassName( 'wrongAnswerForm' ),
    wrongAnswers  = [].map.call(inputs, function( input ) {
        return input.value;
    });

    var body = {};
    if($('#removeMedia').prop('checked')) {
        body = {
            'id': id,
            'question': question,
            'theme': theme,
            'correctanswer': correctAnswer,
            'incorrectanswers': wrongAnswers,
            'mediaAction': "delete"
        };
    } else {
        body = {
            'id': id,
            'question': question,
            'theme': theme,
            'correctanswer': correctAnswer,
            'incorrectanswers': wrongAnswers,
            'media': base64Image
        };
    }
    updateQuestion(id, body);
    $('#removeMedia').prop('checked', false);
}

function updateQuestion(id, body){
    var data = JSON.stringify(body);
    $.ajax({
        type: "PUT",
        url: "http://"+url+"/api/questions/" + id,
        data: data,
        contentType: "application/json",
        dataType: "json",
        beforeSend: function(xhr) {
            xhr.setRequestHeader('Authorization', 'key='+apiKey+',user=' + localStorage.getItem("user") + ',password=' + localStorage.getItem("password"));
        },
        statusCode: {
            200: function (response){
                $("#success").html("<strong>Lyckades!</strong> Uppdatering av fråga lyckades!").fadeIn();

                setTimeout(function(){
                    $("#success").fadeOut();
                }, 5000);

                $('#edit-modal').modal('hide');
                $.LoadingOverlay("show");
                var selectedTheme = $("#theme-select").val();
                populateQuestionList(selectedTheme);
            },
            415: function (response){
                $("#error").html("<strong>Misslyckades!</strong> Uppdatering av fråga misslyckades!").fadeIn();

                setTimeout(function(){
                    $("#error").fadeOut();
                }, 5000);
            }
        }
    });
}
