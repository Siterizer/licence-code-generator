$("#login").submit(function(event) {
    event.preventDefault();
    var ajaxRequest;
    var formData = {username: $("#username").val(), password: $("#password").val()};
    $("#result").html('');
    ajaxRequest= $.ajax({
        url: "api/login",
        type: "post",
        contentType : "application/json",
        data: JSON.stringify(formData)
    });
    ajaxRequest.done(function (response){
         $("#result").html('Submitted successfully');
    });
    ajaxRequest.fail(function (response){
        $("#result").html("error " + response.status);
    });
});