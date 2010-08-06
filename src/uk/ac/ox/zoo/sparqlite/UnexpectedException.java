/**
 * 
 */
package uk.ac.ox.zoo.sparqlite;

/**
 * Represents unexpected exception arising during processing of request.
 * @author <a href="http://purl.org/net/aliman">Alistair Miles</a>
 * @version $Revision: 565 $ on $Date: 2008-07-18 12:27:57 +0100 (Fri, 18 Jul 2008) $
 */
public class UnexpectedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5402512561423564714L;

	public UnexpectedException() {
		super();
	}
	
	public UnexpectedException(String message) {
		super(message);
	}

	public UnexpectedException(String message, Throwable nested) {
		super(message, nested);
	}

}
