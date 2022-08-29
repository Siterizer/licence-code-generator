$("#register").submit(function(event) {
    var ajaxRequest;
    event.preventDefault();
    $("#result").html('');
    var values = $(this).serialize();
       ajaxRequest= $.ajax({
            url: "register",
            type: "post",
            data: values
       });
    ajaxRequest.done(function (response){
         $("#result").html('Submitted successfully');
    });
    ajaxRequest.fail(function (data){
        $("#result").html("error     " + data['responseText']);
    });
});