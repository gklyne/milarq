package cmd;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class generate_hasterm_triples
    {
    static final Pattern word = Pattern.compile( "[äA-Za-z]+([-:][äA-Za-z]+)*" );
    
    public static void main( String [] args ) throws IOException
        { new generate_hasterm_triples().run(); }
    
    generate_hasterm_triples()
        {
        for (String x: "the per par une les dans del and des auf von den vor mit und der".split( " " )) exclusions.add(x);
        }
    
    private Set<String> exclusions = new HashSet<String>();
    
    public void run() throws IOException
        {
        // int count = 0;
        BufferedReader br = new BufferedReader( new InputStreamReader( System.in ) );
        System.out.println( "@prefix claros: <http://purl.org/NET/Claros/vocab#>." );
        while (true)
            {
            String line = readLongLine(br);
            // count += 1; System.out.println( ">> " + count + ": " + line );
            if (line == null) break;
            String [] parts = line.split( ",", 2 );
            String subject = parts[0];
            if (subject.startsWith( "http://" ))
                {
                boolean needsSubject = true;
                if (parts.length == 2)
                    {
                    for (String w: allMatches( word, parts[1] ))
                        if (w.length() > 2 && !exclusions.contains( w ))
                            {
                            if (needsSubject)
                                {
                                System.out.print( "<" + subject + "> claros:subject-has-term" );
                                needsSubject = false;
                                }
                            else
                                System.out.print( ", " );
                            System.out.print( " \"" + w.toLowerCase() + "\"" );
                            }
                    if (!needsSubject) System.out.println( "." );
                    }
                }
            }
        }

    private String readLongLine( BufferedReader br ) throws IOException
        {
        String s = br.readLine();
        if (s == null) return s;
        while (s.contains( "\"" ) && s.charAt( s.length() - 1 ) != '"') s += br.readLine();
        return s;
        }

    private List<String> allMatches( Pattern p, String s )
        {
        List<String> result = new ArrayList<String>();
        Matcher m = p.matcher( s );
        while (m.find()) result.add( m.group() );
        return result;
        }
    }
