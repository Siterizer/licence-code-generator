$("#login").submit(function(event) {
    var ajaxRequest;
    event.preventDefault();
    $("#result").html('');
    var values = $(this).serialize();
       ajaxRequest= $.ajax({
            url: "login",
            type: "post",
            data: values
       });
    ajaxRequest.done(function (response){
         $("#result").html('Submitted successfully');
    });
    ajaxRequest.fail(function (response){
        $("#result").html("error " + response.status);
    });
});