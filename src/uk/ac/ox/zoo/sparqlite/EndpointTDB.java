package uk.ac.ox.zoo.sparqlite;

import java.io.IOException;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;

import uk.ac.ox.zoo.sparqlite.config.Config;
import uk.ac.ox.zoo.sparqlite.exceptions.EndpointNotFoundException;
import uk.ac.ox.zoo.sparqlite.exceptions.UnexpectedException;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.query.larq.IndexLARQ;
import com.hp.hpl.jena.query.larq.LARQ;
import com.hp.hpl.jena.sparql.util.Context;

public class EndpointTDB extends Endpoint {
	
    Log log = LogFactory.getLog(EndpointTDB.class);

    Dataset dataset = null;
	IndexLARQ index = null;
	
	final Config config;
	
	/**
	    Create an endpoint with the given configuration.
	*/
	public EndpointTDB( Config config ) {
	    this.config = config;
	}
	
	@Override public QueryExecution getQueryExecution(Query query) 
	    throws EndpointNotFoundException, UnexpectedException {
		getDataset();
		QueryExecution execution = null;
		try {
			log.trace("create query execution");
			execution = QueryExecutionFactory.create(query, dataset);
			Context context = execution.getContext();
			config.setContext( context );
			if (index != null) {
				log.trace("set larq index");
				LARQ.setDefaultIndex(context, index);
			}
		} catch (Throwable ex) {
        	String message = "unexpected error: "+ex.getLocalizedMessage();
        	throw new UnexpectedException(message, ex);
		}
		return execution;
	}

	
	@Override void close() throws UnexpectedException {
		if (index != null) {
			try {
				log.trace("close larq index");
				index.close();
			} catch (Throwable ex) {
	        	String message = "unexpected error: "+ex.getLocalizedMessage();
	        	throw new UnexpectedException(message, ex);
			}
		}
		if (dataset != null) {
			try {
				log.trace("close dataset");
				dataset.close();
			} catch (Throwable ex) {
	        	String message = "unexpected error: "+ex.getLocalizedMessage();
	        	throw new UnexpectedException(message, ex);
			}
		}
	}

	/**
	 * @return
	 * @throws EndpointNotFoundException
	 * @throws UnexpectedException
	 */
	public Dataset getDataset() throws EndpointNotFoundException, UnexpectedException {
		guardExists();
		if (dataset == null) {
			try {
			    dataset = config.getDataset();
			    String location = config.getLARQIndexLocation();
			    if (location != null) setLARQIndex( location );
			} catch (Throwable ex) {
	        	String message = "unexpected error: " + ex.getLocalizedMessage();
	        	throw new UnexpectedException(message, ex);
			}
		}
		return dataset;
	}

    private void setLARQIndex( String location ) throws UnexpectedException
        {
        try 
            { index = new IndexLARQ(IndexReader.open(location)); } 
        catch (CorruptIndexException e) 
            { throw new UnexpectedException("unexpected exception opening lucene index reader: " + e.getLocalizedMessage(), e); } 
        catch (IOException e) 
            { throw new UnexpectedException("unexpected exception opening lucene index reader: " + e.getLocalizedMessage(), e); }
        }
	
	void guardExists() throws UnexpectedException, EndpointNotFoundException {
		log.trace("exists guard condition");
		if (!exists()) 
		    throw new EndpointNotFoundException
		        ("no store description file found for path: " + config.getStoreName() );
	}
	
	@Override public boolean exists() throws UnexpectedException {
		return config.getStoreExists();
	}

    @Override public String getPathInfo()
        { return config.getPathInfo(); }

}
