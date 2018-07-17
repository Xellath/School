$(function(){
    $('#uploadForm').on("submit", function(e) {
        e.preventDefault();
        uploadMedia();
    });

    $(document).keypress(function(e) {
        if(e.which == 13) {
            $('#uploadForm').ajaxSubmit(function(data) {
                uploadMedia();
            });
        }
    });

    $('input[name=media]').change(function(e) {
        var value = $(this).val();
        if(value !== null) {
            $('#selected-file').val(value);
        }
    });

    $('#media-select').change(function() {
        var value = $(this).val();
        switch(value) {
            case "photo":
                $('input[name=media]').attr("accept", "image/*");
                break;
            case "audio":
                $('input[name=media]').attr("accept", "audio/*");
                break;
            case "video":
                $('input[name=media]').attr("accept", "video/*");
                break;
        }
    });

    $('#refresh-button').click(function(e) {
        populateTable($('#media-selector').val());
    });

    $('#media-modal').on('show.bs.modal', function(e) {
        var button = $(e.relatedTarget);
        var modal = $(this);
        modal.find('.modal-title').text(button.data('mediaTitle'))
        modal.find('.modal-body').html(modalMedia(button.data('mediaValue'), button.data('mediaType')));
    });

    $('#media-modal').on('hidden.bs.modal', function () {
        var modal = $(this);
        modal.find('.modal-body').empty();
    });

    populateTable($('#media-selector').val());
});

function populateTable(mediaType) {
    var formattedData = ((mediaType == "all") ? "" : "&type=" + mediaType);

    $.ajax({
        url: "/af2015/il2/server.php",
        method: "GET",
        data: "action=getMedia" + formattedData,
        success: function(response) {
            if(response !== "") {
                var jsonTable = JSON.parse(response);
                $("#media-table").bootstrapTable('destroy');
                $("#media-table").bootstrapTable({
                    data: jsonTable.files
                });

                $('.table-photo').click(function(e) {
                    $('#media-modal').modal('show', $(this));
                });

                $('video').click(function(e) {
                    $('#media-modal').modal('show', $(this));
                });

                $("a.tooltip-icon").tooltip();
                $("a.tooltip-icon").click(function(e) {
                    e.preventDefault();
                });
            }
        }
    });
}

function uploadMedia() {
    var progress = $('.progress');
    var bar = $('.progress-bar');
    var success = $('#success');
    var error = $('#error');
    $('#uploadForm').ajaxSubmit({
        beforeSend: function() {
            progress.removeClass('hidden').show();
            var percentVal = '0%';
            bar.width(percentVal).html(percentVal).show();
        },
        uploadProgress: function(event, position, total, percentComplete) {
            var percentVal = percentComplete + '%';
            bar.width(percentVal).html(percentVal);
        },
        success: function(response) {
            if(response !== "") {
                var jsonResponse = JSON.parse(response);
                if(jsonResponse.success == true) {
                    $('#uploadForm').trigger('reset');
                    $('#media-select').val('photo').selectpicker('refresh');
                    success.removeClass('hidden').hide().fadeIn(1000);
                    setTimeout(function (){
                        populateTable($('#media-selector').val());
                        progress.addClass('hidden');
                        bar.width("0%").html("0%").hide();
                        success.fadeOut(1000);
                    }, 5000);
                } else if(jsonResponse.success == false) {
                    error.removeClass('hidden').hide().fadeIn(1000);
                    setTimeout(function (){
                        progress.addClass('hidden');
                        bar.width("0%").html("0%").hide();
                        error.fadeOut(1000);
                    }, 8000);
                }
            } else {
                progress.addClass('hidden');
                bar.width("0%").html("0%").hide();
                error.removeClass('hidden').hide().fadeIn(1000);
                setTimeout(function (){
                    error.fadeOut(1000);
                }, 8000);
            }
        },
        error: function(msg) {
            progress.addClass('hidden');
            bar.width("0%").html("0%").hide();
            error.removeClass('hidden').hide().fadeIn(1000);
            setTimeout(function (){
                error.fadeOut(1000);
            }, 8000);
        }
    });
}

function typeFormatter(value) {
    var iconElement;
    switch(value) {
        case "photo":
            iconElement = '<a class="tooltip-icon" href="#" data-toggle="tooltip" data-placement="top" title="Foto">' +
                    '<i class="fa fa-file-image-o fa-3x"></i></a>';
            break;
        case "audio":
            iconElement = '<a class="tooltip-icon" href="#" data-toggle="tooltip" data-placement="top" title="Ljud">' +
                    '<i class="fa fa-file-audio-o fa-3x"></i></a>';
            break;
        case "video":
            iconElement = '<a class="tooltip-icon" href="#" data-toggle="tooltip" data-placement="top" title="Video">' +
                    '<i class="fa fa-file-video-o fa-3x"></i></a>';
            break;
    }

    return iconElement;
}

function titleFormatter(value) {
    return '<h3>' + value + '</h3>';
}

function mediaFormatter(value, row, index) {
    var mediaElement;
    switch(row.type) {
        case "photo":
            mediaElement = '<img class="table-photo" src="' + value + '" alt="Foto" data-media-type="' + row.type + '" data-media-value="' + value + '" data-media-title="' + row.title + '">';
            break;
        case "audio":
            mediaElement = '<audio controls>' +
                    '<source src="' + value + '" type="audio/ogg">' +
                    '<source src="' + value + '" type="audio/mpeg">' +
                    '</p>Your browser does not support the audio tag.</p>' +
                    '</audio>';
            break;
        case "video":
            mediaElement = '<video data-media-type="' + row.type + '" data-media-value="' + value + '" data-media-title="' + row.title + '" controls>' +
                    '<source src="' + value + '" type="video/mp4">' +
                    '<source src="' + value + '" type="video/webm">' +
                    '<p>Your browser does not support the video tag.</p>' +
                    '</video>';
            break;
    }

    return mediaElement;
}

function modalMedia(value, type) {
    var mediaElement;
    switch(type) {
        case "photo":
            mediaElement = '<img class="modal-image" src="' + value + '" alt="Photo">';
            break;
        case "audio":
            mediaElement = '<audio class="modal-audio" controls>' +
                    '<source src="' + value + '" type="audio/ogg">' +
                    '<source src="' + value + '" type="audio/mpeg">' +
                    '</p>Your browser does not support the audio tag.</p>' +
                    '</audio>';
            break;
        case "video":
            mediaElement = '<video class="modal-video" controls>' +
                    '<source src="' + value + '" type="video/mp4">' +
                    '<source src="' + value + '" type="video/webm">' +
                    '<p>Your browser does not support the video tag.</p>' +
                    '</video>';
            break;
    }

    return mediaElement;
}
