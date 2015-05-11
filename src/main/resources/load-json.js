$.getJSON( window.location, renderJSON );

function renderJSON(json) {

	$("#content").html(tmplLoader.render('question', json));

	var pre = $("<pre/>");
	$("#raw").append(pre);
	pre.text(JSON.stringify(json, null, 2));

}