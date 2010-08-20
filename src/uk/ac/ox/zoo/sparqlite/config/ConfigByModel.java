package uk.ac.ox.zoo.sparqlite.config;

import com.hp.hpl.jena.assembler.Assembler;
import com.hp.hpl.jena.assembler.Mode;
import com.hp.hpl.jena.assembler.assemblers.AssemblerBase;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Resource;

public class ConfigByModel extends Config
    {
    public ConfigByModel( String pathInfo, IndexMap indexMap, Resource datasetRoot )
        {
        super( pathInfo, indexMap );
        this.datasetRoot = datasetRoot;
        this.compositeIndexDirectory = (String) new AB().open( datasetRoot );
        }
    
    static class AB extends AssemblerBase
        {
        @Override public Object open( Assembler a, Resource root, Mode mode )
            { return getUniqueString( root, Vocab.compositeIndexDirectory ); }
        }
    
    private final Resource datasetRoot;
    private final String compositeIndexDirectory;

    @Override public Dataset getDataset()
        { return (Dataset) Assembler.general.open( datasetRoot ); }

    @Override public String getLARQIndexLocation()
        { return getLARQIndexLocation( datasetRoot ); }

    @Override public String getStoreName()
        { return datasetRoot.getURI(); }

    @Override public boolean getStoreExists()
        { return true; }

    @Override public String getCompositeIndexDirectory()
        { return compositeIndexDirectory; }

    }
