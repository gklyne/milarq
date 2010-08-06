package uk.ac.ox.zoo.sparqlite;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;

/**
 * HEALTH WARNING: this processor is inherently unsafe and may deliver unpredictable results
 * FIXME
 * @author Alistair Miles
 */
public class ProcessorAsync extends Processor {

	public static final String QUERYEXECUTIONTIMEOUT = "sparqlite.processor.QueryExecutionTimeout";
	private Log log = LogFactory.getLog(ProcessorAsync.class);
	Executor executor;

	public ProcessorAsync(Endpoint endpoint, ServletConfig config, Executor executor) {
		super(endpoint, config);
		this.executor = executor;
		trace("initialised async request processor");
	}
	
	@Override public
	Object executeQuery(final Query q, final QueryExecution qe) throws UnexpectedException, AbortRequestException {
		trace("define callable for query execution");
		Callable<Object> callable = new Callable<Object>() {

			//@Override
			public Object call() throws Exception {
				trace("begin async query execution");
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
				} catch (java.lang.NullPointerException npe) {
					warn("caught null pointer exception during query execution");
				} catch (Exception ex) {
					error("caught unexpected exception during query execution");
					ex.printStackTrace();
				}
				trace("end async query execution");
				return result;
			}
			
		};
		
		Object result = null;
		
		FutureTask<Object> future = new FutureTask<Object>(callable);
		executor.execute(future);

		int timeout = Integer.parseInt(initParams.get(ProcessorAsync.QUERYEXECUTIONTIMEOUT));
		trace("query execution timeout: "+timeout);
		
		try {
			result = future.get(timeout, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			trace("caught interrupted exception");
			qe.abort();
			throw new UnexpectedException("interrupted exception during query execution", e);
		} catch (ExecutionException e) {
			trace("caught execution exception");
			qe.abort();
			throw new UnexpectedException("execution exception during query execution", e);
		} catch (TimeoutException e) {
			trace("query execution timeout");
			trace("abort query");
			qe.abort();
			
			// 408 Request Timeout
			// The client did not produce a request within the time that the 
			// server was prepared to wait. The client MAY repeat the request 
			// without modifications at any later time.
			// N.B. this may cause clients (e.g. YUI) to repeatedly send the same 
			// request.
//			throw new AbortRequestException(HttpServletResponse.SC_REQUEST_TIMEOUT, "query execution exceeded time allowed ("+timeout+" seconds)");
			
			// 403 Forbidden
			// The server understood the request, but is refusing to fulfill it. 
			// Authorization will not help and the request SHOULD NOT be repeated.
			throw new AbortRequestException(HttpServletResponse.SC_FORBIDDEN, "query execution exceeded time allowed ("+timeout+" seconds)");
		}
		
		return result;
	}


	@Override
	void finalise(QueryExecution queryExecution, Endpoint endpoint, HttpServletResponse response) {
		trace("finalising request processor");
		try {
			
			if ( queryExecution != null) {
				trace("close query execution");
				queryExecution.close();
			}
			
			if ( endpoint != null ) {
				trace("close endpoint");
				endpoint.close(); // N.B. if query execution timed out, this causes lots of log errors 
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
