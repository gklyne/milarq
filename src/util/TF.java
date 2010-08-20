package util;

import com.hp.hpl.jena.rdf.model.*;

public class TF
    {
    public static final String NS = "http://epimorphics.com/vocabularies/tf/";
    
    public static final Resource TestRun = r( "TestRun" );

    public static final Resource TestInstance = r( "TestInstance" );

    public static final Property fromFile = p( "fromFile" );

    public static final Property withTitle = p( "withTitle" );
    
    public static final Property withLabel = p( "withLabel" );

    public static final Property withArguments = p( "withArguments" );

    public static final Property queryVariables = p( "queryVariables" );

    public static final Property queryVar = p( "queryVar" );

    public static final Property withQuery = p( "withQuery" );

    public static final Resource Iteration = r( "Iteration" );

    public static final Property iteration = p( "iteration" );

    public static final Property testInstance = p( "testInstance" );

    public static final Property index = p( "index" );

    public static final Property timeInMs = p( "timeInMs" );

    public static final Property varName = p( "varName" );

    public static final Property varValue = p( "varValue" );

    public static final Property resultRow = p( "resultRow" );

    public static final Property binding = p( "binding" );


    private static Resource r( String local )
        { return ResourceFactory.createResource( NS + local ); }

    private static Property p( String local )
        { return ResourceFactory.createProperty( NS + local ); }

    }
