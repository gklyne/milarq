package uk.ac.ox.zoo.sparqlite.config.tests;

import static org.junit.Assert.assertEquals;

import java.util.regex.Pattern;

import org.junit.Test;

import uk.ac.ox.zoo.sparqlite.config.ConditionalTransform;
import uk.ac.ox.zoo.sparqlite.config.Sparqlite;

public class TestSparqlite
    {

    @Test public void ensureTrivalSparqlitePreservesPathInfo()
        {
        Sparqlite s = new Sparqlite();
        assertEquals( "spoo", s.pathToLocation( "spoo" ) );
        assertEquals( "flarn", s.pathToLocation( "flarn" ) );
        }
    
    @Test public void ensureMapAllToConstantTransformsWork()
        {
        ConditionalTransform [] mappings =
            {
            new ConditionalTransform( Pattern.compile(".*"), "constant" )
            };
        Sparqlite s = new Sparqlite( mappings );
        assertEquals( "constant", s.pathToLocation( "spoo" ) );
        assertEquals( "constant", s.pathToLocation( "flarn" ) );
        assertEquals( "constant", s.pathToLocation( "spick/span" ) );
        }
    
    @Test public void ensureConstantMatchingByRE()
        {
        ConditionalTransform [] mappings =
            {
            new ConditionalTransform( Pattern.compile(".*AAA.*"), "a" ),
            new ConditionalTransform( Pattern.compile(".*BBB.*"), "b" )
            };
        Sparqlite s = new Sparqlite( mappings );
        assertEquals( "a", s.pathToLocation( "AAA" ) );
        assertEquals( "b", s.pathToLocation( "BBB" ) );
        assertEquals( "spick/span", s.pathToLocation( "spick/span" ) );
        }
    
    @Test public void ensurePercentSIsSubstituted()
        {
        ConditionalTransform [] mappings =
            {
            new ConditionalTransform( Pattern.compile(".*"), "WEB-INF/tdb/%s.ttl" )
            };
        Sparqlite s = new Sparqlite( mappings );
        assertEquals( "WEB-INF/tdb/simple.ttl", s.pathToLocation( "simple" ) );
        }

    }