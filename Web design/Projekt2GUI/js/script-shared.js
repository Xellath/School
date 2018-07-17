
//Global variables------
var allThemes;
var base64Image;
var apiKey = "FelYueMcxy6NHU9UY1ulNKHca71deOVs";
var url = "213.67.43.138:7500";
var loginName;
var loginPassword;
//----------------------

$(document).ready(function(){
      $("#login-a").text("Logga in");
    if (localStorage.getItem("user")!= null && localStorage.getItem("password")!= null ){
        $("#logina").hide();
        $("#logouta").show();
        $("#info").hide();
    } else {
        $("#logouta").hide();
        $("#logina").show();
        $("#info").show();
    }
});

function encodeImageFileAsURL(element) {
    var file = element.files[0];
    var reader = new FileReader();
    reader.onloadend = function() {
        fileInput = reader.result;
        var base64 = reader.result.split(",");
        base64Image = base64[1];
    }
    reader.readAsDataURL(file);
}

function getTheme(){
    $.ajax({
        url: "http://"+url+"/api/themes",
        dataType: "JSON",
        contentType: "application/json",
        beforeSend: function(xhr) {
            xhr.setRequestHeader('Authorization', 'key='+apiKey);
        },
    }).done(function(data){
        allThemes = "";
        var i;
        for(i = 0; i<data.length; i++){
            if(i < 1){
                allThemes = "" + data[i].theme +",";
            }else if(i < data.length-1){
                allThemes = allThemes + data[i].theme +",";
            } else{
                allThemes = allThemes + data[i].theme;
            }
        }
        theThemes(allThemes);
    }).fail(function(data){
        var errorMessage = JSON.stringify(data);
    });
}

function theThemes(allThemes){
    $(".theme-group").empty();
    var array = allThemes.split(",");
    for(var i = 0; i<array.length; i++){
        $(".theme-group").append("<option value='" + array[i] + "'>" + array[i] + "</option>");
    }

    $("#theme-select").selectpicker('refresh');
    $("#theme-selectModal").selectpicker('refresh');
}

$(document).on('click', '.browse', function(){
    var file = $(this).parent().parent().parent().find('.file');
    file.trigger('click');
});
$(document).on('change', '.file', function(){
    $(this).parent().find('.form-control').val($(this).val().replace(/C:\\fakepath\\/i, ''));
});

$("#btnAdd").on("click", function(){
    $(".wrongAnswerForm:first").clone().val("").appendTo(".felSvar").hide().fadeIn();
});

$('#loginForm').submit(function(e){
    loginName = $('#loginName').val();
    loginPassword = $("#loginPassword").val();
    e.preventDefault(e);
    localStorage.setItem("user", loginName);
    localStorage.setItem("password", loginPassword);
    checkLogin();
    $('#login-modal').modal('hide');
});

function checkLogin(){
    $.ajax({
        type: "POST",
        url: "http://"+url+"/admin/authenticate",
        contentType: "application/json",
        beforeSend: function(xhr) {
            xhr.setRequestHeader('Authorization', 'key='+apiKey+',user='+localStorage.getItem("user")+',password='+localStorage.getItem("password"));
        },
        statusCode: {
            200: function (response){
                $("#logina").hide();
                $("#logouta").show();
                $("#info").hide();
            },
            400: function (response){
                localStorage.clear();
            }
        }
    });
}

function logout() {
    localStorage.clear();
    $("#logina").show();
    $("#logouta").hide();
    $("#info").show();
}
