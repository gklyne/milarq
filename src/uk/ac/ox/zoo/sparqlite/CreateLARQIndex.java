/**
 * $Id: $
 */

package uk.ac.ox.zoo.sparqlite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.larq.IndexBuilderString;
import com.hp.hpl.jena.query.larq.IndexBuilderSubject;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.tdb.TDBFactory;

import java.util.Iterator;

// Create LARQ index for TDB dataset
//
// Sample command arguments for testing, run from project directory
// 
// CreateLARQIndex test/tdb/books test/lucene/books
public class CreateLARQIndex
    {
    static Log log = LogFactory.getLog(CreateLARQIndex.class);

    public static void main (String[] args)
        {
        //System.setProperty("log4j.rootCategory", "WARN, stdout");
        //System.setProperty("log4j.category.uk.ac.ox.zoo.sparqlite.CreateLARQIndex", "WARN");       
        //log = LogFactory.getLog(CreateLARQIndex.class);
        
        log.info("check the command line");
        String error = null;
        if (args.length != 3)
            {
            error = "Exactly three arguments expected";
            };
        if (error != null)
    	    {
            String[] usage =
                {
                "",
                "Usage: java CreateLARQIndex tdbdir stringindexdir subjectindexdir",
                "",
                "where:",
                "    tdbdir - indicates a directory where a Jena TDB dataset is located",
                "    stringindexdir - a directory where a Lucene string index will be created",
                "    subjectindexdir - a directory where a Lucene subject index will be created"
                };
    	    // Use this form later
    	    String[] usage1 =
    	        {
                "",
    	        "Usage: java CreateLARQIndex model",
                "",
    	        "where:",
    	        "    model - is a file containing the Jena assembler for the model to be indexed",
    	        "            also indicating the directory for the LARQ Lucene index."    	        
    	        };
    	    System.out.println("Command line error: "+error);
            for (String s: usage)
                {
                System.out.println(s);
                }
    	    System.exit(1);
    	    };
    	String tdbdir = args[0];
    	String stringindexdir = args[1];
    	String subjectindexdir = args[2];
    	    
    	log.info("create the indexes");
    	createIndex(tdbdir, stringindexdir, subjectindexdir);
        System.out.println("CreateLARQIndex - indexes for TDB data "+tdbdir+" created in "+stringindexdir+" and "+subjectindexdir);

        log.info("completed");
        System.exit(0);        
        }

	public static void createIndex(String tdbdir, String stringindexdir, String subjectindexdir)
		{
		log.info("open TDB dataset");
		Dataset dataset = TDBFactory.createDataset(tdbdir);
		
		log.info("instantiate Lucene string index builder");
		IndexBuilderString larqStringBuilder = new IndexBuilderString(stringindexdir);
		
		log.info("instantiate Lucene subject index builder");
		IndexBuilderSubject larqSubjectBuilder = new IndexBuilderSubject(subjectindexdir);
		
		log.info("index strings and literals in default model");
		Model defaultModel = dataset.getDefaultModel();
		larqStringBuilder.indexStatements(defaultModel.listStatements());
		larqSubjectBuilder.indexStatements(defaultModel.listStatements());
		
		for (Iterator<String> names = dataset.listNames(); names.hasNext(); ) 
			{
			String name = names.next().toString();
		    log.info("index strings and literals in named model: "+name);
		    Model model = dataset.getNamedModel(name);
		    larqStringBuilder.indexStatements(model.listStatements());
		    larqSubjectBuilder.indexStatements(model.listStatements());
			}
		
		log.info("closing index writer and dataset");
		larqStringBuilder.closeWriter();
		larqSubjectBuilder.closeWriter();
		dataset.close();
		
		log.info("all done");
		}
	
	}	// End of class CreateLARQIndex

// End.
