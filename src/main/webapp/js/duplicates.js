function toDate(timestamp){
    let date = new Date(timestamp);
    return date.toDateString()
}
$(document).ready(function () {

    jQuery.getJSON("/duplicates?"+window.location.search.substring(1) , function (data) {
        let $table = $("#duplicates");
        for(let i in data){
            $table.append('<tr></tr><td><A HREF="'+data[i].url+'">'+data[i].url+' ('+toDate(data[i].urlDate)+')'+'</A></td><td><A HREF="'+data[i].original+'">'+data[i].original+' ('+toDate(data[i].originalDate)+')'+'</A></td></tr>')

        }
    });

});
