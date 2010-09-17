package cmd;

import java.io.*;
import java.util.*;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.vocabulary.RDF;

import uk.ac.ox.zoo.sparqlite.config.*;
import util.CommandArgs;

public class generic_indexing_example
    {
    private static final String MYPF = "eh:/my-property-function";
    private static final String INDEX_FILE = "example.spoo";
    private static final String TDB_DIRECTORY = "example.tdb";

    public static void main( String [] args ) throws IOException
        { new generic_indexing_example( CommandArgs.parse( args ) ).run(); }
    
    private final String directory;
    
    public generic_indexing_example( CommandArgs a )
        {
        directory = a.getOptional( "-indir", "work" );
        }
    
    public void run() throws IOException
        {
        Sparqlite s = createSparqlite();
        createIndexFile();
        Dataset d = createTDB();
        s.registerPropertyFunctions();
        Config c = s.getConfig( "fakePathInfo", PathMapper.Create.usePrefix( "MUN" ) );
        String queryString =
            "select * where"
            + "\n{"
            + "\n?s <!PF!> ('!IF!' ?v)"
                .replace( "!IF!", INDEX_FILE )
                .replace( "!PF!", MYPF )
            + "\n. ?s <eh:/property> ?w"
            + "\n}" 
            + "\n"
            ;
        System.err.println( ">> query string is:\n" + queryString );
        Query q = QueryFactory.create( queryString );
        QueryExecution e = QueryExecutionFactory.create( q, c.getDataset() );
        c.setContext( e.getContext() );
        ResultSet rs = e.execSelect();
        while (rs.hasNext()) System.err.println( pretty( rs.next() ) );
        }

    private String pretty( QuerySolution q )
        {
        StringBuilder b = new StringBuilder();
        for (String n: asSet( q.varNames() ))
            {
            b.append( n ).append( "=" ).append( q.get( n ) ).append( "\n" );
            }
        return b.toString();
        }

    private <T> Set<T> asSet( Iterator<T> it )
        {
        Set<T> result = new HashSet<T>();
        while (it.hasNext()) result.add( it.next() );
        return result;
        }

    private void createIndexFile() throws FileNotFoundException
        {
        FileOutputStream fos = new FileOutputStream( directory + "/" + INDEX_FILE );
        PrintStream out = new PrintStream( fos ); 
        out.println( "<http://example.com/thing>,17" );
        out.println( "<http://example.com/thunk>,99" );
        out.close();
        }
    
    private Dataset createTDB()
        {
        String path = directory + "/" + TDB_DIRECTORY;
        new File( path ).mkdir();
        Dataset d = TDBFactory.createDataset( path );
        addExampleData( d.getDefaultModel() );
        System.err.println( ">> TDB contains: " );
        d.getDefaultModel().write( System.err, "TTL" );
        return d;
        }

    private void addExampleData( Model m )
        {
        if (true) m.add
            (
            m.createResource( "http://example.com/thing" ),
            m.createProperty( "eh:/property" ),
            m.createLiteral( "Epistle" )
            );
        if (false) m.add
            (
            m.createResource( "http://example.com/thunk" ),
            m.createProperty( "eh:/not-the-one" ),
            m.createLiteral( "Spikard" )
            );
        }

    private Sparqlite createSparqlite()
        {
        Resource root = constructAssemblerDescription();
        return (Sparqlite) new Sparqlite.Make().open( root );
        }

    private Resource constructAssemblerDescription()
        {
        Model m = ModelFactory.createDefaultModel();
        Resource root = m.createResource( "eh:/" );
        Resource mapIf = m.createResource();
        Resource ds = m.createResource( "http://example.com/x/replacement/fakePathInfo" );
        Resource r = m.createResource( "eh:/registration" );
        Resource i = m.createResource( "eh:/indexing" );
        Property LOC = m.createProperty( "http://jena.hpl.hp.com/2008/tdb#location" );
        root
            .addProperty( RDF.type, Vocab.SPARQLITE )
            .addProperty( Vocab.mapIf, mapIf )
            .addProperty( Vocab.sparqliteDataset, ds )
            .addProperty( Vocab.register, r )
            .addProperty( Vocab.index, i )
            .addLiteral( Vocab.defaultGraphIsUnion, false )
            ;
        ds
            .addProperty( RDF.type, Vocab.TDB_Dataset )
            .addProperty( LOC, directory + "/" + TDB_DIRECTORY )
            ;
        i
            .addProperty( Vocab.forPredicate, MYPF )
            .addProperty( Vocab.useDirectory, directory )
            ;
        r
            .addProperty( Vocab.forPredicate, MYPF )
            .addProperty( Vocab.useClass, propertyfunctions.genericIndex.class.getName() );
        mapIf
            .addProperty( Vocab.matches, ".*" )
            .addProperty( Vocab.replacement, "http://example.com/x/replacement/%s" )
            ;
        m.setNsPrefix( "ja", "http://jena.hpl.hp.com/2005/11/Assembler#>" );
        m.setNsPrefix( "tdb", "http://jena.hpl.hp.com/2008/tdb#" );
        m.setNsPrefix( "lite", "http://purl.org/net/sparqlite/vocab#" );
        m.write( System.out, "TTL" );
        return root;
        }
    
    }
