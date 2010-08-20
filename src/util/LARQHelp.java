package util;

import org.apache.lucene.index.IndexReader;

public class LARQHelp
    {
    public static IndexReader getReader( String larqIndex ) 
        {
        try { return IndexReader.open( larqIndex ); }
        catch (Exception e) { throw new RuntimeException( e ); }
        }
    }
