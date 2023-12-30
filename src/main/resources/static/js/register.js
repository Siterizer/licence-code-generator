$("#register").submit(function(event) {
    event.preventDefault();
    var formData = {email: $("#email").val(), username: $("#username").val(),
                    password: $("#password").val(), matchedPassword: $("#matchedPassword").val()};
    var ajaxRequest;
    $("#result").html('');
    ajaxRequest= $.ajax({
        type: "post",
        url: "register",
        contentType : "application/json",
        data: JSON.stringify(formData)
    });
    ajaxRequest.done(function (response){
         $("#result").html('Submitted successfully');
    });
    ajaxRequest.fail(function (data){
        $("#result").html("error     " + data['responseText']);
    });
});