/**
 * 
 */
package uk.ac.ox.zoo.sparqlite.cmd.support;

import java.util.ArrayList;
import java.util.List;

import util.StringHelp;
import util.TF;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

public class Result
    {
    final String fromFile;
    final String title;
    final String label;
    final String args;
    
    final List<Answer> answers = new ArrayList<Answer>();
    String queryString = null;
    List<String> vars = null;
    
    public Result( String fromFile, String label, String title, String args )
        {
        this.fromFile = fromFile;
        this.title = title;
        this.label = label;
        this.args = args;
        }

    public void add( Answer a )
        { answers.add( a ); }
    
    public void setVars( List<String> vars )
        { this.vars = vars; }

    public void setQuery( String queryString )
        { this.queryString = queryString; }

    public void renderAsRDF( Resource root )
        {
        Resource x = root.getModel().createResource().addProperty( RDF.type, TF.TestInstance );
        root.addProperty( TF.testInstance, x );
        x.addProperty( TF.fromFile, fromFile );
        x.addProperty( TF.withTitle, title );
        x.addProperty( TF.withLabel, label );
        x.addProperty( TF.withArguments, args );
        x.addProperty( TF.withQuery, queryString );
        for (String var: vars) x.addProperty( TF.queryVar, var );
        x.addProperty( TF.queryVariables, StringHelp.join( vars, " " ) );
        for (Answer a: answers) a.renderAsRDF( x );
        }
    }