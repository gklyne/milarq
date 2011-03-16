package uk.ac.ox.zoo.sparqlite.config;

import java.io.File;

import com.hp.hpl.jena.assembler.Assembler;
import com.hp.hpl.jena.assembler.AssemblerHelp;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.RDFS;

public class ConfigByFile extends Config
    {    
    protected final String storeDescFilePath;
    protected Model description;
    
    public ConfigByFile( String PATHINFO, String storeDescFilePath )
        { this( PATHINFO, new IndexMap(), storeDescFilePath, false );  }    
    
    public ConfigByFile( String PATHINFO, IndexMap indexMap, String storeDescFilePath, boolean defaultGraphIsUnion  )
        { 
        super( PATHINFO, indexMap, defaultGraphIsUnion ); 
        this.storeDescFilePath = storeDescFilePath; 
        }

    @Override public String getStoreName()
        { return storeDescFilePath; }
    
    @Override public boolean getStoreExists()
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
            description = AssemblerModelLoader.get(sd);
            description.add( Vocab.TDB_Dataset, RDFS.subClassOf, Vocab.RDF_Dataset );            
            }
        return description;
        }
    
    @Override public String getLARQIndexLocation()
        {
        Resource root = getDescriptionRoot();
        log.trace( "dataset root is " + root );
        return getLARQIndexLocation( root );
        }

    @Override public String getCompositeIndexDirectory()
        { return null; }
    }
