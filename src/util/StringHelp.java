package util;

import java.util.List;

public class StringHelp
    {
    public static String join( List<String> bits, String gap )
        {
        StringBuilder result = new StringBuilder();
        for (String bit: bits) result.append( gap ).append( bit );
        return result.toString();
        }

    public static String safe( String s )
        {
        return s.replaceAll( "&", "&amp;" ).replaceAll( "<", "&lt;" ).replaceAll( ">", "&gt;" ) + "\n";
        }
    }
