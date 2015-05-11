$.getJSON( window.location, renderJSON );

function renderJSON(json) {
	$("#content").append($( "pre" )).html(JSON.stringify(json, null, 2));
}