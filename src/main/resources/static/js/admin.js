$(document).ready(function() {
    fill_table()
});
function fill_table(){
    $.get("admin/info", function( data ) {
        for(let i = 0; i<data.length; i++){
            console.log(data[i]);
            var isChecked = ((data[i].locked) ? 'checked' : '');
            var url_to_use = ((data[i].locked) ? 'unblock' : 'block');
            $(users_array).find('tbody').append("<tr><td>" + data[i].id +"</td>" +
            "<td>" + data[i].username + "</td>" +
            "<td>" + data[i].email + "</td>" +
            "<td>" + data[i].roles + "</td>" +
            "<td>" + generate_licences(data[i].licences) + "</td>" +
            "<td><input type='checkbox'" + isChecked + "></td>" +
            "<td><button type='button' onclick='block_unblock(\"" + data[i].id + "\", \"" + url_to_use + "\")' id='" + data[i].id + "' value='submit'>Submit</button></td>" +
            "</tr>");
        }
    });
}
function clear_table(){
    $(users_array).find('tbody').empty();
}
function generate_licences(licences){
    result = '';
    for(let i = 0; i<licences.length; i++){
        result += (
        "name: " + licences[i].name + ", " +
        "licence:<br> <button type='button' id=\"button_" + licences[i].licence +
        "\" onclick=toggle_visibility(\"" + licences[i].licence + "\")>show</button> " +
        "<div id=\"div_" + licences[i].licence + "\" style=\"display: none;\">" + licences[i].licence + "</div><br>"
        );
    }
    console.log(result);
    return result;
}
function toggle_visibility(id) {
    var button = document.getElementById("button_" + id);
    var div = document.getElementById("div_" + id);
    if ( div.style.display == 'block' ){
        div.style.display = 'none';
        button.innerHTML = 'show';
    }
    else{
        div.style.display = 'block';
        button.innerHTML = 'hide';
    }
}
function block_unblock(id, url) {
    var ajaxRequest;
    event.preventDefault();
    $("#result").html('');
    var values = $(this).serialize();
       ajaxRequest= $.ajax({
            url: "admin/"+ url,
            type: "post",
            contentType : "application/json",
            data: JSON.stringify(id)
       });
    ajaxRequest.done(function (response){
         $("#result").html('Submitted successfully');
         clear_table();
         fill_table();
    });
    ajaxRequest.fail(function (data){
        $("#result").html("error     " + data['responseText']);
    });
}