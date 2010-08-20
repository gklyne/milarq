package uk.ac.ox.zoo.sparqlite.cmd.support;

import java.util.*;

import util.StringHelp;

import com.hp.hpl.jena.rdf.model.RDFNode;

public class StripKnownPrefixes implements Context.ToString
    {
    @Override public String toString( RDFNode n )
        { return trimQuery( n.asNode().getLiteralLexicalForm() ); }
   
    private String trimQuery( String queryString )
        {
        List<String> lines = Arrays.asList( queryString.split( "\n" ) );
        int from = 0;
        while (from < lines.size())
            {
            String line = lines.get(from);
            if (retain(line)) break; else from += 1;
            }
        return StringHelp.join( lines.subList( from, lines.size() ), "\n" );
        }

    private boolean retain( String line )
        {
        return !knownLines.contains( line.replaceAll( " +", " " ) );
        }
    
    static final Set<String> knownLines = new HashSet<String>();
        {
        knownLines.add( "" );
        knownLines.add( " " );
        knownLines.add( "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" );
        knownLines.add( "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" );
        knownLines.add( "PREFIX pf: <http://jena.hpl.hp.com/ARQ/property#>" );
        knownLines.add( "PREFIX crm: <http://purl.org/NET/crm-owl#>" );
        knownLines.add( "PREFIX claros: <http://purl.org/NET/Claros/vocab#>" );
        knownLines.add( "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>" );
        knownLines.add( "PREFIX arqfn: <java:uk.ac.ox.zoo.sparqlite.>" );
        knownLines.add( "PREFIX fast: <eh:/claros/fast#>" );
        knownLines.add( "PREFIX cpf: <java:propertyfunctions.>" );
        }    
        
    static
        {
        Context.maps.put( "stripPrefixes", new StripKnownPrefixes() );
        } 
    }
