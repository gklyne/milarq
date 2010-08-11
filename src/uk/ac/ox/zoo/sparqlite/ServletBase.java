package uk.ac.ox.zoo.sparqlite;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;


/**
 * A partial implementation of the SPARQL protocol, for use with Jena SDB.
 * @author <a href="http://purl.org/net/aliman">Alistair Miles</a>
 * @version $Revision: 565 $ on $Date: 2008-07-18 12:27:57 +0100 (Fri, 18 Jul 2008) $
 */
public class ServletBase extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	static final long serialVersionUID = 1L;
   
//	private static Log log = LogFactory.getLog(ServletBase.class);
   
   
	protected void doCommon(HttpServletRequest request, HttpServletResponse response, Endpoint endpoint) throws ServletException, IOException {
		Processor re = new Processor(endpoint, getServletConfig());
		re.exec(request, response);

	}  	  	  	  
	

}