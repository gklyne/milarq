package uk.ac.ox.zoo.sparqlite;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;

import uk.ac.ox.zoo.sparqlite.config.Config;
import uk.ac.ox.zoo.sparqlite.config.Vocab;

import uk.ac.ox.zoo.sparqlite.exceptions.EndpointNotFoundException;
import uk.ac.ox.zoo.sparqlite.exceptions.UnexpectedException;

import com.hp.hpl.jena.assembler.Assembler;
import com.hp.hpl.jena.assembler.AssemblerHelp;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.larq.IndexLARQ;
import com.hp.hpl.jena.query.larq.LARQ;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDFS;

public class EndpointTDB extends Endpoint {
	
    Log log = LogFactory.getLog(EndpointTDB.class);

    Dataset dataset = null;
	IndexLARQ index = null;
	
	final Config config;
	
    public EndpointTDB(HttpServletRequest request, ServletContext context) {
        this
            ( new Config( "", context.getRealPath("WEB-INF/tdb" + request.getPathInfo() +".ttl") ) 
            , request
            , context 
            );
    }
    
	public EndpointTDB( Config config, HttpServletRequest request, ServletContext context ) {
	    this.config = config;
	}
	
	public EndpointTDB( String storeDescFilePath ) {
	    this.config = Config.fake(storeDescFilePath);
	}

	@Override public QueryExecution getQueryExecution(Query query) 
	    throws EndpointNotFoundException, UnexpectedException {
		getDataset();
		QueryExecution execution = null;
		try {
			log.trace("create query execution");
			execution = QueryExecutionFactory.create(query, dataset);
			// TODO: control UnionDefaultGraph setting with option
			execution.getContext().set( TDB.symUnionDefaultGraph, config.getSymUnionDefaultGraph() ); 
			if (index != null) {
				log.trace("set larq index");
				LARQ.setDefaultIndex(execution.getContext(), index);
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
			    String sd = config.getStoreDescFilePath();
			    log.trace( "assembling dataset and optional LARQ index from " + sd );
			    Model model = FileManager.get().loadModel( sd );
			    model.add( Vocab.TDB_Dataset, RDFS.subClassOf, Vocab.RDF_Dataset );
			    Resource root = AssemblerHelp.singleRoot( model, Vocab.RDF_Dataset );
			    log.trace( "dataset root is " + root );
			    dataset = (Dataset) Assembler.general.open( root );
			    setLARQIndexIfPresent( root );				
			} catch (Throwable ex) {
	        	String message = "unexpected error: " + ex.getLocalizedMessage();
	        	throw new UnexpectedException(message, ex);
			}
		}
		return dataset;
	}

    private void setLARQIndexIfPresent( Resource root ) throws UnexpectedException
        {
        Statement locStatement = root.getProperty( Vocab.LARQ_Location );
        if (locStatement == null)
            log.trace( "no LARQ location provided" );
        else
            {
            String location = locStatement.getString();
            log.trace( "LARQ location " + location + " provided" );
            setLARQIndex( location );			        
            }
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
		        ("no store description file found for path: " + config.getStoreDescFilePath() );
	}
	
	@Override public boolean exists() throws UnexpectedException {
		return config.getStoreDescFileExists();
	}

    @Override public String getPathInfo()
        { return config.GETPATHINFO(); }

}
