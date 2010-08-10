package uk.ac.ox.zoo.sparqlite;

import java.io.File;
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
import com.hp.hpl.jena.sparql.core.assembler.AssemblerUtils;
import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDFS;

public class EndpointTDB extends Endpoint {
	
    Log log = LogFactory.getLog(EndpointTDB.class);

    public Dataset dataset = null;

    String storeDescFilePath = null;
	boolean exists = false;
	IndexLARQ index = null;
	boolean lookedForIndex = false;
	
	final Config config;
	
	static {
		// required to make the generic dataset assembler work with tdb (0.7) graphs
		AssemblerUtils.init();
	}

    public EndpointTDB(HttpServletRequest request, ServletContext context) {
        this( Config.neutral, request, context );
    }
    
	public EndpointTDB(Config config, HttpServletRequest request, ServletContext context) {
		// convention for mapping request path to store description file path
	    this.config = config;
		this.pathInfo = request.getPathInfo();
		this.storeDescFilePath = context.getRealPath("WEB-INF/tdb"+pathInfo+".ttl");
		init();
	}
	
	public EndpointTDB( String storeDescFilePath ) {
		this.storeDescFilePath = storeDescFilePath;
		this.config = Config.fake(storeDescFilePath);
		init();
	}

	void init() {

		log.trace("initialise endpoint");

		log.trace("check store desc file exists");
		exists = new File(storeDescFilePath).exists();
		
	}

	@Override public
	QueryExecution getQueryExecution(Query query) throws EndpointNotFoundException, UnexpectedException {
		getDataset();
		QueryExecution execution = null;
		try {
			log.trace("create query execution");
			execution = QueryExecutionFactory.create(query, dataset);
			// TODO: control UnionDefaultGraph setting with option
			execution.getContext().set(TDB.symUnionDefaultGraph, false); // HERE
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
//				log.trace("closing default model");
//				dataset.getDefaultModel().close();
//				for (Iterator it=dataset.listNames(); it.hasNext(); ) {
//					String name = (String) it.next();
//					log.trace("closing named model: "+name);
//					dataset.getNamedModel(name).close();
//				}
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
			    log.trace( "assembling dataset and optional LARQ index from " + storeDescFilePath );
// OLD HAD:		storeDescModel.read(storeDescFile.toURI().toString(), "TURTLE");
			    Model model = FileManager.get().loadModel( storeDescFilePath );
			    model.add( Vocab.TDB_Dataset, RDFS.subClassOf, Vocab.RDF_Dataset );
			    // model.write( System.err, "TTL" ); // DEBUG
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
		if (!exists()) throw new EndpointNotFoundException("no store description file found for path: "+storeDescFilePath);
	}
	
	@Override public boolean exists() throws UnexpectedException {
		return exists;
	}

}
