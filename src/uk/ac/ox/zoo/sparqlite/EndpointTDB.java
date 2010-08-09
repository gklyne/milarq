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

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.larq.IndexLARQ;
import com.hp.hpl.jena.query.larq.LARQ;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.sparql.core.assembler.AssemblerUtils;
import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.vocabulary.RDF;

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

	IndexLARQ getLarqIndex(Resource storeDescRoot) throws UnexpectedException {
		Property larqLocation = storeDescRoot.getModel().createProperty("http://purl.org/net/sparqlite/vocab#larqLocation");
		
		log.trace("look for a larq lucene index location");
		if (storeDescRoot.hasProperty(larqLocation)) {

			String location = storeDescRoot.getProperty(larqLocation).getString();
			log.trace("found larq location: "+location);

			log.trace("instantiate a larq index");

			// N.B. this doesn't work, probably because IndexBuilderString wipes the index before creating a new one...
//				IndexBuilderString larqBuilder = new IndexBuilderString(location);
//				index = larqBuilder.getIndex();
			
			try {
				index = new IndexLARQ(IndexReader.open(location));
			} catch (CorruptIndexException e) {
				throw new UnexpectedException("unexpected exception opening lucene index reader: "+e.getLocalizedMessage(), e);
			} catch (IOException e) {
				throw new UnexpectedException("unexpected exception opening lucene index reader: "+e.getLocalizedMessage(), e);
			}
		} 
		else {
			log.trace("no larq location found");
			// larqLocation not required
//				throw new UnexpectedException("no sparqlite:larqLocation property found on dataset description");
		}
		return index;
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

				log.trace("assemble dataset");
				log.trace("check if pure TDB or mixed dataset");
				
				log.trace("read the assembler store description into a model");
				File storeDescFile = new File(storeDescFilePath);
				Model storeDescModel = ModelFactory.createDefaultModel();
				storeDescModel.read(storeDescFile.toURI().toString(), "TURTLE");
				
				log.trace("find the dataset description root");
				
				ResIterator list = storeDescModel.listSubjectsWithProperty(RDF.type, Vocab.RDF_Dataset);
				if (list.hasNext()) {
					log.trace("found mixed dataset");
					Resource storeDescRoot = list.nextResource();
					getLarqIndex(storeDescRoot);
					log.trace("assemble mixed dataset via generic dataset factory");
					log.trace("storeDescFilePath: " + storeDescFilePath );
					dataset = DatasetFactory.assemble(storeDescFilePath);
					return dataset;
				}
				
				list = storeDescModel.listSubjectsWithProperty(RDF.type, Vocab.TDB_Dataset);
				if (list.hasNext()) {
					log.trace("found TDB dataset");
					Resource storeDescRoot = list.nextResource();
					getLarqIndex(storeDescRoot);
					log.trace("assemble TDB dataset via TDB factory, using " + storeDescFilePath );
					dataset = TDBFactory.assembleDataset(storeDescFilePath);
					System.err.println( "|>> dataset := " + dataset );
					return dataset;
				}
								
			} catch (Throwable ex) {
	        	String message = "unexpected error: "+ex.getLocalizedMessage();
	        	throw new UnexpectedException(message, ex);
			}
			if (dataset == null) {
				throw new UnexpectedException("could not assemble dataset; assembler file does not contain a dataset node?");
			}
		}
		return dataset;
	}
	
	void guardExists() throws UnexpectedException, EndpointNotFoundException {
		log.trace("exists guard condition");
		if (!exists()) throw new EndpointNotFoundException("no store description file found for path: "+storeDescFilePath);
	}
	
	@Override public boolean exists() throws UnexpectedException {
		return exists;
	}

}
