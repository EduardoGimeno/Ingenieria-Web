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
                        var blob = new  Blob([msg])
                        var link = document.createElement("a")
                        link.href = window.URL.createObjectURL(blob)
                        link.download = "response.csv"
                        document.body.appendChild(link)
                        $("#resultCSV").html(
                            "<div class='alert alert-success lead'><a target='_blank' >"
                            + "Download"
                            + "</a></div>");
                        document.getElementById('resultCSV').onclick = function(){
                            link.click()
                            //document.body.removeChild(link)
                        }   
                        //link.click()
                        //document.body.removeChild(link)
                    },
                    error: function () {
                        $("#resultCSV").html(
                            "<div class='alert alert-danger lead'>ERROR</div>");
                    }
                });
        });        
    });