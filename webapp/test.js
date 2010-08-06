/**
 * This script contains tests for SPARQLite TDB request handler.
 * @author Alistair Miles
 */

// convenience variables
var assert = YAHOO.util.Assert;
var log = YAHOO.log;

// utility function
function testRequestFailureStatus(runner, method, path, content, expected) {
	
    var success = function(response) {
        runner.resume(function() {
            assert.fail("request should not succeed");
        });
    }

    var failure = function(response) {
        runner.resume(function() {
            log("response code: "+response.status, "test");
            assert.areEqual(expected, response.status); 
        });
    }

    // define the callback object
    var callback = {
        success: success,
        failure: failure
    };
    
    YAHOO.util.Connect.asyncRequest(method, path, callback, content);

    runner.wait();
}

var _testCase = {
	name: "SPARQLite Endpoint Test Case"
};

_testCase.testGetNoQueryParam = function() {

    testRequestFailureStatus(this, "GET", path, null, 400); // 400 bad request
	
};

_testCase.testGetBadQuery = function() {

    testRequestFailureStatus(this, "GET", path+"?query=foo", null, 400); // 400 bad request
    
};

_testCase.testPostNoQueryParam = function() {

    testRequestFailureStatus(this, "POST", path, "foo=bar", 400); // 400 bad request
    
};

_testCase.testPostBadQuery = function() {

    testRequestFailureStatus(this, "POST", path, "query=foo", 400); // 400 bad request
    
};

_testCase.testGetAskQuery = function() {
    log("== begin testAskQuery ==");

    var runner = this;
    	
	var success = function(response) {
        log("success case callback, status "+response.status);
        runner.resume(function() {
        	
            var data = null;
            
            try {
                log("parse the response text", "test");
                data = YAHOO.lang.JSON.parse(response.responseText);
            } catch (e) {
                assert.fail("exception parsing JSON response: " + response.responseText + " " + e);
            }

            assert.isNotUndefined(data["boolean"]);
            log("ask response: "+data["boolean"], "test");

            var contentType = response.getResponseHeader["Content-Type"];
            log("received content type: "+contentType, "test");
            assert.isTrue(contentType.indexOf("application/sparql-results+json")>=0, "expected content type 'application/sparql-results+json'") 
        	
        });
    }

    var failure = function(response) {
        log("failure case callback, status "+response.status);
        runner.resume(function() {
        	assert.fail("request failed "+response.status+" "+response.statusText+" : "+response.responseText);
        });
    }

    // define the callback object
    var callback = {
        success: success,
        failure: failure
    };
    
    var query = "ASK { ?s a ?o }";
    YAHOO.util.Connect.asyncRequest("GET", path+"?query="+escape(query), callback, null);
    runner.wait();
};

_testCase.testPostAskQuery = function() {

    var runner = this;
        
    var success = function(response) {
        runner.resume(function() {
            
            var data = null;
            
            try {
                log("parse the response text", "test");
                data = YAHOO.lang.JSON.parse(response.responseText);
            } catch (e) {
                assert.fail("exception parsing JSON response: " + response.responseText + " " + e);
            }

            assert.isNotUndefined(data["boolean"]);
            log("ask response: "+data["boolean"], "test");

            var contentType = response.getResponseHeader["Content-Type"];
            log("received content type: "+contentType, "test");
            assert.isTrue(contentType.indexOf("application/sparql-results+json")>=0, "expected content type 'application/sparql-results+json'") 
                        
        });
    }

    var failure = function(response) {
        runner.resume(function() {
            assert.fail("request failed "+response.status+" "+response.statusText+" : "+response.responseText);
        });
    }

    // define the callback object
    var callback = {
        success: success,
        failure: failure
    };
    
    var query = "ASK { ?s a ?o }";
    var content = "query="+escape(query);    
    YAHOO.util.Connect.asyncRequest("POST", path, callback, content);
    runner.wait();
    
};

_testCase.testGetSelectQuery = function() {

    var runner = this;
        
    var success = function(response) {
        runner.resume(function() {
            
            var data = null;
            
            try {
                log("parse the response text", "test");
                data = YAHOO.lang.JSON.parse(response.responseText);
            } catch (e) {
                assert.fail("exception parsing JSON response: " + response.responseText + " " + e);
            }

            assert.isNotUndefined(data["head"]);
            log("select response: "+response.responseText, "test");

            var contentType = response.getResponseHeader["Content-Type"];
            log("received content type: "+contentType, "test");
            assert.isTrue(contentType.indexOf("application/sparql-results+json")>=0, "expected content type 'application/sparql-results+json'") 
            
        });
    }

    var failure = function(response) {
        runner.resume(function() {
            assert.fail("request failed "+response.status+" "+response.statusText+" : "+response.responseText);
        });
    }

    // define the callback object
    var callback = {
        success: success,
        failure: failure
    };
    
    var query = "SELECT * WHERE { ?s a ?o }";
    YAHOO.util.Connect.asyncRequest("GET", path+"?query="+escape(query), callback, null);
    runner.wait();
    
};

_testCase.testPostSelectQuery = function() {

    var runner = this;
        
    var success = function(response) {
        runner.resume(function() {
            
            var data = null;
            
            try {
                log("parse the response text", "test");
                data = YAHOO.lang.JSON.parse(response.responseText);
            } catch (e) {
                assert.fail("exception parsing JSON response: " + response.responseText + " " + e);
            }

            assert.isNotUndefined(data["head"]);
            log("select response: "+response.responseText, "test");

            var contentType = response.getResponseHeader["Content-Type"];
            log("received content type: "+contentType, "test");
            assert.isTrue(contentType.indexOf("application/sparql-results+json")>=0, "expected content type 'application/sparql-results+json'") 
            
        });
    }

    var failure = function(response) {
        runner.resume(function() {
            assert.fail("request failed "+response.status+" "+response.statusText+" : "+response.responseText);
        });
    }

    // define the callback object
    var callback = {
        success: success,
        failure: failure
    };
    
    var query = "SELECT * WHERE { ?s a ?o }";
    var content = "query="+escape(query);    
    YAHOO.util.Connect.asyncRequest("POST", path, callback, content);
    runner.wait();
    
};

_testCase.testGetConstructQuery = function() {
    var runner = this;
   
    var success = function(response) {
        runner.resume(function() {         
            assert.isTrue(response.responseText.length>0);
            assert.isTrue(response.responseText.indexOf("rdf:RDF")>=0);
            log("select response: "+response.responseText, "test");
            var contentType = response.getResponseHeader["Content-Type"];
            log("received content type: "+contentType, "test");
            assert.isTrue(contentType.indexOf("application/rdf+xml")>=0, "expected content type 'application/rdf+xml'") 
        });
    }

    var failure = function(response) {
        runner.resume(function() {
            assert.fail("request failed "+response.status+" "+response.statusText+" : "+response.responseText);
        });
    }

    // define the callback object
    var callback = {
        success: success,
        failure: failure
    };
    
    var query = "CONSTRUCT { ?s a ?t } WHERE { ?s a ?t }";
    YAHOO.util.Connect.asyncRequest("GET", path+"?query="+escape(query), callback, null);
    runner.wait();
};

_testCase.testPostConstructQuery = function() {
    var runner = this;
   
    var success = function(response) {
        runner.resume(function() {         
            assert.isTrue(response.responseText.length>0);
            assert.isTrue(response.responseText.indexOf("rdf:RDF")>=0);
            log("select response: "+response.responseText, "test");
            var contentType = response.getResponseHeader["Content-Type"];
            log("received content type: "+contentType, "test");
            assert.isTrue(contentType.indexOf("application/rdf+xml")>=0, "expected content type 'application/rdf+xml'") 
        });
    }

    var failure = function(response) {
        runner.resume(function() {
            assert.fail("request failed "+response.status+" "+response.statusText+" : "+response.responseText);
        });
    }

    // define the callback object
    var callback = {
        success: success,
        failure: failure
    };
    
    var query = "CONSTRUCT { ?s a ?t } WHERE { ?s a ?t }";
    var content = "query="+escape(query);
    YAHOO.util.Connect.asyncRequest("POST", path, callback, content);
    runner.wait();
};

_testCase.testGetDescribeQuery = function() {
    var runner = this;
   
    var success = function(response) {
        runner.resume(function() {         
            assert.isTrue(response.responseText.length>0);
            assert.isTrue(response.responseText.indexOf("rdf:RDF")>=0);
            log("select response: "+response.responseText, "test");
            var contentType = response.getResponseHeader["Content-Type"];
            log("received content type: "+contentType, "test");
            assert.isTrue(contentType.indexOf("application/rdf+xml")>=0, "expected content type 'application/rdf+xml'") 
        });
    }

    var failure = function(response) {
        runner.resume(function() {
            assert.fail("request failed "+response.status+" "+response.statusText+" : "+response.responseText);
        });
    }

    // define the callback object
    var callback = {
        success: success,
        failure: failure
    };
    
    var query = "DESCRIBE * WHERE { ?s a ?t }";
    YAHOO.util.Connect.asyncRequest("GET", path+"?query="+escape(query), callback, null);
    runner.wait();
};

_testCase.testPostDescribeQuery = function() {
    var runner = this;
   
    var success = function(response) {
        runner.resume(function() {         
            assert.isTrue(response.responseText.length>0);
            assert.isTrue(response.responseText.indexOf("rdf:RDF")>=0);
            log("select response: "+response.responseText, "test");
            var contentType = response.getResponseHeader["Content-Type"];
            log("received content type: "+contentType, "test");
            assert.isTrue(contentType.indexOf("application/rdf+xml")>=0, "expected content type 'application/rdf+xml'") 
        });
    }

    var failure = function(response) {
        runner.resume(function() {
            assert.fail("request failed "+response.status+" "+response.statusText+" : "+response.responseText);
        });
    }

    // define the callback object
    var callback = {
        success: success,
        failure: failure
    };
    
    var query = "DESCRIBE * WHERE { ?s a ?t }";
    var content = "query="+escape(query);
    YAHOO.util.Connect.asyncRequest("POST", path, callback, content);
    runner.wait();
};

function setupLogger() {
	var logReader = new YAHOO.tool.TestLogger("logger");
	YAHOO.log("Logger setup done");	
}

function runTests() {
    path = document.getElementById("path").value;
    log("path: "+path);
	YAHOO.log("Running tests");
	YAHOO.tool.TestRunner.clear();
	YAHOO.tool.TestRunner.add(new YAHOO.tool.TestCase(_testCase));
	YAHOO.tool.TestRunner.run();
}

YAHOO.util.Event.onDOMReady(setupLogger);
//YAHOO.util.Event.onDOMReady(runTests);

