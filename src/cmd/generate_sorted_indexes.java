package cmd;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

import util.CommandArgs;

import au.com.bytecode.opencsv.CSVReader;

public class generate_sorted_indexes
    {
    private static final Comparator<Element> byEarliestDate = new Comparator<Element>()
        {
        @Override public int compare( Element a, Element b )
            { return a.notBefore - b.notBefore; }
        };
        
    private static final Comparator<Element> reversedByLatestDate = new Comparator<Element>()
        {
        @Override public int compare( Element b, Element a )
            { return a.notAfter - b.notAfter; }
        };

    public static void main( String [] args ) throws IOException
        {
        new generate_sorted_indexes( args ).run();
        }
       
    generate_sorted_indexes( String [] args )
        {
        CommandArgs a = CommandArgs.parse( "-indexes -ug -nb -na", args );
        if (a.bad()) throw new IllegalArgumentException( a.messages().toString() );
        indexDir = a.getOptional( "-indexes", "indexes" );
        useGenericFormat = asBoolean( a.getOptional( "-ug", "false" ) );
        notBeforeOnly = asBoolean( a.getOptional( "-nb", "false" ) );
        notAfterOnly = asBoolean( a.getOptional( "-na", "false" ) );
        }
    
    public static final Pattern True = Pattern.compile( "^(true|t|yes)$", Pattern.CASE_INSENSITIVE );
    public static final Pattern False = Pattern.compile( "^(false|f|no)$", Pattern.CASE_INSENSITIVE );
    
    private boolean asBoolean( String b )
        {
        if (True.matcher( b ).find()) return true;
        if (False.matcher( b ).find()) return false;
        throw new IllegalArgumentException( "not a boolean value: " + b );
        }

    final String indexDir;
    final boolean useGenericFormat;
    final boolean notBeforeOnly;
    final boolean notAfterOnly;
    
    static class Element
        {
        final String subject;
        final int notBefore;
        final int notAfter;

        public Element( String subject, int notBefore, int notAfter )
            { this.subject = subject; this.notBefore = notBefore; this.notAfter = notAfter; }
        }

    public void run() throws IOException
        {
        BufferedReader br = new BufferedReader( new InputStreamReader( System.in ) );
        CSVReader r = new CSVReader( br );
        String currentTerm = "";
        List<Element> elementsForCurrentTerm = new ArrayList<Element>();
        while (true)
            {
            String [] line = r.readNext();
            if (line == null) break;
            String term = line[0], subject = line[1];
            int notBefore = dateInteger( line[2] ), notAfter = dateInteger( line[3] );
            if (!term.equals( currentTerm ))
                {
                if (currentTerm.length() > 0) outputIndex( currentTerm, elementsForCurrentTerm );
                elementsForCurrentTerm.clear();
                currentTerm = term;
                }
            elementsForCurrentTerm.add( new Element( subject, notBefore, notAfter ) );
            }
        }

    private int dateInteger( String s )
        {
        String cleaned = s
            .replaceAll( " *\\d* *\\p{Alpha}+ *", "" )
            .replaceAll( " *\\p{Alpha}+ *\\d\\d? +", "" )
            .replaceAll( "\\d\\d? +", "" )
            .replaceAll( "[^-\\d]*", "" )
            ;
        if (cleaned.length() == 0) return 9999;
        // System.err.println( ">> '" + s + "' becomes '" + cleaned + "', length " + cleaned.length() );
        return Integer.parseInt( cleaned );
        }

    private void outputIndex( String currentTerm, List<Element> elementsForCurrentTerm ) throws IOException
        {
    	if (notBeforeOnly)
    	    {
            Collections.sort( elementsForCurrentTerm, byEarliestDate );
	        writeIndexFile( elementsForCurrentTerm, new File( indexDir, currentTerm ) );    		
    	    }
    	else if (notAfterOnly)
    	    {
            Collections.sort( elementsForCurrentTerm, reversedByLatestDate );
	        writeIndexFile( elementsForCurrentTerm, new File( indexDir, currentTerm ) );    		
    	    }
    	else
	    	{
            Collections.sort( elementsForCurrentTerm, byEarliestDate );
	        writeIndexFile( elementsForCurrentTerm, new File( indexDir, currentTerm + "-nb" ) );
	        Collections.sort( elementsForCurrentTerm, reversedByLatestDate );
	        writeIndexFile( elementsForCurrentTerm, new File( indexDir, currentTerm + "-na" ) );
	    	}
        }

    private void writeIndexFile( List<Element> elementsForCurrentTerm, File target ) throws FileNotFoundException
        {
        BufferedOutputStream bos = new BufferedOutputStream( new FileOutputStream( target ) );
        PrintStream ps = new PrintStream( bos );
        for (Element e: elementsForCurrentTerm)
            {
            if (useGenericFormat)
                {
                ps.print( "<" );
                ps.print( e.subject );
                ps.print( ">" );
                }
            else
                ps.print( e.subject );
            ps.print( "," );
            ps.print( e.notBefore );
            ps.print( "," );
            ps.print( e.notAfter );
            ps.println();
            }
        ps.flush();
        ps.close();
        }
    }
