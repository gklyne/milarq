package uk.ac.ox.zoo.sparqlite.config;

import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.sparql.util.Context;
import com.hp.hpl.jena.sparql.util.Symbol;

/**
    Map from predicate names to directory names
    (Essentially just a restricted view on a Map)
    
    @author chris
*/
public class IndexMap
    {
    public IndexMap() 
        {}
    
    private final Map<String, String> map = new HashMap<String, String>();
    
    public String getDirectoryFor( String predicate )
        { return map.get( predicate ); }
    
    public void put( String predicate, String directory )
        { map.put( predicate, directory ); }

    public void setContext( Context context )
        {
        for (String predicate: map.keySet())
            context.set( Symbol.create( predicate ), map.get( predicate ) );
        }
    }