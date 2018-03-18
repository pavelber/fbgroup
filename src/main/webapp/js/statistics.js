
function show(tableid, data) {
    let $table = $("#"+tableid);
    for(let key in data){
        $table.append("<tr></tr><td>"+key+"</td><td>"+data[key]+"</td></tr>")
    }
}

$(document).ready(function () {

    jQuery.getJSON("/statistics?"+window.location.search.substring(1) , function (stats) {
        show("likes",stats['likes']);
        show("comments",stats['comments']);
        show("posts",stats['posts']);
        show("received_likes",stats['receivedLikes']);
        show("received_comments",stats['receivedComments']);
    });

});
