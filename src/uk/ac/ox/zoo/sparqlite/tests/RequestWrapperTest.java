package uk.ac.ox.zoo.sparqlite.tests;

import static org.easymock.EasyMock.*;

import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;

import uk.ac.ox.zoo.sparqlite.*;
import uk.ac.ox.zoo.sparqlite.exceptions.MalformedRequestException;
import uk.ac.ox.zoo.sparqlite.exceptions.NotImplementedException;

import com.hp.hpl.jena.query.Query;

import junit.framework.TestCase;

public class RequestWrapperTest extends TestCase {

	private RequestHeaders headers;
	private RequestParams params;

	protected void setUp() throws Exception {
		super.setUp();
		headers = new RequestHeaders();
		headers.add("Host", "localhost").add("User-agent", "sparqlite-tester");
		params = new RequestParams();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testRequestWrapper_Get_Ask() {
		try {
			
			String query = "ASK { ?s ?p ?o}";
			params.add("query", query);

			RequestWrapper wrapped = new RequestWrapper("GET", headers, params);
			assertNotNull(wrapped);
			
			assertTrue(wrapped.isWellFormed());
			
			Query sq = wrapped.getQuery();
			assertTrue(sq.isAskType());
			
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getLocalizedMessage());
		}		

	}

	public void testRequestWrapper_Get_Select() {
		try {
			
			String query = "SELECT * WHERE { ?s ?p ?o}";
			params.add("query", query);

			RequestWrapper wrapped = new RequestWrapper("GET", headers, params);
			assertNotNull(wrapped);
			
			assertTrue(wrapped.isWellFormed());
			
			Query sq = wrapped.getQuery();
			assertTrue(sq.isSelectType());
			
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getLocalizedMessage());
		}		

	}

	public void testRequestWrapper_Get_NoQueryParam() {
		try {
			
			RequestWrapper wrapped = null;
			
			try {
				wrapped = new RequestWrapper("GET", headers, params);
				fail("exception should be thrown");
			} catch ( MalformedRequestException mre ) {
				// expected
			}
			assertNull(wrapped);
			
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getLocalizedMessage());
		}		

	}

	public void testRequestWrapper_Get_BadQuery() {
		try {
			
			String query = "this is not a SPARQL query";
			params.add("query", query);

			RequestWrapper wrapped = null;
			
			try {
				wrapped = new RequestWrapper("GET", headers, params);
				fail("exception should be thrown");
			} catch ( MalformedRequestException mre ) {
				// expected
			}
			assertNull(wrapped);
			
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getLocalizedMessage());
		}		

	}

	public void testRequestWrapper_Post_Ask() {
		try {
			
			String queryString = "ASK { ?s ?p ?o }";
			params.add("query", queryString);
			String encodedQuery = URLEncoder.encode(queryString, "UTF-8");
			String content = "query="+encodedQuery+"&someotherstuff";
			int contentLength = content.length();
			
			headers
				.add("Content-Type", "application/x-www-form-urlencoded")
				.add("Content-Length", Integer.toString(contentLength));

			RequestWrapper wrapped = new RequestWrapper("POST", headers, params);

			assertNotNull(wrapped);
			assertTrue(wrapped.isWellFormed());
			
			Query sq = wrapped.getQuery();
			assertTrue(sq.isAskType());
			
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getLocalizedMessage());
		}		

	}

	public void testRequestWrapper_Post_Select() {
		try {
			
			String queryString = "SELECT * WHERE { ?s ?p ?o }";
			params.add("query", queryString);
			String encodedQuery = URLEncoder.encode(queryString, "UTF-8");
			String content = "query="+encodedQuery+"&someotherstuff";
			int contentLength = content.length();
			
			headers
				.add("Content-Type", "application/x-www-form-urlencoded")
				.add("Content-Length", Integer.toString(contentLength));

			RequestWrapper wrapped = new RequestWrapper("POST", headers, params);

			assertNotNull(wrapped);
			assertTrue(wrapped.isWellFormed());
			
			Query sq = wrapped.getQuery();
			assertTrue(sq.isSelectType());
			
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getLocalizedMessage());
		}		

	}

	public void testRequestWrapper_Post_Ask_ContentTypeVariation() {
		try {
			
			String queryString = "SELECT * WHERE { ?s ?p ?o }";
			params.add("query", queryString);
			String encodedQuery = URLEncoder.encode(queryString, "UTF-8");
			String content = "query="+encodedQuery+"&someotherstuff";
			int contentLength = content.length();
			
			headers
				.add("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
				.add("Content-Length", Integer.toString(contentLength));

			RequestWrapper wrapped = new RequestWrapper("POST", headers, params);

			assertNotNull(wrapped);
			assertTrue(wrapped.isWellFormed());
			
			Query sq = wrapped.getQuery();
			assertTrue(sq.isSelectType());
			
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getLocalizedMessage());
		}		

	}

	public void testRequestWrapper_Post_Ask_UnsupportedContentType() {
		try {
			
			String queryString = "foo bar";
			params.add("query", queryString);
			String encodedQuery = URLEncoder.encode(queryString, "UTF-8");
			String content = "query="+encodedQuery+"&someotherstuff";
			int contentLength = content.length();
			
			headers
				.add("Content-Type", "foo/bar")
				.add("Content-Length", Integer.toString(contentLength));

			RequestWrapper wrapped = null;
			try {
				wrapped = new RequestWrapper("POST", headers, params);
				fail("exception should be thrown");
			} catch ( NotImplementedException nie ) {
				// expected
			}
			assertNull(wrapped);
			
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getLocalizedMessage());
		}		

	}

	public void testRequestWrapper_Post_Ask_MisspelledContentType() {
		try {
			
			String queryString = "foo bar";
			params.add("query", queryString);
			String encodedQuery = URLEncoder.encode(queryString, "UTF-8");
			String content = "query="+encodedQuery+"&someotherstuff";
			int contentLength = content.length();
			
			headers
				.add("Content-Type", "application-x-www-form-url-encoded")
				.add("Content-Length", Integer.toString(contentLength));

			RequestWrapper wrapped = null;
			try {
				wrapped = new RequestWrapper("POST", headers, params);
				fail("exception should be thrown");
			} catch ( NotImplementedException nie ) {
				// expected
			}
			assertNull(wrapped);

		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getLocalizedMessage());
		}		

	}

	public void testRequestWrapperFromPost_Ask_UnsupportedCharSet() {
		try {
			
			String queryString = "foo bar";
			params.add("query", queryString);
			String encodedQuery = URLEncoder.encode(queryString, "UTF-8");
			String content = "query="+encodedQuery+"&someotherstuff";
			int contentLength = content.length();
			
			headers
				.add("Content-Type", "application/x-www-form-urlencoded; charset=foo")
				.add("Content-Length", Integer.toString(contentLength));

			RequestWrapper wrapped = null;
			try {
				wrapped = new RequestWrapper("POST", headers, params);
				fail("exception should be thrown");
			} catch ( NotImplementedException nie ) {
				// expected
			}
			assertNull(wrapped);
			
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getLocalizedMessage());
		}		

	}
	
	@SuppressWarnings("unchecked")
	public void testNewInstanceFromGetRequest() {
		try {
			
			// create mock
			HttpServletRequest request = createMock(HttpServletRequest.class);

			String queryString = "ASK { ?s ?p ?o }";
			Vector paramNames = new  Vector();
			paramNames.add("query");
			String[] queryParams = { queryString } ;
			
			Vector headerNamesV = new Vector();
			headerNamesV.add("Host");
			Vector hostV = new Vector();
			hostV.add("localhost");
			Enumeration host = hostV.elements();
			headerNamesV.add("User-agent");
			Vector uaV = new Vector();
			uaV.add("sparqlite-tester");
			Enumeration ua = uaV.elements();
			Enumeration headerNames = headerNamesV.elements();
			
			// set up expected sequence of calls on request 
	
			// expect inspect method
			expect(request.getMethod()).andReturn("GET");

			// expect read headers first
			expect(request.getHeaderNames()).andReturn(headerNames); 
			expect(request.getHeaders("Host")).andReturn(host);
			expect(request.getHeaders("User-agent")).andReturn(ua);
			
			// expect read params next
			expect(request.getParameterNames()).andReturn(paramNames.elements());
			expect(request.getParameterValues("query")).andReturn(queryParams);

			replay(request);
			
			// call method
			
			RequestWrapper wrapped = RequestWrapper.newInstance(request);

			assertNotNull(wrapped);
			assertTrue(wrapped.isWellFormed());
			
			Query sq = wrapped.getQuery();
			assertTrue(sq.isAskType());
			
			verify(request);
			
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getLocalizedMessage());
		}		
		
	}
	
	@SuppressWarnings("unchecked")
	public void testNewInstanceFromPostRequest() {
		try {
			
			// create mock
			HttpServletRequest request = createMock(HttpServletRequest.class);

			String queryString = "ASK { ?s ?p ?o }";
			String encodedQuery = URLEncoder.encode(queryString, "UTF-8");
			String content = "query="+encodedQuery+"&someotherstuff";
			int contentLength = content.length();
			Vector paramNames = new  Vector();
			paramNames.add("query");
			String[] queryParams = { queryString } ;
			
			Vector headerNamesV = new Vector();

			headerNamesV.add("Host");
			Vector hostV = new Vector();
			hostV.add("localhost");
			Enumeration host = hostV.elements();

			headerNamesV.add("User-agent");
			Vector uaV = new Vector();
			uaV.add("sparqlite-tester");
			Enumeration ua = uaV.elements();

			headerNamesV.add("Content-Type");
			Vector ctV = new Vector();
			ctV.add("application/x-www-form-urlencoded; charset=UTF-8");
			Enumeration ct = ctV.elements();

			headerNamesV.add("Content-Length");
			Vector clV = new Vector();
			clV.add(Integer.toString(contentLength));
			Enumeration cl = clV.elements();

			Enumeration headerNames = headerNamesV.elements();
			
			// set up expected sequence of calls on request 

			expect(request.getMethod()).andReturn("POST");

			expect(request.getHeaderNames()).andReturn(headerNames); 			
			expect(request.getHeaders("Host")).andReturn(host);
			expect(request.getHeaders("User-agent")).andReturn(ua);
			expect(request.getHeaders("Content-Type")).andReturn(ct);
			expect(request.getHeaders("Content-Length")).andReturn(cl);
			
			expect(request.getParameterNames()).andReturn(paramNames.elements());
			expect(request.getParameterValues("query")).andReturn(queryParams);

//			StringReader reader = new StringReader(content);
//			expect(request.getReader()).andReturn(new BufferedReader(reader));

			replay(request);
			
			// call method
			
			RequestWrapper wrapped = RequestWrapper.newInstance(request);

			assertNotNull(wrapped);
			assertTrue(wrapped.isWellFormed());
			
			Query sq = wrapped.getQuery();
			assertTrue(sq.isAskType());
			
			verify(request);
			
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getLocalizedMessage());
		}		
		
	}

	public void testRequestWrapper_Get_Select_ARQ() {
		try {
			
			String query = "SELECT (count(distinct ?s) AS ?count) WHERE { ?s ?p ?o}";
			params.add("query", query);

			InitParams initParams = new InitParams();
			initParams.add(RequestWrapper.QUERYSYNTAX, "ARQ");
			RequestWrapper wrapped = new RequestWrapper("GET", headers, params, initParams);
			assertNotNull(wrapped);
			
			assertTrue(wrapped.isWellFormed());
			
			Query sq = wrapped.getQuery();
			assertTrue(sq.isSelectType());
			
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getLocalizedMessage());
		}		

	}

	public void testRequestWrapper_Get_Select_ARQ_as_SPARQL() {
		try {
			
			String query = "SELECT (count(distinct ?s) AS ?count) WHERE { ?s ?p ?o}";
			params.add("query", query);

			InitParams initParams = new InitParams();
			initParams.add(RequestWrapper.QUERYSYNTAX, "SPARQL");

			RequestWrapper wrapped = null;
			try {
				wrapped = new RequestWrapper("GET", headers, params, initParams);
				fail("exception should be thrown");
			} catch ( MalformedRequestException mre ) {
				// expected
			}
			assertNull(wrapped);
						
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getLocalizedMessage());
		}		

	}
	
	@SuppressWarnings("unchecked")
	public void testNewInstanceFromGetRequest_ARQ() {
		try {
			
			// create mock
			HttpServletRequest request = createMock(HttpServletRequest.class);

			String queryString = "SELECT (count(distinct ?s) AS ?count) WHERE { ?s ?p ?o}";
			Vector paramNames = new  Vector();
			paramNames.add("query");
			String[] queryParams = { queryString } ;
			
			Vector headerNamesV = new Vector();
			headerNamesV.add("Host");
			Vector hostV = new Vector();
			hostV.add("localhost");
			Enumeration host = hostV.elements();
			headerNamesV.add("User-agent");
			Vector uaV = new Vector();
			uaV.add("sparqlite-tester");
			Enumeration ua = uaV.elements();
			Enumeration headerNames = headerNamesV.elements();
			
			ServletConfig config = createMock(ServletConfig.class);
			
			Vector names = new Vector();
			names.add(RequestWrapper.QUERYSYNTAX);
			
			// set expectations on servlet config
			expect(config.getInitParameterNames()).andReturn(names.elements());
			expect(config.getInitParameter(RequestWrapper.QUERYSYNTAX)).andReturn("ARQ");
			replay(config);			
			
			// set up expected sequence of calls on request 
	
			// expect inspect method
			expect(request.getMethod()).andReturn("GET");

			// expect read headers first
			expect(request.getHeaderNames()).andReturn(headerNames); 
			expect(request.getHeaders("Host")).andReturn(host);
			expect(request.getHeaders("User-agent")).andReturn(ua);
			
			// expect read params next
			expect(request.getParameterNames()).andReturn(paramNames.elements());
			expect(request.getParameterValues("query")).andReturn(queryParams);

			replay(request);
			
			// call method
			
			RequestWrapper wrapped = RequestWrapper.newInstance(request, config);

			assertNotNull(wrapped);
			assertTrue(wrapped.isWellFormed());
			
			Query sq = wrapped.getQuery();
			assertTrue(sq.isSelectType());
			
			verify(request);
			
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getLocalizedMessage());
		}		
		
	}	


}
