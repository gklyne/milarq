package uk.ac.ox.zoo.sparqlite.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.assembler.Assembler;
import com.hp.hpl.jena.assembler.AssemblerHelp;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.shared.NotFoundException;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
    Class for Sparqlite configuration

    @author chris
*/
public class Config
    {
    
    Log log = LogFactory.getLog( Config.class );
    
    public static Config neuxxtral = new Config( "<pathinfo>", "<neutral>" );

    protected final String storeDescFilePath;
    protected final String PATHINFO;
    
    public Config( String PATHINFO, String storeDescFilePath )
        { this.PATHINFO = PATHINFO; this.storeDescFilePath = storeDescFilePath; }
    
    public static Config fake( String storeDescFilePath )
        {
        return new Config( "<PATHINFO>", storeDescFilePath );
        }

    static final Map<String, Config> namedConfigs = new HashMap<String, Config>();
    
    public static Config getNamedConfig( String name )
        {
        Config c = namedConfigs.get( name );
        if (c == null) throw new NotFoundException( name );
        return c;
        }

    public static void setNamedConfig( String name, Config config )
        {
        namedConfigs.put( name, config );
        }

    public static Config create( String pathInfo, ServletContext context )
        {
        String storeDescFilePath = context.getRealPath( "WEB-INF/tdb" + pathInfo + ".ttl" );
        return new Config( pathInfo, storeDescFilePath );
        }

    public String getStoreDescFilePath()
        { return storeDescFilePath; }

    public boolean getStoreDescFileExists()
        { return new File( storeDescFilePath ).exists(); }
    
    public String GETPATHINFO()
        { return PATHINFO; }

    public boolean getSymUnionDefaultGraph()
        { return false; }

    public Dataset getDataset()
        {
        String sd = this.getStoreDescFilePath();
        log.trace( "assembling dataset from " + sd );
        Model model = FileManager.get().loadModel( sd );
        model.add( Vocab.TDB_Dataset, RDFS.subClassOf, Vocab.RDF_Dataset );
        Resource root = AssemblerHelp.singleRoot( model, Vocab.RDF_Dataset );
        log.trace( "dataset root is " + root );
        return (Dataset) Assembler.general.open( root );
        }
    
    public String getLARQIndexLocation()
        {
        String sd = this.getStoreDescFilePath();
        log.trace( "assembling dataset from " + sd );
        Model model = FileManager.get().loadModel( sd );
        model.add( Vocab.TDB_Dataset, RDFS.subClassOf, Vocab.RDF_Dataset );
        Resource root = AssemblerHelp.singleRoot( model, Vocab.RDF_Dataset );
        log.trace( "dataset root is " + root );
        Statement locStatement = root.getProperty( Vocab.LARQ_Location );
        if (locStatement == null)
            {
            log.trace( "no LARQ location provided" );
            return null;
            }
        else
            {
            String location = locStatement.getString();
            log.trace( "LARQ location " + location + " provided" );
            return location;                   
            }
        }
    }
