package uk.ac.ox.zoo.sparqlite.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.*;

/**
    Class for Sparqlite configuration.

    @author chris
*/
public abstract class Config
    {
    protected static final Log log = LogFactory.getLog( Config.class );
    
    protected final String pathInfo;

    public Config( String pathInfo )
        { this.pathInfo = pathInfo; }
    
    public String getPathInfo()
        { return pathInfo; }

    public boolean getSymUnionDefaultGraph()
        { return false; }
    
    public abstract Dataset getDataset();
    
    public abstract String getLARQIndexLocation();

    public abstract String getStoreName();
    
    public abstract boolean getStoreExists();

    /**
        Utility method providing some assemblerish
        functionality -- given a root resource, deliver the LARQ index
        location hanging off it.
    */
    public String getLARQIndexLocation( Resource root )
        {
        Statement locStatement = root.getProperty( Vocab.LARQ_Location );
        if (locStatement == null)
            {
            log.trace( "no LARQ location provided" );
            return null;
            }
        else
            {
            String location = locStatement.getString();
            log.trace( "LARQ location " + location + " provided" );
            return location;                   
            }
        }
    }
