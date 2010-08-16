package uk.ac.ox.zoo.sparqlite.propertyfunctions;

import com.hp.hpl.jena.query.larq.IndexLARQ;
import com.hp.hpl.jena.query.larq.LARQ;
import com.hp.hpl.jena.sparql.engine.ExecutionContext;

public class simpleTextMatch extends TweakedLuceneSearch
	{
    private IndexLARQ index = null ;

    @Override protected IndexLARQ getIndex(ExecutionContext execCxt)
    	{ 
        if ( index == null ) index = LARQ.getDefaultIndex(execCxt.getContext()) ;
        return index ; 
    	}
	}
