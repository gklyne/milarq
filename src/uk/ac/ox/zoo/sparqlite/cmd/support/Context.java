/**
 * 
 */
package uk.ac.ox.zoo.sparqlite.cmd.support;

import java.util.*;

import util.StringHelp;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.shared.PrefixMapping;

public class Context
    {
    final Map<String, RDFNode> bindings = new HashMap<String, RDFNode>();
    final Model data;
    
    public static Map<String, ToString> maps = new HashMap<String, ToString>();
        
    public Context( Model data )
        { this.data = data; }

    public void putBinding( String name, RDFNode value )
        { bindings.put( name, value ); }

    public void removeBinding( String name )
        { bindings.remove( name ); }
    
    public String evalToString( String valx )
        {
        String [] cutForType = valx.split( "\\|", 2 );
        String [] parts = cutForType[0].split( "\\." );
        RDFNode current = eval(parts[0]);
        for (int i = 1; i < parts.length; i += 1)
            current = follow( current, eval(parts[i]) );
        ToString convert = cutForType.length < 2 ? plain : lookup( cutForType[1] );
        return convert.toString( current ); 
        }
    
    private ToString lookup( String name )
        {
        ToString result = maps.get( name );
        if (result == null) throw new RuntimeException( name + " not found in " + maps.keySet() );
        return result;
        }

    final ToString div1000 = new ToString() 
        {
        @Override public String toString( RDFNode n )
            { 
            long value = ((Literal) n).getLong();
            return Float.toString( value / 1000.0f ); 
            }
        };   

    final ToString plain = new ToString() 
        {
        @Override public String toString( RDFNode n )
            {
            if (n.isLiteral()) return StringHelp.safe( n.asNode().getLiteralLexicalForm() );
            if (n.isURIResource()) return data.shortForm( n.asNode().getURI() );
            return n.toString();
            }
        };        
        
        {
        maps.put( "usual", plain );
        maps.put( "div1000", div1000 );
        }
    
    public interface ToString
        {
        String toString( RDFNode n );
        }

    private RDFNode follow( RDFNode current, RDFNode p )
        {
        try 
            { return ((Resource) current).getProperty( p.as( Property.class ) ).getObject(); }
        catch (NullPointerException e)
            {
            System.err.println( "OOPS: current = " + current + ", property = " + p );
            throw e;
            }
        }

    private RDFNode eval( String x )
        {
        if (x.startsWith( "?" )) return bindings.get( x );
        return data.createResource( data.expandPrefix( x ) );
        }

    public Repeat buildFor( Context c, String forx )
        {
        int space = forx.indexOf( ' ' );
        String name = forx.substring( 0, space );
        String expr = forx.substring( space + 1 );
        return new Repeat( name, queryToResults( c, name, expr ) );
        }

    private List<RDFNode> queryToResults( Context c, String name, String expr )
        {
        String queryString = "<prefixes>\nselect <name> where\n<expr>"
            .replaceAll( "<prefixes>", asPrefixes( data ) )
            .replaceAll( "<name>", name )
            .replaceAll( "<expr>", expr )
            ;
        Query q = QueryFactory.create( queryString, Syntax.syntaxARQ ) ;
        QueryExecution qexec = QueryExecutionFactory.create( q, data );
        QuerySolution initial = c.initialSPARQLBindings();
        qexec.setInitialBinding( initial );
        System.err.println( ">> running query\n" + queryString );
//            System.err.println( ">> initial bindings: " + initial );
        ResultSet rs = qexec.execSelect();
        List<RDFNode> result = new ArrayList<RDFNode>();
        while (rs.hasNext())
            {
            result.add( rs.next().get( name ) );
            }
//            System.err.println( ">> results: " + result );
        return result;
        }

    private QuerySolution initialSPARQLBindings()
        {
        QuerySolutionMap qm = new QuerySolutionMap();
        for (String key: bindings.keySet()) qm.add( key, bindings.get( key ) );
        return qm;
        }

    private String asPrefixes( PrefixMapping pm )
        {
        StringBuilder result = new StringBuilder();
        for (String prefix: pm.getNsPrefixMap().keySet())
            {
            result.append( "PREFIX " )
                .append( prefix )
                .append( ": <"  )
                .append( pm.getNsPrefixURI( prefix ) )
                .append( ">\n" )
                ;
            }
        return result.toString();
        }
    }