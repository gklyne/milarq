package uk.ac.ox.zoo.sparqlite.config.tests;

import static org.junit.Assert.*;

import java.util.*;
import java.util.regex.Pattern;

import org.junit.Test;

import com.hp.hpl.jena.assembler.Assembler;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.test.ModelTestBase;
import com.hp.hpl.jena.shared.WrappedException;

import uk.ac.ox.zoo.sparqlite.config.*;

public class TestSparqlite
    {
    static 
        { try { Class.forName( Init.class.getName() ); }
          catch (ClassNotFoundException e) { throw new WrappedException( e ); } 
        }
    
    @Test public void ensureAssemblerCreatesTransforms()
        {
        Model m = ModelTestBase.modelWithStatements
            ( "eh:/root rdf:type " + Vocab.SPARQLITE 
            + "; eh:/root http://purl.org/net/sparqlite/vocab#mapIf _m"
            + "; _m http://purl.org/net/sparqlite/vocab#matches 'pattern'"
            + "; _m http://purl.org/net/sparqlite/vocab#replacement 'replacement'"
            );
        Resource root = m.createResource( "eh:/root" );
        Sparqlite s = (Sparqlite) Assembler.general.open( root );
        Set<ConditionalTransform> expected = new HashSet<ConditionalTransform>();
        expected.add( new ConditionalTransform( Pattern.compile( "pattern" ), "replacement" ) );
        assertEquals( expected, s.getTransforms() );
        }
    
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
        assertEquals( "WEB-INF/tdb/another.ttl", s.pathToLocation( "another" ) );
        }
    
    @Test public void ensurePercentSIsMultiplySubstituted()
        {
        ConditionalTransform [] mappings =
            {
            new ConditionalTransform( Pattern.compile(".*"), "/%s/%s.ttl" )
            };
        Sparqlite s = new Sparqlite( mappings );
        assertEquals( "/simple/simple.ttl", s.pathToLocation( "simple" ) );
        assertEquals( "/another/another.ttl", s.pathToLocation( "another" ) );
        }
    
    @Test public void ensurePercentPIsSubstituted()
        {
        ConditionalTransform [] mappings =
            {
            new ConditionalTransform( Pattern.compile(".*"), "[%p]" )
            };
        Sparqlite s = new Sparqlite( mappings );
        assertEquals( "[alpha]", s.pathToLocation( "alpha" ) );
        assertEquals( "[beta]", s.pathToLocation( "beta.thing" ) );
        assertEquals( "[gamma]", s.pathToLocation( "thing/gamma" ) );
        assertEquals( "[delta]", s.pathToLocation( "thing/delta.thing" ) );
        }
    
    @Test public void ensurePercentQIsSubstituted()
        {
        ConditionalTransform [] mappings =
            {
            new ConditionalTransform( Pattern.compile(".*"), "[%q]" )
            };
        Sparqlite s = new Sparqlite( mappings );
        assertEquals( "[alpha]", s.pathToLocation( "alpha" ) );
        assertEquals( "[beta.thing]", s.pathToLocation( "beta.thing" ) );
        assertEquals( "[gamma]", s.pathToLocation( "thing/gamma" ) );
        assertEquals( "[delta.thing]", s.pathToLocation( "thing/delta.thing" ) );
        }
    
    @Test public void ensurePercentRIsSubstituted()
        {
        ConditionalTransform [] mappings =
            {
            new ConditionalTransform( Pattern.compile(".*"), "[%r]" )
            };
        Sparqlite s = new Sparqlite( mappings );
        assertEquals( "[alpha]", s.pathToLocation( "alpha" ) );
        assertEquals( "[beta]", s.pathToLocation( "beta.thing" ) );
        assertEquals( "[thing]", s.pathToLocation( "thing/gamma" ) );
        assertEquals( "[thing]", s.pathToLocation( "thing.extra/delta.thing" ) );
        }
    }