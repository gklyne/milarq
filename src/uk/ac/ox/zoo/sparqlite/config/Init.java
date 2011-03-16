package uk.ac.ox.zoo.sparqlite.config;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.assembler.Assembler;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.BrokenException;
import com.hp.hpl.jena.sparql.core.assembler.AssemblerUtils;

import uk.ac.ox.zoo.sparqlite.ServletBase;

public class Init extends ServletBase
    {
    protected static Log log = LogFactory.getLog(Init.class);
    
    private static final long serialVersionUID = 1L;
    
    protected static Sparqlite sparqlite = null;
    
    static 
        {
        AssemblerUtils.init();
        Assembler.general.implementWith( Vocab.SPARQLITE, new Sparqlite.Make() );
        }

    public void init()
        {
        log.trace( "Sparqlite Init servlet" );
        String configPath = getServletConfig().getInitParameter( Vocab.SPARQLITE_CONFIG );
        String configRoot = getServletConfig().getInitParameter( Vocab.SPARQLITE_ROOT );
        String realConfigName = getServletContext().getRealPath( configPath );
        log.trace( "loading assembly file from " + realConfigName );
        Model config = AssemblerModelLoader.get(realConfigName);
        Resource root = config.createResource( configRoot );
        sparqlite = (Sparqlite) Assembler.general.open( root );
        log.trace( "assembled sparqlite configuration object" );
        sparqlite.registerPropertyFunctions();
        }
    
    public static Sparqlite getSparqlite()
        {
        if (sparqlite == null) throw new BrokenException
            ( "attempt to get Sparqlite object, but no such object has been assembled" );
        return sparqlite;
        }
    }
