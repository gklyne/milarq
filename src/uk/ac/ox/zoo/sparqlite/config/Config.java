package uk.ac.ox.zoo.sparqlite.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.sparql.util.Context;
import com.hp.hpl.jena.tdb.TDB;

/**
    Class for Sparqlite configuration.

    @author chris
*/
public abstract class Config
    {
    protected static final Log log = LogFactory.getLog( Config.class );
    
    protected final String pathInfo;
    protected final IndexMap indexMap;
    protected final boolean defaultGraphIsUnion;

    public Config( String pathInfo, IndexMap indexMap, boolean setUnionDefaultGraph )
        { 
        this.pathInfo = pathInfo; 
        this.indexMap = indexMap; 
        this.defaultGraphIsUnion = setUnionDefaultGraph;
        }
    
    public String getPathInfo()
        { return pathInfo; }

    public boolean getDefaultGraphIsUnion()
        { return defaultGraphIsUnion; }
    
    public abstract Dataset getDataset();
    
    public abstract String getLARQIndexLocation();

    public abstract String getStoreName();
    
    public abstract boolean getStoreExists();

    public abstract String getCompositeIndexDirectory();

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

    public void setContext( Context context )
        {
        context.set( TDB.symUnionDefaultGraph, defaultGraphIsUnion ); 
        context.set( Vocab.CompositeIndexDirectory, getCompositeIndexDirectory() );
        indexMap.setContext( context );        
        }
    }
