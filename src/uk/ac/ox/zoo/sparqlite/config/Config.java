package uk.ac.ox.zoo.sparqlite.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import com.hp.hpl.jena.shared.NotFoundException;

/**
    Class for Sparqlite configuration

    @author chris
*/
public class Config
    {
    public static Config neuxxtral = new Config( "<pathinfo>", "<neutral>" );

    protected final String storeDescFilePath;
    protected final String PATHINFO;
    
    public Config( String PATHINFO, String storeDescFilePath )
        { this.PATHINFO = PATHINFO; this.storeDescFilePath = storeDescFilePath; }
    
    public static Config fake( String storeDescFilePath )
        {
        return new Config( "<PATHINFO>", storeDescFilePath );
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

    public static Config create( String pathInfo, ServletContext context )
        {
        String storeDescFilePath = context.getRealPath( "WEB-INF/tdb" + pathInfo + ".ttl" );
        return new Config( pathInfo, storeDescFilePath );
        }

    public String getStoreDescFilePath()
        { return storeDescFilePath; }

    public boolean getStoreDescFileExists()
        { return new File( storeDescFilePath ).exists(); }
    
    public String GETPATHINFO()
        { return PATHINFO; }

    }
