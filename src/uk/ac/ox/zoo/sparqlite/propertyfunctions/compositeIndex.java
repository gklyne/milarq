package uk.ac.ox.zoo.sparqlite.propertyfunctions;

import java.io.*;
import java.util.*;

import org.openjena.atlas.lib.NotImplemented;

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
import com.hp.hpl.jena.sparql.util.IndentedWriter;
import com.hp.hpl.jena.sparql.util.Symbol;

public class compositeIndex extends PropertyFunctionEval
	{

	public compositeIndex() 
		{
		super(PropFuncArgType.PF_ARG_SINGLE, PropFuncArgType.PF_ARG_LIST);
		}
	
	private String indexTerm;
	private String indexDir;
	private List<Element> index;
	
	static class Element
		{
		String subjectURI;
		int notBefore;
		int notAfter;
		}
	
	@Override public void build(PropFuncArg argSubject, Node predicate, PropFuncArg argObject, ExecutionContext execCxt)
	     {
	     super.build( argSubject, predicate, argObject, execCxt );
	     if (argSubject.isList()) 
	    	 throw new QueryBuildException( "compositeIndex: subject must be non-list" );
	     if (!argObject.isList() || argObject.getArgList().size() != 3)
	    	 throw new QueryBuildException( "compositeIndex: object must be three-element list" );
	     List<Node> objects = argObject.getArgList();
	     Node subject = argSubject.getArg(), term = objects.get(0);
	     Node notBefore = objects.get(1), notAfter = objects.get(2);
	     if (!term.isLiteral())
	    	 throw new QueryBuildException( "compositeIndex: term must be literal" );
	     if (!subject.isVariable())
	    	 throw new QueryBuildException( "compositeIndex: [currently] subject must be variable" );
	     if (!notBefore.isVariable())
	    	 throw new QueryBuildException( "compositeIndex: [currently] notBefore field must be variable" );
         if (!notAfter.isVariable())
             throw new QueryBuildException( "compositeIndex: [currently] notAfter field must be variable" );
	     enableIndex( execCxt, term.getLiteralLexicalForm() );
	     }

	private final Symbol CompositeIndexDirectory = Symbol.create( "com.epimorphics.milarq.indexes" );
	
	private void enableIndex( ExecutionContext c, String term ) 
		{
		indexDir = c.getContext().getAsString( CompositeIndexDirectory );
		if (indexDir == null) indexDir = "/home/chris/MILARQ/extras/indexes";
		indexTerm = term;
		}

	@Override public QueryIterator execEvaluated
		( final Binding binding
		, PropFuncArg argSubject
		, Node predicate
		, PropFuncArg argObject
		, ExecutionContext execCxt
		) 
		{
		loadIndexIfNecessary();
	    List<Node> objects = argObject.getArgList();
	    final Node subject = argSubject.getArg();
	    final Node notBefore = objects.get(1), notAfter = objects.get(2);
	    if (!subject.isVariable()) throw new RuntimeException( "only doing variable subjects." );
	    if (!notBefore.isVariable()) throw new RuntimeException( "only doing variable dates." );
        if (!notAfter.isVariable()) throw new RuntimeException( "only doing variable dates." );
	    final Var VS = Var.alloc(subject), VB = Var.alloc(notBefore), VA = Var.alloc(notAfter);
		return new QueryIterator() 
			{
			int i = 0;
			
			@Override public void abort() 
				{}

			@Override public Binding nextBinding() 
				{ return next(); }

			@Override public void close() 
				{}

			@Override public boolean hasNext() 
				{ return i < index.size(); }

			@Override public Binding next() 
				{
				Element x = index.get(i++);
				Node S = Node.createURI( x.subjectURI );
				Node NB = Node.createLiteral( LiteralLabelFactory.create( x.notBefore ) );
                Node NA = Node.createLiteral( LiteralLabelFactory.create( x.notAfter ) );
				Binding1 result = new Binding1( new Binding1( new Binding1( binding, VB, NB ), VS, S ), VA, NA );
                return result;
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

	private void loadIndexIfNecessary() 
		{
		if (index == null)
			{
			long timeBase = System.currentTimeMillis();
			index = new ArrayList<Element>();
			String indexFullName = indexDir + "/" + indexTerm + "-nb";
			BufferedReader in = inFromFile( indexFullName );
			while (true)
				{
				String line = readLine( in );
				if (line == null) break;
				String [] parts = line.split(",");
				Element e = new Element();
				e.notBefore = Integer.parseInt( parts[1] );
                e.notBefore = Integer.parseInt( parts[2] );
				e.subjectURI = parts[0];
				index.add( e );
				}
			long time = System.currentTimeMillis() - timeBase;
			System.err.println( ">> read " + indexTerm + " index: " + index.size() + " elements (" + (time/1000.0) + "s)." );
			}
		}

	private String readLine(BufferedReader in) 
		{ 
		try { return in.readLine(); } 
		catch (IOException e) { throw new RuntimeException( e ); }
		}

	private BufferedReader inFromFile(String indexFullName) 
		{
		try { return new BufferedReader( new FileReader( new File( indexFullName ) ) );	} 
		catch (FileNotFoundException e) { throw new RuntimeException( e ); }
		}
	}
