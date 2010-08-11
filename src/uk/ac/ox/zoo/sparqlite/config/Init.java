package uk.ac.ox.zoo.sparqlite.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.assembler.Assembler;
import com.hp.hpl.jena.assembler.Mode;
import com.hp.hpl.jena.assembler.assemblers.AssemblerBase;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.core.assembler.AssemblerUtils;
import com.hp.hpl.jena.util.FileManager;

import uk.ac.ox.zoo.sparqlite.ServletBase;

public class Init extends ServletBase
    {
    protected static Log log = LogFactory.getLog(Init.class);
    
    private static final long serialVersionUID = 1L;
    
    static class Sparqlite
        {
    
        }
    
    static class InitAssembler extends AssemblerBase
        {
        @Override public Object open( Assembler a, Resource root, Mode mode )
            {
            return new Sparqlite();
            }
        }
    
    static 
        {
        AssemblerUtils.init();
        Assembler.general.implementWith( Vocab.SPARQLITE, new InitAssembler() );
        }

    public void init()
        {
        log.trace( "Sparqlite Init servlet" );
        String configPath = getServletConfig().getInitParameter( Vocab.SPARQLITE_CONFIG );
        String configRoot = getServletConfig().getInitParameter( Vocab.SPARQLITE_ROOT );
        String realConfigName = getServletContext().getRealPath( configPath );
        log.trace( "loading assembly file from " + realConfigName );
        Model config = FileManager.get().loadModel( realConfigName );
        Resource root = config.createResource( configRoot );
        Sparqlite s = (Sparqlite) Assembler.general.open( root );
        System.err.println( ">> " + s );
        Config.setNamedConfig( "sparqlite", new Config( "<spoo>", "<init>" ) );
        }
    }
