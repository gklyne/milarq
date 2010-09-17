package uk.ac.ox.zoo.sparqlite.cmd.support;

import java.io.IOException;
import java.io.Reader;

/**
    A Reader that has nothing in it.
    
     @author chris
*/
public class EmptyReader extends Reader
    {
    /**
        Ignore the arguments and return -1 for EOF.
    */
    @Override public int read( char[] cbuf, int off, int len ) 
        { return -1; }
    
    @Override public void close() throws IOException
        {}
    }