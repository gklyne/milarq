package uk.ac.ox.zoo.sparqlite.tests;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import uk.ac.ox.zoo.sparqlite.EndpointTDB;
import uk.ac.ox.zoo.sparqlite.config.ConfigByFile;

/**
    Utility methods for tests creating TDB endpoints given request/context
    pairs or description file paths.
 
    @author chris
*/
public class Utilities
    {
    public static EndpointTDB createEndpointTDB( HttpServletRequest request, ServletContext context )
        {
        return new EndpointTDB( new ConfigByFile( "", context.getRealPath("WEB-INF/tdb" + request.getPathInfo() +".ttl") ) );
        }

    public static EndpointTDB createEndpointTDB( String storeDescFilePath )
        {
        return new EndpointTDB( new ConfigByFile( "<no-path-supplied>", storeDescFilePath ) );
        }
    }
