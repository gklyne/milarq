package uk.ac.ox.zoo.sparqlite.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

    /**
        For diagostics 
     */
    public Set<String> keySet()
        { return map.keySet(); }
    
    /**
        Answer the directory path associated with the given predicate,
        or null if there isn't one.
    */
    public String getDirectoryFor( String predicate )
        { return map.get( predicate ); }
    
    /**
        Associate the given directory with the given predicate, so that
        getDirectory(predicate) will deliver directory.
    */
    public void put( String predicate, String directory )
        { map.put( predicate, directory ); }

    /**
        Set a symbol in the ARQ context c for each predicate in the
        map, with value the corresponding directory.
    */
    public void setContext( Context c )
        {
        for (String predicate: map.keySet())
            c.set( Symbol.create( predicate ), map.get( predicate ) );
        }
    }