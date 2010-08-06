/**
 * 
 */
package uk.ac.ox.zoo.sparqlite;

/**
 * Represents anticipated exception arising during processing of request.
 * @author <a href="http://purl.org/net/aliman">Alistair Miles</a>
 * @version $Revision: 565 $ on $Date: 2008-07-18 12:27:57 +0100 (Fri, 18 Jul 2008) $
 */
public class AbortRequestException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int responseCode = -1;
	
	public AbortRequestException(int responseCode, String message) {
		super(message);
		this.responseCode = responseCode;
	}
	
	public int getResponseCode() {
		return responseCode;
	}

}
