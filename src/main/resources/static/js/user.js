$(document).ready(function() {

    $.get("user/info", function( data ) {
        $("#user_email").html(data.email);
        $("#user_products").html(data.products.toString());
    });
});
    $("#changePassword").submit(function(event) {
        var ajaxRequest;
        event.preventDefault();
        $("#result").html('');
        var values = $(this).serialize();
           ajaxRequest= $.ajax({
                url: "user/updatePassword",
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