package cmd;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;

import util.CommandArgs;

/**
    <p>
    Spike to try out retrieving with a Lucene index. 
    </p>
    
    <pre>
        -larq  Lucene index directory
        -term T term to search for in `term` field of index
    </pre>    
    
    <p>
    Outputs subjects with their earliest values which refer to that T. 
    </p>
*/
public class readbyterm
    {
    public static void main( String [] args ) throws CorruptIndexException, IOException, ParseException
        {
        CommandArgs a = CommandArgs.parse( args );
        Analyzer analyzer = new StandardAnalyzer();
        IndexSearcher isearcher = new IndexSearcher( a.getOnly( "-larq" ) );
        QueryParser parser = new QueryParser( "term", analyzer );
        Query query = parser.parse( a.getOnly( "-term" ) );
        Sort s = new Sort( "earliest" );
        Hits hits = isearcher.search( query, s );
        for (int i = 0; i < hits.length(); i++) 
            {
            Document hitDoc = hits.doc(i);
            System.out.println( ">> " + hitDoc.get("subject") + " & " + hitDoc.get( "earliest" ) );
            }
        isearcher.close();
        }
    }
