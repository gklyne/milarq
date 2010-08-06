package uk.ac.ox.zoo.sparqlite.tests;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import java.io.ByteArrayOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uk.ac.ox.zoo.sparqlite.*;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;

import junit.framework.TestCase;

public class ResponseWriterTest extends TestCase {
	
	private Log log = LogFactory.getLog(ResponseWriterTest.class);

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public final void testResponseWriter() {
		try {
			// create mock request
			HttpServletRequest request = createMock(HttpServletRequest.class);
	
			// set up expected sequence of calls on request 
			// expect ignore accept headers
			replay(request);
			
			// create mock response
			HttpServletResponse response = createMock(HttpServletResponse.class);
	
			// set up expected sequence of calls on response
			expect(response.isCommitted()).andReturn(false);
			replay(response);
			
			// create output stream
//			ByteArrayOutputStream out = new ByteArrayOutputStream();
			
			// create response writer
			ResponseWriter rw = new ResponseWriter(request, response);
		
			assertNotNull(rw);
			
			verify(request);
			verify(response);
	
		} catch ( Exception e ) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
	}

	public final void testDoWriteBoolean() {
		try {
			// create mock request
			HttpServletRequest request = createMock(HttpServletRequest.class);
	
			// set up expected sequence of calls on request 
			// expect ignore accept headers
			replay(request);
			
			// create mock response
			HttpServletResponse response = createMock(HttpServletResponse.class);
	
			// set up expected sequence of calls on response
			expect(response.isCommitted()).andReturn(false);
			response.setStatus(200);
			response.setContentType("application/sparql-results+json");
			response.setCharacterEncoding("UTF-8");
			response.flushBuffer();
			replay(response);
			
			// create output stream
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			
			// create response writer
			ResponseWriter rw = new ResponseWriter(request, response);
		
			// call doWriteBoolean
			rw.doWriteBoolean(true, out);
			
			// test stuff
			log.info("output: "+out.toString());
			
			assertTrue(out.toByteArray().length > 0);
			
			verify(request);
			verify(response);
	
		} catch ( Exception e ) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
	}

	public final void testDoWriteResultSet() {
		try {
			// create mock request
			HttpServletRequest request = createMock(HttpServletRequest.class);
	
			// set up expected sequence of calls on request 
			// expect ignore accept headers
			replay(request);
			
			// create mock response
			HttpServletResponse response = createMock(HttpServletResponse.class);
	
			// set up expected sequence of calls on response
			expect(response.isCommitted()).andReturn(false);
			response.setStatus(200);
			response.setContentType("application/sparql-results+json");
			response.setCharacterEncoding("UTF-8");
			response.flushBuffer();
			replay(response);
			
			// create output stream
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			
			// create response writer
			ResponseWriter rw = new ResponseWriter(request, response);
		
			// create a result set
			String filePath = "webapp/WEB-INF/tdb/rowling.ttl";
			Endpoint endpoint = new EndpointTDB(filePath);
			String queryString = "SELECT * WHERE { ?s ?p ?o }";
			Query query = QueryFactory.create(queryString,Syntax.syntaxSPARQL);
			QueryExecution qe = endpoint.getQueryExecution(query);
			ResultSet resultSet = qe.execSelect();
			
			// call doWriteBoolean
			rw.doWriteResultSet(resultSet, out);
			
			// test stuff
			log.info("output: "+out.toString());
			
			assertTrue(out.toByteArray().length > 0);
			
			verify(request);
			verify(response);
	
		} catch ( Exception e ) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
	}

	public final void testDoWriteModel() {
		try {
			// create mock request
			HttpServletRequest request = createMock(HttpServletRequest.class);
	
			// set up expected sequence of calls on request 
			// expect ignore accept headers
			replay(request);
			
			// create mock response
			HttpServletResponse response = createMock(HttpServletResponse.class);
	
			// set up expected sequence of calls on response
			expect(response.isCommitted()).andReturn(false);
			response.setStatus(200);
			response.setContentType("application/rdf+xml");
			response.setCharacterEncoding("UTF-8");
			response.flushBuffer();
			replay(response);
			
			// create output stream
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			
			// create response writer
			ResponseWriter rw = new ResponseWriter(request, response);
		
			// create a model
			String filePath = "webapp/WEB-INF/tdb/rowling.ttl";
			Endpoint endpoint = new EndpointTDB(filePath);
			String queryString = "CONSTRUCT { ?s ?p ?o } WHERE { ?s ?p ?o }";
			Query query = QueryFactory.create(queryString,Syntax.syntaxSPARQL);
			QueryExecution qe = endpoint.getQueryExecution(query);
			Model model = qe.execConstruct();
			
			// call doWriteBoolean
			rw.doWriteModel(model, out);
			
			// test stuff
			log.info("output: "+out.toString());
			
			assertTrue(out.toByteArray().length > 0);
			
			verify(request);
			verify(response);
	
		} catch ( Exception e ) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
	}

}
