package uk.ac.ox.zoo.sparqlite;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uk.ac.ox.zoo.sparqlite.exceptions.*;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QueryParseException;
import com.hp.hpl.jena.query.Syntax;

public class RequestWrapper {

	Query query = null;
	String queryString = null;
	String method = null;
	RequestHeaders headers = null;
	RequestParams params = null;
	Log log = LogFactory.getLog(RequestWrapper.class);
	InitParams initParams = null;
	
	public static final String QUERYSYNTAX = "sparqlite.processor.QuerySyntax";
	static final String SYNTAXARQ = "ARQ";

	public RequestWrapper(String method, RequestHeaders headers, RequestParams params, InitParams initParams) 
	throws UnexpectedException, MalformedRequestException, NotImplementedException {
		if (method == null || headers == null || params == null) {
			throw new UnexpectedException("one or more of required arguments was null; method: "+method+"; headers: "+headers+"; params: "+params);
		}
		this.method = method;
		this.headers = headers;
		this.params = params;
		this.initParams = initParams;
		init();
	}
	
	public RequestWrapper(String method, RequestHeaders headers, RequestParams params) throws UnexpectedException, MalformedRequestException, NotImplementedException {
		if (method == null || headers == null || params == null) {
			throw new UnexpectedException("one or more of required arguments was null; method: "+method+"; headers: "+headers+"; params: "+params);
		}
		this.method = method;
		this.headers = headers;
		this.params = params;
		init();
	}
	
	void init() throws NotImplementedException, MalformedRequestException {
		
		if (method.equals("GET")) {
			log.trace("processing GET");
		} else if (method.equals("POST")) {
			log.trace("processing POST");
			checkContentType(headers);
			log.trace("content type ok");
		} else {
			throw new MalformedRequestException("only GET and POST are allowed");
		}
		
		queryString = params.getFirst("query");
		if (queryString == null) {
			throw new MalformedRequestException("request must include 'query' parameter; none found");
		}
		log.trace("found query param: "+queryString);
		
		log.trace("check query syntax");
		Syntax syntax = Syntax.syntaxSPARQL; // SPARQL by default
		if (initParams != null) {
			String syntaxString = initParams.get(QUERYSYNTAX);
			if (syntaxString != null) {
				if (syntaxString.equals(SYNTAXARQ)) {
					syntax = Syntax.syntaxARQ;
					log.trace("using ARQ syntax");
				}
			}
		}
		
		try { // to parse the query
			query = QueryFactory.create(queryString, syntax) ;
		} catch ( QueryParseException qe ) {
			String message = "malformed query: "+qe.getLocalizedMessage();
			throw new MalformedRequestException(message);
		}
		log.trace("parsed query ok");
	}


	public static RequestWrapper newInstance(HttpServletRequest request) 
	throws MalformedRequestException, NotImplementedException, UnexpectedException {
		String method = request.getMethod();
		RequestHeaders headers = new RequestHeaders(request);
		RequestParams params = new RequestParams(request);
		return new RequestWrapper(method,headers,params);
	}
	
	public static RequestWrapper newInstance(HttpServletRequest request, ServletConfig config)
	throws MalformedRequestException, NotImplementedException, UnexpectedException {
		String method = request.getMethod();
		RequestHeaders headers = new RequestHeaders(request);
		RequestParams params = new RequestParams(request);
		InitParams initParams = new InitParams(config);
		return new RequestWrapper(method,headers,params,initParams);
	}	
		
	static RequestWrapper newInstance(HttpServletRequest request, InitParams initParams)
	throws MalformedRequestException, NotImplementedException, UnexpectedException {
		String method = request.getMethod();
		RequestHeaders headers = new RequestHeaders(request);
		RequestParams params = new RequestParams(request);
		return new RequestWrapper(method,headers,params,initParams);
	}	
		
	void checkContentType(RequestHeaders headers) throws NotImplementedException {
		log.trace("checking content type of POST request");
		String contentType = headers.getFirst("Content-Type");
		if (contentType != null) {
			log.trace("found content type: "+contentType);
			String[] tokens = contentType.split(";");
			log.trace("split by ';' first token: '"+tokens[0]+"'");
			if (!tokens[0].trim().equalsIgnoreCase("application/x-www-form-urlencoded")) {
				throw new NotImplementedException("only application/x-www-form-urlencoded is supported");
			}
			if (tokens.length > 1 && tokens[1].indexOf("charset=")>=0) {
				String charset = tokens[1].trim().substring(8).trim();
				if (!charset.equalsIgnoreCase("UTF-8")) {
					throw new NotImplementedException("only charset UTF-8 is supported");
				}
			}			
		}
	}
	
	RequestHeaders getHeaders() {
		return headers;
	}
	
	RequestParams getParams() {
		return params;
	}
	
	String getMethod() {
		return method;
	}

	public Query getQuery() {
		return query;
	}

	public boolean isWellFormed() {
		return (query != null);
	}

	String getQueryString() {
		return queryString;
	}

}
