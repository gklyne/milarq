package uk.ac.ox.zoo.sparqlite.config;

import uk.ac.ox.zoo.sparqlite.ServletBase;

public class Init extends ServletBase
    {
    private static final long serialVersionUID = 1L;

    public void init()
        {
        Config.setNamedConfig( "sparqlite", new Config() );
        }
    }
