package uk.ac.ox.zoo.sparqlite.tests;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uk.ac.ox.zoo.sparqlite.*;
import uk.ac.ox.zoo.sparqlite.exceptions.EndpointNotFoundException;
import uk.ac.ox.zoo.sparqlite.exceptions.UnexpectedException;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.query.larq.IndexBuilderString;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.util.StringUtils;
import com.hp.hpl.jena.tdb.TDBFactory;

import junit.framework.TestCase;

public class EndpointTDBLARQTest extends TestCase {

	static Log log = LogFactory.getLog(EndpointTDBTest.class);
	
	static {
		log.info("create an index over test data");

		String tdbLoc = "webapp/WEB-INF/data/tdb/rowling";
		String luceneLoc = "webapp/WEB-INF/data/lucene/rowling";
		
		log.info("open rowling model");
		Model rowling = TDBFactory.createModel(tdbLoc);
		
		log.info("create rowling lucene index");
		IndexBuilderString larqBuilder = new IndexBuilderString(luceneLoc);
		larqBuilder.indexStatements(rowling.listStatements());
		larqBuilder.closeWriter();
		
		tdbLoc = "webapp/WEB-INF/data/tdb/tolkien";
		luceneLoc = "webapp/WEB-INF/data/lucene/tolkien";
		
		log.info("open tolkien model");
		Model tolkien = TDBFactory.createModel(tdbLoc);
		
		log.info("create tolkien lucene index");
		larqBuilder = new IndexBuilderString(luceneLoc);
		larqBuilder.indexStatements(tolkien.listStatements());
		larqBuilder.closeWriter();
		
		tdbLoc = "webapp/WEB-INF/data/tdb/allbooks";
		luceneLoc = "webapp/WEB-INF/data/lucene/allbooks";
		
		log.info("open allbooks dataset");
		Dataset allbooks = TDBFactory.createDataset(tdbLoc);
		
		log.info("create allbooks lucene index");
		larqBuilder = new IndexBuilderString(luceneLoc);
		for (Iterator<String> it = allbooks.listNames(); it.hasNext();) {
			String modelName = it.next().toString();
			log.info("indexing named model: "+modelName);
			Model model = allbooks.getNamedModel(modelName);
			larqBuilder.indexStatements(model.listStatements());
		}
		larqBuilder.closeWriter();
		
	}

	public EndpointTDBLARQTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testEndpointTDB() {
		
		String filePath;
		EndpointTDB endpoint;
		
		try {

			filePath = "webapp/WEB-INF/tdb/doesnotexist.ttl";
			endpoint = Utilities.createEndpointTDB( filePath );
			assertFalse(endpoint.exists());

			filePath = "webapp/WEB-INF/tdb/rowling-larq.ttl";
			endpoint = Utilities.createEndpointTDB( filePath );
			assertTrue(endpoint.exists());

		} catch (Exception ex) {
			ex.printStackTrace();
			fail(ex.getLocalizedMessage());
		}
		
	}
	
	public void testGetDataset() {
		
		String filePath;
		EndpointTDB endpoint;
		Dataset dataset;

		try {

			filePath = "webapp/WEB-INF/tdb/rowling-larq.ttl";
			endpoint = Utilities.createEndpointTDB( filePath );
			dataset = endpoint.getDataset();
			
			assertNotNull(dataset);
			
			Model model = dataset.getDefaultModel();
			assertFalse(model.isEmpty());
			
			Resource s = model.createResource("http://example.org/book/book1");
			Property p = model.createProperty("http://purl.org/dc/elements/1.1/title");
			String o = "Harry Potter and the Philosopher's Stone";
			assertTrue(model.contains(s, p, o));

		} catch (Exception ex) {
			ex.printStackTrace();
			fail(ex.getLocalizedMessage());
		}
		
		try {

			filePath = "webapp/WEB-INF/tdb/doesnotexists.ttl";
			endpoint = Utilities.createEndpointTDB( filePath );
			dataset = endpoint.getDataset();
			
			fail("this should never be reached, as exception should be thrown");

		} catch (EndpointNotFoundException enfe) {
			log.trace("caught expected exception");
			
		} catch (UnexpectedException ue) {
			ue.printStackTrace();
			fail(ue.getLocalizedMessage());
		}

	}
	
	public void testGetNamedGraphDataset() {
		
		String filePath;
		EndpointTDB endpoint;
		Dataset dataset;

		try {

			filePath = "webapp/WEB-INF/tdb/allbooks-larq.ttl";
			endpoint = Utilities.createEndpointTDB( filePath );
			dataset = endpoint.getDataset();
			
			assertNotNull(dataset);
			
			Model defaultModel = dataset.getDefaultModel();
//			assertFalse(defaultModel.isEmpty());
			assertTrue(defaultModel.isEmpty()); // default graph is empty
			
//			Resource s = defaultModel.createResource("http://example.org/book/book1");
//			Property p = defaultModel.createProperty("http://purl.org/dc/elements/1.1/title");
//			String o = "Harry Potter and the Philosopher's Stone";
//			assertTrue(defaultModel.contains(s, p, o));
			
			Model tolkien = dataset.getNamedModel("http://example.org/tolkien");
			assertFalse(tolkien.isEmpty());
			
			Resource s = tolkien.createResource("http://example.org/book2/book1");
			Property p = tolkien.createProperty("http://purl.org/dc/elements/1.1/title");
			String o = "The Hobbit";
			assertTrue(tolkien.contains(s, p, o));
			
			Model rowling = dataset.getNamedModel("http://example.org/rowling");
			assertFalse(rowling.isEmpty());
			
			s = rowling.createResource("http://example.org/book/book1");
			p = rowling.createProperty("http://purl.org/dc/elements/1.1/title");
			o = "Harry Potter and the Philosopher's Stone";
			assertTrue(rowling.contains(s, p, o));
			
		} catch (Exception ex) {
			ex.printStackTrace();
			fail(ex.getLocalizedMessage());
		}
		
	}

	public void testGetQueryExecution() {

		String filePath;
		EndpointTDB endpoint;
		QueryExecution execution;
		String queryString;
		Query query;
		
		try {
			
			filePath = "webapp/WEB-INF/tdb/rowling-larq.ttl";
			endpoint = Utilities.createEndpointTDB( filePath );
			assertTrue( endpoint.exists() );

			queryString = "ASK { ?s ?p ?o }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);
			execution = endpoint.getQueryExecution(query);
			
			assertNotNull(execution);
			
			assertTrue( "no items found in TDB/LARQ", execution.execAsk() );

			queryString = "SELECT * WHERE { ?s ?p ?o }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);
			execution = endpoint.getQueryExecution(query);
			
			assertNotNull(execution);
			
			ResultSet results = execution.execSelect();
			assertNotNull(results);
			assertTrue(results.hasNext());

		} catch (Exception ex) {
			ex.printStackTrace();
			fail(ex.getLocalizedMessage());
		}

	}
	
	public void testQueryExecutionWithNamedGraphs() {

		String filePath;
		EndpointTDB endpoint;
		QueryExecution execution;
		String queryString;
		Query query;
		
		try {
			
			filePath = "webapp/WEB-INF/tdb/allbooks-larq.ttl";
			endpoint = Utilities.createEndpointTDB( filePath );

			queryString = "ASK { ?s ?p ?o }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);
			execution = endpoint.getQueryExecution(query);
			
			assertNotNull(execution);
//			assertTrue(execution.execAsk());
			assertFalse(execution.execAsk()); // default graph is empty

			queryString = "ASK { GRAPH <http://example.org/rowling> { ?s ?p ?o } }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);
			execution = endpoint.getQueryExecution(query);
			
			assertNotNull(execution);
			assertTrue(execution.execAsk());

			queryString = "ASK { GRAPH <http://example.org/tolkien> { ?s ?p ?o } }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);
			execution = endpoint.getQueryExecution(query);
			
			assertNotNull(execution);
			assertTrue(execution.execAsk());

			queryString = "ASK { GRAPH <http://example.org/doesnotexist> { ?s ?p ?o } }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);
			execution = endpoint.getQueryExecution(query);
			
			assertNotNull(execution);
			assertFalse(execution.execAsk());

			queryString = "SELECT * WHERE { ?s ?p ?o }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);
			execution = endpoint.getQueryExecution(query);
			
			assertNotNull(execution);
			ResultSet results = execution.execSelect();
			assertNotNull(results);
//			assertTrue(results.hasNext());
			assertFalse(results.hasNext()); // default graph is empty

			queryString = "SELECT * WHERE { GRAPH <http://example.org/rowling> { ?s ?p ?o } }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);
			execution = endpoint.getQueryExecution(query);
			
			assertNotNull(execution);
			results = execution.execSelect();
			assertNotNull(results);
			assertTrue(results.hasNext());

			queryString = "SELECT * WHERE { GRAPH <http://example.org/tolkien> { ?s ?p ?o } }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);
			execution = endpoint.getQueryExecution(query);
			
			assertNotNull(execution);
			results = execution.execSelect();
			assertNotNull(results);
			assertTrue(results.hasNext());

		} catch (Exception ex) {
			ex.printStackTrace();
			fail(ex.getLocalizedMessage());
		}

	}	
	
	public void testQueryExecutionWithLarq() {
		
		String filePath;
		EndpointTDB endpoint;
		QueryExecution execution;
		String queryString;
		Query query;
		
		try {

			filePath = "webapp/WEB-INF/tdb/rowling-larq.ttl"; // jena assembler store description for TDB store
			endpoint = Utilities.createEndpointTDB( filePath );
			
			queryString = StringUtils.join("\n", new String[] {
					"PREFIX pf: <http://jena.hpl.hp.com/ARQ/property#>",
					"ASK { ?lit pf:textMatch 'potter' }"
			});
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);
			
			execution = endpoint.getQueryExecution(query);
			
			assertTrue(execution.execAsk());
			
		} catch (Throwable e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
		
	}
	
	public void testQueryExecutionWithLarqAndNamedGraphs() {
		
		String filePath;
		EndpointTDB endpoint;
		QueryExecution execution;
		String queryString;
		Query query;
		
		try {

			filePath = "webapp/WEB-INF/tdb/allbooks-larq.ttl"; // jena assembler store description for TDB store
			endpoint = Utilities.createEndpointTDB( filePath );
			
			queryString = StringUtils.join("\n", new String[] {
					"PREFIX pf: <http://jena.hpl.hp.com/ARQ/property#>",
					"ASK { GRAPH <http://example.org/rowling> { ?lit pf:textMatch 'potter' } }"
			});
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);
			
			execution = endpoint.getQueryExecution(query);
			
			assertTrue(execution.execAsk());
			
			queryString = StringUtils.join("\n", new String[] {
					"PREFIX pf: <http://jena.hpl.hp.com/ARQ/property#>",
					"ASK { GRAPH <http://example.org/tolkien> { ?lit pf:textMatch 'hobbit' } }"
			});
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);
			
			execution = endpoint.getQueryExecution(query);
			
			assertTrue(execution.execAsk());
			
		} catch (Throwable e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
		
	}
	
	public void testQueryExecutionWithLarqAndNamedGraphsAndMixedDataset() {
		
		String filePath;
		EndpointTDB endpoint;
		QueryExecution execution;
		String queryString;
		Query query;
		
		try {

			filePath = "webapp/WEB-INF/tdb/allbooks-mixed-larq.ttl"; // jena assembler store description for TDB store
			endpoint = Utilities.createEndpointTDB( filePath );
			
			queryString = StringUtils.join("\n", new String[] {
					"PREFIX pf: <http://jena.hpl.hp.com/ARQ/property#>",
					"ASK { GRAPH <http://example.org/rowling> { ?lit pf:textMatch 'potter' } }"
			});
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);
			
			execution = endpoint.getQueryExecution(query);
			
			assertTrue(execution.execAsk());
			
			queryString = StringUtils.join("\n", new String[] {
					"PREFIX pf: <http://jena.hpl.hp.com/ARQ/property#>",
					"ASK { GRAPH <http://example.org/tolkien> { ?lit pf:textMatch 'hobbit' } }"
			});
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);
			
			execution = endpoint.getQueryExecution(query);
			
			assertTrue(execution.execAsk());
			
		} catch (Throwable e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
		
	}
	
	public void testGuardExists() {
		try {
			
			String filePath = "webapp/WEB-INF/tdb/doesnotexist.ttl";

			// start testing
			EndpointTDB endpoint = Utilities.createEndpointTDB( filePath );
			assertFalse(endpoint.exists());
			
			try {
				endpoint.getDataset();
				fail("should throw not found exception");
			} catch (EndpointNotFoundException ex) {
				// expected
			}

			try {
				endpoint.getQueryExecution(null);
				fail("should throw not found exception");
			} catch (EndpointNotFoundException ex) {
				// expected
			}


		} catch (Exception ex) {
			ex.printStackTrace();
			fail(ex.getLocalizedMessage());
		}
		
	}
}
