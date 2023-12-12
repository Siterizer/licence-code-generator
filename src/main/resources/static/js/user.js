$(document).ready(function() {

    $.get("user/info", function( data ) {
        $("#user_email").html(data.email);

        for(let i = 0; i<data.licences.length; i++){
            $(user_products).find('tbody').append("<tr><td>" + data.licences[i].name +"</td>" +
            "<td>" + data.licences[i].licence + "</td>" +
            "</tr>");
        }
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