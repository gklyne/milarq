/**
 * 
 */
package uk.ac.ox.zoo.sparqlite.cmd.support;

import java.util.List;

import com.hp.hpl.jena.rdf.model.RDFNode;

public class Repeat
    {
    final List<RDFNode> results;
    public final String name;
    
    public Repeat( String name, List<RDFNode> results )
        { this.name = name; this.results = results; }
    
    public List<RDFNode> resultsAsNodes()
        { return results; }
    
    }