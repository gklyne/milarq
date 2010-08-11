package uk.ac.ox.zoo.sparqlite.config;

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

    public Config getConfig( String pathInfo, ServletContext context )
        {
        return Config.create( pathInfo, context );
        }
    }