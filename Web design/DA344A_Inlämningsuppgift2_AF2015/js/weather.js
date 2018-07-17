$(function(){
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(getPosition, showError);
    } else {
        $("#spinner").fadeOut(1000);
        $("#header-city").html("Browser does not support Geolocation.");
        $("#info").hide();
        $("#error").removeClass("hidden").hide().fadeIn(1000);
    }

    function getPosition(position) {
        $.ajax({
            url: "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(SELECT%20woeid%20FROM%20geo.places%20WHERE%20text%3D%22(" + position.coords.latitude + "%2C%20" + position.coords.longitude + ")%22)%20and%20u%3D%22c%22&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys",
            dataType: "json",
            success: function(json) {
                $("#spinner").fadeOut(1000);
                $("#info").hide();
                $("#success").removeClass("hidden").hide().fadeIn(1000);
                $("#header-city").html(json.query.results.channel.location.city + ", " + json.query.results.channel.location.country).hide().fadeIn(1000);
                $("#weather").append(" " + json.query.results.channel.item.condition.text).removeClass("hidden").hide().fadeIn(1000);
                $("#temperature").append(" " + json.query.results.channel.item.condition.temp + " <i class='wi wi-celsius wi-2x'></i>").removeClass("hidden").hide().fadeIn(1000);
                $("#weather-icon").addClass("wi-yahoo-" + json.query.results.channel.item.condition.code).removeClass("hidden").hide().fadeIn(1000);

                setTimeout(function (){
                    $("#success").fadeOut(1000);
                }, 8000);
            },
            error: function(msg) {
                $("#spinner").fadeOut(1000);
                $("#header-city").html("Weather API could not provide weather data.");
                $("#info").hide();
                $("#error").removeClass("hidden").hide().fadeIn(1000);
            }
        });
    }

    function showError(error) {
        $("#spinner").fadeOut(1000);
        $("#info").hide();
        $("#error").removeClass("hidden").hide().fadeIn(1000);
        switch(error.code) {
            case error.PERMISSION_DENIED:
                $("#header-city").html("User denied the request for Geolocation.");
                break;
            case error.POSITION_UNAVAILABLE:
                $("#header-city").html("Location information is unavailable.");
                break;
            case error.TIMEOUT:
                $("#header-city").html("The request to get user location timed out.");
                break;
            case error.UNKNOWN_ERROR:
                $("#header-city").html("An unknown error occurred.");
                break;
        }

        $("#header-city").hide().fadeIn(1000);
    }
});
