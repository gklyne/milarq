package cmd;

import java.util.Iterator;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.tdb.TDBFactory;

import util.CommandArgs;

public class query
    {
    private static final String XSD_Integer = XSDDatatype.XSDinteger.getURI();

    public static void main( String [] args )
        {
        CommandArgs a = CommandArgs.parse( args );
        String q = a.getOnly( "-query" );
        String t = a.getOnly( "-tdb" );
        Dataset m = TDBFactory.createDataset( t );
        String queryString = timings.PREFIXES + "\n" + q;
        // System.err.println( ">> " + queryString );
        Query query = QueryFactory.create( queryString, Syntax.syntaxARQ );
        QueryExecution qexec = QueryExecutionFactory.create( query, m );
        for (ResultSet results = qexec.execSelect(); results.hasNext();)
            {
            String comma = "";
            QuerySolution soln = results.nextSolution();
            for (Iterator<String> strings = soln.varNames(); strings.hasNext();) 
                {
                String name = strings.next();
                RDFNode n = soln.get( name );
                System.out.print( comma ); comma = ",";
                System.out.print( protect( asField(n) ) );
                }
            System.out.println();
            }
        }

    private static String asField( RDFNode n )
        {
        Node nan = n.asNode();
        if (n.isURIResource()) return ((Resource) n).getModel().shortForm( nan.getURI() );
        if (n.isLiteral() && isInteger( nan )) return nan.getLiteralLexicalForm();
        return n.toString().replaceAll("[\n\r]+", " " ).replaceAll( " +", " " );
        }

    private static boolean isInteger( Node nan )
        { return XSD_Integer.equals( nan.getLiteralDatatypeURI() ); }

    private static String protect( String s )
        {
        int comma = s.indexOf( ',' ), quote = s.indexOf( '"' );
        if (comma < 0 && quote < 0)
            return s;
        else if (quote < 0)
            return "\"" + s + "\"";
        else
            return "\"" + s.replaceAll( "\"", "\\\\\"" ) + "\"";
        }
    }
