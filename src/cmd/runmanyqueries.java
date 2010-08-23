package cmd;

import java.util.*;


import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.query.larq.IndexLARQ;
import com.hp.hpl.jena.query.larq.LARQ;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.sparql.util.Context;
import com.hp.hpl.jena.sparql.util.Symbol;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import uk.ac.ox.zoo.sparqlite.cmd.support.Answer;
import uk.ac.ox.zoo.sparqlite.cmd.support.Result;
import uk.ac.ox.zoo.sparqlite.config.Sparqlite;
import util.*;

/**
    <h2>Run many timing queries.</h2>

    <pre>
        -from f1 ... fn    the test files to run
        -title T           the title to use
        -stylesheet S      name of the stylesheet [should move to weave]
        -groups g1 ... gn  groups to use (all, if not present)
    </pre>
    
    <p>
    Queries are run only if -groups is not specified or if they have at least
    one group in common with the -groups argument.
    </p>
    
    <p>
    The results are written to standard output as Turtle suitable for use
    by weave, qv.
    </p>
    
    <p>
    Each test file takes the form
    </p>
    
    <pre>
    !query title of query
    -groups g1 ... gn
    -count N
    -larq LD
    -tdb TDB
    -label L
    BODY OF QUERY
    </pre>
    
    <p>
    The -options can appear in any order, and multiple options can appear
    on one line. Some options may be omitted.
    </p>
    
    <ul>
    <li>g1...gn are the groups this test is in.
    <li>N is the number of times the test is run to allow for cache warming.
    <li>The default is 1, but 4 is a good working choice.
    <li>LD is the name of a Lucene index directory, if required.
    <li>TDB is the name of the TDB directory where the data-to-query is.
    <li>L is the label used to identify the test to weave.
    <li>BODY OF QUERY is the SPARQL query that will be run.    
    </ul>
    
    @author chris
*/
public class runmanyqueries
    {
    public static void main( String [] args )
        {
        CommandArgs a = CommandArgs.parse( "-from -groups -title -stylesheet", args );
        runmanyqueries rq = new runmanyqueries( a );
        for (String from: a.getAll( "-from" ))
            {
            String completeFile = FileManager.get().readWholeFileAsUTF8( from );
            String [] tests = completeFile.split( "(^|\n)!query *" );
            for (String test: tests)
                if (test.length() > 0)
                    {
                    int cut = test.indexOf('\n');
                    String title = test.substring(0, cut);
                    int argsStart = cut + 1;
                    while (test.charAt(cut+1) == '-') cut = test.indexOf( '\n', cut + 1 );
                    rq.runTest( from, title, test.substring( argsStart, cut ), test.substring( cut ) );
                    }
            }
        Model m = rq.renderAsRDF();
        m.write( System.out, "TTL" );
        }
    
    private Model renderAsRDF()
        {
        Model result = ModelFactory.createDefaultModel();
        result.setNsPrefix( "tf", TF.NS );
        result.setNsPrefix( "xsd", "http://www.w3.org/2001/XMLSchema#" );
        result.setNsPrefix( "bz", "http://www.beazley.ox.ac.uk/record/" );
        result.setNsPrefix( "rdfs", RDFS.getURI() );
        result.setNsPrefix( "rdf", RDF.getURI() );
        result.setNsPrefix( "crm", "http://purl.org/NET/crm-owl#" );
        result.setNsPrefix( "claros", "http://purl.org/NET/Claros/vocab#" );
        result.setNsPrefix( "pf", "http://jena.hpl.hp.com/ARQ/property#" ); 
        result.setNsPrefix( "xsd", "http://www.w3.org/2001/XMLSchema#" );
        result.setNsPrefix( "arqfn", "java:uk.ac.ox.zoo.sparqlite." );
        result.setNsPrefix( "graph", "http://purl.org/NET/Claros/graph/" );
        Resource root = result.createResource().addProperty( RDF.type, TF.TestRun );
        for (Result r: results) r.renderAsRDF( root );
        return result;
        }

    final List<Result> results = new ArrayList<Result>();
    final Set<String> allowed = new HashSet<String>();
    final String styleSheet;
    final String title;
    
    public runmanyqueries( CommandArgs a )
        {
        title = a.getOptional( "-title", "a test run" );
        styleSheet = a.getOptional( "-stylesheet", "styles/results.css" );
        allowed.addAll( a.getAll( "-groups" ) );
        }

    private void runTest( String fromFile, String title, String args, String query )
        {
        String tidyArgs = args.replaceAll( "\n| +", " " );
        CommandConfig c = new CommandConfig( tidyArgs.split( " +" ) );
        Result result = new Result( fromFile, c.label, title, tidyArgs );
        if (permitted( c )) runAndTime( query, result, c );
        }

    private boolean permitted( CommandConfig c )
        {
        return allowed.isEmpty() || containsSome( allowed, c.groups );
        }

    private boolean containsSome( Set<String> allowed, List<String> groups )
        {
        for (String g: groups) if (allowed.contains( g )) return true;
        return false;
        }

    private void runAndTime( String query, Result result, CommandConfig c )
        {
        
        // TODO rest of approriate config goes here
        
        Dataset d = TDBFactory.createDataset( c.TDBloc );
        String queryString = queryFromArg( query );
        result.setQuery( queryString );
        for (int i = 0; i < c.limit; i += 1)
            {
            // System.err.println( ">> " + queryString );
            Query q = QueryFactory.create( queryString, Syntax.syntaxARQ );
            QueryExecution qexec = QueryExecutionFactory.create( q, d );
            setLARQ( c, qexec );
            c.setContext( qexec.getContext() );
            long zero = System.currentTimeMillis();
            ResultSet rs = qexec.execSelect();
            List<String> resultVars = rs.getResultVars();
            List<Map<String, RDFNode>> rows = new ArrayList<Map<String, RDFNode>>();
            result.setVars( resultVars );
            for (; rs.hasNext();)
                {
                QuerySolution soln = rs.nextSolution();
                Map<String, RDFNode> row = new HashMap<String, RDFNode>();
                for (String name: resultVars) row.put( name, soln.get( name ) );
                rows.add( row );
                }
            long time = System.currentTimeMillis() - zero;
            Answer a = new Answer( i, time, rows );
            result.add( a );
            qexec.close();
            }
        results.add( result );
        d.close();
        }

    private void setLARQ( CommandConfig c, QueryExecution qexec )
        {
        if (c.larqIndex != null) 
            LARQ.setDefaultIndex
                ( qexec.getContext()
                , new IndexLARQ(LARQHelp.getReader( c.larqIndex ) )
                );
        }

    private String queryFromArg( String arg )
        {
        return arg.startsWith( "@" )
            ? FileManager.get().readWholeFileAsUTF8( arg.substring(1) )
            : arg
            ;
        }
    }
