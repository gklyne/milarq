package uk.ac.ox.zoo.sparqlite.config;

import java.io.File;

import com.hp.hpl.jena.assembler.Assembler;
import com.hp.hpl.jena.assembler.AssemblerHelp;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDFS;

public class ConfigByFile extends Config
    {    
    protected final String storeDescFilePath;
    protected Model description;
    
    public ConfigByFile( String PATHINFO, String storeDescFilePath )
        { super( PATHINFO ); this.storeDescFilePath = storeDescFilePath; }

    public String getStoreName()
        { return storeDescFilePath; }
    
    public boolean getStoreExists()
        { return new File( storeDescFilePath ).exists(); }
    
    @Override public Dataset getDataset()
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
            String sd = this.getStoreName();
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
        return getLARQIndexLocation( root );
        }
    

    }
