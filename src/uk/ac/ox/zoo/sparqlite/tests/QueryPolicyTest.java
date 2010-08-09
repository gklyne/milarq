package uk.ac.ox.zoo.sparqlite.tests;
import static org.easymock.EasyMock.*;

import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletConfig;

import uk.ac.ox.zoo.sparqlite.*;
import uk.ac.ox.zoo.sparqlite.exceptions.QueryPolicyException;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.Syntax;

import junit.framework.TestCase;

public class QueryPolicyTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testQueryPolicyServletConfig() {

		ServletConfig config = createMock(ServletConfig.class);
		
		Vector<String> names = new Vector<String>();
		names.add(QueryPolicy.DISALLOWVARIABLEPREDICATES);
		names.add(QueryPolicy.SELECTLIMITCEILING);
		names.add(QueryPolicy.CONSTRUCTLIMITCEILING);
		names.add(QueryPolicy.DESCRIBELIMITCEILING);
		names.add(QueryPolicy.DISALLOWQUERYFORMS);
		names.add(QueryPolicy.DISALLOWFILTERS);
		
		// set expectations
		expect(config.getInitParameterNames()).andReturn(names.elements());
		expect(config.getInitParameter(QueryPolicy.DISALLOWVARIABLEPREDICATES)).andReturn("true");
		expect(config.getInitParameter(QueryPolicy.SELECTLIMITCEILING)).andReturn("500");
		expect(config.getInitParameter(QueryPolicy.CONSTRUCTLIMITCEILING)).andReturn("100");
		expect(config.getInitParameter(QueryPolicy.DESCRIBELIMITCEILING)).andReturn("20");
		expect(config.getInitParameter(QueryPolicy.DISALLOWQUERYFORMS)).andReturn("CONSTRUCT DESCRIBE");
		expect(config.getInitParameter(QueryPolicy.DISALLOWFILTERS)).andReturn("true");
		replay(config);
		
		InitParams params = new InitParams(config);
		QueryPolicy policy = new QueryPolicy(params);
		
		assertEquals(6, policy.rules.keySet().size());
		assertEquals("true", policy.rules.get(QueryPolicy.DISALLOWVARIABLEPREDICATES));
		assertEquals("500", policy.rules.get(QueryPolicy.SELECTLIMITCEILING));
		assertEquals("100", policy.rules.get(QueryPolicy.CONSTRUCTLIMITCEILING));
		assertEquals("20", policy.rules.get(QueryPolicy.DESCRIBELIMITCEILING));
		assertEquals("CONSTRUCT DESCRIBE", policy.rules.get(QueryPolicy.DISALLOWQUERYFORMS));
		assertEquals("true", policy.rules.get(QueryPolicy.DISALLOWFILTERS));
		verify(config);
		
	}

	public void testApplyQueryFormPolicy() {
		
		Map<String,String> rules = new Hashtable<String,String>();
		rules.put(QueryPolicy.DISALLOWQUERYFORMS, "CONSTRUCT DESCRIBE");
		QueryPolicy policy = new QueryPolicy(rules);
		
		String queryString;
		Query query;
		
		queryString = "ASK { ?s ?p ?o }";
		query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);
		
		try {
			policy.applyQueryFormPolicy(query);
		} catch (QueryPolicyException qfde) {
			fail("ask should be allowed");
		}

		queryString = "SELECT * WHERE { ?s ?p ?o }";
		query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);
		
		try {
			policy.applyQueryFormPolicy(query);
		} catch (QueryPolicyException qfde) {
			fail("select should be allowed");
		}

		queryString = "CONSTRUCT { ?s ?p ?o } WHERE { ?s ?p ?o }";
		query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);
		
		try {
			policy.applyQueryFormPolicy(query);
			fail("construct should be disallowed");
		} catch (QueryPolicyException qfde) {
			// expected
		}

		queryString = "DESCRIBE <http://example.com/foo> WHERE { ?s ?p ?o }";
		query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);
		
		try {
			policy.applyQueryFormPolicy(query);
			fail("describe should be disallowed");
		} catch (QueryPolicyException qfde) {
			// expected
		}

	}

	public void testApplyFilterPolicy_disallow() {

		Map<String,String> rules = new Hashtable<String,String>();
		rules.put(QueryPolicy.DISALLOWFILTERS, "true");
		QueryPolicy policy = new QueryPolicy(rules);
		
		String queryString;
		Query query;

		try {
			queryString = "SELECT * WHERE { ?s ?p ?o FILTER ( isIRI(?o) ) }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);			
			policy.applyFilterPolicy(query);
			fail("exception should be thrown");
		} catch ( QueryPolicyException e ) {
			// expected
		}

		try {
			queryString = "SELECT * WHERE { ?s ?p ?o . ?o ?q ?x FILTER ( isLiteral(?x) ) }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);			
			policy.applyFilterPolicy(query);
			fail("exception should be thrown");
		} catch ( QueryPolicyException e ) {
			// expected
		}

		try {
			queryString = "SELECT * WHERE { { ?s ?p ?o } { ?o ?q ?x FILTER ( isBlank(?x) ) } }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);			
			policy.applyFilterPolicy(query);
			fail("exception should be thrown");
		} catch ( QueryPolicyException e ) {
			// expected
		}

		try {
			queryString = "SELECT * WHERE { { ?s ?p ?o } UNION { ?o ?q ?x FILTER ( isLiteral(?x) ) } }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);			
			policy.applyFilterPolicy(query);
			fail("exception should be thrown");
		} catch ( QueryPolicyException e ) {
			// expected
		}

		try {
			queryString = "SELECT * WHERE { ?s <http://example.com/p> ?o OPTIONAL { ?o ?q ?x FILTER ( !bound(?x) ) } }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);			
			policy.applyFilterPolicy(query);
			fail("exception should be thrown");
		} catch ( QueryPolicyException e ) {
			// expected
		}

		try {
			queryString = "SELECT * WHERE { ?s ?p ?o . ?o ?q ?x }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);			
			policy.applyFilterPolicy(query);
		} catch ( QueryPolicyException e ) {
			fail("exception should not be thrown");
		}

	}

	public void testApplyFilterPolicy_allow() {

		Map<String,String> rules = new Hashtable<String,String>();
		rules.put(QueryPolicy.DISALLOWFILTERS, "false");
		QueryPolicy policy = new QueryPolicy(rules);
		
		String queryString;
		Query query;

		try {
			queryString = "SELECT * WHERE { ?s ?p ?o FILTER ( isIRI(?o) ) }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);			
			policy.applyFilterPolicy(query);
		} catch ( QueryPolicyException e ) {
			fail("exception should not be thrown");
		}

		try {
			queryString = "SELECT * WHERE { ?s ?p ?o . ?o ?q ?x FILTER ( isLiteral(?x) ) }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);			
			policy.applyFilterPolicy(query);
		} catch ( QueryPolicyException e ) {
			fail("exception should not be thrown");
		}

		try {
			queryString = "SELECT * WHERE { { ?s ?p ?o } { ?o ?q ?x FILTER ( isBlank(?x) ) } }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);			
			policy.applyFilterPolicy(query);
		} catch ( QueryPolicyException e ) {
			fail("exception should not be thrown");
		}

		try {
			queryString = "SELECT * WHERE { { ?s ?p ?o } UNION { ?o ?q ?x FILTER ( isLiteral(?x) ) } }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);			
			policy.applyFilterPolicy(query);
		} catch ( QueryPolicyException e ) {
			fail("exception should not be thrown");
		}

		try {
			queryString = "SELECT * WHERE { ?s <http://example.com/p> ?o OPTIONAL { ?o ?q ?x FILTER ( !bound(?x) ) } }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);			
			policy.applyFilterPolicy(query);
		} catch ( QueryPolicyException e ) {
			fail("exception should not be thrown");
		}

		try {
			queryString = "SELECT * WHERE { ?s ?p ?o . ?o ?q ?x }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);			
			policy.applyFilterPolicy(query);
		} catch ( QueryPolicyException e ) {
			fail("exception should not be thrown");
		}

	}

	public void testApplyFilterPolicy_implicitAllow() {

		Map<String,String> rules = new Hashtable<String,String>();
		QueryPolicy policy = new QueryPolicy(rules);
		
		String queryString;
		Query query;

		try {
			queryString = "SELECT * WHERE { ?s ?p ?o FILTER ( isIRI(?o) ) }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);			
			policy.applyFilterPolicy(query);
		} catch ( QueryPolicyException e ) {
			fail("exception should not be thrown");
		}

		try {
			queryString = "SELECT * WHERE { ?s ?p ?o . ?o ?q ?x FILTER ( isLiteral(?x) ) }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);			
			policy.applyFilterPolicy(query);
		} catch ( QueryPolicyException e ) {
			fail("exception should not be thrown");
		}

		try {
			queryString = "SELECT * WHERE { { ?s ?p ?o } { ?o ?q ?x FILTER ( isBlank(?x) ) } }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);			
			policy.applyFilterPolicy(query);
		} catch ( QueryPolicyException e ) {
			fail("exception should not be thrown");
		}

		try {
			queryString = "SELECT * WHERE { { ?s ?p ?o } UNION { ?o ?q ?x FILTER ( isLiteral(?x) ) } }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);			
			policy.applyFilterPolicy(query);
		} catch ( QueryPolicyException e ) {
			fail("exception should not be thrown");
		}

		try {
			queryString = "SELECT * WHERE { ?s <http://example.com/p> ?o OPTIONAL { ?o ?q ?x FILTER ( !bound(?x) ) } }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);			
			policy.applyFilterPolicy(query);
		} catch ( QueryPolicyException e ) {
			fail("exception should not be thrown");
		}

		try {
			queryString = "SELECT * WHERE { ?s ?p ?o . ?o ?q ?x }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);			
			policy.applyFilterPolicy(query);
		} catch ( QueryPolicyException e ) {
			fail("exception should not be thrown");
		}

	}

	public void testApplyDatasetDescriptionPolicy() {
		Map<String,String> rules = new Hashtable<String,String>();
		QueryPolicy policy = new QueryPolicy(rules);
		
		String queryString;
		Query query;
		
		try {
			
			queryString = "SELECT * FROM <http://example.com/foo> FROM NAMED <http://example.com/bar> WHERE { ?s ?p ?o }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);
			assertTrue(query.hasDatasetDescription());
			
			try {
				policy.applyDatasetDescriptionPolicy(query);
				fail("exception should be thrown");
			} catch (QueryPolicyException ddde) {
				// expected
			}

			queryString = "SELECT * FROM <http://example.com/foo> WHERE { ?s ?p ?o }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);
			assertTrue(query.hasDatasetDescription());
			
			try {
				policy.applyDatasetDescriptionPolicy(query);
				fail("exception should be thrown");
			} catch (QueryPolicyException ddde) {
				// expected
			}

			queryString = "SELECT * FROM NAMED <http://example.com/bar> WHERE { ?s ?p ?o }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);
			assertTrue(query.hasDatasetDescription());
			
			try {
				policy.applyDatasetDescriptionPolicy(query);
				fail("exception should be thrown");
			} catch (QueryPolicyException ddde) {
				// expected
			}

			queryString = "SELECT * WHERE { ?s ?p ?o }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);
			assertFalse(query.hasDatasetDescription());
			
			try {
				policy.applyDatasetDescriptionPolicy(query);
			} catch (QueryPolicyException ddde) {
				fail("exception should not be thrown");
			}

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
	}

	public void testApplyLimitCeilingPolicy() {

		Map<String,String> rules = new Hashtable<String,String>();
		rules.put(QueryPolicy.SELECTLIMITCEILING, "500");
		rules.put(QueryPolicy.CONSTRUCTLIMITCEILING, "100");
		rules.put(QueryPolicy.DESCRIBELIMITCEILING, "20");
		QueryPolicy policy = new QueryPolicy(rules);
		
		String queryString;
		Query query;
		
		try {
			
			queryString = "SELECT * WHERE { ?s ?p ?o }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);
			assertEquals(Query.NOLIMIT, query.getLimit());
			policy.applyLimitCeilingPolicy(query);
			assertEquals(500, query.getLimit());
			
			queryString = "SELECT * WHERE { ?s ?p ?o } LIMIT 1000";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);
			assertEquals(1000, query.getLimit());
			policy.applyLimitCeilingPolicy(query);
			assertEquals(500, query.getLimit());
			
			queryString = "SELECT * WHERE { ?s ?p ?o } LIMIT 10";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);
			assertEquals(10, query.getLimit());
			policy.applyLimitCeilingPolicy(query);
			assertEquals(10, query.getLimit());

			queryString = "CONSTRUCT { ?s ?p ?o } WHERE { ?s ?p ?o }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);
			assertEquals(Query.NOLIMIT, query.getLimit());
			policy.applyLimitCeilingPolicy(query);
			assertEquals(100, query.getLimit());
			
			queryString = "DESCRIBE <http://example.com/foo> WHERE { ?s ?p ?o }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);
			assertEquals(Query.NOLIMIT, query.getLimit());
			policy.applyLimitCeilingPolicy(query);
			assertEquals(20, query.getLimit());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}

	}

	public void testApplyVariablePredicatePolicy_disallow() {
		Map<String,String> rules = new Hashtable<String,String>();
		rules.put(QueryPolicy.DISALLOWVARIABLEPREDICATES, "true");
		QueryPolicy policy = new QueryPolicy(rules);
		
		String queryString;
		Query query;
		
		try {
			queryString = "SELECT * WHERE { ?s ?p ?o }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);			
			policy.applyVariablePredicatePolicy(query);
			fail("exception should be thrown");
		} catch ( QueryPolicyException e ) {
			// expected
		}

		try {
			queryString = "SELECT * WHERE { ?s ?p ?o . ?o ?q ?x }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);			
			policy.applyVariablePredicatePolicy(query);
			fail("exception should be thrown");
		} catch ( QueryPolicyException e ) {
			// expected
		}

		try {
			queryString = "SELECT * WHERE { { ?s ?p ?o } { ?o ?q ?x } }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);			
			policy.applyVariablePredicatePolicy(query);
			fail("exception should be thrown");
		} catch ( QueryPolicyException e ) {
			// expected
		}

		try {
			queryString = "SELECT * WHERE { { ?s ?p ?o } UNION { ?o ?q ?x } }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);			
			policy.applyVariablePredicatePolicy(query);
			fail("exception should be thrown");
		} catch ( QueryPolicyException e ) {
			// expected
		}

		try {
			queryString = "SELECT * WHERE { ?s <http://example.com/p> ?o OPTIONAL { ?o ?q ?x } }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);			
			policy.applyVariablePredicatePolicy(query);
			fail("exception should be thrown");
		} catch ( QueryPolicyException e ) {
			// expected
		}

		try {
			queryString = "SELECT * WHERE { GRAPH ?g { ?s ?p ?o } }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);			
			policy.applyVariablePredicatePolicy(query);
			fail("exception should be thrown");
		} catch ( QueryPolicyException e ) {
			// expected
		}

		try {
			queryString = "SELECT * WHERE { ?s <http://foo.bar/baz> ?o . ?o <http://foo.bar/quux> ?x }";
			query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);			
			policy.applyVariablePredicatePolicy(query);
		} catch ( QueryPolicyException e ) {
			fail("exception should not be thrown");
		}

	}

	public void testApplyVariablePredicatePolicy_allow() {
		Map<String,String> rules = new Hashtable<String,String>();
		rules.put(QueryPolicy.DISALLOWVARIABLEPREDICATES, "false");
		QueryPolicy policy = new QueryPolicy(rules);
		
		String queryString;
		Query query;
		
		try {

			try {
				queryString = "SELECT * WHERE { ?s ?p ?o }";
				query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);			
				policy.applyVariablePredicatePolicy(query);
			} catch ( QueryPolicyException e ) {
				fail("exception should not be thrown");
			}

			try {
				queryString = "SELECT * WHERE { ?s ?p ?o . ?o ?q ?x }";
				query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);			
				policy.applyVariablePredicatePolicy(query);
			} catch ( QueryPolicyException e ) {
				fail("exception should not be thrown");
			}

			try {
				queryString = "SELECT * WHERE { ?s <http://foo.bar/baz> ?o . ?o <http://foo.bar/quux> ?x }";
				query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);			
				policy.applyVariablePredicatePolicy(query);
			} catch ( QueryPolicyException e ) {
				fail("exception should not be thrown");
			}

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
	}

	public void testApplyVariablePredicatePolicy_implicitAllow() {
		Map<String,String> rules = new Hashtable<String,String>();
		QueryPolicy policy = new QueryPolicy(rules);
		
		String queryString;
		Query query;
		
		try {

			try {
				queryString = "SELECT * WHERE { ?s ?p ?o }";
				query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);			
				policy.applyVariablePredicatePolicy(query);
			} catch ( QueryPolicyException e ) {
				fail("exception should not be thrown");
			}

			try {
				queryString = "SELECT * WHERE { ?s ?p ?o . ?o ?q ?x }";
				query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);			
				policy.applyVariablePredicatePolicy(query);
			} catch ( QueryPolicyException e ) {
				fail("exception should not be thrown");
			}

			try {
				queryString = "SELECT * WHERE { ?s <http://foo.bar/baz> ?o . ?o <http://foo.bar/quux> ?x }";
				query = QueryFactory.create(queryString, Syntax.syntaxSPARQL);			
				policy.applyVariablePredicatePolicy(query);
			} catch ( QueryPolicyException e ) {
				fail("exception should not be thrown");
			}

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
	}

}
