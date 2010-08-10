package uk.ac.ox.zoo.sparqlite.config;

import com.hp.hpl.jena.rdf.model.*;

/**
    Vocabulary constants for configuration. Should probably be done
    properly but that will have to wait.
 
     @author chris
*/
public class Vocab
    {
    public static final Resource RDF_Dataset = ResourceFactory.createResource("http://jena.hpl.hp.com/2005/11/Assembler#RDFDataset");
    
    public static final Resource TDB_Dataset = ResourceFactory.createResource("http://jena.hpl.hp.com/2008/tdb#DatasetTDB");

    public static final Property LARQ_Location = ResourceFactory.createProperty("http://purl.org/net/sparqlite/vocab#larqLocation");
    }
