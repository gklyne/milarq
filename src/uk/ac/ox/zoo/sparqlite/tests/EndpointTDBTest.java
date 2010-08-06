package uk.ac.ox.zoo.sparqlite.tests;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uk.ac.ox.zoo.sparqlite.*;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import junit.framework.TestCase;

public class EndpointTDBTest extends TestCase {

	static Log log = LogFactory.getLog(EndpointTDBTest.class);
	
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
			endpoint = new EndpointTDB(filePath);
			assertFalse(endpoint.exists());

			filePath = "webapp/WEB-INF/tdb/rowling.ttl";
			endpoint = new EndpointTDB(filePath);
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

			filePath = "webapp/WEB-INF/tdb/rowling.ttl";
			endpoint = new EndpointTDB(filePath);
			dataset = endpoint.getDataset();
			
			assertNotNull(dataset);
			
			Model model = dataset.getDefaultModel();
			assertFalse(model.isEmpty());
			
			log.trace("listing rowling model...");
			StmtIterator stit = model.listStatements();
			while (stit.hasNext()) {
				log.trace(stit.nextStatement().toString());
			}
			
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
			endpoint = new EndpointTDB(filePath);
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

			filePath = "webapp/WEB-INF/tdb/allbooks.ttl";
			endpoint = new EndpointTDB(filePath);
			dataset = endpoint.getDataset();
			
			assertNotNull(dataset);
			
//			Model rowling = dataset.getDefaultModel();
//			assertFalse(rowling.isEmpty());
//			
//			Resource s = rowling.createResource("http://example.org/book/book1");
//			Property p = rowling.createProperty("http://purl.org/dc/elements/1.1/title");
//			String o = "Harry Potter and the Philosopher's Stone";
//			assertTrue(rowling.contains(s, p, o));
			
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
			
			filePath = "webapp/WEB-INF/tdb/rowling.ttl";
			endpoint = new EndpointTDB(filePath);

			queryString = "ASK { ?s ?p ?o }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);
			execution = endpoint.getQueryExecution(query);
			
			assertNotNull(execution);
			
			boolean result = execution.execAsk();
			assertTrue(result);

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
			
			filePath = "webapp/WEB-INF/tdb/allbooks.ttl";
			endpoint = new EndpointTDB(filePath);

			queryString = "ASK { ?s ?p ?o }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);
			execution = endpoint.getQueryExecution(query);
			
			assertNotNull(execution);
//			assertTrue(execution.execAsk());
			assertFalse(execution.execAsk()); // no content in default graph

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
			assertFalse(results.hasNext()); // no content in default graph

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
	
	public void testQueryExecutionWithMixedDatasetAndNamedGraphs() {

		String filePath;
		EndpointTDB endpoint;
		QueryExecution execution;
		String queryString;
		Query query;
		
		try {
			
			filePath = "webapp/WEB-INF/tdb/allbooks-mixed.ttl";
			endpoint = new EndpointTDB(filePath);

			queryString = "ASK { ?s ?p ?o }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);
			execution = endpoint.getQueryExecution(query);
			
			assertNotNull(execution);
//			assertTrue(execution.execAsk());
			assertFalse(execution.execAsk()); // no content in default graph

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
			assertFalse(results.hasNext()); // no content in default graph

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
	
	public void testGuardExists() {
		try {
			
			String filePath = "webapp/WEB-INF/tdb/doesnotexist.ttl";

			// start testing
			EndpointTDB endpoint = new EndpointTDB(filePath);
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
