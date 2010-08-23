package cmd;

import util.CommandArgs;

public class eg
    {
    public static void main( String [] args )
        {
        CommandArgs a = CommandArgs.parse( "-the -rain -in -spain", args );
        }
    }
