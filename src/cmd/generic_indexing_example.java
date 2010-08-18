package cmd;

import java.io.*;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.sparql.util.Symbol;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.vocabulary.RDF;

import uk.ac.ox.zoo.sparqlite.config.*;
import util.CommandArgs;

public class generic_indexing_example
    {
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
//        PropertyFunctionRegistry.get().put( "eh:/my-property-function", genericIndex.class );
        createIndexFile();
        Dataset d = createTDB();
        Sparqlite s = createSparqlite();
        s.registerPropertyFunctions();
        Config c = s.getConfig( "fakePathInfo", PathMapper.Create.usePrefix( "MUN" ) );
        String queryString =
            "select * where"
            + "\n{"
            + "\n?s <eh:/my-property-function> ('!IF!' ?v)".replace( "!IF!", INDEX_FILE )
            + "\n}"
            ;
        Query q = QueryFactory.create( queryString );
        QueryExecution e = QueryExecutionFactory.create( q, c.getDataset() );
        e.getContext().set( Symbol.create("eh:/my-property-function"), directory );
        c.setContext( e.getContext() );
        ResultSet rs = e.execSelect();
        while (rs.hasNext()) System.err.println( rs.next() );
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
        return d;
        }

    private void addExampleData( Model m )
        {
        // none at present
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
        Property LOC = m.createProperty( "http://jena.hpl.hp.com/2008/tdb#location" );
        root
            .addProperty( RDF.type, Vocab.SPARQLITE )
            .addProperty( Vocab.mapIf, mapIf )
            .addProperty( Vocab.sparqliteDataset, ds )
            .addProperty( Vocab.compositeIndexDirectory, directory )
            .addProperty( Vocab.register, r )
            ;
        ds
            .addProperty( RDF.type, Vocab.TDB_Dataset )
            .addProperty( LOC, directory + "/" + TDB_DIRECTORY )
            ;
        r
            .addProperty( Vocab.forPredicate, "eh:/my-property-function" )
            .addProperty( Vocab.useClass, propertyfunctions.genericIndex.class.getName() );
        mapIf
            .addProperty( Vocab.matches, ".*" )
            .addProperty( Vocab.replacement, "http://example.com/x/replacement/%s" )
            ;
        return root;
        }
    
    }
