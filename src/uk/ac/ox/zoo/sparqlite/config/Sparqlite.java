package uk.ac.ox.zoo.sparqlite.config;

import static org.junit.Assert.*;


import javax.servlet.ServletContext;


import com.hp.hpl.jena.assembler.Assembler;
import com.hp.hpl.jena.assembler.Mode;
import com.hp.hpl.jena.assembler.assemblers.AssemblerBase;
import com.hp.hpl.jena.rdf.model.Resource;

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
            return new Sparqlite();
            }
        }
    
    private final ConditionalTransform[] transforms;
    
    public Sparqlite()
        { this( new ConditionalTransform[] {} ); }
    
    public Sparqlite( ConditionalTransform [] transforms )
        {
        this.transforms = transforms;
        }

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
    }
