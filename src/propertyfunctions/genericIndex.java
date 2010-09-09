package propertyfunctions;

import java.io.*;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.openjena.atlas.lib.NotImplemented;

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
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.engine.binding.Binding1;
import com.hp.hpl.jena.sparql.pfunction.PropFuncArg;
import com.hp.hpl.jena.sparql.pfunction.PropFuncArgType;
import com.hp.hpl.jena.sparql.pfunction.PropertyFunctionEval;
import com.hp.hpl.jena.sparql.serializer.SerializationContext;
import com.hp.hpl.jena.sparql.util.IndentedWriter;
import com.hp.hpl.jena.sparql.util.Symbol;

public class genericIndex extends PropertyFunctionEval
    {
    public genericIndex() 
        { super(PropFuncArgType.PF_ARG_SINGLE, PropFuncArgType.PF_ARG_LIST); }

    Log log = LogFactory.getLog(genericIndex.class);
    
    private String indexTerm;
    private String indexFullName;
    private String indexDir;
    
    @Override public void build(PropFuncArg argSubject, Node predicate, PropFuncArg argObject, ExecutionContext execCxt)
         {
         log.trace("build");
         super.build( argSubject, predicate, argObject, execCxt );
         List<Node> objects = argObject.getArgList();
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
        indexFullName = indexDir + "/" + indexTerm;
        log.trace("enableIndex: indexFullName "+indexFullName);
        }

    @Override public QueryIterator execEvaluated
        ( final Binding binding
        , PropFuncArg argSubject
        , Node predicate
        , PropFuncArg argObject
        , ExecutionContext execCxt
        ) 
        {
        List<Node> objects = argObject.getArgList();
        final Var VS = Var.alloc(argSubject.getArg()); 
        final List<Var> vars = new ArrayList<Var>();
        for (Node v: objects.subList( 1, objects.size() ) ) vars.add( Var.alloc( v ) );
        return new QueryIterator() 
            {
            BufferedReader in = inFromFile( indexFullName );
            String current = readLine( in );
                        
            @Override public void abort() 
                { close(); }

            @Override public Binding nextBinding() 
                { return next(); }

            @Override public void close() 
                { 
                try { in.close(); }
                catch (IOException e) { throw new WrappedIOException( e ); }
                }

            @Override public boolean hasNext() 
                { return current != null; }

            @Override public Binding next() 
                {            
                int i = 0;
                String [] elements = current.split( "," );
                Node S = stringToNode( elements[i++] );
                Binding b = new Binding1( binding, VS, S );
                for (Var v: vars) b = new Binding1( b, v, stringToNode( elements[i++] ) );
                current = readLine( in );
                if (current == null) close();
                System.err.println( ">> gi: returning " + b );
                return b;
                }

            private Node stringToNode( String s )
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

            @Override public void remove() 
                {
                throw new NotImplemented( "oops" );
                }

            @Override public void output(IndentedWriter out, SerializationContext sCxt) 
                {
                // TODO Auto-generated method stub
                }

            @Override public String toString(PrefixMapping pmap) 
                {
                // TODO Auto-generated method stub
                return null;
                }

            @Override public void output(IndentedWriter out) 
                {
                // TODO Auto-generated method stub
                }
            };
        }

    private String readLine(BufferedReader in) 
        { 
        try { return in.readLine(); } 
        catch (IOException e) { throw new WrappedIOException( e ); }
        }

    private BufferedReader inFromFile(String indexFullName) 
        {
        try { return new BufferedReader( new FileReader( new File( indexFullName ) ) ); } 
        catch (FileNotFoundException e) { throw new RuntimeException( e ); }
        }
    

    }
