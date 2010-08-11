package uk.ac.ox.zoo.sparqlite;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uk.ac.ox.zoo.sparqlite.config.Config;
import uk.ac.ox.zoo.sparqlite.config.Init;


/**
 * A partial implementation of the SPARQL protocol, for use with Jena TDB.
 * @author <a href="http://purl.org/net/aliman">Alistair Miles</a>
 * @version $Revision: 565 $ on $Date: 2008-07-18 12:27:57 +0100 (Fri, 18 Jul 2008) $
 */
 public class ServletTDB extends ServletBase implements javax.servlet.Servlet {
   static final long serialVersionUID = 1L;
   
   protected static Log log = LogFactory.getLog(ServletTDB.class);
   
    /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public ServletTDB() {
		super();
	}   	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doCommon(request, response);
	}  	  	  	  
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doCommon(request, response);
	} 
	
	protected void doCommon(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    Config c = Init.getSparqlite().getConfig( request.getPathInfo(), getServletContext() ); 
		Endpoint endpoint = new EndpointTDB( c );
		doCommon(request, response, endpoint); 
	}  	  	  	  

	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init()
	 */
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();
		
        log.info("TDB Servlet initialisation: begin");
//        ServletContext context = this.getServletContext();
//        Enumeration names = context.getInitParameterNames();
//        if (!names.hasMoreElements()) 
//        	log.info("found no init params");
//        while (names.hasMoreElements()) {
//        	String name = (String) names.nextElement();
//        	log.info("found init param name: "+name);
//        	String value = context.getInitParameter(name);
//        	log.info("found init param value: "+value);
//        }

//      Enumeration confignames = config.getInitParameterNames();
//      if (!confignames.hasMoreElements()) 
//      	log.info("found no config init params");
//      while (confignames.hasMoreElements()) {
//      	String name = (String) confignames.nextElement();
//      	log.info("found config init param name: "+name);
//      	String value = config.getInitParameter(name);
//      	log.info("found config init param value: "+value);
//      }

        ServletConfig config = this.getServletConfig();
        InitParams params = new InitParams(config);
        Map<String,String> paramMap = params.asMap();
        for (String key : paramMap.keySet()) {
        	String value = paramMap.get(key);
        	log.info("found config init param: "+key+" = "+value);
        }
        
        log.info("TDB Servlet initialisation: end");
		
	}   
}