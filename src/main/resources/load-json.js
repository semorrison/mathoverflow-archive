$.getJSON( window.location, renderJSON );

function renderJSON(json) {
	$("#content").append($( "pre" )).text(JSON.stringify(json, null, 2));
}