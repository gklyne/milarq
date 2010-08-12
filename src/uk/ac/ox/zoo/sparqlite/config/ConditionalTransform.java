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

    public String substituteWith( String pathInfo )
        {
        return replacement.replaceAll( "%s", pathInfo );
        }
    }