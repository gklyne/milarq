package uk.ac.ox.zoo.sparqlite.config;

import com.hp.hpl.jena.rdf.model.*;

/**
    Vocabulary constants for configuration. Should probably be done
    properly but that will have to wait.
 
     @author chris
*/
public class Vocab
    {
    static Resource r( String URI )
        { return ResourceFactory.createResource( URI ); }
    
    static Property p( String URI )
        { return ResourceFactory.createProperty( URI ); }
    
    /** 
        type of RDF datasets for assemblers
    */
    public static final Resource RDF_Dataset = r("http://jena.hpl.hp.com/2005/11/Assembler#RDFDataset");
    
    /** 
        type of TDB datasets for assemblers 
    */
    public static final Resource TDB_Dataset = r("http://jena.hpl.hp.com/2008/tdb#DatasetTDB");

    /** 
        property for LARQ index location, for assemblers 
    */
    public static final Property LARQ_Location = p("http://purl.org/net/sparqlite/vocab#larqLocation");

    /**
        Type of Sparqlite object, for assemblers.
    */
    public static final Resource SPARQLITE = r( "http://purl.org/net/sparqlite/vocab#Sparqlite" );

    /** 
        Sparqlite configuration init param name: value is the relative name
        of the Sparqlite assembly file. 
    */
    static final String SPARQLITE_CONFIG = "uk.ac.ox.zoo.sparqlite.config";

    /**
       Sparqlite configuration root param name: value is the URL of the
       root resource of the Sparqlite assembly file.
    */
    public static final String SPARQLITE_ROOT = "uk.ac.ox.zoo.sparqlite.root";

    /**
        Property of Sarqlite configuration objects giving a mapping fragment. 
    */
    public static final Property mapIf = p( "http://purl.org/net/sparqlite/vocab#mapIf" );

    /**
        Property of mapIf objects giving the match regexp. 
    */
    public static final Property matches = p( "http://purl.org/net/sparqlite/vocab#matches" );

    /**
        Property of mapIf objects giving a replacement string. 
    */
    public static final Property replacement = p( "http://purl.org/net/sparqlite/vocab#replacement" );

    /**
        Property of Sparqlite object that refers to the dataset configuration
    */
    public static final Property sparqliteDataset = p( "http://purl.org/net/sparqlite/vocab#dataset" );
    }
