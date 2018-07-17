$(document).ready(function(){
    getTheme();
});
//Global variables
//--------------------------
var isThemeSelected = false;
var theSelectedTheme;
//--------------------------

$('#theme-select').on('changed.bs.select', function (event, clickedIndex, newValue, oldValue) {
    $("#inputTheme").val("").prop("disabled", true).fadeOut();
    isThemeSelected = true;
    if(clickedIndex == 1) {
        $("#inputTheme").val("").prop("disabled", false).fadeIn();

        isThemeSelected = false;
    }
});
$(function () {
    $('[data-toggle="tooltip"]').tooltip()
})
var text_max = 140;
$('#count_message').html(0 + ' av '+text_max + " tecken");
var fileInput;

$('#inputQuestion').keyup(function() {
    var text_length = $('#inputQuestion').val().length;
    var text_remaining = text_max - text_length;
    $('#count_message').html(text_length + " av " + text_max + " tecken");

});

$("#btnSend").on("click", function(){
    var inputTheme = $('#inputTheme').val();
    var inputQuestion = $("#inputQuestion").val();
    var inputCorrectAnswer = $("#inputCorrectAnswer").val();
    var inputWrongAnswer = $(".wrongAnswerForm").val();
    var media = $("input.file").val();

    //Check if selected theme or adding new theme
    if(isThemeSelected){
        theSelectedTheme = $("#theme-select").val();
        questionCreation();
    }else if(!isThemeSelected){
        var body = {
            "theme": inputTheme
        };

        $.when.apply(this, sendTheme(body)).then(function(data) {
            //Get themes and check if the theme is aviable

            var themeArray = allThemes.split(",");
            if(jQuery.inArray(inputTheme, themeArray) == -1){
                theSelectedTheme = inputTheme;
                questionCreation();
            }else{
                $("#error").html("<strong>Misslyckades!</strong> Temat existerar redan (om temat redan är skapat, välj det i dropdown-boxen)!").fadeIn();
                setTimeout(function(){
                    $("#error").fadeOut();
                }, 5000);
                $('html, body').animate({
                    scrollTop: $(".nav").offset().top
                }, 800);
            }

            getTheme();
        });
    }
});

function validationMessage(errorMessage){

}

//Creating question as JSON and sending it to the ajax function
function questionCreation(){
    var question = $("#inputQuestion").val();
    var correctAnswer = $("#inputCorrectAnswer").val();
    var inputWrongAnswer = $(".wrongAnswerForm").val();
    var theme = theSelectedTheme;
    var inputs = document.getElementsByClassName( 'wrongAnswerForm' ),
        wrongAnswers  = [].map.call(inputs, function( input ) {
            return input.value;
        });

    var body = {
        'question': question,
        'theme': theme,
        'correctanswer': correctAnswer,
        'incorrectanswers': wrongAnswers,
        "media": base64Image
    };
    sendQuestion(body);
}
//Ajax function to send the theme JSON
function sendTheme(body) {
    var data = JSON.stringify(body);
    return $.ajax({
        type: "POST",
        url: "http://"+url+"/api/themes",
        data: data,
        contentType: "application/json",
        dataType: "json",
        beforeSend: function(xhr) {
            xhr.setRequestHeader('Authorization', 'key='+apiKey);
        }
    });
}
//Ajax function to send the question JSON
function sendQuestion(body){
    $.LoadingOverlay("show");
    var data = JSON.stringify(body);
    $.ajax({
        type: "POST",
        url: "http://"+url+"/api/questions",
        data: data,
        contentType: "application/json",
        dataType: "json",
        beforeSend: function(xhr) {
            xhr.setRequestHeader('Authorization', 'key='+apiKey);
        },
        statusCode: {
            201: function (response){
                $.LoadingOverlay("hide");

                $("#success").html("<strong>Lyckades!</strong> Insättning av fråga lyckades!").fadeIn();
                setTimeout(function(){
                    $("#success").fadeOut();
                }, 5000);
                $('html, body').animate({
                    scrollTop: $(".nav").offset().top
                }, 800);

            },
            400: function (response){
                $.LoadingOverlay("hide");
                $("#error").html("<strong>Misslyckades!</strong> Insättning av fråga misslyckades!").fadeIn();
                setTimeout(function(){
                    $("#error").fadeOut();
                }, 5000);
                $('html, body').animate({
                    scrollTop: $(".nav").offset().top
                }, 800);
            },
            415: function (response){
                $.LoadingOverlay("hide");
                $("#error").html("<strong>Misslyckades!</strong> Insättning av fråga misslyckades!").fadeIn();
                setTimeout(function(){
                    $("#error").fadeOut();
                }, 5000);
                $('html, body').animate({
                    scrollTop: $(".nav").offset().top
                }, 800);
            },
            500: function (response){
                $.LoadingOverlay("hide");
                $("#error").html("<strong>Misslyckades!</strong> Insättning av fråga misslyckades (kontrollera dubletter)!").fadeIn();
                setTimeout(function(){
                    $("#error").fadeOut();
                }, 5000);
                $('html, body').animate({
                    scrollTop: $(".nav").offset().top
                }, 800);
            }
        },
        complete: function(data){
        }
    });
}
