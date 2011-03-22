package uk.ac.ox.zoo.sparqlite.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;

/**
 * Loads a model from the filesystem and reinterprets tdb:location and
 * sparqlite:larqLocation relative to the assembler file, not the current
 * working directory.
 * 
 * @author Alexander Dutton <alexander.dutton@zoo.ox.ac.uk>
 */
public class AssemblerModelLoader {
	protected static Log log = LogFactory.getLog(AssemblerModelLoader.class);
	
	public static Model get(String filename) {
		Model model = ModelFactory.createDefaultModel();
		File file = new File(filename);
		try {
			model.read(new FileInputStream(filename), getFileURI(file), "N3");
		} catch (FileNotFoundException e) {
			log.error("Could not find config file " + filename);
			throw new RuntimeException("Could not find config file" + filename);
		}
		
		setLocationsRelativeToFile(file, model);
		
		return model;

	}
	
	private static void setLocationsRelativeToFile(File file, Model model) {
		Property[] locations = {
				model.createProperty("http://jena.hpl.hp.com/2008/tdb#location"),
				model.createProperty("http://purl.org/net/sparqlite/vocab#larqLocation"),
				model.createProperty("http://purl.org/net/sparqlite/vocab#useDirectory"),
		};
		
		for (Property location : locations) {
			List<Statement> stmts = model.listStatements(new SimpleSelector((Resource) null, location, (Literal) null)).toList();
			for (Statement stmt : stmts) {
				String path = stmt.getObject().asLiteral().getString();
				path = (new File(file.getParent(), path)).getAbsolutePath();
				
				model.remove(stmt);
				model.add(stmt.getSubject(), stmt.getPredicate(), model.createLiteral(path));
			}
		}
		
	}

	private static String getFileURI(File file) {
		// return file.toURI().toString();
		return "info:x-sparqlite:assembly";
	}
}
