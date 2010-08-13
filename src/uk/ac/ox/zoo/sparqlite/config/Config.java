package uk.ac.ox.zoo.sparqlite.config;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.assembler.Assembler;
import com.hp.hpl.jena.assembler.AssemblerHelp;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
    Class for Sparqlite configuration

    @author chris
*/
public class Config
    {
    Log log = LogFactory.getLog( Config.class );
    
    protected final String storeDescFilePath;
    protected final String PATHINFO;
    
    protected Model description;
    
    public Config( String PATHINFO, String storeDescFilePath )
        { this.PATHINFO = PATHINFO; this.storeDescFilePath = storeDescFilePath; }

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
        Resource root = getDescriptionRoot();
        return (Dataset) Assembler.general.open( root );
        }

    private Resource getDescriptionRoot()
        {
        loadDescriptionIfNecessary();
        Resource root = AssemblerHelp.singleRoot( description, Vocab.RDF_Dataset );
        log.trace( "description root is " + root );
        return root;
        }
    
    private Model loadDescriptionIfNecessary()
        {
        if (description == null)
            {
            String sd = this.getStoreDescFilePath();
            log.trace( "loading description model from " + sd );
            description = FileManager.get().loadModel( sd );
            description.add( Vocab.TDB_Dataset, RDFS.subClassOf, Vocab.RDF_Dataset );            
            }
        return description;
        }
    
    public String getLARQIndexLocation()
        {
        Resource root = getDescriptionRoot();
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
