function manageGroup(id){
    $.ajax({
        type: "GET",
        url: "/group?id="+id+"&manage="+$("#"+id).prop( "checked" )
    });
    return true;
}

function createStatusHMTML(status,id){
    if (status === "TAKEN") return '<span class="label label-danger">Managed by other admin</span>';
    let defaultvalue = "";
    if (status === "CHECKED")
        defaultvalue = "checked";
    return '<input type="checkbox" '+defaultvalue+' onclick="manageGroup('+id+')" id="'+id+'">'
}

$(document).ready(function () {
    let $groups = $("#groups-table");

    jQuery.getJSON("/groups" , function (groups) {
        groups.forEach(function (group) {
            $groups.append("<tr><td>" + group.name +"</td><td>" + createStatusHMTML(group.status,group.id) +"</td></tr>");
        });
        
    });

});
