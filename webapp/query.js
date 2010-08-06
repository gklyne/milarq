/**
 * @author Alistair Miles
 */

// convenience variables
var log = YAHOO.log;

function runQuery() {
	
	log("running query");

    document.getElementById("status").className = "pending";
    document.getElementById("status").innerHTML = "pending...";
    document.getElementById("results").innerHTML = "";
    document.getElementById("headers").innerHTML = "";

	var success = function(response) {
		log("request successful")
        document.getElementById("status").innerHTML = response.status + " " + response.statusText;
        document.getElementById("status").className = "ok";
		
		document.getElementById("headers").innerHTML = response.getAllResponseHeaders;
		
		var contentType = response.getResponseHeader["Content-Type"];
		log("received content type: "+contentType);
		if (contentType.indexOf("application/sparql-results+json")>=0) {
		    log("found json");
            document.getElementById("results").innerHTML = response.responseText;
		} else if (contentType.indexOf("application/rdf+xml")>=0) {
            log("found rdf/xml");
            var content = response.responseText.replace(/</g,"&lt;");
            content.replace(/>/g,"&gt;");
            document.getElementById("results").innerHTML = content;
        } else {
            log("found unrecognised content type", "error");
        }
    }

    var failure = function(response) {
        document.getElementById("status").innerHTML = response.status + " " + response.statusText;
        document.getElementById("status").className = "notok";
        document.getElementById("headers").innerHTML = response.getAllResponseHeaders;
        document.getElementById("results").innerHTML = response.responseText;
    	log("request failed, response code: "+response.status+" "+response.statusText+", "+response.responseText, "error");
    }

    // define the callback object
    var callback = {
        success: success,
        failure: failure
    };
    
    var path = document.getElementById("path").value;
    var query = document.getElementById("query").value;
    var method = document.getElementById("method").value;
    log("path: "+path+", method: "+method+", query: "+query);
    
    if (method == "GET") {
        YAHOO.util.Connect.asyncRequest("GET", path+"?query="+escape(query), callback, null);
    } else if (method == "POST") {
        var content = "query="+escape(query);
        YAHOO.util.Connect.asyncRequest("POST", path, callback, content);
    }

};

function setupLogger() {
	var logReader = new YAHOO.widget.LogReader("logger");
	
	YAHOO.log("Logger setup done");	
}

YAHOO.util.Event.onDOMReady(setupLogger);
//YAHOO.util.Event.onDOMReady(runTests);

