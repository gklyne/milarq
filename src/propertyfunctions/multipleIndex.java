package propertyfunctions;

import static propertyfunctions.compositeIndex.bb;

import java.io.*;
import java.util.*;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;

import org.openjena.atlas.lib.NotImplemented;
import org.openjena.atlas.io.IndentedWriter;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.impl.LiteralLabelFactory;
import com.hp.hpl.jena.query.QueryBuildException;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.ExecutionContext;
import com.hp.hpl.jena.sparql.engine.QueryIterator;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.engine.binding.Binding1;
import com.hp.hpl.jena.sparql.pfunction.PropFuncArg;
import com.hp.hpl.jena.sparql.pfunction.PropFuncArgType;
import com.hp.hpl.jena.sparql.pfunction.PropertyFunctionEval;
import com.hp.hpl.jena.sparql.serializer.SerializationContext;
import com.hp.hpl.jena.sparql.util.Symbol;

public class multipleIndex extends PropertyFunctionEval
    {

    public multipleIndex() 
        {
        super(PropFuncArgType.PF_ARG_SINGLE, PropFuncArgType.PF_ARG_LIST);
        }
    
    private String indexTerm;
    private String indexDir;
    private IndexSearcher searcher;
    
    private Analyzer analyzer = new StandardAnalyzer();
    
    @Override public void build(PropFuncArg argSubject, Node predicate, PropFuncArg argObject, ExecutionContext execCxt)
         {
         super.build( argSubject, predicate, argObject, execCxt );
         if (argSubject.isList()) 
             throw new QueryBuildException( "multipleIndex: subject must be non-list" );
         if (!argObject.isList() || argObject.getArgList().size() != 3)
             throw new QueryBuildException( "multipleIndex: object must be three-element list" );
         List<Node> objects = argObject.getArgList();
         Node subject = argSubject.getArg();
         Node term = objects.get(0), earliest = objects.get(1), latest = objects.get(2);
         if (!term.isLiteral())
             throw new QueryBuildException( "multipleIndex: term must be literal" );
         if (!subject.isVariable())
             throw new QueryBuildException( "multipleIndex: [currently] subject must be variable" );
         if (!earliest.isVariable())
             throw new QueryBuildException( "multipleIndex: [currently] earliest date must be variable" );
         if (!latest.isVariable())
             throw new QueryBuildException( "multipleIndex: [currently] latest date must be variable" );
         enableIndex( execCxt, term.getLiteralLexicalForm() );
         }

    private final Symbol MultipleIndexLuceneDirectory = Symbol.create( "com.epi.milarq.larq-dir" );
    
    private void enableIndex( ExecutionContext c, String term ) 
        {
        indexDir = c.getContext().getAsString( MultipleIndexLuceneDirectory );
        if (indexDir == null) indexDir = "/home/chris/MILARQ/extras/larq";
        indexTerm = term;
        }

    private void openSearcherIfNecessary()
        {
        if (searcher == null) 
            try { searcher = new IndexSearcher( indexDir ); }
        catch (Exception e)
            { throw new RuntimeException( e ); }
        }
    
    @Override public QueryIterator execEvaluated
        ( final Binding binding
        , PropFuncArg argSubject
        , Node predicate
        , PropFuncArg argObject
        , ExecutionContext execCxt
        ) 
        {
        openSearcherIfNecessary();
        List<Node> objects = argObject.getArgList();
        final Node subject = argSubject.getArg();
        Node earliest = objects.get(1), latest = objects.get(2);
        if (!subject.isVariable()) throw new RuntimeException( "only doing variable subjects." );
        if (!earliest.isVariable()) throw new RuntimeException( "only doing variable earliest dates." );
        if (!latest.isVariable()) throw new RuntimeException( "only doing variable latest dates." );
        final Var VS = Var.alloc(subject);
        final Var VE = Var.alloc(earliest), VL = Var.alloc(latest);
        return new QueryIterator() 
            {
            int i = 0;
            Sort s = new Sort( "earliest" );
            QueryParser qp = new QueryParser( "term", analyzer );
            Query q = parseQuery();
            Hits hits = search();

            private Hits search()
                {
                try { return searcher.search( q, s ); }
                catch (IOException e) { throw new RuntimeException( e ); }
                }

            private Query parseQuery()
                {
                try { return qp.parse( indexTerm ); }
                catch (ParseException e) { throw new RuntimeException( e ); }
                }
            
            @Override public void abort() 
                {}

            @Override public Binding nextBinding() 
                { return next(); }

            @Override public void close() 
                {}

            @Override public boolean hasNext() 
                { return i < hits.length(); }

            @Override public Binding next() 
                {
                Document x = hitsDocument(i++);
                Node S = Node.createURI( x.get( "subject" ) );
                int en = Integer.parseInt( x.get( "earliest" ) );
                int ln = Integer.parseInt( x.get( "latest" ) );
                Node E = Node.createLiteral( LiteralLabelFactory.create( en ) );
                Node L = Node.createLiteral( LiteralLabelFactory.create( ln ) );
                Binding result = bb( bb( bb( binding, VE, E ), VS, S ), VL, L );
                return result;
                }

            private Document hitsDocument( int i )
                { 
                try { return hits.doc(i); }
                catch (Exception e) { throw new RuntimeException( e ); }
                }

            @Override public void remove() 
                {
                throw new NotImplemented( "oops" );
                }

            @Override public void output(IndentedWriter out, SerializationContext sCxt) 
                {
                out.print( "multipleIndex for " + indexTerm );
                }

            @Override public String toString(PrefixMapping pmap) 
                {
                return "multipleIndex for " + indexTerm;
                }

            @Override public void output(IndentedWriter out) 
                {
                out.print( "multipleIndex for " + indexTerm );
                }
            };
        }
    }
