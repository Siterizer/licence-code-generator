$(document).ready(function() {
    $("#get_main_page").click(function(){
        $("#content-placeholder").html("Content BAR")
    });
    $("#get_register").click(function(){
        $("#content-placeholder").load("register");
    });
    $("#get_login").click(function(){
        $("#content-placeholder").load("login");
    });
    $("#get_users").click(function(){
        $("#content-placeholder").load("user");
    });
    $("#get_admin").click(function(){
        $("#content-placeholder").load("admin");
    });
});