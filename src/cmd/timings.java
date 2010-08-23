package cmd;

import java.util.Iterator;


import util.*;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.query.larq.IndexLARQ;
import com.hp.hpl.jena.query.larq.LARQ;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.util.FileManager;
 
/**
    Code to run queries multiple times. Usually called from runmanyqueries.
    Not documented here.
    
    @author chris
*/
public class timings 
    {
    public static void main( String [] args ) 
    	{
    	CommandConfig c = new CommandConfig( args );
        String queryString = getQueryFromArg( c.queryArg );
        Dataset m = TDBFactory.createDataset( c.TDBloc );
        for (int i = 0; i < c.limit; i += 1) runQuery( c, i, queryString, m );
        m.close();
        }

    private static String getQueryFromArg( String arg ) 
        {
        return arg.startsWith("@")
            ? FileManager.get().readWholeFileAsUTF8( arg.substring(1) )
            : constructQuery( arg )
            ;
        }
    
    public static final String PREFIXES =
        "PREFIX rdf:    <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
        + "\nPREFIX rdfs:   <http://www.w3.org/2000/01/rdf-schema#>"
        + "\nPREFIX pf:     <http://jena.hpl.hp.com/ARQ/property#>"
        + "\nPREFIX crm:    <http://purl.org/NET/crm-owl#>"
        + "\nPREFIX claros: <http://purl.org/NET/Claros/vocab#>"
        + "\nPREFIX xsd:    <http://www.w3.org/2001/XMLSchema#>"
        + "\nPREFIX arqfn:  <java:uk.ac.ox.zoo.sparqlite.>"
        + "\nPREFIX fast:   <eh:/claros/fast#>"
        + "\nPREFIX cpf:    <java:propertyfunctions.>"
        ;
    
    private static String constructQuery(String arg) 
        {
        return PREFIXES + "\nselect * where { graph ?g { " + arg + " } }";
        }

    public static void runQuery( CommandConfig c, int i, String queryString, Dataset m )
        {
        Query query = QueryFactory.create( queryString, Syntax.syntaxARQ );
        QueryExecution qexec = QueryExecutionFactory.create( query, m );
        if (c.log) qexec.getContext().set( TDB.symLogExec, true );
        if (c.larqIndex != null) LARQ.setDefaultIndex(qexec.getContext(), new IndexLARQ(LARQHelp.getReader(c.larqIndex)));
        try 
            {
            long zero = System.currentTimeMillis();
            int count = 0;
            ResultSet results = qexec.execSelect() ;
            for ( ; results.hasNext() ; )
                {
            	count += 1;
                QuerySolution soln = results.nextSolution() ;
                if (c.show)
                	{
                	// System.err.println( ">> " + soln );
                	for (Iterator<String> strings = soln.varNames(); strings.hasNext();) 
                    	{
                    	String name = strings.next();
                    	RDFNode n = soln.get( name );
                    	System.out.print( "| " + name + ":" + abbreviate(n) );
                    	}
                	System.out.println( " |" );
                	}
                if (c.one) break;
                }
            long time = System.currentTimeMillis() - zero;
            System.err.println( "] logged time for iteration " + i + " (" + count + " elements) is " + (time/1000.0) + "s" );
            } 
        finally 
            { qexec.close() ; }
        }

	private static String abbreviate(RDFNode n) 
		{
		if (n.isURIResource()) return ((Resource) n).getModel().shortForm( n.asNode().getURI() );
		return n.toString().replaceAll("\n", " ");
		}
    }
