$(document).ready(function() {
    fill_products_table()
    show_user_info()
});
$("#changePassword").submit(function(event) {
    var ajaxRequest;
    event.preventDefault();
    $("#result").html('');
    var formData = {oldPassword: $("#oldPassword").val(), newPassword: $("#newPassword").val(),
                    newMatchedPassword: $("#newMatchedPassword").val()};
    ajaxRequest= $.ajax({
        url: "user/updatePassword",
        type: "post",
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
function show_user_info(){
    $.get("user/info", function( data ) {
        $("#user_email").html(data.email);

        for(let i = 0; i<data.licences.length; i++){
            $(user_licences).find('tbody').append("<tr><td>" + data.licences[i].name +"</td>" +
            "<td>" + data.licences[i].licence + "</td>" +
            "</tr>");
        }
    });
}
function fill_products_table(){
    $.get("products", function( data ) {
        for(let i = 0; i<data.length; i++){
            $(products_array).find('tbody').append("<tr><td>" + data[i].id +"</td>" +
            "<td>" + data[i].name + "</td>" +
            "<td>" + data[i].price + "</td>" +
            "<td><button type='button' onclick='buy_licence(\"" + data[i].id + "\")' id='" + data[i].id + "' value='submit'>Submit</button></td>" +

            "</tr>");
        }
    });
}
function buy_licence(id) {
    var ajaxRequest;
    event.preventDefault();
    $("#result").html('');
    var values = $(this).serialize();
       ajaxRequest= $.ajax({
            url: "licence/buy/",
            type: "post",
            contentType : "application/json",
            data: JSON.stringify(id)
       });
    ajaxRequest.done(function (response){
         $("#result").html('Submitted successfully');
         clear_licence_table();
         show_user_info();
    });
    ajaxRequest.fail(function (data){
        $("#result").html("error     " + data['responseText']);
    });
}
function clear_licence_table(){
    $(user_licences).find('tbody').empty();
}