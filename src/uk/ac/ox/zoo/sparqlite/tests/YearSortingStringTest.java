package uk.ac.ox.zoo.sparqlite.tests;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uk.ac.ox.zoo.sparqlite.YearSortingString;

import com.hp.hpl.jena.sparql.expr.NodeValue;

import junit.framework.TestCase;

public class YearSortingStringTest extends TestCase {
	
	private Log log = LogFactory.getLog(YearSortingStringTest.class);

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testYear2000AD() {
		log.debug("testYear2000");
		try {
			YearSortingString ytd = new YearSortingString();
			NodeValue nodeyear = NodeValue.makeString("2000");
			NodeValue nodesort = ytd.exec(nodeyear);
			assertEquals("12000", nodesort.asNode().getLiteralLexicalForm());
			
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
			YearSortingString ytd = new YearSortingString();
			NodeValue nodeyear = NodeValue.makeString("-425");
			NodeValue nodesort = ytd.exec(nodeyear);
			assertEquals("09575", nodesort.asNode().getLiteralLexicalForm());
			
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
