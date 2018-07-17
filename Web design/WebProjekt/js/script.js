/* Anropar API och hämtar resultat som matchar den inmatade strängen */
$("#progress-bar").hide();
$("#donut-result").hide();
$("#area-result").hide();
$("#donut-result-kommun").hide();
$("#result-section").hide();
$('.parallax').parallax();
$('select').material_select();
$("#lon").hide();
$("#lan").hide();
$("#kom").hide();
    var ssyk = [];
var chartData = [];

var areaResult = Morris.Line({
  element: 'area-result',
    resize: true,
    redraw: true,
  data: [
     { y: '2010', b: 1},
     { y: '2011', b: 2},
     { y: '2012', b: 3},
     { y: '2013', b: 4},
     { y: '2014', b: 5},
     { y: '2015', b: 6},
     { y: '2016', b: 7},
     { y: '2017', b: 8},
     { y: '2018', b: 9},
     { y: '2019', b: 10},

  ],
  xkey: 'y',
  ykeys: ['b'],
  labels: ['Lön'],

  lineColors: ['#0BA462']
});

var donutResult = Morris.Donut({
  element: 'donut-result',
  resize: true,
  redraw: true,
  data: [{
    label: "placeholder",
    value: 0
  }],
  backgroundColor: "#ccc",
  labelColor: "#060",
  colors: [
    "#0BA462",
    "#39B580",
    "#67C69D",
  ]
}).on('click', function (i, row) {
  console.log(row.label + row.value);
  console.log(row.kommun);
  var data = row.kommun;

  $("#donut-result-kommun").hide();
  $("#result").empty();
  $("#result-section").hide();
  $("#kom").hide();
  if (data.length > 0) {
    var donutData = [];
    var selected;
    (function (data) {
      var kommunJobb = {};
      $.each(data, function () {
        if (!kommunJobb[this["kommunnamn"]])
          kommunJobb[this["kommunnamn"]] = [];
        kommunJobb[this["kommunnamn"]].push(this);
      });

      for (var d in kommunJobb) {
        donutData.push({
          label: kommunJobb[d][0].kommunnamn,
          value: kommunJobb[d].length,
          data: kommunJobb[d]
        });
        selected = kommunJobb[d][0].kommunnamn;
      }
    })(data);

    donutResultKommun.setData(donutData);
    $("#kom").fadeIn(2000);
    $("#donut-result-kommun").fadeIn(2000);
    donutResultKommun.redraw();
    donutResultKommun.select(0);
    setTimeout(function () {
      donutResultKommun.redraw();
  }, 2050);

    console.log(donutData);
  }
});

var donutResultKommun = Morris.Donut({
  element: 'donut-result-kommun',
  resize: true,
  data: [{
    label: "placeholder",
    value: 0
  }],
  backgroundColor: "#ccc",
  labelColor: "#060",
  colors: [
    "#0BA462",
    "#39B580",
    "#67C69D",
  ]
}).on('click', function (i, row) {
  console.log(row.label + row.value);
  console.log(row.data);
  console.log(i, row);
  var data = row.data;
  $("#result").empty();
  $("#result-section").hide();

  for (var j in data) {
    $("#result").append('<i class="material-icons">label_outline</i>');
    $("#result").append('<a href = "' + data[j].jobbdata.annonsurl + '">' + data[j].jobbdata.annonsrubrik + '</a>' + '<br>');

  }

  $("#result-section").fadeIn(2000);
});

$("#submit-job").on("click", function () {
  $("#progress-bar").fadeIn();
  $("#result").empty();
  $("#donut-result").hide();
  $("#donut-result-kommun").hide();
  $("#result-section").hide();
  $("#area-result").hide();
  $("#lon").hide();
  $("#lan").hide();
  $("#kom").hide();


  var movieDiv = document.getElementById("div-job");
  var baseUrl = "http://api.arbetsformedlingen.se/platsannons/matchning";

  var yrkesgruppId = $("#select-special").find(":selected").attr("data-yrkesgruppid");
  sessionStorage.setItem("job", yrkesgruppId);
  console.log(yrkesgruppId);

  var id = [10, 20, 9, 21, 13, 23, 6, 8, 7, 25, 12, 1, 4, 3, 17, 24, 22, 19, 14, 18, 5];
  var lanArr = ["Blekinge Län", "Dalarnas län", "Gotlands län",
    "Gävleborgs län", "Hallands län", "Jämtlands län",
    "Jönköpings län", "Kalmar län", "Kronobergs län",
    "Norrbottens län", "Skåne län", "Stockholms län",
    "Södermanlands län", "Uppsala län", "Värmlands län",
    "Västerbottens län", "Västernorrlands län", "Västmanlands län",
    "Västra Götalands län", "Örebro län", "Östergötlands län"
  ]; // namn på alla län.
  var jobArr = []; // antal jobb i län.
  var promises = [];
  var kommunArr = [];
  var i = 0;
  for (i = 0; i < id.length; i++) {
    (function (i) {
      var request = $.ajax({
        url: baseUrl,
        dataType: "JSON",
        data: {
          "lanid": id[i],
          "yrkesgruppid": yrkesgruppId
        }
      }).done(function (data) {
        var jobresult = data.matchningslista;

        //      console.log(jobresult);
        //      console.log(lanArr[i]);
        var annons = jobresult.antal_platsannonser;

        //        console.log(annons);

        jobArr[i] = annons;

        var data = jobresult.matchningdata;
        //    console.log(data);
        var size = 0;
        var kommunData = [];
        for (var j in data) {
          kommunData.push({
            kommunnamn: data[j].kommunnamn,
            jobbdata: data[j]
          });
        }

        kommunArr[i] = kommunData;
      }).fail(function (data) {
        console.log(data);
      });

      promises.push(request);
    })(i);
  }

  $.when.apply(null, promises).done(function () {
    donutResult.setData([{
        label: lanArr[0],
        value: jobArr[0],
        kommun: kommunArr[0]
      },
      {
        label: lanArr[1],
        value: jobArr[1],
        kommun: kommunArr[1]
      },
      {
        label: lanArr[2],
        value: jobArr[2],
        kommun: kommunArr[2]
      },
      {
        label: lanArr[3],
        value: jobArr[3],
        kommun: kommunArr[3]
      },
      {
        label: lanArr[4],
        value: jobArr[4],
        kommun: kommunArr[4]
      },
      {
        label: lanArr[5],
        value: jobArr[5],
        kommun: kommunArr[5]
      },
      {
        label: lanArr[6],
        value: jobArr[6],
        kommun: kommunArr[6]
      },
      {
        label: lanArr[7],
        value: jobArr[7],
        kommun: kommunArr[7]
      },
      {
        label: lanArr[8],
        value: jobArr[8],
        kommun: kommunArr[8]
      },
      {
        label: lanArr[9],
        value: jobArr[9],
        kommun: kommunArr[9]
      },
      {
        label: lanArr[10],
        value: jobArr[10],
        kommun: kommunArr[10]
      },
      {
        label: lanArr[11],
        value: jobArr[11],
        kommun: kommunArr[11]
      },
      {
        label: lanArr[12],
        value: jobArr[12],
        kommun: kommunArr[12]
      },
      {
        label: lanArr[13],
        value: jobArr[13],
        kommun: kommunArr[13]
      },
      {
        label: lanArr[14],
        value: jobArr[14],
        kommun: kommunArr[14]
      },
      {
        label: lanArr[15],
        value: jobArr[15],
        kommun: kommunArr[15]
      },
      {
        label: lanArr[16],
        value: jobArr[16],
        kommun: kommunArr[16]
      },
      {
        label: lanArr[17],
        value: jobArr[17],
        kommun: kommunArr[17]
      },
      {
        label: lanArr[18],
        value: jobArr[18],
        kommun: kommunArr[18]
      },
      {
        label: lanArr[19],
        value: jobArr[19],
        kommun: kommunArr[19]
      },
      {
        label: lanArr[20],
        value: jobArr[20],
        kommun: kommunArr[20]
      }
    ]);
    getWage(ssyk);
    $("#progress-bar").fadeOut(2000);

    $("#donut-result").fadeIn(3000);
    $("#lan").fadeIn(3000);

    donutResult.select(10); // Select skånes län

    areaResult.redraw();
    $("#lon").fadeIn(2000);
    $("#area-result").fadeIn(2000);
    donutResult.redraw();
  });
});

/**
 * Hämtar data från arbetsförmedlingens api och lägger till det i första dropdown. Den data som hämtas är yrkesområden.
 */
function populateJobs() {
  $.ajax({
    type: "GET",
    // url: "http://api.arbetsformedlingen.se/af/v0/platsannonser/soklista/yrkesgrupper?",
    url: "http://api.arbetsformedlingen.se/af/v0//platsannonser/soklista/yrkesomraden",
    // data: {"yrkesomradeid" : 3}, // 3 är id för data/id
    dataType: "json",
    success: function (response) {
      $.each(response.soklista.sokdata, function (index, data) {
        // Skapa options nodes för drop down lista.
        var option = document.createElement("option");
        $(option)
          .attr("value", index)
          .attr("data-yrkesomradeid", data.id)
          .html(data.namn)
          .appendTo($("#select-main"))
      });
      $('#select-main').material_select();
    },
    fail: function (response) {
      console.log(response);
    }
  });
}

/**
 * Hämtar specialiserade yrkesgrupper från arbetsförmedlingen.
 * @param {int} id
 *    En id som innehåller yrkesomradeid.
 */
function populateSpecilzation(id) {
  $("#select-special").empty();
  $.ajax({
    type: "GET",
    url: "http://api.arbetsformedlingen.se/af/v0/platsannonser/soklista/yrkesgrupper?",
    data: {
      "yrkesomradeid": id
    },
    dataType: "json",
    success: function (response) {
      console.log(response);
      $.each(response.soklista.sokdata, function (index, data) {
        var option = document.createElement("option");
        $(option)
          .attr("value", index)
          .attr("data-yrkesgruppid", data.id)
          .html(data.namn)
          .appendTo($("#select-special"));
      });
      $('#select-special').material_select();
    },
    fail: function (response) {
      console.log(response);
    }
  });

}

function totalJobbs() {
  $.ajax({
    type: "GET",
    url: "http://api.arbetsformedlingen.se/platsannons/soklista/lan",
    dataType: "json",
    success: function (response) {
      var antalLedigaJobb = response.soklista;
      $("#totalJobbs").text(antalLedigaJobb.totalt_antal_ledigajobb);

      console.log(antalLedigaJobb.totalt_antal_ledigajobb);
    },
    fail: function (response) {
      console.log(response);
    }
  });
}

$(document).ready(function () {
  populateJobs();
  totalJobbs();

  $("#select-main").change(function () {
    var selectedMainGroup = $("#select-main").find(":selected").attr("data-yrkesomradeid");
    console.log(selectedMainGroup);
      getPrognos(selectedMainGroup);
    populateSpecilzation(selectedMainGroup);

  });
});

function getWage(ssyk) {
  // Hamta ut id fran ssyk
  var ids = [];
  for (var i = 0; i < ssyk.length; i++) {
    if (ids.indexOf(ssyk[i].id) === -1) {
      ids.push(ssyk[i].id);
    }
  }
  console.log(ids);

  var jsonObj = {
    "query": [{
        "code": "Sektor",
        "selection": {
          "filter": "item",
          "values": [
            "0"
          ]
        }
      },
      {
        "code": "Yrkesgrupp",
        "selection": {
          "filter": "item",
          "values": ids
        }
      },
      {
        "code": "Kon",
        "selection": {
          "filter": "item",
          "values": [
            "1"
          ]
        }
      },
      {
        "code": "ContentsCode",
        "selection": {
          "filter": "item",
          "values": [
            "AM0110G1"
          ]
        }
      }
    ],
    "response": {
      "format": "json"
    }
  };

  $.ajax({
    type: "POST",
    url: "http://api.scb.se/OV0104/v1/doris/sv/ssd/START/AM/AM0110/AM0110B/LoneSpridSektorYrk",
    data: JSON.stringify(jsonObj),
    dataType: "json",
    success: function (response) {
      console.log(response);
      var wageMap = new Map();
      for (var i = 0; i < response.data.length; i++) {

        var wage = response.data[i].values[0];
        // Kontrollerar om data inte finns tillanglig
        if (wage === '..') {
          wage = 0;
          console.log("lön är 0");
        }

        var currentObj = {
          year: response.data[i].key[3],
          //id: response.data[i].key[1],
          wage: wage
        };
        console.log(currentObj.year);
        // Lagg till values till en map.
        if (!wageMap.has(currentObj.year)) {
          var elem = {
            totwage: parseInt(currentObj.wage),
            counter: 1,
            avgwage: parseInt(currentObj.wage) / 1
          };
          wageMap.set(currentObj.year, elem);
        } else {
          var elem = wageMap.get(currentObj.year);
          elem.totwage = parseInt(elem.totwage) + parseInt(currentObj.wage);
          elem.counter++;
          elem.avgwage = parseInt(elem.totwage) / parseInt(elem.counter);
          wageMap.set(currentObj.year, elem);
        }
      }
      console.log(wageMap);
      chartData = [];
      wageMap.forEach(function (value, key, map) {
        var wage = map.get(key).avgwage;
        chartData.push({ y : key, b : Math.round(wage)});
      });
      console.log(chartData);



      areaResult.setData(chartData);

    },
    fail: function (response) {

    }
  });


}

function getPrognos(yrkesomradeid) {
  $.ajax({
    type: "GET",
    url: "http://api.arbetsformedlingen.se/af/v0/yrkesprognoser/soklista/prognoser?",
    data: {
      "yrkesomradeid": yrkesomradeid
    },
    dataType: "json",
    success: function (response) {

      console.log(response);
      // Hamtar ssyk for jobb
      for (var i = 0; i < response.length; i++) {
        ssyk.push({
          "id": response[i].yrkesgruppID,
          "name": response[i].prognosrubrik
        });
      }

    }
  });
}
