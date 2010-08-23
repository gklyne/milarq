/**
 * 
 */
package util;

import java.io.PrintStream;
import java.util.*;

import com.hp.hpl.jena.sparql.util.Context;
import com.hp.hpl.jena.sparql.util.Symbol;

public class CommandConfig
    {
    public String larqIndex;
    public String TDBloc;
    public String label;
    public String queryArg;
    public boolean one;
    public boolean log;
    public boolean show;
    public int limit;
    public List<String> groups;
    public Map<Symbol, String> settings = new HashMap<Symbol, String>();
    
    public static String ALLOWED = "-tdb -label -groups -query -larq -one -log -noShow -count -set";
    
    public void display(PrintStream out)
    	{
    	out.println( "label:        " + label );
    	out.println( "larqIndex:    " + larqIndex );
    	out.println( "tdb location: " + TDBloc );
    	out.println( "query arg:    " + queryArg );
    	out.println( "#repetitions: " + limit );
    	out.println( "show results: " + show );
    	out.println( "only one row: " + show );
    	out.println( "show tdb log: " + log );
    	out.println( "settings:     " + settings );
    	}

    public CommandConfig( String [] args )
        { 
        CommandArgs a = CommandArgs.parse( ALLOWED, args );
        if (a.bad()) throw new RuntimeException( a.messages().toString() );
        String loc = a.getOptional( "-loc" );
        if (loc == null)
            this.TDBloc = a.getOnly("-tdb");
        else
            {
            System.err.println( "NOTE: option -loc has been superceeded by -tdb." );
            this.TDBloc = loc;
            }
        this.label = a.getOptional( "-label", "ANON" );
        this.groups = a.getAll( "-groups" );
        this.queryArg = a.getOptional( "-query" );
        this.larqIndex = a.getOptional( "-larq" );
        this.one = a.getPresent( "-one" );
        this.log = a.getPresent( "-log" );
        this.show = !a.getPresent( "-noShow" );
        this.limit = a.getInteger( "-count", 1 );
        setSettings( a.getAll( "-set" ) );
        if (this.show) this.display( System.err ); 
        }

    private void setSettings( List<String> all )
        {
        for (String setting: all)
            {
            int eq = setting.indexOf( '=' );
            String symbol = setting.substring( 0, eq ), value = setting.substring( eq + 1 );
            settings.put( Symbol.create( symbol ), value );
            }
        }

    static final Symbol i = Symbol.create( "java:propertyfunctions.genericIndex" );
    
    public void setContext( Context context )
        {
//        context.set( i, "gindexes" );
        for (Symbol key: settings.keySet()) context.set( key, settings.get( key ) );
        }
    }