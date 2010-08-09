package uk.ac.ox.zoo.sparqlite.config;

import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.shared.NotFoundException;

/**
    Class for Sparqlite configuration

    @author chris
*/
public class Config
    {

    public static Config neutral = new Config();

    public static Config fake( String storeDescFilePath )
        {
        return new Config();
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

    }
