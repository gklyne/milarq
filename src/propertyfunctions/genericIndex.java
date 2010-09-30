package propertyfunctions;

import static propertyfunctions.compositeIndex.bb;

import java.io.*;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.openjena.atlas.lib.NotImplemented;
import org.openjena.atlas.io.IndentedWriter;

import uk.ac.ox.zoo.sparqlite.cmd.support.EmptyReader;

import com.hp.hpl.jena.datatypes.*;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.impl.LiteralLabel;
import com.hp.hpl.jena.graph.impl.LiteralLabelFactory;
import com.hp.hpl.jena.query.QueryBuildException;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.shared.WrappedIOException;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.ExecutionContext;
import com.hp.hpl.jena.sparql.engine.QueryIterator;
import com.hp.hpl.jena.sparql.engine.binding.*;
import com.hp.hpl.jena.sparql.pfunction.PropFuncArg;
import com.hp.hpl.jena.sparql.pfunction.PropFuncArgType;
import com.hp.hpl.jena.sparql.pfunction.PropertyFunctionEval;
import com.hp.hpl.jena.sparql.serializer.SerializationContext;
import com.hp.hpl.jena.sparql.util.Symbol;

public class genericIndex extends PropertyFunctionEval
    {
    public genericIndex() 
        { super(PropFuncArgType.PF_ARG_SINGLE, PropFuncArgType.PF_ARG_EITHER ); }

    Log log = LogFactory.getLog(genericIndex.class);
    
    private String indexTerm;
    private String indexFullName;
    private String indexDir;
    
    @Override public void build(PropFuncArg argSubject, Node predicate, PropFuncArg argObject, ExecutionContext execCxt)
         {
         super.build( argSubject, predicate, argObject, execCxt );
         List<Node> objects = asList( argObject );
         enableIndex( execCxt, predicate.getURI(), objects.get(0).getLiteralLexicalForm() );
         }

    private void enableIndex( ExecutionContext c, String URI, String term ) 
        {
        log.trace("enableIndex: URI "+URI+", term "+term);
        String dir = c.getContext().getAsString( Symbol.create( URI ) );
        if (dir == null)
            throw new QueryBuildException( "no value supplied for composite index directory for predicate " + URI );
        log.trace("enableIndex: dir "+dir);
        indexDir = dir;
        indexTerm = term;
        indexFullName = indexDir + "/" + indexTerm.toLowerCase();
        log.trace("enableIndex: indexFullName "+indexFullName);
        }

    private List<Node> asList( PropFuncArg arg )
        { return arg.isList() ? arg.getArgList() : list( arg.getArg() ); }
    
    private <T> List<T> list( T ... args )
        { return Arrays.asList( args ); }
    
    @Override public QueryIterator execEvaluated
        ( final Binding binding
        , PropFuncArg argSubject
        , Node predicate
        , PropFuncArg argObject
        , ExecutionContext execCxt
        ) 
        {
        List<Node> objects = asList( argObject );
        final Node subject = argSubject.getArg();
        final List<Var> vars = new ArrayList<Var>();
        for (Node v: objects.subList( 1, objects.size() ) ) vars.add( Var.alloc( v ) );
        return subject.isVariable()
            ? new GenericIndexIteratorSubjectVariable( binding, vars, subject )
            : new GenericIndexIteratorSubjectBound( binding, vars, subject )
            ;
        }

    private String readLine(BufferedReader in) 
        { 
        try { return in.readLine(); } 
        catch (IOException e) { throw new WrappedIOException( e ); }
        }

    /**
         Open a buffered reader over the file named by indexFullName.
         If the file does not exist, an empty reader is returned and
         a message logged, rather than throwing an exception (ie the
         query will fail cleanly).
    */
    private BufferedReader inFromFile(String indexFullName) 
        {
        try 
            { return new BufferedReader( new FileReader( new File( indexFullName ) ) ); } 
        catch (FileNotFoundException e) 
            {
            log.warn( "could not find index file " +  indexFullName + ": no bindings created" );
            return new BufferedReader( new EmptyReader() );
            }
        }
    
    private class GenericIndexIterator
        {
        protected final Binding binding;
        protected final List<Var> vars;
        protected final Node subject;
        
        protected final BufferedReader in = inFromFile( indexFullName );
        
        protected String current = readLine( in );

        private GenericIndexIterator( Binding binding, List<Var> vars, Node subject )
            {
            this.binding = binding;
            this.vars = vars;
            this.subject = subject;
            }

        public void abort() 
            { close(); }

        public void close() 
            { 
            try { in.close(); }
            catch (IOException e) { throw new WrappedIOException( e ); }
            }
        
        protected Node stringToNode( String s )
            {
            String spelling = s.replaceAll( "\\\\C", "," );
            if (spelling.startsWith( "<" ))
                return Node.createURI( spelling.substring( 1, spelling.length() - 1 ) );
            else if (spelling.matches( "^-?[0-9].*" ))
                return Node.createLiteral( LiteralLabelFactory.create( Integer.parseInt( spelling ) ) );
            else
                {
                int quote = spelling.lastIndexOf( '"' );
                String lf = spelling.substring( 1, quote );
                if (quote == spelling.length() - 1) return Node.createLiteral( lf );
                else if (spelling.charAt( quote + 1 ) == '@') return Node.createLiteral( lf, spelling.substring( quote + 2 ), false );
                else
                    {
                    RDFDatatype dt = new BaseDatatype( spelling.substring( quote + 2 ) );
                    LiteralLabel ll = LiteralLabelFactory.createLiteralLabel( lf, "", dt );
                    return Node.createLiteral( ll );
                    }
                }
            }

        public void remove() 
            {
            throw new NotImplemented( "oops" );
            }

        public void output(IndentedWriter out, SerializationContext sCxt) 
            {
            // TODO Auto-generated method stub
            }

        public String toString(PrefixMapping pmap) 
            {
            // TODO Auto-generated method stub
            return null;
            }

        public void output(IndentedWriter out) 
            {
            // TODO Auto-generated method stub
            }
        }
    
    private final class GenericIndexIteratorSubjectBound extends GenericIndexIterator implements QueryIterator
        {
        private GenericIndexIteratorSubjectBound( Binding binding, List<Var> vars, Node subject )
            { super( binding, vars, subject ); }

        @Override public Binding nextBinding() 
            { return next(); }

        public boolean hasNext() 
            {
            while (true)
                {
                if (current == null) { close(); return false; }
                if (stringToNode(current.split(",")[0]).equals(subject)) return true;
                readAndCloseIfDone();
                }
            }
        
        @Override public Binding next() 
            {            
            if (!hasNext()) throw new NoSuchElementException();
            int i = 1;
            String [] elements = current.split( "," );
            Binding b = binding;
            for (Var v: vars) b = compositeIndex.bb( b, v, stringToNode( elements[i++] ) );
            readAndCloseIfDone();
            return b;
            }

        private void readAndCloseIfDone()
            {
            current = readLine( in );
            if (current == null) close();
            }
        }
    
    private final class GenericIndexIteratorSubjectVariable extends GenericIndexIterator implements QueryIterator
        {
        private GenericIndexIteratorSubjectVariable( Binding binding, List<Var> vars, Node subject )
            { super( binding, vars, subject ); }

        @Override public Binding nextBinding() 
            { return next(); }
        
        public boolean hasNext() 
            { return current != null; }
        
        @Override public Binding next() 
            {            
            int i = 0;
            String [] elements = current.split( "," );
            Node S = stringToNode( elements[i++] );
            final Var VS = Var.alloc(subject); 
            Binding b = bb( binding, VS, S );
            for (Var v: vars) b = bb( b, v, stringToNode( elements[i++] ) );
            current = readLine( in );
            if (current == null) close();
            ////TODO: remove? System.err.println( ">> gi: returning " + b );
            return b;
            }
        }   
    }
