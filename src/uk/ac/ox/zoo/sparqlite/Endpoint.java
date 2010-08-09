package uk.ac.ox.zoo.sparqlite;

import uk.ac.ox.zoo.sparqlite.exceptions.EndpointNotFoundException;
import uk.ac.ox.zoo.sparqlite.exceptions.UnexpectedException;

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
