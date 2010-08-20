/**
 * 
 */
package uk.ac.ox.zoo.sparqlite.cmd.support;

import java.util.List;
import java.util.Map;

import util.TF;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.RDF;

public class Answer
    {
    final int i;
    final long time;
    final List<Map<String, RDFNode>> rows;
    
    public Answer( int i, long time, List<Map<String, RDFNode>> rows )
        {
        this.i = i;
        this.time = time;
        this.rows = rows;
        }

    public void renderAsRDF( Resource test )
        {
        Resource iter = test.getModel().createResource().addProperty( RDF.type, TF.Iteration );
        test.addProperty( TF.iteration, iter );
        iter.addLiteral( TF.index, i );
        iter.addLiteral( TF.timeInMs, time );        
        for (Map<String, RDFNode> row: rows) renderRowAsRDF( iter, row ); 
        }

    private void renderRowAsRDF( Resource iter, Map<String, RDFNode> solution )
        {
        Resource row = iter.getModel().createResource();
        iter.addProperty( TF.resultRow, row );
        for (String name: solution.keySet())
            {      
            Resource binding = iter.getModel().createResource();
            row.addProperty( TF.binding, binding );
            binding.addProperty( TF.varName, name ).addProperty( TF.varValue, varValue( solution, name ) );
            }
        }

    private static final RDFNode noSolution = ResourceFactory.createPlainLiteral( "none" );
    
    private RDFNode varValue( Map<String, RDFNode> solution, String name )
        {
        RDFNode node = solution.get( name );
        return node == null ? noSolution : node;
        }    
    }