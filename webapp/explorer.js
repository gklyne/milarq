/**
 * @author Alistair Miles
 */

// convenience variables
var log = YAHOO.log;

function launchExplore() {
    log("launch explore");    
    var uri = document.getElementById("startnode").value;
    if (uri != "") {
        explore(uri);
    }    
    else {
        log("no start URI found", "error");
    }
}

function explore(uri) {

    log("running explore");

    var cns = document.getElementById("currentnode");
    cns.innerHTML = "&lt;"+uri+"&gt;";
    
    var dps = document.getElementById("dataprops");
    dps.innerHTML = "pending...";

    var lso = document.getElementById("linksout");
    lso.innerHTML = "pending...";

    var lsi = document.getElementById("linksin");
    lsi.innerHTML = "pending...";
    
    var g = document.getElementById("graphuri").value;
    var limit = document.getElementById("limit").value;
    
    getData(uri, g, limit);
    getLinksOut(uri, g, limit);
    getLinksIn(uri, g, limit);
}


function getLinksOut(uri, graph, limit) {
    
    var success = function(response) {
        log("links out success");
        resultset = YAHOO.lang.JSON.parse(response.responseText);
        bindings = resultset.results.bindings;
        content = "";
        if (bindings.length > 0) {
            for (var i=0; i<bindings.length; i++) {
                var link = bindings[i].link.value;
                var p = bindings[i].p.value;
                content += "<li>";
                content += "&lt;"+p+"&gt; "
                content += "&lt;<a href=\"#\" onClick=\"explore('"+link+"')\">" + link + "</a>&gt;";
                content += "</li>";
            }
        }
        else {
            content = "<li>none</li>";
        }
        log("links out content: "+content);
        document.getElementById("linksout").innerHTML = content;
    }

    var failure = function(response) {
        log("request failed, response code: "+response.status+" "+response.statusText+", "+response.responseText, "error");
    }

    // define the callback object
    var callback = {
        success: success,
        failure: failure
    };
    
    var path = document.getElementById("path").value;
    var query = "";
    if (graph && graph != "") {
        query = "SELECT * WHERE { GRAPH <"+graph+"> { <"+uri+"> ?p ?link . FILTER (isIRI(?link)) } } LIMIT "+limit;
    } else {
        query = "SELECT * WHERE { <"+uri+"> ?p ?link . FILTER (isIRI(?link)) } LIMIT "+limit;        
    }
    
    var method = "GET";
    log("path: "+path+", method: "+method+", query: "+query);
    
    if (method == "GET") {
        YAHOO.util.Connect.asyncRequest("GET", path+"?query="+escape(query), callback, null);
    } else if (method == "POST") {
        var content = "query="+escape(query);
        YAHOO.util.Connect.asyncRequest("POST", path, callback, content);
    }
    
    
}

function getLinksIn(uri, graph, limit) {
    
    var success = function(response) {
        log("links in success");
        resultset = YAHOO.lang.JSON.parse(response.responseText);
        bindings = resultset.results.bindings;
        content = "";
        if (bindings.length > 0) {
            for (var i=0; i<bindings.length; i++) {
                var link = bindings[i].link.value;
                var p = bindings[i].p.value;
                content += "<li>";
                content += "&lt;<a href=\"#\" onClick=\"explore('"+link+"')\">" + link + "</a>&gt;";
                content += " &lt;"+p+"&gt;"
                content += "</li>";
            }
        }
        else {
            content = "<li>none</li>";
        }
        log("links in content: "+content);
        document.getElementById("linksin").innerHTML = content;
    }

    var failure = function(response) {
        log("request failed, response code: "+response.status+" "+response.statusText+", "+response.responseText, "error");
    }

    // define the callback object
    var callback = {
        success: success,
        failure: failure
    };
    
    var path = document.getElementById("path").value;
    var query = "";
    if (graph && graph != "") {
        query = "SELECT * WHERE { GRAPH <"+graph+"> { ?link ?p <"+uri+"> . FILTER (isIRI(?link)) } } LIMIT "+limit;
    } else {
        query = "SELECT * WHERE { ?link ?p <"+uri+"> . FILTER (isIRI(?link)) } LIMIT "+limit;        
    }
    
    var method = "GET";
    log("path: "+path+", method: "+method+", query: "+query);
    
    if (method == "GET") {
        YAHOO.util.Connect.asyncRequest("GET", path+"?query="+escape(query), callback, null);
    } else if (method == "POST") {
        var content = "query="+escape(query);
        YAHOO.util.Connect.asyncRequest("POST", path, callback, content);
    }
    
    
}

function getData(uri, graph, limit) {
    
    var success = function(response) {
        log("data props success");
        resultset = YAHOO.lang.JSON.parse(response.responseText);
        bindings = resultset.results.bindings;
        content = "";
        if (bindings.length > 0) {
            for (var i=0; i<bindings.length; i++) {
                var data = bindings[i].data;
                var p = bindings[i].p.value;
                content += "<li>";
                content += "&lt;"+p+"&gt; "
                content += "\"" + data.value + "\"";
                if (data.datatype) {
                    content += "^^&lt;" + data.datatype + "&gt;";
                } else if (data["xml:lang"]) {
                    content += "@" + data["xml:lang"];
                }
                content += "</li>";
            }
        }
        else {
            content = "<li>none</li>";
        }
        log("dataprops content: "+content);
        document.getElementById("dataprops").innerHTML = content;
    }

    var failure = function(response) {
        log("request failed, response code: "+response.status+" "+response.statusText+", "+response.responseText, "error");
    }

    // define the callback object
    var callback = {
        success: success,
        failure: failure
    };
    
    var path = document.getElementById("path").value;
    var query = "";
    if (graph && graph != "") {
        query = "SELECT * WHERE { GRAPH <"+graph+"> { <"+uri+"> ?p ?data . FILTER (isLiteral(?data)) } } LIMIT "+limit;
    } else {
        query = "SELECT * WHERE { <"+uri+"> ?p ?data . FILTER (isLiteral(?data)) } LIMIT "+limit;        
    }
    
    var method = "GET";
    log("path: "+path+", method: "+method+", query: "+query);
    
    if (method == "GET") {
        YAHOO.util.Connect.asyncRequest("GET", path+"?query="+escape(query), callback, null);
    } else if (method == "POST") {
        var content = "query="+escape(query);
        YAHOO.util.Connect.asyncRequest("POST", path, callback, content);
    }
    
}



function setupLogger() {
	var logReader = new YAHOO.widget.LogReader("logger");
	
	YAHOO.log("Logger setup done");	
}

YAHOO.util.Event.onDOMReady(setupLogger);
//YAHOO.util.Event.onDOMReady(runTests);

