package uk.ac.ox.zoo.sparqlite;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;

public abstract class Endpoint {
	
	protected String pathInfo = null;

	public abstract QueryExecution getQueryExecution(Query query) throws EndpointNotFoundException, UnexpectedException;
	
	abstract boolean exists() throws UnexpectedException;
	
	abstract void close() throws EndpointNotFoundException, UnexpectedException;
	
	String getPathInfo() {
		return pathInfo;
	}

}
