package uk.ac.ox.zoo.sparqlite.tests;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uk.ac.ox.zoo.sparqlite.YearToDateString;

import com.hp.hpl.jena.sparql.expr.NodeValue;

import junit.framework.TestCase;

public class YearToDateStringTest extends TestCase {
	
	private Log log = LogFactory.getLog(YearToDateStringTest.class);

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testYear2000AD() {
		log.debug("testYear2000");
		try {
			YearToDateString ytd = new YearToDateString();
			NodeValue nodeyear = NodeValue.makeString("2000");
			NodeValue nodedate = ytd.exec(nodeyear);
			assertEquals("2000-01-01T00:00:00", nodedate.asNode().getLiteralLexicalForm());
			
			//assertNotNull(wrapped);			
			//assertTrue(wrapped.isWellFormed());
			
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getLocalizedMessage());
		}
	}


	public void testYear425BC() {
		log.debug("testYear425BC");
		try {
			YearToDateString ytd = new YearToDateString();
			NodeValue nodeyear = NodeValue.makeString("-425");
			NodeValue nodedate = ytd.exec(nodeyear);
			assertEquals("-0425-01-01T00:00:00", nodedate.asNode().getLiteralLexicalForm());
			
			//assertNotNull(wrapped);			
			//assertTrue(wrapped.isWellFormed());
			
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getLocalizedMessage());
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

}
