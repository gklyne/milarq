package uk.ac.ox.zoo.sparqlite;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.ox.zoo.sparqlite.exceptions.UnexpectedException;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;

public class ResponseWriter {

	Object result;
	HttpServletRequest request;
	HttpServletResponse response;

	public ResponseWriter(HttpServletRequest request, HttpServletResponse response) throws UnexpectedException {
		// check guard condition
		if (response.isCommitted()) 
			throw new UnexpectedException("response is committed, cannot write result");
		this.request = request;
		this.response = response;
	}
	
	void doWrite(Object result) throws UnexpectedException {
		OutputStream out;
		try {
			out = response.getOutputStream();
		} catch (IOException e) {
			throw new UnexpectedException("caught IO exception getting output stream");
		}
		if (out == null)
			throw new UnexpectedException("output stream is null");
		
		if (result instanceof Boolean) {
			doWriteBoolean(((Boolean)result).booleanValue(), out);
		} else if (result instanceof ResultSet) {
			doWriteResultSet((ResultSet)result, out);
		} else if (result instanceof Model) {
			doWriteModel((Model)result, out);
		} else throw new UnexpectedException("unexpected result type: "+result.getClass());
	}

	public void doWriteBoolean(boolean result, OutputStream out) throws UnexpectedException {
		try {
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/sparql-results+json");
			response.setCharacterEncoding("UTF-8");
			response.flushBuffer();	// commit the response
			ResultSetFormatter.outputAsJSON(out, result); 
		} catch (Exception e) {
			throw new UnexpectedException("caught exception writing boolean result: "+e.getLocalizedMessage(), e);
		}
	}

	public void doWriteResultSet(ResultSet resultSet, OutputStream out) throws UnexpectedException {
		try {
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/sparql-results+json");
			response.setCharacterEncoding("UTF-8");
			response.flushBuffer();	// commit the response
			ResultSetFormatter.outputAsJSON(out, resultSet); 
		} catch (Exception e) {
			throw new UnexpectedException("caught exception writing boolean result: "+e.getLocalizedMessage(), e);
		}
	}

	public void doWriteModel(Model model, OutputStream out) throws UnexpectedException {
		try {
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/rdf+xml");
			response.setCharacterEncoding("UTF-8");
			response.flushBuffer();	// commit the response
			model.write(out, "RDF/XML");
		} catch (Exception e) {
			throw new UnexpectedException("caught exception writing boolean result: "+e.getLocalizedMessage(), e);
		}
	}
	
}
