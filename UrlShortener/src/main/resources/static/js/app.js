$(document).ready(
    function () {
        $("#shortener").submit(
            function (event) {
                event.preventDefault();
                $.ajax({
                    type: "POST",
                    url: "/link",
                    data: $(this).serialize(),
                    success: function (msg) {
                        $("#result").html(
                            "<div class='alert alert-success lead'><a target='_blank' href='"
                            + msg.uri
                            + "'>"
                            + msg.uri
                            + "</a></div>");
                    },
                    error: function () {
                        $("#result").html(
                            "<div class='alert alert-danger lead'>ERROR</div>");
                    }
                });
            });
        $("#shortenerCSV").submit(
            function (event) {
                event.preventDefault();
                $.ajax({
                    type: "POST",
                    url: "/linkCSV",
                    data: new FormData(this),
                    contentType: false,
                    processData: false,
                    success: function (msg) {
                        $("#resultCSV").html(
                            "<div class='alert alert-success lead'><a target='_blank'"
                            +"href=/linkCSV"
                            + ">"
                            + "Download"
                            + "</a></div>");   
                    },
                    error: function () {
                        $("#resultCSV").html(
                            "<div class='alert alert-danger lead'>ERROR</div>");
                    }
                });
        });        
    });