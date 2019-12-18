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

    function connect() {
        var socket = new SockJS('/gs-guide-websocket');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function (frame) {
            console.log('Connected: ' + frame);
            stompClient.subscribe('/topic/info', function (info) {
                showInfo(JSON.parse(info.body));
            });
        });
    }
    
    function disconnect() {
        if (stompClient !== null) {
            stompClient.disconnect();
        }
        setConnected(false);
        console.log("Disconnected");
    }
    
    function showInfo(message) {
        $("#osMostUsed").html(message.osMostUsed);
	    $("#browserMostUsed").html(message.browserMostUsed);
	    $("#osLastUsed").html(message.osLastUsed);
	    $("#browserLastUsed").html(message.browserLastUsed);
    }