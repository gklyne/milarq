package uk.ac.ox.zoo.sparqlite.config;

import java.util.*;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.assembler.Assembler;
import com.hp.hpl.jena.assembler.Mode;
import com.hp.hpl.jena.assembler.assemblers.AssemblerBase;
import com.hp.hpl.jena.rdf.model.*;

/**
    Sparqlite configuration object and an assembler for it. 
     @author chris
*/
public class Sparqlite
    {      
    static final protected Log log = LogFactory.getLog( Config.class );
    
    public static class Make extends AssemblerBase
        {
        @Override public Object open( Assembler a, Resource root, Mode mode )
            {
            List<ConditionalTransform> ts = getTransformsFor( root );
            IndexMap im = getIndexMapFor( root );
            log.trace( "creating new Sparqlite with root " + root + " and transforms " + ts );
            return new Sparqlite( root, ts, im );
            }

        private IndexMap getIndexMapFor( Resource root )
            {
            IndexMap im = new IndexMap();
            for (RDFNode v: root.listProperties( Vocab.index ).mapWith( Statement.Util.getObject ).toList())
                {
                if (v.isResource())
                    {
                    Resource f = (Resource) v;
                    Literal p = getUniqueLiteral( f, Vocab.forPredicate );
                    Literal d = getUniqueLiteral( f, Vocab.useDirectory );
                    im.put( p.getLexicalForm(), d.getLexicalForm() );
                    }
                else 
                    throw new RuntimeException( "the object " + v + " of an index must be a Resource" );
                }
            return im;
            }

        private List<ConditionalTransform> getTransformsFor( Resource root )
            {
            List<ConditionalTransform> ts = new ArrayList<ConditionalTransform>();
            for (RDFNode v: root.listProperties( Vocab.mapIf ).mapWith( Statement.Util.getObject ).toList())
                {
                if (v.isResource())
                    {
                    Resource f = (Resource) v;
                    Literal m = getUniqueLiteral( f, Vocab.matches );
                    Literal r = getUniqueLiteral( f, Vocab.replacement );
                    Pattern RE = Pattern.compile( m.getLexicalForm() );
                    ts.add( new ConditionalTransform( RE, r.getLexicalForm() ) );
                    } 
                else 
                    throw new RuntimeException( "the object " + v + " of a mapIf must be a Resource" );
                }
            return ts;
            }
        }
    
    private final Resource root;
    private final IndexMap indexMap;
    private final List<ConditionalTransform> transforms;
    
    private static final Resource missingRoot = 
        ModelFactory.createDefaultModel().createResource( "eh:/NoAssemblyRoot" );
    
    public Sparqlite()
        { this( new ConditionalTransform[] {} ); }
    
    public Sparqlite( ConditionalTransform [] transforms )
        { this( Arrays.asList( transforms ) ); }

    public Sparqlite( List<ConditionalTransform> transforms )
        {
        this( missingRoot, transforms, new IndexMap() ); 
        }

    public Sparqlite( Resource root, List<ConditionalTransform> transforms, IndexMap indexMap )
        { 
        this.root = root;
        this.indexMap = indexMap;
        this.transforms = transforms; 
        }

    public Config getConfig( String pathInfo, PathMapper context )
        {
        String loc = pathToLocation( pathInfo );
        if (looksLikeURI( loc ))
            {
            Resource r = root.getModel().createResource( loc );
            if (!root.hasProperty( Vocab.sparqliteDataset, r )) 
                throw new RuntimeException( "the dataset resource " + r + " for " + pathInfo + " is not a dataset object of " + root );
            return new ConfigByModel( pathInfo, indexMap, r );
            }
        else
            {
            String storeDescFilePath = context.mapPath( loc );
            return new ConfigByFile( pathInfo, indexMap, storeDescFilePath );
            }
        }
    
    private boolean looksLikeURI( String loc )
        {
        return loc.matches( "^https?://.*" );
        }

    public String pathToLocation( String pathInfo )
        {
        for (ConditionalTransform t: transforms)
            if (t.RE.matcher( pathInfo ).matches())
                return t.substituteWith( pathInfo );
        return pathInfo;
        }

    public Set<ConditionalTransform> getTransforms()
        { return new HashSet<ConditionalTransform>( transforms ); }
    }
