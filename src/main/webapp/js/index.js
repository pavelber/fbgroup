$(document).ready(function () {
    let $groups = $("#groups-table");

    jQuery.getJSON("/groups" , function (groups) {
        groups.forEach(function (group) {
            $groups.append("<tr><td>" + group.name +"</td><td>" + group.status +"</td><td>"+group.lastCheck+"</td></tr>");
        });
        
    });

});
