/**
 * 
 */
package util;

import java.io.PrintStream;
import java.util.List;

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
    
    public void display(PrintStream out)
    	{
    	out.println( "label:        " + label );
    	out.println( "largIndex:    " + larqIndex );
    	out.println( "tdb location: " + TDBloc );
    	out.println( "query arg:    " + queryArg );
    	out.println( "#repetitions: " + limit );
    	out.println( "show results: " + show );
    	out.println( "only one row: " + show );
    	out.println( "show tdb log: " + log );
    	}

    public CommandConfig( String [] args )
        { 
        CommandArgs a = CommandArgs.parse( args );
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
        this.queryArg = a.getOptional("-query");
        this.larqIndex = a.getOptional("-larq");
        this.one = a.getPresent("-one");
        this.log = a.getPresent("-log");
        this.show = !a.getPresent("-noShow");
        this.limit = a.getInteger("-count", 1);
        if (this.show) this.display( System.err ); 
        }
    }