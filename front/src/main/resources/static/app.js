let stompClient = null;
const id = 123456789;
const token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI1ODYiLCJhdXRoIjoiUk9MRV9VU0VSIiwiZXhwIjoxNjk5NDUxNDkwfQ.ELB__ZSJoWzWXoU2sChRkHLUnKwXyfGWIcKi9SSNY74cpLoV9uTezH-sob89r7NgJTOnonYHKqTyLIolqmp8Dw";

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    // const socket = new SockJS('http://localhost:8090/api/message/stomp/message', {}, {transports: ['websocket', 'xhr-streaming', 'xhr-polling']});
    // let socket = new SockJS('http://localhost:8090/api/message/stomp/message');
    // let socket = new SockJS('http://localhost:8090/api/message/stomp/message', {
    //     transportOptions: {
    //         'xhr-streaming': {
    //             headers: {
    //                 "Authorization": "Bearer "+token
    //             }
    //         }
    //     }
    // });
    let socket = new SockJS('http://localhost:9010/stomp/message');

    stompClient = Stomp.over(socket);
    let headers = {
        'Authorization': 'Bearer '+token
    }
    stompClient.connect(headers, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        // stompClient.subscribe('/sub/'+id, function (message) {
        stompClient.subscribe('/user/queue/message', function (message) {
            showMessage(JSON.parse(message.body).message);
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

function sendName() {
    // stompClient.send("/pub/send", {}, JSON.stringify({'name': $("#name").val()}));
    // stompClient.send("/pub/sendTo", {},JSON.stringify({'message': $("#name").val(), 'id': id}));
    stompClient.send("/pub/sendToUser", {},JSON.stringify({'message': $("#name").val()}));
}

function showMessage(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
});