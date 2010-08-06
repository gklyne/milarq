package uk.ac.ox.zoo.sparqlite.tests;

import java.util.Enumeration;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.ox.zoo.sparqlite.*;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;

import junit.framework.TestCase;
import static org.easymock.EasyMock.*;

public class ProcessorTest extends TestCase {

	HttpServletRequest request;
	HttpServletResponse response;
	ServletContext servletContext;
	ServletConfig servletConfig;

	protected void setUp() throws Exception {
		super.setUp();
		request = createMock(HttpServletRequest.class);
		response = createMock(HttpServletResponse.class);
		servletContext = createMock(ServletContext.class);
		servletConfig = createMock(ServletConfig.class);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	@SuppressWarnings("unchecked")
	public void testProcessor() {
		try {
			
			// set up expected sequence of calls on request 
			expect(request.getPathInfo()).andReturn("/rowling"); // called by EndpointTDB
			replay(request);

			// set up expected sequence of calls on request 
			// none
			replay(response);

			// set up expected calls on context
			expect(servletContext.getRealPath("WEB-INF/tdb/rowling.ttl")).andReturn("webapp/WEB-INF/tdb/rowling.ttl"); // called by EndpointTDB
			replay(servletContext);

			// set up expected calls on config 
			Vector names = new Vector();
			expect(servletConfig.getInitParameterNames()).andReturn(names.elements());
			replay(servletConfig);
			
			// begin testing
			Endpoint endpoint = new EndpointTDB(request, servletContext);
			Processor processor = new Processor(endpoint, servletConfig);
			
			assertNotNull(endpoint);
			assertNotNull(processor);
						
			verify(request); verify(response); verify(servletContext); verify(servletConfig);
			
			
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getLocalizedMessage());
		}
	}

	public void testCheckEndpointExists_doesnotexist() {
		try {
			
			// set up expected sequence of calls on request 
			expect(request.getPathInfo()).andReturn("/doesnotexist"); // called by EndpointTDB
			replay(request);

			// set up expected sequence of calls on request 
			// none
			replay(response);

			// set up expected calls on context
			expect(servletContext.getRealPath("WEB-INF/tdb/doesnotexist.ttl")).andReturn("webapp/WEB-INF/tdb/doesnotexist.ttl"); // called by EndpointTDB
			replay(servletContext);
			
			// set up expected calls on config 
			Vector names = new Vector();
			expect(servletConfig.getInitParameterNames()).andReturn(names.elements());
			replay(servletConfig);
			
			// begin testing
			Endpoint endpoint = new EndpointTDB(request, servletContext);
			Processor handler = new Processor(endpoint, servletConfig);

			try {
				handler.checkEndpointExists();
				fail("exception should be thrown");
			} catch (AbortRequestException are) {
				// expected
				assertEquals(404, are.getResponseCode());
			}
			
			verify(request); verify(response); verify(servletContext); verify(servletConfig);
						
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getLocalizedMessage());
		}
	}

	public void testCheckEndpointExists_exists() {
		try {
			
			// set up expected sequence of calls on request 
			expect(request.getPathInfo()).andReturn("/rowling"); // called by EndpointTDB
			replay(request);

			// set up expected sequence of calls on request 
			// none
			replay(response);

			// set up expected calls on context
			expect(servletContext.getRealPath("WEB-INF/tdb/rowling.ttl")).andReturn("webapp/WEB-INF/tdb/rowling.ttl"); // called by EndpointTDB
			replay(servletContext);
			
			// set up expected calls on config 
			Vector names = new Vector();
			expect(servletConfig.getInitParameterNames()).andReturn(names.elements());
			replay(servletConfig);
			
			// begin testing
			Endpoint endpoint = new EndpointTDB(request, servletContext);
			Processor handler = new Processor(endpoint, servletConfig);

			try {
				handler.checkEndpointExists();
			} catch (AbortRequestException are) {
				fail("exception should not be thrown");
			}
			
			verify(request); verify(response); verify(servletContext); verify(servletConfig);
						
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getLocalizedMessage());
		}
	}
	
	@SuppressWarnings("unchecked")
	public void testWrapRequest() {
		try {
			// create mock
			HttpServletRequest request = createMock(HttpServletRequest.class);

			Vector headerNamesV = new Vector();
			Enumeration headerNames = headerNamesV.elements();
			
			String queryString = "ASK { ?s ?p ?o }";
			Vector paramNames = new  Vector();
			paramNames.add("query");
			String[] queryParams = { queryString } ;
			
			// set up expected sequence of calls on request 
			expect(request.getPathInfo()).andReturn("/rowling"); // called by EndpointTDB
			expect(request.getHeaderNames()).andReturn(headerNames); // called by RequestWrapper 			
			expect(request.getParameterNames()).andReturn(paramNames.elements());
			expect(request.getParameterValues("query")).andReturn(queryParams);
			expect(request.getMethod()).andReturn("GET"); // called by RequestWrapper
			replay(request);

			// set up expected calls on context
			expect(servletContext.getRealPath("WEB-INF/tdb/rowling.ttl")).andReturn("webapp/WEB-INF/tdb/rowling.ttl"); // called by EndpointTDB
			replay(servletContext);
			
			// set up expected calls on config 
			expect(servletConfig.getInitParameterNames()).andReturn((new Vector().elements()));
			replay(servletConfig);

			// begin testing
			Endpoint endpoint = new EndpointTDB(request, servletContext);
			Processor processor = new Processor(endpoint, servletConfig);

			// call method
			RequestWrapper wrapped = processor.wrapRequest(request);
			
			assertTrue(wrapped.isWellFormed());
			assertTrue(wrapped.getQuery().isAskType());
			
			verify(request); verify(servletContext); verify(servletConfig);
			
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getLocalizedMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public void testWrapRequest_badGet() {
		try {
			// create mock
			HttpServletRequest request = createMock(HttpServletRequest.class);

			Vector headerNamesV = new Vector();
			Enumeration headerNames = headerNamesV.elements();
			
			Vector paramNames = new Vector();
			
			// set up expected sequence of calls on request 
			expect(request.getPathInfo()).andReturn("/rowling"); // called by EndpointTDB
			expect(request.getHeaderNames()).andReturn(headerNames); // called by RequestWrapper 			
			expect(request.getParameterNames()).andReturn(paramNames.elements()); // called by RequestWrapper
			expect(request.getMethod()).andReturn("GET"); // called by RequestWrapper
			replay(request);

			// set up expected calls on context
			expect(servletContext.getRealPath("WEB-INF/tdb/rowling.ttl")).andReturn("webapp/WEB-INF/tdb/rowling.ttl"); // called by EndpointTDB
			replay(servletContext);
			
			// set up expected calls on config 
			expect(servletConfig.getInitParameterNames()).andReturn((new Vector().elements()));
			replay(servletConfig);

			// begin testing
			Endpoint endpoint = new EndpointTDB(request, servletContext);
			Processor handler = new Processor(endpoint, servletConfig);

			// call method
			RequestWrapper wrapped = null;
			try {
				wrapped = handler.wrapRequest(request);
				fail("exception should be thrown");
			} catch ( AbortRequestException are ) {
				assertEquals(400, are.getResponseCode());
			}
			assertNull(wrapped);
			
			verify(request); verify(servletContext); verify(servletConfig);
			
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getLocalizedMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public void testWrapRequest_ARQ() {
		try {
			// create mock
			HttpServletRequest request = createMock(HttpServletRequest.class);

			Vector headerNamesV = new Vector();
			Enumeration headerNames = headerNamesV.elements();
			
			String queryString = "SELECT (count(distinct ?s) AS ?count) WHERE { ?s ?p ?o}";
			Vector paramNames = new  Vector();
			paramNames.add("query");
			String[] queryParams = { queryString } ;
			
			// set up expected sequence of calls on request 
			expect(request.getPathInfo()).andReturn("/rowling"); // called by EndpointTDB
			expect(request.getHeaderNames()).andReturn(headerNames); // called by RequestWrapper 			
			expect(request.getParameterNames()).andReturn(paramNames.elements());
			expect(request.getParameterValues("query")).andReturn(queryParams);
			expect(request.getMethod()).andReturn("GET"); // called by RequestWrapper
			replay(request);

			// set up expected calls on context
			expect(servletContext.getRealPath("WEB-INF/tdb/rowling.ttl")).andReturn("webapp/WEB-INF/tdb/rowling.ttl"); // called by EndpointTDB
			replay(servletContext);
			
			// set up expected calls on config 
			Vector names = new Vector();
			names.add(RequestWrapper.QUERYSYNTAX);
			
			// set expectations on servlet config
			expect(servletConfig.getInitParameterNames()).andReturn(names.elements());
			expect(servletConfig.getInitParameter(RequestWrapper.QUERYSYNTAX)).andReturn("ARQ");
			replay(servletConfig);			

			// begin testing
			Endpoint endpoint = new EndpointTDB(request, servletContext);
			Processor processor = new Processor(endpoint, servletConfig);

			// call method
			RequestWrapper wrapped = processor.wrapRequest(request);
			
			assertTrue(wrapped.isWellFormed());
			assertTrue(wrapped.getQuery().isSelectType());
			
			verify(request); verify(servletContext); verify(servletConfig);
			
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getLocalizedMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public void testApplyQueryPolicy_lax() {
		try {
			// create mock
			HttpServletRequest request = createMock(HttpServletRequest.class);

			// set up expected sequence of calls on request 
			expect(request.getPathInfo()).andReturn("/rowling"); // called by EndpointTDB
			replay(request);

			// set up expected calls on context
			expect(servletContext.getRealPath("WEB-INF/tdb/rowling.ttl")).andReturn("webapp/WEB-INF/tdb/rowling.ttl"); // called by EndpointTDB
			replay(servletContext);
			
			// set up expected calls on config 
			Vector v = new Vector();
			Enumeration names = v.elements();			
			expect(servletConfig.getInitParameterNames()).andReturn(names);
			replay(servletConfig);

			// begin testing
			Endpoint endpoint = new EndpointTDB(request, servletContext);
			Processor handler = new Processor(endpoint, servletConfig);

			String queryString = "SELECT * WHERE { ?s ?p ?o }";
			Query query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);
			
			// call method
			try {
				Query applied = handler.applyQueryPolicy(query);
				assertNotNull(applied);
			} catch ( AbortRequestException are ) {
				fail("exception should not be thrown");
			}
				
			verify(request); verify(servletContext); verify(servletConfig);		
			
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getLocalizedMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public void testApplyQueryPolicy_strict() {
		try {
			// create mock
			HttpServletRequest request = createMock(HttpServletRequest.class);

			// set up expected sequence of calls on request 
			expect(request.getPathInfo()).andReturn("/rowling"); // called by EndpointTDB
			replay(request);

			// set up expected calls on context
			expect(servletContext.getRealPath("WEB-INF/tdb/rowling.ttl")).andReturn("webapp/WEB-INF/tdb/rowling.ttl"); // called by EndpointTDB
			replay(servletContext);
			
			// set up expected calls on config 
			Vector names = new Vector();
			names.add(QueryPolicy.DISALLOWVARIABLEPREDICATES);
			names.add(QueryPolicy.DISALLOWFILTERS);
			names.add(QueryPolicy.SELECTLIMITCEILING);
			names.add(QueryPolicy.CONSTRUCTLIMITCEILING);
			names.add(QueryPolicy.DESCRIBELIMITCEILING);
			names.add(QueryPolicy.DISALLOWQUERYFORMS);
			
			expect(servletConfig.getInitParameterNames()).andReturn(names.elements()); // called by QueryPolicy
			expect(servletConfig.getInitParameter(QueryPolicy.DISALLOWVARIABLEPREDICATES)).andReturn("true"); // called by QueryPolicy
			expect(servletConfig.getInitParameter(QueryPolicy.DISALLOWFILTERS)).andReturn("true"); // called by QueryPolicy
			expect(servletConfig.getInitParameter(QueryPolicy.SELECTLIMITCEILING)).andReturn("500"); // called by QueryPolicy
			expect(servletConfig.getInitParameter(QueryPolicy.CONSTRUCTLIMITCEILING)).andReturn("100"); // called by QueryPolicy
			expect(servletConfig.getInitParameter(QueryPolicy.DESCRIBELIMITCEILING)).andReturn("20"); // called by QueryPolicy
			expect(servletConfig.getInitParameter(QueryPolicy.DISALLOWQUERYFORMS)).andReturn("DESCRIBE"); // called by QueryPolicy

			replay(servletConfig);

			// begin testing
			Endpoint endpoint = new EndpointTDB(request, servletContext);
			Processor handler = new Processor(endpoint, servletConfig);

			// variable predicates
			String queryString = "SELECT * WHERE { ?s ?p ?o }";
			Query query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);
			Query applied = null;
			try {
				applied = handler.applyQueryPolicy(query);
				fail("exception should be thrown");
			} catch ( AbortRequestException are ) {
				assertEquals(403, are.getResponseCode());
			}
			assertNull(applied);

			// select limit
			queryString = "SELECT * WHERE { ?s <http://example.com/p> ?o }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);			
			assertEquals(Query.NOLIMIT, query.getLimit());
			applied = null;
			try {
				applied = handler.applyQueryPolicy(query);
			} catch ( AbortRequestException are ) {
				fail("exception should not be thrown");
			}
			assertEquals(500, applied.getLimit());

			// construct limit
			queryString = "CONSTRUCT { ?s <http://example.com/q> ?o } WHERE { ?s <http://example.com/p> ?o }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);			
			assertEquals(Query.NOLIMIT, query.getLimit());
			applied = null;
			try {
				applied = handler.applyQueryPolicy(query);
			} catch ( AbortRequestException are ) {
				fail("exception should not be thrown");
			}
			assertEquals(100, applied.getLimit());

			// disallow DESCRIBE
			queryString = "DESCRIBE <http://example.com/foo>";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);
			applied = null;
			try {
				applied = handler.applyQueryPolicy(query);
				fail("exception should be thrown");
			} catch ( AbortRequestException are ) {
				assertEquals(403, are.getResponseCode());
			}
			assertNull(applied);

			verify(request); verify(servletContext); verify(servletConfig);	
			
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getLocalizedMessage());
		}
	}
	
	@SuppressWarnings("unchecked")
	public void testCreateQueryExecution() {
		try {
			// create mock
			HttpServletRequest request = createMock(HttpServletRequest.class);
	
			// set up expected sequence of calls on request 
			expect(request.getPathInfo()).andReturn("/rowling"); // called by EndpointTDB
			replay(request);
	
			// set up expected calls on context
			expect(servletContext.getRealPath("WEB-INF/tdb/rowling.ttl")).andReturn("webapp/WEB-INF/tdb/rowling.ttl"); // called by EndpointTDB
			replay(servletContext);
			
			// set up expected calls on config 
			Vector v = new Vector();
			Enumeration names = v.elements();			
			expect(servletConfig.getInitParameterNames()).andReturn(names);
			replay(servletConfig);

			// begin testing
			Endpoint endpoint = new EndpointTDB(request, servletContext);
			Processor handler = new Processor(endpoint, servletConfig);
	
			String queryString = "SELECT * WHERE { ?s ?p ?o }";
			Query query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);
			
			// call method
			QueryExecution qe = handler.createQueryExecution(query, endpoint);
			
			assertNotNull(qe);
			
			verify(request); verify(servletContext); verify(servletConfig);
			
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getLocalizedMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public void testExecuteQuery() {
		try {
			// create mock
			HttpServletRequest request = createMock(HttpServletRequest.class);
	
			// set up expected sequence of calls on request 
			expect(request.getPathInfo()).andReturn("/rowling"); // called by EndpointTDB
			replay(request);
	
			// set up expected calls on context
			expect(servletContext.getRealPath("WEB-INF/tdb/rowling.ttl")).andReturn("webapp/WEB-INF/tdb/rowling.ttl"); // called by EndpointTDB
			replay(servletContext);
			
			// set up expected calls on config 
			Vector v = new Vector();
			Enumeration names = v.elements();			
			expect(servletConfig.getInitParameterNames()).andReturn(names);
			replay(servletConfig);

			// begin testing
			Endpoint endpoint = new EndpointTDB(request, servletContext);
			Processor handler = new Processor(endpoint, servletConfig);

			// SELECT
			
			String queryString = "SELECT * WHERE { ?s ?p ?o }";
			Query query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);
			
			// call methods
			QueryExecution qe = handler.createQueryExecution(query, endpoint);
			Object result = handler.executeQuery(query, qe);

			// test
			assertTrue(result instanceof ResultSet);

			// ASK 
			
			queryString = "ASK { ?s ?p ?o }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);
			
			// call methods
			qe = handler.createQueryExecution(query, endpoint);
			result = handler.executeQuery(query, qe);

			// test
			assertTrue(result instanceof Boolean);

			// ASK 
			
			queryString = "CONSTRUCT { ?s ?p ?o } WHERE { ?s ?p ?o }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);
			
			// call methods
			qe = handler.createQueryExecution(query, endpoint);
			result = handler.executeQuery(query, qe);

			// test
			assertTrue(result instanceof Model);

			// DESCRIBE 
			
			queryString = "DESCRIBE * WHERE { ?s ?p ?o }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);
			
			// call methods
			qe = handler.createQueryExecution(query, endpoint);
			result = handler.executeQuery(query, qe);

			// test
			assertTrue(result instanceof Model);
			
			verify(request); verify(servletContext); verify(servletConfig);
			
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getLocalizedMessage());
		}
	}

}
