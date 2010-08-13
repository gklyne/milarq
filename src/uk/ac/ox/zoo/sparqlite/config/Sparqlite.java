package uk.ac.ox.zoo.sparqlite.config;

import java.util.*;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

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
    public static class Make extends AssemblerBase
        {
        @Override public Object open( Assembler a, Resource root, Mode mode )
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
                    } // TODO: else throw some exception
                }
            return new Sparqlite( ts );
            }
        }
    
    private final List<ConditionalTransform> transforms;
    
    public Sparqlite()
        { this( new ConditionalTransform[] {} ); }
    
    public Sparqlite( ConditionalTransform [] transforms )
        { this.transforms = Arrays.asList( transforms ); }

    public Sparqlite( List<ConditionalTransform> transforms )
        { this.transforms = transforms; }

    // TODO this should be controlled by the specification file
    public Config getConfig( String pathInfo, ServletContext context )
        {
        String storeDescFilePath = context.getRealPath( "WEB-INF/tdb" + pathInfo + ".ttl" );
        return new Config( pathInfo, storeDescFilePath );
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
