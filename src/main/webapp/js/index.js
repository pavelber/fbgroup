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

function createLink(status, id, file, text){
    if (status === "TAKEN" || status === "CHECKED")
        return '<A HREF="/'+file+'?groupId='+id+'">'+text+'</A>';
    else return ''
}

$(document).ready(function () {
    let $groups = $("#groups-table");

    jQuery.getJSON("/groups" , function (groups) {
        groups.forEach(function (group) {
            $groups.append("<tr>" +
                "<td>"+group.name +"</td>" +
                "<td>"+createLink(group.status, group.id, 'stat.html', 'Statistics') +"</td>" +
                "<td>"+createLink(group.status, group.id, 'dupl.html', 'Duplicates') +"</td>" +
                "<td>" + createStatusHMTML(group.status,group.id) +"</td>" +
                "</tr>");
        });
        
    });

});
