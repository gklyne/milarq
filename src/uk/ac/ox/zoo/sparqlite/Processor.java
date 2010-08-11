package uk.ac.ox.zoo.sparqlite;

import java.io.IOException;
import java.util.Date;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uk.ac.ox.zoo.sparqlite.exceptions.*;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;

public class Processor {

	protected Endpoint endpoint = null;
	protected int count = -1;
	private Log log = LogFactory.getLog(Processor.class);
	protected Date start, before, after, end;
	protected ServletConfig config;
	protected InitParams initParams;
	protected static int requestCount = 0;

	public Processor(Endpoint endpoint, ServletConfig config) {
		
		this.endpoint = endpoint;
		this.count = ++requestCount;
		this.config = config;
		this.initParams = new InitParams(config);

		trace("initialised request processor");
	}
	
	public void exec(HttpServletRequest request, HttpServletResponse response) {
		RequestWrapper wrapper = null;
		Query query = null;
		QueryExecution qe = null;
		Object result = null;
		info( "begin processing request for path "+endpoint.getPathInfo() );
		try {

			start = new Date();
			before = new Date();
			
			trace("check if the endpoint exists");
			checkEndpointExists();
			after = new Date(); trace("completed check endpoint exists in "+(after.getTime()-before.getTime())+" ms"); before = after;
			
			trace("check the well-formedness of the request");
			wrapper = wrapRequest(request);
			after = new Date(); trace("completed check request form in "+(after.getTime()-before.getTime())+" ms"); before = after;

			trace("apply query policy rules");
			query = applyQueryPolicy(wrapper.getQuery());
			after = new Date();	trace("completed query policy in "+(after.getTime()-before.getTime())+" ms"); before = after;

			trace("create a query execution");
			qe = createQueryExecution(query, this.endpoint);
			after = new Date();	trace("completed create query execution in "+(after.getTime()-before.getTime())+" ms"); before = after;
			
			trace("execute the query");
			result = executeQuery(query, qe);
			after = new Date();	trace("completed execute query in "+(after.getTime()-before.getTime())+" ms"); before = after;
			
			trace("serialise and send the response");
			writeResponse(result, request, response);
			after = new Date();	trace("completed send response in "+(after.getTime()-before.getTime())+" ms"); before = after;

		} catch ( AbortRequestException are ) {
			
			trace("abort the request");
			abortRequest(are, response);
			
		} catch ( UnexpectedException une ) {
			
			trace("handle an unexpected exception");
			handleUnexpectedException(une, response);
			
		} catch ( Exception e ) {
			
			trace("handle uncaught exception");
			handleUncaughtException(e, response);
			
		}
		finally {
			
			trace("perform any final cleanup, release resources etc.");
			finalise(qe, endpoint, response);

			end = new Date();
			info("request processing completed in "+(end.getTime()-start.getTime())+" ms");
			
		}
	}

	public void checkEndpointExists() throws UnexpectedException, AbortRequestException {
		if (!endpoint.exists()) {
			String message = "no endpoint found for path: "+endpoint.getPathInfo();
			trace("404 not found");
			throw new AbortRequestException(HttpServletResponse.SC_NOT_FOUND, message);
		}
	}

	public RequestWrapper wrapRequest(HttpServletRequest req) throws AbortRequestException, UnexpectedException {
		try {
			RequestWrapper wrapped = RequestWrapper.newInstance(req, initParams);
			info("query: "+wrapped.getQueryString());
			return wrapped;
		} catch (MalformedRequestException e) {
			String message = "request is not well-formed: "+e.getLocalizedMessage();
			throw new AbortRequestException(HttpServletResponse.SC_BAD_REQUEST, message);
		} catch (NotImplementedException e) {
			String message = "request is not implemented: "+e.getLocalizedMessage();
			throw new AbortRequestException(HttpServletResponse.SC_NOT_IMPLEMENTED, message);
		} 
		
	}
	
	public Query applyQueryPolicy(Query query) throws AbortRequestException, UnexpectedException {
		try {
			QueryPolicy policy = new QueryPolicy(initParams);
			return policy.apply(query);
		} catch (QueryPolicyException qpe) {
			String message = "request is not permitted by query policy rules: "+qpe.getLocalizedMessage();
			throw new AbortRequestException(HttpServletResponse.SC_FORBIDDEN, message);
		}
	}

	public QueryExecution createQueryExecution(Query query, Endpoint endpoint) throws UnexpectedException {
		try {
			QueryExecution qe = endpoint.getQueryExecution(query);
			return qe;
		} catch (EndpointNotFoundException e) {
			throw new UnexpectedException("attempted to create query execution but endpoint not found",e);
		} 
	}

	public Object executeQuery(Query q, QueryExecution qe) throws UnexpectedException, AbortRequestException {
		Object result = null;
		try {
			if ( q.isSelectType() ) {
				trace("execute SELECT query");
				result = qe.execSelect();
			} else if ( q.isAskType() ) {
				trace("execute ASK query");
				result = new Boolean(qe.execAsk());
			} else if ( q.isConstructType() ) {
				trace("execute CONSTRUCT query");
				result = qe.execConstruct();
			} else if ( q.isDescribeType() ) {
				trace("execute DESCRIBE query");
				result = qe.execDescribe();
			}
		} catch ( Throwable ex ) {
        	String message = "caught exception during query execution: "+ex.getLocalizedMessage();
        	throw new UnexpectedException(message, ex);
		} 	
		if (result == null) {
			throw new UnexpectedException("query execution result is null");
		}
		return result;
	}

	void writeResponse(Object result, HttpServletRequest req, HttpServletResponse res) throws UnexpectedException {
		ResponseWriter writer = new ResponseWriter(req, res);
		writer.doWrite(result);
	}

	void abortRequest(AbortRequestException are, HttpServletResponse response) {
		warn("aborting request: "+are.getMessage());
		sendError(are.getResponseCode(), are.getMessage(), response);
	}
	
	void handleUnexpectedException(UnexpectedException une, HttpServletResponse response) {
		error("caught unexpected exception: "+une.getMessage());
		if (une.getCause() != null) 
			une.getCause().printStackTrace();
		sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, une.getLocalizedMessage(), response);
	}
	
	void handleUncaughtException(Exception e, HttpServletResponse response) {
		error("exception not caught elsewhere: "+e.getMessage());
		e.printStackTrace();
		sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getLocalizedMessage(), response);
	}
	
	void sendError(int code, String details, HttpServletResponse response) {
		trace("try to send error response, code: "+code+", details: "+details);
		if ( !response.isCommitted() ) {
			try {
				// try to send an error, if response not yet committed
//				response.sendError(code, details);
				response.setStatus(code);
				response.setContentType("text/plain");
				response.setContentLength(details.length());
				response.getWriter().print(details);
				
			} catch ( IllegalStateException ilex ) {
				// probably won't ever be thrown, if we first check whether response is committed
				error("could not send error: "+ilex.getMessage());
				ilex.printStackTrace();
				
			} catch (IOException ex) {
				error("caught IO exception sending error response: "+ex.getLocalizedMessage());
				ex.printStackTrace();
			}
		} else {
			warn("cannot send error response because already committed");
		}
	}

	void finalise(QueryExecution queryExecution, Endpoint endpoint, HttpServletResponse response) {
		trace("finalising request handler");
		try {
			
			if ( queryExecution != null) {
				trace("close query execution");
				queryExecution.close();
			}
			
			if ( endpoint != null ) {
				trace("close endpoint");
				endpoint.close();
			}

			trace("flush buffer");
			response.flushBuffer();
			
		} catch ( Exception ex ) {
			error("error finalising request handler: "+ex.getLocalizedMessage());
			ex.printStackTrace();
		}
	}


	private void trace( String message ) {
		log.trace("["+count+"] "+message + " ["+Thread.currentThread().getName()+"]");
	}
	
	private void info( String message ) {
		log.info("["+count+"] "+message);
	}

	private void warn( String message ) {
		log.warn("["+count+"] "+message);
	}

	private void error( String message ) {
		log.error("["+count+"] "+message);
	}

}
