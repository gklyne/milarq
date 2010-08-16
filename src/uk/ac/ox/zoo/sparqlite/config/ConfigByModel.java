package uk.ac.ox.zoo.sparqlite.config;

import com.hp.hpl.jena.assembler.Assembler;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Resource;

public class ConfigByModel extends Config
    {

    public ConfigByModel( String pathInfo, Resource datasetRoot )
        {
        super( pathInfo );
        this.datasetRoot = datasetRoot;
        }
    
    private final Resource datasetRoot;

    @Override public Dataset getDataset()
        { return (Dataset) Assembler.general.open( datasetRoot ); }

    @Override public String getLARQIndexLocation()
        { return getLARQIndexLocation( datasetRoot ); }

    @Override public String getStoreName()
        { return datasetRoot.getURI(); }

    @Override public boolean getStoreExists()
        { return true; }

    }
