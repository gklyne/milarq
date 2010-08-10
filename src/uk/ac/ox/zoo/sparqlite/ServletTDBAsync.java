package uk.ac.ox.zoo.sparqlite;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uk.ac.ox.zoo.sparqlite.config.Config;


/**
 * HEALTH WARNING: this processor is inherently unsafe and may deliver unpredictable results
 * FIXME
 * @author Alistair Miles
 */
 public class ServletTDBAsync extends ServletBase implements javax.servlet.Servlet {
   static final long serialVersionUID = 1L;

   private static final String EXECUTOR = "executor";

   
   protected static Log log = LogFactory.getLog(ServletTDBAsync.class);
   
    /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public ServletTDBAsync() {
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
		Endpoint endpoint = new EndpointTDB( request, getServletContext() );
		doCommon(request, response, endpoint); // superclass
	}  	  	  	 
	
	@Override
	protected void doCommon(HttpServletRequest request, HttpServletResponse response, Endpoint endpoint) throws ServletException, IOException {
		
		Executor executor = (Executor) this.getServletContext().getAttribute(EXECUTOR); 		
		ProcessorAsync re = new ProcessorAsync(endpoint, getServletConfig(), executor);
		re.exec(request, response);

	}  	  	  	  


	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init()
	 */
	public void init() throws ServletException {
		super.init();
		
        log.info("TDB Async Servlet initialisation: begin");
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

		if ( this.getServletContext().getAttribute(EXECUTOR) == null ) {
	        log.info("create executor");
			Executor executor = Executors.newCachedThreadPool();
			this.getServletContext().setAttribute(EXECUTOR, executor);
		}
		
        log.info("TDB Async Servlet initialisation: end");
		
	}   
}