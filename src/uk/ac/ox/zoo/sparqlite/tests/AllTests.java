package uk.ac.ox.zoo.sparqlite.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for uk.ac.ox.zoo.sparqlite");
		//$JUnit-BEGIN$
		suite.addTestSuite(RequestWrapperTest.class);
		suite.addTestSuite(EndpointTDBTest.class);
		suite.addTestSuite(EndpointTDBLARQTest.class);
		suite.addTestSuite(QueryPolicyTest.class);
		suite.addTestSuite(ResponseWriterTest.class);
		suite.addTestSuite(ProcessorTest.class);
		suite.addTestSuite(ProcessorAsyncTest.class);
		//$JUnit-END$
		return suite;
	}

}
