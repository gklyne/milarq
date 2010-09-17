package uk.ac.ox.zoo.sparqlite.config;

import java.util.regex.Pattern;

/**
    A ConditionalTransform describes a piece of a mapping from a string
    to a replacement string.
 
     @author chris
*/
public class ConditionalTransform 
    {
    final Pattern RE;
    final String replacement;
    
    public ConditionalTransform( Pattern RE, String replacement )
        { this.RE = RE; this.replacement = replacement; }

    @Override public int hashCode()
        { return RE.pattern().hashCode() ^ replacement.hashCode(); }
    
    @Override public boolean equals( Object other )
        { return other instanceof ConditionalTransform && same( (ConditionalTransform) other ); }
        
    @Override public String toString()
        { return "<CT /" + RE + "/ => '" + replacement + "'>"; }
    
    private boolean same( ConditionalTransform other )
        {
        return 
            RE.pattern().equals( other.RE.pattern() ) 
            && replacement.equals( other.replacement ); 
        }

    public String substituteWith( String pathInfo )
        {
        return replacement
            .replaceAll( "%s", pathInfo )
            .replaceAll( "%p", leafNameNoSuffix( pathInfo ) )
            .replaceAll( "%q", leafNameKeepSuffix( pathInfo ) )
            .replaceAll( "%r", rootNameNoSuffix( pathInfo ) )
            ;
        }

    private String leafNameKeepSuffix( String pathInfo )
        {
        return pathInfo.replaceAll( ".*/", "" );
        }

    private String rootNameNoSuffix( String pathInfo )
        {
        return pathInfo.replaceAll( "^/", "" ).replaceAll( "[/.].*", "" );
        }

    private String leafNameNoSuffix( String pathInfo )
        {
        return pathInfo.replaceAll( ".*/", "" ).replaceAll( "\\.[^/]*", "" );
        }
    }