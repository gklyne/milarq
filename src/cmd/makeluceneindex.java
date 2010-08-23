package cmd;

import java.io.*;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import au.com.bytecode.opencsv.CSVReader;

import util.*;

/**
    <p>
    make a Lucene index for the data found in the specified CSV file,
    which has the format:
    </p>
    
    <pre>
        later:-DIGITS, early:-DIGITS, s:SOMEURI, term:SOMETERM
    </pre>
    
    arguments:
    
    <pre>
      -csv filename
      -larq directoryname
    </pre>
*/
public class makeluceneindex
    {
    public static void main( String [] args ) throws CorruptIndexException, IOException
        {
        CommandArgs a = CommandArgs.parse( args );
        Analyzer analyzer = new StandardAnalyzer();
        Directory directory = FSDirectory.getDirectory( a.getOnly( "-larq" ) );
        IndexWriter iwriter = new IndexWriter( directory, analyzer, true );
        iwriter.setMaxFieldLength( 25000 );
        
//        String allContent = FileManager.get().readWholeFileAsUTF8( a.getOnly( "-csv" ) );
//        String [] lines = allContent.split( "\n" );
        
        Reader x = new FileReader( new File( a.getOnly( "-csv" ) ) );
        CSVReader r = new CSVReader( x );
        
        while (true)
            { // later:-450, early:-500, s:http://www.beazley.ox.ac.uk/record/31C67A69-67BD-4CC5-BBD6-8D891CCD1D93, term:skyphos
            String [] cols = r.readNext();
            if (cols == null) break;
            // System.err.println( ">> " + Arrays.asList( cols ) );
            String later = cols[0].substring( 6 ), early = cols[1].substring( 7 );
            String subject = cols[2].substring( 3 ), term = cols[3].substring( 5 );
            Document doc = new Document();
            doc.add( new Field( "earliest", early, Field.Store.YES, Field.Index.UN_TOKENIZED ) );
            doc.add( new Field( "latest", later, Field.Store.YES, Field.Index.UN_TOKENIZED ) );
            doc.add( new Field( "subject", subject, Field.Store.YES, Field.Index.NO ) );
            doc.add( new Field( "term", term, Field.Store.YES, Field.Index.TOKENIZED ) );
            iwriter.addDocument(doc);
            }
        
        iwriter.optimize();
        iwriter.close();
        }    
    
    public static String[] splitCSV( String line )
        {
        CSVReader r = new CSVReader( new StringReader( line ) );
        try { return r.readNext(); }
        catch (IOException e) { throw new RuntimeException( e ); }
        }
    }
