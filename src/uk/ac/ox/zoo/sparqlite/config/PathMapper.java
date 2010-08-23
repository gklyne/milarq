package uk.ac.ox.zoo.sparqlite.config;

import javax.servlet.ServletContext;

/**
    An interface that maps a path to another path. Typically used for
    wrapping a ServletContext's getRealPath, or testing in the absence of same.
 
     @author chris
*/
public interface PathMapper
    {
    /**
        Answer the mapped path.
    */
    public String mapPath( String path );

    public static class Create
        {
        public static PathMapper from( final ServletContext c )
            {
            return new PathMapper() 
                {
                @Override public String mapPath( String path )
                    { return c.getRealPath( path ); }
                };
            }

        public static PathMapper usePrefix( final String prefix )
            {
            return new PathMapper() 
                {
                @Override public String mapPath( String path )
                    { return prefix + path; }
                };
            
            }
        }
    }
